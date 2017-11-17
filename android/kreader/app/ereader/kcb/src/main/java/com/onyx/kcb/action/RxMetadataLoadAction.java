package com.onyx.kcb.action;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.rxrequest.data.db.RxLibraryLoadRequest;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.kcb.R;
import com.onyx.kcb.event.LibraryItemClickEvent;
import com.onyx.kcb.event.MetadataItemClickEvent;
import com.onyx.kcb.holder.LibraryDataHolder;
import com.onyx.kcb.model.DataModel;
import com.onyx.kcb.model.ModelType;

import java.util.List;

/**
 * Created by suicheng on 2017/4/15.
 */

public class RxMetadataLoadAction extends BaseAction<LibraryDataHolder> {
    private boolean showDialog = true;
    private boolean loadFromCache = true;

    private DataModel dataModel;
    private QueryArgs queryArgs;

    public RxMetadataLoadAction(DataModel dataModel, QueryArgs queryArgs) {
        this.queryArgs = queryArgs;
        this.dataModel = dataModel;
    }

    public RxMetadataLoadAction(DataModel dataModel, QueryArgs queryArgs, boolean showDialog) {
        this.queryArgs = queryArgs;
        this.showDialog = showDialog;
        this.dataModel = dataModel;
    }

    public void setLoadFromCache(boolean loadFromCache) {
        this.loadFromCache = loadFromCache;
    }

    @Override
    public void execute(final LibraryDataHolder dataHolder, final RxCallback baseCallback) {
        final RxLibraryLoadRequest libraryRequest = new RxLibraryLoadRequest(dataHolder.getDataManager(), queryArgs);
        libraryRequest.setLoadFromCache(loadFromCache);
        libraryRequest.execute(new RxCallback<RxLibraryLoadRequest>() {
            @Override
            public void onNext(RxLibraryLoadRequest rxLibraryLoadRequest) {
                List<Library> libraryList = libraryRequest.getLibraryList();
                dataModel.items.clear();
                for (Library library : libraryList) {
                    DataModel model = new DataModel();
                    model.type.set(ModelType.Library);
                    model.id.set(library.getIdString());
                    model.title.set(library.getName());
                    model.desc.set(library.getDescription());
                    model.event.set(new LibraryItemClickEvent(library));
                    dataModel.items.add(model);
                }
                List<Metadata> bookList = libraryRequest.getBookList();
                for (Metadata metadata : bookList) {
                    DataModel model = new DataModel();
                    model.type.set(ModelType.Metadata);
                    model.id.set(metadata.getIdString());
                    model.title.set(metadata.getName());
                    model.desc.set(metadata.getDescription());
                    model.event.set(new MetadataItemClickEvent(metadata));
                    CloseableReference<Bitmap> bitmap = rxLibraryLoadRequest.getThumbnailMap().get(metadata.getAssociationId());
                    if (bitmap != null) {
                        model.cover.set(bitmap.get());
                    } else {
                        model.cover.set(BitmapFactory.decodeResource(dataHolder.getContext().getResources(), R.drawable.library_default_cover));
                    }
                    dataModel.items.add(model);
                }
                dataModel.count.set((int) libraryRequest.getTotalCount());
                if (baseCallback != null) {
                    baseCallback.onNext(rxLibraryLoadRequest);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                if (baseCallback != null) {
                    baseCallback.onError(throwable);
                }
            }
        });

        if (showDialog) {
            showLoadingDialog(dataHolder, R.string.loading);
        }
    }
}
