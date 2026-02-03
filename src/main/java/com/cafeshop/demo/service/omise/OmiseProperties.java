package com.cafeshop.demo.service.omise;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter @Setter
@ConfigurationProperties(prefix = "omise.api")
public class OmiseProperties {
    private String baseUrl;
    private String secretKey;
}
