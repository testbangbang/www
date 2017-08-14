package com.onyx.android.dr.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;

/**
 * Created by hehai on 2016/12/1.
 */
public abstract class BaseFragment extends Fragment {
    private static final String STATUS_SAVE_IS_HIDDEN = "status_save_is_hidden";
    View rootView;
    public boolean isStored;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            boolean isHidden = savedInstanceState.getBoolean(STATUS_SAVE_IS_HIDDEN);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            if (isHidden) {
                transaction.hide(this);
            } else {
                transaction.show(this);
            }
            transaction.commit();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int viewID = getRootView();
        rootView = inflater.inflate(viewID, container, false);
        ButterKnife.bind(this, rootView);
        initView(rootView);
        loadData();
        initListener();
        return rootView;
    }

    protected abstract void initListener();

    protected abstract void initView(View rootView);

    protected abstract void loadData();

    protected abstract int getRootView();

    public abstract boolean onKeyBack();

    public boolean onKeyPageUp() {
        return false;
    }

    public boolean onKeyPageDown() {
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(STATUS_SAVE_IS_HIDDEN, true);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getSimpleName());
        MobclickAgent.onResume(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getSimpleName());
        MobclickAgent.onPause(getActivity());
    }
}
