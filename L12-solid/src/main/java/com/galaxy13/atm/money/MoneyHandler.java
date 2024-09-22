package com.galaxy13.atm.money;

import java.util.List;

public interface MoneyHandler<T extends Money> {

    int getMoneyResidue();

    void store(T money);

    List<T> retreive(int amount);
}
