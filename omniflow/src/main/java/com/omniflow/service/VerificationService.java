package com.omniflow.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.omniflow.dto.VerificationResultDto;
import com.omniflow.model.Order;
import com.omniflow.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Optional;

@Service
public class VerificationService {

    private static final String SALT = "OMNIFLOW_ENTERPRISE_CRYPTO_SALT_2026_X99";
    private final OrderRepository orderRepository;

    public VerificationService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Computes deterministic cryptographic SHA-256 tamper-evident hash for an Order.
     */
    public String calculateVerificationHash(Order order) {
        try {
            String payload = String.format("%s|%s|%s|%s|%s",
                    order.getOrderNumber(),
                    order.getTotalAmount() != null ? order.getTotalAmount().toPlainString() : "0.00",
                    order.getCustomerEmail() != null ? order.getCustomerEmail().trim().toLowerCase() : "",
                    order.getOrderDate() != null ? order.getOrderDate().toString() : "",
                    SALT
            );

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(payload.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error computing cryptographic verification hash", e);
        }
    }

    /**
     * Generates a ZXing QR Code encoded as base64 PNG data URI.
     */
    public String generateQrCodeBase64(String content, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height);

            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(pngData);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Verifies order authenticity against database and recalculates hash to detect tampering.
     */
    public VerificationResultDto verify(String query) {
        VerificationResultDto result = new VerificationResultDto();
        result.setVerifiedAt(java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        if (query == null || query.trim().isEmpty()) {
            result.setValid(false);
            result.setStatusMessage("Empty search query provided.");
            return result;
        }

        String cleaned = query.trim();
        Optional<Order> orderOpt = orderRepository.findByVerificationHash(cleaned);
        if (orderOpt.isEmpty()) {
            orderOpt = orderRepository.findByOrderNumber(cleaned);
        }

        if (orderOpt.isEmpty()) {
            result.setValid(false);
            result.setStatusMessage("RECORD NOT FOUND: No valid consignment or order matches this hash/number.");
            return result;
        }

        Order order = orderOpt.get();
        String currentHash = calculateVerificationHash(order);

        result.setOrderNumber(order.getOrderNumber());
        result.setCustomerName(order.getCustomerName());
        result.setOrderDate(order.getOrderDate());
        result.setOrderStatus(order.getStatus().name());
        result.setTotalAmount(order.getTotalAmount());
        result.setItemCount(order.getItems() != null ? order.getItems().size() : 0);
        result.setStoredHash(order.getVerificationHash());
        result.setComputedHash(currentHash);

        if (order.getVerificationHash() != null && order.getVerificationHash().equalsIgnoreCase(currentHash)) {
            result.setValid(true);
            result.setStatusMessage("VERIFIED AUTHENTIC: Cryptographic SHA-256 signature matches digital ledger records.");
        } else {
            result.setValid(false);
            result.setStatusMessage("WARNING - INTEGRITY COMPROMISED: Record payload differs from original cryptographic signature!");
        }

        return result;
    }
}

