package com.guidev1911.ecommerce.mapper;

import com.guidev1911.ecommerce.dto.EnderecoDTO;
import com.guidev1911.ecommerce.model.Endereco;
import com.guidev1911.ecommerce.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EnderecoMapperTest {

    private EnderecoMapper mapper = Mappers.getMapper(EnderecoMapper.class);

    private Endereco endereco;
    private EnderecoDTO enderecoDTO;
    private Usuario usuario;

    @BeforeEach
    void setup() {
        usuario = new Usuario();
        usuario.setId(1L);

        endereco = new Endereco();
        endereco.setId(10L);
        endereco.setUsuario(usuario);
        endereco.setLogradouro("Rua A");
        endereco.setNumero("123");
        endereco.setComplemento("Apto 1");
        endereco.setBairro("Centro");
        endereco.setCidade("Cidade X");
        endereco.setEstado("Estado Y");
        endereco.setCep("00000-000");
        endereco.setPais("Brasil");
        endereco.setPrincipal(true);

        enderecoDTO = new EnderecoDTO();
        enderecoDTO.setLogradouro("Rua B");
        enderecoDTO.setNumero("456");
        enderecoDTO.setComplemento("Apto 2");
        enderecoDTO.setBairro("Bairro Z");
        enderecoDTO.setCidade("Cidade W");
        enderecoDTO.setEstado("Estado V");
        enderecoDTO.setCep("11111-111");
        enderecoDTO.setPais("Brasil");
        enderecoDTO.setPrincipal(false);
    }

    @Test
    void deveMapearDTOParaEntidade() {
        Endereco entity = mapper.toEntity(enderecoDTO);

        assertThat(entity).isNotNull();
        assertThat(entity.getLogradouro()).isEqualTo(enderecoDTO.getLogradouro());
        assertThat(entity.getNumero()).isEqualTo(enderecoDTO.getNumero());
        assertThat(entity.getComplemento()).isEqualTo(enderecoDTO.getComplemento());
        assertThat(entity.getBairro()).isEqualTo(enderecoDTO.getBairro());
        assertThat(entity.getCidade()).isEqualTo(enderecoDTO.getCidade());
        assertThat(entity.getEstado()).isEqualTo(enderecoDTO.getEstado());
        assertThat(entity.getCep()).isEqualTo(enderecoDTO.getCep());
        assertThat(entity.getPais()).isEqualTo(enderecoDTO.getPais());
        assertThat(entity.isPrincipal()).isEqualTo(enderecoDTO.isPrincipal());
        assertThat(entity.getUsuario()).isNull();
    }

    @Test
    void deveMapearEntidadeParaDTO() {
        EnderecoDTO dto = mapper.toDTO(endereco);

        assertThat(dto).isNotNull();
        assertThat(dto.getLogradouro()).isEqualTo(endereco.getLogradouro());
        assertThat(dto.getNumero()).isEqualTo(endereco.getNumero());
        assertThat(dto.getComplemento()).isEqualTo(endereco.getComplemento());
        assertThat(dto.getBairro()).isEqualTo(endereco.getBairro());
        assertThat(dto.getCidade()).isEqualTo(endereco.getCidade());
        assertThat(dto.getEstado()).isEqualTo(endereco.getEstado());
        assertThat(dto.getCep()).isEqualTo(endereco.getCep());
        assertThat(dto.getPais()).isEqualTo(endereco.getPais());
        assertThat(dto.isPrincipal()).isEqualTo(endereco.isPrincipal());
    }

    @Test
    void deveMapearListaDeEntidadesParaDTOs() {
        List<EnderecoDTO> dtos = mapper.toDTOList(List.of(endereco));

        assertThat(dtos).hasSize(1);
        assertThat(dtos.getFirst().getLogradouro()).isEqualTo(endereco.getLogradouro());
    }

    @Test
    void deveAtualizarEntidadeComDTO() {
        mapper.updateEntityFromDto(enderecoDTO, endereco);

        assertThat(endereco.getLogradouro()).isEqualTo(enderecoDTO.getLogradouro());
        assertThat(endereco.getNumero()).isEqualTo(enderecoDTO.getNumero());
        assertThat(endereco.getComplemento()).isEqualTo(enderecoDTO.getComplemento());
        assertThat(endereco.getBairro()).isEqualTo(enderecoDTO.getBairro());
        assertThat(endereco.getCidade()).isEqualTo(enderecoDTO.getCidade());
        assertThat(endereco.getEstado()).isEqualTo(enderecoDTO.getEstado());
        assertThat(endereco.getCep()).isEqualTo(enderecoDTO.getCep());
        assertThat(endereco.getPais()).isEqualTo(enderecoDTO.getPais());
        assertThat(endereco.isPrincipal()).isEqualTo(enderecoDTO.isPrincipal());
    }
}