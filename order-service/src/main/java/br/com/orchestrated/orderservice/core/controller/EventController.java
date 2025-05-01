package br.com.orchestrated.orderservice.core.controller;

import br.com.orchestrated.orderservice.core.document.Event;
import br.com.orchestrated.orderservice.core.dto.EventFilters;
import br.com.orchestrated.orderservice.core.service.EventService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/event")
public class EventController {

    private final EventService eventService;

    @GetMapping
    public Event findFilters(EventFilters filters){
        return eventService.findByFilters(filters);
    }

    @GetMapping("all")
    public List<Event> findAll(){
        return eventService.findAll();
    }
}
