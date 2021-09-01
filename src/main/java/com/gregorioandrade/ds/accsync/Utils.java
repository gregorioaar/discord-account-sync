package com.gregorioandrade.ds.accsync;

import java.util.Random;

public class Utils {

    private static final Random RANDOM = new Random();

    public static String generateRandomSixDigitString(){
        return String.format("%06d", RANDOM.nextInt(999999));
    }

}
