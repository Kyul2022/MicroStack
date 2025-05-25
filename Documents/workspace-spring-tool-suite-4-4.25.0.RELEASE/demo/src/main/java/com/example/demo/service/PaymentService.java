package com.example.demo.service;

import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    public boolean processPayment(double amount) {
        // Simulate payment (always succeeds for MVP)
        return true;
    }
}