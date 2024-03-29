package com.api.MoriMagazineAPI.service;

import com.api.MoriMagazineAPI.data.ClienteEntity;
import com.api.MoriMagazineAPI.data.ClienteRepository;

import com.api.MoriMagazineAPI.exeception.ResourceNotFoundException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {
    @Autowired
    ClienteRepository clienteRepository;

    public ClienteEntity criarCliente(ClienteEntity cliente) {
        cliente.setId(null);
        clienteRepository.save(cliente);
        return cliente;
    }

    public ClienteEntity atualizarCliente(Integer clienteId, ClienteEntity clienteRequest) {
        ClienteEntity cliente = getClienteId(clienteId);
        cliente.setNome(clienteRequest.getNome());
        cliente.setEndereco(clienteRequest.getEndereco());
        cliente.setCpf(clienteRequest.getCpf());
        cliente.setDataNascimento(clienteRequest.getDataNascimento());
        cliente.setSexo(clienteRequest.getSexo());
        cliente.setTelefone(clienteRequest.getTelefone());
        clienteRepository.save(cliente);
        return cliente;
    }

    public ClienteEntity getClienteId(Integer clienteId) {
        return clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado: " + clienteId));
    }

    public List<ClienteEntity> listarTodosClientes() {
        return clienteRepository.findAll();
    }

    public void deletarCliente(Integer clienteId) {
        ClienteEntity cliente = getClienteId(clienteId);
        clienteRepository.deleteById(cliente.getId());
    }

    

    public List<ClienteEntity> getClientePorCPF(String cpf) {
   List<ClienteEntity> clientes = clienteRepository.findByCpf(cpf);

        if (clientes.isEmpty()) {
            throw new ResourceNotFoundException("Nenhum cliente encontrado para o CPF: " + cpf);
        }

        return clientes;      
    }

    public List<ClienteEntity> getClientePorNome(String nome) {
List<ClienteEntity> clientes = clienteRepository.findByNomeContaining(nome);
        if (clientes.isEmpty()) {
            throw new ResourceNotFoundException("Nenhum cliente encontrado com o nome: " + nome);
        }
        return clientes;
    }
}