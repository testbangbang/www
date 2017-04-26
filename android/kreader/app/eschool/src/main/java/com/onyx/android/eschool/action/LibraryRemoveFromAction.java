package com.onyx.android.eschool.action;

import android.widget.Toast;

import com.onyx.android.eschool.R;
import com.onyx.android.eschool.holder.LibraryDataHolder;
import com.onyx.android.eschool.utils.EventUtils;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.request.data.db.RemoveFromLibraryRequest;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.List;

/**
 * Created by suicheng on 2017/4/19.
 */

public class LibraryRemoveFromAction extends BaseAction<LibraryDataHolder> {

    private Library fromLibrary;
    private List<Metadata> removeList;

    public LibraryRemoveFromAction(Library fromLibrary, List<Metadata> removeList) {
        this.fromLibrary = fromLibrary;
        this.removeList = removeList;
    }

    @Override
    public void execute(final LibraryDataHolder dataHolder, BaseCallback baseCallback) {
        RemoveFromLibraryRequest removeRequest = new RemoveFromLibraryRequest(fromLibrary, removeList);
        dataHolder.getDataManager().submit(dataHolder.getContext(), removeRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    ToastUtils.showToast(dataHolder.getContext(), R.string.library_remove_from_fail);
                    return;
                }
                processLoadAction(dataHolder);
            }
        });
    }

    private void processLoadAction(final LibraryDataHolder dataHolder) {
        MetadataLoadAction loadAction = new MetadataLoadAction(
                dataHolder.getQueryArgs(CollectionUtils.getSize(dataHolder.getBookList()) - removeList.size(), 0));
        loadAction.execute(dataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    return;
                }
                EventUtils.postLoadFinishEvent(dataHolder.getEventBus());
            }
        });
    }
}
