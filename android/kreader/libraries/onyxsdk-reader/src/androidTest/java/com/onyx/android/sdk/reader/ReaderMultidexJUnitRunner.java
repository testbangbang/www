package com.onyx.android.sdk.reader;

import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.test.runner.AndroidJUnitRunner;

/**
 * Created by joy on 3/22/18.
 */

public class ReaderMultidexJUnitRunner extends AndroidJUnitRunner {

    @Override
    public void onCreate(Bundle arguments) {
        MultiDex.install(getTargetContext());
        super.onCreate(arguments);
    }


}
