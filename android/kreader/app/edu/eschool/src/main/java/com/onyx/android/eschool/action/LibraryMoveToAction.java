package com.onyx.android.eschool.action;

import android.app.Activity;
import android.app.FragmentManager;

import com.onyx.android.eschool.R;
import com.onyx.android.eschool.holder.LibraryDataHolder;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.request.data.db.LibraryMoveToRequest;

import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.List;

/**
 * Created by suicheng on 2017/4/14.
 */

public class LibraryMoveToAction extends BaseAction<LibraryDataHolder> {

    private FragmentManager fragmentManager;
    private List<Library> libraryList;
    private List<Metadata> chosenItemsList;

    private BaseCallback baseCallback;

    public LibraryMoveToAction(Activity activity, List<Library> libraryList, List<Metadata> list) {
        this.fragmentManager = activity.getFragmentManager();
        this.libraryList = libraryList;
        this.chosenItemsList = list;
    }

    @Override
    public void execute(LibraryDataHolder dataHolder, BaseCallback baseCallback) {
        if (CollectionUtils.isNullOrEmpty(chosenItemsList)) {
            ToastUtils.showToast(dataHolder.getContext(), R.string.chose_one_book_at_least);
            return;
        }
        showDialogLibraryTableOfContent(dataHolder);
        this.baseCallback = baseCallback;
    }

    private void showDialogLibraryTableOfContent(final LibraryDataHolder dataHolder) {
        final LibraryChoiceAction choiceAction = new LibraryChoiceAction(fragmentManager,
                dataHolder.getContext().getString(R.string.menu_library_add));
        choiceAction.execute(dataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    return;
                }
                addChosenBooksToLibrary(dataHolder, choiceAction.getChooseLibrary());
            }
        });
    }

    private void addChosenBooksToLibrary(final LibraryDataHolder dataHolder, Library toLibrary) {
        Library fromLibrary = new Library();
        fromLibrary.setIdString(dataHolder.getLibraryViewInfo().getLibraryIdString());
        LibraryMoveToRequest moveRequest = new LibraryMoveToRequest(fromLibrary, toLibrary, chosenItemsList);
        dataHolder.getDataManager().submit(dataHolder.getContext(), moveRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                hideLoadingDialog();
                if (e != null) {
                    ToastUtils.showToast(dataHolder.getContext(), R.string.library_add_to_fail);
                }
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
        showLoadingDialog(dataHolder, R.string.adding);
    }
}
