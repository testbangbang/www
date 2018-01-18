package com.onyx.jdread.library.action;

import android.content.Context;
import android.databinding.ObservableList;

import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.data.rxrequest.data.db.RxLibraryLoadRequest;
import com.onyx.android.sdk.data.rxrequest.data.db.RxRenameLibraryRequest;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.library.model.LibraryDataBundle;
import com.onyx.jdread.library.view.LibraryBuildDialog;
import com.onyx.jdread.main.action.BaseAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hehai on 17-12-22.
 */

public class LibraryRenameAction extends BaseAction<LibraryDataBundle> {
    private Context context;
    private DataModel dataModel;
    private List<DataModel> libraryList = new ArrayList<>();

    public LibraryRenameAction(Context context, DataModel dataModel) {
        this.context = context;
        this.dataModel = dataModel;
    }

    @Override
    public void execute(final LibraryDataBundle libraryDataBundle, final RxCallback baseCallback) {
        final QueryArgs queryArgs = new QueryArgs(SortBy.CreationTime, SortOrder.Desc);
        RxLibraryLoadRequest request = new RxLibraryLoadRequest(libraryDataBundle.getDataManager(), queryArgs, libraryList, false, libraryDataBundle.getEventBus(), false);
        request.setLoadFromCache(false);
        request.execute(new RxCallback<RxLibraryLoadRequest>() {
            @Override
            public void onNext(RxLibraryLoadRequest loadRequest) {
                libraryList.clear();
                libraryList.addAll(loadRequest.getModels());
                showRenameDialog(libraryDataBundle, baseCallback);
            }
        });
    }

    private void showRenameDialog(final LibraryDataBundle libraryDataBundle, final RxCallback baseCallback) {
        final LibraryBuildDialog.DialogModel model = new LibraryBuildDialog.DialogModel();
        model.title.set(libraryDataBundle.getAppContext().getString(R.string.rename_library));
        LibraryBuildDialog.Builder builder = new LibraryBuildDialog.Builder(context, model);
        final LibraryBuildDialog libraryBuildDialog = builder.create();
        model.setPositiveClickLister(new LibraryBuildDialog.DialogModel.OnClickListener() {
            @Override
            public void onClicked() {
                if (StringUtils.isNotBlank(model.libraryName.get())) {
                    if (model.libraryName.get().equals(dataModel.title.get())) {
                        ToastUtils.showToast(libraryDataBundle.getAppContext(), R.string.the_same_name);
                        return;
                    }
                    if (isExist(libraryDataBundle, model.libraryName.get())) {
                        ToastUtils.showToast(libraryDataBundle.getAppContext(), R.string.group_exist);
                        return;
                    }
                    renameLibrary(libraryDataBundle, model.libraryName.get(), baseCallback);
                    libraryBuildDialog.dismiss();
                } else {
                    ToastUtils.showToast(libraryDataBundle.getAppContext(), R.string.please_enter_group_name);
                }
            }
        });

        model.setNegativeClickLister(new LibraryBuildDialog.DialogModel.OnClickListener() {
            @Override
            public void onClicked() {
                libraryBuildDialog.dismiss();
            }
        });
        libraryBuildDialog.show();
    }

    private void renameLibrary(LibraryDataBundle libraryDataBundle, String newName, RxCallback baseCallback) {
        RxRenameLibraryRequest request = new RxRenameLibraryRequest(libraryDataBundle.getDataManager(), dataModel.idString.get(), newName);
        request.execute(baseCallback);
    }

    private boolean isExist(LibraryDataBundle libraryDataBundle, String newLibraryName) {
        for (DataModel dataModel : libraryList) {
            if (newLibraryName.equals(dataModel.title.get())) {
                return true;
            }
        }
        ObservableList<DataModel> libraryPathList = libraryDataBundle.getLibraryViewDataModel().libraryPathList;
        if (!CollectionUtils.isNullOrEmpty(libraryPathList)) {
            DataModel currentLibrary = libraryPathList.get(libraryPathList.size() - 1);
            if (newLibraryName.equals(currentLibrary.title.get())) {
                return true;
            }
        }
        return false;
    }
}
