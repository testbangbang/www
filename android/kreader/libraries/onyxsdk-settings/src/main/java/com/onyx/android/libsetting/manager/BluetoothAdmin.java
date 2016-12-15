package com.onyx.android.libsetting.manager;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by solskjaer49 on 2016/12/14 16:17.
 */

public class BluetoothAdmin {
    private BluetoothAdapter adapter;
    private IntentFilter bluetoothStateFilter;
    private BroadcastReceiver bluetoothStateReceiver;
    private Callback callback;
    private Context context;

    public interface Callback {
        void onBluetoothStateChange(boolean isBluetoothEnable);
    }

    public BluetoothAdmin(Context context, Callback callback) {
        this.context = context;
        adapter = BluetoothAdapter.getDefaultAdapter();
        initBluetoothStateFilterAndReceiver();
        this.callback = callback;
    }

    public boolean isBluetoothAvailable() {
        return adapter == null;
    }

    public boolean isEnabled() {
        return adapter != null && adapter.isEnabled();
    }

    private void initBluetoothStateFilterAndReceiver() {
        bluetoothStateFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        bluetoothStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case BluetoothAdapter.ACTION_STATE_CHANGED:
                        callback.onBluetoothStateChange(adapter.isEnabled());
                        break;
                }
            }
        };
    }

    public void setEnabled(boolean enabled) {
        if (enabled) {
            adapter.enable();
        } else {
            adapter.disable();
        }
    }

    public void toggleBluetoothEnabled() {
        setEnabled(!adapter.isEnabled());
    }

    public BluetoothAdmin setCallback(Callback callback) {
        this.callback = callback;
        return this;
    }

    public boolean registerReceiver() {
        if (context == null) {
            return false;
        }
        context.registerReceiver(bluetoothStateReceiver, bluetoothStateFilter);
        return true;
    }

    public boolean unregisterReceiver() {
        if (context == null) {
            return false;
        }
        context.unregisterReceiver(bluetoothStateReceiver);
        return true;
    }
}