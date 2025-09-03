package com.guidev1911.ecommerce.repository;

import com.guidev1911.ecommerce.model.Carrinho;
import com.guidev1911.ecommerce.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarrinhoRepository extends JpaRepository<Carrinho, Long> {
        Optional<Carrinho> findByUsuario(Usuario usuario);
}


