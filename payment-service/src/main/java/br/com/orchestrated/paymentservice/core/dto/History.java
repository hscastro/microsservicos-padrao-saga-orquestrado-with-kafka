package br.com.orchestrated.paymentservice.core.dto;


import br.com.orchestrated.paymentservice.core.enums.EEventSource;
import br.com.orchestrated.paymentservice.core.enums.ESagaStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class History {
    private EEventSource source;
    private ESagaStatus status;
    private String message;
    private LocalDateTime createdAt;

}
