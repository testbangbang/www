package com.onyx.jdread.setting.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.databinding.FragmentDictionaryBinding;
import com.onyx.jdread.library.view.DashLineItemDivider;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.setting.adapter.DictionaryListAdapter;
import com.onyx.jdread.setting.event.BackToReadingToolsEvent;
import com.onyx.jdread.setting.model.DictionaryModel;
import com.onyx.jdread.setting.model.SettingBundle;
import com.onyx.jdread.setting.request.RxDictionaryListLoadRequest;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by hehai on 18-1-16.
 */

public class DictionaryFragment extends BaseFragment {

    private DictionaryModel dictionaryModel;
    private FragmentDictionaryBinding binding;
    private DictionaryListAdapter dictionaryListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDictionaryBinding.inflate(inflater, container, false);
        dictionaryModel = new DictionaryModel();
        binding.setModel(dictionaryModel);
        binding.dictionaryTitle.setTitleModel(dictionaryModel.titleBarModel);
        initRecycler();
        loadData();
        return binding.getRoot();
    }

    private void initRecycler() {
        DashLineItemDivider dividerItemDecoration = new DashLineItemDivider();
        binding.dictionaryRecycler.addItemDecoration(dividerItemDecoration);
        dictionaryListAdapter = new DictionaryListAdapter();
        binding.dictionaryRecycler.setAdapter(dictionaryListAdapter);
    }

    private void loadData() {
        RxDictionaryListLoadRequest request = new RxDictionaryListLoadRequest(SettingBundle.getInstance().getDataManager(), dictionaryModel);
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                dictionaryListAdapter.notifyDataSetChanged();
            }
        });
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
    public void onBackToReadingToolsEvent(BackToReadingToolsEvent event) {
        viewEventCallBack.viewBack();
    }
}
