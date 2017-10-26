package com.onyx.android.sdk.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.data.ReaderMenu;
import com.onyx.android.sdk.data.ReaderMenuAction;
import com.onyx.android.sdk.data.ReaderMenuState;
import com.onyx.android.sdk.data.WindowParameters;
import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.ui.data.ReaderLayerMenuItem;
import com.onyx.android.sdk.ui.view.ReaderLayerMenuLayout;

/**
 * Created by joy on 6/28/16.
 */
public class DialogReaderMenu extends Dialog {

    private Context context;
    private ReaderMenu.ReaderMenuCallback readerMenuCallback;
    private ReaderLayerMenuLayout menuLayout;
    private ImageButton noteButton;
    private ImageButton sideNoteButton;
    private ImageButton prevButton;
    private ImageButton nextButton;
    private SeekBar seekBarProgress;
    private TextView textViewProgress;
    private ImageButton nextChapter;
    private ImageButton prevChapter;

    private ReaderMenuState readerMenuState;
    private int currentPage = 0;

    public DialogReaderMenu(Context context, ReaderMenu.ReaderMenuCallback menuCallback, boolean fullscreen) {
        super(context, fullscreen ? android.R.style.Theme_Translucent_NoTitleBar_Fullscreen : android.R.style.Theme_Translucent_NoTitleBar);
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
        updateWindowParameters(new WindowParameters(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT, Gravity.BOTTOM));
    }

    private void initDialogContent() {
        findViewById(R.id.dismiss_zone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readerMenuCallback.onHideMenu();
            }
        });

        menuLayout = (ReaderLayerMenuLayout) findViewById(R.id.layout_reader_menu);
        noteButton = (ImageButton) findViewById(R.id.note_button);
        sideNoteButton = (ImageButton) findViewById(R.id.side_note_button);
        prevButton = (ImageButton) findViewById(R.id.pre_button);
        nextButton = (ImageButton) findViewById(R.id.next_button);
        seekBarProgress = (SeekBar) findViewById(R.id.seek_bar_page);
        textViewProgress = (TextView) findViewById(R.id.text_view_progress);
        nextChapter = (ImageButton) findViewById(R.id.chapter_forward);
        prevChapter = (ImageButton) findViewById(R.id.chapter_back);

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

        noteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readerMenuCallback.onMenuItemClicked(ReaderLayerMenuItem.createSimpleMenuItem(ReaderMenuAction.NOTE_WRITING));
            }
        });

        sideNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readerMenuCallback.onMenuItemClicked(ReaderLayerMenuItem.createSimpleMenuItem(ReaderMenuAction.NOTE_SIDE_NOTE));
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readerMenuCallback.onMenuItemClicked(ReaderLayerMenuItem.createSimpleMenuItem(ReaderMenuAction.NAVIGATE_BACKWARD));
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readerMenuCallback.onMenuItemClicked(ReaderLayerMenuItem.createSimpleMenuItem(ReaderMenuAction.NAVIGATE_FORWARD));
            }
        });

        nextChapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readerMenuCallback.onMenuItemClicked(ReaderLayerMenuItem.createSimpleMenuItem(ReaderMenuAction.NEXT_CHAPTER));
            }
        });

        prevChapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readerMenuCallback.onMenuItemClicked(ReaderLayerMenuItem.createSimpleMenuItem(ReaderMenuAction.PREV_CHAPTER));
            }
        });

        initPageProgress();
    }

    public void show(ReaderMenuState state) {
        EpdController.disableRegal();
        updateReaderState(state);
        updateWindowParameters(state.getWindowParameters());
        show();
    }

    public void updateReaderState(ReaderMenuState state) {
        readerMenuState = state;
        currentPage = readerMenuState.getPageIndex();
        ((TextView)findViewById(R.id.text_view_title)).setText(state.getTitle());
        ((TextView)findViewById(R.id.text_view_progress)).setText(formatPageProgress(state));
        nextButton.setEnabled(state.canGoForward());
        prevButton.setEnabled(state.canGoBack());
        updatePageProgress(readerMenuState.getPageIndex());
        seekBarProgress.setMax(readerMenuState.getPageCount());
        seekBarProgress.setProgress(currentPage);

        if (readerMenuState != null && !readerMenuState.isFixedPagingMode()) {
            noteButton.setVisibility(View.GONE);
        }

        if (readerMenuState != null && !readerMenuState.isSupportingSideNote()) {
            sideNoteButton.setVisibility(View.GONE);
        }
    }

    private void updateWindowParameters(WindowParameters params) {
        getWindow().setGravity(params.gravity | Gravity.BOTTOM);
        getWindow().setLayout(params.width, params.height);
    }

    private String formatPageProgress(ReaderMenuState state) {
        return String.valueOf(state.getPageIndex() + 1) + "/" + String.valueOf(state.getPageCount());
    }

    private void initPageProgress() {
        seekBarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    return;
                }

                int page = Math.max(progress - 1, 0);
                currentPage = page;
                updatePageProgress(page);
                readerMenuCallback.onMenuItemValueChanged(ReaderLayerMenuItem.createSimpleMenuItem(ReaderMenuAction.JUMP_PAGE), null, currentPage);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void updatePageProgress(int page) {
        textViewProgress.setText(String.format("%d/%d", page + 1, readerMenuState.getPageCount()));
    }
}
