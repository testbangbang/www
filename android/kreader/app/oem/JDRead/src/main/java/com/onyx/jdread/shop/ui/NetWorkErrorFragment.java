package com.onyx.jdread.shop.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.jdread.R;
import com.onyx.jdread.databinding.FragmentNetworkErrorBinding;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.setting.ui.WifiFragment;
import com.onyx.jdread.shop.event.TopBackEvent;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.model.TitleBarViewModel;
import com.onyx.jdread.util.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by jackdeng on 2018/3/5.
 */

public class NetWorkErrorFragment extends BaseFragment {

    private FragmentNetworkErrorBinding netErrorBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        netErrorBinding = FragmentNetworkErrorBinding.inflate(inflater, container, false);
        initView();
        return netErrorBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.ensureRegister(getEventBus(), this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Utils.ensureUnregister(getEventBus(), this);
    }

    private void initView() {
        SpannableString spannableString = new SpannableString(ResManager.getString(R.string.please_check_wifi));
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                gotoWifiFragment();
            }
        }, ResManager.getInteger(R.integer.net_error_tip_goto_setting_start), ResManager.getInteger(R.integer.net_error_tip_goto_setting_end), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        netErrorBinding.pleaseCheckWifi.setText(spannableString);
        netErrorBinding.pleaseCheckWifi.setMovementMethod(LinkMovementMethod.getInstance());
        TitleBarViewModel titleBarViewModel = new TitleBarViewModel();
        titleBarViewModel.setEventBus(getEventBus());
        String title = "";
        Bundle bundle = getBundle();
        if (bundle != null) {
            title = bundle.getString(Constants.NET_ERROR_TITLE);
            titleBarViewModel.showTitleBar.set(bundle.getBoolean(Constants.NET_ERROR_SHOW_TITLE_BAR, true));
        }
        titleBarViewModel.leftText = title;
        netErrorBinding.setTitleBarViewModel(titleBarViewModel);
    }

    private void gotoWifiFragment() {
        viewEventCallBack.gotoView(WifiFragment.class.getName());
    }

    private EventBus getEventBus() {
        return getShopDataBundle().getEventBus();
    }

    public ShopDataBundle getShopDataBundle() {
        return ShopDataBundle.getInstance();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTopBackEvent(TopBackEvent event) {
        if (getViewEventCallBack() != null) {
            getViewEventCallBack().viewBack();
        }
    }
}
