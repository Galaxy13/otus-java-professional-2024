package com.galaxy13.atm.cells;

import com.galaxy13.atm.money.Money;

public interface Cell {
    void putMoney(Money money);

    Money retrieveMoney(int moneyValue);
}
