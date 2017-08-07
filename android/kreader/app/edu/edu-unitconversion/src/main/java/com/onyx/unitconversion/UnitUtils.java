package com.onyx.unitconversion;

import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import java.text.DecimalFormat;

/**
 * Created by ming on 2017/5/26.
 */

public class UnitUtils {
    private static final String TAG = "UnitUtils";

    private static DecimalFormat dfExp = new DecimalFormat("#.#######E0");
    private static DecimalFormat dfNoexp = new DecimalFormat("#.#######");

    public static Spanned getFormattedValueStr(double value) {
        String strValue = getValueStr(value);
        try{
            if(strValue.contains("E")){
                strValue = strValue.replace("E", " × 10<sup><small>");
                strValue += "</small></sup>";
            }
        }catch(Exception e){
            Log.d(TAG, "Error while rendering unit.", e);
        }
        return Html.fromHtml(strValue);
    }

    public static String getValueStr(double val) {
        if((Math.abs(val) > 1e6) || (Math.abs(val) < 1e-6 && Math.abs(val) > 0.0)){
            return dfExp.format(val);
        } else {
            return dfNoexp.format(val);
        }
    }
}
