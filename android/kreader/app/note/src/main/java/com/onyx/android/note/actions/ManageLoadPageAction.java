package com.onyx.android.note.actions;

import com.onyx.android.note.R;
import com.onyx.android.note.activity.ManageActivity;
import com.onyx.android.note.utils.Constant;
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

public class ManageLoadPageAction<T extends ManageActivity> extends BaseNoteAction<T> {
    public ManageLoadPageAction(List<String> list) {
        this.toLoadIDList = list;
    }

    List<String> toLoadIDList;


    @Override
    public void execute(final T activity, final BaseCallback callback) {
        final NoteLoadThumbnailByUIDRequest loadThumbnailByUIDRequest = new NoteLoadThumbnailByUIDRequest(toLoadIDList,
                Constant.PERTIME_THUMBNAIL_LOAD_LIMIT);
        activity.getNoteViewHelper().submit(activity, loadThumbnailByUIDRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    return;
                }

                ArrayList<String> updateTagList = new ArrayList<>();
                updateTagList.add(GAdapterUtil.TAG_THUMBNAIL);
                GAdapterUtil.updateAdapterContent(activity.getContentView().getCurrentAdapter(),
                        Utils.adapterFromNoteModelList(loadThumbnailByUIDRequest.getNoteList(),
                                R.drawable.ic_student_note_folder_gray,
                                R.drawable.ic_student_note_pic_gray),
                        GAdapterUtil.TAG_UNIQUE_ID, updateTagList, activity.getLookupTable(), false);
                activity.getContentView().updateCurrentPage(true, false);
                if (callback != null) {
                    callback.done(request, e);
                }
            }
        });
    }
}
