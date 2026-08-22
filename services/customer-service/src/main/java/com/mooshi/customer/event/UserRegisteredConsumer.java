package com.mooshi.customer.event;

import com.mooshi.customer.model.Customer;
import com.mooshi.customer.repository.CustomerRepository;
import com.mooshi.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRegisteredConsumer {

    private final CustomerRepository customerRepository;

    @KafkaListener(topics = UserRegisteredEvent.TOPIC, groupId = "customer-service")
    public void handleUserRegistered(UserRegisteredEvent event) {
        log.info("Received UserRegisteredEvent for userId={}", event.userId());

        if (customerRepository.findByUserId(event.userId()).isPresent()) {
            log.info("Customer already exists for userId={}", event.userId());
            return;
        }

        Customer customer = Customer.builder()
            .userId(event.userId())
            .build();
        customerRepository.save(customer);
        log.info("Created customer profile for userId={}", event.userId());
    }
}
