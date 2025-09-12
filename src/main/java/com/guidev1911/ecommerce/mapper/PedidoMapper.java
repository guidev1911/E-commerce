package com.guidev1911.ecommerce.mapper;

import com.guidev1911.ecommerce.dto.ItemPedidoDTO;
import com.guidev1911.ecommerce.dto.PedidoDTO;
import com.guidev1911.ecommerce.dto.ProdutoDTO;
import com.guidev1911.ecommerce.model.ItemPedido;
import com.guidev1911.ecommerce.model.Pedido;
import com.guidev1911.ecommerce.model.Produto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PedidoMapper {

    @Mapping(source = "usuario.id", target = "usuarioId")
    @Mapping(source = "itens", target = "itens")
    @Mapping(source = "total", target = "total")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "criadoEm", target = "criadoEm")
    @Mapping(source = "expiraEm", target = "expiraEm")
    @Mapping(source = "enderecoEntrega.id", target = "enderecoId")
    @Mapping(source = "frete", target = "frete")
    PedidoDTO toDTO(Pedido pedido);

    @Mapping(source = "produto.id", target = "produtoId")
    @Mapping(source = "produto.nome", target = "nomeProduto")
    @Mapping(source = "produto.peso", target = "peso")
    @Mapping(source = "produto.tamanho", target = "tamanho")
    @Mapping(source = "produto.fragilidade", target = "fragilidade")
    ItemPedidoDTO toItemDTO(ItemPedido item);

    List<PedidoDTO> toDTOList(List<Pedido> pedidos);
}
