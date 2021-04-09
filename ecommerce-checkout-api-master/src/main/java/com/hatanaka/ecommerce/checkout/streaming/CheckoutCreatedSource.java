package com.hatanaka.ecommerce.checkout.streaming;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

// Abstração Produtor
public interface CheckoutCreatedSource {

    String OUTPUT = "checkout-created-output";  // tópico virtual

    @Output(OUTPUT)
    MessageChannel output();
}
