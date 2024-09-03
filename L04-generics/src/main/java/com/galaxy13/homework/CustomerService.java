package com.galaxy13.homework;

import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class CustomerService {

    private final NavigableMap<Customer, String> serviceMap;

    public CustomerService() {
        serviceMap = new TreeMap<>(
                Comparator.comparingLong(Customer::getScores)
        );
    }

    public Map.Entry<Customer, String> getSmallest() {
        Map.Entry<Customer, String> smallestEntry = serviceMap.firstEntry();
        if (smallestEntry != null) {
            Customer smallestCustomer = smallestEntry.getKey();
            return Map.entry(new Customer(smallestCustomer), smallestEntry.getValue());
        }
        return null;
    }

    public Map.Entry<Customer, String> getNext(Customer customer) {
        Map.Entry<Customer, String> nextEntry = serviceMap.higherEntry(customer);
        if (nextEntry != null) {
            Customer nextCustomer = nextEntry.getKey();
            return Map.entry(new Customer(nextCustomer), nextEntry.getValue());
        }
        return null;
    }

    public void add(Customer customer, String data) {
        serviceMap.put(customer, data);
    }
}
