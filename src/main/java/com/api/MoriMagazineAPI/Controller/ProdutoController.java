package com.api.MoriMagazineAPI.controller;

import com.api.MoriMagazineAPI.data.ProdutoEntity;
import com.api.MoriMagazineAPI.service.ProdutoService;

import jakarta.validation.Valid;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

// Importações omitidas por brevidade
@Controller
@RequestMapping("/produto")
public class ProdutoController {

    @Autowired
    ProdutoService produtoService;

    @GetMapping("/listar")
    public String viewProdutosPage(Model model) {
        model.addAttribute("listarProdutos", produtoService.listarTodosProdutos());
        return "IndexProdutos";
    }
 @GetMapping("/deletar/{id}")
    public String mostrarFormularioExclusao(@PathVariable(value = "id") Integer id, Model model) {
        // Buscar o produto pelo ID
        ProdutoEntity produto = produtoService.getProdutoId(id);
        
        // Verificar se o produto existe
        if (produto == null) {
            // Se o produto não existir, redirecionar para a lista de produtos
            return "redirect:/produto/listar";
        }
        
        // Adicionar o produto à model
        model.addAttribute("produto", produto);
        
        // Retornar o nome da página de confirmação de exclusão
        return "ConfirmarExclusaoProduto";
    }

    @PostMapping("/deletar/{id}")
    public String deletarProduto(@PathVariable(value = "id") Integer id) {
        // Deletar o produto com o ID fornecido
        produtoService.deletarProduto(id);
        
        // Redirecionar o usuário de volta para a lista de produtos após a exclusão
        return "redirect:/produto/listar";
    }

    // Outros métodos do controller


    @GetMapping("/criarForm")
    public String criarProdutoForm(Model model) {
        model.addAttribute("produto", new ProdutoEntity());
        return "inserirProduto";
    }
@PostMapping("/salvarProduto")
    public String salvarProduto(@Valid @ModelAttribute("produto") ProdutoEntity produto,
            BindingResult result) {

        if (result.hasErrors()) {
            return "inserirProduto"; // Retorna para a página de inserção de produto se houver erros de validação
        }

        // Converte o preço para um formato adequado antes de salvar no banco de dados
        try {
            String precoFormatado = produto.getPreco().replace(",", ".").replace("R$", "").trim();
            produto.setPreco(precoFormatado);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (produto.getId() == null) {
            produtoService.criarProduto(produto);
        } else {
            produtoService.atualizarProduto(produto.getId(), produto);
        }

        return "redirect:/produto/listar"; // Redireciona para a página de listagem de produtos após salvar com sucesso
    }


   @GetMapping("/atualizarForm/{id}")
public String atualizarProdutoForm(@PathVariable(value = "id") Integer id, Model model) {
    ProdutoEntity produto = produtoService.getProdutoId(id);
    model.addAttribute("produto", produto);
    model.addAttribute("dataCompra", produto.getCompra()); // Passando a data de compra para a página
    return "atualizarProduto";
}


 @PostMapping("/atualizar")
public String atualizarProduto(
    @Valid @ModelAttribute("produto") ProdutoEntity produto,
    BindingResult result) {

    if (result.hasErrors()) {
        return "atualizarProduto";
    }

    // Formatando o preço para remover o "R$" antes de atualizar
    String precoFormatado = produto.getPreco().replace("R$", "").trim();
    produto.setPreco(precoFormatado);

    // Atualizando apenas os campos que foram preenchidos
    if (produto.getNomeProduto() != null && !produto.getNomeProduto().isEmpty()) {
        produtoService.atualizarNomeProduto(produto.getId(), produto.getNomeProduto());
    }
    if (produto.getPreco() != null && !produto.getPreco().isEmpty()) {
        produtoService.atualizarPrecoProduto(produto.getId(), produto.getPreco());
    }
    if (produto.getCompra() != null) {
        produtoService.atualizarDataCompraProduto(produto.getId(), produto.getCompra());
    }

    return "redirect:/produto/listar";
}



@GetMapping("/pesquisar")
    public String pesquisarProdutoPorNome(@RequestParam("termo") String termo, Model model) {
        List<ProdutoEntity> resultados = produtoService.getProdutoPorNome(termo);
        model.addAttribute("resultados", resultados);
        return "pesquisa-produto";
    }

}
