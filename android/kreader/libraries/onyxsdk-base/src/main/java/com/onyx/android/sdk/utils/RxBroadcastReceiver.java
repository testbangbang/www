package com.onyx.android.sdk.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.onyx.android.sdk.device.EnvironmentUtil;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

/**
 * Created by lxm on 2017/11/18.
 */

public final class RxBroadcastReceiver {

    public static class IntentWithContext {
        private Context context;
        private Intent intent;

        public IntentWithContext(Context context, Intent intent) {
            this.context = context;
            this.intent = intent;
        }

        public Context getContext() {
            return context;
        }

        public Intent getIntent() {
            return intent;
        }
    }

    public static Observable<IntentWithContext> fromBroadcast(final Context context, final IntentFilter filter) {

        return Observable.create(new ObservableOnSubscribe<IntentWithContext>() {
            @Override
            public void subscribe(final ObservableEmitter<IntentWithContext> emitter) throws Exception {
                final BroadcastReceiver receiver = new BroadcastReceiver() {

                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Log.e("", "onReceive: =============================");
                        emitter.onNext(new IntentWithContext(context, intent));
                    }

                };
                context.registerReceiver(receiver, filter);
                emitter.setDisposable(new Disposable() {
                    @Override
                    public void dispose() {
                        context.unregisterReceiver(receiver);
                    }

                    @Override
                    public boolean isDisposed() {
                        return false;
                    }
                });
            }
        });
    }

    public static Observable<Boolean> connectivityState(final Context context) {
        return RxBroadcastReceiver
                .fromBroadcast(context, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
                .map(new Function<IntentWithContext, Boolean>() {

                    @Override
                    public Boolean apply(IntentWithContext intentWithContext) throws Exception {
                        NetworkInfo info = intentWithContext
                                .getIntent()
                                .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                        if (info != null) {
                            //If the current network connection is successful and the network connection is available
                            if (NetworkInfo.State.CONNECTED == info.getState() && info.isAvailable()) {
                                if (info.getType() == ConnectivityManager.TYPE_WIFI
                                        || info.getType() == ConnectivityManager.TYPE_MOBILE) {
                                    return true;
                                }
                            }
                        }
                        return false;
                    }
                });
    }

    public static Observable<Boolean> mediaMount(final Context context) {
        return RxBroadcastReceiver
                .fromBroadcast(context, new IntentFilter(Intent.ACTION_BOOT_COMPLETED))
                .map(new Function<IntentWithContext, Boolean>() {

                    @Override
                    public Boolean apply(IntentWithContext intentWithContext) throws Exception {
                        Intent intent = intentWithContext.getIntent();
                        Log.e("", "apply: ==============" + intent.getAction());
                        if (EnvironmentUtil.isRemovableSDDirectory(intentWithContext.getContext(), intent)) {
                            return true;
                        }
                        return false;
                    }
                });
    }

}