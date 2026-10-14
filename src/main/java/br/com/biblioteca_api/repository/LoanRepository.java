    package br.com.biblioteca_api.repository;

    import br.com.biblioteca_api.model.Loan;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.Lock;
    import jakarta.persistence.LockModeType;
    import org.springframework.data.repository.query.Param;

    import java.time.LocalDate;
    import java.util.List;
    import java.util.Optional;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;

    public interface LoanRepository extends JpaRepository<Loan, Long> {

        List<Loan> findByReturnedFalseAndDueDateBefore(LocalDate date);

        boolean existsByBookIdAndReturnedFalse(Long bookId);

        boolean existsByClientIdAndReturnedFalse(Long clientId);

        List<Loan> findByBookIdAndReturnedTrue(Long bookId);

        List<Loan> findByClientIdAndReturnedTrue(Long clientId);

        long countByBookIdAndReturnedFalse(Long bookId);

        List<Loan> findByReturnedFalse();
        List<Loan> findByClientIdAndReturnedFalse(Long clientId);
        Page<Loan> findByReturnedFalse(Pageable pageable);

        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @org.springframework.data.jpa.repository.Query("select l from Loan l where l.id = :id")
        Optional<Loan> findByIdForUpdate(@Param("id") Long id);
    }
