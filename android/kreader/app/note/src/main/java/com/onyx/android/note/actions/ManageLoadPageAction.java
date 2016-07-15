package com.onyx.android.note.actions;

import com.onyx.android.note.R;
import com.onyx.android.note.activity.ManageActivity;
import com.onyx.android.note.utils.Constant;
import com.onyx.android.note.utils.Utils;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.GAdapterUtil;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.scribble.request.note.NoteLoadThumbnailByUIDRequest;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.onyx.android.sdk.data.GAdapterUtil.hasThumbnail;

/**
 * Created by solskjaer49 on 16/7/15 17:51.
 */

public class ManageLoadPageAction<T extends ManageActivity> extends BaseNoteAction<T> {
    private boolean forceUpdate;
    private int targetPage;

    public ManageLoadPageAction(int targetPage, boolean forceUpdate) {
        this.targetPage = targetPage;
        this.forceUpdate = forceUpdate;
    }

    public ManageLoadPageAction(int targetPage) {
        this(targetPage, false);
    }

    @Override
    public void execute(final T activity, final BaseCallback callback) {
        activity.getLookupTable().clear();
        final int begin = activity.getContentView().getPageBegin(targetPage);
        final int end = activity.getContentView().getPageEnd(targetPage);
        if (begin < 0 || end < 0 || GAdapterUtil.isNullOrEmpty(activity.getContentView().getCurrentAdapter())) {
            return;
        }

        List<String> list = new ArrayList<String>();
        for (int i = begin; i <= end && i < activity.getContentView().getCurrentAdapter().size(); ++i) {
            GObject object = activity.getContentView().getCurrentAdapter().get(i);
            if (!hasThumbnail(object) || forceUpdate) {
                final String key = GAdapterUtil.getUniqueId(object);
                if (!StringUtils.isNullOrEmpty(key)) {
                    activity.getLookupTable().put(key, i);
                    list.add(key);
                }
            }
        }

        final NoteLoadThumbnailByUIDRequest loadThumbnailByUIDRequest = new NoteLoadThumbnailByUIDRequest(list,
                Constant.PERTIME_THUMBNAIL_LOAD_LIMIT);
        activity.getNoteViewHelper().submit(activity, loadThumbnailByUIDRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    return;
                }

                GAdapterUtil.updateAdapterContent(activity.getContentView().getCurrentAdapter(),
                        Utils.adapterFromNoteModelList(loadThumbnailByUIDRequest.getNoteList(),
                                R.drawable.ic_student_note_folder_gray,
                                R.drawable.ic_student_note_pic_gray),
                        GAdapterUtil.TAG_UNIQUE_ID, activity.getLookupTable());
                activity.getContentView().updateCurrentPage(true, false);
                if (callback != null) {
                    callback.done(request, e);
                }
            }
        });
    }
}
