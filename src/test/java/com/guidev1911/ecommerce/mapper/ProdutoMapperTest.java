package com.guidev1911.ecommerce.mapper;

import com.guidev1911.ecommerce.dto.ProdutoDTO;
import com.guidev1911.ecommerce.model.Categoria;
import com.guidev1911.ecommerce.model.Produto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProdutoMapperTest {

    private final ProdutoMapper mapper = ProdutoMapper.INSTANCE;

    @Test
    void deveMapearProdutoParaDTO() {
        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Notebook");
        produto.setPreco(new BigDecimal("2000"));
        Categoria cat = new Categoria();
        cat.setId(5L);
        produto.setCategoria(cat);

        ProdutoDTO dto = mapper.toDTO(produto);

        assertEquals(produto.getId(), dto.getId());
        assertEquals(produto.getNome(), dto.getNome());
        assertEquals(produto.getPreco(), dto.getPreco());
        assertEquals(5L, dto.getCategoriaId());
    }

    @Test
    void deveMapearDTOParaProduto() {
        ProdutoDTO dto = new ProdutoDTO();
        dto.setNome("Mouse");
        dto.setPreco(new BigDecimal("150"));
        dto.setCategoriaId(3L);

        Produto produto = mapper.toEntity(dto);

        assertNull(produto.getId());
        assertEquals("Mouse", produto.getNome());
        assertEquals(new BigDecimal("150"), produto.getPreco());
        assertEquals(3L, produto.getCategoria().getId());
    }

    @Test
    void deveAtualizarProdutoComDTO() {
        Produto produto = new Produto();
        produto.setNome("Teclado");

        ProdutoDTO dto = new ProdutoDTO();
        dto.setNome("Teclado Mecânico");

        mapper.updateEntityFromDTO(dto, produto);

        assertEquals("Teclado Mecânico", produto.getNome());
    }

    @Test
    void deveMapearCategoria() {
        Categoria cat = mapper.map(7L);
        assertEquals(7L, cat.getId());
        assertNull(mapper.map(null));
    }
}