package com.onyx.einfo.action;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;

import com.onyx.einfo.R;
import com.onyx.einfo.holder.LibraryDataHolder;
import com.onyx.einfo.dialog.DialogAddEdit;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.request.data.db.LibraryBuildRequest;
import com.onyx.android.sdk.ui.utils.ToastUtils;

import java.util.UUID;

/**
 * Created by suicheng on 2017/4/15.
 */

public class LibraryBuildAction extends BaseAction<LibraryDataHolder> {

    private FragmentManager fragmentManager;
    private String parentLibraryIdString;
    private BaseCallback callback;

    public LibraryBuildAction(Activity activity,  String parentId) {
        this.fragmentManager = activity.getFragmentManager();
        this.parentLibraryIdString = parentId;
    }

    @Override
    public void execute(LibraryDataHolder dataHolder, BaseCallback baseCallback) {
        this.callback = baseCallback;
        showBuildSubLibraryDialog(dataHolder);
    }

    private void showBuildSubLibraryDialog(final LibraryDataHolder dataHolder) {
        Context context = dataHolder.getContext();
        DialogAddEdit dialog = new DialogAddEdit(dataHolder.getContext(),
                context.getString(R.string.library_build),
                new String[]{context.getString(R.string.library_name), context.getString(R.string.description)}, 2);
        dialog.setOnCreatedListener(new DialogAddEdit.OnCreatedListener() {
            @Override
            public void onCreate(String combineString, String spitSymbol) {
                String[] contentList = combineString.split(spitSymbol);
                buildLibrary(dataHolder, contentList);
            }
        });
        dialog.show(fragmentManager);
    }

    private void buildLibrary(final LibraryDataHolder dataHolder, String[] contentList) {
        Library library = new Library();
        library.setParentUniqueId(parentLibraryIdString);
        library.setIdString(UUID.randomUUID().toString());
        library.setName(contentList[0]);
        library.setDescription(contentList[1]);
        LibraryBuildRequest buildLibraryRequest = new LibraryBuildRequest(library, null);
        dataHolder.getDataManager().submit(dataHolder.getContext(), buildLibraryRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    ToastUtils.showToast(dataHolder.getContext(), R.string.library_build_fail);
                    return;
                }
                BaseCallback.invoke(callback, request, e);
            }
        });
    }
}
