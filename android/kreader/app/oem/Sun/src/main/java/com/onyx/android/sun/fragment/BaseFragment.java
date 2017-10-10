package com.onyx.android.sun.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;


/**
 * Created by hehai on 2016/12/1.
 */
public abstract class BaseFragment extends Fragment {
    private static final String STATUS_SAVE_IS_HIDDEN = "status_save_is_hidden";
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
        ViewDataBinding binding = DataBindingUtil.inflate(inflater, viewID, container, false);
        View view = binding.getRoot();
        initView(binding);
        loadData();
        initListener();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    protected abstract void loadData();

    protected abstract void initView(ViewDataBinding binding);

    protected abstract void initListener();

    protected abstract int getRootView();

    public abstract boolean onKeyBack();

    public boolean onKeyPageUp() {
        return false;
    }

    public boolean onKeyPageDown() {
        return false;
    }

    public void stopEvent() {
    }

    public void initData(Object object) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(STATUS_SAVE_IS_HIDDEN, true);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
