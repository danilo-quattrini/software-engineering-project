package it.unicam.cs.ids2425.events.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InviteByEmail {

    @NotBlank
    @Email
    private String email;
}
