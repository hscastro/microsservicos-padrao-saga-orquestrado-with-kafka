package br.com.orchestrated.paymentservice.core.dto;

import br.com.orchestrated.paymentservice.core.enums.EEventSource;
import br.com.orchestrated.paymentservice.core.enums.ESagaStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    private String id;
    private String transactionId;
    private String orderId;
    private String payload;
    private EEventSource source;
    private ESagaStatus status;
    private List<History> eventHistory;
}
