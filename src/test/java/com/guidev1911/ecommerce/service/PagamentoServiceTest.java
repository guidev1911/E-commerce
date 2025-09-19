package com.guidev1911.ecommerce.service;

import static org.junit.jupiter.api.Assertions.*;

import com.guidev1911.ecommerce.dto.PagamentoDTO;
import com.guidev1911.ecommerce.exception.PedidoNaoEncontradoException;
import com.guidev1911.ecommerce.model.*;
import com.guidev1911.ecommerce.repository.PagamentoRepository;
import com.guidev1911.ecommerce.repository.PedidoRepository;
import com.guidev1911.ecommerce.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PagamentoServiceTest {

    @Mock
    private PagamentoRepository pagamentoRepository;
    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private ProdutoRepository produtoRepository;

    private PagamentoService pagamentoService;

    private Pedido pedido;
    private Produto produto;
    private ItemPedido item;

    @BeforeEach
    void setUp() {
        pagamentoService = new PagamentoService(pagamentoRepository, pedidoRepository, produtoRepository) {
            @Override
            public void simularCallbackAsync(Long pagamentoId) {
            }
        };

        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Notebook");
        produto.setEstoque(5);

        item = new ItemPedido();
        item.setProduto(produto);
        item.setQuantidade(2);
        item.setSubtotal(new BigDecimal("2000"));

        pedido = new Pedido();
        pedido.setId(10L);
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setItens(List.of(item));
    }

    @Test
    void deveIniciarPagamento() {
        when(pedidoRepository.findById(10L)).thenReturn(Optional.of(pedido));

        Pagamento pagamentoSalvo = new Pagamento();
        pagamentoSalvo.setId(100L);
        pagamentoSalvo.setPedido(pedido);
        pagamentoSalvo.setStatus(StatusPagamento.PENDENTE);
        pagamentoSalvo.setValor(new BigDecimal("2000.00"));

        when(pagamentoRepository.save(any())).thenReturn(pagamentoSalvo);

        PagamentoDTO dto = pagamentoService.iniciarPagamento(10L, MetodoPagamento.CARTAO);

        assertNotNull(dto);
        assertEquals(StatusPagamento.PENDENTE, dto.getStatus());

        assertEquals(0, dto.getValor().compareTo(new BigDecimal("2000.00")));
        verify(pagamentoRepository).save(any());
    }

    @Test
    void deveLancarExcecaoSePedidoNaoPendente() {
        pedido.setStatus(StatusPedido.PAGO);
        when(pedidoRepository.findById(10L)).thenReturn(Optional.of(pedido));

        assertThrows(IllegalStateException.class,
                () -> pagamentoService.iniciarPagamento(10L, MetodoPagamento.CARTAO));
    }

    @Test
    void deveAprovarPagamentoQuandoEstoqueSuficiente() {
        Pagamento pagamento = new Pagamento();
        pagamento.setId(100L);
        pagamento.setPedido(pedido);
        pagamento.setStatus(StatusPagamento.PENDENTE);
        pagamento.setValor(new BigDecimal("2000"));

        when(pagamentoRepository.findById(100L)).thenReturn(Optional.of(pagamento));
        when(produtoRepository.save(any())).thenReturn(produto);
        when(pedidoRepository.save(any())).thenReturn(pedido);
        when(pagamentoRepository.save(any())).thenReturn(pagamento);

        pagamentoService.processarCallback(100L, true);

        assertEquals(StatusPagamento.APROVADO, pagamento.getStatus());
        assertEquals(StatusPedido.PAGO, pedido.getStatus());
        assertEquals(3, produto.getEstoque());
        assertNotNull(pedido.getPagoEm());
        assertNotNull(pagamento.getConfirmadoEm());
    }

    @Test
    void deveRecusarPagamento() {
        Pagamento pagamento = new Pagamento();
        pagamento.setId(100L);
        pagamento.setPedido(pedido);
        pagamento.setStatus(StatusPagamento.PENDENTE);
        pagamento.setValor(new BigDecimal("2000"));

        when(pagamentoRepository.findById(100L)).thenReturn(Optional.of(pagamento));
        when(pedidoRepository.save(any())).thenReturn(pedido);
        when(pagamentoRepository.save(any())).thenReturn(pagamento);

        pagamentoService.processarCallback(100L, false);

        assertEquals(StatusPagamento.RECUSADO, pagamento.getStatus());
        assertEquals(StatusPedido.CANCELADO, pedido.getStatus());
    }

    @Test
    void deveRecusarPagamentoSeEstoqueInsuficiente() {
        item.setQuantidade(10);
        Pagamento pagamento = new Pagamento();
        pagamento.setId(100L);
        pagamento.setPedido(pedido);
        pagamento.setStatus(StatusPagamento.PENDENTE);
        pagamento.setValor(new BigDecimal("2000"));

        when(pagamentoRepository.findById(100L)).thenReturn(Optional.of(pagamento));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> pagamentoService.processarCallback(100L, true));

        assertTrue(ex.getMessage().contains("Estoque insuficiente"));
        assertEquals(StatusPagamento.RECUSADO, pagamento.getStatus());
        assertEquals(StatusPedido.CANCELADO, pedido.getStatus());
    }

    @Test
    void deveLancarExcecaoSePedidoNaoEncontrado() {
        when(pedidoRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(PedidoNaoEncontradoException.class,
                () -> pagamentoService.iniciarPagamento(10L, MetodoPagamento.CARTAO));
    }

    @Test
    void deveLancarExcecaoSePagamentoNaoEncontrado() {
        when(pagamentoRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class,
                () -> pagamentoService.processarCallback(100L, true));
    }
}