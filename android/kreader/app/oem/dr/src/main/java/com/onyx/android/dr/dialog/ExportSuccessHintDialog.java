package com.onyx.android.dr.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;

/**
 * Created by zhouzhiming on 2017/7/13.
 */
public class ExportSuccessHintDialog extends Dialog {
    private LinearLayout contentView;
    private Button confirm;

    public ExportSuccessHintDialog(Context context) {
        super(context, R.style.base_dialog);
        initUI();
    }

    private void initUI() {
        View dlgView = LayoutInflater.from(DRApplication.getInstance()).inflate(R.layout.dialog_export_success_hint, null);
        setContentView(dlgView);
        confirm = (Button) dlgView.findViewById(R.id.export_success_dialog_confirm);
        setCanceledOnTouchOutside(true);
        initEvent();
    }

    private void initEvent() {
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }
}
