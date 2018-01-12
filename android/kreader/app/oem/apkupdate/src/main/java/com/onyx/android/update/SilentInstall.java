package com.onyx.android.update;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by huxiaomao on 17/7/1.
 */

public class SilentInstall {
    private static final String TAG = SilentInstall.class.getSimpleName();
    public static final String REBOOT = "reboot";

    public static void rebootDevice(final Context context) {
        try {
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            powerManager.reboot(REBOOT);
        } catch (Exception e) {
            Log.i(TAG,e.toString());
        }
    }

    public static String installSilent(Context context, String filePath) {
        String resultMessage = "";
        File file;
        if (filePath == null || filePath.length() == 0 || (file = new File(filePath)) == null || file.length() <= 0
                || !file.exists() || !file.isFile()) {
            resultMessage = String.format(context.getString(R.string.file_not_exist),filePath);
            return resultMessage;
        }
        Log.i(TAG,context.getString(R.string.start_install));
        String[] args = { "pm", "install", "-r", filePath };
        ProcessBuilder processBuilder = new ProcessBuilder(args);

        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder errorMsg = new StringBuilder();
        try {
            process = processBuilder.start();
            successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s;

            while ((s = successResult.readLine()) != null) {
                successMsg.append(s);
            }

            while ((s = errorResult.readLine()) != null) {
                errorMsg.append(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
            resultMessage = e.toString();
            Log.i(TAG,context.getString(R.string.install_fail));
        } catch (Exception e) {
            e.printStackTrace();
            resultMessage = e.toString();
            Log.i(TAG,context.getString(R.string.install_fail));
        } finally {
            try {
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (process != null) {
                process.destroy();
            }
        }
        if (successMsg.toString().contains("Success") || successMsg.toString().contains("success")) {
            Log.i(TAG,successMsg.toString());
            resultMessage = "";
        }
        Log.i(TAG,context.getString(R.string.install_complete));
        return resultMessage;
    }
}
