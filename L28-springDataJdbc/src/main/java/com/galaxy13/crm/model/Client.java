package com.galaxy13.crm.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;

import java.util.Set;

@NoArgsConstructor
@Setter
@Getter
@ToString
public class Client {
    @Id
    private Long id;
    private String name;

    @MappedCollection(idColumn = "address_id")
    private Address address;

    @MappedCollection(idColumn = "client_id")
    private Set<Phone> phones;
}

