package com.onyx.android.sdk.ui.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.onyx.android.sdk.data.ReaderMenu;
import com.onyx.android.sdk.data.ReaderMenuAction;
import com.onyx.android.sdk.data.ReaderMenuState;
import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.ui.compat.AppCompatLinearLayout;
import com.onyx.android.sdk.ui.data.ReaderLayerMenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 2016/11/21.
 */

public class DialogReaderColorMenu extends OnyxBaseDialog {

    private Context context;
    private ReaderMenu.ReaderMenuCallback readerMenuCallback;
    private LinearLayout menuView;
    private TextView bookName;
    private SeekBar seekBarProgress;
    private ImageView undoView;
    private TextView positionView;
    private TextView totalView;
    private ImageView closeMenu;

    private ReaderMenuState readerMenuState;
    private List<Integer> jumpPages = new ArrayList<>();
    private int currentPage = 0;

    public DialogReaderColorMenu(Context context, ReaderMenu.ReaderMenuCallback menuCallback) {
        super(context, R.style.CustomDialog);
        this.context = context;
        readerMenuCallback = menuCallback;

        setContentView(R.layout.dialog_reader_edu_menu);
        fitDialogToWindow();
        this.setCanceledOnTouchOutside(true);

        initDialogContent();
    }

    private void initDialogContent() {
        menuView = (LinearLayout) findViewById(R.id.menu_view);
        bookName = (TextView) findViewById(R.id.book_name);
        seekBarProgress = (SeekBar) findViewById(R.id.seek_bar_page);
        undoView = (ImageView) findViewById(R.id.undo_view);
        positionView = (TextView) findViewById(R.id.position_text);
        totalView = (TextView) findViewById(R.id.total_text);
        closeMenu = (ImageView) findViewById(R.id.close_menu);
        undoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int lastPage = getLastPage();
                if (lastPage >=0) {
                    readerMenuCallback.onMenuItemValueChanged(ReaderLayerMenuItem.createSimpleMenuItem(ReaderMenuAction.JUMP_PAGE), null, lastPage);
                    updatePageProgress(lastPage);
                }
            }
        });
        closeMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readerMenuCallback.onMenuItemClicked(ReaderLayerMenuItem.createSimpleMenuItem(ReaderMenuAction.EXIT));
            }
        });

        initPageProgress();
    }

    private int getLastPage() {
        int page = -1;
        if (jumpPages.size() > 0) {
            page = jumpPages.remove(jumpPages.size() - 1);
            if (page == currentPage && jumpPages.size() > 0) {
                page = jumpPages.remove(jumpPages.size() - 1);
            }
        }
        return page;
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
        bookName.setText(state.getTitle());
        currentPage = readerMenuState.getPageIndex();
        jumpPages.add(currentPage);
        updatePageProgress(readerMenuState.getPageIndex());
        show();
    }

    public void updateMenuView(View view) {
        menuView.removeAllViews();
        menuView.addView(view);
    }

    private void initPageProgress() {
        seekBarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    return;
                }
                int page = progress - 1;
                currentPage = page;
                updatePageProgress(page);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                jumpPages.add(currentPage);
                readerMenuCallback.onMenuItemValueChanged(ReaderLayerMenuItem.createSimpleMenuItem(ReaderMenuAction.JUMP_PAGE), null, currentPage);
            }
        });
    }

    private void updatePageProgress(int page) {
        seekBarProgress.setMax(readerMenuState.getPageCount());
        seekBarProgress.setProgress(page + 1);
        positionView.setText(String.valueOf(page + 1));
        totalView.setText("/" + readerMenuState.getPageCount());
    }
}
