package com.onyx.einfo.model;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;

import com.onyx.einfo.BR;
import com.onyx.einfo.R;

import java.lang.ref.WeakReference;

/**
 * Created by suicheng on 2017/9/9.
 */
public class StorageItemViewModel extends BaseObservable {
    @Nullable
    private WeakReference<StorageItemNavigator> navigatorImpl;

    private final ObservableField<FileModel> fileModelObservable = new ObservableField<>();
    public final ObservableField<String> documentName = new ObservableField<>();
    public final ObservableField<Bitmap> thumbnail = new ObservableField<>();
    public final ObservableBoolean isDocument = new ObservableBoolean();
    public final ObservableBoolean isSelected = new ObservableBoolean();
    public final ObservableBoolean enableSelection = new ObservableBoolean(true);
    private final Context mContext;

    public StorageItemViewModel(final Context context, StorageItemNavigator navigator) {
        mContext = context.getApplicationContext();
        navigatorImpl = new WeakReference<>(navigator);
        fileModelObservable.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                FileModel fileModel = getFileModel();
                if (fileModel != null) {
                    documentName.set(fileModel.getName());
                    isDocument.set(fileModel.isFileType());
                    thumbnail.set(BitmapFactory.decodeResource(mContext.getResources(), isDocument.get() ?
                            R.drawable.unknown_document :
                            (fileModel.isGoUpType() ? R.drawable.directory_go_up : R.drawable.directory)));
                }
            }
        });
    }

    /**
     * Called by the Data Binding library when item is clicked.
     */
    public void itemClicked() {
        FileModel model = getFileModel();
        if (model == null) {
            return;
        }
        StorageItemNavigator navigator = getNavigator();
        if (navigator != null) {
            navigator.onClick(this);
        }
    }

    public boolean itemLongClicked() {
        FileModel model = getFileModel();
        if (model == null) {
            return false;
        }
        StorageItemNavigator navigator = getNavigator();
        if (navigator != null) {
            navigator.onLongClick(this);
        }
        return true;
    }

    public void setFileModel(FileModel model) {
        fileModelObservable.set(model);
    }

    public FileModel getFileModel() {
        return fileModelObservable.get();
    }

    private StorageItemNavigator getNavigator() {
        if (navigatorImpl == null || navigatorImpl.get() == null) {
            return null;
        }
        return navigatorImpl.get();
    }

    public void setEnableSelection(boolean enable) {
        enableSelection.set(enable);
    }

    public void setSelected(boolean select) {
        isSelected.set(select);
    }
}
