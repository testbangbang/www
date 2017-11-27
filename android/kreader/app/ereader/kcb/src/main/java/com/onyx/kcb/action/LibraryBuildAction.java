package com.onyx.kcb.action;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;

import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.rxrequest.data.db.RxLibraryBuildRequest;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.kcb.R;
import com.onyx.kcb.dialog.DialogAddEdit;
import com.onyx.kcb.holder.DataBundle;
import com.onyx.kcb.model.DialogAddEditModel;

import java.util.UUID;

/**
 * Created by hehai on 17-11-13.
 */

public class LibraryBuildAction extends BaseAction<DataBundle> {
    private FragmentManager fragmentManager;
    private String parentLibraryIdString;
    private DialogAddEditModel model;

    public LibraryBuildAction(Activity activity, String parentId) {
        this.fragmentManager = activity.getFragmentManager();
        this.parentLibraryIdString = parentId;
    }

    @Override
    public void execute(final DataBundle dataBundle, final RxCallback baseCallback) {
        Context context = dataBundle.getAppContext();
        model = new DialogAddEditModel();
        model.contentTitle.set(context.getString(R.string.menu_library_build));
        model.titles.add(context.getString(R.string.library_name));
        model.titles.add(context.getString(R.string.description));
        DialogAddEdit dialog = new DialogAddEdit(dataBundle.getAppContext(), model);
        dialog.setOnCreatedListener(new DialogAddEdit.OnCreatedListener() {
            @Override
            public void onCreate() {
                String[] contentList = model.combine.get().split(model.spitSymbol.get());
                buildLibrary(dataBundle, contentList, baseCallback);
            }
        });
        dialog.show(fragmentManager);
    }

    private void buildLibrary(final DataBundle dataBundle, String[] contentList, final RxCallback baseCallback) {
        Library library = new Library();
        library.setParentUniqueId(parentLibraryIdString);
        library.setIdString(UUID.randomUUID().toString());
        library.setName(contentList[0]);
        library.setDescription(contentList[1]);
        final RxLibraryBuildRequest buildLibraryRequest = new RxLibraryBuildRequest(dataBundle.getDataManager(), library, null);
        buildLibraryRequest.execute(new RxCallback<RxLibraryBuildRequest>() {
            @Override
            public void onNext(RxLibraryBuildRequest rxLibraryBuildRequest) {
                baseCallback.onNext(buildLibraryRequest);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                if (throwable != null) {
                    ToastUtils.showToast(dataBundle.getAppContext(), R.string.library_build_fail);
                }
                baseCallback.onError(throwable);
            }
        });
    }
}
