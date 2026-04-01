package com.design.circutebreaker.controller;

import com.design.circutebreaker.model.Payment;
import com.design.circutebreaker.service.PayInitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {

    private PayInitService payInitService;

    @Autowired
    public PaymentController(PayInitService payInitService) {
        this.payInitService = payInitService;
    }


    @PostMapping("/payInit")
    public ResponseEntity<String> payInit(@RequestBody Payment payment) {
        return payInitService.handlePayment(payment);
    }

    public ResponseEntity<String> paymentFallback(Throwable throwable) {
        return ResponseEntity.badRequest().build();
    }

    @RequestMapping("/fallback")
    public ResponseEntity<String> fallback() {
        return new ResponseEntity<>("Service is temporarily unavailable. CB is Open, Please try again later.", HttpStatus.SERVICE_UNAVAILABLE);
    }
}
