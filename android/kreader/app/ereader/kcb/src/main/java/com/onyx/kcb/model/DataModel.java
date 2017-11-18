package com.onyx.kcb.model;

import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.graphics.Bitmap;

import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.kcb.event.ItemClickEvent;
import com.onyx.kcb.event.ItemLongClickEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

/**
 * Created by hehai on 17-11-13.
 */

public class DataModel extends BaseObservable {
    public final ObservableField<File> file = new ObservableField<>();
    public final ObservableField<Metadata> metadata = new ObservableField<>();
    public final ObservableField<Library> library = new ObservableField<>();
    public final ObservableField<ModelType> type = new ObservableField<>();
    public final ObservableField<String> id = new ObservableField<>();
    public final ObservableField<String> title = new ObservableField<>();
    public final ObservableField<String> author = new ObservableField<>();
    public final ObservableField<String> size = new ObservableField<>();
    public final ObservableField<String> desc = new ObservableField<>();
    public final ObservableField<Bitmap> cover = new ObservableField<>();

    public void itemClicked() {
        ItemClickEvent event = new ItemClickEvent(this);
        EventBus.getDefault().post(event);
    }

    public boolean itemLongClick() {
        ItemLongClickEvent event = new ItemLongClickEvent(this);
        EventBus.getDefault().post(event);
        return true;
    }
}
