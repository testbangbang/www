package com.onyx.android.sample.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sample.view.OverlaySurfaceView;

/**
 * Created by wangxu on 17-8-3.
 */

public class OverlayUpdateFragment extends BaseTestFragment {

    private OverlaySurfaceView overlaySurfaceView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        overlaySurfaceView = new OverlaySurfaceView(getActivity());
        return overlaySurfaceView;
    }

    @Override
    public void stopTest() {
        super.stopTest();
        overlaySurfaceView.stop();
    }
}
