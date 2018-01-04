package com.onyx.jdread.personal.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.OnyxPageDividerItemDecoration;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.GiftCenterBinding;
import com.onyx.jdread.library.view.LibraryDeleteDialog;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.personal.action.UserLoginAction;
import com.onyx.jdread.personal.adapter.GiftCenterAdapter;
import com.onyx.jdread.personal.event.UserLoginEvent;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.setting.event.BackToSettingFragmentEvent;
import com.onyx.jdread.util.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
        PersonalDataBundle.getInstance().getEventBus().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        PersonalDataBundle.getInstance().getEventBus().unregister(this);
    }

    private void initData() {
        TitleBarModel titleModel = PersonalDataBundle.getInstance().getTitleModel();
        titleModel.title.set(JDReadApplication.getInstance().getResources().getString(R.string.gift_center));
        titleModel.backEvent.set(new BackToSettingFragmentEvent());
        binding.giftCenterTitleBar.setTitleModel(titleModel);
    }

    private void initView() {
        binding.giftCenterRecycler.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        binding.giftCenterRecycler.addItemDecoration(new OnyxPageDividerItemDecoration(JDReadApplication.getInstance(), OnyxPageDividerItemDecoration.VERTICAL));
        giftCenterAdapter = new GiftCenterAdapter();
        binding.giftCenterRecycler.setAdapter(giftCenterAdapter);
    }

    private void initListener() {
        if (giftCenterAdapter != null) {
            giftCenterAdapter.setOnItemClickListener(new PageRecyclerView.PageAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    // TODO: 2018/1/3
                    LibraryDeleteDialog.DialogModel model = new LibraryDeleteDialog.DialogModel();
                    final LibraryDeleteDialog dialog = new LibraryDeleteDialog.Builder(JDReadApplication.getInstance(), model).create();
                    String tips = JDReadApplication.getInstance().getResources().getString(R.string.receive_gift_package_tips);
                    model.message.set(String.format(tips, position + ""));
                    model.setPositiveClickLister(new LibraryDeleteDialog.DialogModel.OnClickListener() {
                        @Override
                        public void onClicked() {
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
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackToSettingFragmentEvent(BackToSettingFragmentEvent event) {
        viewEventCallBack.viewBack();
    }
}
