package com.ecommerce.backend.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct; // Eğer Spring Boot 3+ ve Jakarta EE kullanıyorsanız
// import javax.annotation.PostConstruct; // Eğer Spring Boot 2.x ve Java EE kullanıyorsanız
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // Eğer constructor injection için Lombok kullanıyorsanız
public class StripeService {

    @Value("${stripe.secret.key}")
    private String secretKey;

    // @RequiredArgsConstructor kullanmıyorsanız constructor'ı manuel ekleyin:
    // public StripeService(@Value("${stripe.secret.key}") String secretKey) {
    //     this.secretKey = secretKey;
    // }

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    public PaymentIntent createPaymentIntent(Long amount, String currency) throws StripeException {
        PaymentIntentCreateParams params =
            PaymentIntentCreateParams.builder()
                .setAmount(amount) // Tutar cent cinsinden olmalı (örn: 10.00 USD için 1000)
                .setCurrency(currency.toLowerCase()) // "usd", "eur", vb.
                // Ödeme yöntemlerini otomatik olarak yönetmek için Stripe'a bırakabilirsiniz
                // veya manuel olarak belirleyebilirsiniz.
                .setAutomaticPaymentMethods(
                  PaymentIntentCreateParams.AutomaticPaymentMethods
                    .builder()
                    .setEnabled(true)
                    .build()
                )
                // Ya da spesifik ödeme yöntemleri:
                // .addPaymentMethodType("card")
                .build();
        return PaymentIntent.create(params);
    }

    // Buraya Stripe ile ilgili diğer metodları ekleyebilirsiniz
    // (örneğin, ödeme onayı, iade işlemleri, webhook işleme vb.)
}