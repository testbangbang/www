package com.onyx.kcb.model;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.kcb.KCPApplication;
import com.onyx.kcb.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

/**
 * Created by hehai on 17-11-17.
 */

public class LibraryViewDataModel extends Observable {
    public final ObservableList<DataModel> items = new ObservableArrayList<>();
    public final ObservableField<String> title = new ObservableField<>();
    public final ObservableInt count = new ObservableInt();
    private Map<DataModel, Boolean> mapSelected = new HashMap<>();

    public static void libraryToDataModel(ObservableList<DataModel> dataModels, List<Library> libraryList) {
        if (CollectionUtils.isNullOrEmpty(libraryList)) {
            return;
        }
        for (Library library : libraryList) {
            DataModel model = new DataModel();
            model.type.set(ModelType.Library);
            model.id.set(library.getIdString());
            model.title.set(library.getName());
            model.desc.set(library.getDescription());
            model.cover.set(BitmapFactory.decodeResource(KCPApplication.getInstance().getResources(), R.drawable.library_default_cover));
            dataModels.add(model);
        }
    }

    public static void metadataToDataModel(ObservableList<DataModel> dataModels, List<Metadata> metadataList, Map<String, CloseableReference<Bitmap>> thumbnailMap) {
        if (CollectionUtils.isNullOrEmpty(metadataList)) {
            return;
        }
        for (Metadata metadata : metadataList) {
            DataModel model = new DataModel();
            model.type.set(ModelType.Metadata);
            model.id.set(metadata.getIdString());
            model.title.set(metadata.getName());
            model.desc.set(metadata.getDescription());
            CloseableReference<Bitmap> bitmap = thumbnailMap.get(metadata.getAssociationId());
            if (bitmap != null) {
                model.cover.set(bitmap.get());
            } else {
                model.cover.set(BitmapFactory.decodeResource(KCPApplication.getInstance().getResources(), R.drawable.book_default_cover));
            }

            dataModels.add(model);
        }
    }

    public Map<DataModel, Boolean> getItemSelectedMap() {
        return mapSelected;
    }

    public void clearItemSelectedMap() {
        getItemSelectedMap().clear();
    }

    public void addItemSelected(DataModel itemModel, boolean clearBeforeAdd) {
        if (clearBeforeAdd) {
            clearItemSelectedMap();
        }
        getItemSelectedMap().put(itemModel, true);
    }
}
