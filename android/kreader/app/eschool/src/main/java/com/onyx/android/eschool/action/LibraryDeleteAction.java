package com.onyx.android.eschool.action;

import android.view.View;
import android.widget.Toast;

import com.onyx.android.eschool.R;
import com.onyx.android.eschool.holder.LibraryDataHolder;
import com.onyx.android.eschool.utils.EventUtils;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.request.data.db.LibraryDeleteRequest;
import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.CollectionUtils;

/**
 * Created by suicheng on 2017/4/19.
 */

public class LibraryDeleteAction extends BaseAction<LibraryDataHolder> {

    private Library deleteLibrary;

    public LibraryDeleteAction(Library deleteLibrary) {
        this.deleteLibrary = deleteLibrary;
    }

    @Override
    public void execute(final LibraryDataHolder dataHolder, BaseCallback baseCallback) {
        final OnyxAlertDialog alertDialog = new OnyxAlertDialog();
        alertDialog.setParams(new OnyxAlertDialog.Params()
                .setTittleString(dataHolder.getContext().getString(R.string.tip))
                .setAlertMsgString(dataHolder.getContext().getString(R.string.library_delete_tip))
                .setPositiveAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        confirmDelete(dataHolder);
                    }
                }));
        alertDialog.show(dataHolder.getFragmentManager(), this.getClass().getSimpleName());
    }

    private void confirmDelete(final LibraryDataHolder dataHolder) {
        LibraryDeleteRequest deleteRequest = new LibraryDeleteRequest(deleteLibrary);
        dataHolder.getDataManager().submit(dataHolder.getContext(), deleteRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    ToastUtils.showToast(dataHolder.getContext(), R.string.library_delete_fail);
                    return;
                }
                processLoadAction(dataHolder);
            }
        });
    }

    private void processLoadAction(final LibraryDataHolder dataHolder) {
        MetadataLoadAction loadAction = new MetadataLoadAction(
                dataHolder.getQueryArgs(CollectionUtils.getSize(dataHolder.getBookList()), 0));
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
