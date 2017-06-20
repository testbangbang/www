package com.onyx.android.sdk.ui.dialog;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.onyx.android.sdk.ui.R;

/**
 * Created by ming on 2017/1/12.
 */

public class DialogChoose extends OnyxBaseDialog{

    public interface Callback {
        void onClickListener(int index);
    }

    private Button btn1;
    private Button btn2;
    private Callback callback;
    private int title1ResId;
    private int title2ResId;

    public DialogChoose(Context context, int title1ResId, int title2ResId, Callback callback) {
        super(context, R.style.CustomDialog);
        this.callback = callback;
        this.title1ResId = title1ResId;
        this.title2ResId = title2ResId;
        init();
    }

    private void init() {
        setContentView(R.layout.dialog_choose);
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn1.setText(title1ResId);
        btn2.setText(title2ResId);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.onClickListener(0);
                    dismiss();
                }
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.onClickListener(1);
                    dismiss();
                }
            }
        });
    }


}
