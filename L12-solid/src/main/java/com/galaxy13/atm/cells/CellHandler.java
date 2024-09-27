package com.galaxy13.atm.cells;

import com.galaxy13.atm.money.Money;

public interface CellHandler {

    void putMoney(Money money);

    Money retrieveFromCells(int value);
}
