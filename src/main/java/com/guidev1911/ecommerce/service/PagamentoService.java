package com.guidev1911.ecommerce.service;

import com.guidev1911.ecommerce.dto.PagamentoDTO;
import com.guidev1911.ecommerce.exception.PedidoNaoEncontradoException;
import com.guidev1911.ecommerce.model.*;
import com.guidev1911.ecommerce.repository.PagamentoRepository;
import com.guidev1911.ecommerce.repository.PedidoRepository;
import com.guidev1911.ecommerce.repository.ProdutoRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final Random random = new Random();

    public PagamentoService(PagamentoRepository pagamentoRepository,
                            PedidoRepository pedidoRepository,
                            ProdutoRepository produtoRepository) {
        this.pagamentoRepository = pagamentoRepository;
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public PagamentoDTO iniciarPagamento(Long pedidoId, MetodoPagamento metodo) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new PedidoNaoEncontradoException("Pedido não encontrado"));

        if (pedido.getStatus() != StatusPedido.PENDENTE) {
            throw new IllegalStateException("Pedido não está pendente de pagamento.");
        }

        BigDecimal totalCalculado = recalcularTotal(pedido);
        pedido.setTotal(totalCalculado.setScale(2, RoundingMode.HALF_UP));

        Pagamento pagamento = new Pagamento();
        pagamento.setPedido(pedido);
        pagamento.setValor(pedido.getTotal().setScale(2, RoundingMode.HALF_UP));
        pagamento.setMetodo(metodo);
        pagamento.setCriadoEm(LocalDateTime.now());
        pagamento.setStatus(StatusPagamento.PENDENTE);

        Pagamento salvo = pagamentoRepository.save(pagamento);

        simularCallbackAsync(salvo.getId());

        return new PagamentoDTO(
                salvo.getId(),
                salvo.getStatus(),
                salvo.getMetodo(),
                salvo.getValor(),
                salvo.getConfirmadoEm()
        );
    }

    @Async
    public void simularCallbackAsync(Long pagamentoId) {
        try {
            Thread.sleep(3000 + random.nextInt(4000));
            boolean aprovado = random.nextInt(100) < 80;
            processarCallback(pagamentoId, aprovado);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Transactional
    public void processarCallback(Long pagamentoId, boolean aprovado) {
        Pagamento pagamento = pagamentoRepository.findById(pagamentoId)
                .orElseThrow(() -> new IllegalStateException("Pagamento não encontrado"));

        Pedido pedido = pagamento.getPedido();

        if (aprovado) {
            for (ItemPedido item : pedido.getItens()) {
                Produto produto = item.getProduto();
                if (produto.getEstoque() < item.getQuantidade()) {
                    pagamento.setStatus(StatusPagamento.RECUSADO);
                    pedido.setStatus(StatusPedido.CANCELADO);
                    pagamentoRepository.save(pagamento);
                    pedidoRepository.save(pedido);
                    throw new IllegalStateException("Estoque insuficiente para: " + produto.getNome());
                }
            }

            for (ItemPedido item : pedido.getItens()) {
                Produto produto = item.getProduto();
                produto.setEstoque(produto.getEstoque() - item.getQuantidade());
                produtoRepository.save(produto);
            }

            pagamento.setStatus(StatusPagamento.APROVADO);
            pagamento.setConfirmadoEm(LocalDateTime.now());
            pedido.setStatus(StatusPedido.PAGO);
            pedido.setPagoEm(LocalDateTime.now());

        } else {
            pagamento.setStatus(StatusPagamento.RECUSADO);
            pedido.setStatus(StatusPedido.CANCELADO);
        }

        pedidoRepository.save(pedido);
        pagamentoRepository.save(pagamento);
    }

    private BigDecimal recalcularTotal(Pedido pedido) {
        return pedido.getItens().stream()
                .map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}