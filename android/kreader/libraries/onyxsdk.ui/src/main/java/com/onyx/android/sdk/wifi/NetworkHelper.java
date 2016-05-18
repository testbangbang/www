package com.onyx.android.sdk.wifi;

import com.onyx.android.sdk.ui.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

/**
 * @author Simon
 *
 */
public class NetworkHelper
{
    public static boolean requestWifi(final Context context)
    {

        if (isWifiConnected(context)) {
            return true;
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.wifi_dialog_title).setPositiveButton(R.string.OK,
                    new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.putExtra("closeWindowOnConnected", true);
                    context.startActivity(intent);
                }
            })
            .setNegativeButton(R.string.cancel, null)
            .setMessage(R.string.wifi_dialog_content)
            .show();
            return false;
        }
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            return true;
        } else {
            return false;
        }
    }
}
