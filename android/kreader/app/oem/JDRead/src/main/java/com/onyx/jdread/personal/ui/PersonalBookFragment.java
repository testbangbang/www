package com.onyx.jdread.personal.ui;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.OnyxPageDividerItemDecoration;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.PersonalBookBinding;
import com.onyx.jdread.library.model.PopMenuModel;
import com.onyx.jdread.library.view.MenuPopupWindow;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.personal.adapter.PersonalBookAdapter;
import com.onyx.jdread.personal.event.FilterAllEvent;
import com.onyx.jdread.personal.event.FilterHaveBoughtEvent;
import com.onyx.jdread.personal.event.FilterReadVipEvent;
import com.onyx.jdread.personal.event.FilterSelfImportEvent;
import com.onyx.jdread.personal.model.PersonalBookModel;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.setting.event.BackToSettingFragmentEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by li on 2018/1/4.
 */

public class PersonalBookFragment extends BaseFragment {
    private PersonalBookBinding binding;
    private PersonalBookAdapter personalBookAdapter;
    private PersonalBookModel personalBookModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = (PersonalBookBinding) DataBindingUtil.inflate(inflater, R.layout.fragment_personal_book, container, false);
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

    private void initView() {
        binding.personalBookRecycler.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        OnyxPageDividerItemDecoration decoration = new OnyxPageDividerItemDecoration(JDReadApplication.getInstance(), OnyxPageDividerItemDecoration.VERTICAL);
        binding.personalBookRecycler.addItemDecoration(decoration);
        personalBookAdapter = new PersonalBookAdapter();
        binding.personalBookRecycler.setAdapter(personalBookAdapter);
    }

    private void initData() {
        TitleBarModel titleModel = PersonalDataBundle.getInstance().getTitleModel();
        titleModel.title.set(JDReadApplication.getInstance().getResources().getString(R.string.personal_books));
        titleModel.backEvent.set(new BackToSettingFragmentEvent());
        binding.personalBookTitle.setTitleModel(titleModel);
        personalBookModel = PersonalDataBundle.getInstance().getPersonalBookModel();
        setFilter(R.string.the_default);
    }

    private void initListener() {
        binding.personalBookFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuPopupWindow popupWindow = new MenuPopupWindow(getActivity(), PersonalDataBundle.getInstance().getEventBus());
                List<PopMenuModel> menus = personalBookModel.getMenus();
                if (menus != null && menus.size() > 0) {
                    popupWindow.showPopupWindow(binding.personalBookFilter, menus, JDReadApplication
                                    .getInstance().getResources().getInteger(R.integer.personal_book_filter_x),
                            JDReadApplication.getInstance().getResources().getInteger(R.integer.personal_book_filter_y));
                }

            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackToSettingFragmentEvent(BackToSettingFragmentEvent event) {
        viewEventCallBack.viewBack();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFilterAllEvent(FilterAllEvent event) {
        setFilter(event.getResId());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFilterHaveBoughtEvent(FilterHaveBoughtEvent event) {
        setFilter(event.getResId());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFilterReadVipEvent(FilterReadVipEvent event) {
        setFilter(event.getResId());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFilterSelfImportEvent(FilterSelfImportEvent event) {
        setFilter(event.getResId());
    }

    private void setFilter(int id) {
        switch (id) {
            case R.string.the_default:
                binding.setFilterName(JDReadApplication.getInstance().getResources().getString(R.string.all));
                break;
            case R.string.have_bought:
                binding.setFilterName(JDReadApplication.getInstance().getResources().getString(R.string.have_bought));
                break;
            case R.string.read_vip:
                binding.setFilterName(JDReadApplication.getInstance().getResources().getString(R.string.read_vip));
                break;
            case R.string.self_import:
                binding.setFilterName(JDReadApplication.getInstance().getResources().getString(R.string.self_import));
                break;
        }
    }
}
