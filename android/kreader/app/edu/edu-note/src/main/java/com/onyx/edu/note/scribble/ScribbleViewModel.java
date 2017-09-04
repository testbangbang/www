package com.onyx.edu.note.scribble;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;
import android.util.SparseArray;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.note.NoteDocumentOpenRequest;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.request.ShapeDataInfo;
import com.onyx.android.sdk.utils.DateTimeUtil;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.edu.note.NoteApplication;
import com.onyx.edu.note.actions.scribble.DocumentCreateAction;
import com.onyx.edu.note.actions.scribble.DocumentEditAction;
import com.onyx.edu.note.data.ScribbleAction;
import com.onyx.edu.note.data.ScribbleFunctionBarMenuID;
import com.onyx.edu.note.scribble.event.HandlerActivateEvent;
import com.onyx.edu.note.scribble.event.RequestInfoUpdateEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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
    private SparseArray<List<Integer>> mFunctionBarMenuSubMenuIDListSparseArray = new SparseArray<>();
    private boolean mIsKeyboardInput = false;
    private boolean mIsBuildingSpan = false;

    void setNavigator(ScribbleNavigator mScribbleNavigator) {
        this.mNavigator = mScribbleNavigator;
    }

    private ScribbleNavigator mNavigator;
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
        mNoteManager = NoteApplication.getInstance().getNoteManager();
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
                    mCurrentNoteModel.set(req.getNoteModel());
                    mNoteTitle.set(mCurrentNoteModel.get() != null ? mCurrentNoteModel.get().getTitle() :
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

    private void updateInfo(AsyncBaseNoteRequest request) {
        mShapeDataInfo.set(mNoteManager.getShapeDataInfo());
        mCurrentPage.set(mShapeDataInfo.get().getHumanReadableCurPageIndex());
        mTotalPage.set(mShapeDataInfo.get().getPageCount());
    }

    void onActivityDestroyed() {
        // Clear references to avoid potential memory leaks.
        mNoteManager.unregisterEventBus(this);
        mNavigator = null;
    }

    void setDocumentTitle(String title) {
        mNoteTitle.set(title);
    }

    List<Integer> getSubMenuIDList(int mainMenuID) {
        return mFunctionBarMenuSubMenuIDListSparseArray.get(mainMenuID);
    }

    private void setFunctionBarMenuIDList(List<Integer> functionBarMenuIDList) {
        mFunctionBarMenuIDList.clear();
        mFunctionBarMenuIDList.addAll(functionBarMenuIDList);
    }

    private void setToolBarMenuIDList(List<Integer> toolBarMenuIDList) {
        mToolBarMenuIDList.clear();
        mToolBarMenuIDList.addAll(toolBarMenuIDList);
    }

    @Subscribe
    public void onRequestFinished(RequestInfoUpdateEvent event) {
        if (!event.getRequest().isAbort() && event.getThrowable() == null) {
            updateInfo(event.getRequest());
        }
    }

    @Subscribe
    public void onHandlerActivate(HandlerActivateEvent activateEvent) {
        setFunctionBarMenuIDList(activateEvent.getFunctionBarMenuFunctionIDList());
        setToolBarMenuIDList(activateEvent.getToolBarMenuFunctionIDList());
        mFunctionBarMenuSubMenuIDListSparseArray = activateEvent.getFunctionBarMenuSubMenuIDListSparseArray();
    }

}
