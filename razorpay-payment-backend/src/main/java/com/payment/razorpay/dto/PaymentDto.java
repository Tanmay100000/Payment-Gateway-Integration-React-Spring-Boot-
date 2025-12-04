package com.payment.razorpay.dto;

import lombok.Data;

@Data
public class PaymentDto {

	private String orderId;
    private String paymentId;
    private String signature;
    private Double amount;
    private String status;

}
