package br.com.orchestrated.orderservice.core.service;

import br.com.orchestrated.orderservice.core.document.Event;
import br.com.orchestrated.orderservice.core.dto.EventFilters;
import br.com.orchestrated.orderservice.core.repository.EventRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.security.oauthbearer.internals.secured.ValidateException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static io.micrometer.common.util.StringUtils.isEmpty;

@Slf4j
@Service
@AllArgsConstructor
public class EventService {

    private final EventRepository repository;

    public void notifyEnding(Event event){
        event.setId(event.getOrderId());
        event.setCreatedAt(LocalDateTime.now());
        save(event);
        log.info("Order {} with saga notified! TransactionId {}", event.getOrderId());
    }

    public Event findByFilters(EventFilters filters){
        validateEmptyFilter(filters);
        if (! isEmpty(filters.getOrderId())){
            return findOrderById(filters.getOrderId());
        }else{
            return findTransactionById(filters.getTransactionId());
        }
    }

    private Event findOrderById(String orderId){
        return repository.findTopicByOrderIdOrderByCreatedAtDesc(orderId)
                .orElseThrow(() -> new ValidateException("Event not found by order id"));
    }

    private Event findTransactionById(String transactionId){
        return repository.findTopicByTransactionIdOrderByCreatedAtDesc(transactionId)
                .orElseThrow(() -> new ValidateException("Event not found by transaction id"));
    }

    private void validateEmptyFilter(EventFilters filters) {
        if (isEmpty(filters.getOrderId()) && isEmpty(filters.getTransactionId())){
            throw new ValidateException("OrderID or TransactionID must be informed.");
        }
    }

    public List<Event> findAll(){
        return repository.findAllByOrderByCreatedAtDesc();
    }

    public Event save(Event event){ return repository.save(event); }
}
