package com.payment.razorpay.service;

import com.payment.razorpay.repository.PaymentRepository;
import com.payment.razorpay.dto.PaymentDto;
import com.payment.razorpay.model.Payment;
import com.razorpay.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.HashMap;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpaySecret;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    // CREATE ORDER
    public Map<String, Object> createOrder(Double amount) throws RazorpayException {

        RazorpayClient client = new RazorpayClient(razorpayKeyId, razorpaySecret);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", (int)(amount * 100));
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "txn_" + System.currentTimeMillis());

        Order order = client.orders.create(orderRequest);

        Map<String, Object> response = new HashMap<>();
        response.put("id", (String) order.get("id"));
        response.put("amount", ((Number) order.get("amount")).intValue());
        response.put("currency", (String) order.get("currency"));
        response.put("status", (String) order.get("status"));

        return response;
    }

    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    // VERIFY SIGNATURE
    public boolean verifySignature(PaymentDto dto) {
        try {
            String payload = dto.getOrderId() + "|" + dto.getPaymentId();

            Utils.verifySignature(payload, dto.getSignature(), razorpaySecret);
            return true;

        } catch (Exception e) {
            return false;
        }
    }
}
