package com.beeswork.balanceaccountservice.restcontroller;

import java.util.Random;

public class RandomStringGenerator {

    public static String generate(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            stringBuilder.append((char) (random.nextInt(26) + 97));
        }
        return stringBuilder.toString();
    }
}
