package com.onyx.edu.note.manager;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;
import android.graphics.BitmapFactory;

import com.onyx.edu.note.R;
import com.onyx.edu.note.data.NoteModel;
import com.onyx.edu.note.data.NoteType;

/**
 * Created by solskjaer49 on 2017/6/6 18:12.
 * All Data logic should place here.
 * e.g. remove note/folder,add folder.
 * all data logic can use asynchronous thread.
 * because dataBinding framework will handle the sync.(By using Observable Mode).
 */

public class ManagerViewModel extends BaseObservable {
    static final String TAG = ManagerViewModel.class.getSimpleName();

    private Context mContext; // To avoid leaks, this must be an Application Context.

    // These observable fields will update Views automatically
    public final ObservableList<NoteModel> items = new ObservableArrayList<>();
    public final ObservableInt folderCount = new ObservableInt();
    public final ObservableInt noteCount = new ObservableInt();
    public final ObservableInt currentPage = new ObservableInt();
    public final ObservableInt totalPage = new ObservableInt();

    public void setNavigator(ManagerNavigator navigator) {
        this.mNavigator = navigator;
    }

    ManagerNavigator mNavigator;

    public void start() {
        loadData(true);
    }

    public ManagerViewModel(
            Context context) {
        mContext = context.getApplicationContext(); // Force use of Application Context.
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
                case NoteType.FOLDER:
                    folderTotal++;
                    break;
                case NoteType.NOTE:
                    noteTotal++;
                    break;
            }
        }
        folderCount.set(folderTotal);
        noteCount.set(noteTotal);
    }

    public void setPageStatus(int curPage, int allPage) {
        currentPage.set(curPage);
        totalPage.set(allPage);
    }

    public void loadData(boolean forceUpdate) {
        if (forceUpdate) {
            //TODO:test code.
            items.clear();
            items.add(NoteModel.buildCreateNoteModel(mContext));
            for (int i = 0; i < 12; i++) {
                NoteModel model = new NoteModel();
                model.setUniqueID(Integer.toString(i));
                model.setDocumentName("note" + i);
                model.setLastModifiedDate("2017-5-" + i);
                model.setType(NoteType.NOTE);
                model.setThumbnail(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.unknown_application));
                items.add(model);
            }
        }
    }

    void onActivityDestroyed() {
        // Clear references to avoid potential memory leaks.
        mNavigator = null;
    }

}
