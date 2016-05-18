package com.onyx.android.sdk.ui.data;

import android.view.KeyEvent;

import com.onyx.android.sdk.device.DeviceInfo;
import com.onyx.android.sdk.device.EpdController;
import com.onyx.android.sdk.ui.OnyxTableView;

/**
 * Created by joy on 5/8/14.
 */
public class DefaultTableViewCallback extends OnyxTableView.TableViewCallback {
    private EpdController.UpdateMode initWaveform = EpdController.UpdateMode.GC;
    private EpdController.UpdateMode pageChangeWaveform = EpdController.UpdateMode.GC;
    private EpdController.UpdateMode dpadWaveform = EpdController.UpdateMode.DW;

    public void setInitWaveform(EpdController.UpdateMode mode) {
        initWaveform = mode;
    }

    public void setPageChangeWaveform(EpdController.UpdateMode mode) {
        pageChangeWaveform = mode;
    }

    public void setDpadWaveform(EpdController.UpdateMode mode) {
        dpadWaveform = mode;
    }

    @Override
    public void beforePageChanging(OnyxTableView tableView, int newPage, int oldPage) {
        EpdController.setViewDefaultUpdateMode(tableView, dpadWaveform);
    }

    @Override
    public void afterPageChanged(OnyxTableView tableView, int newPage, int oldPage) {
        EpdController.resetViewUpdateMode(tableView);
    }

    @Override
    public void beforeDPadMoving(OnyxTableView tableView, KeyEvent event) {
        EpdController.setViewDefaultUpdateMode(tableView, dpadWaveform);
    }

    @Override
    public void afterDPadMoved(OnyxTableView tableView, KeyEvent event) {

    }
}
