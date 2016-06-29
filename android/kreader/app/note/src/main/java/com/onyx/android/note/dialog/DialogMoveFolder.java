package com.onyx.android.note.dialog;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.onyx.android.note.R;
import com.onyx.android.note.utils.Utils;
import com.onyx.android.sdk.data.GAdapter;
import com.onyx.android.sdk.data.GAdapterUtil;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;
import com.onyx.android.sdk.ui.view.ContentItemView;
import com.onyx.android.sdk.ui.view.ContentView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by solskjaer49 on 16/6/28 18:14.
 */

public class DialogMoveFolder extends OnyxAlertDialog {

    public DialogMoveFolder setDataList(List<NoteModel> dataList) {
        this.dataList = dataList;
        return this;
    }

    List<NoteModel> dataList;
    DialogMoveFolderCallback callback;

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
        setParams(new Params().setTittleString(getString(R.string.move))
                .setCustomLayoutResID(R.layout.alert_dialog_content_move_folder)
                .setEnableFunctionPanel(false)
                .setCustomLayoutHeight((int) (5 * getResources().getDimension(R.dimen.button_minHeight)))
                .setCustomViewAction(new CustomViewAction() {
                    @Override
                    public void onCreateCustomView(View customView, TextView pageIndicator) {
                        ContentView targetLibraryContentView = (ContentView) customView.findViewById(R.id.contentView_move_folder);
                        targetLibraryContentView.setupGridLayout(5, 1);
                        GAdapter adapter = Utils.adapterFromNoteModelListWithFullPathTitle(dataList,
                                R.drawable.ic_student_note_folder_gray_250dp,
                                R.drawable.ic_student_note_pic_gray_250dp);
                        HashMap<String, Integer> mapping = new HashMap<String, Integer>();
                        mapping.put(GAdapterUtil.TAG_TITLE_STRING, R.id.textview_title);
                        mapping.put(GAdapterUtil.TAG_DIVIDER_VIEW, R.id.divider);
                        targetLibraryContentView.setSubLayoutParameter(R.layout.dialog_move_folder_item, mapping);
                        targetLibraryContentView.setShowPageInfoArea(false);
                        targetLibraryContentView.setAdapter(adapter, 0);
                        targetLibraryContentView.setCallback(new ContentView.ContentViewCallback() {
                            @Override
                            public void onItemClick(ContentItemView view) {
                                GObject temp = view.getData();
                                callback.onMove(GAdapterUtil.getUniqueId(temp));
                            }
                        });
                    }
                }));
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (callback != null) {
            callback.onDismiss();
        }
        super.onDismiss(dialog);
    }
}
