package com.api.MoriMagazineAPI.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    
    /**
     * Encontra um usuário pelo nome de usuário.
     *
     * @param username O nome de usuário a ser pesquisado.
     * @return A entidade do usuário se encontrada, caso contrário, null.
     */
    UserEntity findByUsername(String username);
    
    /**
     * Encontra um usuário pelo e-mail.
     *
     * @param email O e-mail a ser pesquisado.
     * @return A entidade do usuário se encontrada, caso contrário, null.
     */
    UserEntity findByEmail(String email);
    
    /**
     * Encontra um usuário pelo token de redefinição de senha.
     *
     * @param resetToken O token de redefinição de senha a ser pesquisado.
     * @return A entidade do usuário se encontrada, caso contrário, null.
     */
    UserEntity findByResetToken(String resetToken);
}
