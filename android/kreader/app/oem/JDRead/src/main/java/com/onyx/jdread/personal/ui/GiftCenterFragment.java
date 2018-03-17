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
import com.onyx.jdread.databinding.GiftCenterBinding;
import com.onyx.jdread.library.view.DashLineItemDivider;
import com.onyx.jdread.library.view.LibraryDeleteDialog;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.personal.action.CheckGiftAction;
import com.onyx.jdread.personal.action.GetGiftAction;
import com.onyx.jdread.personal.adapter.GiftCenterAdapter;
import com.onyx.jdread.personal.cloud.entity.jdbean.CheckGiftBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.GiftBean;
import com.onyx.jdread.personal.event.ReceivePackageEvent;
import com.onyx.jdread.personal.model.GiftPackageModel;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.setting.event.BackToSettingFragmentEvent;
import com.onyx.jdread.util.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by li on 2017/12/29.
 */

public class GiftCenterFragment extends BaseFragment {
    private GiftCenterBinding binding;
    private GiftCenterAdapter giftCenterAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = (GiftCenterBinding) DataBindingUtil.inflate(inflater, R.layout.fragment_gift_center, container, false);
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
        titleModel.title.set(ResManager.getString(R.string.gift_center));
        titleModel.backEvent.set(new BackToSettingFragmentEvent());
        binding.giftCenterTitleBar.setTitleModel(titleModel);

        final CheckGiftAction action = new CheckGiftAction();
        action.execute(PersonalDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                CheckGiftBean.DataBean data = action.getData();
                if (data != null) {
                    GiftPackageModel giftPackageModel = new GiftPackageModel();
                    giftPackageModel.setEventBus(PersonalDataBundle.getInstance().getEventBus());
                    giftPackageModel.loadGifts();
                    giftCenterAdapter.setData(data.gift ? giftPackageModel.getGiftBeans() : null);
                }
            }
        });
    }

    private void initView() {
        binding.giftCenterRecycler.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        DashLineItemDivider decoration = new DashLineItemDivider();
        binding.giftCenterRecycler.addItemDecoration(decoration);
        giftCenterAdapter = new GiftCenterAdapter();
        binding.giftCenterRecycler.setAdapter(giftCenterAdapter);
    }

    private void initListener() {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackToSettingFragmentEvent(BackToSettingFragmentEvent event) {
        viewEventCallBack.viewBack();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceivePackageEvent(ReceivePackageEvent event) {
        if (!Utils.isNetworkConnected(JDReadApplication.getInstance())) {
            ToastUtil.showToast(ResManager.getString(R.string.wifi_no_connected));
            return;
        }
        LibraryDeleteDialog.DialogModel model = new LibraryDeleteDialog.DialogModel();
        final LibraryDeleteDialog dialog = new LibraryDeleteDialog.Builder(JDReadApplication.getInstance(), model).create();
        String tips = ResManager.getString(R.string.receive_gift_package_tips);
        model.message.set(tips);
        model.setPositiveClickLister(new LibraryDeleteDialog.DialogModel.OnClickListener() {
            @Override
            public void onClicked() {
                receive();
                dialog.dismiss();
            }
        });
        model.setNegativeClickLister(new LibraryDeleteDialog.DialogModel.OnClickListener() {
            @Override
            public void onClicked() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void receive() {
        final GetGiftAction action = new GetGiftAction();
        action.execute(PersonalDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                GiftBean giftBean = action.getGiftBean();
                if (giftBean != null) {
                    giftCenterAdapter.clear();
                    ToastUtil.showToast(giftBean.result_code == 0 ?
                            ResManager.getString(R.string.receive_success) :
                            ResManager.getString(R.string.receive_fail));
                }
            }
        });
    }
}
