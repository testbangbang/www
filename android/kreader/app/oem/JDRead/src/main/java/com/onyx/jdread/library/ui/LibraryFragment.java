package com.onyx.jdread.library.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.data.GPaginator;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.event.ItemClickEvent;
import com.onyx.android.sdk.data.event.ItemLongClickEvent;
import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.data.model.ModelType;
import com.onyx.android.sdk.data.utils.QueryBuilder;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.utils.SelectionMode;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.SinglePageRecyclerView;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.utils.ViewDocumentUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.common.BaseFragment;
import com.onyx.jdread.databinding.FragmentLibraryBinding;
import com.onyx.jdread.event.ModifyLibraryDataEvent;
import com.onyx.jdread.library.LibraryDeleteDialog;
import com.onyx.jdread.library.action.MetadataDeleteAction;
import com.onyx.jdread.library.action.RxMetadataLoadAction;
import com.onyx.jdread.library.adapter.ModelAdapter;
import com.onyx.jdread.library.event.DeleteBookEvent;
import com.onyx.jdread.library.event.LibraryBackEvent;
import com.onyx.jdread.library.event.LibraryManageEvent;
import com.onyx.jdread.library.event.LibraryMenuEvent;
import com.onyx.jdread.library.model.DataBundle;
import com.onyx.jdread.library.model.LibraryViewDataModel;
import com.onyx.jdread.library.model.PageIndicatorModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;

/**
 * Created by huxiaomao on 2017/12/7.
 */

public class LibraryFragment extends BaseFragment {

    private DataBundle dataBundle;
    private FragmentLibraryBinding libraryBinding;
    private SinglePageRecyclerView contentView;
    private ModelAdapter modelAdapter;
    private int row = JDReadApplication.getInstance().getResources().getInteger(R.integer.library_view_type_thumbnail_row);
    private int col = JDReadApplication.getInstance().getResources().getInteger(R.integer.library_view_type_thumbnail_col);
    private GPaginator pagination;
    private PageIndicatorModel pageIndicatorModel;
    private DataModel currentChosenModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        libraryBinding = FragmentLibraryBinding.inflate(inflater, container, false);
        libraryBinding.setView(this);
        initDataBundle();
        initPageRecyclerView();
        initPageIndicator();
        initData();
        return libraryBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        getEventBus().register(this);
    }

    private EventBus getEventBus() {
        return dataBundle.getEventBus();
    }

    @Override
    public void onStop() {
        super.onStop();
        getEventBus().unregister(this);
    }

    private void initData() {
        loadData();
    }

    private void loadData() {
        loadData(libraryBuildQueryArgs());
    }

    private void loadData(QueryArgs queryArgs) {
        loadData(queryArgs, true);
    }

    private void loadData(QueryArgs queryArgs, boolean loadFromCache) {
        final RxMetadataLoadAction loadAction = new RxMetadataLoadAction(queryArgs);
        loadAction.setLoadFromCache(loadFromCache);
        loadAction.setLoadMetadata(isLoadMetadata());
        loadAction.execute(dataBundle, new RxCallback() {
            @Override
            public void onNext(Object o) {
                updateContentView();
            }
        });
        preloadNext();
    }

    private void updateContentView() {
        SinglePageRecyclerView contentPageView = getContentView();
        if (contentPageView == null) {
            return;
        }
        contentPageView.getAdapter().notifyDataSetChanged();
        updatePageIndicator();
    }

    private void updatePageIndicator() {
        int totalCount = getTotalCount();
        pagination.resize(row, col, totalCount);
        pageIndicatorModel.updateCurrentPage(totalCount);
        pageIndicatorModel.updateTotal(totalCount);
        dataBundle.getLibraryViewDataModel().updateDeletePage();
    }

    private int getTotalCount() {
        return dataBundle.getLibraryViewDataModel().count.get();
    }

    private QueryArgs libraryBuildQueryArgs() {
        QueryArgs args = dataBundle.getLibraryViewDataModel().libraryQuery();
        QueryBuilder.andWith(args.conditionGroup, null);
        return args;
    }

    private void initPageRecyclerView() {
        SinglePageRecyclerView contentPageView = getContentView();
        contentPageView.setLayoutManager(new DisableScrollGridManager(getContext().getApplicationContext()));
        modelAdapter = new ModelAdapter();
        modelAdapter.setRowAndCol(row, col);
        contentPageView.setAdapter(modelAdapter);
        contentPageView.setOnChangePageListener(new SinglePageRecyclerView.OnChangePageListener() {
            @Override
            public void prev() {
                prevPage();
            }

            @Override
            public void next() {
                nextPage();
            }
        });
    }

    private void initPageIndicator() {
        pagination = dataBundle.getLibraryViewDataModel().getQueryPagination();
        pagination.setCurrentPage(0);
        pageIndicatorModel = new PageIndicatorModel(pagination, new PageIndicatorModel.PageChangedListener() {
            @Override
            public void prev() {
                prevPage();
            }

            @Override
            public void next() {
                nextPage();
            }

            @Override
            public void gotoPage(int currentPage) {

            }

            @Override
            public void onRefresh() {
                pagination.setCurrentPage(0);
                loadData();
            }
        });
        libraryBinding.setIndicatorModel(pageIndicatorModel);
    }

    private void nextPage() {
        if (!pagination.nextPage()) {
            return;
        }
        final RxMetadataLoadAction loadAction = new RxMetadataLoadAction(dataBundle.getLibraryViewDataModel().nextPage(), false);
        loadAction.setLoadFromCache(true);
        loadAction.execute(dataBundle, new RxCallback() {
            @Override
            public void onNext(Object o) {
                updateContentView();
            }
        });
        preloadNext();
    }

    private void preloadNext() {
        int preLoadPage = pagination.getCurrentPage() + 1;
        if (preLoadPage >= pagination.pages()) {
            return;
        }
        final RxMetadataLoadAction loadAction = new RxMetadataLoadAction(dataBundle.getLibraryViewDataModel().pageQueryArgs(preLoadPage), false);
        loadAction.setLoadMetadata(isLoadMetadata());
        loadAction.execute(dataBundle, null);
    }

    private boolean isLoadMetadata() {
        return true;
    }

    private void prevPage() {
        if (!pagination.prevPage()) {
            return;
        }
        final RxMetadataLoadAction loadAction = new RxMetadataLoadAction(dataBundle.getLibraryViewDataModel().prevPage(), false);
        loadAction.setLoadFromCache(true);
        loadAction.setLoadMetadata(isLoadMetadata());
        loadAction.execute(dataBundle, new RxCallback() {
            @Override
            public void onNext(Object o) {
                updateContentView();
            }
        });
        preloadPrev();
    }

    private void preloadPrev() {
        int preLoadPage = pagination.getCurrentPage() - 1;
        if (preLoadPage < 0) {
            return;
        }
        final RxMetadataLoadAction loadAction = new RxMetadataLoadAction(dataBundle.getLibraryViewDataModel().pageQueryArgs(preLoadPage), false);
        loadAction.setLoadMetadata(isLoadMetadata());
        loadAction.execute(dataBundle, null);
    }

    private void initDataBundle() {
        dataBundle = JDReadApplication.getDataBundle();
        dataBundle.setLibraryViewDataModel(LibraryViewDataModel.create(dataBundle.getEventBus(), row, col));
        libraryBinding.setLibraryModel(dataBundle.getLibraryViewDataModel());
    }

    public SinglePageRecyclerView getContentView() {
        return libraryBinding.contentPageView;
    }

    private boolean processBackRequest() {
        if (isMultiSelectionMode()) {
            quitMultiSelectionMode();
            updateContentView();
            return true;
        }
        if (CollectionUtils.isNullOrEmpty(dataBundle.getLibraryViewDataModel().libraryPathList)) {
            return false;
        }
        removeLastParentLibrary();
        loadData();
        return false;
    }

    private void removeLastParentLibrary() {
        dataBundle.getLibraryViewDataModel().libraryPathList.remove(dataBundle.getLibraryViewDataModel().libraryPathList.size() - 1);
        int size = dataBundle.getLibraryViewDataModel().libraryPathList.size();
        dataBundle.getLibraryViewDataModel().title.set(size > 0 ? dataBundle.getLibraryViewDataModel().libraryPathList.get(size - 1).title.get() : "");
        loadData();
    }

    private void quitMultiSelectionMode() {
        modelAdapter.setMultiSelectionMode(SelectionMode.NORMAL_MODE);
        dataBundle.getLibraryViewDataModel().quitManageMode();
        viewEventCallBack.hideOrShowFunctionBar(true);
    }

    @Subscribe
    public void onLibraryBackEvent(LibraryBackEvent event) {
        processBackRequest();
    }

    @Subscribe
    public void onLibraryManageEvent(LibraryManageEvent event) {
        dataBundle.getLibraryViewDataModel().title.set(getString(R.string.manage_book));
        modelAdapter.setMultiSelectionMode(SelectionMode.MULTISELECT_MODE);
        getIntoMultiSelectMode();
        dataBundle.getLibraryViewDataModel().setShowManage(false);
        viewEventCallBack.hideOrShowFunctionBar(false);
    }

    @Subscribe
    public void onLibraryMenuEvent(LibraryMenuEvent event) {

    }

    @Subscribe
    public void onItemLongClickEvent(ItemLongClickEvent event) {
        currentChosenModel = event.getDataModel();
        dataBundle.getLibraryViewDataModel().addItemSelected(currentChosenModel, true);
    }

    @Subscribe
    public void onItemClickEvent(ItemClickEvent event) {
        if (isMultiSelectionMode()) {
            processMultiModeItemClick(event.getModel());
        } else {
            processNormalModeItemClick(event.getModel());
        }
    }

    @Subscribe
    public void onModifyLibraryDataEvent(ModifyLibraryDataEvent event) {
        dataBundle.getLibraryViewDataModel().libraryPathList.clear();
        pagination.setCurrentPage(0);
        loadData();
    }

    @Subscribe
    public void onDeleteBookEvent(DeleteBookEvent event) {
        LibraryDeleteDialog.DialogModel dialogModel = new LibraryDeleteDialog.DialogModel();
        dialogModel.message.set(getString(R.string.delete_book_prompt));
        LibraryDeleteDialog.Builder builder = new LibraryDeleteDialog.Builder(getContext(), dialogModel);
        final LibraryDeleteDialog libraryDeleteDialog = builder.create();
        libraryDeleteDialog.show();
        dialogModel.setPositiveClickLister(new LibraryDeleteDialog.DialogModel.OnClickListener() {
            @Override
            public void onClicked() {
                deleteBook();
                libraryDeleteDialog.dismiss();
            }
        });
        dialogModel.setNegativeClickLister(new LibraryDeleteDialog.DialogModel.OnClickListener() {
            @Override
            public void onClicked() {
                libraryDeleteDialog.dismiss();
            }
        });
    }

    private void deleteBook() {
        MetadataDeleteAction metadataDeleteAction = new MetadataDeleteAction();
        metadataDeleteAction.execute(dataBundle, new RxCallback() {
            @Override
            public void onNext(Object o) {
                int deletePageCount = dataBundle.getLibraryViewDataModel().getDeletePageCount();
                loadData(dataBundle.getLibraryViewDataModel().gotoPage(pagination.getCurrentPage() - deletePageCount));
                quitMultiSelectionMode();
            }
        });
    }

    private void processNormalModeItemClick(DataModel model) {
        if (model.type.get() == ModelType.TYPE_LIBRARY) {
            processLibraryItem(model);
        } else {
            processBookItemOpen(model);
        }
    }

    private void processLibraryItem(DataModel model) {
        addLibraryToParentRefList(model);
        loadData(libraryBuildQueryArgs(), false);
    }

    private void addLibraryToParentRefList(DataModel model) {
        dataBundle.getLibraryViewDataModel().libraryPathList.add(model);
        dataBundle.getLibraryViewDataModel().title.set(model.title.get());
        dataBundle.getLibraryViewDataModel().setShowManage(true);
    }

    private void processBookItemOpen(DataModel dataModel) {
        String filePath = dataModel.absolutePath.get();
        if (StringUtils.isNullOrEmpty(filePath)) {
            return;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        ActivityUtil.startActivitySafely(getContext().getApplicationContext(),
                ViewDocumentUtils.viewActionIntentWithMimeType(file),
                ViewDocumentUtils.getReaderComponentName(getContext().getApplicationContext()));
    }

    private void processMultiModeItemClick(DataModel dataModel) {
        if (dataModel.type.get() == ModelType.TYPE_LIBRARY) {
            return;
        }
        dataModel.checked.set(!dataModel.checked.get());
        dataBundle.getLibraryViewDataModel().clickItem(dataModel);
        updateContentView();
    }

    private void getIntoMultiSelectMode() {
        modelAdapter.setMultiSelectionMode(SelectionMode.MULTISELECT_MODE);
        dataBundle.getLibraryViewDataModel().clearItemSelectedList();
        updateContentView();
    }

    private boolean isMultiSelectionMode() {
        return modelAdapter.isMultiSelectionMode();
    }
}
