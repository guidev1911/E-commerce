package com.guidev1911.ecommerce.mapper;

import java.math.BigDecimal;
import java.util.List;

import com.guidev1911.ecommerce.dto.CarrinhoDTO;
import com.guidev1911.ecommerce.dto.ItemCarrinhoDTO;
import com.guidev1911.ecommerce.model.Carrinho;
import com.guidev1911.ecommerce.model.ItemCarrinho;
import com.guidev1911.ecommerce.model.Produto;
import com.guidev1911.ecommerce.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import static org.assertj.core.api.Assertions.assertThat;


public class CarrinhoMapperTest {

    private CarrinhoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(CarrinhoMapper.class);
    }

    @Test
    void testToItemDTO() {
        Produto produto = new Produto();
        produto.setId(10L);
        produto.setNome("Notebook");
        produto.setPreco(BigDecimal.valueOf(3500.00));

        ItemCarrinho item = new ItemCarrinho();
        item.setProduto(produto);
        item.setQuantidade(2);

        ItemCarrinhoDTO dto = mapper.toItemDTO(item);

        assertThat(dto).isNotNull();
        assertThat(dto.getProdutoId()).isEqualTo(produto.getId());
        assertThat(dto.getNomeProduto()).isEqualTo(produto.getNome());
        assertThat(dto.getPrecoUnitario()).isEqualByComparingTo(produto.getPreco());
        assertThat(dto.getQuantidade()).isEqualTo(item.getQuantidade());
        assertThat(dto.getSubtotal()).isEqualByComparingTo(item.getSubtotal());
    }

    @Test
    void testToDTO() {
        Usuario usuario = new Usuario();
        usuario.setId(5L);

        Produto produto1 = new Produto();
        produto1.setId(1L);
        produto1.setNome("Mouse");
        produto1.setPreco(BigDecimal.valueOf(100));

        Produto produto2 = new Produto();
        produto2.setId(2L);
        produto2.setNome("Teclado");
        produto2.setPreco(BigDecimal.valueOf(200));

        ItemCarrinho item1 = new ItemCarrinho();
        item1.setProduto(produto1);
        item1.setQuantidade(1);

        ItemCarrinho item2 = new ItemCarrinho();
        item2.setProduto(produto2);
        item2.setQuantidade(3);

        Carrinho carrinho = new Carrinho();
        carrinho.setUsuario(usuario);
        carrinho.setItens(List.of(item1, item2));

        item1.setCarrinho(carrinho);
        item2.setCarrinho(carrinho);

        CarrinhoDTO dto = mapper.toDTO(carrinho);
        assertThat(dto).isNotNull();
        assertThat(dto.getUsuarioId()).isEqualTo(usuario.getId());

        List<ItemCarrinhoDTO> itens = dto.getItens();
        assertThat(itens).isNotNull();
        assertThat(itens).hasSize(2);

        assertThat(dto.getTotal()).isEqualByComparingTo(carrinho.getTotal());

        assertThat(itens)
                .extracting(ItemCarrinhoDTO::getProdutoId)
                .containsExactlyInAnyOrder(1L, 2L);

    }
}
