package com.ptob.demo.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
public class SecurityConfig {

    @Value("${app.security.bearer-token}")
    private String bearerToken;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/api-docs/**", "/actuator/health/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(new BearerTokenFilter(bearerToken), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    static class BearerTokenFilter extends OncePerRequestFilter {
        private final String expected;
        BearerTokenFilter(String expected) { this.expected = expected; }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {
            String path = request.getRequestURI();
            if (path.startsWith("/swagger-ui") || path.startsWith("/api-docs") || path.startsWith("/actuator/health")) {
                filterChain.doFilter(request, response);
                return;
            }
            String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (auth != null && auth.equals("Bearer " + expected)) {
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken("local-dev", "N/A", AuthorityUtils.createAuthorityList("ROLE_API")));
                filterChain.doFilter(request, response);
                return;
            }
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Missing or invalid bearer token\"}");
        }
    }
}
