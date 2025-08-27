package com.guidev1911.ecommerce.mapper;

import com.guidev1911.ecommerce.dto.CategoriaDTO;
import com.guidev1911.ecommerce.model.Categoria;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {
    CategoriaMapper INSTANCE = Mappers.getMapper(CategoriaMapper.class);

    CategoriaDTO toDTO(Categoria categoria);
    Categoria toEntity(CategoriaDTO dto);
    void updateFromDto(CategoriaDTO dto, @MappingTarget Categoria categoria);
}