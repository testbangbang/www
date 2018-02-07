package com.onyx.jdread.library.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.library.event.WifiPassBookErrorEvent;
import com.onyx.jdread.library.model.LibraryDataBundle;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.databinding.FragmentWifiPassBookBinding;
import com.onyx.jdread.library.event.BackToLibraryFragmentEvent;
import com.onyx.jdread.library.fileserver.FileServer;
import com.onyx.jdread.library.model.FileServerModel;
import com.onyx.jdread.library.request.RxFileServerAddressRequest;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.common.ToastUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

/**
 * Created by hehai on 17-12-27.
 */

public class WiFiPassBookFragment extends BaseFragment {

    private FileServerModel fileServerModel;
    private FileServer server;
    private FragmentWifiPassBookBinding passBookBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        passBookBinding = FragmentWifiPassBookBinding.inflate(inflater, container, false);
        try {
            server = new FileServer(JDReadApplication.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileServerModel = new FileServerModel();
        passBookBinding.wifiPassTitleBar.setTitleModel(fileServerModel.titleBarModel);
        passBookBinding.setFileServerModel(fileServerModel);
        return passBookBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        LibraryDataBundle.getInstance().getEventBus().register(this);
        try {
            server.start();
            loadData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        RxFileServerAddressRequest request = new RxFileServerAddressRequest(LibraryDataBundle.getInstance().getDataManager(), fileServerModel);
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                passBookBinding.qrImage.setImageBitmap(fileServerModel.bitmap.get());
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        server.stop();
        LibraryDataBundle.getInstance().getEventBus().unregister(this);
        ToastUtil.showOffsetToast(ResManager.getString(R.string.quit_wifi_pass_book), ResManager.getInteger(R.integer.pass_book_toast_offset_y));
    }

    @Subscribe
    public void onBackToLibraryFragment(BackToLibraryFragmentEvent event) {
        viewEventCallBack.gotoView(LibraryFragment.class.getName());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWifiPassBookErrorEvent(WifiPassBookErrorEvent event) {
        ToastUtil.showOffsetToast(event.getMessage(), ResManager.getInteger(R.integer.pass_book_toast_offset_y));
    }
}
