package com.galaxy13.crm.dto;

import com.galaxy13.crm.model.Client;
import com.galaxy13.crm.model.Phone;
import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ClientDTO implements Serializable {
    private final Long id;
    private final String name;
    private final String address;
    private final List<String> phones;

    public ClientDTO(Client client) {
        this.id = client.getId();
        this.name = client.getName();
        if (client.getAddress() != null) {
            this.address = client.getAddress().getStreet();
        } else {
            this.address = "";
        }
        if (client.getPhones() != null) {
            this.phones = client.getPhones().stream().map(Phone::getNumber).toList();
        } else {
            this.phones = new ArrayList<>();
        }
    }
}
