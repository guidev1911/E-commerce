package com.guidev1911.ecommerce.mapper;

import com.guidev1911.ecommerce.dto.ProdutoDTO;
import com.guidev1911.ecommerce.model.Categoria;
import com.guidev1911.ecommerce.model.Produto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import java.util.List;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProdutoMapper {

    ProdutoMapper INSTANCE = Mappers.getMapper(ProdutoMapper.class);

    @Mapping(source = "categoria.id", target = "categoriaId")
    ProdutoDTO toDTO(Produto produto);

    @Mapping(source = "categoriaId", target = "categoria")
    Produto toEntity(ProdutoDTO produtoDTO);

    List<ProdutoDTO> toDTOList(List<Produto> produtos);

    @Mapping(source = "categoriaId", target = "categoria")
    void updateEntityFromDTO(ProdutoDTO produtoDTO, @MappingTarget Produto produto);

    default Categoria map(Long categoriaId) {
        if (categoriaId == null) return null;
        Categoria categoria = new Categoria();
        categoria.setId(categoriaId);
        return categoria;
    }
}