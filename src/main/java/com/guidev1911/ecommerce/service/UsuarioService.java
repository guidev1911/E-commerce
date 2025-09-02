package com.guidev1911.ecommerce.service;

import com.guidev1911.ecommerce.dto.AuthResponse;
import com.guidev1911.ecommerce.dto.UserRegisterDTO;
import com.guidev1911.ecommerce.exception.CredenciaisInvalidasException;
import com.guidev1911.ecommerce.exception.EmailJaRegistradoException;
import com.guidev1911.ecommerce.exception.RefreshTokenException;
import com.guidev1911.ecommerce.exception.UsuarioNaoEncontradoException;
import com.guidev1911.ecommerce.model.RefreshToken;
import com.guidev1911.ecommerce.model.Role;
import com.guidev1911.ecommerce.model.Usuario;
import com.guidev1911.ecommerce.repository.UsuarioRepository;
import com.guidev1911.ecommerce.security.JwtTokenProvider;
import com.guidev1911.ecommerce.security.RefreshTokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          JwtTokenProvider jwtProvider,
                          RefreshTokenService refreshTokenService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.refreshTokenService = refreshTokenService;
    }

    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado: " + email));
    }

    public Usuario register(UserRegisterDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new EmailJaRegistradoException("Email já registrado");
        }
        Usuario u = new Usuario();
        u.setEmail(dto.getEmail());
        u.setNome(dto.getNome());
        u.setSenha(passwordEncoder.encode(dto.getSenha()));
        u.getRoles().add(Role.ROLE_USER);
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

}

