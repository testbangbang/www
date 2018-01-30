package com.onyx.jdread.personal.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.PointsForBinding;
import com.onyx.jdread.library.view.DashLineItemDivider;
import com.onyx.jdread.library.view.LibraryDeleteDialog;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.personal.adapter.PointsForAdapter;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.model.PointsForData;
import com.onyx.jdread.personal.model.PointsForModel;
import com.onyx.jdread.setting.event.BackToSettingFragmentEvent;
import com.onyx.jdread.util.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by li on 2018/1/2.
 */

public class PointsForFragment extends BaseFragment {
    private PointsForBinding binding;
    private PointsForAdapter pointsForAdapter;
    private PointsForModel pointsForModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = (PointsForBinding) DataBindingUtil.inflate(inflater, R.layout.fragment_points_for, container, false);
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
        titleModel.title.set(JDReadApplication.getInstance().getResources().getString(R.string.points_for));
        titleModel.backEvent.set(new BackToSettingFragmentEvent());
        binding.pointsForTitleBar.setTitleModel(titleModel);

        pointsForModel = PersonalDataBundle.getInstance().getPointsForModel();
        if (pointsForAdapter != null) {
            pointsForAdapter.setData(pointsForModel.getList());
        }
    }

    private void initView() {
        binding.pointsForRecycler.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        DashLineItemDivider decoration = new DashLineItemDivider();
        binding.pointsForRecycler.addItemDecoration(decoration);
        pointsForAdapter = new PointsForAdapter();
        binding.pointsForRecycler.setAdapter(pointsForAdapter);
    }

    private void initListener() {
        if (pointsForAdapter != null) {
            pointsForAdapter.setOnItemClickListener(new PageRecyclerView.PageAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    PointsForData pointsForData = pointsForModel.getList().get(position);
                    LibraryDeleteDialog.DialogModel dialogModel = new LibraryDeleteDialog.DialogModel();
                    final LibraryDeleteDialog dialog = new LibraryDeleteDialog.Builder(JDReadApplication.getInstance(), dialogModel).create();
                    String tips = JDReadApplication.getInstance().getResources().getString(R.string.points_for_tips);
                    dialogModel.message.set(String.format(tips, pointsForData.getDays(), pointsForData.getPoints()));
                    dialogModel.setNegativeClickLister(new LibraryDeleteDialog.DialogModel.OnClickListener() {
                        @Override
                        public void onClicked() {
                            dialog.dismiss();
                        }
                    });
                    dialogModel.setPositiveClickLister(new LibraryDeleteDialog.DialogModel.OnClickListener() {
                        @Override
                        public void onClicked() {
                            // TODO: 2018/1/3
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackToSettingFragmentEvent(BackToSettingFragmentEvent event) {
        viewEventCallBack.viewBack();
    }
}
