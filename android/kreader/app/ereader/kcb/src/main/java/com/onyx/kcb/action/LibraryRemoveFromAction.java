package com.onyx.kcb.action;

import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.data.rxrequest.data.db.RxRemoveFromLibraryRequest;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.kcb.holder.DataBundle;

import java.util.List;

/**
 * Created by hehai on 17-12-4.
 */

public class LibraryRemoveFromAction extends BaseAction<DataBundle> {
    private List<DataModel> removeList;
    private DataModel library;

    public LibraryRemoveFromAction(List<DataModel> removeList, DataModel library) {
        this.removeList = removeList;
        this.library = library;
    }

    @Override
    public void execute(DataBundle dataBundle, final RxCallback baseCallback) {
        RxRemoveFromLibraryRequest request = new RxRemoveFromLibraryRequest(dataBundle.getDataManager(),library,removeList);
        request.execute(new RxCallback<RxRemoveFromLibraryRequest>() {
            @Override
            public void onNext(RxRemoveFromLibraryRequest removeFromLibraryRequest) {
                if (baseCallback!=null){
                    baseCallback.onNext(removeFromLibraryRequest);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                if (baseCallback!=null){
                    baseCallback.onError(throwable);
                }
            }
        });
    }
}
