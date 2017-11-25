package com.onyx.kcb.action;

import android.app.Activity;
import android.app.FragmentManager;
import android.view.View;

import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.rxrequest.data.db.RxLibraryDeleteRequest;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.kcb.R;
import com.onyx.kcb.holder.DataBundle;

/**
 * Created by suicheng on 2017/4/19.
 */

public class LibraryDeleteAction extends BaseAction<DataBundle> {

    private FragmentManager fragmentManager;
    private Library deleteLibrary;

    public LibraryDeleteAction(Activity activity, Library deleteLibrary) {
        this.fragmentManager = activity.getFragmentManager();
        this.deleteLibrary = deleteLibrary;
    }

    @Override
    public void execute(final DataBundle dataHolder, final RxCallback baseCallback) {
        final OnyxAlertDialog alertDialog = new OnyxAlertDialog();
        alertDialog.setParams(new OnyxAlertDialog.Params()
                .setTittleString(dataHolder.getAppContext().getString(R.string.tip))
                .setAlertMsgString(dataHolder.getAppContext().getString(R.string.library_delete_tip))
                .setPositiveAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        confirmDelete(dataHolder, baseCallback);
                    }
                }));
        alertDialog.show(fragmentManager, this.getClass().getSimpleName());
    }

    private void confirmDelete(final DataBundle dataHolder, final RxCallback baseCallback) {
        RxLibraryDeleteRequest deleteRequest = new RxLibraryDeleteRequest(dataHolder.getDataManager(), deleteLibrary);
        deleteRequest.execute(new RxCallback<RxLibraryDeleteRequest>() {
            @Override
            public void onNext(RxLibraryDeleteRequest rxLibraryDeleteRequest) {
                baseCallback.onNext(rxLibraryDeleteRequest);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                if (throwable != null) {
                    ToastUtils.showToast(dataHolder.getAppContext(), R.string.library_delete_fail);
                }
            }
        });
    }
}
