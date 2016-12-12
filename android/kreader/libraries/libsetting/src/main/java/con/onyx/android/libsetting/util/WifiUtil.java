package con.onyx.android.libsetting.util;

import android.content.Context;
import android.content.pm.PackageManager;

import con.onyx.android.libsetting.R;
import con.onyx.android.libsetting.data.wifi.WifiBand;

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

    public static boolean hasWifi(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI);
    }
}
