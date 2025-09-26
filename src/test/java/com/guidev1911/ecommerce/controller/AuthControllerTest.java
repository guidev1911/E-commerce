package com.guidev1911.ecommerce.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.guidev1911.ecommerce.dto.*;
import com.guidev1911.ecommerce.exception.UsuarioNaoEncontradoException;
import com.guidev1911.ecommerce.model.Usuario;
import com.guidev1911.ecommerce.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import java.util.Map;
import static org.mockito.Mockito.*;
import org.springframework.http.ResponseEntity;

class AuthControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveRegistrarUsuario() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setEmail("teste@email.com");
        dto.setNome("Teste");
        dto.setSenha("123456");

        Usuario usuarioSalvo = new Usuario();
        usuarioSalvo.setId(1L);
        usuarioSalvo.setEmail(dto.getEmail());

        when(usuarioService.register(dto)).thenReturn(usuarioSalvo);

        ResponseEntity<?> response = authController.register(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals(1L, body.get("id"));
        assertEquals("teste@email.com", body.get("email"));

        verify(usuarioService).register(dto);
    }

    @Test
    void deveLogarUsuario() {
        AuthRequest req = new AuthRequest();
        req.setEmail("teste@email.com");
        req.setSenha("123456");

        AuthResponse authResponse = new AuthResponse();
        authResponse.setAccessToken("token123");
        authResponse.setRefreshToken("refresh123");
        authResponse.setExpiresIn(3600L);

        when(usuarioService.authenticate(req.getEmail(), req.getSenha())).thenReturn(authResponse);

        ResponseEntity<AuthResponse> response = authController.login(req);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("token123", response.getBody().getAccessToken());

        verify(usuarioService).authenticate(req.getEmail(), req.getSenha());
    }

    @Test
    void deveAtualizarTokenComRefresh() {
        Map<String, String> body = Map.of("refreshToken", "refresh123");
        AuthResponse newTokens = new AuthResponse();
        newTokens.setAccessToken("novoToken");
        newTokens.setRefreshToken("refresh123");
        newTokens.setExpiresIn(3600L);

        when(usuarioService.refreshToken("refresh123")).thenReturn(newTokens);

        ResponseEntity<AuthResponse> response = authController.refresh(body);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("novoToken", response.getBody().getAccessToken());

        verify(usuarioService).refreshToken("refresh123");
    }

    @Test
    void deveRetornarDadosDoUsuarioLogado() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("teste@email.com");

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Teste");
        usuario.setEmail("teste@email.com");

        when(usuarioService.findByEmail("teste@email.com")).thenReturn(usuario);

        ResponseEntity<UsuarioDTO> response = authController.me(auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Teste", response.getBody().getNome());
        assertEquals("teste@email.com", response.getBody().getEmail());

        verify(usuarioService).findByEmail("teste@email.com");
    }
    @Test
    void deveAtualizarNomeDoUsuario() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("teste@email.com");

        UsuarioUpdateDTO dto = new UsuarioUpdateDTO();
        dto.setNome("Nome Atualizado");
        dto.setSenha(null);

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Teste");
        usuario.setEmail("teste@email.com");

        Usuario usuarioAtualizado = new Usuario();
        usuarioAtualizado.setId(1L);
        usuarioAtualizado.setNome("Nome Atualizado");
        usuarioAtualizado.setEmail("teste@email.com");

        when(usuarioService.findByEmail("teste@email.com")).thenReturn(usuario);
        when(usuarioService.atualizarUsuario(usuario.getId(), dto)).thenReturn(usuarioAtualizado);

        ResponseEntity<UsuarioDTO> response = authController.atualizarMe(dto, auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Nome Atualizado", response.getBody().getNome());
        assertEquals("teste@email.com", response.getBody().getEmail());

        verify(usuarioService).findByEmail("teste@email.com");
        verify(usuarioService).atualizarUsuario(usuario.getId(), dto);
    }

    @Test
    void deveAtualizarNomeESenhaDoUsuario() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("teste@email.com");

        UsuarioUpdateDTO dto = new UsuarioUpdateDTO();
        dto.setNome("Nome Novo");
        dto.setSenha("novaSenha");

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Teste");
        usuario.setEmail("teste@email.com");

        Usuario usuarioAtualizado = new Usuario();
        usuarioAtualizado.setId(1L);
        usuarioAtualizado.setNome("Nome Novo");
        usuarioAtualizado.setEmail("teste@email.com");

        when(usuarioService.findByEmail("teste@email.com")).thenReturn(usuario);
        when(usuarioService.atualizarUsuario(usuario.getId(), dto)).thenReturn(usuarioAtualizado);

        ResponseEntity<UsuarioDTO> response = authController.atualizarMe(dto, auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Nome Novo", response.getBody().getNome());
        assertEquals("teste@email.com", response.getBody().getEmail());

        verify(usuarioService).findByEmail("teste@email.com");
        verify(usuarioService).atualizarUsuario(usuario.getId(), dto);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExistirAoAtualizar() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("naoexiste@email.com");

        UsuarioUpdateDTO dto = new UsuarioUpdateDTO();
        dto.setNome("Qualquer Nome");

        when(usuarioService.findByEmail("naoexiste@email.com"))
                .thenThrow(new UsuarioNaoEncontradoException("Usuario não encontrado"));

        assertThrows(UsuarioNaoEncontradoException.class,
                () -> authController.atualizarMe(dto, auth));

        verify(usuarioService).findByEmail("naoexiste@email.com");
    }
}