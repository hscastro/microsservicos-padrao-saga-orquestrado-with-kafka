package br.com.orchestrated.productvalidationservice.core.service;

import br.com.orchestrated.productvalidationservice.config.exception.ValidateException;
import br.com.orchestrated.productvalidationservice.core.dto.Event;
import br.com.orchestrated.productvalidationservice.core.dto.History;
import br.com.orchestrated.productvalidationservice.core.model.Validation;
import br.com.orchestrated.productvalidationservice.core.producer.KafkaProducer;
import br.com.orchestrated.productvalidationservice.core.repository.ProductRepository;
import br.com.orchestrated.productvalidationservice.core.repository.ValidationRepository;
import br.com.orchestrated.productvalidationservice.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static br.com.orchestrated.productvalidationservice.core.enums.ESagaStatus.*;
import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@Service
@AllArgsConstructor
public class ProductValidationService {

    private static final String CURENT_SOURCE = "PRODUCT_VALIDATION_SERVICE";
    private JsonUtil jsonUtil;
    private KafkaProducer producer;
    private ProductRepository productRepository;
    private ValidationRepository validationRepository;

    public void validateExistingProduct(Event event){
        try {
            checkCurrentValidation(event);
            handlerSuccess(event);

        } catch (Exception ex) {
            log.error("Error trying to validate product:", ex);
            handleFailCurrentNotExcuted(event, ex.getMessage());
        }
        producer.sendEvent(jsonUtil.toJson(event));
    }

    private void handleFailCurrentNotExcuted(Event event, String message) {
        event.setStatus(ROLLBACK_PENDING);
        event.setSource(CURENT_SOURCE);
        addHistory(event, "Fail to validate product: ".concat(message));
    }

    public void rollbackEvent(Event event){
        changeValidationToFail(event);
        event.setStatus(FAIL);
        event.setSource(CURENT_SOURCE);
        addHistory(event, "Rollback executed on product validation!");
        producer.sendEvent(jsonUtil.toJson(event));
    }

    private void changeValidationToFail(Event event) {
        validationRepository.findByOrderIdAndTransactionId(event.getPayload().getId(), event.getPayload().getTransactionId())
                .ifPresentOrElse(validation -> {
                    validation.setSuccess(false);
                    validationRepository.save(validation);
                }, ()-> createValidation(event, false));
    }


//    private void checkCurrentValidation(Event event) {
//        if (isEmpty(event.getPayload()) || isEmpty(event.getPayload().getProducts())){
//            throw new ValidateException("Product list is empty!");
//        }
//
//        if (isEmpty(event.getPayload().getId()) || isEmpty(event.getPayload().getTransactionId())){
//            throw new ValidateException("OrderId and TransactionId must be informed!");
//        }
//    }

    private void validateProductInformed(Event event) {
        if (isEmpty(event.getPayload()) || isEmpty(event.getPayload().getProducts())){
            throw new ValidateException("Product list is empty!");
        }

        if (isEmpty(event.getPayload().getId()) || isEmpty(event.getPayload().getTransactionId())){
            throw new ValidateException("OrderId and TransactionId must be informed!");
        }
    }

    private void checkCurrentValidation(Event event) {
        validateProductInformed(event);

        if (validationRepository.existsByOrderIdAndTransactionId(event.getOrderId(),
                event.getTransactionId())){
            throw new ValidateException("There's another transactionId for this validation.");
        }
    }

    private void validateExistingProduct(String code){
        if (!productRepository.existsByCode(code)){
            throw new ValidateException("Product does not exists in database!");
        }
    }

    private void createValidation(Event event, boolean success){
        var validation = Validation.builder()
                .orderId(event.getOrderId())
                .transactionId(event.getTransactionId())
                .success(success)
                .build();
        validationRepository.save(validation);
    }

    private void handlerSuccess(Event event) {
        event.setStatus(SUCCESS);
        event.setSource(CURENT_SOURCE);
        addHistory(event, "Product are validated successufully!");
    }


    private void addHistory(Event event, String message){
        var history = History
                .builder()
                .source(event.getSource())
                .status(event.getStatus())
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();
    }

}
