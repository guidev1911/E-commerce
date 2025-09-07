package com.guidev1911.ecommerce.repository;


import com.guidev1911.ecommerce.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
}