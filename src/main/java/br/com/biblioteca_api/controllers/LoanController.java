package br.com.biblioteca_api.controllers;

import br.com.biblioteca_api.dto.LoanRequest;
import br.com.biblioteca_api.dto.LoanResponse;
import br.com.biblioteca_api.dto.ReturnedLoanResponse;
import br.com.biblioteca_api.dto.PageResponse;
import br.com.biblioteca_api.model.Loan;
import br.com.biblioteca_api.model.ReturnedLoan;
import br.com.biblioteca_api.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.time.Clock;
import java.time.LocalDate;
import org.springframework.data.domain.PageRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api/loans")
@Validated
public class LoanController {

    private final LoanService loanService;
    private final Clock clock;

    public LoanController(LoanService loanService, Clock clock) {
        this.loanService = loanService;
        this.clock = clock;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<LoanResponse> create(@RequestBody @Valid LoanRequest request) {
        Loan loan = loanService.createLoan(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(LoanResponse.from(loan, today()));
    }

    @PatchMapping("/{id}/return")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<LoanResponse> returnBook(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(LoanResponse.from(loanService.returnLoan(id), today()));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<LoanResponse>> listAll() {
        return ResponseEntity.ok(loanService.listLoans().stream().map(loan -> LoanResponse.from(loan, today())).toList());
    }

    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public PageResponse<LoanResponse> listPage(@RequestParam(defaultValue = "0") @Min(0) int page,
                                               @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return PageResponse.from(loanService.listLoanPage(PageRequest.of(page, size)), loan -> LoanResponse.from(loan, today()));
    }

    @GetMapping("/late")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<LoanResponse>> listLate() {
        return ResponseEntity.ok(loanService.listLateLoans().stream().map(loan -> LoanResponse.from(loan, today())).toList());
    }

    @GetMapping("/returned")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<ReturnedLoanResponse>> listReturned() {
        return ResponseEntity.ok(loanService.listReturnedLoans().stream().map(ReturnedLoanResponse::from).toList());
    }

    @GetMapping("/returned/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public PageResponse<ReturnedLoanResponse> listReturnedPage(@RequestParam(defaultValue = "0") @Min(0) int page,
                                                               @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return PageResponse.from(loanService.listReturnedLoanPage(PageRequest.of(page, size)), ReturnedLoanResponse::from);
    }

    private LocalDate today() { return LocalDate.now(clock); }
}
