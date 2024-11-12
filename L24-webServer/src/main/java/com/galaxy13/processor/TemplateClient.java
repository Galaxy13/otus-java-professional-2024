package com.galaxy13.processor;

import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;

import java.util.Optional;

public class TemplateClient {
    private final Long id;
    private final String name;
    private final String address;
    private final String phone;

    public TemplateClient(Client client) {
        this.id = client.getId();
        this.name = client.getName();
        this.address = Optional.ofNullable(client.getAddress()).orElse(new Address()).getStreet();
        this.phone = Optional.ofNullable(client.getPhones().getFirst()).orElse(new Phone()).getNumber();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }
}
