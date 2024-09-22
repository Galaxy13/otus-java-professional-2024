package com.galaxy13.atm.cells;

import com.galaxy13.atm.money.Money;

public interface Cell<T extends Money> {
    void putMoney(T money);

    T retrieveMoney(int moneyValue);
}
