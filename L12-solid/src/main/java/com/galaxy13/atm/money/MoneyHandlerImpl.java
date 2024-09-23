package com.galaxy13.atm.money;

import com.galaxy13.atm.cells.CellHandler;
import com.galaxy13.atm.cells.CellHandlerImpl;
import com.galaxy13.atm.cells.MapCell;
import com.galaxy13.atm.exceptions.NotEnoughMoney;
import com.galaxy13.atm.exceptions.UnreachableAmountException;
import com.galaxy13.atm.exceptions.UnsupportedMoneyValue;

import java.util.*;

public class MoneyHandlerImpl implements MoneyHandler {
    private final List<Integer> moneyList;
    private final Set<Integer> checkSet;
    private final CellHandler mapCellHandler;
    private int atmMoneyResidue = 0;

    public MoneyHandlerImpl(int[] denominations, int cellsNumber) {
        checkSet = new HashSet<>();
        for (int j : denominations) {
            checkSet.add(j);
        }
        moneyList = new ArrayList<>();
        mapCellHandler = new CellHandlerImpl(denominations, cellsNumber, MapCell::new);
    }

    @Override
    public void store(Money money) {
        if (checkSet.contains(money.value())) {
            atmMoneyResidue += money.value();
            moneyList.add(money.value());
            mapCellHandler.putMoney(money);
        } else {
            throw new UnsupportedMoneyValue(money);
        }
    }

    @Override
    public List<Money> retreive(int amount) {
        if (amount > atmMoneyResidue) {
            throw new NotEnoughMoney(amount);
        }
        List<Integer> indexList = new ArrayList<>();
        if (recursiveBacktrack(indexList, amount, 0)) {
            return retreiveMoneyFromCells(indexList);
        } else {
            throw new UnreachableAmountException(moneyList, amount);
        }
    }

    @Override
    public int getMoneyResidue() {
        return atmMoneyResidue;
    }

    private boolean recursiveBacktrack(List<Integer> resultList, int currentTarget, int moneyIdx) {
        if (currentTarget == 0) {
            return true;
        }
        if (moneyIdx == moneyList.size()) {
            return false;
        }
        if (moneyList.get(moneyIdx) > currentTarget) {
            return recursiveBacktrack(resultList, currentTarget, moneyIdx + 1);
        }
        if (recursiveBacktrack(resultList, currentTarget, moneyIdx + 1)) {
            return true;
        }
        resultList.add(moneyIdx);
        boolean found = recursiveBacktrack(resultList, currentTarget - moneyList.get(moneyIdx), moneyIdx + 1);
        if (!found) {
            resultList.removeLast();
        }
        return found;
    }

    private List<Money> retreiveMoneyFromCells(List<Integer> indices) {
        List<Money> result = new ArrayList<>();
        for (Integer index : indices) {
            int moneyValue = moneyList.get(index);
            result.add(mapCellHandler.retrieveFromCells(moneyValue));
        }
        indices.sort(Collections.reverseOrder());
        for (int index : indices) {
            atmMoneyResidue -= moneyList.get(index);
            moneyList.remove(index);
        }
        return result;
    }
}
