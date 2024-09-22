package com.galaxy13.atm.cells;

import com.galaxy13.atm.money.Money;

public interface CellHandler<T extends Money> {

    void putMoney(T money);

    T retrieveFromCells(int value);
}
