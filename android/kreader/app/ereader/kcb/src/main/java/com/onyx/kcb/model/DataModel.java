package com.onyx.kcb.model;

import android.databinding.BaseObservable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;
import android.graphics.Bitmap;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by hehai on 17-11-13.
 */

public class DataModel extends BaseObservable {
    public final ObservableList<DataModel> items = new ObservableArrayList<>();
    public final ObservableField<ModelType> type = new ObservableField<>();
    public final ObservableField<String> id = new ObservableField<>();
    public final ObservableField<String> title = new ObservableField<>();
    public final ObservableField<String> author = new ObservableField<>();
    public final ObservableField<String> size = new ObservableField<>();
    public final ObservableField<String> desc = new ObservableField<>();
    public final ObservableField<Bitmap> cover = new ObservableField<>();
    public final ObservableField<Object> event = new ObservableField<>();
    public final ObservableInt count = new ObservableInt();

    public void itemClicked() {
        Object event = this.event.get();
        if (event == null) {
            return;
        }
        EventBus.getDefault().post(event);
    }

    public boolean itemLongClick(){
        return false;
    }
}
