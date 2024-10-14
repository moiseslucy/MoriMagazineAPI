package com.api.MoriMagazineAPI.data;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<ClienteEntity, Integer> {

    public List<ClienteEntity> findByCpf(String cpf);
    // Métodos de pesquisa específicos, se necessário

    public List<ClienteEntity> findByNomeContaining(String nome);

    public Optional<ClienteEntity> findById(Long clienteId);
// Método para pesquisar clientes pelo nome (ou parte dele)
    List<ClienteEntity> findByNomeContainingIgnoreCase(String nome);

    public boolean existsById(Long clienteId);
}

