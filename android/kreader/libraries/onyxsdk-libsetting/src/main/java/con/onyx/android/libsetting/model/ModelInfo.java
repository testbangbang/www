package con.onyx.android.libsetting.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import con.onyx.android.libsetting.BR;

/**
 * Created by solskjaer49 on 2016/11/24 15:16.
 */

public class ModelInfo extends BaseObservable {
    @Bindable
    public String getDeviceName() {
        return deviceName;
    }

    public ModelInfo setDeviceName(String deviceName) {
        this.deviceName = deviceName;
        notifyPropertyChanged(BR.deviceName);
        return this;
    }

    @Bindable
    public String getDeviceSerial() {
        return deviceSerial;
    }

    public ModelInfo setDeviceSerial(String deviceSerial) {
        this.deviceSerial = deviceSerial;
        notifyPropertyChanged(BR.deviceSerial);
        return this;
    }

    @Bindable
    public String getDeviceVersion() {
        return deviceVersion;
    }

    public ModelInfo setDeviceVersion(String deviceVersion) {
        this.deviceVersion = deviceVersion;
        notifyPropertyChanged(BR.deviceVersion);
        return this;
    }

    @Bindable
    public int getDeviceLogoResId() {
        return deviceLogoResId;
    }

    public ModelInfo setDeviceLogoResId(int deviceLogoResId) {
        this.deviceLogoResId = deviceLogoResId;
        notifyPropertyChanged(BR.deviceLogoResId);
        return this;
    }

    private String deviceVersion;
    private String deviceName;
    private String deviceSerial;
    private int deviceLogoResId;

    public ModelInfo(String deviceName, String deviceSerial, String deviceVersion, int logoID) {
        this.deviceName = deviceName;
        this.deviceSerial = deviceSerial;
        this.deviceVersion = deviceVersion;
        this.deviceLogoResId = logoID;
    }

}
