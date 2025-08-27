package com.guidev1911.ecommerce.mapper;

import com.guidev1911.ecommerce.dto.ProdutoDTO;
import com.guidev1911.ecommerce.model.Produto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface ProdutoMapper {

    ProdutoMapper INSTANCE = Mappers.getMapper(ProdutoMapper.class);

    ProdutoDTO toDTO(Produto produto);

    Produto toEntity(ProdutoDTO produtoDTO);

    List<ProdutoDTO> toDTOList(List<Produto> produtos);

    void updateEntityFromDTO(ProdutoDTO produtoDTO, @MappingTarget Produto produto);
}