package com.api.MoriMagazineAPI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
public class MoriMagazineApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoriMagazineApiApplication.class, args);
	}

@Controller
    public static class HomeController {

        @GetMapping("/")
        public String paginaInicial() {
            return "pagina_inicial";
        }
    }
}
