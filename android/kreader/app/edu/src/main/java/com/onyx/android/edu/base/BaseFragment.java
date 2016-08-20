package com.onyx.android.edu.base;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.edu.utils.ToastUtils;

/**
 * Created by ming on 16/6/24.
 */
public abstract class BaseFragment extends Fragment{

    protected abstract int getLayoutId();

    protected View root;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(getLayoutId(),container,false);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void showToast(String message){
        ToastUtils.showToast(getActivity(),message);
    }
}
