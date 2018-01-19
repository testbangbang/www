package com.onyx.jdread.main.common;


import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by huxiaomao on 2017/12/7.
 */

public class BaseFragment extends Fragment {
    private Bundle bundle;
    protected ChildViewEventCallBack viewEventCallBack = null;
    private LoadingDialog loadingDialog;
    private LoadingDialog.DialogModel loadingModel;

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

    public void showLoadingDialog(String loadingText) {
        if (loadingModel == null) {
            loadingModel = new LoadingDialog.DialogModel();
        }
        loadingModel.setLoadingText(loadingText);
        if (loadingDialog == null) {
            LoadingDialog.Builder builder = new LoadingDialog.Builder(getContext().getApplicationContext(), loadingModel);
            loadingDialog = builder.create();
        }
        loadingDialog.show();
    }

    public void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public Bundle getBundle() {
        return bundle;
    }
}
