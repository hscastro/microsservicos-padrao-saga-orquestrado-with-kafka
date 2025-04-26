package br.com.orchestrated.orderservice.core.service;

import br.com.orchestrated.orderservice.core.document.Event;
import br.com.orchestrated.orderservice.core.document.Order;
import br.com.orchestrated.orderservice.core.dto.OrderRequest;
import br.com.orchestrated.orderservice.core.producer.SagaProducer;
import br.com.orchestrated.orderservice.core.repository.OrderRepository;
import br.com.orchestrated.orderservice.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrderService {

    public static final String TRANSACTION_ID_PATTERN = "%s_%s";

    private final EventService eventService;
    private final SagaProducer producer;
    private final JsonUtil jsonUtil;
    private final OrderRepository orderRepository;


    public Order createOrder(OrderRequest orderRequest){
        var order = Order.builder()
                .products(orderRequest.getProducts())
                .createdAt(LocalDateTime.now())
                .transactionId(
                        String.format(TRANSACTION_ID_PATTERN, Instant.now().toEpochMilli(), UUID.randomUUID()))
                .build();

        orderRepository.save(order);
        producer.sendEvent(jsonUtil.toJson(createPayload(order)));

        return order;
    }

    public Event createPayload(Order order){
        var event = Event.builder()
                .orderId(order.getId())
                .transactionId(order.getTransactionId())
                .payload(order)
                .createAt(LocalDateTime.now())
                .build();
        eventService.save(event);
        return event;
    }

    
}
