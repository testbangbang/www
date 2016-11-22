package com.onyx.android.sdk.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.onyx.android.sdk.data.ReaderMenu;
import com.onyx.android.sdk.data.ReaderMenuAction;
import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.ui.data.ReaderLayerMenuItem;

/**
 * Created by ming on 2016/11/21.
 */

public class DialogReaderEduMenu extends Dialog {

    private Context context;
    private ReaderMenu.ReaderMenuCallback readerMenuCallback;

    public DialogReaderEduMenu(Context context, ReaderMenu.ReaderMenuCallback menuCallback) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.context = context;
        readerMenuCallback = menuCallback;

        setContentView(R.layout.dialog_reader_edu_menu);
        fitDialogToWindow();
        this.setCanceledOnTouchOutside(true);

        initDialogContent();
    }

    private void initDialogContent() {
        findViewById(R.id.button_toc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readerMenuCallback.onHideMenu();
                readerMenuCallback.onMenuItemClicked(ReaderLayerMenuItem.createSimpleMenuItem(ReaderMenuAction.DIRECTORY_TOC));
            }
        });
    }

    private void fitDialogToWindow() {
        Window mWindow = getWindow();
        WindowManager.LayoutParams mParams = mWindow.getAttributes();
        mParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mParams.gravity = Gravity.BOTTOM;
        mWindow.setAttributes(mParams);
        //force use all space in the screen.
        mWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }
}
