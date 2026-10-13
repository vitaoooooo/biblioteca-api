package br.com.biblioteca_api.dto;

import br.com.biblioteca_api.model.Loan;
import java.time.LocalDate;

public record LoanResponse(Long id, ClientReference client, BookReference book, LocalDate loanDate,
                           LocalDate dueDate, LocalDate returnedDate, boolean late) {
    public static LoanResponse from(Loan loan, LocalDate currentDate) {
        return new LoanResponse(loan.getId(), new ClientReference(loan.getClient().getId(), loan.getClient().getName()),
                new BookReference(loan.getBook().getId(), loan.getBook().getName(), loan.getBook().getAuthor()),
                loan.getLoanDate(), loan.getDueDate(), loan.getReturnedDate(), loan.isLate(currentDate));
    }
    public record ClientReference(Long id, String name) { }
    public record BookReference(Long id, String name, String author) { }
}
