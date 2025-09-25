package com.guidev1911.ecommerce.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.guidev1911.ecommerce.dto.AuthResponse;
import com.guidev1911.ecommerce.dto.EnderecoDTO;
import com.guidev1911.ecommerce.dto.UserRegisterDTO;
import com.guidev1911.ecommerce.exception.CredenciaisInvalidasException;
import com.guidev1911.ecommerce.exception.EmailJaRegistradoException;
import com.guidev1911.ecommerce.exception.RefreshTokenException;
import com.guidev1911.ecommerce.exception.UsuarioNaoEncontradoException;
import com.guidev1911.ecommerce.mapper.UsuarioMapper;
import com.guidev1911.ecommerce.model.RefreshToken;
import com.guidev1911.ecommerce.model.Role;
import com.guidev1911.ecommerce.model.Usuario;
import com.guidev1911.ecommerce.repository.UsuarioRepository;
import com.guidev1911.ecommerce.security.JwtTokenProvider;
import com.guidev1911.ecommerce.security.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider jwtProvider;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("teste@email.com");
        usuario.setNome("Teste");
        usuario.setSenha("encodedPassword");
    }

    @Test
    void deveRetornarUsuarioQuandoEmailExiste() {
        when(usuarioRepository.findByEmail("teste@email.com"))
                .thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.findByEmail("teste@email.com");

        assertNotNull(resultado);
        assertEquals("Teste", resultado.getNome());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        when(usuarioRepository.findByEmail("naoexiste@email.com"))
                .thenReturn(Optional.empty());

        assertThrows(UsuarioNaoEncontradoException.class,
                () -> usuarioService.findByEmail("naoexiste@email.com"));
    }

    @Test
    void deveRegistrarUsuarioComSenhaEncriptada() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setEmail("novo@email.com");
        dto.setNome("Novo");
        dto.setSenha("123456");
        dto.setEndereco(null);

        Usuario mockUsuario = new Usuario();
        mockUsuario.setEmail(dto.getEmail());
        mockUsuario.setSenha("encoded123");
        mockUsuario.getRoles().add(Role.ROLE_USER);

        when(usuarioMapper.toEntity(dto)).thenReturn(mockUsuario);

        when(usuarioRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("encoded123");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario salvo = usuarioService.register(dto);

        assertNotNull(salvo);
        assertEquals("novo@email.com", salvo.getEmail());
        assertEquals("encoded123", salvo.getSenha());
        assertTrue(salvo.getRoles().contains(Role.ROLE_USER));
        assertEquals(0, salvo.getEnderecos().size());
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaRegistrado() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setEmail("existe@email.com");

        when(usuarioRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        assertThrows(EmailJaRegistradoException.class, () -> usuarioService.register(dto));
    }

    @Test
    void deveAutenticarComCredenciaisValidas() {
        when(usuarioRepository.findByEmail("teste@email.com"))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("123456", "encodedPassword")).thenReturn(true);
        when(jwtProvider.generateAccessToken(usuario.getEmail())).thenReturn("fakeAccessToken");
        when(jwtProvider.getAccessTokenExpiryMs()).thenReturn(3600L);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("fakeRefresh");
        refreshToken.setUsuario(usuario);

        when(refreshTokenService.createRefreshToken(usuario)).thenReturn(refreshToken);

        AuthResponse resp = usuarioService.authenticate("teste@email.com", "123456");

        assertNotNull(resp);
        assertEquals("fakeAccessToken", resp.getAccessToken());
        assertEquals("fakeRefresh", resp.getRefreshToken());
    }

    @Test
    void deveLancarExcecaoQuandoSenhaInvalida() {
        when(usuarioRepository.findByEmail("teste@email.com"))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("errada", "encodedPassword")).thenReturn(false);

        assertThrows(CredenciaisInvalidasException.class,
                () -> usuarioService.authenticate("teste@email.com", "errada"));
    }

    @Test
    void deveGerarNovosTokensQuandoRefreshTokenValido() {
        RefreshToken oldToken = new RefreshToken();
        oldToken.setToken("old");
        oldToken.setUsuario(usuario);

        RefreshToken newToken = new RefreshToken();
        newToken.setToken("new");
        newToken.setUsuario(usuario);

        when(refreshTokenService.findByToken("old")).thenReturn(Optional.of(oldToken));
        when(refreshTokenService.rotateToken(oldToken)).thenReturn(newToken);
        when(jwtProvider.generateAccessToken(usuario.getEmail())).thenReturn("newAccess");
        when(jwtProvider.getAccessTokenExpiryMs()).thenReturn(3600L);

        AuthResponse resp = usuarioService.refreshToken("old");

        assertNotNull(resp);
        assertEquals("newAccess", resp.getAccessToken());
        assertEquals("new", resp.getRefreshToken());
    }

    @Test
    void deveLancarExcecaoQuandoRefreshTokenInvalido() {
        when(refreshTokenService.findByToken("invalido")).thenReturn(Optional.empty());

        assertThrows(RefreshTokenException.class,
                () -> usuarioService.refreshToken("invalido"));
    }
}