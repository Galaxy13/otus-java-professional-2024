package ru.otus.crm.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "client")
public class Client implements Cloneable {

    @Id
    @SequenceGenerator(name = "client_gen", sequenceName = "client_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_gen")
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "address_id")
    private Address address;

    @Fetch(FetchMode.SUBSELECT)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "client_id", updatable = false, nullable = false)
    private List<Phone> phones;

    public Client() {
        this.phones = new ArrayList<>();
    }

    public Client(String name) {
        this.id = null;
        this.name = name;
        this.phones = new ArrayList<>();
    }

    public Client(Long id, String name, List<Phone> phones) {
        this.id = id;
        this.name = name;
        this.phones = phones;
    }

    @Override
    @SuppressWarnings({"java:S2975", "java:S1182"})
    public Client clone() {
        List<Phone> clonedPhoneList;
        if (this.phones == null) {
            clonedPhoneList = null;
        } else {
            clonedPhoneList = this.phones.stream().map(Phone::clonePhone).toList();
        }
        return new Client(this.id,
                this.name,
                Address.cloneAddress(this.address),
                clonedPhoneList);
    }

    @Override
    public String toString() {
        return "Client{" + "id=" + id + ", name='" + name + '\'' + '}';
    }
}
