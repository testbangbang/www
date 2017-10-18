package com.onyx.android.sun.scribble;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.asyncrequest.note.NoteDocumentOpenRequest;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.request.ShapeDataInfo;
import com.onyx.android.sdk.utils.DateTimeUtil;
import com.onyx.android.sun.SunApplication;


import org.greenrobot.eventbus.Subscribe;

import java.util.Date;

/**
 * Created by solskjaer49 on 2017/6/22 11:56.
 */

public class ScribbleViewModel extends BaseObservable {
    private static final String TAG = ScribbleViewModel.class.getSimpleName();

    // These observable fields will update Views automatically
    public final ObservableInt mCurrentPage = new ObservableInt();
    public final ObservableInt mTotalPage = new ObservableInt();
    private final ObservableField<ShapeDataInfo> mShapeDataInfo = new ObservableField<>();
    public final ObservableField<String> mNoteTitle = new ObservableField<>();
    private boolean mIsKeyboardInput = false;
    private boolean mIsBuildingSpan = false;
    private NoteManager mNoteManager;
    private String mParentID;

    public String getCurrentDocumentUniqueID() {
        return mCurrentDocumentUniqueID;
    }

    public String getParentUniqueID() {
        return mParentID;
    }

    private String mCurrentDocumentUniqueID;

    ScribbleViewModel(Context context) {
        // Force use of Application Context.
        mNoteManager = SunApplication.getInstence().getNoteManager();
        mNoteManager.registerEventBus(this);
    }

    void start(String uniqueID, String parentID, @ScribbleAction.ScribbleActionDef int action, final BaseCallback callback) {
        mCurrentDocumentUniqueID = uniqueID;
        mParentID = parentID;
        BaseCallback baseCallback = new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (!request.isAbort() && e == null) {
                    NoteDocumentOpenRequest req = (NoteDocumentOpenRequest) request;
                    updateInfo(req);
                    NoteModel currentNoteModel = req.getNoteModel();
                    mNoteTitle.set(currentNoteModel != null ? currentNoteModel.getTitle() :
                            DateTimeUtil.formatDate(new Date()));
                    AsyncBaseNoteRequest noteRequest = (AsyncBaseNoteRequest)request;
                    mNoteManager.post(new RequestInfoUpdateEvent(noteRequest.getShapeDataInfo(), request, e));
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

    public boolean isKeyboardInput() {
        return mIsKeyboardInput;
    }

    public void setKeyboardInput(boolean isKeyboardInput) {
        mIsKeyboardInput = isKeyboardInput;
    }

    public boolean isBuildingSpan() {
        return mIsBuildingSpan;
    }

    public void setBuildingSpan(boolean isBuildingSpan) {
        mIsBuildingSpan = isBuildingSpan;
    }

    private void updateInfo(AsyncBaseNoteRequest request) {
        mShapeDataInfo.set(mNoteManager.getShapeDataInfo());
        mCurrentPage.set(mShapeDataInfo.get().getHumanReadableCurPageIndex());
        mTotalPage.set(mShapeDataInfo.get().getPageCount());
    }

    void onActivityDestroyed() {
        // Clear references to avoid potential memory leaks.
        mNoteManager.unregisterEventBus(this);
    }

    @Subscribe
    public void onRequestFinished(RequestInfoUpdateEvent event) {
        if (!event.getRequest().isAbort() && event.getThrowable() == null) {
            updateInfo(event.getRequest());
        }
    }
}
