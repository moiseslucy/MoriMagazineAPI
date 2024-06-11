package com.api.MoriMagazineAPI.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "Cliente")
public class ClienteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Size(min = 2, message = "Informe ao menos 2 caracteres para o campo nome")
    private String nome;

    @NotBlank(message = "Endereço obrigatório")
    private String endereco;

    @NotBlank(message = "CPF obrigatório")
    private String cpf;

    @NotNull(message = "Data de Nascimento obrigatória")
   private LocalDate dataNascimento;



    private String sexo; // Alterado para String

    private String telefone;
}
