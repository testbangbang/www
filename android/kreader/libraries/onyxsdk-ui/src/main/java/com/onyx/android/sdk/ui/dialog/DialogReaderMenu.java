package com.onyx.android.sdk.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.onyx.android.sdk.data.ReaderMenu;
import com.onyx.android.sdk.data.ReaderMenuAction;
import com.onyx.android.sdk.data.ReaderMenuItem;
import com.onyx.android.sdk.data.ReaderMenuState;
import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.ui.data.ReaderLayerMenuItem;
import com.onyx.android.sdk.ui.data.ReaderLayerMenuState;
import com.onyx.android.sdk.ui.view.ReaderLayerMenuLayout;

import java.net.URI;

/**
 * Created by joy on 6/28/16.
 */
public class DialogReaderMenu extends Dialog {

    private Context context;
    private ReaderMenu.ReaderMenuCallback readerMenuCallback;
    private ReaderLayerMenuLayout menuLayout;

    public DialogReaderMenu(Context context, ReaderMenu.ReaderMenuCallback menuCallback) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        this.context = context;
        readerMenuCallback = menuCallback;

        setContentView(R.layout.dialog_reader_menu);
        fitDialogToWindow();
        this.setCanceledOnTouchOutside(true);

        initDialogContent();
    }

    public ReaderLayerMenuLayout getReaderMenuLayout() {
        return menuLayout;
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

    private void initDialogContent() {
        findViewById(R.id.dismiss_zone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readerMenuCallback.onHideMenu();
            }
        });

        menuLayout = (ReaderLayerMenuLayout)findViewById(R.id.layout_reader_menu);

        findViewById(R.id.layout_back_area).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readerMenuCallback.onMenuItemClicked(ReaderLayerMenuItem.createSimpleMenuItem(ReaderMenuAction.EXIT));
            }
        });

        findViewById(R.id.button_toc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readerMenuCallback.onHideMenu();
                readerMenuCallback.onMenuItemClicked(ReaderLayerMenuItem.createSimpleMenuItem(ReaderMenuAction.DIRECTORY_TOC));
            }
        });

        findViewById(R.id.button_dict).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readerMenuCallback.onHideMenu();
                readerMenuCallback.onMenuItemClicked(ReaderLayerMenuItem.createSimpleMenuItem(ReaderMenuAction.DICT));
            }
        });

        findViewById(R.id.button_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readerMenuCallback.onHideMenu();
                readerMenuCallback.onMenuItemClicked(ReaderLayerMenuItem.createSimpleMenuItem(ReaderMenuAction.SEARCH));
            }
        });

        findViewById(R.id.text_view_progress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readerMenuCallback.onMenuItemClicked(ReaderLayerMenuItem.createSimpleMenuItem(ReaderMenuAction.GOTO_PAGE));
            }
        });

        findViewById(R.id.pre_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readerMenuCallback.onMenuItemClicked(ReaderLayerMenuItem.createSimpleMenuItem(ReaderMenuAction.NAVIGATE_BACKWARD));
            }
        });

        findViewById(R.id.next_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readerMenuCallback.onMenuItemClicked(ReaderLayerMenuItem.createSimpleMenuItem(ReaderMenuAction.NAVIGATE_FORWARD));
            }
        });

    }

    public void show(ReaderLayerMenuState state) {
        updateReaderState(state);
        show();
    }

    private void updateReaderState(ReaderLayerMenuState state) {
        ((TextView)findViewById(R.id.text_view_title)).setText(state.getTitle());
        ((TextView)findViewById(R.id.text_view_progress)).setText(formatPageProgress(state));
    }

    private String formatPageProgress(ReaderMenuState state) {
        return String.valueOf(state.getPageIndex() + 1) + "/" + String.valueOf(state.getPageCount());
    }
}
