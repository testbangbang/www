package com.onyx.edu.note.scribble;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import com.onyx.android.sdk.scribble.data.NoteModel;

/**
 * Created by solskjaer49 on 2017/6/22 11:56.
 */

public class ScribbleViewModel extends BaseObservable {
    private static final String TAG = ScribbleViewModel.class.getSimpleName();

    // To avoid leaks, this must be an Application Context.
    private Context mContext;

    // These observable fields will update Views automatically
    public final ObservableInt currentPage = new ObservableInt();
    public final ObservableInt totalPage = new ObservableInt();
    private final ObservableField<NoteModel> currentNoteModel = new ObservableField<>();
    public final ObservableField<String> currentFolderTitle = new ObservableField<>();
    private ScribbleNavigator scribbleNavigator;

    public ScribbleViewModel(Context context) {
        // Force use of Application Context.
        this.mContext = context.getApplicationContext();
    }

    public void start(String uniqueID){

    }

    void loadNoteModel(){

    }

    public void onPrevPage(){

    }

    public void onNextPage(){

    }
}
