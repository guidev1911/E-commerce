package com.guidev1911.ecommerce.mapper;

import com.guidev1911.ecommerce.dto.CarrinhoDTO;
import com.guidev1911.ecommerce.dto.ItemCarrinhoDTO;
import com.guidev1911.ecommerce.model.Carrinho;
import com.guidev1911.ecommerce.model.ItemCarrinho;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CarrinhoMapper {

    @Mapping(target = "usuarioId", source = "usuario.id")
    @Mapping(target = "itens", source = "itens")
    @Mapping(target = "total", expression = "java(carrinho.getTotal())")
    CarrinhoDTO toDTO(Carrinho carrinho);

    @Mapping(target = "produtoId", source = "produto.id")
    @Mapping(target = "nomeProduto", source = "produto.nome")
    @Mapping(target = "precoUnitario", source = "produto.preco")
    @Mapping(target = "subtotal", expression = "java(itemCarrinho.getSubtotal())")
    ItemCarrinhoDTO toItemDTO(ItemCarrinho itemCarrinho);
}
