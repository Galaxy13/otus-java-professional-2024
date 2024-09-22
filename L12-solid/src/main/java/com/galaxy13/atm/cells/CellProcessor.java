package com.galaxy13.atm.cells;

import com.galaxy13.atm.exceptions.UnsupportedMoneyValue;
import com.galaxy13.atm.money.Money;

import java.util.HashMap;
import java.util.Map;

public class CellProcessor<T extends Money> extends CellHandler<T> {
    private final Map<Integer, Cell<T>> cells;

    public CellProcessor(int[] schema, int cellAmount) {
        if (schema.length < cellAmount) {
            cellAmount = schema.length;
        }
        cells = new HashMap<>();
        float bucketSize = schema.length / (float) cellAmount;
        for (int currentBucket = 0; currentBucket < cellAmount; currentBucket++) {
            int start = (int) Math.ceil(currentBucket * bucketSize);
            int end = (int) Math.ceil(currentBucket * bucketSize + bucketSize);
            for (int i = start; i < end; i++) {
                cells.put(schema[i], new MapCell<>());
            }
        }
    }

    @Override
    public void putMoney(T money) {
        if (cells.containsKey(money.getValue())) {
            cells.get(money.getValue()).putMoney(money);
        } else {
            throw new UnsupportedMoneyValue(money);
        }
    }

    @Override
    public T retrieveFromCells(int value) {
        var cell = cells.get(value);
        return cell.retrieveMoney(value);
    }
}
