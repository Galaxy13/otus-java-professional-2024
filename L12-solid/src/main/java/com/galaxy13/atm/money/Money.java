package com.galaxy13.atm.money;

public abstract class Money {
    private final int value;

    protected Money(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
