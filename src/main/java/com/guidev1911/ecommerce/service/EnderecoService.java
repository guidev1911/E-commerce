package com.guidev1911.ecommerce.service;

import com.guidev1911.ecommerce.dto.EnderecoDTO;
import com.guidev1911.ecommerce.exception.EnderecoNaoEncontradoException;
import com.guidev1911.ecommerce.exception.EnderecoNaoPertenceAoUsuarioException;
import com.guidev1911.ecommerce.exception.UsuarioNaoEncontradoException;
import com.guidev1911.ecommerce.mapper.EnderecoMapper;
import com.guidev1911.ecommerce.model.Endereco;
import com.guidev1911.ecommerce.model.Usuario;
import com.guidev1911.ecommerce.repository.EnderecoRepository;
import com.guidev1911.ecommerce.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EnderecoMapper enderecoMapper;

    public EnderecoService(EnderecoRepository enderecoRepository,
                           UsuarioRepository usuarioRepository,
                           EnderecoMapper enderecoMapper) {
        this.enderecoRepository = enderecoRepository;
        this.usuarioRepository = usuarioRepository;
        this.enderecoMapper = enderecoMapper;
    }
    @Transactional
    public EnderecoDTO adicionarEndereco(Long usuarioId, EnderecoDTO dto) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(usuarioId));

        Endereco endereco = enderecoMapper.toEntity(dto);
        endereco.setUsuario(usuario);

        if (dto.isPrincipal()) {
            removerPrincipais(usuario);
        }

        Endereco salvo = enderecoRepository.save(endereco);
        return enderecoMapper.toDTO(salvo);
    }

    @Transactional(readOnly = true)
    public List<EnderecoDTO> listarEnderecos(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(usuarioId));

        return enderecoMapper.toDTOList(enderecoRepository.findByUsuario(usuario));
    }

    @Transactional
    public EnderecoDTO atualizarEndereco(Long usuarioId, Long enderecoId, EnderecoDTO dto) {
        Endereco endereco = enderecoRepository.findById(enderecoId)
                .orElseThrow(() -> new EnderecoNaoEncontradoException(enderecoId));

        if (!endereco.getUsuario().getId().equals(usuarioId)) {
            throw new EnderecoNaoPertenceAoUsuarioException(enderecoId, usuarioId);
        }

        if (dto.isPrincipal()) {
            removerPrincipais(endereco.getUsuario());
        }

        enderecoMapper.updateEntityFromDto(dto, endereco);

        return enderecoMapper.toDTO(enderecoRepository.save(endereco));
    }

    @Transactional
    public void deletarEndereco(Long usuarioId, Long enderecoId) {
        Endereco endereco = enderecoRepository.findById(enderecoId)
                .orElseThrow(() -> new EntityNotFoundException("Endereço não encontrado"));

        if (!endereco.getUsuario().getId().equals(usuarioId)) {
            throw new IllegalArgumentException("Endereço não pertence ao usuário informado");
        }

        enderecoRepository.delete(endereco);
    }

    private void removerPrincipais(Usuario usuario) {
        List<Endereco> enderecos = enderecoRepository.findByUsuario(usuario);
        for (Endereco e : enderecos) {
            if (e.isPrincipal()) {
                e.setPrincipal(false);
            }
        }
        enderecoRepository.saveAll(enderecos);
    }
}
