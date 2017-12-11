package com.onyx.android.eschool.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.eschool.R;
import com.onyx.android.eschool.SchoolApp;
import com.onyx.android.eschool.custom.PageIndicator;
import com.onyx.android.eschool.databinding.FragmentHomeworkBinding;
import com.onyx.android.eschool.databinding.ItemHomeworkBinding;
import com.onyx.android.eschool.events.TabSwitchEvent;
import com.onyx.android.eschool.holder.LibraryDataHolder;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.GPaginator;
import com.onyx.android.sdk.data.QueryBase;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.model.common.FetchPolicy;
import com.onyx.android.sdk.data.model.v2.Homework;
import com.onyx.android.sdk.data.model.v2.ResourceCode;
import com.onyx.android.sdk.data.model.v2.ResourceQuery;
import com.onyx.android.sdk.data.request.cloud.v2.HomeworkListLoadRequest;
import com.onyx.android.sdk.data.utils.CloudConf;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.data.utils.MetadataUtils;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.data.v2.ContentService;
import com.onyx.android.sdk.ui.dialog.DialogProgressHolder;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.utils.ViewDocumentUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2017/12/7.
 */
public class HomeworkFragment extends Fragment {
    private static final String TAG = "HomeworkFragment";

    private PageRecyclerView contentPageView;

    private FragmentHomeworkBinding binding;
    private PageIndicator pageIndicator;

    private DialogProgressHolder dialogProgressHolder = new DialogProgressHolder();

    private int pageCol = 3;
    private int pageRow = 2;
    private int queryLimit = 20;

    private CloudManager tempCloudManager;
    private boolean isVisibleToUser;

    private List<Homework> homeworkDataList = new ArrayList<>();

    public static HomeworkFragment newInstance() {
        HomeworkFragment fragment = new HomeworkFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_homework, container, false);
        initView();
        return binding.getRoot();
    }

    private void initView() {
        initPageView();
        initPageIndicator();
    }

    private void initPageIndicator() {
        pageIndicator = new PageIndicator(binding.pageIndicatorLayout.getRoot(), contentPageView.getPaginator());
        pageIndicator.setTotalFormat(getString(R.string.total_format));
        pageIndicator.setPageChangedListener(new PageIndicator.PageChangedListener() {
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
        });
        pageIndicator.setDataRefreshListener(new PageIndicator.DataRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
    }

    private void initPageView() {
        contentPageView = binding.contentPageView;
        contentPageView.setLayoutManager(new DisableScrollGridManager(getContext()));
        contentPageView.setAdapter(new PageRecyclerView.PageAdapter<ItemViewHolder>() {
            @Override
            public int getRowCount() {
                return pageRow;
            }

            @Override
            public int getColumnCount() {
                return pageCol;
            }

            @Override
            public int getDataCount() {
                return getTotalCount();
            }

            @Override
            public ItemViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                ItemHomeworkBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.item_homework, parent, false);
                return new ItemViewHolder(binding);
            }

            @Override
            public void onPageBindViewHolder(ItemViewHolder holder, final int position) {
                holder.itemView.setTag(position);
                Homework item = homeworkDataList.get(position);
                holder.bind(item);
            }
        });
        contentPageView.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPageChange(int position, int itemCount, int pageSize) {
                updatePageIndicator();
                if (!contentPageView.getPaginator().hasNextPage()) {
                    loadMoreData();
                }
            }
        });
        contentPageView.setOnLoadListener(new PageRecyclerView.OnLoadListener() {
            @Override
            public void onRefresh() {
                prevPage();
            }

            @Override
            public void onLoadMore() {
                nextPage();
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadData(getQueryArgs(queryLimit, 0), true, null);
    }

    private void prevPage() {
        if (getPagination().isFirstPage()) {
            postPrevTab();
            return;
        }
        contentPageView.prevPage();
    }

    private void nextPage() {
        if (getPagination().isLastPage()) {
            postNextTab();
            return;
        }
        contentPageView.nextPage();
    }

    private void postNextTab() {
        EventBus.getDefault().post(TabSwitchEvent.createNextTabSwitch());
    }

    private void postPrevTab() {
        EventBus.getDefault().post(TabSwitchEvent.createPrevTabSwitch());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onKeyEvent(KeyEvent keyEvent) {
        if (contentPageView != null && isVisibleToUser) {
            contentPageView.dispatchKeyEvent(keyEvent);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (isVisibleToUser) {
            updateContentView(homeworkDataList);
        }
    }

    private boolean isVisibleToUser() {
        return isVisibleToUser;
    }

    private GPaginator getPagination() {
        return contentPageView.getPaginator();
    }

    private QueryBase getQueryArgs(int limit, int offset) {
        QueryBase args = new QueryBase();
        args.limit = limit;
        args.offset = offset;
        args.query = JSONObjectParseUtils.toJson(new ResourceQuery(ResourceCode.MY_HOMEWORK, ResourceCode.MY_HOMEWORK_REF));
        return args;
    }

    private void refreshData() {
        final QueryBase args = getQueryArgs(queryLimit, 0);
        args.fetchPolicy = FetchPolicy.CLOUD_ONLY;
        loadData(args, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                gotoPage(0);
            }
        });
    }

    private void loadMoreData() {
        QueryBase args = getQueryArgs(queryLimit, CollectionUtils.getSize(homeworkDataList));
        loadData(args, false, null);
    }

    private void loadData(final QueryBase args, final boolean clearLocal, final BaseCallback callback) {
        final HomeworkListLoadRequest loadRequest = new HomeworkListLoadRequest(args, clearLocal);
        getCloudManager().submitRequest(getContext(), loadRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                dialogProgressHolder.dismissProgressDialog(loadRequest);
                QueryResult<Homework> result = loadRequest.getQueryResult();
                if (e != null || result.hasException()) {
                    showTipsMessage(R.string.refresh_fail);
                    return;
                }
                if (!QueryResult.isValidQueryResult(result)) {
                    showTipsMessage(R.string.no_more_items);
                    return;
                }
                notificationDataChanged(loadRequest.getQueryResult(), clearLocal);
                BaseCallback.invoke(callback, loadRequest, null);
            }
        });
        dialogProgressHolder.showProgressDialog(getContext(), loadRequest, R.string.loading, null);
    }

    private void showTipsMessage(int resId) {
        if (!isVisibleToUser()) {
            return;
        }
        ToastUtils.showToast(getContext().getApplicationContext(), resId);
    }

    private void notificationDataChanged(QueryResult<Homework> result, boolean clear) {
        if (result == null || CollectionUtils.isNullOrEmpty(result.list)) {
            return;
        }
        if (clear) {
            homeworkDataList.clear();
        }
        homeworkDataList.addAll(result.list);
        updateContentView(result.list);
    }

    private void updateContentView(List<Homework> list) {
        updateContentView();
    }

    private void updateContentView() {
        if (contentPageView == null) {
            return;
        }
        contentPageView.getAdapter().notifyDataSetChanged();
        updatePageIndicator();
    }

    private int getTotalCount() {
        return CollectionUtils.getSize(homeworkDataList);
    }

    private void updatePageIndicator() {
        int totalCount = getTotalCount();
        getPagination().resize(pageRow, pageCol, totalCount);
        pageIndicator.resetGPaginator(getPagination());
        pageIndicator.updateTotal(totalCount);
        pageIndicator.updateCurrentPage(totalCount);
    }

    private void gotoPage(int selectPage) {
        contentPageView.gotoPage(selectPage);
        updatePageIndicator();
    }

    private void processItemClick(int position) {
        Intent intent = new Intent();
        intent.setComponent(ViewDocumentUtils.getHomeworkAppComponent());
        MetadataUtils.putIntentExtraData(intent, homeworkDataList.get(position));
        boolean success = ActivityUtil.startActivitySafely(getContext(), intent);
        if (!success) {
            ToastUtils.showToast(getContext().getApplicationContext(), R.string.app_no_installed);
        }
    }

    private void updateTokenHeader(final CloudManager cloudManager) {
        if (StringUtils.isNotBlank(cloudManager.getToken())) {
            ServiceFactory.addRetrofitTokenHeader(cloudManager.getCloudConf().getApiBase(),
                    Constant.HEADER_AUTHORIZATION,
                    ContentService.CONTENT_AUTH_PREFIX + cloudManager.getToken());
        }
    }

    private CloudConf getCloudConf(String host, String api) {
        return CloudConf.create(host, api, Constant.DEFAULT_CLOUD_STORAGE);
    }

    private CloudManager getTempCloudManager() {
        if (tempCloudManager != null) {
            return tempCloudManager;
        }
        tempCloudManager = new CloudManager();
        CloudConf conf = getCloudConf("http://120.78.79.5/", "http://120.78.79.5/api/");
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI1OWVkOWIyYTA3Njk3ZDRiNGYzOGYzMzYiLCJyb2xlIjoidGVhY2hlciIsImlhdCI6MTUxMjcwNjY3MiwiZXhwIjoxNTE1Mjk4NjcyfQ.H6rMKBwFD3UHsAiPYSTryO-_DAo2ad3OEjoHQmpsL_c";
        tempCloudManager.setAllCloudConf(conf);
        tempCloudManager.setToken(token);
        updateTokenHeader(tempCloudManager);
        return tempCloudManager;
    }

    private CloudManager getCloudManager() {
        return getTempCloudManager();
    }

    private LibraryDataHolder getDataHolder() {
        return SchoolApp.getLibraryDataHolder();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private final ItemHomeworkBinding binding;

        public ItemViewHolder(ItemHomeworkBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    processItemClick((Integer) v.getTag());
                }
            });
        }

        public void bind(Homework item) {
            binding.setViewModel(item);
            binding.executePendingBindings();
        }
    }
}
