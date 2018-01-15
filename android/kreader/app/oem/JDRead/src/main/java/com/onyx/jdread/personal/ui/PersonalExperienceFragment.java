package com.onyx.jdread.personal.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.OnyxPageDividerItemDecoration;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.PersonalExperienceBinding;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.personal.action.GetReadOverAction;
import com.onyx.jdread.personal.action.GetReadTotalAction;
import com.onyx.jdread.personal.adapter.PersonalExperienceAdapter;
import com.onyx.jdread.personal.cloud.entity.jdbean.ReadOverInfoBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.ReadTotalInfoBean;
import com.onyx.jdread.personal.common.LoginHelper;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.setting.event.BackToSettingFragmentEvent;
import com.onyx.jdread.setting.model.SettingTitleModel;
import com.onyx.jdread.shop.view.DividerItemDecoration;
import com.onyx.jdread.util.TimeUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;

/**
 * Created by li on 2017/12/29.
 */

public class PersonalExperienceFragment extends BaseFragment {
    private PersonalExperienceBinding binding;
    private PersonalExperienceAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = (PersonalExperienceBinding) DataBindingUtil.inflate(inflater, R.layout.fragment_personal_experience, container, false);
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
        String imgUrl = LoginHelper.getImgUrl();
        String userName = LoginHelper.getUserName();
        if (StringUtils.isNotBlank(imgUrl) && StringUtils.isNotBlank(userName)) {
            binding.setImageUrl(imgUrl);
            binding.setUserName(userName);
        }
        TitleBarModel titleModel = PersonalDataBundle.getInstance().getTitleModel();
        titleModel.title.set(JDReadApplication.getInstance().getResources().getString(R.string.personal_experience));
        titleModel.backEvent.set(new BackToSettingFragmentEvent());
        binding.experienceTitleBar.setTitleModel(titleModel);

        GetReadTotalAction getReadTotalAction = new GetReadTotalAction();
        getReadTotalAction.execute(PersonalDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                ReadTotalInfoBean readTotalInfo = PersonalDataBundle.getInstance().getReadTotalInfo();
                String firstAccessTime = readTotalInfo.getFirstAccessTime();
                String currentTime = readTotalInfo.getCurrentTime();
                int totalCount = readTotalInfo.getTotalCount();
                int days = 0;
                if (StringUtils.isNotBlank(firstAccessTime) && StringUtils.isNotBlank(currentTime)) {
                    Date firstData = TimeUtils.parseDateDefault(firstAccessTime);
                    Date currentData = TimeUtils.parseDateDefault(currentTime);
                    days = TimeUtils.daysBetweenDefault(firstData, currentData);
                }
                binding.setStayTime(days + "");
                binding.setTotalCount(totalCount + "");
            }
        });

        GetReadOverAction getReadOverAction = new GetReadOverAction();
        getReadOverAction.execute(PersonalDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                ReadOverInfoBean readOverInfo = PersonalDataBundle.getInstance().getReadOverInfo();
                int read_books_count = readOverInfo.getRead_books_count();
                int notes_count = readOverInfo.getNotes_count();
                binding.setReadOver(read_books_count + "");
                binding.setNotes(notes_count + "");
            }
        });
    }

    private void initView() {
        binding.experienceRecycler.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        DividerItemDecoration decoration = new DividerItemDecoration(JDReadApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        binding.experienceRecycler.addItemDecoration(decoration);
        adapter = new PersonalExperienceAdapter();
        binding.experienceRecycler.setAdapter(adapter);
    }

    private void initListener() {
        binding.experienceNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.experienceRecycler.nextPage();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackToSettingFragmentEvent(BackToSettingFragmentEvent event) {
        viewEventCallBack.viewBack();
    }
}
