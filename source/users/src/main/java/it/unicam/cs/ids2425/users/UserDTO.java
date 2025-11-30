package it.unicam.cs.ids2425.users;

import dto.DTOInterface;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import it.unicam.cs.ids2425.users.roles.UserRole;

@NoArgsConstructor
@Getter
@Setter
public class UserDTO implements DTOInterface<User, UserDTO> {
    private String role;
    private String name;
    private String email;
    private String password;

    @Override
    public UserDTO toDTO(User entity) {
        UserDTO dto = new UserDTO();
        dto.setRole(entity.getSimpleRole());
        dto.setName(entity.getName());
        dto.setEmail(entity.getEmail());
        dto.setPassword(entity.getPassword());
        return dto;
    }

    @Override
    public User fromDTO() {
        return UserRole.valueOf(this.role.toUpperCase())
                .getBuilder()
                .get()
                .name(this.name)
                .email(this.email)
                .password(this.password)
                .build();
    }
}
