package com.api.MoriMagazineAPI.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")  // Garante que o email seja único
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome de usuário é obrigatório")
    @Column(nullable = false, unique = true)  // Garante que o nome de usuário seja único e não nulo
    private String username;

    @NotBlank(message = "A senha é obrigatória")
    @Column(nullable = false)  // Garante que a senha não seja nula
    private String password;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "O email deve ser válido")
    @Column(nullable = false, unique = true)  // Garante que o email seja único e não nulo
    private String email;  // Para recuperação de senha

    @Column(nullable = false)  // Garante que o papel não seja nulo
    private String role = "USER";  // Define um valor padrão para o papel

    @Column(name = "reset_token")
    private String resetToken;  // Token para recuperação de senha

    @Column(name = "token_expiry_date")
    private Date tokenExpiryDate;  // Data de expiração do token de redefinição de senha
}
