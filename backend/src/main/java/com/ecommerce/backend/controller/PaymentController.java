package com.ecommerce.backend.controller;

import com.ecommerce.backend.service.StripeService; // Oluşturduğunuz StripeService
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payments") // Endpoint'i /api/payments olarak değiştirebiliriz
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200") // Frontend adresiniz
@Tag(name = "Payment", description = "Payment processing APIs")
public class PaymentController {

    private final StripeService stripeService;

    // Belki frontend'den yayınlanabilir anahtarı almak için bir endpoint (opsiyonel)
    @Value("${stripe.publishable.key}") // application.properties'e eklemeniz gerekebilir
    private String stripePublishableKey;

    @GetMapping("/config")
    public ResponseEntity<Map<String, String>> getStripeConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("publishableKey", stripePublishableKey);
        return ResponseEntity.ok(config);
    }

    @Operation(summary = "Create Payment Intent", description = "Creates a Stripe Payment Intent and returns its client secret.")
    @PostMapping("/create-payment-intent")
    public ResponseEntity<Map<String, String>> createPaymentIntent(@RequestBody PaymentIntentRequest paymentIntentRequest) {
        try {
            // Frontend'den gelen tutar (örneğin, 10.00 USD ise 1000 olarak)
            // Gerçek uygulamada bu tutarı sepetin toplamından veya siparişten almalısınız.
            Long amountInCents = Math.round(paymentIntentRequest.getAmount() * 100); // Örneğin, amount double ise

            PaymentIntent paymentIntent = stripeService.createPaymentIntent(amountInCents, paymentIntentRequest.getCurrency());

            Map<String, String> response = new HashMap<>();
            response.put("clientSecret", paymentIntent.getClientSecret());
            response.put("paymentIntentId", paymentIntent.getId()); // Frontend'de gerekirse

            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "An unexpected error occurred while creating payment intent.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Frontend'den gelen isteği karşılamak için basit bir DTO
    static class PaymentIntentRequest {
        private Double amount; // Frontend'den gelen tutar (örn: 123.45)
        private String currency; // Para birimi (örn: "usd", "try")

        // Getters and Setters
        public Double getAmount() {
            return amount;
        }

        public void setAmount(Double amount) {
            this.amount = amount;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }
    }

    // Mevcut /pay ve /status endpoint'leriniz Stripe ile doğrudan ilgili değilse
    // kaldırılabilir veya Stripe'a uygun şekilde güncellenebilir.
    // Şimdilik yorum satırı yapıyorum:
    /*
    @PostMapping("/pay")
    public String makePayment(@RequestParam double amount) {
        return "Payment of $" + amount + " was successful! 💳✔️";
    }

    @GetMapping("/status")
    public String paymentStatus() {
        return "Payment status: SUCCESS!";
    }
    */
}