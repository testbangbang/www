package com.onyx.jdread.personal.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.PersonalTaskBinding;
import com.onyx.jdread.library.view.DashLineItemDivider;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.personal.action.ReadForVoucherAction;
import com.onyx.jdread.personal.action.SignForVoucherAction;
import com.onyx.jdread.personal.adapter.PersonalTaskAdapter;
import com.onyx.jdread.personal.cloud.entity.jdbean.ReadForVoucherBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.SignForVoucherBean;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.model.PersonalTaskData;
import com.onyx.jdread.personal.model.PersonalTaskModel;
import com.onyx.jdread.setting.event.BackToSettingFragmentEvent;
import com.onyx.jdread.util.TimeUtils;
import com.onyx.jdread.util.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by li on 2018/1/3.
 */

public class PersonalTaskFragment extends BaseFragment {
    private PersonalTaskBinding binding;
    private PersonalTaskAdapter personalTaskAdapter;
    private static final int READING_VOUCHER_LOCATION = 1;
    private List<PersonalTaskData> tasks;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = (PersonalTaskBinding) DataBindingUtil.inflate(inflater, R.layout.fragment_personal_task, container, false);
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

    private void initView() {
        binding.personalTaskRecycler.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        DashLineItemDivider decoration = new DashLineItemDivider();
        binding.personalTaskRecycler.addItemDecoration(decoration);
        personalTaskAdapter = new PersonalTaskAdapter();
        binding.personalTaskRecycler.setAdapter(personalTaskAdapter);
    }

    private void initData() {
        TitleBarModel titleModel = PersonalDataBundle.getInstance().getTitleModel();
        titleModel.title.set(JDReadApplication.getInstance().getResources().getString(R.string.personal_task));
        titleModel.backEvent.set(new BackToSettingFragmentEvent());
        binding.personalTaskTitle.setTitleModel(titleModel);

        PersonalTaskModel personalTaskModel = PersonalDataBundle.getInstance().getPersonalTaskModel();
        tasks = personalTaskModel.getTasks();
        if (personalTaskAdapter != null) {
            personalTaskAdapter.setData(tasks);
        }

        if (PersonalDataBundle.getInstance().getSigned()) {
            final SignForVoucherAction signForVoucherAction = new SignForVoucherAction();
            signForVoucherAction.execute(PersonalDataBundle.getInstance(), new RxCallback() {
                @Override
                public void onNext(Object o) {
                    SignForVoucherBean resultBean = signForVoucherAction.getResultBean();
                    if (resultBean.result_code == 0) {
                        PersonalDataBundle.getInstance().setCurrentDay(TimeUtils.getCurrentDataInString());
                    }
                }
            });
        }
    }

    private void initListener() {
        if (personalTaskAdapter != null) {
            personalTaskAdapter.setOnItemClickListener(new PageRecyclerView.PageAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    if (position == READING_VOUCHER_LOCATION) {
                        receiveVoucher(position);
                    }
                }
            });
        }
    }

    private void receiveVoucher(final int position) {
        final ReadForVoucherAction readForVoucherAction = new ReadForVoucherAction();
        readForVoucherAction.execute(PersonalDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                ReadForVoucherBean resultBean = readForVoucherAction.getResultBean();
                if (resultBean != null && resultBean.result_code == 0) {
                    int voucher = resultBean.data.voucher;
                    ToastUtil.showToast(String.format(ResManager.getString(R.string.add_ten_vouchers), voucher));
                    PersonalTaskData personalTaskData = tasks.get(position);
                    personalTaskData.setTaskStatus(ResManager.getString(R.string.have_receive));
                    PersonalDataBundle.getInstance().setReceiveReadVoucherTime(TimeUtils.getCurrentDataInString());
                } else {
                    ToastUtil.showToast(ResManager.getString(R.string.receive_fail));
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackToSettingFragmentEvent(BackToSettingFragmentEvent event) {
        viewEventCallBack.viewBack();
    }
}
