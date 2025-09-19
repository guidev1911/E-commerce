package com.guidev1911.ecommerce.service;

import static org.junit.jupiter.api.Assertions.*;

import com.guidev1911.ecommerce.dto.ProdutoDTO;
import com.guidev1911.ecommerce.exception.CategoriaNaoEncontradaException;
import com.guidev1911.ecommerce.exception.ProdutoNaoEncontradoException;
import com.guidev1911.ecommerce.mapper.ProdutoMapper;
import com.guidev1911.ecommerce.model.Categoria;
import com.guidev1911.ecommerce.model.Produto;
import com.guidev1911.ecommerce.repository.CategoriaRepository;
import com.guidev1911.ecommerce.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.Mockito.*;

class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private ProdutoMapper produtoMapper;

    @InjectMocks
    private ProdutoService produtoService;

    private Produto produto;
    private ProdutoDTO produtoDTO;
    private Categoria categoria;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Informática");

        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Notebook");
        produto.setPreco(new BigDecimal("3500.00"));
        produto.setCategoria(categoria);

        produtoDTO = new ProdutoDTO();
        produtoDTO.setId(1L);
        produtoDTO.setNome("Notebook");
        produtoDTO.setPreco(new BigDecimal("3500.00"));
        produtoDTO.setCategoriaId(1L);
    }

    @Test
    void deveListarProdutosFiltrados() {
        Page<Produto> page = new PageImpl<>(List.of(produto));

        when(produtoRepository.findAll(ArgumentMatchers.<Specification<Produto>>any(), any(Pageable.class)))
                .thenReturn(page);

        when(produtoMapper.toDTO(produto)).thenReturn(produtoDTO);

        Page<ProdutoDTO> resultado = produtoService.listarFiltrado(1L, null, null, "Note", PageRequest.of(0, 10));

        assertEquals(1, resultado.getContent().size());
        assertEquals("Notebook", resultado.getContent().getFirst().getNome());

        verify(produtoMapper, times(1)).toDTO(produto);
    }

    @Test
    void deveRetornarPaginaVaziaQuandoNaoHaProdutos() {
        Page<Produto> page = Page.empty(PageRequest.of(0, 10));

        when(produtoRepository.findAll(ArgumentMatchers.<Specification<Produto>>any(), any(Pageable.class)))
                .thenReturn(page);

        Page<ProdutoDTO> resultado = produtoService.listarFiltrado(1L, null, null, "Note", PageRequest.of(0, 10));

        assertTrue(resultado.isEmpty());
        verify(produtoRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void deveBuscarProdutoPorId() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoMapper.toDTO(produto)).thenReturn(produtoDTO);

        ProdutoDTO resultado = produtoService.buscarPorId(1L);

        assertEquals("Notebook", resultado.getNome());
    }

    @Test
    void deveLancarExcecaoQuandoProdutoNaoEncontrado() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProdutoNaoEncontradoException.class, () -> produtoService.buscarPorId(1L));
    }

    @Test
    void deveCriarVariosProdutosComCategoriaExistente() {
        when(produtoMapper.toEntity(produtoDTO)).thenReturn(produto);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(produtoRepository.saveAll(anyList())).thenReturn(List.of(produto));
        when(produtoMapper.toDTO(produto)).thenReturn(produtoDTO);

        List<ProdutoDTO> resultado = produtoService.criarVarios(List.of(produtoDTO));

        assertEquals(1, resultado.size());
        assertEquals("Notebook", resultado.getFirst().getNome());
        verify(produtoRepository).saveAll(anyList());
    }

    @Test
    void deveLancarExcecaoAoCriarProdutoComCategoriaInexistente() {
        when(produtoMapper.toEntity(produtoDTO)).thenReturn(produto);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CategoriaNaoEncontradaException.class, () -> produtoService.criarVarios(List.of(produtoDTO)));
    }

    @Test
    void deveAtualizarProdutoComCategoriaExistente() {
        ProdutoDTO dtoAtualizado = new ProdutoDTO();
        dtoAtualizado.setId(1L);
        dtoAtualizado.setNome("Notebook Gamer");
        dtoAtualizado.setPreco(new BigDecimal("4500.00"));
        dtoAtualizado.setCategoriaId(1L);

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        doAnswer(invocation -> {
            ProdutoDTO dtoArg = invocation.getArgument(0);
            Produto entidadeArg = invocation.getArgument(1);
            entidadeArg.setNome(dtoArg.getNome());
            entidadeArg.setPreco(dtoArg.getPreco());
            return null;
        }).when(produtoMapper).updateEntityFromDTO(dtoAtualizado, produto);

        when(produtoRepository.save(produto)).thenReturn(produto);
        when(produtoMapper.toDTO(produto)).thenReturn(dtoAtualizado);

        ProdutoDTO resultado = produtoService.atualizar(1L, dtoAtualizado);

        assertEquals("Notebook Gamer", resultado.getNome());
        assertEquals(new BigDecimal("4500.00"), resultado.getPreco());
        verify(produtoRepository).save(produto);
    }

    @Test
    void deveLancarExcecaoAoAtualizarProdutoComCategoriaInexistente() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CategoriaNaoEncontradaException.class, () -> produtoService.atualizar(1L, produtoDTO));
    }

    @Test
    void deveDeletarProduto() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        produtoService.deletar(1L);

        verify(produtoRepository).delete(produto);
    }
}
