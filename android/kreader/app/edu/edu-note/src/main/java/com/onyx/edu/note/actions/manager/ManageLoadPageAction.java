package com.onyx.edu.note.actions.manager;

import com.onyx.edu.note.actions.BaseNoteAction;

/**
 * Created by solskjaer49 on 16/7/15 17:51.
 */

public abstract class ManageLoadPageAction extends BaseNoteAction {
//    public ManageLoadPageAction(List<String> list) {
//        this.toLoadIDList = list;
//    }
//
//    private NoteLoadThumbnailByUIDRequest loadThumbnailByUIDRequest;
//
//    private List<String> toLoadIDList;
//
//    public void execute(final T activity) {
//        execute(activity, new BaseCallback() {
//            @Override
//            public void done(BaseRequest request, Throwable e) {
//                if (e != null) {
//                    return;
//                }
//                ArrayList<String> updateTagList = new ArrayList<>();
//                updateTagList.add(GAdapterUtil.TAG_THUMBNAIL);
//                GAdapterUtil.updateAdapterContent(activity.getContentView().getCurrentAdapter(),
//                        Utils.adapterFromNoteModelList(loadThumbnailByUIDRequest.getNoteList(),
//                                NoteAppConfig.sharedInstance(activity).getFolderIconRes(),
//                                R.drawable.ic_student_note_pic_gray),
//                        GAdapterUtil.TAG_UNIQUE_ID, updateTagList, activity.getLookupTable(), false);
//                activity.getContentView().updateCurrentPage(true, false);
//            }
//        });
//    }
//
//    @Override
//    public void execute(final T activity, final BaseCallback callback) {
//        loadThumbnailByUIDRequest = new NoteLoadThumbnailByUIDRequest(toLoadIDList,
//                Constant.PER_PAGE_THUMBNAIL_LOAD_LIMIT);
//        activity.submitRequest(loadThumbnailByUIDRequest, callback);
//    }
}
