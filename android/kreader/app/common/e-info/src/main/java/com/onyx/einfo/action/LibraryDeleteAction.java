package com.onyx.einfo.action;

import android.app.Activity;
import android.app.FragmentManager;
import android.view.View;

import com.onyx.einfo.R;
import com.onyx.einfo.holder.LibraryDataHolder;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.request.data.db.LibraryDeleteRequest;
import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;
import com.onyx.android.sdk.ui.utils.ToastUtils;

/**
 * Created by suicheng on 2017/4/19.
 */

public class LibraryDeleteAction extends BaseAction<LibraryDataHolder> {

    private FragmentManager fragmentManager;
    private Library deleteLibrary;
    private BaseCallback callback;

    public LibraryDeleteAction(Activity activity, Library deleteLibrary) {
        this.fragmentManager = activity.getFragmentManager();
        this.deleteLibrary = deleteLibrary;
    }

    @Override
    public void execute(final LibraryDataHolder dataHolder, BaseCallback baseCallback) {
        this.callback = baseCallback;
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
        alertDialog.show(fragmentManager, this.getClass().getSimpleName());
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
                BaseCallback.invoke(callback, request, e);
            }
        });
    }
}
