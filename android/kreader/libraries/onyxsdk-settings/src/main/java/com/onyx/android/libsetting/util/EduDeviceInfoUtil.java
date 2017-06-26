package com.onyx.android.libsetting.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.google.zxing.WriterException;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.device.IMX6Device;
import com.onyx.android.sdk.device.RK3026Device;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.utils.FileUtils;

import java.io.File;

import static com.onyx.android.libsetting.util.Constant.SCREEN_BARCODE_FILE_PATH;

/**
 * Created by solskjaer49 on 2017/6/26 14:59.
 */

public class EduDeviceInfoUtil {
    private static final String TAG = EduDeviceInfoUtil.class.getSimpleName();
    static private boolean isCachedBarcodePicLegal = false;

    static public String getBarCode() {
        return OnyxSystemProperties.get("sys.panel.barcode", "");
    }

    static public String getVComInfo() {
        String vcomEndPoint = getVComEndPoint();
        if (TextUtils.isEmpty(vcomEndPoint)) {
            return "";
        }
        String vcomInfo = FileUtils.readContentOfFile(new File(getVComEndPoint()));
        if (!TextUtils.isEmpty(vcomInfo)) {
            vcomInfo = Double.valueOf(vcomInfo) / 100 + " V";
        }
        return vcomInfo;
    }

    static private String getVComEndPoint() {
        if (Device.currentDevice() instanceof RK3026Device) {
            return "/sys/devices/platform/onyx_misc.0/vcom_value";
        } else if (Device.currentDevice() instanceof IMX6Device) {
            return "/sys/class/hwmon/hwmon0/device/vcom_value";
        }
        return null;
    }

    private static boolean isCachedBarcodeBitmapLegal(Bitmap cacheBitmap) {
        if (!isCachedBarcodePicLegal) {
            if (getBarCode().equalsIgnoreCase(QRCodeUtil.decodeCodeBitmap(cacheBitmap))) {
                isCachedBarcodePicLegal = true;
            }
            return isCachedBarcodePicLegal;
        }
        return true;
    }

    /**
     * get ScreenBarcode Bitmap ,if cached is exist and legal,just use it.
     * if not legal(screen has been replaced,regenerate it).
     * @param context
     * @return
     * @throws WriterException
     */
    public static Bitmap getScreenBarCodeBitmap(Context context) throws WriterException {
        File cacheFile = new File(SCREEN_BARCODE_FILE_PATH);
        if (cacheFile.exists() && cacheFile.canRead()) {
            Bitmap cachedBitmap = BitmapFactory.decodeFile(cacheFile.getPath());
            if (isCachedBarcodeBitmapLegal(cachedBitmap)) {
                return cachedBitmap;
            } else {
                return generateScreenBarCodeBitmap(context);
            }
        } else {
            return generateScreenBarCodeBitmap(context);
        }
    }

    private static Bitmap generateScreenBarCodeBitmap(Context context){
        Bitmap resultBitmap = null;
        try {
            resultBitmap = QRCodeUtil.stringToBarcodeEncode(context, getBarCode(), 350, 150);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
        BitmapUtils.saveBitmap(resultBitmap, SCREEN_BARCODE_FILE_PATH, true);
        return resultBitmap;
    }
}
