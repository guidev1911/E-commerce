package com.guidev1911.ecommerce.mapper;

import com.guidev1911.ecommerce.dto.ItemPedidoDTO;
import com.guidev1911.ecommerce.dto.PedidoDTO;
import com.guidev1911.ecommerce.model.ItemPedido;
import com.guidev1911.ecommerce.model.Pedido;
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
    PedidoDTO toDTO(Pedido pedido);

    @Mapping(source = "produto.id", target = "produtoId")
    @Mapping(source = "produto.nome", target = "nomeProduto")
    ItemPedidoDTO toItemDTO(ItemPedido item);

    List<PedidoDTO> toDTOList(List<Pedido> pedidos);
}