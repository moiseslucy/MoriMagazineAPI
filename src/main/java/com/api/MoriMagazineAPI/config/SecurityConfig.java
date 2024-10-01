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
                .requestMatchers("/", "/css/**", "/js/**", "/imagens/**", "/favicon.ico").permitAll() // Acesso público à página inicial e recursos estáticos
                .requestMatchers("/login", "/register", "/forgot-password", "/reset-password").permitAll() // Acesso público para login, registro e recuperação de senha
                .anyRequest().authenticated() // Exige autenticação para outros endpoints
            )
            .formLogin(form -> form
                .loginPage("/login") // Página de login personalizada
                .defaultSuccessUrl("/", true) // Redireciona para a página inicial após login bem-sucedido
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout") // Redireciona após logout
                .permitAll()
            )
            .csrf(csrf -> csrf.disable()) // Desabilitar CSRF temporariamente para desenvolvimento
            .addFilterBefore(cspFilter(), UsernamePasswordAuthenticationFilter.class); // Adiciona o filtro CSP na cadeia de segurança

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Codificador de senha
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
                    "img-src 'self' data: https://cdn-icons-png.flaticon.com; " + // Permitir imagens inline e do mesmo domínio, além do Flaticon
                    "connect-src 'self'; " +
                    "frame-ancestors 'self'; " +
                    "base-uri 'self'; " +
                    "form-action 'self';");
                chain.doFilter(request, response);
            }

            @Override
            public void init(FilterConfig filterConfig) throws ServletException {
                // Inicialização do filtro, se necessário
            }

            @Override
            public void destroy() {
                // Limpeza do filtro, se necessário
            }
        };
    }
}
