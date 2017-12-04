package com.onyx.android.dr.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;

/**
 * Created by zhouzhiming on 2017/7/13.
 */
public class ExportSuccessHintDialog extends Dialog {
    private final Context context;
    private Button confirm;
    private TextView titleText;
    private TextView contentText;

    public ExportSuccessHintDialog(final Context context) {
        super(context, R.style.base_dialog);
        this.context = context;
        initUI();
    }

    private void initUI() {
        View dlgView = LayoutInflater.from(DRApplication.getInstance()).inflate(R.layout.dialog_export_success_hint, null);
        setContentView(dlgView);
        confirm = (Button) dlgView.findViewById(R.id.export_success_dialog_confirm);
        titleText = (TextView) dlgView.findViewById(R.id.export_success_dialog_title);
        contentText = (TextView) dlgView.findViewById(R.id.export_success_dialog_hint);
        setCanceledOnTouchOutside(true);
        initEvent();
    }

    public void initData(String title, String content) {
        titleText.setText(title);
        contentText.setText(content);
        show();
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
