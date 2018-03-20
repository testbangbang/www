package com.onyx.jdread.personal.ui;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.onyx.android.sdk.data.GPaginator;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ConsumptionRecordBinding;
import com.onyx.jdread.databinding.NoneResultBinding;
import com.onyx.jdread.library.view.DashLineItemDivider;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.model.NoneResultModel;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.personal.action.ConsumeRecordAction;
import com.onyx.jdread.personal.adapter.ConsumptionRecordAdapter;
import com.onyx.jdread.personal.cloud.entity.jdbean.ConsumeRecordBean;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.setting.event.BackToSettingFragmentEvent;
import com.onyx.jdread.util.Utils;
import com.onyx.jdread.util.ViewCompatUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by li on 2018/1/2.
 */

public class ConsumptionRecordFragment extends BaseFragment {
    private ConsumptionRecordBinding binding;
    private ConsumptionRecordAdapter adapter;
    private GPaginator paginator;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = (ConsumptionRecordBinding) DataBindingUtil.inflate(inflater, R.layout.fragment_consumption_record, container, false);
        initView();
        initData();
        initListener();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.ensureRegister(PersonalDataBundle.getInstance().getEventBus(), this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Utils.ensureUnregister(PersonalDataBundle.getInstance().getEventBus(), this);
    }

    private void initData() {
        TitleBarModel titleModel = PersonalDataBundle.getInstance().getTitleModel();
        titleModel.title.set(ResManager.getString(R.string.consumption_record));
        titleModel.backEvent.set(new BackToSettingFragmentEvent());
        binding.consumptionRecordTitle.setTitleModel(titleModel);

        final ConsumeRecordAction consumeRecordAction = new ConsumeRecordAction();
        consumeRecordAction.execute(PersonalDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                List<ConsumeRecordBean.DataBean> data = consumeRecordAction.getData();
                if (data != null) {
                    adapter.setData(data);
                    binding.consumptionRecordRecycler.resize(adapter.getRowCount(), adapter.getColumnCount(), data.size());
                    setPageSize();
                }
                updateContentView();
            }
        });
    }

    private void initView() {
        binding.consumptionRecordRecycler.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        DashLineItemDivider decoration = new DashLineItemDivider();
        binding.consumptionRecordRecycler.addItemDecoration(decoration);
        adapter = new ConsumptionRecordAdapter(false);
        binding.consumptionRecordRecycler.setAdapter(adapter);
        paginator = binding.consumptionRecordRecycler.getPaginator();
        binding.consumptionRecordRecycler.setPageTurningCycled(true);
    }

    private void initListener() {
        binding.consumptionRecordRecycler.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPageChange(int position, int itemCount, int pageSize) {
                setPageSize();
            }
        });
    }

    private void updateContentView() {
        if (binding == null || binding.contentView == null) {
            return;
        }
        adapter.notifyDataSetChanged();
        binding.contentView.setVisibility(adapter.getDataCount() > 0 ? View.VISIBLE : View.GONE);
        ViewCompatUtil.showNoneResultView(binding.noneResultView, adapter.getDataCount() <= 0,
                new NoneResultModel(R.mipmap.ic_me_consume_none, ResManager.getString(R.string.consumption_none_record)));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackToSettingFragmentEvent(BackToSettingFragmentEvent event) {
        viewEventCallBack.viewBack();
    }

    private void setPageSize() {
        String progressText = paginator.getProgressText();
        binding.setPageText(progressText);
    }
}
