package com.api.MoriMagazineAPI.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // Permitir acesso público a páginas e recursos públicos específicos
                .requestMatchers("/", "/css/**", "/js/**", "/imagens/**", "/favicon.ico", "/error", "/login", "/register", "/forgot-password", "/reset-password").permitAll()

                // Permitir todas as requisições para endpoints de API (Postman) sem autenticação
                .requestMatchers("/api/**", "/produto/api/**", "/cliente/api/**", "/transacao/api/**").permitAll()

                // Exigir autenticação para todos os outros endpoints web
                .anyRequest().authenticated()
            )
            // Configuração para Basic Auth e formLogin
            .httpBasic(httpBasic -> httpBasic.realmName("API Realm"))
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            // Desativar CSRF globalmente para facilitar o acesso via API
            .csrf(csrf -> csrf.disable())
            .addFilterBefore(cspFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public Filter cspFilter() {
        return new Filter() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
                HttpServletResponse httpServletResponse = (HttpServletResponse) response;
                httpServletResponse.setHeader("Content-Security-Policy", 
                    "default-src 'self'; " +
                    "style-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://fonts.googleapis.com https://stackpath.bootstrapcdn.com https://cdnjs.cloudflare.com; " +
                    "script-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://code.jquery.com https://cdnjs.cloudflare.com https://stackpath.bootstrapcdn.com; " +
                    "font-src 'self' https://fonts.gstatic.com https://cdnjs.cloudflare.com; " +
                    "img-src 'self' data: https://cdn-icons-png.flaticon.com; " +
                    "connect-src 'self'; " +
                    "frame-ancestors 'self'; " +
                    "base-uri 'self'; " +
                    "form-action 'self';");
                chain.doFilter(request, response);
            }

            @Override
            public void init(FilterConfig filterConfig) throws ServletException {}

            @Override
            public void destroy() {}
        };
    }
}