package com.api.MoriMagazineAPI.service;

import com.api.MoriMagazineAPI.data.UserEntity;
import com.api.MoriMagazineAPI.data.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

import java.util.Date;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SesClient sesClient;

    // Variável configurável para o remetente de email com valor padrão
    @Value("${spring.mail.from:default_email@example.com}")
    private String emailFrom;

    /**
     * Encontra um usuário pelo email.
     *
     * @param email O email do usuário.
     * @return A entidade do usuário se encontrada, caso contrário, null.
     */
    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Cria um token de redefinição de senha para o usuário.
     *
     * @param user A entidade do usuário.
     */
    public void createPasswordResetToken(UserEntity user) {
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);  // Define o token na entidade do usuário
        user.setTokenExpiryDate(new Date(System.currentTimeMillis() + 15 * 60 * 1000));  // 15 minutos de validade
        userRepository.save(user);  // Salva o usuário com o novo token
    }

    /**
     * Envia um e-mail com o link de redefinição de senha.
     *
     * @param user A entidade do usuário.
     */
    public void sendPasswordResetEmail(UserEntity user) {
        String resetUrl = "http://localhost:8080/reset-password?token=" + user.getResetToken();
        String userEmail = user.getEmail();

        // Verifica se o email está verificado no AWS SES
        if (!isEmailVerified(userEmail)) {
            verifyEmail(userEmail);
            logger.info("Solicitação de verificação enviada para {}", userEmail);
            throw new IllegalStateException("O email não está verificado. Verifique seu email e tente novamente.");
        }

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(userEmail);
        mailMessage.setFrom(emailFrom);  // Usando o email configurado com valor padrão
        mailMessage.setSubject("Redefinição de Senha - Mori Magazine");
        mailMessage.setText("Para redefinir sua senha, clique no link abaixo:\n" + resetUrl + "\n\nEste link expirará em 15 minutos.");

        try {
            mailSender.send(mailMessage);
            logger.info("Email de redefinição de senha enviado para {}", userEmail);
        } catch (Exception e) {
            logger.error("Erro ao enviar email de redefinição de senha: {}", e.getMessage(), e);
            // Lidar com exceções de envio de email
        }
    }

    /**
     * Verifica se um email está verificado no AWS SES.
     *
     * @param email O email a ser verificado.
     * @return true se o email estiver verificado, caso contrário, false.
     */
    private boolean isEmailVerified(String email) {
        ListIdentitiesRequest request = ListIdentitiesRequest.builder()
                .identityType(IdentityType.EMAIL_ADDRESS)
                .build();
        ListIdentitiesResponse response = sesClient.listIdentities(request);
        return response.identities().contains(email);
    }

    /**
     * Solicita a verificação de um email no AWS SES.
     *
     * @param email O email a ser verificado.
     */
    private void verifyEmail(String email) {
        VerifyEmailIdentityRequest request = VerifyEmailIdentityRequest.builder()
                .emailAddress(email)
                .build();
        sesClient.verifyEmailIdentity(request);
    }

    /**
     * Valida o token de redefinição de senha.
     *
     * @param token O token de redefinição de senha.
     * @return true se o token for válido, caso contrário, false.
     */
    public boolean validatePasswordResetToken(String token) {
        UserEntity user = userRepository.findByResetToken(token);
        if (user == null) {
            return false;
        }
        // Verificar se o token está expirado
        if (user.getTokenExpiryDate().before(new Date())) {
            return false;
        }
        return true;
    }

    /**
     * Atualiza a senha do usuário e limpa o token de redefinição de senha.
     *
     * @param user A entidade do usuário.
     * @param newPassword A nova senha do usuário.
     */
    public void updatePassword(UserEntity user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null); // Limpar o token após uso
        user.setTokenExpiryDate(null); // Limpar a data de expiração do token
        userRepository.save(user); // Salva a nova senha no banco de dados
    }

    /**
     * Salva um novo usuário ou atualiza um usuário existente, garantindo que as senhas sejam codificadas.
     *
     * @param user A entidade do usuário.
     */
    public void save(UserEntity user) {
        // Verificar se o email já está em uso
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("Email já registrado.");
        }

        // Verificar se o nome de usuário já está em uso
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("Nome de usuário já registrado.");
        }

        // Definindo um papel padrão se não for fornecido
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");  // Define um papel padrão
        }

        // Codificar a senha
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Salvar o usuário no banco de dados
        userRepository.save(user);
    }

    /**
     * Encontra um usuário pelo token de redefinição de senha.
     *
     * @param resetToken O token de redefinição de senha.
     * @return A entidade do usuário se encontrada, caso contrário, null.
     */
    public UserEntity findByResetToken(String resetToken) {
        return userRepository.findByResetToken(resetToken);
    }
}
