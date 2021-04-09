package com.hatanaka.ecommerce.checkout.service;

import com.hatanaka.ecommerce.checkout.entity.CheckoutEntity;
import com.hatanaka.ecommerce.checkout.event.CheckoutCreatedEvent;
import com.hatanaka.ecommerce.checkout.repository.CheckoutRepository;
import com.hatanaka.ecommerce.checkout.resource.CheckoutRequest;
import com.hatanaka.ecommerce.checkout.streaming.CheckoutCreatedSource;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
// @RequiredArgsConstructor faz um construtor em
// tempo de compilação para os atributos final
@RequiredArgsConstructor
public class CheckoutServiceImplementation implements CheckoutService{
    // @Autowired não é recomendado
    private final CheckoutRepository checkoutRepository;
    private final CheckoutCreatedSource checkoutCreatedSource;

    @Override
    public Optional<CheckoutEntity> create(CheckoutRequest checkoutRequest) {
        final CheckoutEntity checkoutEntity = CheckoutEntity.builder()
                .code(UUID.randomUUID().toString())
                .status(CheckoutEntity.Status.CREATED)
                .build();

        final CheckoutEntity entity = checkoutRepository.save(checkoutEntity);

        // Envia uma mensagem pelo Kafka
        final CheckoutCreatedEvent checkoutCreatedEvent = CheckoutCreatedEvent
                .newBuilder()
                .setCheckoutCode(entity.getCode())
                .setStatus(entity.getStatus().name())
                .build();
        checkoutCreatedSource.output().send(MessageBuilder
                .withPayload(checkoutCreatedEvent).build()
        );

        return Optional.of(entity);
    }
}


//    @Override
//    public Optional<CheckoutEntity> create(CheckoutRequest checkoutRequest) {
//        final CheckoutEntity checkoutEntity = CheckoutEntity.builder()
//                .code(UUID.randomUUID().toString())
//                .status(CheckoutEntity.Status.CREATED)
//                .build();
//
//        return Optional.of(checkoutRepository.save(checkoutEntity));
//    }