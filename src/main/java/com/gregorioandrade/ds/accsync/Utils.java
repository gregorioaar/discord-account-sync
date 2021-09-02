package com.gregorioandrade.ds.accsync;

import java.util.Random;

public class Utils {

    private static final Random RANDOM = new Random();

    public static String generateRandomSixDigitString(){
        return String.format("%d%05d", RANDOM.nextInt(9)+1, RANDOM.nextInt(100000));
    }

}
