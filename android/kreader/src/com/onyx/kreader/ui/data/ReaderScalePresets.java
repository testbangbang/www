package com.onyx.kreader.ui.data;

import android.util.Pair;

/**
 * Created by Joy on 2016/4/21.
 */
public class ReaderScalePresets {
    public static float presetValues[] = new float[] {
            0.1f, 0.25f, 0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f, 2.25f, 2.5f, 2.75f, 3.0f, 3.5f, 4.0f
    };

    public static float scaleUp(float currentValue) {
        if (currentValue < minValue()) {
            return minValue();
        }
        if (currentValue > maxValue()) {
            return maxValue();
        }
        Pair<Integer, Integer> range = locateBetween(currentValue);
        if (range.first != range.second) {
            return presetValues[range.second];
        }
        return presetValues[Math.min(presetValues.length - 1, range.second + 1)];
    }

    public static float scaleDown(float currentValue) {
        if (currentValue > maxValue()) {
            return maxValue();
        }
        if (currentValue < minValue()) {
            return minValue();
        }
        Pair<Integer, Integer> range = locateBetween(currentValue);
        if (range.first != range.second) {
            return presetValues[range.first];
        }
        return presetValues[Math.max(0, range.first - 1)];
    }

    private static float minValue() {
        return presetValues[0];
    }

    private static float maxValue() {
        return presetValues[presetValues.length - 1];
    }

    private static Pair<Integer, Integer> locateBetween(float value) {
        for (int i = 0; i < presetValues.length; i++) {
            if (Float.compare(value, presetValues[i]) == 0) {
                return new Pair<>(i, i);
            }
            if (Float.compare(value, presetValues[i]) < 0) {
                return new Pair<>(i - 1, i);
            }
        }
        throw new IllegalArgumentException();
    }
}
