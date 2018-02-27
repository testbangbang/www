package com.onyx.jdread.shop.model;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import com.onyx.jdread.R;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.shop.event.VipButtonClickEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by jackdeng on 2018/1/19.
 */

public class VipUserInfoViewModel extends BaseSubjectViewModel{
    public final ObservableField<String> name = new ObservableField<>();
    public final ObservableField<String> imageUrl = new ObservableField<>();
    public final ObservableField<String> vipStatus = new ObservableField<>();
    public final ObservableField<String> buttonContent = new ObservableField<>();
    public final ObservableBoolean showLoginButton = new ObservableBoolean(false);
    public EventBus eventBus;

    public VipUserInfoViewModel(EventBus eventBus) {
        setEventBus(eventBus);
        setSubjectType(SubjectType.TYPE_VIP_USER);
    }

    public void onVipButtonClick() {
        getEventBus().post(new VipButtonClickEvent(buttonContent.get()));
    }

    public void onLoginButtonClick() {
        getEventBus().post(new VipButtonClickEvent(ResManager.getString(R.string.login_immediately)));
    }
}
