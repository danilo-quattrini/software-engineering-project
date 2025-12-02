package it.unicam.cs.ids2425.payment;

import dto.DTOInterface;
import it.unicam.cs.ids2425.location.LocationDTO;
import it.unicam.cs.ids2425.payment.referable.Referable;
import it.unicam.cs.ids2425.users.User;
import it.unicam.cs.ids2425.users.UserDTO;
import it.unicam.cs.ids2425.users.roles.Admin;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class PaymentDTO implements DTOInterface<Payment, PaymentDTO> {
    private UserDTO payer;
    private Referable reference;
    private LocationDTO location;
    private boolean processed;

    @Override
    public PaymentDTO toDTO(Payment entity) {
        PaymentDTO dto = new PaymentDTO();
        dto.setPayer(new UserDTO().toDTO(entity.getPayer()));
        dto.setReference(entity.getReference());
        dto.setLocation(location.toDTO(entity.getLocation()));
        dto.setProcessed(processed);
        return dto;
    }

    @Override
    public Payment fromDTO() {
        User payerEntity;

        if ("ADMIN".equalsIgnoreCase(this.payer.getRole())) {
            payerEntity = Admin.builder()
                    .name(this.payer.getName())
                    .email(this.payer.getEmail())
                    .password(this.payer.getPassword())
                    .build();
        } else {
            payerEntity = this.payer.fromDTO();
        }

        return Payment.builder()
                .payer(payerEntity)
                .reference(this.reference)
                .location(this.location != null ? this.location.fromDTO() : null)
                .processed(this.processed)
                .build();
    }
}
