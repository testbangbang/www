package com.onyx.edu.reader.ui.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.sdk.data.ReaderMenu;
import com.onyx.android.sdk.data.ReaderMenuAction;
import com.onyx.android.sdk.data.ReaderMenuState;
import com.onyx.android.sdk.ui.data.ReaderLayerMenuItem;
import com.onyx.android.sdk.ui.dialog.OnyxBaseDialog;
import com.onyx.edu.reader.R;


/**
 * Created by ming on 2016/11/21.
 */

public class DialogReaderColorMenu extends OnyxBaseDialog {

    private Context context;
    private ReaderMenu.ReaderMenuCallback readerMenuCallback;
    private LinearLayout menuView;
    private TextView positionView;
    private ImageView prev;
    private ImageView next;

    private ReaderMenuState readerMenuState;

    public DialogReaderColorMenu(Context context, ReaderMenu.ReaderMenuCallback menuCallback) {
        super(context, R.style.CustomDialog);
        this.context = context;
        readerMenuCallback = menuCallback;

        setContentView(R.layout.dialog_reader_color_menu);
        fitDialogToWindow();
        this.setCanceledOnTouchOutside(true);

        initDialogContent();
    }

    private void initDialogContent() {
        menuView = (LinearLayout) findViewById(R.id.menu_view);
        positionView = (TextView) findViewById(R.id.position_text);
        prev = (ImageView) findViewById(R.id.prev);
        next = (ImageView) findViewById(R.id.next);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readerMenuCallback.onMenuItemClicked(ReaderLayerMenuItem.createSimpleMenuItem(ReaderMenuAction.PREV_PAGE));
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readerMenuCallback.onMenuItemClicked(ReaderLayerMenuItem.createSimpleMenuItem(ReaderMenuAction.NEXT_PAGE));
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
        mWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void show(ReaderMenuState state) {
        readerMenuState = state;
        updatePageProgress(readerMenuState.getPageIndex());
        show();
    }

    public void updateMenuView(View view) {
        menuView.removeAllViews();
        menuView.addView(view);
    }

    private void updatePageProgress(int page) {
        positionView.setText(String.valueOf(page + 1) + "/" + readerMenuState.getPageCount());
    }
}
