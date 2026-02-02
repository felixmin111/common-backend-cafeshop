package com.cafeshop.demo.service.webhook;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

@Component
public class OmiseSignatureVerifier {

    @Value("${omise.webhook.secret:}")
    private String webhookSecretBase64;

    public void verify(String rawBody, String signatureHeader, String timestampHeader) {
        System.out.println("signatureHeader: " + signatureHeader);
        System.out.println("timestampHeader: " + timestampHeader);

        if (webhookSecretBase64 == null || webhookSecretBase64.isBlank()
                || "your_webhook_secret_base64".equals(webhookSecretBase64)) {
            return;
        }

        if (signatureHeader == null || signatureHeader.isBlank()) {
            throw new IllegalArgumentException("Missing Omise-Signature header");
        }
        if (timestampHeader == null || timestampHeader.isBlank()) {
            throw new IllegalArgumentException("Missing Omise-Signature-Timestamp header");
        }


        try {
            String signedPayload = timestampHeader + "." + (rawBody == null ? "" : rawBody);

            byte[] secret = Base64.getDecoder().decode(webhookSecretBase64);

            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));
            byte[] expected = mac.doFinal(signedPayload.getBytes(StandardCharsets.UTF_8));

            String[] candidates = signatureHeader.split(",");

            for (String c : candidates) {
                String sig = c.trim();

                if (!isHex(sig) || sig.length() != 64) continue;

                byte[] provided = hexToBytes(sig);
                if (MessageDigest.isEqual(provided, expected)) {
                    return;
                }
            }

            throw new IllegalArgumentException("Invalid webhook signature");

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Webhook signature verification failed: " + e.getMessage(), e);
        }
    }

    private static byte[] hexToBytes(String hex) {
        if (hex.length() % 2 != 0) throw new IllegalArgumentException("Invalid hex length");
        byte[] out = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i += 2) {
            out[i / 2] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
        }
        return out;
    }

    private static boolean isHex(String s) {
        return s != null && s.matches("^[0-9a-fA-F]+$") && s.length() % 2 == 0;
    }
}
