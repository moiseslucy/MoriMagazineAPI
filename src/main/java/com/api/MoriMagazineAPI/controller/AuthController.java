package com.api.MoriMagazineAPI.controller;

import com.api.MoriMagazineAPI.data.UserEntity;
import com.api.MoriMagazineAPI.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // Certifique-se de que login.html está no diretório templates
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserEntity());
        return "register"; // Certifique-se de que register.html está no diretório templates
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserEntity user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register"; // Retorna para a página de registro se houver erros de validação
        }
        try {
            userService.save(user); // Salva o usuário no banco de dados
            return "redirect:/"; // Redireciona para a página inicial após registro bem-sucedido
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Ocorreu um erro durante o registro. Tente novamente.");
            e.printStackTrace(); // Para desenvolvimento, imprima o stack trace
            return "register"; // Retorna para a página de registro em caso de exceção
        }
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password"; // Certifique-se de que forgot-password.html está no diretório templates
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        UserEntity user = userService.findByEmail(email);
        if (user != null) {
            try {
                userService.createPasswordResetToken(user);
                userService.sendPasswordResetEmail(user); // Envia e-mail com link de redefinição de senha
                model.addAttribute("message", "Link de recuperação de senha enviado para o seu e-mail.");
            } catch (Exception e) {
                model.addAttribute("errorMessage", "Erro ao enviar o e-mail de redefinição de senha. Tente novamente.");
            }
        } else {
            model.addAttribute("errorMessage", "Email não encontrado. Tente novamente.");
        }
        return "forgot-password"; // Exibe uma mensagem de sucesso ou erro na mesma página
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        if (userService.validatePasswordResetToken(token)) {
            model.addAttribute("token", token);
            return "reset-password"; // Certifique-se de que reset-password.html está no diretório templates
        } else {
            model.addAttribute("errorMessage", "Token de redefinição de senha inválido ou expirado.");
            return "forgot-password"; // Exibe erro na página de recuperação de senha
        }
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token, 
                                       @RequestParam("password") String password, Model model) {
        UserEntity user = userService.findByResetToken(token);
        if (user != null) {
            try {
                userService.updatePassword(user, password); // Hash da senha deve ser feito no UserService
                model.addAttribute("message", "Senha redefinida com sucesso. Faça login com sua nova senha.");
                return "redirect:/"; // Redireciona para a página inicial após redefinição de senha
            } catch (Exception e) {
                model.addAttribute("errorMessage", "Erro ao redefinir a senha. Tente novamente.");
                e.printStackTrace(); // Para desenvolvimento, imprima o stack trace
                return "reset-password"; // Retorna à página de redefinição de senha com erro
            }
        } else {
            model.addAttribute("errorMessage", "Token de redefinição de senha inválido ou expirado.");
            return "reset-password"; // Retorna à página de redefinição de senha com erro
        }
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Model model, Exception e) {
        model.addAttribute("error", e.getMessage());
        return "error"; // Certifique-se de ter uma página de erro genérica
    }
}
