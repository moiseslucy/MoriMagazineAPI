package com.api.MoriMagazineAPI.controller;
import com.api.MoriMagazineAPI.model.Preferencia;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class SiteController {

    @GetMapping("/preferencias")
    public String preferencias() {
        return "preferencias";
    }

    @PostMapping("/preferencias")
    public ModelAndView gravaPreferencias(@ModelAttribute Preferencia pref, HttpServletResponse response) {
        Cookie cookiePrefNome = new Cookie("pref-nome", pref.getNome());
        cookiePrefNome.setDomain("localhost");
        cookiePrefNome.setHttpOnly(true);
        cookiePrefNome.setMaxAge(86400);
        response.addCookie(cookiePrefNome);
        
        Cookie cookiePrefEstilo = new Cookie("pref-estilo", pref.getEstilo());
        cookiePrefEstilo.setDomain("localhost");
        cookiePrefEstilo.setHttpOnly(true);
        cookiePrefEstilo.setMaxAge(86400);
        response.addCookie(cookiePrefEstilo);
        
        return new ModelAndView("redirect:/");
    }

    @GetMapping("/site")
    public String index(@CookieValue(name="pref-nome", defaultValue="") String nome,
                        @CookieValue(name="pref-estilo", defaultValue="claro") String tema,
                        Model model) {
        model.addAttribute("nome", nome);
        model.addAttribute("css", tema);
        return "index";
    }
}
