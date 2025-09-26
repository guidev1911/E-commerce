package com.guidev1911.ecommerce.service;

import static org.junit.jupiter.api.Assertions.*;

import com.guidev1911.ecommerce.dto.EnderecoDTO;
import com.guidev1911.ecommerce.exception.EnderecoNaoEncontradoException;
import com.guidev1911.ecommerce.exception.EnderecoNaoPertenceAoUsuarioException;
import com.guidev1911.ecommerce.exception.UsuarioNaoEncontradoException;
import com.guidev1911.ecommerce.mapper.EnderecoMapper;
import com.guidev1911.ecommerce.model.Endereco;
import com.guidev1911.ecommerce.model.Usuario;
import com.guidev1911.ecommerce.repository.EnderecoRepository;
import com.guidev1911.ecommerce.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnderecoServiceTest {

    @Mock
    private EnderecoRepository enderecoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EnderecoMapper enderecoMapper;

    @InjectMocks
    private EnderecoService enderecoService;

    private Usuario usuario;
    private Endereco endereco;
    private EnderecoDTO enderecoDTO;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Teste");

        endereco = new Endereco();
        endereco.setId(1L);
        endereco.setUsuario(usuario);

        enderecoDTO = new EnderecoDTO();
        enderecoDTO.setLogradouro("Rua X");
        enderecoDTO.setNumero("123");
        enderecoDTO.setBairro("Centro");
        enderecoDTO.setCidade("Cidade Y");
        enderecoDTO.setEstado("Estado Z");
        enderecoDTO.setCep("00000-000");
        enderecoDTO.setPais("Brasil");
        enderecoDTO.setPrincipal(true);

    }

    @Test
    void deveAdicionarEndereco() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(enderecoMapper.toEntity(enderecoDTO)).thenReturn(endereco);
        when(enderecoRepository.save(any(Endereco.class))).thenReturn(endereco);
        when(enderecoMapper.toDTO(endereco)).thenReturn(enderecoDTO);

        EnderecoDTO resultado = enderecoService.adicionarEndereco(1L, enderecoDTO);

        assertNotNull(resultado);
        verify(enderecoRepository).save(endereco);
        verify(enderecoMapper).toDTO(endereco);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontradoAoAdicionar() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNaoEncontradoException.class,
                () -> enderecoService.adicionarEndereco(1L, enderecoDTO));
    }

    @Test
    void deveListarEnderecos() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(enderecoRepository.findByUsuario(usuario)).thenReturn(List.of(endereco));
        when(enderecoMapper.toDTOList(List.of(endereco))).thenReturn(List.of(enderecoDTO));

        List<EnderecoDTO> resultado = enderecoService.listarEnderecos(1L);

        assertEquals(1, resultado.size());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontradoAoListar() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNaoEncontradoException.class,
                () -> enderecoService.listarEnderecos(1L));
    }

    @Test
    void deveAtualizarEndereco() {
        when(enderecoRepository.findById(1L)).thenReturn(Optional.of(endereco));
        when(enderecoRepository.save(endereco)).thenReturn(endereco);
        when(enderecoMapper.toDTO(endereco)).thenReturn(enderecoDTO);

        EnderecoDTO resultado = enderecoService.atualizarEndereco(1L, 1L, enderecoDTO);

        assertNotNull(resultado);
        verify(enderecoMapper).updateEntityFromDto(enderecoDTO, endereco);
    }

    @Test
    void deveLancarExcecaoQuandoEnderecoNaoEncontradoAoAtualizar() {
        when(enderecoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EnderecoNaoEncontradoException.class,
                () -> enderecoService.atualizarEndereco(1L, 1L, enderecoDTO));
    }

    @Test
    void deveLancarExcecaoQuandoEnderecoNaoPertenceAoUsuario() {
        Usuario outroUsuario = new Usuario();
        outroUsuario.setId(2L);
        endereco.setUsuario(outroUsuario);

        when(enderecoRepository.findById(1L)).thenReturn(Optional.of(endereco));

        assertThrows(EnderecoNaoPertenceAoUsuarioException.class,
                () -> enderecoService.atualizarEndereco(1L, 1L, enderecoDTO));
    }

    @Test
    void deveDeletarEndereco() {
        when(enderecoRepository.findById(1L)).thenReturn(Optional.of(endereco));

        enderecoService.deletarEndereco(1L, 1L);

        verify(enderecoRepository).delete(endereco);
    }

    @Test
    void deveLancarExcecaoQuandoEnderecoNaoEncontradoAoDeletar() {
        when(enderecoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EnderecoNaoEncontradoException.class,
                () -> enderecoService.deletarEndereco(1L, 1L));
    }

    @Test
    void deveLancarExcecaoQuandoEnderecoNaoPertenceAoUsuarioAoDeletar() {
        Usuario outroUsuario = new Usuario();
        outroUsuario.setId(2L);
        endereco.setUsuario(outroUsuario);

        when(enderecoRepository.findById(1L)).thenReturn(Optional.of(endereco));

        assertThrows(EnderecoNaoPertenceAoUsuarioException.class,
                () -> enderecoService.deletarEndereco(1L, 1L));
    }
    @Test
    void deveRemoverEnderecoPrincipalAnteriorAoAdicionarNovoPrincipal() {

        Endereco enderecoExistente = new Endereco();
        enderecoExistente.setId(2L);
        enderecoExistente.setUsuario(usuario);
        enderecoExistente.setPrincipal(true);

        List<Endereco> enderecosExistentes = List.of(enderecoExistente);

        when(usuarioRepository.findById(usuario.getId())).thenReturn(java.util.Optional.of(usuario));
        when(enderecoRepository.findByUsuario(usuario)).thenReturn(enderecosExistentes);
        when(enderecoMapper.toEntity(enderecoDTO)).thenReturn(endereco);
        when(enderecoRepository.save(endereco)).thenReturn(endereco);
        when(enderecoMapper.toDTO(endereco)).thenReturn(enderecoDTO);

        EnderecoDTO resultado = enderecoService.adicionarEndereco(usuario.getId(), enderecoDTO);

        assertFalse(enderecoExistente.isPrincipal());

        verify(enderecoRepository, atLeastOnce()).saveAll(enderecosExistentes);

        assertEquals(enderecoDTO, resultado);
    }
}