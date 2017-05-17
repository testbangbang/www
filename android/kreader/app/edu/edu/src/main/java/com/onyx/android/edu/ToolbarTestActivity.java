package com.onyx.android.edu;

import android.view.View;
import android.widget.ImageButton;

import com.onyx.android.edu.base.BaseActivity;
import com.onyx.android.sdk.ui.view.OnyxToolbar;

import butterknife.Bind;

/**
 * Created by ming on 16/9/20.
 */
public class ToolbarTestActivity extends BaseActivity {

    @Bind(R.id.toolbar_test)
    OnyxToolbar toolbarTest;
    @Bind(R.id.button1)
    ImageButton button1;
    @Bind(R.id.button2)
    ImageButton button2;
    @Bind(R.id.button3)
    ImageButton button3;
    @Bind(R.id.button4)
    ImageButton button4;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_toolbar_test;
    }

    @Override
    protected void initView() {
        toolbarTest.setFillStyle(OnyxToolbar.FillStyle.WrapContent);
        toolbarTest.setOnMenuClickListener(new OnyxToolbar.OnMenuClickListener() {
            @Override
            public OnyxToolbar OnClickListener(View view) {
                if (view.equals(button1)){
                    return createOnyxToolbar(1);
                }else if (view.equals(button2)){
                    return createOnyxToolbar(2);
                }else if (view.equals(button3)){
                    return createOnyxToolbar(3);
                }
                return null;
            }
        });
    }

    private OnyxToolbar createOnyxToolbar(int count){
        OnyxToolbar toolbar = new OnyxToolbar(ToolbarTestActivity.this);
        for (int i = 0; i < count; i++) {
            toolbar.addView(OnyxToolbar.Builder.createImageView(ToolbarTestActivity.this, R.drawable.ic_dialog_close));
        }

        toolbar.setOnMenuClickListener(new OnyxToolbar.OnMenuClickListener() {
            @Override
            public OnyxToolbar OnClickListener(View view) {
                return createOnyxToolbar(8);
            }
        });
        return toolbar;
    }

    @Override
    protected void initData() {

    }

}
