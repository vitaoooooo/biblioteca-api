package br.com.biblioteca_api.dto;

import br.com.biblioteca_api.model.Client;

public record ClientResponse(Long id, String name, String email, String phoneNumber, boolean active) {
    public static ClientResponse from(Client client) {
        return new ClientResponse(client.getId(), client.getName(), client.getEmail(), client.getPhoneNumber(), client.isActive());
    }
}
