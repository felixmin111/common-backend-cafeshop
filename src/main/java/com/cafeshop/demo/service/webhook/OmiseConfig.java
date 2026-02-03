package com.cafeshop.demo.service.webhook;

import com.cafeshop.demo.service.omise.OmiseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration("omiseWebhookConfig")
@EnableConfigurationProperties(OmiseProperties.class)
public class OmiseConfig {}
