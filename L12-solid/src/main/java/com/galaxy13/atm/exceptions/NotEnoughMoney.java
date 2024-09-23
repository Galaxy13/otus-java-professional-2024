package com.galaxy13.atm.exceptions;

public class NotEnoughMoney extends RuntimeException {
    public NotEnoughMoney(int currentAmount) {
        super("ATM doesn't have enough money. Current amount" + currentAmount);
    }
}
