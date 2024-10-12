package ru.otus.crm.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "phone")
public class Phone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "phone_id", updatable = false, nullable = false, unique = true)
    private Long id;

    @Column(name = "number")
    private String number;


    public static Phone clonePhone(Phone phone) {
        if (phone == null) return null;
        return new Phone(phone.getId(), phone.getNumber());
    }
}
