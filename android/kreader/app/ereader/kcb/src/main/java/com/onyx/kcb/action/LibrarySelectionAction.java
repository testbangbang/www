package com.onyx.kcb.action;

import android.app.FragmentManager;

import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.LibraryTableOfContentEntry;
import com.onyx.android.sdk.data.rxrequest.data.db.RxLibraryGotoRequest;
import com.onyx.android.sdk.data.rxrequest.data.db.RxLibraryTableOfContentLoadRequest;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.ui.view.TreeRecyclerView;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.kcb.R;
import com.onyx.kcb.dialog.DialogTableOfLibrary;
import com.onyx.kcb.holder.DataBundle;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2017/4/29.
 */

public class LibrarySelectionAction extends BaseAction<DataBundle> {

    private String title;
    private List<DataModel> parentPathList = new ArrayList<>();
    private Library librarySelected;
    private FragmentManager fragmentManager;
    private RxCallback rxCallback;
    private DataBundle dataHolder;

    public LibrarySelectionAction(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public LibrarySelectionAction(FragmentManager fragmentManager, String title) {
        this.fragmentManager = fragmentManager;
        this.title = title;
    }

    @Override
    public void execute(final DataBundle dataHolder, final RxCallback baseCallback) {
        this.dataHolder = dataHolder;
        rxCallback = baseCallback;
        loadRequest(dataHolder, baseCallback);
    }

    private void loadRequest(final DataBundle dataHolder, final RxCallback callback) {
        final RxLibraryTableOfContentLoadRequest loadRequest = new RxLibraryTableOfContentLoadRequest(dataHolder.getDataManager(), null);
        loadRequest.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                hideLoadingDialog(dataHolder);
                showLibraryEntryTocDialog(dataHolder, loadRequest.getLibraryTableOfContentEntry(), callback);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                hideLoadingDialog(dataHolder);
                if (throwable != null) {
                    ToastUtils.showToast(dataHolder.getAppContext(), R.string.library_toc_load_fail);
                }
            }
        });
        showLoadingDialog(dataHolder, R.string.library_toc_loading);
    }

    private void showLibraryEntryTocDialog(final DataBundle dataHolder, LibraryTableOfContentEntry tocEntry,
                                           final RxCallback callback) {
        if (tocEntry == null || CollectionUtils.isNullOrEmpty(tocEntry.children)) {
            ToastUtils.showToast(dataHolder.getAppContext(), R.string.no_library_choice);
            return;
        }
        final DialogTableOfLibrary dialogTableOfLibrary = new DialogTableOfLibrary(tocEntry, title);
        dialogTableOfLibrary.setItemActionCallBack(new TreeRecyclerView.Callback() {
            @Override
            public void onTreeNodeClicked(TreeRecyclerView.TreeNode node) {
                librarySelected = (Library) node.getTag();
                dialogTableOfLibrary.dismiss();
                rxCallback.onNext(node);
            }

            @Override
            public void onItemCountChanged(int position, int itemCount) {
            }
        });
        dialogTableOfLibrary.show(fragmentManager, DialogTableOfLibrary.class.getSimpleName());
    }

    public void gotoLibrary(DataBundle dataHolder, final DataModel gotoLibrary, final RxCallback baseCallback) {
        final RxLibraryGotoRequest gotoRequest = new RxLibraryGotoRequest(dataHolder.getDataManager(), gotoLibrary);
        gotoRequest.execute(new RxCallback<RxLibraryGotoRequest>() {
            @Override
            public void onNext(RxLibraryGotoRequest rxLibraryGotoRequest) {
                parentPathList = gotoRequest.getParentLibraryList();
                parentPathList.add(gotoLibrary);
                baseCallback.onNext(rxLibraryGotoRequest);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
            }
        });
    }

    public List<DataModel> getParentPathList() {
        return parentPathList;
    }

    public DataModel getLibrarySelected() {
        DataModel dataModel = new DataModel(dataHolder.getEventBus());
        dataModel.id.set(librarySelected.getId());
        dataModel.idString.set(librarySelected.getIdString());
        dataModel.parentId.set(librarySelected.getParentUniqueId());
        dataModel.title.set(librarySelected.getName());
        return dataModel;
    }
}
