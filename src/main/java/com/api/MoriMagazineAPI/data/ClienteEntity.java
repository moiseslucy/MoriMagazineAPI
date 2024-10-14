package com.api.MoriMagazineAPI.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor  // Lombok cria o construtor padrão sem argumentos
@AllArgsConstructor // Lombok cria o construtor com todos os argumentos
@Entity
@Table(name = "Cliente")
public class ClienteEntity {

   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;  // Alterado para Integer

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, message = "Informe ao menos 2 caracteres para o campo nome")
    private String nome;

    @NotBlank(message = "Endereço é obrigatório")
    private String endereco;

    @NotBlank(message = "CPF é obrigatório")
    private String cpf;

    @NotNull(message = "Data de Nascimento é obrigatória")
    private LocalDate dataNascimento;

    private String sexo;
    private String telefone;
}