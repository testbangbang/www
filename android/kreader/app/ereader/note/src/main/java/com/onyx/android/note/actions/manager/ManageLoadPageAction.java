package com.onyx.android.note.actions.manager;

import com.onyx.android.note.R;
import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.note.activity.BaseManagerActivity;
import com.onyx.android.note.utils.Constant;
import com.onyx.android.note.utils.NoteAppConfig;
import com.onyx.android.note.utils.Utils;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.GAdapterUtil;
import com.onyx.android.sdk.scribble.request.note.NoteLoadThumbnailByUIDRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by solskjaer49 on 16/7/15 17:51.
 */

public class ManageLoadPageAction<T extends BaseManagerActivity> extends BaseNoteAction<T> {
    public ManageLoadPageAction(List<String> list) {
        this.toLoadIDList = list;
    }

    private NoteLoadThumbnailByUIDRequest loadThumbnailByUIDRequest;

    private List<String> toLoadIDList;

    public void execute(final T activity) {
        execute(activity, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    return;
                }
                ArrayList<String> updateTagList = new ArrayList<>();
                updateTagList.add(GAdapterUtil.TAG_THUMBNAIL);
                GAdapterUtil.updateAdapterContent(activity.getContentView().getCurrentAdapter(),
                        Utils.adapterFromNoteModelList(loadThumbnailByUIDRequest.getNoteList(),
                                NoteAppConfig.sharedInstance(activity).getFolderIconRes(),
                                R.drawable.ic_student_note_pic_gray),
                        GAdapterUtil.TAG_UNIQUE_ID, updateTagList, activity.getLookupTable(), false);
                activity.getContentView().updateCurrentPage(true, false);
            }
        });
    }

    @Override
    public void execute(final T activity, final BaseCallback callback) {
        loadThumbnailByUIDRequest = new NoteLoadThumbnailByUIDRequest(toLoadIDList,
                Constant.PERTIME_THUMBNAIL_LOAD_LIMIT);
        activity.submitRequest(loadThumbnailByUIDRequest, callback);
    }
}
