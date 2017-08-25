package com.onyx.android.dr.devicesetting.data.wifi;

import android.content.Context;

import com.onyx.android.dr.R;


/**
 * Created by solskjaer49 on 2016/12/3 12:16.
 */

public class WifiUtil {
    public static
    @WifiBand.WifiBandDef
    int convertFrequencyToBand(int freq) {
        if (freq >= 2412 && freq <= 2484) {
            return WifiBand.B_G_N_NETWORK;
        } else if (freq >= 5170 && freq <= 5825) {
            return WifiBand.A_H_J_N_AC_NETWORK;
        } else {
            return WifiBand.UNKNOWN;
        }
    }

    public static String getBandString(Context context, int frequnecy) {
        switch (convertFrequencyToBand(frequnecy)) {
            case WifiBand.B_G_N_NETWORK:
                return context.getString(R.string.bgn_net_work);
            case WifiBand.A_H_J_N_AC_NETWORK:
                return context.getString(R.string.ac_network);
            case WifiBand.UNKNOWN:
            default:
                return context.getString(R.string.unknown_network);
        }
    }

}
