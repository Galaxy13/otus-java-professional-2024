package com.galaxy13.atm.cells;

import com.galaxy13.atm.money.Money;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapCell implements Cell {
    private final Map<Integer, List<Money>> cellStorage;

    public MapCell() {
        this.cellStorage = new HashMap<>();
    }

    @Override
    public void putMoney(Money money) {
        cellStorage.computeIfAbsent(money.value(), x -> new ArrayList<>()).add(money);
    }

    @Override
    public Money retrieveMoney(int moneyValue) {
        return cellStorage.get(moneyValue).removeLast();
    }
}
