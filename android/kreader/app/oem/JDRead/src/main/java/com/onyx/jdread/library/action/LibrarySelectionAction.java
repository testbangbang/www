package com.onyx.jdread.library.action;

import android.app.Activity;
import android.databinding.ObservableList;

import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.rxrequest.data.db.RxLibraryLoadRequest;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.library.model.DataBundle;
import com.onyx.jdread.library.view.LibraryBuildDialog;
import com.onyx.jdread.library.view.MoveToLibraryListDialog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by suicheng on 2017/4/29.
 */

public class LibrarySelectionAction extends BaseAction<DataBundle> {

    private Map<String, List<Metadata>> chosenItemsMap;
    private List<DataModel> libraryList = new ArrayList<>();
    private DataModel librarySelected;
    private Activity activity;

    public LibrarySelectionAction(Map<String, List<Metadata>> chosenItemsMap, Activity activity) {
        this.activity = activity;
        this.chosenItemsMap = chosenItemsMap;
    }

    @Override
    public void execute(final DataBundle dataHolder, final RxCallback baseCallback) {
        loadRequest(dataHolder, baseCallback);
    }

    private void loadRequest(final DataBundle dataBundle, final RxCallback callback) {
        final QueryArgs queryArgs = new QueryArgs(SortBy.CreationTime, SortOrder.Desc);
        RxLibraryLoadRequest request = new RxLibraryLoadRequest(dataBundle.getDataManager(), queryArgs, libraryList, false, dataBundle.getEventBus(), false);
        request.setLoadFromCache(false);
        request.execute(new RxCallback<RxLibraryLoadRequest>() {
            @Override
            public void onNext(RxLibraryLoadRequest loadRequest) {
                libraryList.clear();
                libraryList.addAll(loadRequest.getModels());
                filterLibrary(dataBundle);
                showLibraryEntryTocDialog(dataBundle, callback);
            }
        });
    }

    private void filterLibrary(DataBundle dataBundle) {
        DataModel dataModel = new DataModel(dataBundle.getEventBus());
        dataModel.title.set(dataBundle.getAppContext().getString(R.string.library_name));
        dataModel.idString.set("");
        libraryList.add(0, dataModel);
        if (!CollectionUtils.isNullOrEmpty(chosenItemsMap) && chosenItemsMap.size() == 1) {
            for (String libraryId : chosenItemsMap.keySet()) {
                Iterator<DataModel> iterator = libraryList.iterator();
                while (iterator.hasNext()) {
                    DataModel next = iterator.next();
                    if (libraryId.equals(next.idString.get())) {
                        iterator.remove();
                    }
                }
            }
        }
    }

    private void showLibraryEntryTocDialog(final DataBundle dataBundle, final RxCallback callback) {
        MoveToLibraryListDialog.DialogModel model = new MoveToLibraryListDialog.DialogModel(dataBundle.getAppContext().getResources().getInteger(R.integer.library_list_dialog_row)
                , dataBundle.getAppContext().getResources().getInteger(R.integer.library_list_dialog_col), libraryList);
        MoveToLibraryListDialog.Builder builder = new MoveToLibraryListDialog.Builder(activity, model);
        final MoveToLibraryListDialog libraryListDialog = builder.create();
        model.setListener(new MoveToLibraryListDialog.DialogModel.ClickListener() {
            @Override
            public void onItemClicked(DataModel dataModel) {
                librarySelected = dataModel;
                callback.onNext(librarySelected);
            }

            @Override
            public void onBuildLibraryClicked() {
                libraryListDialog.dismiss();
                showBuildLibrary(dataBundle, callback);
            }
        });
        libraryListDialog.show();
    }

    private void showBuildLibrary(final DataBundle dataBundle, final RxCallback callback) {
        final LibraryBuildDialog.DialogModel model = new LibraryBuildDialog.DialogModel();
        LibraryBuildDialog.Builder builder = new LibraryBuildDialog.Builder(activity, model);
        final LibraryBuildDialog libraryBuildDialog = builder.create();
        model.setPositiveClickLister(new LibraryBuildDialog.DialogModel.OnClickListener() {
            @Override
            public void onClicked() {
                if (StringUtils.isNotBlank(model.libraryName.get())) {
                    if (isExist(dataBundle, model.libraryName.get())) {
                        ToastUtils.showToast(dataBundle.getAppContext(), R.string.group_exist);
                        return;
                    }
                    librarySelected = new DataModel(dataBundle.getEventBus());
                    librarySelected.idString.set(UUID.randomUUID().toString());
                    librarySelected.title.set(model.libraryName.get());
                    callback.onNext(librarySelected);
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

    public DataModel getLibrarySelected() {
        return librarySelected;
    }
}
