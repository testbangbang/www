package com.onyx.android.libsetting.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.ui.compat.AppCompatUtils;
import com.onyx.android.sdk.utils.DeviceUtils;

import java.io.File;

/**
 * Created by solskjaer49 on 2017/5/19 17:01.
 */

public class QRCodeUtil {
    public static final String CFA_QR_CODE_FILE_PATH = "data/local/assets/device_qr_code.png";
    private static final String TAG = QRCodeUtil.class.getSimpleName();
    private static final boolean DEBUG = false;

    static {
        System.loadLibrary("onyx_cfa");
    }

    static public native void toRgbwBitmap(final Bitmap dst, final Bitmap src, int orientation);


    private static Bitmap getCFABitMap(Bitmap src) {
        Bitmap dst = Bitmap.createBitmap(src.getWidth() * 2, src.getHeight() * 2, Bitmap.Config.ARGB_8888);
        toRgbwBitmap(dst, src, 0);
        return dst;
    }

    public static Bitmap getQRCodeCFABitmap(Context context) throws WriterException {
        File cacheFile = new File(CFA_QR_CODE_FILE_PATH);
        if (cacheFile.exists() && cacheFile.canRead()) {
            return BitmapFactory.decodeFile(cacheFile.getPath());
        } else {
            return stringToImageEncode(context, DeviceUtils.getDeviceMacAddress(context), 120,
                    context.getResources().getColor(android.R.color.holo_blue_dark));
        }
    }

    public static Bitmap stringToImageEncode(Context context, String value, int targetSize) throws WriterException {
        return stringToImageEncode(context, value, targetSize, context.getResources().getColor(android.R.color.black));
    }

    public static Bitmap stringToImageEncode(Context context, String value, int targetSize, int qrCodeColor) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    value,
                    BarcodeFormat.QR_CODE,
                    targetSize, targetSize, null
            );
        } catch (IllegalArgumentException Illegalargumentexception) {
            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();
        int bitMatrixHeight = bitMatrix.getHeight();
        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];
        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;
            for (int x = 0; x < bitMatrixWidth; x++) {
                pixels[offset + x] = bitMatrix.get(x, y) ? qrCodeColor :
                        context.getResources().getColor(android.R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);
        bitmap.setPixels(pixels, 0, bitMatrixWidth, 0, 0, bitMatrixWidth, bitMatrixHeight);
        if (qrCodeColor != context.getResources().getColor(android.R.color.black) && AppCompatUtils.isColorDevice(context)) {
            return getCFABitMap(bitmap);
        } else {
            return bitmap;
        }
    }
}
