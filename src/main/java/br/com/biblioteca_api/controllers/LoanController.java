package br.com.biblioteca_api.controllers;

import br.com.biblioteca_api.dto.LoanRequest;
import br.com.biblioteca_api.model.Loan;
import br.com.biblioteca_api.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping
    public ResponseEntity<Loan> create(@RequestBody @Valid LoanRequest request) {
        Loan loan = loanService.createLoan(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(loan);
    }

    @PatchMapping("/{id}/return")
    public ResponseEntity<Loan> returnBook(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.returnLoan(id));
    }

    @GetMapping
    public ResponseEntity<List<Loan>> listAll() {
        return ResponseEntity.ok(loanService.listLoans());
    }

    @GetMapping("/late")
    public ResponseEntity<List<Loan>> listLate() {
        return ResponseEntity.ok(loanService.listLateLoans());
    }
}