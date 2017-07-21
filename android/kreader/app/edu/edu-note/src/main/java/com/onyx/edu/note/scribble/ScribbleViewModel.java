package com.onyx.edu.note.scribble;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.note.NoteDocumentOpenRequest;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.request.ShapeDataInfo;
import com.onyx.android.sdk.utils.DateTimeUtil;
import com.onyx.edu.note.NoteManager;
import com.onyx.edu.note.actions.scribble.DocumentCreateAction;
import com.onyx.edu.note.actions.scribble.DocumentEditAction;
import com.onyx.edu.note.data.ScribbleAction;
import com.onyx.edu.note.data.ScribbleFunctionBarMenuID;

import java.util.Date;
import java.util.List;

/**
 * Created by solskjaer49 on 2017/6/22 11:56.
 */

public class ScribbleViewModel extends BaseObservable {
    private static final String TAG = ScribbleViewModel.class.getSimpleName();

    // These observable fields will update Views automatically
    public final ObservableInt mCurrentPage = new ObservableInt();
    public final ObservableInt mTotalPage = new ObservableInt();
    public final ObservableList<Integer> mFunctionBarMenuIDList = new ObservableArrayList<>();
    public final ObservableList<Integer> mToolBarMenuIDList = new ObservableArrayList<>();
    private final ObservableField<NoteModel> mCurrentNoteModel = new ObservableField<>();
    private final ObservableField<ShapeDataInfo> mShapeDataInfo = new ObservableField<>();
    public final ObservableField<String> mNoteTitle = new ObservableField<>();

    void setNavigator(ScribbleNavigator mScribbleNavigator) {
        this.mNavigator = mScribbleNavigator;
    }

    private ScribbleNavigator mNavigator;
    private NoteManager mNoteManager;

    public String getCurrentDocumentUniqueID() {
        return mCurrentDocumentUniqueID;
    }

    private String mCurrentDocumentUniqueID;

    ScribbleViewModel(Context context) {
        // Force use of Application Context.
        mNoteManager = NoteManager.sharedInstance(context.getApplicationContext());
    }

    void start(String uniqueID, String parentID, @ScribbleAction.ScribbleActionDef int action, final BaseCallback callback) {
        mCurrentDocumentUniqueID = uniqueID;
        BaseCallback baseCallback = new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (!request.isAbort() && e == null) {
                    NoteDocumentOpenRequest req = (NoteDocumentOpenRequest) request;
                    updateInfo(req);
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

    //Todo:temp use navigator communicate with handler manager.

    public void onPrevPage() {
        mNavigator.onFunctionBarMenuFunctionItem(ScribbleFunctionBarMenuID.PREV_PAGE);
    }

    public void onNextPage() {
        mNavigator.onFunctionBarMenuFunctionItem(ScribbleFunctionBarMenuID.NEXT_PAGE);
    }

    public void addPage() {
        mNavigator.onFunctionBarMenuFunctionItem(ScribbleFunctionBarMenuID.ADD_PAGE);
    }

    public void deletePage() {
        mNavigator.onFunctionBarMenuFunctionItem(ScribbleFunctionBarMenuID.DELETE_PAGE);
    }

    public void onRequestFinished(AsyncBaseNoteRequest request, Throwable throwable) {
        if (!request.isAbort() && throwable == null) {
            updateInfo(request);
        }
    }

    private void updateInfo(AsyncBaseNoteRequest request) {
        mNoteManager.setShapeDataInfo(request.getShapeDataInfo());
        mShapeDataInfo.set(mNoteManager.getShapeDataInfo());
        mCurrentPage.set(mShapeDataInfo.get().getHumanReadableCurPageIndex());
        mTotalPage.set(mShapeDataInfo.get().getPageCount());
    }

    void onActivityDestroyed() {
        // Clear references to avoid potential memory leaks.
        mNavigator = null;
    }

    void setDocumentTitle(String title) {
        mNoteTitle.set(title);
    }

    public void setFunctionBarMenuIDList(List<Integer> functionBarMenuIDList) {
        mFunctionBarMenuIDList.clear();
        mFunctionBarMenuIDList.addAll(functionBarMenuIDList);
    }

    public void setToolBarMenuIDList(List<Integer> toolBarMenuIDList) {
        mToolBarMenuIDList.clear();
        mToolBarMenuIDList.addAll(toolBarMenuIDList);
    }
}
