package com.onyx.jdread.common;


import android.support.v4.app.Fragment;

/**
 * Created by huxiaomao on 2017/12/7.
 */

public class BaseFragment extends Fragment {
    private ChildViewEventCallBack viewEventCallBack = null;

    public interface ChildViewEventCallBack {
        void gotoView(String childClassName);

        void viewBack();

        void hideOrShowSystemBar(boolean flags);

        void hideOrShowFunctionBar(boolean flags);
    }

    public void setViewEventCallBack(ChildViewEventCallBack viewEventCallBack) {
        this.viewEventCallBack = viewEventCallBack;
    }

    public void hideWindow() {

    }

    protected ChildViewEventCallBack getViewEventCallBack() {
        return viewEventCallBack;
    }
}
