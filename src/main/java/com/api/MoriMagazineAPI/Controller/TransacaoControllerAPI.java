/*package com.api.MoriMagazineAPI.controller;

import com.api.MoriMagazineAPI.data.TransacaoEntity;
import com.api.MoriMagazineAPI.service.TransacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/transacoes")
public class TransacaoControllerAPI {

    @Autowired
    private TransacaoService transacaoService;

 @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<TransacaoEntity> criarTransacao(@RequestBody TransacaoEntity transacao) {
  // ... rest of the method logic


        if (transacao.getCliente() == null || transacao.getCliente().getId() == null) {
            return ResponseEntity.badRequest().build(); 
        }

        Long clienteId = transacao.getCliente().getId().longValue();
        TransacaoEntity novaTransacao = transacaoService.criarTransacao(transacao, clienteId);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/transacoes/" + novaTransacao.getId()));
        return new ResponseEntity<>(novaTransacao, headers, HttpStatus.CREATED);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TransacaoEntity>> listarTransacoes() {
        List<TransacaoEntity> transacoes = transacaoService.listarTodasTransacoes();
        return ResponseEntity.ok(transacoes);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransacaoEntity> getTransacaoById(@PathVariable Long id) {
        return transacaoService.getTransacaoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransacaoEntity> atualizarTransacao(@PathVariable Long id, @RequestBody TransacaoEntity transacao) {
        try {
            TransacaoEntity transacaoAtualizada = transacaoService.atualizarTransacao(id, transacao);
            return ResponseEntity.ok(transacaoAtualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deletarTransacao(@PathVariable Long id) {
        transacaoService.deletarTransacao(id);
        return ResponseEntity.noContent().build();
    }
}
*/