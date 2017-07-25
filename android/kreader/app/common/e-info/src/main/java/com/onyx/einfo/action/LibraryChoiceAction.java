package com.onyx.einfo.action;

import android.app.FragmentManager;

import com.onyx.einfo.R;
import com.onyx.einfo.dialog.DialogTableOfLibrary;
import com.onyx.einfo.holder.LibraryDataHolder;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.LibraryTableOfContentEntry;
import com.onyx.android.sdk.data.request.data.db.LibraryGotoRequest;
import com.onyx.android.sdk.data.request.data.db.LibraryTableOfContentLoadRequest;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.ui.view.TreeRecyclerView;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2017/4/29.
 */

public class LibraryChoiceAction extends BaseAction<LibraryDataHolder> {

    private String title;
    private List<Library> parentPathList = new ArrayList<>();
    private Library chooseLibrary;
    private FragmentManager fragmentManager;

    public LibraryChoiceAction(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public LibraryChoiceAction(FragmentManager fragmentManager, String title) {
        this.fragmentManager = fragmentManager;
        this.title = title;
    }

    @Override
    public void execute(final LibraryDataHolder dataHolder, final BaseCallback baseCallback) {
        loadRequest(dataHolder, baseCallback);
    }

    private void loadRequest(final LibraryDataHolder dataHolder, final BaseCallback callback) {
        final LibraryTableOfContentLoadRequest loadRequest = new LibraryTableOfContentLoadRequest(null);
        dataHolder.getDataManager().submit(dataHolder.getContext(), loadRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                hideLoadingDialog();
                if (e != null) {
                    ToastUtils.showToast(request.getContext(), R.string.library_toc_load_fail);
                    return;
                }
                showLibraryEntryTocDialog(dataHolder, loadRequest.getLibraryTableOfContentEntry(), callback);
            }
        });
        showLoadingDialog(dataHolder, dataHolder.getContext().getString(R.string.library_toc_loading));
    }

    private void showLibraryEntryTocDialog(final LibraryDataHolder dataHolder, LibraryTableOfContentEntry tocEntry,
                                           final BaseCallback callback) {
        if (tocEntry == null || CollectionUtils.isNullOrEmpty(tocEntry.children)) {
            ToastUtils.showToast(dataHolder.getContext(), R.string.no_library_choice);
            return;
        }
        final DialogTableOfLibrary dialogTableOfLibrary = new DialogTableOfLibrary(tocEntry, title);
        dialogTableOfLibrary.setItemActionCallBack(new TreeRecyclerView.Callback() {
            @Override
            public void onTreeNodeClicked(TreeRecyclerView.TreeNode node) {
                chooseLibrary = (Library) node.getTag();
                dialogTableOfLibrary.dismiss();
                BaseCallback.invoke(callback, null, null);
            }

            @Override
            public void onItemCountChanged(int position, int itemCount) {
            }
        });
        dialogTableOfLibrary.show(fragmentManager, DialogTableOfLibrary.class.getSimpleName());
    }

    public void gotoLibrary(LibraryDataHolder dataHolder, final Library gotoLibrary, final BaseCallback baseCallback) {
        final LibraryGotoRequest gotoRequest = new LibraryGotoRequest(gotoLibrary);
        dataHolder.getDataManager().submit(dataHolder.getContext(), gotoRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e == null) {
                    parentPathList = gotoRequest.getParentLibraryList();
                }
                parentPathList.add(gotoLibrary);
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }

    public List<Library> getParentPathList() {
        return parentPathList;
    }

    public Library getChooseLibrary() {
        return chooseLibrary;
    }
}
