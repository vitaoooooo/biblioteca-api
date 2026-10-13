package br.com.biblioteca_api.repository;

import br.com.biblioteca_api.model.ReturnedLoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReturnedLoanRepository extends JpaRepository<ReturnedLoan, Long> {
    Page<ReturnedLoan> findAll(Pageable pageable);
}
