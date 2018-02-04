package com.onyx.jdread.setting.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.data.QueryBase;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.model.common.FetchPolicy;
import com.onyx.android.sdk.data.utils.QueryBuilder;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.FeedbackRecordBinding;
import com.onyx.jdread.library.view.DashLineItemDivider;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.setting.adapter.FeedbackRecordAdapter;
import com.onyx.jdread.setting.data.database.FeedbackRecord;
import com.onyx.jdread.setting.data.database.FeedbackRecord_Table;
import com.onyx.jdread.setting.event.BackToSettingFragmentEvent;
import com.onyx.jdread.setting.model.SettingBundle;
import com.onyx.jdread.setting.request.RxFeedbackRecordListLoadRequest;
import com.onyx.jdread.util.Utils;
import com.raizlabs.android.dbflow.sql.language.OrderBy;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by li on 2018/1/18.
 */

public class FeedbackRecordFragment extends BaseFragment {
    private FeedbackRecordBinding binding;
    private FeedbackRecordAdapter feedbackRecordAdapter;
    private int queryLimit = 20;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_feedback_record, container, false);
        initView();
        initData();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.ensureRegister(SettingBundle.getInstance().getEventBus(), this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Utils.ensureUnregister(SettingBundle.getInstance().getEventBus(), this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideLoadingDialog();
    }

    private void initView() {
        binding.feedbackRecordRecycler.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        DashLineItemDivider dividerItemDecoration = new DashLineItemDivider();
        binding.feedbackRecordRecycler.addItemDecoration(dividerItemDecoration);
        feedbackRecordAdapter = new FeedbackRecordAdapter();
        binding.feedbackRecordRecycler.setAdapter(feedbackRecordAdapter);
        binding.feedbackRecordRecycler.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPageChange(int position, int itemCount, int pageSize) {
                if (!feedbackRecordAdapter.getPagePaginator().hasNextPage()) {
                    loadMoreDataList();
                }
            }
        });
    }

    private void initData() {
        initTitleData();
        loadDataList();
    }

    private void initTitleData() {
        TitleBarModel titleBarModel = new TitleBarModel(SettingBundle.getInstance().getEventBus());
        titleBarModel.title.set(JDReadApplication.getInstance().getResources().getString(R.string.feedback_history));
        titleBarModel.backEvent.set(new BackToSettingFragmentEvent());
        binding.feedbackRecordTitle.setTitleModel(titleBarModel);
    }

    private QueryBase getQueryArgs() {
        QueryBase args = new QueryBase();
        args.limit = queryLimit;
        args.fetchPolicy = FetchPolicy.CLOUD_MEM_DB;
        args.orderByList.add(QueryBuilder.ascDescOrder(OrderBy.fromProperty(FeedbackRecord_Table.createdAt),
                args.order == SortOrder.Asc));
        return args;
    }

    private void loadDataList() {
        loadData(getQueryArgs(), false);
    }

    private void loadMoreDataList() {
        QueryBase args = getQueryArgs();
        args.offset = feedbackRecordAdapter.getDataCount();
        loadData(args, true);
    }

    private void loadData(QueryBase queryArgs, final boolean loadMore) {
        final RxFeedbackRecordListLoadRequest listLoadRequest = new RxFeedbackRecordListLoadRequest(
                SettingBundle.getInstance().getDataManager(), queryArgs);
        listLoadRequest.execute(new RxCallback<RxFeedbackRecordListLoadRequest>() {
            @Override
            public void onNext(RxFeedbackRecordListLoadRequest request) {
                notifyDataChanged(listLoadRequest.getRecordList(), loadMore);
            }

            @Override
            public void onError(Throwable throwable) {
                ToastUtil.showToast(R.string.network_or_server_error);
            }

            @Override
            public void onFinally() {
                hideLoadingDialog();
            }
        });
        showLoadingDialog(getString(loadMore ? R.string.loading_more : R.string.loading));
    }

    private void notifyDataChanged(List<FeedbackRecord> list, boolean loadMore) {
        if (loadMore && CollectionUtils.isNullOrEmpty(list)) {
            ToastUtil.showToast(R.string.no_more_content);
            return;
        }
        feedbackRecordAdapter.addDataList(list, !loadMore);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackToSettingFragmentEvent(BackToSettingFragmentEvent event) {
        viewEventCallBack.viewBack();
    }
}
