package com.onyx.android.sdk.data.model;

import android.databinding.BaseObservable;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ObservableLong;
import android.graphics.Bitmap;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.sdk.data.event.ItemClickEvent;
import com.onyx.android.sdk.data.event.ItemLongClickEvent;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by hehai on 17-11-13.
 */

public class DataModel extends BaseObservable {
    public final ObservableField<ModelType> type = new ObservableField<>();
    public final ObservableField<String> parentId = new ObservableField<>();
    public final ObservableLong id = new ObservableLong();
    public final ObservableField<String> idString = new ObservableField<>();
    public final ObservableField<String> title = new ObservableField<>();
    public final ObservableField<String> author = new ObservableField<>();
    public final ObservableField<String> size = new ObservableField<>();
    public final ObservableField<String> desc = new ObservableField<>();
    public final ObservableField<String> absolutePath = new ObservableField<>();
    public final ObservableField<String> associationId = new ObservableField<>();
    public final ObservableField<CloseableReference<Bitmap>> coverBitmap = new ObservableField<>();
    public final ObservableInt coverDefault = new ObservableInt();
    public final ObservableBoolean checked = new ObservableBoolean(false);
    public final ObservableBoolean enableSelection = new ObservableBoolean(false);
    public final ObservableField<FileModel> fileModel = new ObservableField<>();
    public final ObservableBoolean isDocument = new ObservableBoolean(false);
    private EventBus eventBus;

    public DataModel(EventBus eventBus) {
        this.eventBus = eventBus;
        initPropertyChangeCallback();
    }

    private void initPropertyChangeCallback() {
        fileModel.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                FileModel fileModel = getFileModel();
                if (fileModel != null) {
                    title.set(fileModel.getName());
                    isDocument.set(fileModel.isFileType());
                    CloseableReference<Bitmap> coverBitmap = fileModel.getThumbnail();
                    if (coverBitmap != null) {
                        setCoverThumbnail(coverBitmap);
                    }
                }
            }
        });
    }

    public void itemClicked() {
        ItemClickEvent event = new ItemClickEvent(this);
        eventBus.post(event);
    }

    public boolean itemLongClick() {
        ItemLongClickEvent event = new ItemLongClickEvent(this);
        eventBus.post(event);
        return true;
    }

    public void setEnableSelection(boolean enable) {
        enableSelection.set(enable);
    }

    public void setFileModel(FileModel model) {
        fileModel.set(model);
    }

    public FileModel getFileModel() {
        return fileModel.get();
    }

    public void setChecked(boolean isChecked) {
        checked.set(isChecked);
    }

    public void setCoverThumbnail(CloseableReference<Bitmap> bitmap) {
        coverBitmap.set(bitmap);
    }

    public void setAssociationId(String associationId) {
        this.associationId.set(associationId);
    }

    public String getAssociationId() {
        return associationId.get();
    }

    public EventBus getEventBus() {
        return eventBus;
    }
}
