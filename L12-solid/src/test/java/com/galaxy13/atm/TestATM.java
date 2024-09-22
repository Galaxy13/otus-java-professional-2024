package com.galaxy13.atm;

import com.galaxy13.atm.exceptions.NotEnoughMoney;
import com.galaxy13.atm.exceptions.UnreachableAmountException;
import com.galaxy13.atm.exceptions.UnsupportedMoneyValue;
import com.galaxy13.atm.money.Money;
import com.galaxy13.atm.money.moneytypes.Ruble;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("java:S5778")
class TestATM {
    private ATM<Ruble> atm;

    @BeforeEach
    void setUpBeforeClass() {
        this.atm = new ATM<>();
    }

    @Test
    void testATMStore() {
        atm.acceptMoney(new Ruble(100));
        atm.acceptMoney(new Ruble(200));
        assertThat(atm.residue()).isEqualTo(300);
    }

    @Test
    void testATMStoreList() {
        List<Ruble> moneyList = new ArrayList<>() {{
            add(new Ruble(100));
            add(new Ruble(200));
            add(new Ruble(500));
        }};
        atm.acceptMoney(moneyList);
        assertThat(atm.residue()).isEqualTo(800);
    }

    @Test
    void testATMCorrectWithdrawal() {
        atm.acceptMoney(new Ruble(500));
        atm.acceptMoney(new Ruble(500));
        atm.acceptMoney(new Ruble(200));
        atm.acceptMoney(new Ruble(100));
        List<Ruble> withdrawalList = atm.withdraw(1000);
        assertThat(withdrawalList).hasSize(2);
        assertThat(getWithdrawalAmount(withdrawalList)).isEqualTo(1000);
        assertThat(atm.residue()).isEqualTo(300);
    }

    @Test
    void testATMNotEnoughMoney() {
        atm.acceptMoney(new Ruble(100));
        assertThatThrownBy(() -> atm.withdraw(500)).isInstanceOf(NotEnoughMoney.class);
    }

    @Test
    void testATMUnreachableWithdrawal() {
        atm.acceptMoney(new Ruble(500));
        atm.acceptMoney(new Ruble(500));
        atm.acceptMoney(new Ruble(200));
        assertThatThrownBy(() -> atm.withdraw(1100)).isInstanceOf(UnreachableAmountException.class);
    }

    @Test
    void testUnsupportedMoney() {
        // unsupported value by Ruble schema
        assertThatThrownBy(() -> {
            Ruble ruble = new Ruble(300);
            atm.acceptMoney(ruble);
        }).isInstanceOf(UnsupportedMoneyValue.class);
    }

    @Test
    void testCustomSchema() {
        int[] newSchema = {1, 5, 10};
        atm = new ATM<>(newSchema);
        atm.acceptMoney(new Ruble(1));
        atm.acceptMoney(new Ruble(5));
        assertThat(atm.residue()).isEqualTo(6);
        List<Ruble> result = atm.withdraw(6);
        assertThat(result).hasSize(2);
        assertThat(getWithdrawalAmount(result)).isEqualTo(6);
        assertThatThrownBy(() -> {
            Ruble ruble = new Ruble(500);
            atm.acceptMoney(ruble);
        }).isInstanceOf(UnsupportedMoneyValue.class);
    }

    private int getWithdrawalAmount(List<? extends Money> moneyList) {
        return moneyList.stream().mapToInt(Money::getValue).sum();
    }
}
