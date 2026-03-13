package com.cafeshop.demo.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Service
public class QzTraySigningService {

    @Value("${qz.certificate-path}")
    private String certificatePath;

    @Value("${qz.private-key-path}")
    private String privateKeyPath;

    private String certificateText;
    private PrivateKey privateKey;

    @PostConstruct
    public void init() {
        this.certificateText = loadTextFile(certificatePath);
        this.privateKey = loadPrivateKey(privateKeyPath);
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

    private String loadTextFile(String path) {
        try (InputStream is = new ClassPathResource(path).getInputStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load file: " + path, e);
        }
    }

    private PrivateKey loadPrivateKey(String path) {
        try (InputStream is = new ClassPathResource(path).getInputStream()) {
            String pem = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            String privateKeyPem = pem
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] decoded = Base64.getDecoder().decode(privateKeyPem);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(spec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load private key: " + path, e);
        }
    }
}