package com.cafeshop.demo.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Service
public class QzTraySigningService {

    @Value("${qz.certificate:}")
    private String certificateText;

    @Value("${qz.private-key:}")
    private String privateKeyPemText;

    private PrivateKey privateKey;

    @PostConstruct
    public void init() {
        if (certificateText == null || certificateText.isBlank()) {
            throw new RuntimeException("QZ certificate is missing. Please set QZ_CERTIFICATE.");
        }

        if (privateKeyPemText == null || privateKeyPemText.isBlank()) {
            throw new RuntimeException("QZ private key is missing. Please set QZ_PRIVATE_KEY.");
        }

        this.privateKey = loadPrivateKeyFromString(privateKeyPemText);
    }

    public String getCertificate() {
        return certificateText;
    }

    public String sign(String request) {
        try {
            Signature signature = Signature.getInstance("SHA512withRSA");
            signature.initSign(privateKey);
            signature.update(request.getBytes(StandardCharsets.UTF_8));
            byte[] signed = signature.sign();
            return Base64.getEncoder().encodeToString(signed);
        } catch (Exception e) {
            throw new RuntimeException("Failed to sign QZ request", e);
        }
    }

    private PrivateKey loadPrivateKeyFromString(String pem) {
        try {
            if (pem.contains("-----BEGIN RSA PRIVATE KEY-----")) {
                throw new RuntimeException(
                        "Unsupported key format: RSA PRIVATE KEY. Convert it to PKCS8 BEGIN PRIVATE KEY format."
                );
            }

            String privateKeyPem = pem
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] decoded = Base64.getDecoder().decode(privateKeyPem);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(spec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load QZ private key", e);
        }
    }
}