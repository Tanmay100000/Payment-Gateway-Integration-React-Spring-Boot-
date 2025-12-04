package com.payment.razorpay.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payment.razorpay.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long>  {

}
