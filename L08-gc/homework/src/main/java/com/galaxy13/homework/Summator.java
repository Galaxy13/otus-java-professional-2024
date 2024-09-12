package com.galaxy13.homework;

public class Summator {
    private int dataCounter = 0;
    private int sum = 0;
    private int prevValue = 0;
    private int prevPrevValue = 0;
    private int sumLastThreeValues = 0;
    private int someValue = 0;

    // !!! сигнатуру метода менять нельзя
    public void calc(Data data) {
        dataCounter++;
        if (dataCounter == 100_000) {
            dataCounter = 0;
        }
        int dataValue = data.getValue();
        sum += dataValue;

        sumLastThreeValues = dataValue + prevValue + prevPrevValue;

        prevPrevValue = prevValue;
        prevValue = dataValue;

        int addValue = (sumLastThreeValues * sumLastThreeValues / (dataValue + 1) - sum);

        for (var idx = 0; idx < 3; idx++) {
            someValue += addValue;
            someValue = Math.abs(someValue) + dataCounter;
        }
    }

    public int getSum() {
        return sum;
    }

    public int getPrevValue() {
        return prevValue;
    }

    public int getPrevPrevValue() {
        return prevPrevValue;
    }

    public int getSumLastThreeValues() {
        return sumLastThreeValues;
    }

    public int getSomeValue() {
        return someValue;
    }
}
