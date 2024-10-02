package com.api.MoriMagazineAPI.controller;

import com.api.MoriMagazineAPI.data.ClienteEntity;
import com.api.MoriMagazineAPI.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/morimagazine/api/clientes")
public class ClienteController {

    @Autowired
    ClienteService clienteService;

    @GetMapping("/listar")
    public ResponseEntity<List<ClienteEntity>> getAllClientes() {
        List<ClienteEntity> clientes = clienteService.listarTodosClientes();
        return new ResponseEntity<>(clientes, HttpStatus.OK);
    }

    @GetMapping("/pesquisar/{id}")
    public ResponseEntity<ClienteEntity> getClienteById(@PathVariable Integer id) {
        ClienteEntity cliente = clienteService.getClienteId(id);
        return new ResponseEntity<>(cliente, HttpStatus.OK);
    }

    @GetMapping("/pesquisar-cpf/{cpf}")
    public ResponseEntity<List<ClienteEntity>> getPesquisarPorCPFCli(@PathVariable String cpf) {
        List<ClienteEntity> clientes = clienteService.getClientePorCPF(cpf);
        return new ResponseEntity<>(clientes, HttpStatus.OK);
    }

    @PostMapping("/adicionar")
    public ResponseEntity<ClienteEntity> addCliente(@Valid @RequestBody ClienteEntity cliente) {
        var novoCliente = clienteService.criarCliente(cliente);
        return new ResponseEntity<>(novoCliente, HttpStatus.CREATED);
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<ClienteEntity> atualizarCliente(@PathVariable Integer id, @RequestBody ClienteEntity cliente) {
        var clienteAtualizado = clienteService.atualizarCliente(id, cliente);
        return new ResponseEntity<>(clienteAtualizado, HttpStatus.OK);
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity deletarCliente(@PathVariable Integer id) {
        clienteService.deletarCliente(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/pesquisar-nome/{nome}")
    public ResponseEntity<List<ClienteEntity>> getPesquisarPorNomeClientes(@PathVariable String nome) {
        List<ClienteEntity> clientes = clienteService.getClientePorNome(nome);
        return new ResponseEntity<>(clientes, HttpStatus.OK);
    }
}