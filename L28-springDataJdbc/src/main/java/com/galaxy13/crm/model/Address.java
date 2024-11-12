package com.galaxy13.crm.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "address")
public record Address(@Id Long addressId, String street) {
}
