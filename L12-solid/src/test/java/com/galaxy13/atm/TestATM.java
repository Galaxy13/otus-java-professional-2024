package com.galaxy13.atm;

import com.galaxy13.atm.exceptions.NotEnoughMoney;
import com.galaxy13.atm.exceptions.UnreachableAmountException;
import com.galaxy13.atm.exceptions.UnsupportedMoneyValue;
import com.galaxy13.atm.money.Money;
import com.galaxy13.atm.money.MoneyHandler;
import com.galaxy13.atm.money.MoneyHandlerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("java:S5778")
class TestATM {
    private ATM atm;

    @BeforeEach
    void setUpBeforeClass() {
        int[] rubleDenominations = new int[]{5, 10, 50, 100, 200, 500, 1000, 2000, 5000};
        int cellsNumber = 3;
        MoneyHandler handler = new MoneyHandlerImpl(rubleDenominations, cellsNumber);
        this.atm = GalaxyATM.withHandler(handler);
    }

    @Test
    void testATMStore() {
        atm.acceptMoney(new Money(100));
        atm.acceptMoney(new Money(200));
        assertThat(atm.residue()).isEqualTo(300);
    }

    @Test
    void testATMStoreList() {
        List<com.galaxy13.atm.money.Money> moneyList = new ArrayList<>() {{
            add(new Money(100));
            add(new Money(200));
            add(new Money(500));
        }};
        atm.acceptMoney(moneyList);
        assertThat(atm.residue()).isEqualTo(800);
    }

    @Test
    void testATMCorrectWithdrawal() {
        atm.acceptMoney(new Money(500));
        atm.acceptMoney(new Money(500));
        atm.acceptMoney(new Money(200));
        atm.acceptMoney(new Money(100));
        List<com.galaxy13.atm.money.Money> withdrawalList = atm.withdraw(1000);
        assertThat(withdrawalList).hasSize(2);
        assertThat(getWithdrawalAmount(withdrawalList)).isEqualTo(1000);
        assertThat(atm.residue()).isEqualTo(300);
    }

    @Test
    void testATMNotEnoughMoney() {
        atm.acceptMoney(new Money(100));
        assertThatThrownBy(() -> atm.withdraw(500)).isInstanceOf(NotEnoughMoney.class);
    }

    @Test
    void testATMUnreachableWithdrawal() {
        atm.acceptMoney(new Money(500));
        atm.acceptMoney(new Money(500));
        atm.acceptMoney(new Money(200));
        assertThatThrownBy(() -> atm.withdraw(1100)).isInstanceOf(UnreachableAmountException.class);
    }

    @Test
    void testUnsupportedMoney() {
        // unsupported value by Ruble schema
        assertThatThrownBy(() -> {
            Money ruble = new Money(300);
            atm.acceptMoney(ruble);
        }).isInstanceOf(UnsupportedMoneyValue.class);
    }

    @Test
    void testCustomSchema() {
        int[] newDenominations = {1, 5, 10};
        int cells = 1;
        MoneyHandler testHandler = new MoneyHandlerImpl(newDenominations, cells);
        atm = GalaxyATM.withHandler(testHandler);
        atm.acceptMoney(new Money(1));
        atm.acceptMoney(new Money(5));
        assertThat(atm.residue()).isEqualTo(6);
        List<com.galaxy13.atm.money.Money> result = atm.withdraw(6);
        assertThat(result).hasSize(2);
        assertThat(getWithdrawalAmount(result)).isEqualTo(6);
        assertThatThrownBy(() -> {
            Money ruble = new Money(500);
            atm.acceptMoney(ruble);
        }).isInstanceOf(UnsupportedMoneyValue.class);
    }

    private int getWithdrawalAmount(List<? extends com.galaxy13.atm.money.Money> moneyList) {
        return moneyList.stream().mapToInt(com.galaxy13.atm.money.Money::value).sum();
    }
}
