package com.onyx.android.libsetting.view.dialog;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.onyx.android.libsetting.R;
import com.onyx.android.libsetting.util.EduDeviceInfoUtil;
import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;

/**
 * Created by solskjaer49 on 2017/6/26 15:50.
 */

public class ScreenBarCodeDialog extends OnyxAlertDialog {
    private static final String TAG = ScreenBarCodeDialog.class.getSimpleName();
    private Bitmap screenBarCodeBitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Params params = new Params().setTittleString(getString(R.string.barcode))
                .setCustomContentLayoutResID(R.layout.alert_dialog_screen_barcode)
                .setEnableNegativeButton(false)
                .setCustomViewAction(new CustomViewAction() {
                    @Override
                    public void onCreateCustomView(View customView, TextView pageIndicator) {
                        ImageView screenBarCodeImageView = (ImageView) customView.findViewById(R.id.screen_bar_code_image_view);
                        try {
                            screenBarCodeBitmap = EduDeviceInfoUtil.getScreenBarCodeBitmap(getActivity().getApplicationContext());
                        } catch (WriterException e) {
                            e.printStackTrace();
                        }
                        screenBarCodeImageView.setImageBitmap(screenBarCodeBitmap);
                    }
                })
                .setPositiveAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });
        setParams(params);
        super.onCreate(savedInstanceState);
    }

    public void show(FragmentManager manager) {
        super.show(manager, ScreenBarCodeDialog.class.getSimpleName());
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (screenBarCodeBitmap != null && !screenBarCodeBitmap.isRecycled()) {
            screenBarCodeBitmap.recycle();
        }
    }
}
