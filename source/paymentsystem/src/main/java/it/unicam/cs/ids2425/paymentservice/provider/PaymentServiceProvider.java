package it.unicam.cs.ids2425.paymentservice.provider;


import it.unicam.cs.ids2425.payment.PaymentDTO;

public interface PaymentServiceProvider {
    String getName();
    boolean processPayment(PaymentDTO dto);
}
