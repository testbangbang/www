package com.onyx.jdread.library.request;

import android.graphics.Bitmap;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.rxrequest.data.fs.RxBaseFSRequest;
import com.onyx.jdread.R;
import com.onyx.jdread.library.fileserver.FileServer;
import com.onyx.jdread.library.model.FileServerModel;
import com.onyx.jdread.library.utils.QRCodeUtil;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by hehai on 17-12-28.
 */

public class RxFileServerAddressRequest extends RxBaseFSRequest {
    private FileServerModel fileServerModel;

    public RxFileServerAddressRequest(DataManager dm, FileServerModel fileServerModel) {
        super(dm);
        this.fileServerModel = fileServerModel;
    }

    @Override
    public RxFileServerAddressRequest call() throws Exception {
        String serverString = "http://" + getIpAddress() + ":" + FileServer.SERVER_PORT;
        fileServerModel.serverAddress.set(serverString);
        Bitmap bitmap = QRCodeUtil.createQRImage(serverString, getAppContext().getResources().getInteger(R.integer.qr_code_width), getAppContext().getResources().getInteger(R.integer.qr_code_width));
        fileServerModel.bitmap.set(bitmap);
        return this;
    }

    public String getIpAddress() {
        WifiManager wm = (WifiManager) getAppContext().getApplicationContext().getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        return ip;
    }
}
