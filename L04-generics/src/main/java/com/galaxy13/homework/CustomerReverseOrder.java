package com.galaxy13.homework;

import java.util.LinkedList;
import java.util.List;

public class CustomerReverseOrder {

    private final List<Customer> customers;

    public CustomerReverseOrder() {
        customers = new LinkedList<>();
    }

    public void add(Customer customer) {
        customers.add(customer);
    }

    public Customer take() {
        return customers.removeLast();
    }
}
