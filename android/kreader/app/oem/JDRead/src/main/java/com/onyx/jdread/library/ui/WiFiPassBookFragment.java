package com.onyx.jdread.library.ui;

import android.graphics.Bitmap;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.common.BaseFragment;
import com.onyx.jdread.databinding.FragmentWifiPassBookBinding;
import com.onyx.jdread.library.fileserver.FileServer;
import com.onyx.jdread.library.model.FileServerModel;
import com.onyx.jdread.library.utils.QRCodeUtil;

import java.io.IOException;

import static android.content.Context.WIFI_SERVICE;

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
            String serverString = "http://" + getIpAddress() + ":" + FileServer.SERVER_PORT;
            fileServerModel.serverAddress.set(serverString);
            Bitmap bitmap = QRCodeUtil.createQRImage(serverString, getResources().getInteger(R.integer.qr_code_width), getResources().getInteger(R.integer.qr_code_width));
            passBookBinding.qrImage.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        server.stop();
    }

    public String getIpAddress() {
        WifiManager wm = (WifiManager) getContext().getApplicationContext().getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        return ip;
    }
}
