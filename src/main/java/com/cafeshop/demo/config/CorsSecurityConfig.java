package com.cafeshop.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsSecurityConfig {
//    @Value("${app.cors.allowed-origins:}")
    private String allowedOrigins="https://common-frontend-9e8d6a8b4262.herokuapp.com/";

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // supports comma-separated origins in env var
        List<String> origins = allowedOrigins.isBlank()
                ? List.of()
                : List.of(allowedOrigins.split("\\s*,\\s*"));
        config.setAllowedOrigins(origins);

        config.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization","Content-Type","Accept","Origin"));
        config.setExposedHeaders(List.of("Authorization"));

        // If you use cookies/sessions -> true. If you only use JWT in headers, can be false.
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
