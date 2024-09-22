package com.galaxy13.atm;

import com.galaxy13.atm.money.Money;
import com.galaxy13.atm.money.MoneyHandler;
import com.galaxy13.atm.money.MoneyProcessor;

import java.util.List;

public class ATM<T extends Money> {
    private final MoneyHandler<T> moneyHandler;
    private int[] schema = {5, 10, 50, 100, 200, 500, 1000, 2000, 5000};
    private int cellsNumber = 3;

    public ATM() {
        this.moneyHandler = new MoneyProcessor<>(schema, cellsNumber);
    }

    public ATM(int[] schema) {
        this.schema = schema;
        this.moneyHandler = new MoneyProcessor<>(schema, cellsNumber);
    }

    public ATM(int[] schema, int cellsNumber) {
        this.schema = schema;
        this.cellsNumber = cellsNumber;
        this.moneyHandler = new MoneyProcessor<>(schema, cellsNumber);
    }

    public void acceptMoney(List<T> moneyList) {
        for (T money : moneyList) {
            acceptMoney(money);
        }
    }

    public void acceptMoney(T money) {
        moneyHandler.store(money);
    }

    public List<T> withdraw(int amount) {
        return moneyHandler.retreive(amount);
    }

    public int residue() {
        return moneyHandler.getMoneyResidue();
    }
}