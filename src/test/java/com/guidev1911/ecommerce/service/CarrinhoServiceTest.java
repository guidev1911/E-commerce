package com.guidev1911.ecommerce.service;

import com.guidev1911.ecommerce.dto.CarrinhoDTO;
import com.guidev1911.ecommerce.exception.EstoqueInsuficienteException;
import com.guidev1911.ecommerce.mapper.CarrinhoMapper;
import com.guidev1911.ecommerce.model.Carrinho;
import com.guidev1911.ecommerce.model.ItemCarrinho;
import com.guidev1911.ecommerce.model.Produto;
import com.guidev1911.ecommerce.model.Usuario;
import com.guidev1911.ecommerce.repository.CarrinhoRepository;
import com.guidev1911.ecommerce.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarrinhoServiceTest {

    @Mock
    private CarrinhoRepository carrinhoRepository;
    @Mock
    private ProdutoRepository produtoRepository;
    @Mock
    private CarrinhoMapper carrinhoMapper;

    @InjectMocks
    private CarrinhoService carrinhoService;

    private Usuario usuario;
    private Produto produto;
    private Carrinho carrinho;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);

        produto = new Produto();
        produto.setId(10L);
        produto.setNome("Notebook");
        produto.setEstoque(5);

        carrinho = new Carrinho();
        carrinho.setUsuario(usuario);
        carrinho.setItens(new ArrayList<>());
    }

    @Test
    void deveAdicionarItemNovoAoCarrinho() {
        when(carrinhoRepository.findByUsuario(usuario)).thenReturn(Optional.of(carrinho));
        when(produtoRepository.findById(10L)).thenReturn(Optional.of(produto));
        when(carrinhoRepository.save(any())).thenReturn(carrinho);
        when(carrinhoMapper.toDTO(any())).thenReturn(new CarrinhoDTO());

        CarrinhoDTO dto = carrinhoService.alterarItem(usuario, 10L, 2, false);

        assertNotNull(dto);
        assertEquals(1, carrinho.getItens().size());
        assertEquals(2, carrinho.getItens().getFirst().getQuantidade());
    }

    @Test
    void deveLancarExcecaoQuandoQuantidadeMaiorQueEstoque() {
        when(carrinhoRepository.findByUsuario(usuario)).thenReturn(Optional.of(carrinho));
        when(produtoRepository.findById(10L)).thenReturn(Optional.of(produto));

        assertThrows(EstoqueInsuficienteException.class, () -> {
            carrinhoService.alterarItem(usuario, 10L, 10, false);
        });
    }

    @Test
    void deveIncrementarQuantidadeDeItemExistente() {
        ItemCarrinho item = new ItemCarrinho();
        item.setProduto(produto);
        item.setQuantidade(1);
        carrinho.getItens().add(item);

        when(carrinhoRepository.findByUsuario(usuario)).thenReturn(Optional.of(carrinho));
        when(produtoRepository.findById(10L)).thenReturn(Optional.of(produto));
        when(carrinhoRepository.save(any())).thenReturn(carrinho);
        when(carrinhoMapper.toDTO(any())).thenReturn(new CarrinhoDTO());

        carrinhoService.alterarItem(usuario, 10L, 2, true);

        assertEquals(3, carrinho.getItens().getFirst().getQuantidade());
    }

    @Test
    void deveAtualizarQuantidadeDoItem() {
        ItemCarrinho item = new ItemCarrinho();
        item.setProduto(produto);
        item.setQuantidade(1);
        carrinho.getItens().add(item);

        when(carrinhoRepository.findByUsuario(usuario)).thenReturn(Optional.of(carrinho));
        when(produtoRepository.findById(10L)).thenReturn(Optional.of(produto));
        when(carrinhoRepository.save(any())).thenReturn(carrinho);
        when(carrinhoMapper.toDTO(any())).thenReturn(new CarrinhoDTO());

        carrinhoService.alterarItem(usuario, 10L, 3, false);

        assertEquals(3, carrinho.getItens().getFirst().getQuantidade());
    }

    @Test
    void deveRemoverItemQuandoQuantidadeMenorIgualZero() {
        ItemCarrinho item = new ItemCarrinho();
        item.setProduto(produto);
        item.setQuantidade(2);
        carrinho.getItens().add(item);

        when(carrinhoRepository.findByUsuario(usuario)).thenReturn(Optional.of(carrinho));
        when(produtoRepository.findById(10L)).thenReturn(Optional.of(produto));
        when(carrinhoRepository.save(any())).thenReturn(carrinho);
        when(carrinhoMapper.toDTO(any())).thenReturn(new CarrinhoDTO());

        carrinhoService.alterarItem(usuario, 10L, 0, false);

        assertTrue(carrinho.getItens().isEmpty());
    }

    @Test
    void deveLancarExcecaoAoIncrementarAlemDoEstoque() {
        ItemCarrinho item = new ItemCarrinho();
        item.setProduto(produto);
        item.setQuantidade(4);
        carrinho.getItens().add(item);

        when(carrinhoRepository.findByUsuario(usuario)).thenReturn(Optional.of(carrinho));
        when(produtoRepository.findById(10L)).thenReturn(Optional.of(produto));

        assertThrows(EstoqueInsuficienteException.class, () -> {
            carrinhoService.alterarItem(usuario, 10L, 2, true);
        });
    }

    @Test
    void deveRemoverItemExistente() {
        ItemCarrinho item = new ItemCarrinho();
        item.setProduto(produto);
        item.setQuantidade(2);
        carrinho.getItens().add(item);

        when(carrinhoRepository.findByUsuario(usuario)).thenReturn(Optional.of(carrinho));
        when(carrinhoRepository.save(any())).thenReturn(carrinho);
        when(carrinhoMapper.toDTO(any())).thenReturn(new CarrinhoDTO());

        carrinhoService.removerItem(usuario, 10L);

        assertTrue(carrinho.getItens().isEmpty());
    }

    @Test
    void deveListarCarrinhoExistente() {
        when(carrinhoRepository.findByUsuario(usuario)).thenReturn(Optional.of(carrinho));
        when(carrinhoMapper.toDTO(carrinho)).thenReturn(new CarrinhoDTO());

        CarrinhoDTO dto = carrinhoService.listarCarrinho(usuario);

        assertNotNull(dto);
    }

    @Test
    void deveCriarCarrinhoSeNaoExistirAoListar() {
        when(carrinhoRepository.findByUsuario(usuario)).thenReturn(Optional.empty());
        when(carrinhoRepository.save(any())).thenReturn(carrinho);
        when(carrinhoMapper.toDTO(carrinho)).thenReturn(new CarrinhoDTO());

        CarrinhoDTO dto = carrinhoService.listarCarrinho(usuario);

        assertNotNull(dto);
        verify(carrinhoRepository).save(any(Carrinho.class));
    }

    @Test
    void deveLimparCarrinho() {
        ItemCarrinho item = new ItemCarrinho();
        item.setProduto(produto);
        item.setQuantidade(2);
        carrinho.getItens().add(item);

        when(carrinhoRepository.findByUsuario(usuario)).thenReturn(Optional.of(carrinho));
        when(carrinhoRepository.save(any())).thenReturn(carrinho);

        carrinhoService.limparCarrinho(usuario);

        assertTrue(carrinho.getItens().isEmpty());
        verify(carrinhoRepository).save(carrinho);
    }
}