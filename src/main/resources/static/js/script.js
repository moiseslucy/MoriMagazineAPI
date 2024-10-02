// Função para enviar o formulário de transação
async function enviarFormulario() {
    try {
        // Preenchendo campos ocultos antes de enviar o formulário
        const clienteId = document.getElementById('clienteId').value;
        const subtotal = document.getElementById('subtotal').innerText.replace('R$', '').trim();
        const formaPagamento = document.getElementById('formaPagamento').value;
        const parcelamento = document.getElementById('numeroParcelas').value || 'N/A';
        const valorTotal = document.getElementById('valorTotal').innerText.replace('R$', '').trim();
        const dataCompra = document.getElementById('dataVencimento').value;

        // Exibir dados no console para depuração
        console.log('Dados do Cliente:', clienteId);
        console.log('Dados dos Produtos:', getProdutos());
        console.log('Forma de Pagamento:', formaPagamento);
        console.log('Valor Total:', valorTotal);

        // document.getElementById('inputResumoCliente').value = clienteId;
        // document.getElementById('inputResumoSubtotal').value = subtotal;
        // document.getElementById('inputResumoFormaPagamento').value = formaPagamento;
        // document.getElementById('inputResumoParcelamento').value = parcelamento;
        // document.getElementById('inputResumoTotal').value = valorTotal;
        // document.getElementById('inputDataCompra').value = dataCompra;
        // document.getElementById('inputResumoProdutos').value = JSON.stringify(getProdutos());

        // Preenchendo campos adicionais ocultos
        // document.getElementById('dataTransacao').value = dataCompra;
        // document.getElementById('status').value = "Pendente";
        // document.getElementById('valorTotal').value = valorTotal;

        // Criando o objeto FormData com os dados do formulário
        // const formData = new FormData(document.getElementById('inserirTransacao'));
        
        // Enviando os dados para o servidor
        // const response = await fetch('/transacao/salvarTransacao', {
        //     method: 'POST',
        //     body: formData,
        //     headers: {
        //         'Accept': 'application/json'
        //     }
        // });

        // Verificando se a resposta do servidor indica erro
        // if (!response.ok) {
        //     const errorMessage = await response.text();
        //     console.log('Resposta do servidor:', response);
        //     console.log('Mensagem de erro do servidor:', errorMessage);
        //     alert('Erro ao criar transação: ' + errorMessage);
        // } else {
        //     const result = await response.json();
        //     alert('Sucesso: ' + result.message);
        //     window.location.href = "/transacao/listar"; // Redireciona após o sucesso
        // }
    } catch (error) {
        console.error('Erro ao criar transação:', error);
        alert('Erro ao criar transação: ' + error.message);
    }
}

// Função para obter os produtos selecionados
function getProdutos() {
    const produtos = [];
    document.querySelectorAll('#produtosTable tbody tr').forEach(tr => {
        const produto = {
            produtoId: tr.querySelector('.produtoSelect').value,
            quantidade: tr.querySelector('.quantidadeInput').value,
            valorUnitario: tr.querySelector('.valorUnitarioInput').value.replace('R$', '').trim(),
            valorTotal: tr.querySelector('.valorTotalInput').value.replace('R$', '').trim()
        };
        produtos.push(produto);
    });
    return produtos;
}

// Event listener para o botão de envio do formulário
document.addEventListener('DOMContentLoaded', function() {
    const formVenda = document.getElementById('inserirTransacao');
    if (formVenda) {
        formVenda.addEventListener('submit', function(event) {
            event.preventDefault();
            enviarFormulario();
        });
    } else {
        console.error('Elemento com ID "inserirTransacao" não encontrado.');
    }
});
