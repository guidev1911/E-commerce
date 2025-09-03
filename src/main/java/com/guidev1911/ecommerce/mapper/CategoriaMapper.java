package com.guidev1911.ecommerce.mapper;

import com.guidev1911.ecommerce.dto.CategoriaDTO;
import com.guidev1911.ecommerce.model.Categoria;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {

    CategoriaDTO toDTO(Categoria categoria);
    Categoria toEntity(CategoriaDTO dto);

    @Mapping(target = "id", ignore = true)
    void updateFromDto(CategoriaDTO dto, @MappingTarget Categoria categoria);
}
