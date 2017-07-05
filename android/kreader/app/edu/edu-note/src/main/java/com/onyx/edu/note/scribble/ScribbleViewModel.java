package com.onyx.edu.note.scribble;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.request.ShapeDataInfo;
import com.onyx.android.sdk.scribble.request.note.NoteDocumentOpenRequest;
import com.onyx.android.sdk.utils.DateTimeUtil;
import com.onyx.edu.note.NoteManager;
import com.onyx.edu.note.actions.scribble.DocumentCreateAction;
import com.onyx.edu.note.actions.scribble.DocumentEditAction;
import com.onyx.edu.note.actions.scribble.DocumentSaveAction;
import com.onyx.edu.note.data.ScribbleAction;

import java.util.Date;

/**
 * Created by solskjaer49 on 2017/6/22 11:56.
 */

public class ScribbleViewModel extends BaseObservable {
    private static final String TAG = ScribbleViewModel.class.getSimpleName();

    // To avoid leaks, this must be an Application Context.
    private Context mContext;

    // These observable fields will update Views automatically
    public final ObservableInt mCurrentPage = new ObservableInt();
    public final ObservableInt mTotalPage = new ObservableInt();
    private final ObservableField<NoteModel> mCurrentNoteModel = new ObservableField<>();
    private final ObservableField<ShapeDataInfo> mShapeDataInfo = new ObservableField<>();
    public final ObservableField<String> mNoteTitle = new ObservableField<>();
    private ScribbleNavigator mScribbleNavigator;
    private NoteManager mNoteManager;
    private String mCurrentDocumentUniqueID;

    ScribbleViewModel(Context context) {
        // Force use of Application Context.
        this.mContext = context.getApplicationContext();
        mNoteManager = NoteManager.sharedInstance(mContext);
    }

    void start(String uniqueID, String parentID, @ScribbleAction.ScribbleActionDef int action, final BaseCallback callback) {
        mCurrentDocumentUniqueID = uniqueID;
        BaseCallback baseCallback = new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (!request.isAbort() && e == null) {
                    NoteDocumentOpenRequest req = (NoteDocumentOpenRequest) request;
                    mNoteManager.setShapeDataInfo(req.getShapeDataInfo());
                    mShapeDataInfo.set(mNoteManager.getShapeDataInfo());
                    mCurrentNoteModel.set(req.getNoteModel());
                    mNoteTitle.set(mCurrentNoteModel.get() != null ? mCurrentNoteModel.get().getTitle() :
                            DateTimeUtil.formatDate(new Date()));
                }
                BaseCallback.invoke(callback, request, e);
            }
        };
        switch (action) {
            case ScribbleAction.CREATE:
                DocumentCreateAction createAction = new DocumentCreateAction(uniqueID, parentID);
                createAction.execute(mNoteManager, baseCallback);
                break;
            case ScribbleAction.EDIT:
                DocumentEditAction editAction = new DocumentEditAction(uniqueID, parentID);
                editAction.execute(mNoteManager, baseCallback);
                break;
        }
    }

    public void onPrevPage() {

    }

    public void onNextPage() {

    }

    void setDocumentTitle(String title) {
        mNoteTitle.set(title);
    }

    void onSaveDocument(boolean closeAfterSave, BaseCallback callback) {
        DocumentSaveAction documentSaveAction = new DocumentSaveAction(mCurrentDocumentUniqueID,
                mNoteTitle.get(), closeAfterSave);
        documentSaveAction.execute(mNoteManager, callback);
    }
}
