package com.api.MoriMagazineAPI.controller;

import com.api.MoriMagazineAPI.data.ClienteEntity;
import com.api.MoriMagazineAPI.data.ProdutoEntity;
import com.api.MoriMagazineAPI.data.TransacaoEntity;
import com.api.MoriMagazineAPI.data.ParcelaEntity;
import com.api.MoriMagazineAPI.data.StatusParcela;
import com.api.MoriMagazineAPI.dto.ParcelaDetalhesDTO;
import com.api.MoriMagazineAPI.dto.TransacaoDetalhesDTO;
import com.api.MoriMagazineAPI.dto.TransacaoDTO;
import com.api.MoriMagazineAPI.dto.ItemDTO;
import com.api.MoriMagazineAPI.service.ClienteService;
import com.api.MoriMagazineAPI.service.ParcelaService;
import com.api.MoriMagazineAPI.service.ProdutoService;
import com.api.MoriMagazineAPI.service.TransacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/transacao")
public class TransacaoControllerWeb {

    private static final Logger logger = LoggerFactory.getLogger(TransacaoControllerWeb.class);

    private final TransacaoService transacaoService;
    private final ClienteService clienteService;
    private final ProdutoService produtoService;
    private final ParcelaService parcelaService;

    @Autowired
    public TransacaoControllerWeb(TransacaoService transacaoService, ClienteService clienteService, ProdutoService produtoService, ParcelaService parcelaService) {
        this.transacaoService = transacaoService;
        this.clienteService = clienteService;
        this.produtoService = produtoService;
        this.parcelaService = parcelaService;
    }

   @GetMapping("/listar")
public String viewTransacoesPage(@RequestParam(value = "mes", required = false) Integer mes,
                                 @RequestParam(value = "ano", required = false) Integer ano,
                                 @RequestParam(value = "nomeCliente", required = false) String nomeCliente,
                                 @RequestParam(value = "formaPagamento", required = false) String formaPagamento,
                                 Model model) {
    try {
        List<TransacaoEntity> transacoes;
        if (mes != null && ano != null) {
            transacoes = transacaoService.listarTransacoesPorMesAnoFormaPagamento(mes, ano, formaPagamento);
        } else if (nomeCliente != null && !nomeCliente.isEmpty()) {
            transacoes = transacaoService.listarTransacoesPorNomeCliente(nomeCliente);
        } else {
            transacoes = transacaoService.listarTodasTransacoes();
        }
        
        for (TransacaoEntity transacao : transacoes) {
            int parcelasPagas = (int) transacao.getParcelas().stream()
                    .filter(parcela -> parcela.getStatus() == StatusParcela.PAGO)
                    .count();
            for (ParcelaEntity parcela : transacao.getParcelas()) {
                parcela.setRestantes(transacao.getParcelas().size() - parcelasPagas);
            }
        }
        
        model.addAttribute("transacoes", transacoes);
    } catch (Exception e) {
        model.addAttribute("errorMessage", "Erro ao carregar as transações.");
    }
    return "indexTransacoes";
}

    @PostMapping("/salvarTransacao")
    @ResponseBody
    public ResponseEntity<?> salvarTransacao(@Valid @ModelAttribute TransacaoDTO transacaoDTO,
                                             BindingResult bindingResult,
                                             RedirectAttributes redirectAttributes,
                                             Model model,
                                             HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder("Erro de validação: ");
            bindingResult.getAllErrors().forEach(error -> errorMessages.append(error.getDefaultMessage()).append("; "));
            model.addAttribute("clientes", clienteService.listarTodosClientes());
            model.addAttribute("produtos", produtoService.listarTodosProdutos());
            return ResponseEntity.badRequest().body(errorMessages.toString());
        }
        logger.debug("Dados da transação recebidos: {}", transacaoDTO);
        TransacaoEntity transacao = transacaoDTO.toTransacaoEntity(produtoService);
        transacao.setDataTransacao(LocalDate.now());

        String formaPagamento = transacao.getFormaPagamento().toLowerCase();
        if (formaPagamento.equals("dinheiro") || formaPagamento.equals("cartao de debito") || formaPagamento.equals("pix") || formaPagamento.equals("cartao de credito")) {
            transacao.setStatus("Pago");
        } else {
            transacao.setStatus("Pendente");
        }

        for (ItemDTO item : transacaoDTO.getItens()) {
ProdutoEntity produto = produtoService.getProdutoId(item.getProdutoId());
            if (produto != null) {
                if (produto.getQuantidadeRestante() < item.getQuantidade()) {
                    String errorMessage = "A quantidade vendida para o produto " + produto.getNomeProduto() + " excede o estoque disponível. Quantidade em estoque: " + produto.getQuantidadeRestante();
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
                }
                produto.vender(item.getQuantidade());
                produtoService.salvarProduto(produto);
            }
        }

        transacaoService.criarTransacaoWeb(transacao, transacaoDTO.getClienteId(),
                transacaoDTO.getItens().stream().map(ItemDTO::getQuantidade).collect(Collectors.toList()));
        logger.debug("Transação criada: {}", transacao);

        String enviarWhatsApp = request.getParameter("enviarWhatsApp");
        if ("true".equalsIgnoreCase(enviarWhatsApp)) {
            String mensagem = construirMensagemWhatsApp(transacaoDTO, transacao);
            logger.debug("Mensagem de WhatsApp: {}", mensagem);
            return ResponseEntity.ok("Resumo enviado via WhatsApp!");
        }

        if ("XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))) {
            return ResponseEntity.ok("Transação registrada com sucesso!");
        } else {
            redirectAttributes.addFlashAttribute("message", "Transação registrada com sucesso!");
            return ResponseEntity.status(303).header("Location", "/transacao/listar").build();
        }
    }

    private String construirMensagemWhatsApp(TransacaoDTO transacaoDTO, TransacaoEntity transacao) {
        String produtos = transacaoDTO.getItens().stream()
                .map(item -> {
                ProdutoEntity produto = produtoService.getProdutoId(item.getProdutoId());
                    return String.format("%s (%d x R$%.2f)", produto.getNomeProduto(), item.getQuantidade(), produto.getPreco());
                })
                .collect(Collectors.joining(", "));
        StringBuilder mensagem = new StringBuilder();
        mensagem.append("🛍️ *Resumo da Venda* 🛍️\n");
        mensagem.append(String.format("*Cliente:* %s (ID: %d)\n", transacao.getCliente().getNome(), transacao.getCliente().getId()));
        mensagem.append(String.format("*Produtos:* %s\n", produtos));
        mensagem.append(String.format("*Subtotal:* R$ %.2f\n", transacao.getValorTotal()));
        mensagem.append(String.format("*Forma de Pagamento:* %s\n", transacao.getFormaPagamento()));

        if ("Crediário".equals(transacao.getFormaPagamento()) || "Cartão de Crédito".equals(transacao.getFormaPagamento())) {
            mensagem.append("*Parcelamento:* ");
            for (ParcelaEntity parcela : transacao.getParcelas()) {
                mensagem.append(String.format("Parcela de R$ %.2f com vencimento em %s\n", parcela.getValorParcela(), parcela.getDataVencimento()));
            }
        } else {
            mensagem.append("*Parcelamento:* À vista\n");
        }
        mensagem.append(String.format("*Data da Compra:* %s\n", transacao.getDataTransacao()));
        mensagem.append(String.format("*Total:* R$ %.2f\n", transacao.getValorTotal()));
        mensagem.append("\nAgradecemos pela sua preferência! 😊");
        return mensagem.toString();
    }

    @GetMapping("/criarForm")
    public String criarTransacaoForm(Model model) {
        logger.debug("Abrindo formulário de criação de transação");
        model.addAttribute("clientes", clienteService.listarTodosClientes());
        model.addAttribute("produtos", produtoService.listarTodosProdutos());
        model.addAttribute("transacao", new TransacaoDTO());
        return "inserirTransacao";
    }

    @GetMapping("/listar-produtos")
    @ResponseBody
    public ResponseEntity<List<ProdutoEntity>> listarProdutos() {
        try {
            List<ProdutoEntity> produtos = produtoService.listarTodosProdutos();
            return ResponseEntity.ok(produtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/listar-clientes")
    @ResponseBody
    public ResponseEntity<List<ClienteEntity>> listarClientes() {
        try {
            List<ClienteEntity> clientes = clienteService.listarTodosClientes();
            return ResponseEntity.ok(clientes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/buscar-por-mes")
    public String buscarTransacoesPorMes(@RequestParam("mes") int mes, Model model) {
        logger.debug("Buscando transações por mês: {}", mes);
        List<TransacaoEntity> transacoes = transacaoService.listarTransacoesPorMes(mes);
        model.addAttribute("transacoes", transacoes);
        return "indexTransacoes";
    }

    @GetMapping("/buscar-por-mes-e-ano")
    public String buscarTransacoesPorMesEAno(@RequestParam("mes") int mes, @RequestParam("ano") int ano, Model model) {
        logger.debug("Buscando transações por mês: {} e ano: {}", mes, ano);
        List<TransacaoEntity> transacoes = transacaoService.listarTransacoesPorMesAno(mes, ano);
        model.addAttribute("transacoes", transacoes);
        return "indexTransacoes";
    }

    @GetMapping("/buscar-por-nome-cliente")
    public String buscarTransacoesPorNomeCliente(@RequestParam("nomeCliente") String nomeCliente, Model model) {
        logger.debug("Buscando transações por nome do cliente: {}", nomeCliente);
        List<TransacaoEntity> transacoes = transacaoService.listarTransacoesPorNomeCliente(nomeCliente);
        model.addAttribute("transacoes", transacoes);
        return "indexTransacoes";
    }

    @GetMapping("/detalhes/{id}")
    public String viewTransacaoDetalhes(@PathVariable Integer id, Model model) { // Ajuste para Integer
        Optional<TransacaoEntity> transacaoOpt = transacaoService.getTransacaoById(id);
        if (transacaoOpt.isPresent()) {
            TransacaoEntity transacao = transacaoOpt.get();
            TransacaoDetalhesDTO transacaoDetalhesDTO = new TransacaoDetalhesDTO(transacao);
            model.addAttribute("transacao", transacaoDetalhesDTO);
            return "detalhesTransacao";
        } else {
            model.addAttribute("errorMessage", "Transação não encontrada.");
            return "indexTransacoes";
        }
    }

    @GetMapping("/detalhesParcela/{id}")
    public String viewParcelaDetalhes(@PathVariable Integer id, Model model) { // Ajuste para Integer
        ParcelaEntity parcela = parcelaService.findById(id)
                .orElseThrow(() -> new RuntimeException("Parcela não encontrada"));
        ParcelaDetalhesDTO parcelaDetalhesDTO = new ParcelaDetalhesDTO(parcela);
        model.addAttribute("parcela", parcelaDetalhesDTO);
        return "detalhesParcela";
    }

    @GetMapping("/parcelas-atrasadas")
    public String listarParcelasAtrasadas(Model model) {
        List<ParcelaEntity> parcelasAtrasadas = parcelaService.listarParcelasAtrasadas();
        model.addAttribute("parcelasAtrasadas", parcelasAtrasadas);
        return "parcelasAtrasadas";
    }

    @PostMapping("/baixar-parcela/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> baixarParcela(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        try {
            ParcelaEntity parcela = parcelaService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Parcela não encontrada"));
            parcela.setStatus(StatusParcela.PAGO);
            parcelaService.save(parcela);
            TransacaoEntity transacao = parcela.getTransacao();
            boolean todasPagas = transacao.getParcelas().stream()
                    .allMatch(p -> p.getStatus() == StatusParcela.PAGO);
            if (todasPagas) {
                transacao.setStatus("Pago");
                transacaoService.atualizarTransacao(transacao.getId(), transacao);
            }
            response.put("status", "success");
            response.put("message", "Parcela baixada com sucesso.");
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Erro ao baixar a parcela: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/enviar-comprovante/{transacaoId}/{parcelaId}")
    @ResponseBody
    public ResponseEntity<?> enviarComprovantePagamento(@PathVariable Integer transacaoId, @PathVariable Integer parcelaId) { // Ajuste para Integer
        try {
            TransacaoEntity transacao = transacaoService.getTransacaoById(transacaoId)
                    .orElseThrow(() -> new RuntimeException("Transação não encontrada"));
            ParcelaEntity parcela = parcelaService.findById(parcelaId)
                    .orElseThrow(() -> new RuntimeException("Parcela não encontrada"));
            if (parcela.getStatus() != StatusParcela.PAGO) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Não é possível enviar o comprovante para parcelas não baixadas.");
            }
            parcelaService.enviarComprovantePagamento(parcelaId);
            return ResponseEntity.ok("Comprovante enviado com sucesso.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao enviar o comprovante.");
        }
    }

    @GetMapping("/pesquisar-clientes/{termo}")
    @ResponseBody
    public ResponseEntity<List<ClienteEntity>> pesquisarClientes(@PathVariable("termo") String termo) {
        List<ClienteEntity> clientes = clienteService.pesquisarClientes(termo);
        return new ResponseEntity<>(clientes, HttpStatus.OK);
    }
@GetMapping("/pesquisar-produtos/{termo}")
@ResponseBody
public ResponseEntity<List<ProdutoEntity>> pesquisarProdutos(@PathVariable String termo) {
    List<ProdutoEntity> produtos = produtoService.pesquisarProdutos(termo); // Altere para pesquisarProdutos
    return ResponseEntity.ok(produtos);
}
    @PostMapping("/atualizar-status-transacao/{parcelaId}")
    @ResponseBody
    public ResponseEntity<String> atualizarStatusTransacao(@PathVariable Integer parcelaId) { // Ajuste para Integer
        try {
            ParcelaEntity parcela = parcelaService.findById(parcelaId)
                    .orElseThrow(() -> new RuntimeException("Parcela não encontrada"));
            TransacaoEntity transacao = parcela.getTransacao();
            boolean todasPagas = transacao.getParcelas().stream()
                    .allMatch(p -> p.getStatus() == StatusParcela.PAGO);
            if (todasPagas) {
                transacao.setStatus("Pago");
                transacaoService.atualizarTransacao(transacao.getId(), transacao);
            }
            return ResponseEntity.ok("Status da transação atualizado com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar o status da transação: " + e.getMessage());
        }
    }
}