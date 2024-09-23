package com.galaxy13.atm;

import com.galaxy13.atm.money.Money;

import java.util.List;

public interface ATM {

    void acceptMoney(List<Money> moneyList);

    void acceptMoney(Money money);

    List<Money> withdraw(int amount);

    int residue();
}