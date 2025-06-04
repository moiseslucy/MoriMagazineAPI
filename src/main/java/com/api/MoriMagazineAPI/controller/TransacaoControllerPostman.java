package com.api.MoriMagazineAPI.controller;

import com.api.MoriMagazineAPI.data.TransacaoEntity;
import com.api.MoriMagazineAPI.service.TransacaoServicePostman;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transacoes")
public class TransacaoControllerPostman {

    @Autowired
    private TransacaoServicePostman transacaoServicePostman;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransacaoEntity> criarTransacao(@RequestBody TransacaoEntity transacao) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON); // Definir tipo de conteúdo para JSON

        try {
            if (transacao.getCliente() == null || transacao.getCliente().getId() == null) {
                return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
            }
            if (transacao.getDataTransacao() == null) {
                transacao.setDataTransacao(LocalDate.now());
            }
            if (transacao.getFormaPagamento() == null || transacao.getFormaPagamento().isEmpty()) {
                return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
            }
            Integer clienteId = transacao.getCliente().getId();
            TransacaoEntity novaTransacao = transacaoServicePostman.criarTransacao(transacao, clienteId);
            headers.setLocation(URI.create("/api/transacoes/" + novaTransacao.getId()));
            return new ResponseEntity<>(novaTransacao, headers, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TransacaoEntity>> listarTransacoes() {
        List<TransacaoEntity> transacoes = transacaoServicePostman.listarTodasTransacoes();
        return ResponseEntity.ok(transacoes);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransacaoEntity> getTransacaoById(@PathVariable Integer id) {
        return transacaoServicePostman.getTransacaoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransacaoEntity> atualizarTransacao(@PathVariable Integer id, @RequestBody TransacaoEntity transacao) {
        try {
            TransacaoEntity transacaoAtualizada = transacaoServicePostman.atualizarTransacao(id, transacao);
            return ResponseEntity.ok(transacaoAtualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deletarTransacao(@PathVariable Integer id) {
        transacaoServicePostman.deletarTransacao(id);
        return ResponseEntity.noContent().build();
    }
}