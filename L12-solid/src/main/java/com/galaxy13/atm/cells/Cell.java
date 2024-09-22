package com.galaxy13.atm.cells;

import com.galaxy13.atm.money.Money;

public abstract class Cell<T extends Money> {
    public abstract void putMoney(T money);

    public abstract T retrieveMoney(int moneyValue);
}
