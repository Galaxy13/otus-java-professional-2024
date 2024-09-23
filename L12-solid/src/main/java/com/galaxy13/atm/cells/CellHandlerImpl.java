package com.galaxy13.atm.cells;

import com.galaxy13.atm.exceptions.UnsupportedMoneyValue;
import com.galaxy13.atm.money.Money;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class CellHandlerImpl implements CellHandler {
    private final Map<Integer, Cell> cells;

    public CellHandlerImpl(int[] denominations, int cellAmount, Supplier<Cell> cellTypeSupplier) {
        if (denominations.length < cellAmount) {
            cellAmount = denominations.length;
        }
        cells = new HashMap<>();
        float bucketSize = denominations.length / (float) cellAmount;
        for (int currentBucket = 0; currentBucket < cellAmount; currentBucket++) {
            int start = (int) Math.ceil(currentBucket * bucketSize);
            int end = (int) Math.ceil(currentBucket * bucketSize + bucketSize);
            for (int i = start; i < end; i++) {
                cells.put(denominations[i], cellTypeSupplier.get());
            }
        }
    }

    @Override
    public void putMoney(Money money) {
        if (cells.containsKey(money.value())) {
            cells.get(money.value()).putMoney(money);
        } else {
            throw new UnsupportedMoneyValue(money);
        }
    }

    @Override
    public Money retrieveFromCells(int value) {
        var cell = cells.get(value);
        return cell.retrieveMoney(value);
    }
}
