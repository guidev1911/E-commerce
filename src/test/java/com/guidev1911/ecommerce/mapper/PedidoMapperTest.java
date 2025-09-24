package com.guidev1911.ecommerce.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.guidev1911.ecommerce.dto.ItemPedidoDTO;
import com.guidev1911.ecommerce.dto.PedidoDTO;
import com.guidev1911.ecommerce.model.*;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;



class PedidoMapperTest {

    private final PedidoMapper mapper = Mappers.getMapper(PedidoMapper.class);

    @Test
    void deveMapearPedidoParaPedidoDTOComEndereco() {

        Usuario usuario = new Usuario();
        usuario.setId(10L);

        Endereco endereco = new Endereco();
        endereco.setId(5L);
        endereco.setUsuario(usuario);

        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Produto Teste");
        produto.setPeso(PesoProduto.LEVE);
        produto.setTamanho(TamanhoProduto.MEDIO);
        produto.setFragilidade(FragilidadeProduto.MEDIA);

        ItemPedido item = new ItemPedido();
        item.setProduto(produto);
        item.setQuantidade(2);
        item.setSubtotal(BigDecimal.valueOf(100));

        Pedido pedido = new Pedido();
        pedido.setId(100L);
        pedido.setUsuario(usuario);
        pedido.setItens(List.of(item));
        pedido.setTotal(BigDecimal.valueOf(100));
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setCriadoEm(LocalDateTime.now());
        pedido.setExpiraEm(LocalDateTime.now().plusDays(1));
        pedido.setFrete(BigDecimal.valueOf(20));
        pedido.setEnderecoEntrega(endereco);

        PedidoDTO dto = mapper.toDTO(pedido);
        ItemPedidoDTO itemDTO = dto.getItens().getFirst();

        assertEquals(pedido.getId(), dto.getId());
        assertEquals(pedido.getUsuario().getId(), dto.getUsuarioId());
        assertEquals(pedido.getTotal(), dto.getTotal());
        assertEquals(pedido.getStatus().name(), dto.getStatus());
        assertEquals(pedido.getCriadoEm(), dto.getCriadoEm());
        assertEquals(pedido.getExpiraEm(), dto.getExpiraEm());
        assertEquals(pedido.getFrete(), dto.getFrete());
        assertEquals(pedido.getEnderecoEntrega().getId(), dto.getEnderecoId());

        assertEquals(produto.getId(), itemDTO.getProdutoId());
        assertEquals(produto.getNome(), itemDTO.getNomeProduto());
        assertEquals(produto.getPeso(), PesoProduto.LEVE);
        assertEquals(produto.getTamanho(), TamanhoProduto.MEDIO);
        assertEquals(produto.getFragilidade(), FragilidadeProduto.MEDIA);

        assertEquals(item.getQuantidade(), itemDTO.getQuantidade());
        assertEquals(item.getSubtotal(), itemDTO.getSubtotal());
    }
    @Test
    void deveMapearListaDePedidosParaListaDeDTOs() {
        Pedido pedido1 = new Pedido();
        pedido1.setId(1L);

        Pedido pedido2 = new Pedido();
        pedido2.setId(2L);

        List<PedidoDTO> dtos = mapper.toDTOList(List.of(pedido1, pedido2));
        assertEquals(2, dtos.size());
        assertEquals(1L, dtos.get(0).getId());
        assertEquals(2L, dtos.get(1).getId());
    }
}