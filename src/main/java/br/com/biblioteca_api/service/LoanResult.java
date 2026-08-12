package br.com.biblioteca_api.service;

public class LoanResult {
    private boolean sucesso;
    private String mensagem;

    public LoanResult(boolean sucesso, String mensagem) {
        this.sucesso = sucesso;
        this.mensagem = mensagem;
    }

    public boolean isSucesso() {
        return sucesso;
    }

    public String getMensagem() {
        return mensagem;
    }
}
