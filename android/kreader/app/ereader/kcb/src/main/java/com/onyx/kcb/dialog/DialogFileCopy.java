package com.onyx.kcb.dialog;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;
import com.onyx.kcb.R;

public class DialogFileCopy extends OnyxAlertDialog {

    boolean isCut;

    private Callback callback;

    public interface Callback {
        void onCopyCancel();
    }

    public DialogFileCopy() {
    }

    public DialogFileCopy(boolean isCut, Callback callback) {
        this.isCut = isCut;
        this.callback = callback;
    }

    private void notifyCopyCancel() {
        if (callback != null) {
            callback.onCopyCancel();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setParams(new Params().setEnableTittle(false)
                .setCustomContentLayoutResID(R.layout.alert_dialog_content_operate_file)
                .setEnablePositiveButton(false)
                .setCanceledOnTouchOutside(false)
                .setCustomViewAction(new CustomViewAction() {
                    @Override
                    public void onCreateCustomView(View customView, TextView pageIndicator) {
                        TextView msg = (TextView) customView.findViewById(R.id.operating_msg);
                        msg.setText(isCut ? R.string.moving : R.string.copying);
                    }
                }).setNegativeAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        notifyCopyCancel();
                    }
                }).setKeyAction(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                            return true;
                        }
                        return false;
                    }
                }));
        super.onCreate(savedInstanceState);
    }

    public void show(FragmentManager fm) {
        super.show(fm, DialogFileCopy.class.getSimpleName());
    }

    public void setCallback(@NonNull Callback callback) {
        this.callback = callback;
    }

    public void setCut(boolean cut) {
        this.isCut = cut;
    }
}