package com.galaxy13.atm.money;

import java.util.List;

public abstract class MoneyHandler<T extends Money> {

    public abstract int getMoneyResidue();

    public abstract void store(T money);

    public abstract List<T> retreive(int amount);
}
