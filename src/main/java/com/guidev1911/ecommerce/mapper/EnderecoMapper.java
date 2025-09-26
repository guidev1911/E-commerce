package com.guidev1911.ecommerce.mapper;

import com.guidev1911.ecommerce.dto.EnderecoDTO;
import com.guidev1911.ecommerce.model.Endereco;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EnderecoMapper {

    @Mapping(target = "usuario", ignore = true)
    Endereco toEntity(EnderecoDTO dto);

    EnderecoDTO toDTO(Endereco entity);

    List<EnderecoDTO> toDTOList(List<Endereco> enderecos);

    void updateEntityFromDto(EnderecoDTO dto, @MappingTarget Endereco entity);
}