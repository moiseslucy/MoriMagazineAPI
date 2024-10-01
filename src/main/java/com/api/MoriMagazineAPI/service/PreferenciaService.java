package com.api.MoriMagazineAPI.service;

import com.api.MoriMagazineAPI.model.Preferencia;
import org.springframework.stereotype.Service;

@Service
public class PreferenciaService {

    private Preferencia preferencia = new Preferencia();  // Simulação de armazenamento de preferência

    public Preferencia obterPreferenciaAtual() {
        if (preferencia.getEstilo() == null) {
            preferencia.setEstilo("claro");  // Define o padrão como "claro"
        }
        return preferencia;
    }

    public void salvarPreferencia(Preferencia preferencia) {
        this.preferencia = preferencia;
    }
}
