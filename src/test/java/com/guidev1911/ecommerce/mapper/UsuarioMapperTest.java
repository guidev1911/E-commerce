package com.guidev1911.ecommerce.mapper;

import com.guidev1911.ecommerce.dto.UserRegisterDTO;
import com.guidev1911.ecommerce.model.Role;
import com.guidev1911.ecommerce.model.Usuario;
import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import java.time.Instant;


class UsuarioMapperTest {

    private UsuarioMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UsuarioMapperImpl();
    }

    @Test
    void deveMapearUserRegisterDtoParaUsuario() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setEmail("guilherme@example.com");
        dto.setSenha("123456");
        dto.setNome("Guilherme");

        Usuario usuario = mapper.toEntity(dto);

        assertNull(usuario.getId());
        assertEquals("guilherme@example.com", usuario.getEmail());
        assertEquals("123456", usuario.getSenha());
        assertEquals("Guilherme", usuario.getNome());

        Set<Role> rolesEsperadas = new HashSet<>();
        rolesEsperadas.add(Role.ROLE_USER);
        assertEquals(rolesEsperadas, usuario.getRoles());

        Instant antes = Instant.now();
        Instant criadoEm = usuario.getCriadoEm();
        Instant depois = Instant.now();

        assertTrue(!criadoEm.isBefore(antes) && !criadoEm.isAfter(depois));

        assertTrue(usuario.getEnderecos().isEmpty());
    }

    @Test
    void defaultRolesDeveRetornarRoleUser() {
        Set<Role> roles = mapper.defaultRoles();
        assertNotNull(roles);
        assertEquals(1, roles.size());
        assertTrue(roles.contains(Role.ROLE_USER));
    }

    @Test
    void nowDeveRetornarInstanteAtual() {
        Instant antes = Instant.now();
        Instant agora = mapper.now();
        Instant depois = Instant.now();

        assertTrue(!agora.isBefore(antes) && !agora.isAfter(depois));
    }
}