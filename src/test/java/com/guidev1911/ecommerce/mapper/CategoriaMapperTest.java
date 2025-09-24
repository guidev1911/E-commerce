package com.guidev1911.ecommerce.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.guidev1911.ecommerce.dto.CategoriaDTO;
import com.guidev1911.ecommerce.model.Categoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class CategoriaMapperTest {

    private CategoriaMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(CategoriaMapper.class);
    }

    @Test
    void testToDTO() {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Eletrônicos");
        categoria.setDescricao("Produtos eletrônicos variados");

        CategoriaDTO dto = mapper.toDTO(categoria);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(categoria.getId());
        assertThat(dto.getNome()).isEqualTo(categoria.getNome());
        assertThat(dto.getDescricao()).isEqualTo(categoria.getDescricao());
    }

    @Test
    void testToEntity() {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(2L);
        dto.setNome("Roupas");
        dto.setDescricao("Roupas de todos os tipos");

        Categoria entity = mapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(dto.getId());
        assertThat(entity.getNome()).isEqualTo(dto.getNome());
        assertThat(entity.getDescricao()).isEqualTo(dto.getDescricao());
    }

    @Test
    void testUpdateFromDto() {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setNome("Livros");
        dto.setDescricao("Todos os tipos de livros");

        Categoria entity = new Categoria();
        entity.setId(5L);
        entity.setNome("Antigo");
        entity.setDescricao("Descrição antiga");

        mapper.updateFromDto(dto, entity);

        assertThat(entity.getId()).isEqualTo(5L);
        assertThat(entity.getNome()).isEqualTo(dto.getNome());
        assertThat(entity.getDescricao()).isEqualTo(dto.getDescricao());
    }
}