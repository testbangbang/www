package com.onyx.android.dr.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.dr.R;
import com.onyx.android.dr.event.BackToMainViewEvent;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by hehai on 17-6-29.
 */

public class SchoolBasedMaterialsFragment extends BaseFragment {
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.menu_back)
    LinearLayout menuBack;
    @Bind(R.id.title_bar_title)
    TextView titleBarTitle;
    @Bind(R.id.title_bar_right_menu)
    TextView titleBarRightMenu;
    @Bind(R.id.pre_button)
    ImageView preButton;
    @Bind(R.id.next_button)
    ImageView nextButton;
    @Bind(R.id.page_recycler)
    PageRecyclerView pageRecycler;

    @Override
    protected void initListener() {

    }

    @Override
    protected void initView(View rootView) {
        titleBarTitle.setText(getString(R.string.menu_school_based_materials));
        image.setImageResource(R.drawable.ic_professional_materials);
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected int getRootView() {
        return R.layout.fragment_school_based_materials;
    }

    @Override
    public boolean onKeyBack() {
        return false;
    }

    @OnClick({R.id.menu_back, R.id.title_bar_title, R.id.pre_button, R.id.next_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu_back:
                EventBus.getDefault().post(new BackToMainViewEvent());
                break;
            case R.id.title_bar_title:
                break;
            case R.id.pre_button:
                break;
            case R.id.next_button:
                break;
        }
    }
}
