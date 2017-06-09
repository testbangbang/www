package com.onyx.edu.note.manager;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Observable;
import android.databinding.ObservableField;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.onyx.edu.note.data.NoteModel;
import com.onyx.edu.note.data.NoteType;

import java.lang.ref.WeakReference;

/**
 * Created by solskjaer49 on 2017/6/8 16:42.
 */

public class ManagerItemViewModel extends BaseObservable {
    private final ObservableField<NoteModel> mNoteObservable = new ObservableField<>();

    // This navigator is s wrapped in a WeakReference to avoid leaks because it has references to an
    // activity. There's no straightforward way to clear it for each item in a recycler adapter.
    @Nullable
    private WeakReference<ManagerItemNavigator> mNavigator;

    public final ObservableField<String> documentName = new ObservableField<>();
    public final ObservableField<String> lastModifiedDate = new ObservableField<>();
    public final ObservableField<Bitmap> thumbnail = new ObservableField<>();
    private final Context mContext;


    public ManagerItemViewModel(Context context) {
        mContext = context.getApplicationContext();// Force use of Application Context.
        // Exposed observables depend on the mNoteObservable observable:
        mNoteObservable.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                NoteModel note = mNoteObservable.get();
                if (note != null) {
                    documentName.set(note.getDocumentName());
                    lastModifiedDate.set(note.getLastModifiedDate());
                    thumbnail.set(note.getThumbnail());
                }
            }
        });
    }

    public void setNavigator(ManagerItemNavigator navigator) {
        mNavigator = new WeakReference<>(navigator);
    }

    public void setNote(NoteModel note) {
        mNoteObservable.set(note);
    }

    /**
     * Called by the Data Binding library when item is clicked.
     */
    public void itemClicked() {
        String uniqueID = mNoteObservable.get().getUniqueID();
        if (TextUtils.isEmpty(uniqueID)) {
            // Click happened before note was loaded, no-op.
            return;
        }
        if (mNavigator != null && mNavigator.get() != null) {
            switch (mNoteObservable.get().getType()) {
                case NoteType.CREATE_NOTE:
                    mNavigator.get().addNewNote();
                    break;
                case NoteType.FOLDER:
                    mNavigator.get().enterFolder(uniqueID);
                    break;
                case NoteType.NOTE:
                    mNavigator.get().editNote(uniqueID);
                    break;
            }
        }
    }
}
