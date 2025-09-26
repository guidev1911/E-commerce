package com.guidev1911.ecommerce.service;

import com.guidev1911.ecommerce.dto.AuthResponse;
import com.guidev1911.ecommerce.dto.UserRegisterDTO;
import com.guidev1911.ecommerce.dto.UsuarioUpdateDTO;
import com.guidev1911.ecommerce.exception.CredenciaisInvalidasException;
import com.guidev1911.ecommerce.exception.EmailJaRegistradoException;
import com.guidev1911.ecommerce.exception.RefreshTokenException;
import com.guidev1911.ecommerce.exception.UsuarioNaoEncontradoException;
import com.guidev1911.ecommerce.mapper.UsuarioMapper;
import com.guidev1911.ecommerce.model.Endereco;
import com.guidev1911.ecommerce.model.RefreshToken;
import com.guidev1911.ecommerce.model.Usuario;
import com.guidev1911.ecommerce.repository.UsuarioRepository;
import com.guidev1911.ecommerce.security.JwtTokenProvider;
import com.guidev1911.ecommerce.security.RefreshTokenService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final UsuarioMapper usuarioMapper;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          JwtTokenProvider jwtProvider,
                          RefreshTokenService refreshTokenService, UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.refreshTokenService = refreshTokenService;
        this.usuarioMapper = usuarioMapper;
    }

    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado: " + email));
    }

    public Usuario register(UserRegisterDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new EmailJaRegistradoException("Email já registrado");
        }

        Usuario u = usuarioMapper.toEntity(dto);
        u.setSenha(passwordEncoder.encode(dto.getSenha()));

        if (dto.getEndereco() != null) {
            Endereco e = usuarioMapper.toEntity(dto.getEndereco());
            e.setUsuario(u);
            u.getEnderecos().add(e);
        }

        return usuarioRepository.save(u);
    }

    public AuthResponse authenticate(String email, String senha) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(CredenciaisInvalidasException::new);

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new CredenciaisInvalidasException();
        }

        String accessToken = jwtProvider.generateAccessToken(usuario.getEmail());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(usuario);

        return new AuthResponse(accessToken, refreshToken.getToken(), jwtProvider.getAccessTokenExpiryMs());
    }

    public AuthResponse refreshToken(String requestRefreshToken) {
        RefreshToken refreshToken = refreshTokenService.findByToken(requestRefreshToken)
                .orElseThrow(() -> new RefreshTokenException("Refresh token inválido. Faça login novamente."));

        RefreshToken newRefreshToken = refreshTokenService.rotateToken(refreshToken);

        Usuario usuario = newRefreshToken.getUsuario();

        String accessToken = jwtProvider.generateAccessToken(usuario.getEmail());

        return new AuthResponse(accessToken, newRefreshToken.getToken(), jwtProvider.getAccessTokenExpiryMs());
    }
    @Transactional
    public Usuario atualizarUsuario(Long usuarioId, UsuarioUpdateDTO dto) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(usuarioId));

        usuario.setNome(dto.getNome());

        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        }

        return usuarioRepository.save(usuario);
    }

}

