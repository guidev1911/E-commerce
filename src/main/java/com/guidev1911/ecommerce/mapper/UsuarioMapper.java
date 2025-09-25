package com.guidev1911.ecommerce.mapper;

import com.guidev1911.ecommerce.dto.EnderecoDTO;
import com.guidev1911.ecommerce.dto.UserRegisterDTO;
import com.guidev1911.ecommerce.model.Endereco;
import com.guidev1911.ecommerce.model.Role;
import com.guidev1911.ecommerce.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.HashSet;
import java.time.Instant;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", expression = "java(defaultRoles())")
    @Mapping(target = "criadoEm", expression = "java(now())")
    @Mapping(target = "enderecos", ignore = true)
    Usuario toEntity(UserRegisterDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    Endereco toEntity(EnderecoDTO dto);

    default HashSet<Role> defaultRoles() {
        return new HashSet<>(Collections.singleton(Role.ROLE_USER));
    }

    default Instant now() {
        return Instant.now();
    }
}