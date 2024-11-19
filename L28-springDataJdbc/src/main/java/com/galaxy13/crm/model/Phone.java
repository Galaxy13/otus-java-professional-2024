package com.galaxy13.crm.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "phone")
public class Phone {
    @Id
    private Long phoneId;
    private String number;

    public Phone(String number) {
        this.number = number;
    }
}
