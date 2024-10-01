package com.api.MoriMagazineAPI.controller;

import com.api.MoriMagazineAPI.model.Preferencia;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PreferenciaController {

    @GetMapping("/configuracoes/preferencias")
    public String mostrarFormulario(Model model) {
        return "preferencias"; // O nome da página HTML
    }

    @PostMapping("/configuracoes/preferencias")
    public String salvarPreferencias(@RequestParam("estilo") String estilo, Model model) {
        // Aqui você pode salvar a preferência do usuário, por exemplo, na sessão ou banco de dados
        Preferencia preferencia = new Preferencia();
        preferencia.setEstilo(estilo);
        model.addAttribute("preferencia", preferencia);

        // Redirecionar ou retornar uma página de confirmação
        return "redirect:/";
    }
}
