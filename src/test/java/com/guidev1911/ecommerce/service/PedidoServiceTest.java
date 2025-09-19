package com.guidev1911.ecommerce.service;

import static org.junit.jupiter.api.Assertions.*;

import com.guidev1911.ecommerce.dto.*;
import com.guidev1911.ecommerce.exception.CancelamentoNaoPermitidoException;
import com.guidev1911.ecommerce.exception.CarrinhoVazioException;
import com.guidev1911.ecommerce.mapper.PedidoMapper;
import com.guidev1911.ecommerce.model.*;
import com.guidev1911.ecommerce.repository.PedidoRepository;
import com.guidev1911.ecommerce.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private CarrinhoService carrinhoService;
    @Mock
    private PedidoMapper pedidoMapper;
    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private PedidoService pedidoService;

    private Usuario usuario;
    private CarrinhoDTO carrinhoDTO;
    private Produto produto;
    private PedidoCreateDTO pedidoCreateDTO;
    private Endereco endereco;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);

        endereco = new Endereco();
        endereco.setId(100L);
        endereco.setEstado("SE");
        usuario.setEnderecos(List.of(endereco));

        produto = new Produto();
        produto.setId(10L);
        produto.setNome("Notebook");
        produto.setPeso(PesoProduto.LEVE);
        produto.setTamanho(TamanhoProduto.MEDIO);
        produto.setFragilidade(FragilidadeProduto.BAIXA);

        ItemCarrinhoDTO itemDTO = new ItemCarrinhoDTO();
        itemDTO.setProdutoId(produto.getId());
        itemDTO.setQuantidade(2);
        itemDTO.setPrecoUnitario(new BigDecimal("1000"));
        itemDTO.setSubtotal(new BigDecimal("2000"));

        carrinhoDTO = new CarrinhoDTO();
        carrinhoDTO.setItens(List.of(itemDTO));

        pedidoCreateDTO = new PedidoCreateDTO();
        pedidoCreateDTO.setEnderecoId(endereco.getId());
    }

    @Test
    void deveSimularPedido() {
        when(carrinhoService.listarCarrinho(usuario)).thenReturn(carrinhoDTO);
        when(produtoRepository.findAllById(List.of(produto.getId())))
                .thenReturn(List.of(produto));
        when(pedidoMapper.toItemDTO(any())).thenReturn(new ItemPedidoDTO());

        PedidoPreviewDTO preview = pedidoService.simularPedido(usuario, pedidoCreateDTO);

        assertNotNull(preview);
        assertEquals(carrinhoDTO.getItens().size(), preview.getItens().size());
        assertTrue(preview.getSubtotal().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void deveLancarExcecaoSeCarrinhoVazioNaSimulacao() {
        CarrinhoDTO vazio = new CarrinhoDTO();
        vazio.setItens(List.of());

        when(carrinhoService.listarCarrinho(usuario)).thenReturn(vazio);

        assertThrows(CarrinhoVazioException.class,
                () -> pedidoService.simularPedido(usuario, pedidoCreateDTO));
    }

    @Test
    void deveCriarPedido() {
        when(carrinhoService.listarCarrinho(usuario)).thenReturn(carrinhoDTO);
        when(produtoRepository.findAllById(List.of(produto.getId())))
                .thenReturn(List.of(produto));

        Pedido pedidoSalvo = new Pedido();
        pedidoSalvo.setId(1L);
        when(pedidoRepository.save(any())).thenReturn(pedidoSalvo);
        when(pedidoMapper.toDTO(pedidoSalvo)).thenReturn(new PedidoDTO());

        PedidoDTO dto = pedidoService.criarPedido(usuario, pedidoCreateDTO);

        assertNotNull(dto);
        verify(carrinhoService).limparCarrinho(usuario);
        verify(pedidoRepository).save(any());
    }

    @Test
    void deveBuscarPedidoPorId() {
        Pedido pedido = new Pedido();
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setExpiraEm(LocalDateTime.now().plusHours(1));

        when(pedidoRepository.findByIdAndUsuario(1L, usuario)).thenReturn(Optional.of(pedido));
        when(pedidoMapper.toDTO(pedido)).thenReturn(new PedidoDTO());

        PedidoDTO dto = pedidoService.buscarPorId(usuario, 1L);

        assertNotNull(dto);
    }

    @Test
    void deveExpirarPedidoAoBuscar() {
        Pedido pedido = new Pedido();
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setExpiraEm(LocalDateTime.now().minusMinutes(1));

        when(pedidoRepository.findByIdAndUsuario(1L, usuario)).thenReturn(Optional.of(pedido));
        when(pedidoMapper.toDTO(pedido)).thenReturn(new PedidoDTO());

        PedidoDTO dto = pedidoService.buscarPorId(usuario, 1L);

        assertEquals(StatusPedido.EXPIRADO, pedido.getStatus());
        verify(pedidoRepository).save(pedido);
    }

    @Test
    void deveCancelarPedido() {
        Pedido pedido = new Pedido();
        pedido.setStatus(StatusPedido.PENDENTE);

        when(pedidoRepository.findByIdAndUsuario(1L, usuario)).thenReturn(Optional.of(pedido));
        when(pedidoMapper.toDTO(pedido)).thenReturn(new PedidoDTO());

        PedidoDTO dto = pedidoService.cancelarPedido(usuario, 1L);

        assertEquals(StatusPedido.CANCELADO, pedido.getStatus());
        verify(pedidoRepository).save(pedido);
    }

    @Test
    void deveLancarExcecaoAoCancelarPedidoNaoPermitido() {
        Pedido pedido = new Pedido();
        pedido.setStatus(StatusPedido.ENVIADO);

        when(pedidoRepository.findByIdAndUsuario(1L, usuario)).thenReturn(Optional.of(pedido));

        assertThrows(CancelamentoNaoPermitidoException.class,
                () -> pedidoService.cancelarPedido(usuario, 1L));
    }

    @Test
    void deveListarPedidosDoUsuario() {
        Pedido pedido = new Pedido();
        List<Pedido> lista = List.of(pedido);

        when(pedidoRepository.findByUsuario(usuario)).thenReturn(lista);
        when(pedidoMapper.toDTO(pedido)).thenReturn(new PedidoDTO());

        List<PedidoDTO> pedidos = pedidoService.listarPedidos(usuario);

        assertEquals(1, pedidos.size());
    }

    @Test
    void deveCancelarPedidosExpirados() {
        Pedido pedido = new Pedido();
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setExpiraEm(LocalDateTime.now().minusMinutes(1));

        when(pedidoRepository.findByStatus(StatusPedido.PENDENTE)).thenReturn(List.of(pedido));

        pedidoService.cancelarPedidosExpirados();

        assertEquals(StatusPedido.EXPIRADO, pedido.getStatus());
        verify(pedidoRepository).save(pedido);
    }
}