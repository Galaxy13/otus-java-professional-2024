package com.galaxy13.atm.exceptions;

import com.galaxy13.atm.money.Money;

public class UnsupportedMoneyValue extends RuntimeException {
    public UnsupportedMoneyValue(Money money) {
        super("Money value not supported: " + money);
    }
}
