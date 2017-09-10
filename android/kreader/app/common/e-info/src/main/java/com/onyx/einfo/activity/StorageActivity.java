package com.onyx.einfo.activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.onyx.android.sdk.data.GPaginator;
import com.onyx.android.sdk.data.ViewType;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.einfo.R;
import com.onyx.einfo.adapter.BindingViewHolder;
import com.onyx.einfo.adapter.PageAdapter;
import com.onyx.einfo.databinding.ActivityStorageBinding;
import com.onyx.einfo.databinding.FileDetailsItemBinding;
import com.onyx.einfo.databinding.FileThumbnailItemBinding;
import com.onyx.einfo.model.StorageItemViewModel;
import com.onyx.einfo.model.StorageNavigator;
import com.onyx.einfo.model.StorageViewModel;

import java.util.List;

/**
 * Created by suicheng on 2017/9/9.
 */

public class StorageActivity extends OnyxAppCompatActivity implements StorageNavigator {
    private static final String TAG = "StorageActivity";

    private StorageViewModel storageViewModel;
    private ActivityStorageBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        prevBinding();
        initToolbar();
        initRecyclerView();
    }

    private void prevBinding() {
        storageViewModel = new StorageViewModel(this);
        storageViewModel.setNavigator(this);
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
        storageViewModel.setPageStatus(paginator.getVisibleCurrentPage(), paginator.pages());
    }

    private void updateContentView() {
        PageRecyclerView contentPageView = binding.contentPageView;
        if (contentPageView == null) {
            return;
        }
        contentPageView.getAdapter().notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        storageViewModel.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        storageViewModel.onActivityDestroyed();
    }

    @Override
    public void onBackPressed() {
        if (!storageViewModel.goUp()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onGoUp() {
        onBackPressed();
    }

    @Override
    public void onViewChange() {
        PageRecyclerView contentView = binding.contentPageView;
        contentView.setAdapter(contentView.getAdapter());
    }

    private StorageViewModel getStorageViewModel() {
        return storageViewModel;
    }

    private ViewType getViewType() {
        return getStorageViewModel().getCurrentViewType();
    }

    private static class FileItemViewHolder extends BindingViewHolder<FileThumbnailItemBinding, StorageItemViewModel> {
        FileItemViewHolder(FileThumbnailItemBinding binding) {
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
                return new FileItemViewHolder(FileThumbnailItemBinding.inflate(mLayoutInflater, parent, false));
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
            }
            model.setSelected(getStorageViewModel().isItemSelected(model));
            if (getItemViewType(position) == 0) {
                FileItemViewHolder holder = (FileItemViewHolder) viewHolder;
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
