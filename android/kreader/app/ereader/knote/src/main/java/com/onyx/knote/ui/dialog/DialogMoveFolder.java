package com.onyx.knote.ui.dialog;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;
import com.onyx.android.sdk.ui.view.DisableScrollLinearManager;
import com.onyx.android.sdk.ui.view.OnyxPageDividerItemDecoration;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.knote.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by solskjaer49 on 16/6/28 18:14.
 */

public class DialogMoveFolder extends OnyxAlertDialog {

    public DialogMoveFolder setDataSet(List<NoteModel> dataList) {
        for (NoteModel noteModel : dataList) {
            noteModelBooleanHashMap.put(noteModel, false);
        }
        noteModelList.addAll(dataList);
        return this;
    }


    List<NoteModel> noteModelList = new ArrayList<>();
    HashMap<NoteModel, Boolean> noteModelBooleanHashMap = new HashMap<>();
    DialogMoveFolderCallback callback;
    //Use to string to avoid random id coincidentally same as this flag.
    String targetParentID = Boolean.toString(Boolean.FALSE);
    PageRecyclerView targetLibraryPageRecyclerView;

    public interface DialogMoveFolderCallback {
        void onMove(String targetParentId);

        void onDismiss();
    }

    public void setCallback(DialogMoveFolderCallback dialogMoveFolderCallback) {
        this.callback = dialogMoveFolderCallback;
    }


    public void show(FragmentManager fm) {
        super.show(fm, DialogMoveFolder.class.getSimpleName());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Params params = new Params().setTittleString(getString(R.string.move))
                .setCustomContentLayoutResID(R.layout.alert_dialog_content_move_folder)
                .setEnableNegativeButton(false)
                .setCustomLayoutHeight((int) (5 * getResources().getDimension(R.dimen.dialog_move_folder_item_height)))
                .setPositiveAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (StringUtils.isNullOrEmpty(targetParentID)) {
                            callback.onMove(targetParentID);
                        } else if (!targetParentID.equalsIgnoreCase(Boolean.toString(Boolean.FALSE))) {
                            callback.onMove(targetParentID);
                        } else {
                            dismiss();
                        }
                    }
                })
                .setCustomViewAction(new CustomViewAction() {
                    @Override
                    public void onCreateCustomView(View customView, TextView pageIndicator) {
                        targetLibraryPageRecyclerView = (PageRecyclerView) customView.findViewById(R.id.page_recycler_view_move_folder);
                        initRecyclerView(targetLibraryPageRecyclerView);
                    }
                });
        setParams(params);
        super.onCreate(savedInstanceState);
    }

    private void initRecyclerView(PageRecyclerView recyclerView) {
        recyclerView.setLayoutManager(new DisableScrollLinearManager(getActivity()));
        recyclerView.setAdapter(new PageRecyclerView.PageAdapter() {
            @Override
            public int getRowCount() {
                return 5;
            }

            @Override
            public int getColumnCount() {
                return 1;
            }

            @Override
            public int getDataCount() {
                return noteModelList.size();
            }

            @Override
            public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                return new FolderItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_move_folder_item, parent, false));
            }

            @Override
            public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                NoteModel model = noteModelList.get(position);
                ((FolderItemHolder) holder).bindView(pageRecyclerView, model, noteModelBooleanHashMap.get(model));
            }
        });
        OnyxPageDividerItemDecoration itemDecoration;
        itemDecoration = new OnyxPageDividerItemDecoration(getActivity(), OnyxPageDividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        itemDecoration.setActualChildCount(recyclerView.getPageAdapter().getDataCount());
        recyclerView.setItemDecorationHeight(itemDecoration.getDivider().getIntrinsicHeight());
    }

    private class FolderItemHolder extends RecyclerView.ViewHolder {
        private TextView noteModelNameTextView;
        private CheckBox selectionCheckbox;

        public FolderItemHolder(View itemView) {
            super(itemView);
            noteModelNameTextView = (TextView) itemView.findViewById(R.id.textview_title);
            selectionCheckbox = (CheckBox) itemView.findViewById(R.id.target_folder_checkbox);
        }

        public void bindView(final PageRecyclerView pageRecyclerView, final NoteModel model, boolean checked) {
            noteModelNameTextView.setText(model.getExtraAttributes());
            selectionCheckbox.setChecked(checked);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (NoteModel noteModel : noteModelBooleanHashMap.keySet()) {
                        if (noteModel.getUniqueId().equalsIgnoreCase(model.getUniqueId())) {
                            noteModelBooleanHashMap.put(noteModel, true);
                            targetParentID = noteModel.getUniqueId();
                        } else {
                            noteModelBooleanHashMap.put(noteModel, false);
                        }
                    }
                    pageRecyclerView.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (callback != null) {
            callback.onDismiss();
        }
        super.onDismiss(dialog);
    }
}
