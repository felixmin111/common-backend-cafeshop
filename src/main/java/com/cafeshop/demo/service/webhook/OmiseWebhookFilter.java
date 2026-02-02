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

    private final OmiseSignatureVerifier verifier;

    public OmiseWebhookFilter(OmiseSignatureVerifier verifier) {
        this.verifier = verifier;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        if (!uri.startsWith("/api/payments/webhook/omise")) {
            chain.doFilter(request, response);
            return;
        }

        CachedBodyHttpServletRequest wrapped = new CachedBodyHttpServletRequest(request);
        String rawBody = new String(wrapped.getCachedBody(), StandardCharsets.UTF_8);

        String signature = request.getHeader("Omise-Signature");
        String timestamp = request.getHeader("Omise-Signature-Timestamp");

        verifier.verify(rawBody, signature, timestamp);

        chain.doFilter(wrapped, response);
    }
}
