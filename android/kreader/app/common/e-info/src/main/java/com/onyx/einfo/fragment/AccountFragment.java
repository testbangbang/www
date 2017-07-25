package com.onyx.einfo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onyx.einfo.R;
import com.onyx.einfo.InfoApp;
import com.onyx.einfo.events.AccountAvailableEvent;
import com.onyx.einfo.events.TabSwitchEvent;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.db.table.EduAccountProvider;
import com.onyx.android.sdk.data.model.v2.EduAccount;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.data.request.cloud.v2.LoginByHardwareInfoRequest;
import com.onyx.android.sdk.ui.utils.PageTurningDetector;
import com.onyx.android.sdk.ui.utils.PageTurningDirection;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by suicheng on 2017/5/17.
 */

public class AccountFragment extends Fragment {

    @Bind(R.id.username_tv)
    TextView usernameTv;
    @Bind(R.id.organization_tv)
    TextView organizationTv;
    @Bind(R.id.phone_tv)
    TextView phoneTv;

    private float lastX, lastY;
    private boolean isUserVisible = false;

    public static AccountFragment newInstance() {
        return new AccountFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        ButterKnife.bind(this, view);
        initView((ViewGroup) view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initView(ViewGroup viewGroup) {
        final LoginByHardwareInfoRequest accountLoadRequest = new LoginByHardwareInfoRequest<>(EduAccountProvider.CONTENT_URI, EduAccount.class);
        InfoApp.getCloudStore().submitRequest(getContext(), accountLoadRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null || accountLoadRequest.getAccount() == null) {
                    return;
                }
                updateView(accountLoadRequest.getAccount());
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void updateView(NeoAccountBase account) {
        if (account == null || isViewInValid()) {
            return;
        }
        usernameTv.setText(account.getName());
        organizationTv.setText(getString(R.string.organization_do) + account.getFirstGroup());
        phoneTv.setText(getString(R.string.phone_do) + account.getPhone());
    }

    private boolean isViewInValid() {
        return usernameTv == null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastX = ev.getX();
                lastY = ev.getY();
                break;
            case MotionEvent.ACTION_UP:
                int direction = detectDirection(ev);
                if (direction == PageTurningDirection.NEXT) {
                    nextTab();
                } else if (direction == PageTurningDirection.PREV) {
                    prevTab();
                }
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onKeyEvent(KeyEvent event) {
        if (isUserVisible) {
            processKeyEvent(event);
        }
    }

    private void processKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            return;
        }
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_PAGE_DOWN:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                nextTab();
                break;
            case KeyEvent.KEYCODE_PAGE_UP:
            case KeyEvent.KEYCODE_VOLUME_UP:
                prevTab();
                break;
            default:
                break;
        }
    }

    private void nextTab() {
        if (isUserVisible) {
            EventBus.getDefault().post(TabSwitchEvent.createNextTabSwitch());
        }
    }

    private void prevTab() {
        if(isUserVisible) {
            EventBus.getDefault().post(TabSwitchEvent.createPrevTabSwitch());
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isUserVisible = isVisibleToUser;
    }

    private int detectDirection(MotionEvent currentEvent) {
        return PageTurningDetector.detectBothAxisTuring(getContext(), (int) (currentEvent.getX() - lastX), (int) (currentEvent.getY() - lastY));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAccountAvailableEvent(AccountAvailableEvent event) {
        updateView(event.getAccount());
    }
}
