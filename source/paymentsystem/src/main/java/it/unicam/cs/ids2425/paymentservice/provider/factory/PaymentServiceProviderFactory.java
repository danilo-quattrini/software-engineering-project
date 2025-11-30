package it.unicam.cs.ids2425.paymentservice.provider.factory;

import it.unicam.cs.ids2425.paymentservice.provider.PaymentServiceProvider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PaymentServiceProviderFactory {
    private final Map<String, PaymentServiceProvider> providers;

    public PaymentServiceProviderFactory(List<PaymentServiceProvider> providerList) {
        this.providers = providerList.stream()
                .collect(Collectors.toMap(PaymentServiceProvider::getName, p -> p));
        System.out.println("Registered PSP: " + providers.keySet());
    }

    public PaymentServiceProvider getProvider(String name) {
        return providers.get(name);
    }
}
