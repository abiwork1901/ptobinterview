package com.ptob.demo.controller;

import com.ptob.demo.model.AllocationRequest;
import com.ptob.demo.model.ProportionalAllocationRequest;
import com.ptob.demo.service.OmnibusService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/omnibus")
public class OmnibusController {
    private final OmnibusService omnibusService;
    public OmnibusController(OmnibusService omnibusService) { this.omnibusService = omnibusService; }

    @PostMapping("/allocate")
    public Map<String, Object> allocate(@Valid @RequestBody AllocationRequest request) { return omnibusService.allocate(request); }

    @PostMapping("/allocate/proportional")
    public Map<String, Object> proportionalAllocate(@Valid @RequestBody ProportionalAllocationRequest request) {
        return omnibusService.proportionalAllocate(request);
    }
}
