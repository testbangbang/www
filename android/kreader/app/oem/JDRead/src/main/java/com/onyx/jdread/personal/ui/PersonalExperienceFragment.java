package com.onyx.jdread.personal.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.PersonalExperienceBinding;
import com.onyx.jdread.library.view.DashLineItemDivider;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.personal.action.RecommendUserAction;
import com.onyx.jdread.personal.adapter.PersonalExperienceAdapter;
import com.onyx.jdread.personal.cloud.entity.jdbean.RecommendItemBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.UserInfo;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.setting.event.BackToSettingFragmentEvent;
import com.onyx.jdread.shop.ui.BookDetailFragment;
import com.onyx.jdread.util.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

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
        Utils.ensureRegister(PersonalDataBundle.getInstance().getEventBus(), this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Utils.ensureUnregister(PersonalDataBundle.getInstance().getEventBus(), this);
    }

    private void initData() {
        TitleBarModel titleModel = PersonalDataBundle.getInstance().getTitleModel();
        titleModel.title.set(JDReadApplication.getInstance().getResources().getString(R.string.personal_experience));
        titleModel.backEvent.set(new BackToSettingFragmentEvent());
        binding.experienceTitleBar.setTitleModel(titleModel);

        UserInfo userInfo = PersonalDataBundle.getInstance().getUserInfo();
        if (userInfo != null) {
            binding.setUserInfo(userInfo);
        }

        final RecommendUserAction recommendUserAction = new RecommendUserAction();
        recommendUserAction.execute(PersonalDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                List<RecommendItemBean> recommendItems = recommendUserAction.getRecommendItems();
                if (recommendItems != null && recommendItems.size() > 0) {
                    adapter.setData(recommendItems);
                }
            }
        });
    }

    private void initView() {
        binding.experienceRecycler.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        DashLineItemDivider decoration = new DashLineItemDivider();
        binding.experienceRecycler.addItemDecoration(decoration);
        adapter = new PersonalExperienceAdapter();
        binding.experienceRecycler.setAdapter(adapter);
        binding.experienceRecycler.setCanTouchPageTurning(false);
        binding.experienceRecycler.setPageTurningCycled(true);
    }

    private void initListener() {
        binding.experienceNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.experienceRecycler.nextPage();
            }
        });

        adapter.setOnItemClickListener(new PageRecyclerView.PageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                List<RecommendItemBean> data = adapter.getData();
                if (data != null && CollectionUtils.getSize(data) > position) {
                    RecommendItemBean recommendItemBean = data.get(position);
                    if (recommendItemBean != null) {
                        Bundle bundle = new Bundle();
                        bundle.putLong(Constants.SP_KEY_BOOK_ID, recommendItemBean.ebook_id);
                        getViewEventCallBack().gotoView(BookDetailFragment.class.getName(), bundle);
                    }
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackToSettingFragmentEvent(BackToSettingFragmentEvent event) {
        viewEventCallBack.viewBack();
    }
}
