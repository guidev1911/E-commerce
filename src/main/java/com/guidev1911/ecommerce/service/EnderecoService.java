package com.guidev1911.ecommerce.service;

import com.guidev1911.ecommerce.dto.EnderecoDTO;
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
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        Endereco endereco = enderecoMapper.toEntity(dto);
        endereco.setUsuario(usuario);

        Endereco salvo = enderecoRepository.save(endereco);
        return enderecoMapper.toDTO(salvo);
    }

    @Transactional(readOnly = true)
    public List<EnderecoDTO> listarEnderecos(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        return enderecoMapper.toDTOList(enderecoRepository.findByUsuario(usuario));
    }

    @Transactional
    public EnderecoDTO atualizarEndereco(Long usuarioId, Long enderecoId, EnderecoDTO dto) {
        Endereco endereco = enderecoRepository.findById(enderecoId)
                .orElseThrow(() -> new EntityNotFoundException("Endereço não encontrado"));

        if (!endereco.getUsuario().getId().equals(usuarioId)) {
            throw new IllegalArgumentException("Endereço não pertence ao usuário informado");
        }

        endereco.setLogradouro(dto.getLogradouro());
        endereco.setNumero(dto.getNumero());
        endereco.setComplemento(dto.getComplemento());
        endereco.setBairro(dto.getBairro());
        endereco.setCidade(dto.getCidade());
        endereco.setEstado(dto.getEstado());
        endereco.setCep(dto.getCep());
        endereco.setPais(dto.getPais());
        endereco.setPrincipal(dto.isPrincipal());

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
}