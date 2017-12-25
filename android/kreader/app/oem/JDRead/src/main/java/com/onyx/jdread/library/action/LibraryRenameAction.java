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
import com.onyx.jdread.library.model.DataBundle;
import com.onyx.jdread.library.view.LibraryBuildDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hehai on 17-12-22.
 */

public class LibraryRenameAction extends BaseAction<DataBundle> {
    private Context context;
    private DataModel dataModel;
    private List<DataModel> libraryList = new ArrayList<>();

    public LibraryRenameAction(Context context, DataModel dataModel) {
        this.context = context;
        this.dataModel = dataModel;
    }

    @Override
    public void execute(final DataBundle dataBundle, final RxCallback baseCallback) {
        final QueryArgs queryArgs = new QueryArgs(SortBy.CreationTime, SortOrder.Desc);
        RxLibraryLoadRequest request = new RxLibraryLoadRequest(dataBundle.getDataManager(), queryArgs, libraryList, false, dataBundle.getEventBus(), false);
        request.setLoadFromCache(false);
        request.execute(new RxCallback<RxLibraryLoadRequest>() {
            @Override
            public void onNext(RxLibraryLoadRequest loadRequest) {
                libraryList.clear();
                libraryList.addAll(loadRequest.getModels());
                showRenameDialog(dataBundle, baseCallback);
            }
        });
    }

    private void showRenameDialog(final DataBundle dataBundle, final RxCallback baseCallback) {
        final LibraryBuildDialog.DialogModel model = new LibraryBuildDialog.DialogModel();
        model.title.set(dataBundle.getAppContext().getString(R.string.rename_library));
        LibraryBuildDialog.Builder builder = new LibraryBuildDialog.Builder(context, model);
        final LibraryBuildDialog libraryBuildDialog = builder.create();
        model.setPositiveClickLister(new LibraryBuildDialog.DialogModel.OnClickListener() {
            @Override
            public void onClicked() {
                if (StringUtils.isNotBlank(model.libraryName.get())) {
                    if (model.libraryName.get().equals(dataModel.title.get())) {
                        ToastUtils.showToast(dataBundle.getAppContext(), R.string.the_same_name);
                        return;
                    }
                    if (isExist(dataBundle, model.libraryName.get())) {
                        ToastUtils.showToast(dataBundle.getAppContext(), R.string.group_exist);
                        return;
                    }
                    renameLibrary(dataBundle, model.libraryName.get(), baseCallback);
                    libraryBuildDialog.dismiss();
                } else {
                    ToastUtils.showToast(dataBundle.getAppContext(), R.string.please_enter_group_name);
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

    private void renameLibrary(DataBundle dataBundle, String newName, RxCallback baseCallback) {
        RxRenameLibraryRequest request = new RxRenameLibraryRequest(dataBundle.getDataManager(), dataModel.idString.get(), newName);
        request.execute(baseCallback);
    }

    private boolean isExist(DataBundle dataBundle, String newLibraryName) {
        for (DataModel dataModel : libraryList) {
            if (newLibraryName.equals(dataModel.title.get())) {
                return true;
            }
        }
        ObservableList<DataModel> libraryPathList = dataBundle.getLibraryViewDataModel().libraryPathList;
        if (!CollectionUtils.isNullOrEmpty(libraryPathList)) {
            DataModel currentLibrary = libraryPathList.get(libraryPathList.size() - 1);
            if (newLibraryName.equals(currentLibrary.title.get())) {
                return true;
            }
        }
        return false;
    }
}
