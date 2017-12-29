package com.onyx.jdread.library.model;

import android.databinding.ObservableField;
import android.graphics.Bitmap;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.library.event.BackToLibraryFragmentEvent;
import com.onyx.jdread.model.TitleBarModel;

import java.util.Observable;

/**
 * Created by hehai on 17-12-26.
 */

public class FileServerModel extends Observable {

    public final TitleBarModel titleBarModel = new TitleBarModel(JDReadApplication.getDataBundle().getEventBus());
    public final ObservableField<String> serverAddress = new ObservableField<>();
    public final ObservableField<Bitmap> bitmap = new ObservableField<>();

    public FileServerModel() {
        titleBarModel.title.set(JDReadApplication.getInstance().getString(R.string.wifi_pass_book));
        titleBarModel.backEvent.set(new BackToLibraryFragmentEvent());
    }
}
