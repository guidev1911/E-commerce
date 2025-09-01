package com.guidev1911.ecommerce.repository;

import com.guidev1911.ecommerce.model.Carrinho;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarrinhoRepository extends JpaRepository<Carrinho, Long> {
}
