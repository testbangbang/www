package com.onyx.einfo.manager;

import android.content.Context;

import com.onyx.einfo.R;
import com.onyx.einfo.events.AccountAvailableEvent;
import com.onyx.einfo.events.AccountTokenErrorEvent;
import com.onyx.einfo.model.AccountInfo;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by suicheng on 2017/8/18.
 */
public class EventManager {
    private Context appContext;

    public EventManager(Context context) {
        this.appContext = context.getApplicationContext();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAccountAvailableEvent(AccountAvailableEvent event) {
        AccountInfo.sendUserInfoSettingIntent(appContext, event.getAccount());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAccountTokenErrorEvent(AccountTokenErrorEvent errorEvent) {
        AccountInfo.sendUserInfoSettingIntent(appContext,
                appContext.getString(R.string.account_un_login));
    }
}
