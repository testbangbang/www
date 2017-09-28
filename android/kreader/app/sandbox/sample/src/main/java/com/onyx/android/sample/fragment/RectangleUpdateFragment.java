package com.onyx.android.sample.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sample.view.RectangleSurfaceView;

/**
 * Created by wangxu on 17-8-8.
 */

public class RectangleUpdateFragment extends BaseTestFragment {

    private RectangleSurfaceView surfaceView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        surfaceView = new RectangleSurfaceView(getActivity());
        return surfaceView;
    }

    @Override
    public void stopTest() {
        super.stopTest();
        surfaceView.stop();
    }
}
