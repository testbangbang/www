package com.onyx.jdread.setting.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.FragmentTranslateBinding;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.setting.model.SettingBundle;
import com.onyx.jdread.setting.model.TranslateModel;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by hehai on 18-1-15.
 */

public class TranslateFragment extends BaseFragment {

    private FragmentTranslateBinding binding;
    private TranslateModel translateModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTranslateBinding.inflate(inflater, container, false);
        initData();
        initEvent();
        return binding.getRoot();
    }

    private void initEvent() {
        binding.translateEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean isChinese = StringUtils.isChinese(s.toString());
                translateModel.inputLanguage.set(isChinese ? getString(R.string.chinese) : getString(R.string.english));
                translateModel.targetLanguage.set(isChinese ? getString(R.string.english) : getString(R.string.chinese));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initData() {
        translateModel = new TranslateModel();
        binding.titleBar.setTitleModel(translateModel.titleBarModel);
        binding.setModel(translateModel);
        binding.translateResult.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!SettingBundle.getInstance().getEventBus().isRegistered(this)) {
            SettingBundle.getInstance().getEventBus().register(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        SettingBundle.getInstance().getEventBus().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTranslateFragment(TranslateFragment event) {
        viewEventCallBack.viewBack();
    }
}
