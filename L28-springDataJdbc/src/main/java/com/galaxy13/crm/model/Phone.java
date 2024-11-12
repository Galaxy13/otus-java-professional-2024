package com.galaxy13.crm.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "phone")
public record Phone(@Id Long phoneId, String number) {
}
