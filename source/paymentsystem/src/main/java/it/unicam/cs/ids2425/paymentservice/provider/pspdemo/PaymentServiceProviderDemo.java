package it.unicam.cs.ids2425.paymentservice.provider.pspdemo;

import it.unicam.cs.ids2425.payment.PaymentDTO;
import it.unicam.cs.ids2425.paymentservice.provider.PaymentServiceProvider;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceProviderDemo implements PaymentServiceProvider {

    @Override
    public String getName() {
        return "pspdemo";
    }

    @Override
    public boolean processPayment(PaymentDTO dto) {
        System.out.println("Processing demo payment: " + dto.getReference().getAmount() + " " + dto.getReference().getCurrencyCode());
        return true;
    }
}
