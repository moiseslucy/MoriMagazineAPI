package com.api.MoriMagazineAPI.service;

import com.api.MoriMagazineAPI.data.ClienteEntity;
import com.api.MoriMagazineAPI.data.ClienteRepository;
import com.api.MoriMagazineAPI.exeception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;

@Service
public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;

    public ClienteEntity criarCliente(ClienteEntity cliente) {
        cliente.setId(null);
        return clienteRepository.save(cliente);
    }

    public ClienteEntity atualizarCliente(Integer clienteId, ClienteEntity clienteRequest) {
        ClienteEntity cliente = getClienteById(clienteId);
        cliente.setNome(clienteRequest.getNome());
        cliente.setEndereco(clienteRequest.getEndereco());
        cliente.setCpf(clienteRequest.getCpf());
        cliente.setDataNascimento(clienteRequest.getDataNascimento());
        cliente.setSexo(clienteRequest.getSexo());
        cliente.setTelefone(clienteRequest.getTelefone());
        return clienteRepository.save(cliente);
    }

    public ClienteEntity getClienteById(Integer clienteId) {
        return clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado: " + clienteId));
    }

    public List<ClienteEntity> listarTodosClientes() {
        return clienteRepository.findAll();
    }

    public void deletarCliente(Integer clienteId) {
        ClienteEntity cliente = getClienteById(clienteId);
        clienteRepository.deleteById(cliente.getId());
    }

    public List<ClienteEntity> criarClientes(List<ClienteEntity> clientes) {
        clientes.forEach(cliente -> cliente.setId(null)); // Configura o ID como null para criação de novos clientes
        return clienteRepository.saveAll(clientes);
    }

    public List<ClienteEntity> getClientePorCPF(String cpf) {
        List<ClienteEntity> clientes = clienteRepository.findByCpf(cpf);
        if (clientes.isEmpty()) {
            throw new ResourceNotFoundException("Nenhum cliente encontrado para o CPF: " + cpf);
        }
        return clientes;
    }

    public List<ClienteEntity> getClientePorNome(String nome) {
        List<ClienteEntity> clientes = clienteRepository.findByNomeContainingIgnoreCase(nome);
        if (clientes.isEmpty()) {
            throw new ResourceNotFoundException("Nenhum cliente encontrado com o nome: " + nome);
        }
        return clientes;
    }

    public List<ClienteEntity> pesquisarClientes(String termo) {
        if (isNumeric(termo)) {
            Integer id = Integer.valueOf(termo);
            return clienteRepository.findById(id)
                    .map(Collections::singletonList)
                    .orElse(Collections.emptyList());
        } else {
            return clienteRepository.findByNomeContainingIgnoreCase(termo);
        }
    }

    private boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        try {
            Integer.parseInt(str); // Convertendo para Integer
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}