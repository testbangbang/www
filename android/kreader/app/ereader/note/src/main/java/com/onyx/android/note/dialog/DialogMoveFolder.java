package com.onyx.android.note.dialog;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.onyx.android.note.R;
import com.onyx.android.note.utils.NoteAppConfig;
import com.onyx.android.note.utils.Utils;
import com.onyx.android.sdk.data.GAdapter;
import com.onyx.android.sdk.data.GAdapterUtil;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;
import com.onyx.android.sdk.ui.utils.TouchDirection;
import com.onyx.android.sdk.ui.view.ContentItemView;
import com.onyx.android.sdk.ui.view.ContentView;
import com.onyx.android.sdk.utils.StringUtils;

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
    //Use to string to avoid random id coincidentally same as this flag.
    String targetParentID = Boolean.toString(Boolean.FALSE);

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
        final boolean isUseMXStyle = NoteAppConfig.sharedInstance(getActivity()).useMXUIStyle();
        Params params = new Params().setTittleString(getString(R.string.move))
                .setCustomContentLayoutResID(R.layout.alert_dialog_content_move_folder)
                .setCustomLayoutHeight((int) (5 * getResources().getDimension(R.dimen.dialog_move_folder_item_height)))
                .setEnableNegativeButton(false)
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
                        final ContentView targetLibraryContentView = (ContentView) customView.findViewById(R.id.contentView_move_folder);
                        targetLibraryContentView.setupGridLayout(5, 1);
                        targetLibraryContentView.setTouchDirection(TouchDirection.VERTICAL);
                        GAdapter adapter = Utils.adapterFromNoteModelListWithFullPathTitle(dataList,
                                R.drawable.ic_student_note_folder_gray,
                                R.drawable.ic_student_note_pic_gray);
                        HashMap<String, Integer> mapping = new HashMap<String, Integer>();
                        mapping.put(GAdapterUtil.TAG_TITLE_STRING, R.id.textview_title);
                        mapping.put(GAdapterUtil.TAG_DIVIDER_VIEW, R.id.divider);
                        mapping.put(GAdapterUtil.TAG_SELECTABLE,R.id.target_folder_checkbox);
                        targetLibraryContentView.setSubLayoutParameter(isUseMXStyle ?
                                R.layout.mx_dialog_move_folder_item : R.layout.onyx_dialog_move_folder_item, mapping);
                        targetLibraryContentView.setShowPageInfoArea(false);
                        targetLibraryContentView.setAdapter(adapter, 0);
                        targetLibraryContentView.setCallback(new ContentView.ContentViewCallback() {
                            @Override
                            public void onItemClick(ContentItemView view) {
                                GObject temp = view.getData();
                                int dataIndex = targetLibraryContentView.getCurrentAdapter().getGObjectIndex(temp);
                                temp.putBoolean(GAdapterUtil.TAG_SELECTABLE, true);
                                targetParentID = GAdapterUtil.getUniqueId(temp);
                                targetLibraryContentView.getCurrentAdapter().setObject(dataIndex, temp);
                                targetLibraryContentView.unCheckOtherViews(dataIndex, true);
                                targetLibraryContentView.updateCurrentPage();
                            }
                        });
                    }
                });
        if (isUseMXStyle) {
            params.setCustomLayoutResID(R.layout.mx_custom_alert_dialog);
        }
        setParams(params);
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
