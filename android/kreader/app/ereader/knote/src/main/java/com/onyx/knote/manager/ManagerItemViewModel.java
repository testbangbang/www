package com.onyx.knote.manager;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.format.DateFormat;

import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.knote.R;
import com.onyx.knote.util.Utils;

import java.lang.ref.WeakReference;

/**
 * Created by solskjaer49 on 2017/6/8 16:42.
 */

public class ManagerItemViewModel extends BaseObservable {
    private static final String TAG = ManagerItemViewModel.class.getSimpleName();
    private final ObservableField<NoteModel> mNoteObservable = new ObservableField<>();

    // This navigator is s wrapped in a WeakReference to avoid leaks because it has references to an
    // activity. There's no straightforward way to clear it for each item in a recycler adapter.
    @Nullable
    private WeakReference<ManagerItemNavigator> mNavigator;

    public final ObservableField<String> documentName = new ObservableField<>();
    public final ObservableField<String> lastModifiedDate = new ObservableField<>();
    public final ObservableField<Bitmap> thumbnail = new ObservableField<>();
    public final ObservableBoolean isDocument = new ObservableBoolean();
    public final ObservableBoolean isLibrary = new ObservableBoolean();
    private final Context mContext;


    ManagerItemViewModel(final Context context) {
        // Force use of Application Context.
        mContext = context.getApplicationContext();
        // Exposed observables depend on the mNoteObservable observable:
        mNoteObservable.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                NoteModel note = mNoteObservable.get();
                if (note != null) {
                    documentName.set(note.isLibrary() ? Utils.getLibraryTitleWithSize(note) : note.getTitle());
                    lastModifiedDate.set(Utils.isValidNote(note) ?
                            DateFormat.format("yyyy-MM-dd", note.getUpdatedAt()).toString() : null);
                    thumbnail.set(note.isLibrary() ?
                            BitmapFactory.decodeResource(
                                    mContext.getResources(), R.drawable.directory) : note.getThumbnail());
                    isDocument.set(note.isDocument());
                    isLibrary.set(note.isLibrary());
                }
            }
        });
    }

    void setNavigator(ManagerItemNavigator navigator) {
        mNavigator = new WeakReference<>(navigator);
    }

    public void setNote(NoteModel note) {
        mNoteObservable.set(note);
    }

    /**
     * Called by the Data Binding library when item is clicked.
     */
    public void itemClicked() {
        String uniqueID = mNoteObservable.get().getUniqueId();
        if (TextUtils.isEmpty(uniqueID)) {
            // Click happened before note was loaded, no-op.
            return;
        }
        if (mNavigator != null && mNavigator.get() != null) {
            switch (mNoteObservable.get().getType()) {
                case NoteModel.TYPE_LIBRARY:
                    mNavigator.get().enterFolder(uniqueID);
                    break;
                case NoteModel.TYPE_DOCUMENT:
                    mNavigator.get().editNote(uniqueID);
                    break;
                default:
                    mNavigator.get().addNewNote();
                    break;
            }
        }
    }

    public boolean itemLongClicked() {
        String uniqueID = mNoteObservable.get().getUniqueId();
        if (TextUtils.isEmpty(uniqueID)) {
            // Click happened before note was loaded, no-op.
            return false;
        }
        if (mNavigator != null && mNavigator.get() != null) {
            switch (mNoteObservable.get().getType()) {
                case NoteModel.TYPE_LIBRARY:
                case NoteModel.TYPE_DOCUMENT:
                    mNavigator.get().onPendingItem(uniqueID);
                    return true;
                default:
                    return false;
            }
        }
        return false;
    }
}
