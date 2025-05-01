package br.com.orchestrated.productvalidationservice.core.repository;

import br.com.orchestrated.productvalidationservice.core.model.Validation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ValidationRepository extends JpaRepository<Validation, Integer> {

    Boolean existsByOrderIdAndTransactionId(String orderId);
    Optional<Validation> findByOrderIdAndTransactionId(String orderId, String transactionId);

}
