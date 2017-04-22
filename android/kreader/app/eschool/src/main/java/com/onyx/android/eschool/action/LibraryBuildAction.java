package com.onyx.android.eschool.action;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.widget.Toast;

import com.onyx.android.eschool.R;
import com.onyx.android.eschool.holder.LibraryDataHolder;
import com.onyx.android.eschool.SchoolApp;
import com.onyx.android.eschool.dialog.DialogAddEdit;
import com.onyx.android.eschool.utils.EventUtils;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.request.data.db.LibraryBuildRequest;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.UUID;

/**
 * Created by suicheng on 2017/4/15.
 */

public class LibraryBuildAction extends BaseAction<LibraryDataHolder> {

    private FragmentManager fragmentManager;
    private String parentLibraryIdString;

    public LibraryBuildAction(Activity activity,  String parentId) {
        this.fragmentManager = activity.getFragmentManager();
        this.parentLibraryIdString = parentId;
    }

    @Override
    public void execute(LibraryDataHolder dataHolder, BaseCallback baseCallback) {
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
