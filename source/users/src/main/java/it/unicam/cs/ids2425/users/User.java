package it.unicam.cs.ids2425.users;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User {
    @Id
    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_sequence"
    )
    private Long id;
    private String name;
    private String email;
    private String password;

    public String getRole() {
        return "ROLE_" + getSimpleRole();
    }

    public String getSimpleRole() {
        return this.getClass().getSimpleName().toUpperCase();
    }
}
