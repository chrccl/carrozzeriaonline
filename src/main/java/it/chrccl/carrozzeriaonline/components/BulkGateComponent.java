package it.chrccl.carrozzeriaonline.components;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Slf4j
@Component
public class BulkGateComponent {

    @Value("${bulkgate.application.id}")
    private String APPLICATION_ID;

    @Value("${bulkgate.application.token}")
    private String APPLICATION_TOKEN;

    @Value("${bulkgate.quota.id}")
    private String REQUEST_QUOTA_ID;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String sendOtp(String phoneNumber) {
        String jsonPayload = String.format(
                "{ \"application_id\": \"%s\", \"application_token\": \"%s\", \"number\": \"%s\", " +
                        "\"language\": \"it\", \"code_type\": \"int\", \"code_length\": 6, " +
                        "\"expiration\": 900, \"request_quota_identification\": \"%s\" }",
                APPLICATION_ID, APPLICATION_TOKEN, phoneNumber, REQUEST_QUOTA_ID
        );
        log.info("Sending OTP to phone number: {}", phoneNumber);
        try {
            final String SEND_API_URL = "https://portal.bulkgate.com/api/1.0/otp/send";
            String response = sendPostRequest(SEND_API_URL, jsonPayload);
            log.debug("sendOtp API response: {}", response);
            JsonNode responseJson = objectMapper.readTree(response);
            if (responseJson.has("data")) {
                String otpId = responseJson.path("data").path("id").asText();
                log.info("OTP sent successfully. OTP ID: {}", otpId);
                return otpId;
            } else {
                log.error("Unexpected sendOtp API response: {}", response);
            }
        } catch (IOException e) {
            log.error("Error sending OTP to {}: {}", phoneNumber, e.getMessage(), e);
        }
        return null;
    }

    public boolean verifyOtp(String otpID, String code) {
        String jsonPayload = String.format(
                "{ \"application_id\": \"%s\", \"application_token\": \"%s\", \"id\": \"%s\", \"code\": \"%s\" }",
                APPLICATION_ID, APPLICATION_TOKEN, otpID, code
        );
        log.info("Verifying OTP ID: {} with code: {}", otpID, code);
        try {
            final String VERIFY_API_URL = "https://portal.bulkgate.com/api/1.0/otp/verify";
            String response = sendPostRequest(VERIFY_API_URL, jsonPayload);
            log.debug("verifyOtp API response: {}", response);
            JsonNode responseJson = objectMapper.readTree(response);
            boolean verified = responseJson.path("data").path("verified").asBoolean(false);
            log.info("OTP verification result: {}", verified);
            return verified;
        } catch (IOException e) {
            log.error("Error verifying OTP (ID: {}): {}", otpID, e.getMessage(), e);
        }
        return false;
    }

    public String resendOtp(String otpID) {
        String jsonPayload = String.format(
                "{ \"application_id\": \"%s\", \"application_token\": \"%s\", \"id\": \"%s\" }",
                APPLICATION_ID, APPLICATION_TOKEN, otpID
        );
        log.info("Resending OTP for OTP ID: {}", otpID);
        try {
            final String RESEND_API_URL = "https://portal.bulkgate.com/api/1.0/otp/resend";
            String response = sendPostRequest(RESEND_API_URL, jsonPayload);
            log.debug("resendOtp API response: {}", response);
            JsonNode responseJson = objectMapper.readTree(response);
            if (responseJson.has("data")) {
                String newOtpId = responseJson.path("data").path("id").asText();
                log.info("OTP resent successfully. New OTP ID: {}", newOtpId);
                return newOtpId;
            } else {
                log.error("Unexpected resendOtp API response: {}", response);
            }
        } catch (IOException e) {
            log.error("Error resending OTP for OTP ID {}: {}", otpID, e.getMessage(), e);
        }
        return null;
    }

    private String sendPostRequest(String urlString, String jsonPayload) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.setDoOutput(true);

        log.debug("Sending POST to URL: {} with payload: {}", urlString, jsonPayload);
        try (OutputStream os = connection.getOutputStream()) {
            os.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
            os.flush();
        }

        int responseCode = connection.getResponseCode();
        log.debug("HTTP response code: {} from URL: {}", responseCode, urlString);

        InputStream stream = (responseCode >= 200 && responseCode < 300)
                ? connection.getInputStream() : connection.getErrorStream();

        String response;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            response = reader.lines().collect(Collectors.joining());
        }
        if (responseCode >= 200 && responseCode < 300) {
            return response;
        } else {
            throw new IOException("HTTP error code: " + responseCode + ". Response: " + response);
        }
    }

}
