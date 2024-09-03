package com.galaxy13.homework;

import java.util.ArrayDeque;
import java.util.Deque;

public class CustomerReverseOrder {

    private final Deque<Customer> customers;

    public CustomerReverseOrder() {
        customers = new ArrayDeque<>();
    }

    public void add(Customer customer) {
        customers.add(customer);
    }

    public Customer take() {
        return customers.removeLast();
    }
}
