package com.onyx.android.eschool.action;

import com.onyx.android.eschool.R;
import com.onyx.android.eschool.holder.LibraryDataHolder;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.request.data.db.RemoveFromLibraryRequest;
import com.onyx.android.sdk.ui.utils.ToastUtils;

import java.util.List;

/**
 * Created by suicheng on 2017/4/19.
 */

public class LibraryRemoveFromAction extends BaseAction<LibraryDataHolder> {

    private Library fromLibrary;
    private List<Metadata> removeList;
    private BaseCallback callback;

    public LibraryRemoveFromAction(Library fromLibrary, List<Metadata> removeList) {
        this.fromLibrary = fromLibrary;
        this.removeList = removeList;
    }

    @Override
    public void execute(final LibraryDataHolder dataHolder, BaseCallback baseCallback) {
        this.callback = baseCallback;
        RemoveFromLibraryRequest removeRequest = new RemoveFromLibraryRequest(fromLibrary, removeList);
        dataHolder.getDataManager().submit(dataHolder.getContext(), removeRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    ToastUtils.showToast(dataHolder.getContext(), R.string.library_remove_from_fail);
                    return;
                }
                BaseCallback.invoke(callback, request, e);
            }
        });
    }
}
