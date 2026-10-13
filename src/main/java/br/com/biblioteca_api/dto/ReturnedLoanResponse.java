package br.com.biblioteca_api.dto;

import br.com.biblioteca_api.model.ReturnedLoan;
import java.time.LocalDate;

public record ReturnedLoanResponse(Long id, String clientName, String bookName, String bookAuthor,
                                   LocalDate loanDate, LocalDate dueDate, LocalDate returnedDate) {
    public static ReturnedLoanResponse from(ReturnedLoan loan) {
        return new ReturnedLoanResponse(loan.getId(), loan.getClientName(), loan.getBookName(), loan.getBookAuthor(),
                loan.getLoanDate(), loan.getDueDate(), loan.getReturnedDate());
    }
}
