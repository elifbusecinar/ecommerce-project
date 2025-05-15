package com.ecommerce.backend.controller;

import com.ecommerce.backend.model.Order; // Order modelini import edin
import com.ecommerce.backend.model.OrderStatus;
import com.ecommerce.backend.model.PaymentStatus;
import com.ecommerce.backend.service.OrderService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject; // StripeObject importu
import com.stripe.net.Webhook;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/stripe-webhooks") // application.properties'deki context-path ile birlikte /api/stripe-webhooks olacak
@RequiredArgsConstructor
@Tag(name = "Stripe Webhooks", description = "Handles incoming webhooks from Stripe")
public class WebhookController {

    @Value("${stripe.webhook.secret}")
    private String endpointSecret; // application.properties'den gelen signing secret

    private final OrderService orderService; // Siparişleri güncellemek için

    @Operation(summary = "Handle Stripe Webhook Events", description = "Listens for events from Stripe (e.g., payment success/failure) and processes them.")
    @PostMapping
    public ResponseEntity<String> handleStripeEvent(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        if (endpointSecret == null || endpointSecret.isBlank()) {
            log.error("Stripe webhook secret ('stripe.webhook.secret') is not configured in application.properties.");
            // Bu durumda hata döndürmek, yapılandırma eksikliğini gösterir.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Webhook secret not configured.");
        }

        Event event;

        try {
            // Gelen isteğin gerçekten Stripe'dan geldiğini ve değiştirilmediğini doğrula
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            log.warn("Webhook signature verification failed.", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Signature verification failed.");
        } catch (Exception e) {
            log.error("Error parsing webhook event or other unexpected error during constructEvent.", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error parsing event.");
        }

        // EventDataObjectDeserializer, event.getDataObject() yerine kullanılır (Stripe-java v22+)
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;
        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        } else {
            // Belki de bu olay türü için bir data object beklenmiyordur.
            // Bu durumu loglayabilir veya özel olarak ele alabilirsiniz.
            log.warn("Webhook event data object is not present for event type: {}", event.getType());
        }


        // Olay türüne göre işlemleri yap
        switch (event.getType()) {
            case "payment_intent.succeeded":
                PaymentIntent paymentIntentSucceeded = (PaymentIntent) stripeObject;
                if (paymentIntentSucceeded != null) {
                    log.info("PaymentIntent Succeeded: {}", paymentIntentSucceeded.getId());
                    // Ödeme başarılı olduğunda sipariş durumunu güncelle
                    // PaymentIntent ID'sini veya metadata'daki sipariş ID'nizi kullanarak siparişi bulun
                    // Örnek: Eğer PaymentIntent ID'sini siparişin trackingNumber'ı olarak kaydettiyseniz:
                    try {
                        Order order = orderService.getOrderByTrackingNumber(paymentIntentSucceeded.getId());
                        if (order != null) {
                            // Siparişin mevcut durumunu kontrol ederek gereksiz güncellemelerden kaçının
                            if (order.getPaymentStatus() != PaymentStatus.COMPLETED) {
                                orderService.updateOrderStatus(order.getId(), OrderStatus.PROCESSING); // Veya COMPLETED
                                orderService.updatePaymentStatus(order.getId(), PaymentStatus.COMPLETED);
                                log.info("Order ID {} updated to PROCESSING and payment status to COMPLETED for PaymentIntent ID: {}", order.getId(), paymentIntentSucceeded.getId());
                            } else {
                                log.info("Order ID {} already marked as COMPLETED. No update needed for PaymentIntent ID: {}", order.getId(), paymentIntentSucceeded.getId());
                            }
                        } else {
                             log.warn("No order found for succeeded PaymentIntent ID: {}", paymentIntentSucceeded.getId());
                        }
                    } catch (Exception e) {
                        log.error("Error updating order for succeeded PaymentIntent ID {}: {}", paymentIntentSucceeded.getId(), e.getMessage(), e);
                    }
                }
                break;
            case "payment_intent.payment_failed":
                PaymentIntent paymentIntentFailed = (PaymentIntent) stripeObject;
                if (paymentIntentFailed != null) {
                    log.warn("PaymentIntent Payment Failed: {}", paymentIntentFailed.getId());
                    // Ödeme başarısız olduğunda sipariş durumunu güncelle
                    try {
                        Order order = orderService.getOrderByTrackingNumber(paymentIntentFailed.getId());
                         if (order != null) {
                            if (order.getPaymentStatus() != PaymentStatus.FAILED) {
                                orderService.updateOrderStatus(order.getId(), OrderStatus.CANCELLED); // Veya özel bir PAYMENT_FAILED durumu
                                orderService.updatePaymentStatus(order.getId(), PaymentStatus.FAILED);
                                log.info("Order ID {} updated to CANCELLED and payment status to FAILED for PaymentIntent ID: {}", order.getId(), paymentIntentFailed.getId());
                                // Burada stokları geri ekleme mantığı da çağrılabilir (OrderService içinde)
                            } else {
                                log.info("Order ID {} already marked as FAILED. No update needed for PaymentIntent ID: {}", order.getId(), paymentIntentFailed.getId());
                            }
                        } else {
                             log.warn("No order found for failed PaymentIntent ID: {}", paymentIntentFailed.getId());
                        }
                    } catch (Exception e) {
                        log.error("Error updating order for failed PaymentIntent ID {}: {}", paymentIntentFailed.getId(), e.getMessage(), e);
                    }
                }
                break;
            // Diğer olay türlerini de buraya ekleyebilirsiniz (örn: 'charge.refunded')
            // case "charge.refunded":
            //     Charge chargeRefunded = (Charge) stripeObject;
            //     // İade işlemleri...
            //     break;
            default:
                log.warn("Unhandled event type: {}", event.getType());
        }

        return ResponseEntity.ok().body("Received"); // Stripe'a 200 OK döndür
    }
}