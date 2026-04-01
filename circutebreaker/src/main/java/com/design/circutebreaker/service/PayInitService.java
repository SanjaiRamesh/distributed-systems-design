package com.design.circutebreaker.service;

import com.design.circutebreaker.model.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PayInitService {

    @Autowired
    private MyRedisService  myRedisService;


    public ResponseEntity<String> handlePayment(Payment payment) {

        myRedisService.saveData(payment.getFirmRootId(), payment.getAmount().toString());
        if (BigDecimal.TEN.equals(payment.getAmount())) {
            // THROW an exception so Resilience4j counts it as a failure
            throw new RuntimeException("Service Unavailable simulation");
        }

        if (BigDecimal.ONE.equals(payment.getAmount())) {
            throw new RuntimeException("Gateway Timeout simulation");
        }

        return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
    }

    // The fallback will catch the RuntimeException thrown above
    public ResponseEntity<String> paymentFallback(Payment payment, Throwable t) {
        return new ResponseEntity<>("Fallback: Service is down", HttpStatus.SERVICE_UNAVAILABLE);
    }
}
