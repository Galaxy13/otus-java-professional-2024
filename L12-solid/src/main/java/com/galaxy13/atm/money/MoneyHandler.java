package com.galaxy13.atm.money;

import java.util.List;

public interface MoneyHandler {

    int getMoneyResidue();

    void store(Money money);

    List<Money> retreive(int amount);
}
