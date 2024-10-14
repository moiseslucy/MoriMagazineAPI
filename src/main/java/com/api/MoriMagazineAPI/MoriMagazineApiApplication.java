package com.api.MoriMagazineAPI;

import com.api.MoriMagazineAPI.service.EnvironmentVariableService;
import com.api.MoriMagazineAPI.service.PreferenciaService;
import com.api.MoriMagazineAPI.model.Preferencia;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@SpringBootApplication
public class MoriMagazineApiApplication {

    @Autowired
    private EnvironmentVariableService environmentVariableService;

    public static void main(String[] args) {
        SpringApplication.run(MoriMagazineApiApplication.class, args);
    }

    // Método que será executado logo após a inicialização do contexto Spring
    @PostConstruct
    public void init() {
        environmentVariableService.loadSecretsIntoEnv();
    }

    @Controller
    public static class HomeController {

        @Autowired
        private PreferenciaService preferenciaService;

        @GetMapping("/")
        public String paginaInicial(Model model) {
            // Obtém a preferência atual do serviço de preferências
            Preferencia preferencia = preferenciaService.obterPreferenciaAtual();
            model.addAttribute("preferencia", preferencia);

            // Obtém o nome do usuário autenticado, se houver
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String)) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                model.addAttribute("remoteUser", userDetails.getUsername());
            } else {
                model.addAttribute("remoteUser", null);  // Não exibe "anonymousUser"
            }

            return "pagina_inicial"; // Retorna o nome da página inicial para exibição
        }

        @GetMapping("/preferencia/escolher")
        public String escolherEstilo(@RequestParam("estilo") String estilo) {
            // Obtém a preferência atual do serviço de preferências
            Preferencia preferencia = preferenciaService.obterPreferenciaAtual();

            // Define o novo estilo de acordo com o parâmetro recebido
            preferencia.setEstilo(estilo);

            // Salva a preferência atualizada
            preferenciaService.salvarPreferencia(preferencia);

            return "redirect:/"; // Redireciona para a página inicial com a nova preferência
        }
    }
}
