package br.com.orchestrated.orchestratorservice.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderProducts {
    private Product product;
    private int quantity;
}
