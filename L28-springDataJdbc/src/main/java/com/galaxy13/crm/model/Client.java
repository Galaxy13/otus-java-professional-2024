package com.galaxy13.crm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.lang.Nullable;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class Client {
    @Id
    private Long id;
    private String name;

    @Nullable
    @JsonProperty("address")
    @MappedCollection(idColumn = "address_id")
    private Address address;

    @JsonProperty("phones")
    @MappedCollection(idColumn = "client_id")
    private Set<Phone> phones;
}

