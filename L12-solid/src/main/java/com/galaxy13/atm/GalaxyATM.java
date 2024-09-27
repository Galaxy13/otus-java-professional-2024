package com.galaxy13.atm;

import com.galaxy13.atm.money.Money;
import com.galaxy13.atm.money.MoneyHandler;

import java.util.List;

public class GalaxyATM implements ATM {
    private final MoneyHandler moneyHandler;

    private GalaxyATM(MoneyHandler moneyHandler) {
        this.moneyHandler = moneyHandler;
    }

    public static ATM withHandler(MoneyHandler moneyHandler) {
        return new GalaxyATM(moneyHandler);
    }

    @Override
    public void acceptMoney(List<Money> moneyList) {
        for (Money money : moneyList) {
            acceptMoney(money);
        }
    }

    @Override
    public void acceptMoney(Money money) {
        moneyHandler.store(money);
    }

    @Override
    public List<Money> withdraw(int amount) {
        return moneyHandler.retreive(amount);
    }

    @Override
    public int residue() {
        return moneyHandler.getMoneyResidue();
    }
}