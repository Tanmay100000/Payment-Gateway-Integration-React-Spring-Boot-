package com.payment.razorpay.controller;

import com.payment.razorpay.dto.PaymentDto;
import com.payment.razorpay.model.Payment;
import com.payment.razorpay.service.PaymentService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin("*")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create-order")
    public java.util.Map<String, Object> createOrder(@RequestParam Double amount) throws Exception {
        return paymentService.createOrder(amount);
    }

    @PostMapping("/save")
    public Payment savePayment(@RequestBody Payment payment) {
        return paymentService.savePayment(payment);
    }

    @PostMapping("/verify")
    public Map<String, String> verifyPayment(@RequestBody PaymentDto dto) {
        boolean isVerified = paymentService.verifySignature(dto);

        Map<String, String> response = new HashMap<>();
        response.put("status", isVerified ? "SUCCESS" : "FAILED");

        return response;
    }
}
