package com.guidev1911.ecommerce.repository;

import com.guidev1911.ecommerce.model.Endereco;
import com.guidev1911.ecommerce.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
    List<Endereco> findByUsuario(Usuario usuario);
}