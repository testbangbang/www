package com.onyx.android.eschool.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onyx.android.eschool.R;
import com.onyx.android.eschool.events.AccountAvailableEvent;
import com.onyx.android.eschool.events.TabSwitchEvent;
import com.onyx.android.eschool.model.StudentAccount;
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

    @Bind(R.id.student_name)
    TextView studentName;
    @Bind(R.id.student_grade)
    TextView studentGrade;
    @Bind(R.id.student_phone_number)
    TextView studentPhone;

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
        updateView(getActivity());
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

    @SuppressLint("SetTextI18n")
    public void updateView(Context context) {
        StudentAccount studentAccount = StudentAccount.loadAccount(context);
        studentName.setText(studentAccount.getName());
        studentGrade.setText("年级：" + studentAccount.getFirstGroup());
        studentPhone.setText("电话：" + studentAccount.getPhone());
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
        updateView(getContext());
    }
}
