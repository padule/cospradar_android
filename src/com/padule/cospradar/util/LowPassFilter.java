package com.padule.cospradar.util;

public class LowPassFilter {

    private static final float ALPHA = 0.25f;

    public static float[] filter(float[] input, float[] output) {
        if (output == null) {
            return input;
        }
        for (int i=0; i<input.length; i++) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }

}
