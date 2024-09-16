package com.galaxy13.asm;

/*
./gradlew :L10-bytecode:run
 */

public class Main {
    public static void main(String[] args) {
        var summator = new AutoLogger();
        summator.sum(3, 6);
        summator.sum(4, 12, "Sum");
    }
}
