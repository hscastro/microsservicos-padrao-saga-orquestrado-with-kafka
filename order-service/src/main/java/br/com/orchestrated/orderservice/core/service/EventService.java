package br.com.orchestrated.orderservice.core.service;

import br.com.orchestrated.orderservice.core.document.Event;
import br.com.orchestrated.orderservice.core.repository.EventRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public void notifyEnding(Event event){
        event.setId(event.getOrderId());
        event.setCreateAt(LocalDateTime.now());
        save(event);
        log.info("Order {} with saga notified! TransactionId {}", event.getOrderId());
    }

    public List<Event> findAll(){
        return eventRepository.findAllByOrderByCreatedAtDesc();
    }

    public Event save(Event event){ return eventRepository.save(event); }
}
