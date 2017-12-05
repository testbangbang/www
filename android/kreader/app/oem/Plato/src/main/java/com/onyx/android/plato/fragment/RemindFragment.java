package com.onyx.android.plato.fragment;

import android.databinding.ViewDataBinding;

import com.onyx.android.plato.R;
import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.adapter.RemindAdapter;
import com.onyx.android.plato.cloud.bean.ContentBean;
import com.onyx.android.plato.databinding.RemindBinding;
import com.onyx.android.plato.event.ToMainFragmentEvent;
import com.onyx.android.plato.view.DisableScrollGridManager;
import com.onyx.android.plato.view.DividerItemDecoration;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by li on 2017/11/13.
 */

public class RemindFragment extends BaseFragment {
    private List<ContentBean> remindContent;
    private RemindBinding remindBinding;
    private RemindAdapter remindAdapter;

    @Override
    protected void loadData() {

    }

    @Override
    protected void initView(ViewDataBinding binding) {
        remindBinding = (RemindBinding) binding;
        remindBinding.remindRecyclerView.setLayoutManager(new DisableScrollGridManager(SunApplication.getInstance()));
        DividerItemDecoration itemDecoration = new DividerItemDecoration(SunApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        itemDecoration.setDrawLine(true);
        remindBinding.remindRecyclerView.addItemDecoration(itemDecoration);
        remindAdapter = new RemindAdapter();
        remindBinding.remindRecyclerView.setAdapter(remindAdapter);
    }

    @Override
    protected void initListener() {
        if (remindAdapter != null) {
            remindAdapter.setData(remindContent);
        }
    }

    @Override
    protected int getRootView() {
        return R.layout.remaind_layout;
    }

    @Override
    public boolean onKeyBack() {
        EventBus.getDefault().post(new ToMainFragmentEvent());
        return true;
    }

    public void setRemindContent(List<ContentBean> remindContent) {
        this.remindContent = remindContent;
        if (remindAdapter != null) {
            remindAdapter.setData(remindContent);
        }
    }
}
