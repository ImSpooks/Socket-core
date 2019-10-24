package me.ImSpooks.core.helpers;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by Nick on 22 okt. 2019.
 * Copyright © ImSpooks
 */
public class MathHelpers {
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
