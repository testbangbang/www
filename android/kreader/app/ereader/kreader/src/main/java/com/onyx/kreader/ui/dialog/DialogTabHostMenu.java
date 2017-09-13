package com.onyx.kreader.ui.dialog;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.kreader.R;
import com.onyx.kreader.ui.ReaderTabManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by joy on 11/1/16.
 */
public class DialogTabHostMenu extends DialogBase {

    public static abstract class Callback {
        public abstract void onLinkedOpen(String path);
        public abstract void onSideOpen(String left, String right);
        public abstract void onSideNote(String path);
        public abstract void onSideSwitch();
        public abstract void onClosing();
    }

    private class TabViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private CheckBox checkBox;

        private String currentFile;

        public TabViewHolder(View itemView) {
            super(itemView);
            textView = (TextView)itemView.findViewById(R.id.textview);
            checkBox = (CheckBox)itemView.findViewById(R.id.checkbox);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        selectedFiles.add(currentFile);
                    } else {
                        selectedFiles.remove(currentFile);
                    }

                    disableButtons();
                    if (selectedFiles.size() == 1) {
                        buttonDoubleLink.setEnabled(true);
                        buttonSideNote.setEnabled(true);
                    }
                    if (selectedFiles.size() == 2) {
                        buttonSideOpen.setEnabled(true);
                    }
                }
            });
        }

        public void bindView(String path) {
            currentFile = path;
            textView.setText(FileUtils.getFileName(path));
        }
    }

    @Bind(R.id.button_double_link)
    Button buttonDoubleLink;
    @Bind(R.id.button_side_open)
    Button buttonSideOpen;
    @Bind(R.id.button_side_note)
    Button buttonSideNote;
    @Bind(R.id.button_side_switch)
    Button buttonSideSwitch;
    @Bind(R.id.button_close)
    Button buttonClose;

    private ArrayList<String> files = new ArrayList<>();
    private ArrayList<String> selectedFiles = new ArrayList<>();

    private boolean isSideReading;
    private Callback callback;

    public DialogTabHostMenu(final Context context, final List<String> files, final boolean isSideReading, Callback callback) {
        super(context);
        setContentView(R.layout.dialog_tab_host_menu);

        ButterKnife.bind(this);

        this.files.addAll(files);
        this.isSideReading = isSideReading;
        this.callback = callback;

        init();
    }

    @OnClick(R.id.button_double_link)
    void onButtonDoubleLinkClicked() {
        DialogTabHostMenu.this.dismiss();
        if (callback != null) {
            callback.onLinkedOpen(selectedFiles.get(0));
        }
    }

    @OnClick(R.id.button_side_open)
    void onButtonSideOpenClicked() {
        DialogTabHostMenu.this.dismiss();
        if (callback != null) {
            callback.onSideOpen(selectedFiles.get(0), selectedFiles.get(1));
        }
    }

    @OnClick(R.id.button_side_note)
    void onButtonSideNoteClicked() {
        DialogTabHostMenu.this.dismiss();
        if (callback != null) {
            callback.onSideNote(selectedFiles.get(0));
        }
    }

    @OnClick(R.id.button_side_switch)
    void onButtonSideSwitchClicked() {
        DialogTabHostMenu.this.dismiss();
        if (callback != null) {
            callback.onSideSwitch();
        }
    }

    @OnClick(R.id.button_close)
    void onButtonCloseClicked() {
        if (callback != null) {
            DialogTabHostMenu.this.dismiss();
            callback.onClosing();
        }
    }

    private void init() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_tab);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @Override
            public int getItemCount() {
                return Math.min(8, files.size());
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new TabViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.dialog_tab_host_menu_item_view, parent, false));
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                ((TabViewHolder)holder).bindView(files.get(position));
            }

        });

        disableButtons();
    }

    private void disableButtons() {
        buttonDoubleLink.setEnabled(false);
        buttonSideOpen.setEnabled(false);
        buttonSideNote.setEnabled(false);

        if (!isSideReading) {
            buttonSideSwitch.setEnabled(false);
            buttonClose.setEnabled(false);
        }
    }

}
