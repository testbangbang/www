package com.onyx.kcb.action;

import android.app.Activity;
import android.app.FragmentManager;

import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.rxrequest.data.db.RxLibraryMoveToRequest;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.HashUtils;
import com.onyx.kcb.R;
import com.onyx.kcb.holder.LibraryDataHolder;

import java.util.List;

/**
 * Created by suicheng on 2017/4/14.
 */

public class LibraryMoveToAction extends BaseAction<LibraryDataHolder> {

    private FragmentManager fragmentManager;
    private List<DataModel> chosenItemsList;

    private RxCallback baseCallback;

    public LibraryMoveToAction(Activity activity, List<DataModel> list) {
        this.fragmentManager = activity.getFragmentManager();
        this.chosenItemsList = list;
    }

    @Override
    public void execute(LibraryDataHolder dataHolder, RxCallback baseCallback) {
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
        choiceAction.execute(dataHolder, new RxCallback() {
            @Override
            public void onNext(Object o) {
                addChosenBooksToLibrary(dataHolder, choiceAction.getChooseLibrary());
            }
        });
    }

    private void addChosenBooksToLibrary(final LibraryDataHolder dataHolder, DataModel toLibrary) {
        DataModel fromLibrary = new DataModel(dataHolder.getEventBus());
        fromLibrary.idString.set(dataHolder.getLibraryViewInfo().getLibraryIdString());
        RxLibraryMoveToRequest moveRequest = new RxLibraryMoveToRequest(dataHolder.getDataManager(),fromLibrary, toLibrary, chosenItemsList);
        moveRequest.execute(new RxCallback<RxLibraryMoveToRequest>() {
            @Override
            public void onNext(RxLibraryMoveToRequest rxLibraryMoveToRequest) {
                hideLoadingDialog();
                baseCallback.onNext(rxLibraryMoveToRequest);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                if (throwable != null) {
                    ToastUtils.showToast(dataHolder.getContext(), R.string.library_add_to_fail);
                }
            }
        });
        showLoadingDialog(dataHolder, R.string.adding);
    }
}
