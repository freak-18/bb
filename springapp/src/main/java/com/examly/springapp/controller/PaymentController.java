package com.examly.springapp.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "http://localhost:3000")
public class PaymentController {

    @Value("${googlepay.merchant.id}")
    private String merchantId;

    @Value("${googlepay.environment}")
    private String environment;

    @Value("${googlepay.gateway}")
    private String gateway;

    @Value("${googlepay.gateway.merchant.id}")
    private String gatewayMerchantId;

    @PostMapping("/initiate")
    public ResponseEntity<Map<String, String>> initiatePayment(@RequestBody Map<String, String> paymentData) {
        try {
            String orderId = paymentData.get("orderId");
            String amount = paymentData.get("amount");
            
            // Create response for Google Pay
            Map<String, String> response = new HashMap<>();
            response.put("merchantId", merchantId);
            response.put("environment", environment);
            response.put("gateway", gateway);
            response.put("gatewayMerchantId", gatewayMerchantId);
            response.put("orderId", orderId);
            response.put("amount", amount);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to initiate payment: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyPayment(@RequestBody Map<String, Object> paymentData) {
        try {
            // In a real implementation, you would verify the payment token
            // with your payment processor (Stripe, Square, etc.)
            
            String paymentToken = (String) paymentData.get("paymentToken");
            String orderId = (String) paymentData.get("orderId");
            
            // Simulate payment verification
            Map<String, String> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("orderId", orderId);
            response.put("message", "Payment verified successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("status", "FAILED");
            error.put("error", "Payment verification failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }


}