package com.cafeshop.demo.service.webhook;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class OmiseWebhookFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().equals("/api/payments/webhook/omise");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        System.out.println("--->Arrive OmiseWebhookFilter-->");
        String uri = request.getRequestURI();
        if (!uri.startsWith("/api/payments/webhook/omise")) {
            chain.doFilter(request, response);
            return;
        }
        CachedBodyHttpServletRequest wrapped = new CachedBodyHttpServletRequest(request);
        chain.doFilter(wrapped, response);
    }
}
