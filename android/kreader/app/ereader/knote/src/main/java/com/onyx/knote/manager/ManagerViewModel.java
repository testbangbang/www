package com.onyx.knote.manager;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.asyncrequest.note.NoteLibraryLoadRequest;
import com.onyx.android.sdk.scribble.data.AscDescOrder;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.data.SortBy;
import com.onyx.knote.NoteApplication;
import com.onyx.knote.actions.common.CheckNoteNameLegalityAction;
import com.onyx.knote.actions.manager.CreateLibraryAction;
import com.onyx.knote.actions.manager.LoadNoteListAction;
import com.onyx.knote.actions.manager.NoteLibraryRemoveAction;
import com.onyx.knote.util.NotePreference;
import com.onyx.knote.util.Utils;

import java.util.List;


/**
 * Created by solskjaer49 on 2017/6/6 18:12.
 * All Data logic should place here.
 * e.g. remove note/folder,add folder.
 * all data logic can use asynchronous thread.
 * because dataBinding framework will handle the sync.(By using Observable Mode).
 */

public class ManagerViewModel extends BaseObservable {
    private static final String TAG = ManagerViewModel.class.getSimpleName();

    private Context mContext; // To avoid leaks, this must be an Application Context.

    // These observable fields will update Views automatically
    public final ObservableList<NoteModel> items = new ObservableArrayList<>();
    public final ObservableInt folderCount = new ObservableInt();
    public final ObservableInt noteCount = new ObservableInt();
    public final ObservableInt currentPage = new ObservableInt();
    public final ObservableInt totalPage = new ObservableInt();
    private final ObservableField<NoteModel> currentNoteModel = new ObservableField<>();
    public final ObservableField<String> currentFolderTitle = new ObservableField<>();
    private NoteManager mNoteManager;

    public int getCurrentSortBy() {
        return currentSortBy;
    }

    private @SortBy.SortByDef
    int currentSortBy = SortBy.UPDATED_AT;
    private @AscDescOrder.AscDescOrderDef
    int ascOrder = AscDescOrder.DESC;

    void setNavigator(ManagerNavigator navigator) {
        this.mNavigator = navigator;
    }

    private ManagerNavigator mNavigator;

    public void start() {
        loadSortByAndAsc();
        loadData(true, getCurrentNoteModelUniqueID());
    }

    ManagerViewModel(Context context) {
        // Force use of Application Context.
        mContext = context.getApplicationContext();
        mNoteManager = NoteApplication.getInstance().getNoteManager();
        items.addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<NoteModel>>() {
            @Override
            public void onChanged(ObservableList<NoteModel> sender) {
            }

            @Override
            public void onItemRangeChanged(ObservableList<NoteModel> sender, int positionStart, int itemCount) {
                updateTypeCount(sender);
            }

            @Override
            public void onItemRangeInserted(ObservableList<NoteModel> sender, int positionStart, int itemCount) {
                updateTypeCount(sender);
            }

            @Override
            public void onItemRangeMoved(ObservableList<NoteModel> sender, int fromPosition, int toPosition, int itemCount) {
                updateTypeCount(sender);
            }

            @Override
            public void onItemRangeRemoved(ObservableList<NoteModel> sender, int positionStart, int itemCount) {
                updateTypeCount(sender);
            }
        });
    }

    private void updateTypeCount(ObservableList<NoteModel> sender) {
        int folderTotal = 0, noteTotal = 0;
        for (NoteModel model : sender) {
            switch (model.getType()) {
                case NoteModel.TYPE_DOCUMENT:
                    folderTotal++;
                    break;
                case NoteModel.TYPE_LIBRARY:
                    noteTotal++;
                    break;
            }
        }
        folderCount.set(folderTotal);
        noteCount.set(noteTotal);
    }

    void setPageStatus(int curPage, int allPage) {
        currentPage.set(curPage);
        totalPage.set(allPage);
    }

    boolean goUp() {
        if (isTopLevelFolder()) {
            return false;
        } else {
            loadData(true, currentNoteModel.get().getParentUniqueId());
            return true;
        }
    }

    private boolean isTopLevelFolder() {
        return (currentNoteModel.get() == null || (TextUtils.isEmpty(currentNoteModel.get().getParentUniqueId())
                && (TextUtils.isEmpty(currentNoteModel.get().getUniqueId()))));
    }

    //TODO:better method name?
    void loadData() {
        loadData(true, getCurrentNoteModelUniqueID());
    }

    @Nullable
    String getCurrentNoteModelUniqueID() {
        return isTopLevelFolder() ? null : currentNoteModel.get().getUniqueId();
    }

    void loadData(boolean forceUpdate, String id) {
        if (forceUpdate) {
            items.clear();
            LoadNoteListAction action = new LoadNoteListAction(id, currentSortBy, ascOrder);
            action.execute(mNoteManager, new BaseCallback() {
                @Override
                public void done(BaseRequest request, Throwable e) {
                    NoteLibraryLoadRequest req = (NoteLibraryLoadRequest) request;
                    items.add(Utils.createAddNoteItem(mContext));
                    items.addAll(req.getNoteList());
                    currentNoteModel.set(req.getNoteModel());
                    currentFolderTitle.set((isTopLevelFolder() || currentNoteModel.get().isDocument())
                            ? null : currentNoteModel.get().getTitle());
                }
            });
        }
    }

    void onActivityDestroyed() {
        // Clear references to avoid potential memory leaks.
        mNavigator = null;
    }

    void addFolder(final String folderTitle) {
        final CheckNoteNameLegalityAction action =
                new CheckNoteNameLegalityAction(folderTitle, getCurrentNoteModelUniqueID(),
                        NoteModel.TYPE_LIBRARY, false, false);
        action.execute(mNoteManager, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (action.isLegal()) {
                    final CreateLibraryAction action =
                            new CreateLibraryAction(getCurrentNoteModelUniqueID(), folderTitle);
                    action.execute(mNoteManager, new BaseCallback() {
                        @Override
                        public void done(BaseRequest request, Throwable e) {
                            mNavigator.updateFolderCreateStatus(e == null);
                        }
                    });
                } else {
                    mNavigator.showNewFolderTitleIllegal();
                }
            }
        });
    }

    void deleteNote(List<String> targetIDList) {
        NoteLibraryRemoveAction action = new NoteLibraryRemoveAction(targetIDList);
        action.execute(mNoteManager, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                mNavigator.updateNoteRemoveStatus(e == null);
            }
        });
    }

    public void saveSortByAscArgs(@SortBy.SortByDef int sortBy, @AscDescOrder.AscDescOrderDef int ascOrder) {
        currentSortBy = sortBy;
        this.ascOrder = ascOrder;
        NotePreference.setIntValue(NotePreference.KEY_NOTE_SORT_BY, sortBy);
        NotePreference.setIntValue(NotePreference.KEY_NOTE_ASC_ORDER, ascOrder);
    }

    private void loadSortByAndAsc() {
        currentSortBy = SortBy.translate(NotePreference.getIntValue(mContext, NotePreference.KEY_NOTE_SORT_BY, SortBy.CREATED_AT));
        ascOrder = AscDescOrder.translate(NotePreference.getIntValue(mContext, NotePreference.KEY_NOTE_ASC_ORDER, AscDescOrder.DESC));
    }

}
