package com.galaxy13.atm.cells;

import com.galaxy13.atm.money.Money;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapCell<T extends Money> implements Cell<T> {
    private final Map<Integer, List<T>> cellStorage;

    public MapCell() {
        this.cellStorage = new HashMap<>();
    }

    @Override
    public void putMoney(T money) {
        cellStorage.computeIfAbsent(money.getValue(), x -> new ArrayList<>()).add(money);
    }

    @Override
    public T retrieveMoney(int moneyValue) {
        return cellStorage.get(moneyValue).removeLast();
    }
}
