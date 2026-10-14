package br.com.biblioteca_api.repository;

import br.com.biblioteca_api.model.ReturnedLoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReturnedLoanRepository extends JpaRepository<ReturnedLoan, Long> {

    List<ReturnedLoan> findByClientEmailIgnoreCase(String clientEmail);

    Page<ReturnedLoan> findAll(Pageable pageable);

}