package com.galaxy13.crm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;

import java.util.List;

@NoArgsConstructor
@Setter
@Getter
@ToString
public class Client {
    @Id
    private Long id;
    private String name;

    @JsonProperty("address")
    @MappedCollection(idColumn = "address_id")
    private Address address;

    @JsonProperty("phone")
    @MappedCollection(idColumn = "client_id")
//    @JsonDeserialize(using = PhoneDeserializer.class)
    private List<Phone> phones;
}

