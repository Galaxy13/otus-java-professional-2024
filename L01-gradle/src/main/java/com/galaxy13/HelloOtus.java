package com.galaxy13;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;

import java.util.Map;

@SuppressWarnings("java:S106")
public class HelloOtus {
    public static void main(String[] args) {
        ImmutableMap<Integer, String> map = ImmutableSortedMap.of(10, "value1", 2, "value2", 7, "value3");
        System.out.println("Ordering by key:");
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
    }
}