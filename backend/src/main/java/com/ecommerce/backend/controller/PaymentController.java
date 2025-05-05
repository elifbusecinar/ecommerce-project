package com.ecommerce.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    @PostMapping("/pay")
    public String makePayment(@RequestParam double amount) {
        // Burada gerçek Stripe/PayPal integration yapılacak.
        // Şimdilik sadece örnek bir cevap dönelim:
        return "Payment of $" + amount + " was successful! 💳✔️";
    }

    @GetMapping("/status")
    public String paymentStatus() {
        // Gerçek senaryoda ödeme durumunu kontrol ederiz.
        return "Payment status: SUCCESS!";
    }
}
