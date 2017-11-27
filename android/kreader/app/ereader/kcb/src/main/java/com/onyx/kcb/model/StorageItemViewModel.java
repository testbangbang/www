package com.onyx.kcb.model;

import android.databinding.BaseObservable;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.graphics.Bitmap;

import com.onyx.android.sdk.data.model.FileModel;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by suicheng on 2017/9/9.
 */
public class StorageItemViewModel extends BaseObservable {
    private final ObservableField<FileModel> fileModelObservable = new ObservableField<>();
    public final ObservableField<String> documentName = new ObservableField<>();
    public final ObservableField<Bitmap> thumbnail = new ObservableField<>();
    public final ObservableBoolean isDocument = new ObservableBoolean();
    public final ObservableBoolean isSelected = new ObservableBoolean();
    public final ObservableBoolean enableSelection = new ObservableBoolean(true);
    private EventBus eventBus;

    public StorageItemViewModel(final EventBus eventBus) {
        setEventBus(eventBus);
        fileModelObservable.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                FileModel fileModel = getFileModel();
                if (fileModel != null) {
                    documentName.set(fileModel.getName());
                    isDocument.set(fileModel.isFileType());
                    Bitmap coverBitmap = fileModel.getThumbnail();
                    if (coverBitmap != null) {
                        thumbnail.set(coverBitmap);
                    }
                }
            }
        });
    }

    public void itemClicked() {
        FileModel model = getFileModel();
        if (model == null) {
            return;
        }
//        getEventBus().post(new DataModelClickEvent(this));
    }

    public boolean itemLongClicked() {
        FileModel model = getFileModel();
        if (model == null) {
            return false;
        }
//        getEventBus().post(new DataModelLongClickEvent(this));
        return true;
    }

    public void setFileModel(FileModel model) {
        fileModelObservable.set(model);
    }

    public FileModel getFileModel() {
        return fileModelObservable.get();
    }

    public void setEventBus(EventBus bus) {
        this.eventBus = bus;
    }

    private EventBus getEventBus() {
        return eventBus;
    }

    public void setEnableSelection(boolean enable) {
        enableSelection.set(enable);
    }

    public void setSelected(boolean select) {
        isSelected.set(select);
    }

    public void setCoverThumbnail(Bitmap bitmap) {
        thumbnail.set(bitmap);
    }
}