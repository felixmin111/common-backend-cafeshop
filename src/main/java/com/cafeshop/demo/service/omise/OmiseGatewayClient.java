package com.cafeshop.demo.service.omise;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.JsonNode;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class OmiseGatewayClient {

    private final OmiseProperties props;

    private WebClient client() {
        // Omise uses HTTP Basic Auth: username = skey_..., password = empty
        String basic = props.getSecretKey() + ":";
        String auth = Base64.getEncoder().encodeToString(basic.getBytes(StandardCharsets.UTF_8));

        return WebClient.builder()
                .baseUrl(props.getBaseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + auth)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
    }

    public OmiseCreateChargeResult createPromptPayCharge(long amountSatang, String currency, String referenceNo) {
        // 1) Create source (type=promptpay)
        JsonNode source = client().post()
                .uri("/sources")
                .bodyValue("type=promptpay&amount=" + amountSatang + "&currency=" + currency)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        if (source == null || source.get("id") == null) {
            throw new IllegalStateException("Omise source creation failed");
        }
        String sourceId = source.get("id").asText();

        // Try to read QR payload / image from source (structure may vary by Omise version)
        String qrPayload = null;
        String qrImageUrl = null;

        JsonNode sc = source.path("scannable_code");
        if (!sc.isMissingNode()) {
            // Often: scannable_code.data + scannable_code.image.download_uri
            if (sc.get("data") != null && !sc.get("data").isNull()) {
                qrPayload = sc.get("data").asText();
            }
            JsonNode image = sc.path("image");
            if (image.get("download_uri") != null && !image.get("download_uri").isNull()) {
                qrImageUrl = image.get("download_uri").asText();
            }
        }

        // 2) Create charge using that source
        // metadata helps you match later if needed
        String body = "amount=" + amountSatang
                + "&currency=" + currency
                + "&source=" + sourceId
                + "&metadata[referenceNo]=" + referenceNo;

        JsonNode charge = client().post()
                .uri("/charges")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        if (charge == null || charge.get("id") == null) {
            throw new IllegalStateException("Omise charge creation failed");
        }

        String chargeId = charge.get("id").asText();

        // Sometimes charge also contains source/scannable_code; if missing earlier, try from charge:
        if (qrPayload == null) {
            JsonNode chargeSource = charge.path("source");
            JsonNode chargeSc = chargeSource.path("scannable_code");
            if (chargeSc.get("data") != null && !chargeSc.get("data").isNull()) {
                qrPayload = chargeSc.get("data").asText();
            }
            JsonNode image = chargeSc.path("image");
            if (qrImageUrl == null && image.get("download_uri") != null && !image.get("download_uri").isNull()) {
                qrImageUrl = image.get("download_uri").asText();
            }
        }

        return new OmiseCreateChargeResult(chargeId, qrPayload, qrImageUrl);
    }

    public record OmiseCreateChargeResult(String chargeId, String qrPayload, String qrImageUrl) {}
}
