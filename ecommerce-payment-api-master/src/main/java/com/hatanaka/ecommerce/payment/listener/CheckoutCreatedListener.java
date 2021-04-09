package com.hatanaka.ecommerce.payment.listener;

import com.hatanaka.ecommerce.checkout.event.CheckoutCreatedEvent;
import com.hatanaka.ecommerce.payment.entity.PaymentEntity;
import com.hatanaka.ecommerce.payment.event.PaymentCreatedEvent;
import com.hatanaka.ecommerce.payment.service.PaymentService;
import com.hatanaka.ecommerce.payment.streaming.CheckoutProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class CheckoutCreatedListener {

    private final CheckoutProcessor checkoutProcessor;
    private final PaymentService paymentService;

    @StreamListener(CheckoutProcessor.INPUT)
    public void handler(CheckoutCreatedEvent event) {
        // Processar pagamento
        // Vai até algum meio de pagamento efetuá-lo
        // Salva os dados de pagamento no banco de dados
        // Enviar o evento de pagamento processado
        final PaymentEntity paymentEntity = paymentService.create(event).orElseThrow();

        final PaymentCreatedEvent paymentCreatedEvent = PaymentCreatedEvent
                .newBuilder()
                .setCheckoutCode(paymentEntity.getCheckoutCode())
                .setPaymentCode(paymentEntity.getCode())
                .build();
        checkoutProcessor.output().send(
                MessageBuilder.withPayload(paymentCreatedEvent).build()
        );
    }
}
