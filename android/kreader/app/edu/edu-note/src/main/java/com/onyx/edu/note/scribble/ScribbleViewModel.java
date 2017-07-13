package com.onyx.edu.note.scribble;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;
import com.onyx.android.sdk.scribble.request.ShapeDataInfo;
import com.onyx.android.sdk.scribble.request.note.NoteDocumentOpenRequest;
import com.onyx.android.sdk.utils.DateTimeUtil;
import com.onyx.edu.note.NoteManager;
import com.onyx.edu.note.actions.scribble.DocumentAddNewPageAction;
import com.onyx.edu.note.actions.scribble.DocumentCreateAction;
import com.onyx.edu.note.actions.scribble.DocumentDeletePageAction;
import com.onyx.edu.note.actions.scribble.DocumentEditAction;
import com.onyx.edu.note.actions.scribble.DocumentSaveAction;
import com.onyx.edu.note.actions.scribble.GotoNextPageAction;
import com.onyx.edu.note.actions.scribble.GotoPrevPageAction;
import com.onyx.edu.note.actions.scribble.RedoAction;
import com.onyx.edu.note.actions.scribble.UndoAction;
import com.onyx.edu.note.data.ScribbleAction;

import java.util.Date;
import java.util.List;

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
    public final ObservableList<Integer> mMainMenuIDList = new ObservableArrayList<>();
    private final ObservableField<NoteModel> mCurrentNoteModel = new ObservableField<>();
    private final ObservableField<ShapeDataInfo> mShapeDataInfo = new ObservableField<>();
    public final ObservableField<String> mNoteTitle = new ObservableField<>();

    void setNavigator(ScribbleNavigator mScribbleNavigator) {
        this.mNavigator = mScribbleNavigator;
    }

    private ScribbleNavigator mNavigator;
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

    public void onPrevPage() {
        mNoteManager.syncWithCallback(true, true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                GotoPrevPageAction prevPageAction = new GotoPrevPageAction();
                prevPageAction.execute(mNoteManager, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        onRequestFinished((BaseNoteRequest) request, e);
                    }
                });
            }
        });
    }

    public void onNextPage() {
        mNoteManager.syncWithCallback(true, true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                GotoNextPageAction nextPageAction = new GotoNextPageAction();
                nextPageAction.execute(mNoteManager, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        onRequestFinished((BaseNoteRequest) request, e);
                    }
                });
            }
        });
    }

    public void addPage() {
        mNoteManager.syncWithCallback(true, true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                DocumentAddNewPageAction addNewPageAction = new DocumentAddNewPageAction();
                addNewPageAction.execute(mNoteManager, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        onRequestFinished((BaseNoteRequest) request, e);
                    }
                });
            }
        });
    }

    public void deletePage() {
        mNoteManager.syncWithCallback(true, true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                DocumentDeletePageAction deletePageAction = new DocumentDeletePageAction();
                deletePageAction.execute(mNoteManager, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        onRequestFinished((BaseNoteRequest) request, e);
                    }
                });
            }
        });
    }

    public void reDo() {
        mNoteManager.syncWithCallback(false, true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                RedoAction reDoAction = new RedoAction();
                reDoAction.execute(mNoteManager, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        onRequestFinished((BaseNoteRequest) request, e);
                    }
                });
            }
        });
    }

    public void unDo() {
        mNoteManager.syncWithCallback(false, true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                UndoAction unDoAction = new UndoAction();
                unDoAction.execute(mNoteManager, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        onRequestFinished((BaseNoteRequest) request, e);
                    }
                });
            }
        });
    }

    private void onRequestFinished(BaseNoteRequest request, Throwable throwable) {
        if (!request.isAbort() && throwable == null) {
            updateInfo(request);
        }
    }

    private void updateInfo(BaseNoteRequest request) {
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

    public void onSaveDocument(boolean closeAfterSave) {
        onSaveDocument(closeAfterSave, null);
    }

    void onSaveDocument(boolean closeAfterSave, BaseCallback callback) {
        DocumentSaveAction documentSaveAction = new DocumentSaveAction(mCurrentDocumentUniqueID,
                mNoteTitle.get(), closeAfterSave);
        documentSaveAction.execute(mNoteManager, callback);
    }

    void setMainMenuIDList(List<Integer> mainMenuIDList) {
        mMainMenuIDList.clear();
        mMainMenuIDList.addAll(mainMenuIDList);
    }
}
