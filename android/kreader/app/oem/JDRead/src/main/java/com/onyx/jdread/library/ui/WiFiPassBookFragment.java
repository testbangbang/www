package com.onyx.jdread.library.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.common.BaseFragment;
import com.onyx.jdread.databinding.FragmentWifiPassBookBinding;
import com.onyx.jdread.library.fileserver.FileServer;
import com.onyx.jdread.library.model.FileServerModel;
import com.onyx.jdread.library.request.RxFileServerAddressRequest;

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
        try {
            server.start();
            loadData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        RxFileServerAddressRequest request = new RxFileServerAddressRequest(JDReadApplication.getDataBundle().getDataManager(), fileServerModel);
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
    }
}
