package com.onyx.jdread.library.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.jdread.R;
import com.onyx.jdread.common.BaseFragment;

/**
 * Created by huxiaomao on 2017/12/7.
 */

public class LibraryFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.system_bar,null);
        return view;
    }
}
