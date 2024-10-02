package com.api.MoriMagazineAPI.controller;

import com.api.MoriMagazineAPI.data.ClienteEntity;
import com.api.MoriMagazineAPI.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Controller
@RequestMapping("/clientes")
public class ClienteControllerWeb {

    @Autowired
    ClienteService clienteService;

   @GetMapping("/listar")
public String viewClientes(Model model) {
    model.addAttribute("listarClientes", clienteService.listarTodosClientes());
    return "index";
}

   @GetMapping("/deletar/{id}")
public String mostrarFormularioExclusaoCliente(@PathVariable(value = "id") Integer id, Model model) {
    ClienteEntity cliente = clienteService.getClienteId(id);
    if (cliente == null) {
        return "redirect:/clientes/listar";
    }
    model.addAttribute("cliente", cliente);
    return "ConfirmarExclusaoCliente";
}

@PostMapping("/deletar/{id}")
public String deletarCliente(@PathVariable(value = "id") Integer id) {
    clienteService.deletarCliente(id);
    return "redirect:/clientes/listar";
}


    @GetMapping("/criarClienteForm")
    public String criarClienteForm(Model model) {
        ClienteEntity cliente = new ClienteEntity();
        model.addAttribute("cliente", cliente);
        return "inserir";  // Alterado para "clientes/inserir"
    }

  @PostMapping("/salvarCliente")
public String salvarCliente(@Valid @ModelAttribute("cliente") ClienteEntity cliente, BindingResult result) {
    if (result.hasErrors()) {
        return "inserir";
    }
    if (cliente.getId() == null) {
        clienteService.criarCliente(cliente);
    } else {
        clienteService.atualizarCliente(cliente.getId(), cliente);
    }
    return "redirect:/clientes/listar";
}


    @GetMapping("/atualizarForm/{id}")
    public String atualizarClienteForm(@PathVariable(value = "id") Integer id, Model model) {
        ClienteEntity cliente = clienteService.getClienteId(id);
        model.addAttribute("cliente", cliente);
        return "atualizar";
    }

   @PostMapping("/atualizar")
public String atualizarCliente(@Valid @ModelAttribute("cliente") ClienteEntity cliente, BindingResult result) {
    if (result.hasErrors()) {
        return "atualizar";
    }
    clienteService.atualizarCliente(cliente.getId(), cliente);
    return "redirect:/clientes/listar";
}
 
    
    
    @GetMapping("/pesquisar-por-nome/{nome}")
    public ResponseEntity<List<ClienteEntity>> getPesquisarPorNomeClientes(@PathVariable String nome) {
        List<ClienteEntity> clientes = clienteService.getClientePorNome(nome);
        return new ResponseEntity<>(clientes, HttpStatus.OK);
    }


}