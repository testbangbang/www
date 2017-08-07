package com.onyx.unitconversion;

import android.util.Pair;

/**
 * Created by ming on 2017/5/18.
 */

public class Config {

    public static Pair<UnitType, Integer> UNIT_NAME_MAP[] = new Pair[] {
            new Pair<>(UnitType.Length, R.string.length),
            new Pair<>(UnitType.Area, R.string.area),
            new Pair<>(UnitType.Volume, R.string.volume),
            new Pair<>(UnitType.Mass, R.string.mass),
            new Pair<>(UnitType.Temperature, R.string.temperature),
            new Pair<>(UnitType.Time, R.string.time)
    };

}
