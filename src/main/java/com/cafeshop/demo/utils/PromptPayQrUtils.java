package com.cafeshop.demo.utils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

public class PromptPayQrUtils {

    // Build PromptPay payload (static QR, amount optional but recommended)
    // promptPayId: phone (10 digits) or national id (13 digits)
    public static String buildPayload(String promptPayId, BigDecimal amount) {
        String id = normalizePromptPayId(promptPayId);

        // Merchant Account Information template for PromptPay
        // ID "29" contains:
        // 00 = AID "A000000677010111"
        // 01 or 02 = mobile/national id
        String merchantInfo = tlv("00", "A000000677010111") + tlv(idType(id), idValue(id));

        String payload =
                tlv("00", "01") +                 // Payload Format Indicator
                        tlv("01", "12") +                 // Point of Initiation Method (12 = static)
                        tlv("29", merchantInfo) +         // Merchant Account Info
                        tlv("53", "764") +                // Currency THB
                        (amount != null ? tlv("54", formatAmount(amount)) : "") + // Amount
                        tlv("58", "TH") +                 // Country
                        tlv("62", tlv("07", "CAFESHOP")); // Additional data (optional tag 07)

        // CRC16
        String withCrcTag = payload + "6304";
        String crc = crc16Ccitt(withCrcTag);
        return payload + "6304" + crc;
    }

    private static String tlv(String tag, String value) {
        String len = String.format("%02d", value.length());
        return tag + len + value;
    }

    private static String normalizePromptPayId(String raw) {
        String digits = raw == null ? "" : raw.replaceAll("\\D", "");
        if (digits.startsWith("0") && digits.length() == 10) return digits; // mobile local
        if (digits.length() == 13) return digits; // national id
        // allow already normalized
        return digits;
    }

    private static String idType(String id) {
        // mobile (10 digits starting with 0) -> tag 01
        // national id (13 digits) -> tag 02
        if (id.length() == 10 && id.startsWith("0")) return "01";
        return "02";
    }

    private static String idValue(String id) {
        // For mobile, convert to international format without +
        // Thai mobile: 0XXXXXXXXX -> 66XXXXXXXXX (drop leading 0)
        if (id.length() == 10 && id.startsWith("0")) {
            return "66" + id.substring(1);
        }
        return id;
    }

    private static String formatAmount(BigDecimal amount) {
        // 2 decimal places
        return amount.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
    }

    // CRC16-CCITT (FFFF)
    private static String crc16Ccitt(String input) {
        byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
        int crc = 0xFFFF;
        for (byte b : bytes) {
            crc ^= (b & 0xFF) << 8;
            for (int i = 0; i < 8; i++) {
                crc = ((crc & 0x8000) != 0) ? (crc << 1) ^ 0x1021 : (crc << 1);
                crc &= 0xFFFF;
            }
        }
        return String.format("%04X", crc);
    }
}
