package com.ptob.demo.service;

import com.ptob.demo.model.*;
import com.ptob.demo.persistence.TradeEntity;
import com.ptob.demo.persistence.TradeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TradingService {

    private final LedgerService ledgerService;
    private final RiskService riskService;
    private final CostBasisService costBasisService;
    private final EventPublisher eventPublisher;
    private final TradeRepository tradeRepository;
    private final IdempotencyService idempotencyService;

    private static class OrderBook {
        TreeMap<BigDecimal, Deque<Order>> bids = new TreeMap<>(Comparator.reverseOrder());
        TreeMap<BigDecimal, Deque<Order>> asks = new TreeMap<>();
    }

    private final Map<String, OrderBook> books = new ConcurrentHashMap<>();
    private final Map<String, Order> idempotencyOrders = new ConcurrentHashMap<>();

    public TradingService(LedgerService ledgerService,
                          RiskService riskService,
                          CostBasisService costBasisService,
                          EventPublisher eventPublisher,
                          TradeRepository tradeRepository,
                          IdempotencyService idempotencyService) {
        this.ledgerService = ledgerService;
        this.riskService = riskService;
        this.costBasisService = costBasisService;
        this.eventPublisher = eventPublisher;
        this.tradeRepository = tradeRepository;
        this.idempotencyService = idempotencyService;
    }

    @Transactional
    public synchronized Map<String, Object> placeOrder(OrderRequest request) {
        if (idempotencyOrders.containsKey(request.idempotencyKey())) {
            return response(idempotencyOrders.get(request.idempotencyKey()), List.of());
        }
        String existing = idempotencyService.lookup(request.idempotencyKey()).orElse(null);
        if (existing != null && idempotencyOrders.containsKey(request.idempotencyKey())) {
            return response(idempotencyOrders.get(request.idempotencyKey()), List.of());
        }

        String[] assets = parseSymbol(request.symbol());
        String baseAsset = assets[0];
        String quoteAsset = assets[1];
        BigDecimal notional = request.quantity().multiply(request.price());

        riskService.validateOrder(request.accountId(), notional);
        boolean reserved = request.side() == Side.BUY
                ? ledgerService.reserve(request.accountId(), quoteAsset, notional)
                : ledgerService.reserve(request.accountId(), baseAsset, request.quantity());

        Order order = new Order(request.accountId(), request.symbol(), request.side(), request.quantity(), request.price(), request.idempotencyKey());
        idempotencyOrders.put(request.idempotencyKey(), order);

        if (!reserved) {
            order.reject();
            eventPublisher.publish("trading.orders", order.getOrderId(), Map.of("status", "REJECTED", "request", request));
            idempotencyService.record(request.idempotencyKey(), order.getOrderId());
            return response(order, List.of());
        }

        OrderBook book = books.computeIfAbsent(request.symbol(), ignored -> new OrderBook());
        List<Trade> newTrades = match(book, order, baseAsset, quoteAsset);

        if (order.getRemainingQuantity().signum() > 0 && order.getStatus() != OrderStatus.REJECTED) {
            addToBook(book, order);
        }

        idempotencyService.record(request.idempotencyKey(), order.getOrderId());
        eventPublisher.publish("trading.orders", order.getOrderId(), Map.of(
                "orderId", order.getOrderId(),
                "accountId", order.getAccountId(),
                "symbol", order.getSymbol(),
                "side", order.getSide(),
                "status", order.getStatus(),
                "remainingQuantity", order.getRemainingQuantity()));
        return response(order, newTrades);
    }

    @Transactional
    public synchronized Map<String, Object> cancelOrder(String symbol, String orderId) {
        OrderBook book = books.get(symbol);
        if (book == null) throw new IllegalArgumentException("Unknown symbol");
        Order found = removeOrder(book.bids, orderId);
        if (found == null) found = removeOrder(book.asks, orderId);
        if (found == null) throw new IllegalArgumentException("Order not found");

        found.cancel();
        String[] assets = parseSymbol(found.getSymbol());
        if (found.getSide() == Side.BUY) ledgerService.release(found.getAccountId(), assets[1], found.getRemainingQuantity().multiply(found.getPrice()));
        else ledgerService.release(found.getAccountId(), assets[0], found.getRemainingQuantity());

        eventPublisher.publish("trading.orders", found.getOrderId(), Map.of("status", "CANCELLED", "symbol", found.getSymbol()));
        return Map.of("status", "CANCELLED", "orderId", found.getOrderId());
    }

    @Transactional(readOnly = true)
    public synchronized Map<String, Object> getBook(String symbol) {
        OrderBook book = books.computeIfAbsent(symbol, ignored -> new OrderBook());
        return Map.of("symbol", symbol, "bids", summarize(book.bids), "asks", summarize(book.asks));
    }

    @Transactional(readOnly = true)
    public List<Trade> getTrades() {
        return tradeRepository.findAll().stream()
                .map(t -> new Trade(t.getTradeId(), t.getSymbol(), t.getPrice(), t.getQuantity(), t.getBuyOrderId(), t.getSellOrderId(), t.getExecutedAt()))
                .toList();
    }

    private List<Map<String, Object>> summarize(TreeMap<BigDecimal, Deque<Order>> side) {
        List<Map<String, Object>> levels = new ArrayList<>();
        for (Map.Entry<BigDecimal, Deque<Order>> entry : side.entrySet()) {
            BigDecimal qty = entry.getValue().stream().map(Order::getRemainingQuantity).reduce(BigDecimal.ZERO, BigDecimal::add);
            if (qty.signum() > 0) levels.add(Map.of("price", entry.getKey(), "quantity", qty, "orders", entry.getValue().size()));
        }
        return levels;
    }

    private List<Trade> match(OrderBook book, Order taker, String baseAsset, String quoteAsset) {
        List<Trade> newTrades = new ArrayList<>();
        TreeMap<BigDecimal, Deque<Order>> opposite = taker.getSide() == Side.BUY ? book.asks : book.bids;

        while (taker.getRemainingQuantity().signum() > 0 && !opposite.isEmpty()) {
            BigDecimal bestPrice = opposite.firstKey();
            boolean crosses = taker.getSide() == Side.BUY ? taker.getPrice().compareTo(bestPrice) >= 0 : taker.getPrice().compareTo(bestPrice) <= 0;
            if (!crosses) break;

            Deque<Order> queue = opposite.get(bestPrice);
            Order maker = queue.peekFirst();
            BigDecimal qty = taker.getRemainingQuantity().min(maker.getRemainingQuantity());
            taker.fill(qty);
            maker.fill(qty);

            String buyer = taker.getSide() == Side.BUY ? taker.getAccountId() : maker.getAccountId();
            String seller = taker.getSide() == Side.SELL ? taker.getAccountId() : maker.getAccountId();
            BigDecimal quoteAmount = qty.multiply(bestPrice);
            String reference = "trade-" + System.nanoTime();

            ledgerService.settleTrade(buyer, seller, baseAsset, quoteAsset, qty, quoteAmount, reference);
            costBasisService.recordBuy(buyer, baseAsset, qty, quoteAmount);
            costBasisService.recordSell(seller, baseAsset, qty);

            Trade trade = taker.getSide() == Side.BUY
                    ? Trade.of(taker.getSymbol(), bestPrice, qty, taker.getOrderId(), maker.getOrderId())
                    : Trade.of(taker.getSymbol(), bestPrice, qty, maker.getOrderId(), taker.getOrderId());

            tradeRepository.save(new TradeEntity(trade.tradeId(), trade.symbol(), trade.price(), trade.quantity(), trade.buyOrderId(), trade.sellOrderId(), trade.executedAt()));
            newTrades.add(trade);
            eventPublisher.publish("trading.trades", trade.tradeId(), trade);

            if (maker.getRemainingQuantity().signum() == 0) queue.removeFirst();
            if (queue.isEmpty()) opposite.remove(bestPrice);
        }
        return newTrades;
    }

    private void addToBook(OrderBook book, Order order) {
        TreeMap<BigDecimal, Deque<Order>> side = order.getSide() == Side.BUY ? book.bids : book.asks;
        side.computeIfAbsent(order.getPrice(), ignored -> new ArrayDeque<>()).addLast(order);
    }

    private Order removeOrder(TreeMap<BigDecimal, Deque<Order>> side, String orderId) {
        for (Map.Entry<BigDecimal, Deque<Order>> entry : new ArrayList<>(side.entrySet())) {
            for (Order order : new ArrayList<>(entry.getValue())) {
                if (order.getOrderId().equals(orderId)) {
                    entry.getValue().remove(order);
                    if (entry.getValue().isEmpty()) side.remove(entry.getKey());
                    return order;
                }
            }
        }
        return null;
    }

    private String[] parseSymbol(String symbol) {
        String[] assets = symbol.split("/");
        if (assets.length != 2) throw new IllegalArgumentException("Symbol must look like BTC/USDT");
        return assets;
    }

    private Map<String, Object> response(Order order, List<Trade> trades) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("orderId", order.getOrderId());
        result.put("status", order.getStatus());
        result.put("remainingQuantity", order.getRemainingQuantity());
        result.put("trades", trades);
        return result;
    }
}
