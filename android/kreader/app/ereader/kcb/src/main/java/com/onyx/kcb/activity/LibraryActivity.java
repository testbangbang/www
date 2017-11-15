package com.onyx.kcb.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.QueryPagination;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.utils.QueryBuilder;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;
import com.onyx.android.sdk.ui.dialog.DialogLoading;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.SinglePageRecyclerView;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.utils.ViewDocumentUtils;
import com.onyx.kcb.KCPApplication;
import com.onyx.kcb.R;
import com.onyx.kcb.action.RxFileSystemScanAction;
import com.onyx.kcb.action.RxMetadataLoadAction;
import com.onyx.kcb.adapter.ModelAdapter;
import com.onyx.kcb.databinding.ActivityLibraryBinding;
import com.onyx.kcb.event.MetadataItemClickEvent;
import com.onyx.kcb.holder.LibraryDataHolder;
import com.onyx.kcb.model.DataModel;
import com.onyx.kcb.model.ModelType;
import com.onyx.kcb.model.PageIndicatorModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;

/**
 * Created by hehai on 17-11-10.
 */

public class LibraryActivity extends OnyxAppCompatActivity {
    static private boolean hasMetadataScanned = false;
    private ActivityLibraryBinding dataBinding;
    private DataModel dataModel;
    private LibraryDataHolder dataHolder;
    private QueryPagination pagination;
    private PageIndicatorModel pageIndicatorModel;
    private int row = KCPApplication.getInstance().getResources().getInteger(R.integer.library_row);
    private int col = KCPApplication.getInstance().getResources().getInteger(R.integer.library_col);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        EventBus.getDefault().register(this);
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initData() {
        if (!isHasMetadataScanned()) {
            processFileSystemScan();
            return;
        }
        loadData();
    }

    private void loadData() {
        loadData(libraryBuildQueryArgs());
    }

    private QueryArgs libraryBuildQueryArgs() {
        QueryArgs args = dataHolder.getLibraryViewInfo().libraryQuery();
        QueryBuilder.andWith(args.conditionGroup, null);
        return args;
    }

    private void loadData(QueryArgs queryArgs) {
        loadData(queryArgs, true);
    }

    private void loadData(QueryArgs queryArgs, boolean loadFromCache) {
        final RxMetadataLoadAction loadAction = new RxMetadataLoadAction(dataModel, queryArgs);
        loadAction.setLoadFromCache(loadFromCache);
        loadAction.execute(dataHolder, new RxCallback() {
            @Override
            public void onNext(Object o) {
                updateContentView();
                loadAction.hideLoadingDialog();
            }
        });
        nextLoad();
    }

    private void updateContentView() {
        SinglePageRecyclerView contentPageView = dataBinding.contentPageView;
        if (contentPageView == null) {
            return;
        }
        contentPageView.getAdapter().notifyDataSetChanged();
        updatePageIndicator();
    }

    private void updatePageIndicator() {
        int totalCount = getTotalCount();
        pagination.resize(3, 3, totalCount);
        pageIndicatorModel.updateCurrentPage(totalCount);
        pageIndicatorModel.updateTotal(totalCount);
    }

    private int getTotalCount() {
        return dataModel.count.get();
    }

    private void nextLoad() {
        int preLoadPage = pagination.getCurrentPage() + 1;
        if (preLoadPage >= pagination.pages()) {
            return;
        }
        final RxMetadataLoadAction loadAction = new RxMetadataLoadAction(dataModel, dataHolder.getLibraryViewInfo().pageQueryArgs(preLoadPage), false);
        loadAction.execute(dataHolder, new RxCallback() {
            @Override
            public void onNext(Object o) {
                updateContentView();
            }
        });
    }

    private void processFileSystemScan() {
        final DialogLoading dialogLoading = new DialogLoading(this, R.string.loading, false);
        dialogLoading.show();
        RxFileSystemScanAction action = new RxFileSystemScanAction(dataModel.items, RxFileSystemScanAction.MMC_STORAGE_ID, true);
        action.execute(dataHolder, new RxCallback() {
            @Override
            public void onNext(Object o) {
                setHasMetadataScanned(true);
                loadData();
            }

            @Override
            public void onComplete() {
                super.onComplete();
                dialogLoading.dismiss();
            }
        });
    }

    private void initView() {
        dataHolder = getDataHolder();
        dataModel = new DataModel();
        dataModel.type.set(ModelType.Library);
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_library);
        dataBinding.setDataModel(dataModel);
        initPageRecyclerView();
        initPageIndicator();
    }

    private void initPageIndicator() {
        pagination = dataHolder.getLibraryViewInfo().getQueryPagination();
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
                loadData();
            }
        });
        dataBinding.setIndicatorModel(pageIndicatorModel);
    }

    private void initPageRecyclerView() {
        SinglePageRecyclerView contentPageView = getContentView();
        contentPageView.setLayoutManager(new DisableScrollGridManager(getApplicationContext()));
        ModelAdapter modelAdapter = new ModelAdapter();
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

    private void prevPage() {
        if (!pagination.prevPage()) {
            return;
        }
        final RxMetadataLoadAction loadAction = new RxMetadataLoadAction(dataModel, dataHolder.getLibraryViewInfo().prevPage(), false);
        loadAction.execute(dataHolder, new RxCallback() {
            @Override
            public void onNext(Object o) {
                updateContentView();
            }
        });
        prevLoad();
    }

    private void prevLoad() {
        int preLoadPage = pagination.getCurrentPage() - 1;
        if (preLoadPage < 0) {
            return;
        }
        final RxMetadataLoadAction loadAction = new RxMetadataLoadAction(dataModel,
                dataHolder.getLibraryViewInfo().pageQueryArgs(preLoadPage), false);
        loadAction.execute(dataHolder, null);
    }

    private void nextPage() {
        if (!pagination.nextPage()) {
            return;
        }
        final RxMetadataLoadAction loadAction = new RxMetadataLoadAction(dataModel, dataHolder.getLibraryViewInfo().nextPage(), false);
        loadAction.execute(dataHolder, new RxCallback() {
            @Override
            public void onNext(Object o) {
                updateContentView();
            }
        });
        nextLoad();
    }

    public static boolean isHasMetadataScanned() {
        return hasMetadataScanned;
    }

    public static void setHasMetadataScanned(boolean hasMetadataScanned) {
        LibraryActivity.hasMetadataScanned = hasMetadataScanned;
    }

    private LibraryDataHolder getDataHolder() {
        if (dataHolder == null) {
            dataHolder = new LibraryDataHolder(this);
        }
        return dataHolder;
    }

    public SinglePageRecyclerView getContentView() {
        return dataBinding.contentPageView;
    }

    @Subscribe
    public void onMetadataItemClickEvent(MetadataItemClickEvent event) {
        processBookItemOpen(event.getMetadata());
    }

    private void processBookItemOpen(Metadata metadata) {
        String filePath = metadata.getNativeAbsolutePath();
        if (StringUtils.isNullOrEmpty(filePath)) {
            showToast(R.string.file_path_null, Toast.LENGTH_SHORT);
            return;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            showToast(R.string.file_not_exist, Toast.LENGTH_SHORT);
            return;
        }
        ActivityUtil.startActivitySafely(this,
                ViewDocumentUtils.viewActionIntentWithMimeType(file),
                ViewDocumentUtils.getReaderComponentName(this));
    }
}
