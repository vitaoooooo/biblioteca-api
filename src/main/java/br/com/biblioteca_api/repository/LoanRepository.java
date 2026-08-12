package br.com.biblioteca_api.repository;

import br.com.biblioteca_api.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByReturnedFalseAndDueDateBefore(LocalDate date);

    boolean existsByClientId(Long clientId);
}
