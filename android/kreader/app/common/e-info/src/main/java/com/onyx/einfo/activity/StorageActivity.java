package com.onyx.einfo.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.GPaginator;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.ViewType;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.ViewDocumentUtils;
import com.onyx.einfo.R;
import com.onyx.einfo.action.SortByProcessAction;
import com.onyx.einfo.action.StorageDataLoadAction;
import com.onyx.einfo.adapter.BindingViewHolder;
import com.onyx.einfo.adapter.PageAdapter;
import com.onyx.einfo.databinding.ActivityStorageBinding;
import com.onyx.einfo.databinding.FileDetailsItemBinding;
import com.onyx.einfo.databinding.FileThumbnailItemBinding;
import com.onyx.einfo.events.StorageItemViewModelClickEvent;
import com.onyx.einfo.events.StorageItemViewModelLongClickEvent;
import com.onyx.einfo.events.ViewTypeEvent;
import com.onyx.einfo.holder.LibraryDataHolder;
import com.onyx.einfo.manager.ConfigPreferenceManager;
import com.onyx.einfo.model.StorageItemViewModel;
import com.onyx.einfo.model.StorageViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;

/**
 * Created by suicheng on 2017/9/9.
 */

public class StorageActivity extends OnyxAppCompatActivity {
    private static final String TAG = "StorageActivity";

    private StorageViewModel storageViewModel;
    private LibraryDataHolder dataHolder;
    private ActivityStorageBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        getEventBus().register(this);
        loadRootDirData();
    }

    private void initView() {
        prevBinding();
        initToolbar();
        initRecyclerView();
        initSortByView();
    }

    private void prevBinding() {
        storageViewModel = new StorageViewModel(getEventBus());
        binding = DataBindingUtil.setContentView(this, R.layout.activity_storage);
        binding.setViewModel(storageViewModel);
    }

    private void initToolbar() {
        initSupportActionBarWithCustomBackFunction();
    }

    private void initRecyclerView() {
        PageRecyclerView contentPageView = binding.contentPageView;
        contentPageView.setHasFixedSize(true);
        contentPageView.setLayoutManager(new DisableScrollGridManager(getApplicationContext()));
        contentPageView.setAdapter(new ManagerAdapter(this));
        contentPageView.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPageChange(int position, int itemCount, int pageSize) {
                updatePageStatus(false);
            }
        });
        contentPageView.gotoPage(0);
    }

    private void initSortByView() {
        binding.buttonSortBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSortByDialog();
            }
        });
    }

    private void saveSortConfig(SortBy sortBy, SortOrder sortOrder) {
        ConfigPreferenceManager.setStorageSortBy(getApplicationContext(), sortBy);
        ConfigPreferenceManager.setStorageSortOrder(getApplicationContext(), sortOrder);
    }

    private void showSortByDialog() {
        final SortByProcessAction sortByAction = new SortByProcessAction(this,
                ConfigPreferenceManager.getStorageSortBy(this),
                ConfigPreferenceManager.getStorageSortOrder(this));
        sortByAction.execute(getDataHolder(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                saveSortConfig(sortByAction.getResultSortBy(), sortByAction.getResultSortOrder());
                loadData(getStorageViewModel().getCurrentFile());
            }
        });
    }

    private void updatePageStatus(boolean resetPage) {
        PageRecyclerView contentPageView = binding.contentPageView;
        if (contentPageView == null || contentPageView.getPaginator() == null) {
            Log.w(TAG, "detect the null contentPageView or Paginator");
            return;
        }
        GPaginator paginator = contentPageView.getPaginator();
        paginator.resize(getRowCountBasedViewType(), getColCountBasedViewType(), contentPageView.getAdapter().getItemCount());
        if (resetPage) {
            paginator.setCurrentPage(0);
        }
        getStorageViewModel().setPageStatus(paginator.getVisibleCurrentPage(), paginator.pages());
    }

    private void updateContentView() {
        PageRecyclerView contentPageView = binding.contentPageView;
        if (contentPageView == null) {
            return;
        }
        contentPageView.getAdapter().notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getEventBus().unregister(this);
    }

    @Override
    public void onBackPressed() {
        if (getStorageViewModel().canGoUp()) {
            loadData(getStorageViewModel().getParentFile());
            return;
        }
        super.onBackPressed();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLevelGoUpEvent() {
        onBackPressed();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onViewTypeEvent(ViewTypeEvent event) {
        PageRecyclerView contentView = binding.contentPageView;
        contentView.setAdapter(contentView.getAdapter());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onItemClickEvent(StorageItemViewModelClickEvent event) {
        StorageItemViewModel itemModel = event.model;
        if (itemModel.getFileModel().isGoUpType()) {
            onLevelGoUpEvent();
            getStorageViewModel().clearItemSelectedMap();
            return;
        }
        if (getStorageViewModel().isInMultiSelectionMode()) {
            getStorageViewModel().toggleItemModelSelection(itemModel);
            return;
        }
        processFileClick(itemModel.getFileModel().getFile());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onItemLongClickEvent(StorageItemViewModelLongClickEvent event) {
    }

    private void processFileClick(File file) {
        if (file.isDirectory()) {
            loadData(file);
        } else if (file.isFile()) {
            openFile(file);
        }
    }

    private void loadRootDirData() {
        loadData(EnvironmentUtil.getStorageRootDirectory());
    }

    private void loadData(File dir) {
        StorageDataLoadAction dataLoadAction = new StorageDataLoadAction(dir, getStorageViewModel().items);
        dataLoadAction.setSort(ConfigPreferenceManager.getStorageSortBy(getApplicationContext()),
                ConfigPreferenceManager.getStorageSortOrder(getApplicationContext()));
        dataLoadAction.execute(getDataHolder(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    return;
                }
                getStorageViewModel().updateCurrentTitleName(getString(R.string.storage));
            }
        });
    }

    private void loadThumbnail(StorageItemViewModel itemModel) {
        itemModel.setCoverThumbnail(BitmapFactory.decodeResource(getResources(), itemModel.isDocument.get() ?
                R.drawable.unknown_document :
                (itemModel.getFileModel().isGoUpType() ? R.drawable.directory_go_up : R.drawable.directory)));
    }

    private void openFile(File file) {
        Intent intent = ViewDocumentUtils.viewActionIntentWithMimeType(file);
        ResolveInfo info = ViewDocumentUtils.getDefaultActivityInfo(this, intent,
                ViewDocumentUtils.getEduReaderComponentName().getPackageName());
        if (info == null) {
            return;
        }
        ActivityUtil.startActivitySafely(this, intent, info.activityInfo);
    }

    private EventBus getEventBus() {
        return getDataHolder().getEventBus();
    }

    private LibraryDataHolder getDataHolder() {
        if (dataHolder == null) {
            dataHolder = new LibraryDataHolder(getApplicationContext());
        }
        return dataHolder;
    }

    private StorageViewModel getStorageViewModel() {
        return storageViewModel;
    }

    private ViewType getViewType() {
        return getStorageViewModel().getCurrentViewType();
    }

    private static class FileThumbnailItemViewHolder extends BindingViewHolder<FileThumbnailItemBinding, StorageItemViewModel> {
        FileThumbnailItemViewHolder(FileThumbnailItemBinding binding) {
            super(binding);
        }

        public void bindTo(StorageItemViewModel model) {
            mBinding.setViewModel(model);
            mBinding.executePendingBindings();
        }
    }

    private static class FileDetailsItemViewHolder extends BindingViewHolder<FileDetailsItemBinding, StorageItemViewModel> {
        FileDetailsItemViewHolder(FileDetailsItemBinding binding) {
            super(binding);
        }

        public void bindTo(StorageItemViewModel model) {
            mBinding.setViewModel(model);
            mBinding.executePendingBindings();
        }
    }

    private int getRowCountBasedViewType() {
        return getViewType() == ViewType.Thumbnail ? 3 : 7;
    }

    private int getColCountBasedViewType() {
        return getViewType() == ViewType.Thumbnail ? 3 : 1;
    }

    public class ManagerAdapter extends PageAdapter<RecyclerView.ViewHolder, StorageItemViewModel, StorageItemViewModel> {
        private LayoutInflater mLayoutInflater;

        ManagerAdapter(Context context) {
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getRowCount() {
            return getRowCountBasedViewType();
        }

        @Override
        public int getColumnCount() {
            return getColCountBasedViewType();
        }

        @Override
        public int getItemViewType(int position) {
            return getViewType().ordinal();
        }

        @Override
        public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == ViewType.Thumbnail.ordinal()) {
                return new FileThumbnailItemViewHolder(FileThumbnailItemBinding.inflate(mLayoutInflater, parent, false));
            } else {
                return new FileDetailsItemViewHolder(FileDetailsItemBinding.inflate(mLayoutInflater, parent, false));
            }
        }

        @Override
        public void onPageBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            StorageItemViewModel model = getItemVMList().get(position);
            if (model.getFileModel().isGoUpType()) {
                model.setEnableSelection(false);
            } else {
                model.setEnableSelection(getStorageViewModel().isInMultiSelectionMode());
                model.setSelected(getStorageViewModel().isItemSelected(model));
            }
            loadThumbnail(model);
            if (getItemViewType(position) == ViewType.Thumbnail.ordinal()) {
                FileThumbnailItemViewHolder holder = (FileThumbnailItemViewHolder) viewHolder;
                holder.bindTo(model);
            } else {
                FileDetailsItemViewHolder holder = (FileDetailsItemViewHolder) viewHolder;
                holder.bindTo(model);
            }
        }

        @Override
        public void setRawData(List<StorageItemViewModel> rawData, Context context) {
            super.setRawData(rawData, context);
            getItemVMList().addAll(rawData);
            notifyContentChanged();
        }
    }

    private void notifyContentChanged() {
        updateContentView();
        updatePageStatus(true);
    }
}
