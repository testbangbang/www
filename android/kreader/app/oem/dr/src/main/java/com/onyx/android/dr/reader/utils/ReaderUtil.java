package com.onyx.android.dr.reader.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.reader.activity.ReaderActivity;
import com.onyx.android.dr.reader.common.ReaderConstants;
import com.onyx.android.dr.reader.data.OpenBookParam;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.utils.CloudUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Locale;

/**
 * Created by huxiaomao on 17/5/3.
 */

public class ReaderUtil {
    private static final String TAG = ReaderUtil.class.getSimpleName();
    private static final int JDPDF_HEAD_LENGTH = 4;
    private static final int JDPDF_ONE_BYTE = 0x25;
    private static final int JDPDF_TWO_BYTE = 0x50;
    private static final int JDPDF_THREE_BYTE = 0x44;
    private static final int JDPDF_FOUR_BYTE = 0x46;

    public static boolean requestWifi(final Context context) {
        if (isWifiConnected(context)) {
            return true;
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.wifi_dialog_title).setPositiveButton(R.string.ok,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.putExtra("closeWindowOnConnected", true);
                            context.startActivity(intent);
                        }
                    })
                    .setNegativeButton(R.string.Cancel, null)
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

    public static boolean isChinese(final Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        return language.endsWith("zh");
    }

    public static boolean isJEB(final String path) {
        String extension = FileUtils.getFileExtension(path);
        if (extension.contentEquals("jeb")) {
            return true;
        }
        return false;
    }

    public static boolean isJDPDF(final String path) {
        boolean bRet = false;
        FileInputStream fs = null;
        try {
            if (isJEB(path)) {
                if (FileUtils.fileExist(path)) {
                    File file = new File(path);
                    fs = new FileInputStream(file);
                    byte[] head = new byte[JDPDF_HEAD_LENGTH];
                    fs.read(head, 0, JDPDF_HEAD_LENGTH);
                    if (head[0] == JDPDF_ONE_BYTE &&
                            head[1] == JDPDF_TWO_BYTE &&
                            head[2] == JDPDF_THREE_BYTE &&
                            head[3] == JDPDF_FOUR_BYTE) {
                        bRet = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FileUtils.closeQuietly(fs);
        }
        return bRet;
    }

    public static void openBook(final Context context, final OpenBookParam openBookParam) {
        boolean isKReaderOpenBook = false;
        if(isKReaderOpenBook) {
            KReaderOpenBook(context, openBookParam);
        }else {
            Log.e(TAG, "openBook: localPath:" + openBookParam.getLocalPath());
            Intent intent = new Intent(context, ReaderActivity.class);
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(ReaderConstants.BOOK_ID, openBookParam.getBookId());
            intent.putExtra(ReaderConstants.BOOK_NAME, openBookParam.getBookName());
            intent.putExtra(ReaderConstants.BOOK_PASSWORD, openBookParam.getPassword());
            intent.putExtra(ReaderConstants.IS_FLUENT, openBookParam.isFluent());
            Uri uri = Uri.fromFile(new File(openBookParam.getLocalPath()));
            intent.setDataAndType(uri, "application/JEB");
            context.startActivity(intent);
        }
    }

    public static void KReaderOpenBook(final Context context, final OpenBookParam openBookParam) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ReaderConstants.BOOK_NAME, openBookParam.getBookName());
        intent.putExtra(ReaderConstants.BOOK_PASSWORD, openBookParam.getPassword());
        Uri uri = Uri.fromFile(new File(openBookParam.getLocalPath()));

        intent.setDataAndType(uri, "application/JEB");
        context.startActivity(intent);
    }

    public static String getDataSaveFilePath(Metadata book) {
        if (checkBookMetadataPathValid(book)) {
            return book.getNativeAbsolutePath();
        }
        String fileName = FileUtils.fixNotAllowFileName(book.getName() + "." + book.getType());
        if (StringUtils.isBlank(fileName)) {
            return null;
        }
        return new File(CloudUtils.dataCacheDirectory(DRApplication.getInstance(), book.getGuid()), fileName)
                .getAbsolutePath();
    }

    public static boolean checkBookMetadataPathValid(Metadata book) {
        if (StringUtils.isNotBlank(book.getNativeAbsolutePath()) && new File(book.getNativeAbsolutePath()).exists()) {
            return true;
        }
        return false;
    }
}
