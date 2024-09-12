package com.galaxy13.homework;

public class Data(int value) {
    private int value;

    public Data(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }

    public void increment(){
        value++;
    }
}
