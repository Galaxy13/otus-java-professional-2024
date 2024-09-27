package com.galaxy13.atm.exceptions;

import java.util.List;

public class UnreachableAmountException extends RuntimeException {
    public UnreachableAmountException(List<Integer> values, int targetAmount) {
        super("ATM doesn't have enough appropriate banknotes to withdrawal "
                + targetAmount + "\n\rCurrent ATM deposit: " + values);
    }
}
