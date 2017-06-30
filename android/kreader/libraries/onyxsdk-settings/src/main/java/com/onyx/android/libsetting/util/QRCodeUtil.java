package com.onyx.android.libsetting.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.EncodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.onyx.android.sdk.ui.compat.AppCompatUtils;
import com.onyx.android.sdk.utils.NetworkUtil;

import java.io.File;
import java.util.Hashtable;

/**
 * Created by solskjaer49 on 2017/5/19 17:01.
 */

public class QRCodeUtil {
    public static final String CFA_QR_CODE_FILE_PATH = "data/local/assets/device_qr_code.png";
    private static final String TAG = QRCodeUtil.class.getSimpleName();
    private static final boolean DEBUG = false;
    public static final int DEFAULT_SIZE = 280;
    public static final int DEFAULT_CFA_SIZE = 120;
    public static final int WHITE_MARGIN_SIZE = 20;


    static {
        System.loadLibrary("onyx_cfa");
    }

    static public native void toRgbwBitmap(final Bitmap dst, final Bitmap src, int orientation);


    private static Bitmap getCFABitMap(Bitmap src) {
        Bitmap dst = Bitmap.createBitmap(src.getWidth() * 2, src.getHeight() * 2, Bitmap.Config.ARGB_8888);
        toRgbwBitmap(dst, src, 0);
        return dst;
    }

    @Nullable
    public static Bitmap getQRCodeCFABitmap() throws WriterException {
        File cacheFile = new File(CFA_QR_CODE_FILE_PATH);
        if (cacheFile.exists() && cacheFile.canRead()) {
            return BitmapFactory.decodeFile(cacheFile.getPath());
        }else {
            return null;
        }
    }

    public static Bitmap stringToImageEncode(Context context, String value, int targetSize) throws WriterException {
        return stringToImageEncode(context, value, targetSize, false,
                -1, context.getResources().getColor(android.R.color.black));
    }

    public static Bitmap stringToBarcodeEncode(Context context, String value, int width,int height) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    value,
                    BarcodeFormat.CODE_128,
                     width, height, null
            );
        } catch (IllegalArgumentException illegalargumentexception) {
            illegalargumentexception.printStackTrace();
            return null;
        }

        int bitMatrixWidth = bitMatrix.getWidth();
        int bitMatrixHeight = bitMatrix.getHeight();
        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];
        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;
            for (int x = 0; x < bitMatrixWidth; x++) {
                pixels[offset + x] = bitMatrix.get(x, y) ? context.getResources().getColor(android.R.color.black) :
                        context.getResources().getColor(android.R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);
        bitmap.setPixels(pixels, 0, bitMatrixWidth, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    public static Bitmap stringToImageEncode(Context context, String value, int targetSize,
                                             boolean customWhiteMargin, int customWhiteMarginSize,
                                             int qrCodeColor) throws WriterException {
        Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 10);
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    value,
                    BarcodeFormat.QR_CODE,
                    targetSize, targetSize, hints
            );
        } catch (IllegalArgumentException Illegalargumentexception) {
            return null;
        }
        if (customWhiteMargin) {
            bitMatrix = reConfigWhiteMargin(bitMatrix, customWhiteMarginSize);
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

    /**
     *
     * @param matrix source QR code matrix
     * @param preservedWhiteMarginSize size for white margin width.
     * @return
     */
    private static BitMatrix reConfigWhiteMargin(BitMatrix matrix, int preservedWhiteMarginSize) {
        int[] rec = matrix.getEnclosingRectangle();
        int resWidth = rec[2] + preservedWhiteMarginSize;
        int resHeight = rec[3] + preservedWhiteMarginSize;
        int whiteMarginOffset = preservedWhiteMarginSize / 2;

        BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);
        resMatrix.clear();
        for (int i = whiteMarginOffset; i < resWidth; i++) {
            for (int j = whiteMarginOffset; j < resHeight; j++) {
                if (matrix.get(i - whiteMarginOffset + rec[0], j - whiteMarginOffset + rec[1]))
                    resMatrix.set(i, j);
            }
        }
        return resMatrix;
    }

    /**
     * decode QR/Bitmap Code to string.
     * @param bitmap
     * @return can be null,if null,means bitmap is not legal.
     */
    @Nullable
    public static String decodeCodeBitmap(Bitmap bitmap){
        Reader reader = new MultiFormatReader();
        Result result = null;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

        try {
            result = reader.decode(binaryBitmap);
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }

        if (result != null) {
            return result.getText();
        }else {
            return null;
        }
    }
}
