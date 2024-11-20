package com.galaxy13;


public class Main {
    public static void main(String[] args) {
        var threadPrinter = new BinaryThreadPrinter(2);
        threadPrinter.start();
    }
}
