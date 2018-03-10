package com.onyx.jdread.main.common;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.shop.ui.NetWorkErrorFragment;
import com.onyx.jdread.util.Utils;

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
        if (getActivity() == null) {
            return;
        }
        Utils.hideSoftWindow(getActivity());
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

    public boolean isWifiDisconnected() {
        if (!Utils.isNetworkConnected(JDReadApplication.getInstance())) {
            ToastUtil.showToast(ResManager.getString(R.string.wifi_no_connected));
            return true;
        }
        return false;
    }

    public void goNetWorkErrorFragment(){
        getViewEventCallBack().gotoView(NetWorkErrorFragment.class.getName());
    }

    public void checkWifi(String title) {
        if (isWifiDisconnected()) {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.NET_ERROR_TITLE, title);
            if (StringUtils.isNullOrEmpty(title)) {
                bundle.putBoolean(Constants.NET_ERROR_SHOW_TITLE_BAR, false);
            }
            setBundle(bundle);
            goNetWorkErrorFragment();
        }
    }
}
