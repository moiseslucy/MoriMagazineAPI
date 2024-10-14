package com.api.MoriMagazineAPI.service;

import com.api.MoriMagazineAPI.data.ParcelaEntity;
import com.api.MoriMagazineAPI.data.ParcelaRepository;
import com.api.MoriMagazineAPI.data.StatusParcela;
import com.api.MoriMagazineAPI.data.TransacaoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ParcelaService {

    private final ParcelaRepository parcelaRepository;

    @Autowired
    public ParcelaService(ParcelaRepository parcelaRepository) {
        this.parcelaRepository = parcelaRepository;
    }

    /**
     * Encontra uma parcela pelo seu ID.
     *
     * @param id ID da parcela.
     * @return Um Optional contendo a parcela encontrada, ou vazio se não encontrada.
     */
    public Optional<ParcelaEntity> findById(Integer id) { // Alterado para Integer
        return parcelaRepository.findById(id);
    }

    /**
     * Salva uma entidade de parcela no banco de dados.
     *
     * @param parcela A entidade de parcela a ser salva.
     * @return A entidade de parcela salva.
     */
    public ParcelaEntity save(ParcelaEntity parcela) {
        return parcelaRepository.save(parcela);
    }

    /**
     * Lista todas as parcelas atrasadas com base na data atual.
     *
     * @return Uma lista de entidades de parcelas atrasadas.
     */
    public List<ParcelaEntity> listarParcelasAtrasadas() {
        return parcelaRepository.findAtrasadas(LocalDate.now());
    }

    /**
     * Baixa (marca como paga) uma parcela identificada pelo seu ID.
     *
     * @param parcelaId O ID da parcela a ser baixada.
     */
    public void baixarParcela(Integer parcelaId) { // Alterado para Integer
        ParcelaEntity parcela = parcelaRepository.findById(parcelaId)
                .orElseThrow(() -> new RuntimeException("Parcela não encontrada"));
        
        parcela.setStatus(StatusParcela.PAGO);
        parcelaRepository.save(parcela);
        parcelaRepository.flush(); // Forçar a persistência imediata da mudança
    }

    /**
     * Envia um comprovante de pagamento para uma parcela específica.
     *
     * @param parcelaId O ID da parcela para a qual o comprovante será enviado.
     */
    public void enviarComprovantePagamento(Integer parcelaId) { // Alterado para Integer
        ParcelaEntity parcela = parcelaRepository.findById(parcelaId)
                .orElseThrow(() -> new RuntimeException("Parcela não encontrada"));

        if (parcela.getStatus() != StatusParcela.PAGO) {
            throw new IllegalStateException("Não é possível enviar o comprovante para parcelas não baixadas.");
        }

        String mensagem = criarMensagemComprovante(parcela.getTransacao());
        String telefoneCliente = parcela.getTransacao().getCliente().getTelefone();
        enviarMensagemWhatsApp(telefoneCliente, mensagem);
    }

    /**
     * Cria uma mensagem detalhada para o comprovante de pagamento com base na transação.
     *
     * @param transacao A entidade de transação associada à parcela.
     * @return Uma string contendo a mensagem detalhada do comprovante.
     */
    private String criarMensagemComprovante(TransacaoEntity transacao) {
        StringBuilder mensagem = new StringBuilder();
        mensagem.append("Detalhes da Compra\n");
        mensagem.append("Data da Compra: ").append(transacao.getDataTransacao()).append("\n");
        mensagem.append("Cliente: ").append(transacao.getCliente().getNome()).append("\n\n");
        mensagem.append("Parcelas:\n");
        for (ParcelaEntity parcela : transacao.getParcelas()) {
            mensagem.append("Data de Vencimento: ").append(parcela.getDataVencimento())
                    .append(" - Valor: R$ ").append(parcela.getValorParcela())
                    .append(" - Status: ").append(parcela.getStatus())
                    .append("\n");
        }
        mensagem.append("\nObrigado pela compra! Se houver alguma dúvida, entre em contato.");
        return mensagem.toString();
    }

    /**
     * Envia uma mensagem por WhatsApp para o cliente.
     *
     * @param telefoneCliente O número de telefone do cliente.
     * @param mensagem A mensagem a ser enviada.
     */
    private void enviarMensagemWhatsApp(String telefoneCliente, String mensagem) {
        String url = "https://wa.me/" + telefoneCliente + "?text=" + java.net.URLEncoder.encode(mensagem, java.nio.charset.StandardCharsets.UTF_8);
        System.out.println("URL para enviar mensagem: " + url);
    }
}
