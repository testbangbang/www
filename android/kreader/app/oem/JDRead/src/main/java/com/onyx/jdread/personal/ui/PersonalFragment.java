package com.onyx.jdread.personal.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.PersonalBinding;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.personal.action.LoginOutAction;
import com.onyx.jdread.personal.adapter.PersonalAdapter;
import com.onyx.jdread.personal.cloud.entity.jdbean.UserInfo;
import com.onyx.jdread.personal.common.LoginHelper;
import com.onyx.jdread.personal.event.GiftCenterEvent;
import com.onyx.jdread.personal.event.PersonalAccountEvent;
import com.onyx.jdread.personal.event.PersonalBookEvent;
import com.onyx.jdread.personal.event.PersonalNoteEvent;
import com.onyx.jdread.personal.event.PersonalTaskEvent;
import com.onyx.jdread.personal.event.ReadPreferenceEvent;
import com.onyx.jdread.personal.event.UserInfoEvent;
import com.onyx.jdread.personal.event.UserLoginEvent;
import com.onyx.jdread.personal.event.VerifySignEvent;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.model.PersonalModel;
import com.onyx.jdread.shop.view.DividerItemDecoration;
import com.onyx.jdread.util.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by huxiaomao on 2017/12/7.
 */

public class PersonalFragment extends BaseFragment {
    private PersonalBinding binding;
    private PersonalAdapter personalAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = (PersonalBinding) DataBindingUtil.inflate(inflater, R.layout.fragment_personal, container, false);
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

    private void initListener() {
        binding.personalInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewEventCallBack.gotoView(PersonalExperienceFragment.class.getName());
            }
        });

        binding.personalLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogin("");
            }
        });

        binding.personalLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utils.isNetworkConnected(JDReadApplication.getInstance())) {
                    ToastUtil.showToast(ResManager.getString(R.string.wifi_no_connected));
                    return;
                }
                LoginOutAction loginOutAction = new LoginOutAction(binding);
                loginOutAction.execute(PersonalDataBundle.getInstance(), null);
            }
        });
    }

    private void initData() {
        int currentReadTime = JDReadApplication.getInstance().getCurrentReadTime();
        binding.setReadTime(String.valueOf(currentReadTime / Constants.MINUTE_STEP));
        binding.setIsLogin(JDReadApplication.getInstance().getLogin());
        binding.setIsSignToday(PersonalDataBundle.getInstance().isTodaySign());
        if (JDReadApplication.getInstance().getLogin()) {
            LoginHelper.getUserInfo(PersonalDataBundle.getInstance());
            LoginHelper.verifySign(PersonalDataBundle.getInstance());
            setUserInfo(LoginHelper.getImgUrl(), LoginHelper.getUserName());
        }
        PersonalModel personalModel = PersonalDataBundle.getInstance().getPersonalModel();
        if (personalAdapter != null) {
            personalAdapter.setData(personalModel.getPersonalData(), personalModel.getEvents());
        }
    }

    private void initView() {
        binding.personalRecycler.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        DividerItemDecoration decoration = new DividerItemDecoration(JDReadApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        decoration.setColor(ResManager.getColor(R.color.divider_color));
        decoration.setDrawLine(true);
        binding.personalRecycler.addItemDecoration(decoration);
        personalAdapter = new PersonalAdapter(PersonalDataBundle.getInstance().getEventBus());
        binding.personalRecycler.setAdapter(personalAdapter);
    }

    private void setUserInfo(String imageUrl, String nickName) {
        binding.setImageUrl(imageUrl);
        binding.setUserName(nickName);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserLoginEvent(UserLoginEvent event) {
        binding.setIsLogin(JDReadApplication.getInstance().getLogin());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserInfoEvent(UserInfoEvent event) {
        UserInfo userInfo = event.getUserInfo();
        setUserInfo(userInfo.yun_big_image_url, userInfo.nickname);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVerifySignEvent(VerifySignEvent event) {
        PersonalDataBundle.getInstance().setIsTodaySign(event.isTodaySign());
        binding.setIsSignToday(event.isTodaySign());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGiftCenterEvent(GiftCenterEvent event) {
        showLogin(GiftCenterFragment.class.getName());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPersonalAccountEvent(PersonalAccountEvent event) {
        showLogin(PersonalAccountFragment.class.getName());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPersonalBookEvent(PersonalBookEvent event) {
        showLogin(PersonalBookFragment.class.getName());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPersonalNoteEvent(PersonalNoteEvent event) {
        showLogin(PersonalNoteFragment.class.getName());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPersonalTaskEvent(PersonalTaskEvent event) {
        showLogin(PersonalTaskFragment.class.getName());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReadPreferenceEvent(ReadPreferenceEvent event) {
        showLogin(ReadPreferenceFragment.class.getName());
    }

    private void showLogin(String name) {
        if (!Utils.isNetworkConnected(JDReadApplication.getInstance())) {
            ToastUtil.showToast(ResManager.getString(R.string.wifi_no_connected));
            return;
        }
        if (!JDReadApplication.getInstance().getLogin()) {
            LoginHelper.showUserLoginDialog(PersonalDataBundle.getInstance().getPersonalViewModel().getUserLoginViewModel(), getActivity());
            return;
        }
        if (StringUtils.isNotBlank(name)) {
            viewEventCallBack.gotoView(name);
        }
    }

    public void setRefresh() {
        if (!binding.getIsLogin()) {
            initData();
        }
    }
}
