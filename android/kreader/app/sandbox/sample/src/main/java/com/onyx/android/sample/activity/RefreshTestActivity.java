package com.onyx.android.sample.activity;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.onyx.android.sample.R;
import com.onyx.android.sample.events.StopTestEvent;
import com.onyx.android.sample.fragment.BaseTestFragment;
import com.onyx.android.sample.fragment.FastUpdateModeFragment;
import com.onyx.android.sample.fragment.OverlayUpdateFragment;
import com.onyx.android.sample.fragment.ParallelUpdateFragment;
import com.onyx.android.sample.fragment.RectangleUpdateFragment;
import com.onyx.android.sample.fragment.TextSelectionFragment;
import com.onyx.android.sample.fragment.TextUpdateFragment;
import com.onyx.android.sdk.common.request.WakeLockHolder;
import com.onyx.android.sdk.utils.TestUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by wangxu on 17-9-28.
 */

public class RefreshTestActivity extends AppCompatActivity {

    private final static String TAG = RefreshTestActivity.class.getSimpleName();

    private List<BaseTestFragment> fragments = new ArrayList<>();
    private WakeLockHolder wakeLockHolder = new WakeLockHolder();
    private int currentIndex = 0;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            statTest();
            startNextTest();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refresh_test);
        ButterKnife.bind(this);

        acquireWakelock();

        fragments.add(new RectangleUpdateFragment());
        fragments.add(new OverlayUpdateFragment());
        fragments.add(new ParallelUpdateFragment());
        fragments.add(new TextUpdateFragment());
        fragments.add(new TextSelectionFragment());
        fragments.add(new FastUpdateModeFragment());
    }

    @Override
    protected void onResume() {
        super.onResume();
        statTest();
        startNextTest();
    }

    @Override
    public void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
    }

    private void startNextTest() {
        long delay = getDelay();
        handler.postDelayed(runnable, delay);
    }

    private long getDelay() {
        return TestUtils.randInt(5, 20) * 60 * 1000;
    }

    private void statTest() {
        stopTest();
        TestUtils.sleep(5 * 1000);
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        final BaseTestFragment fragment = fragments.get(gitFragmentIndex());
        transaction.replace(R.id.content, fragment);
        transaction.commit();
        Log.i(TAG, "start the " + fragment.toString());
        currentIndex ++;
    }

    private void stopTest() {
        EventBus.getDefault().post(new StopTestEvent());
    }

    private int gitFragmentIndex() {
        if (currentIndex > fragments.size() - 1) {
            currentIndex = 0;
        }
        return currentIndex;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        releaseWakelock();
    }

    private void acquireWakelock() {
        wakeLockHolder.acquireWakeLock(this, WakeLockHolder.WAKEUP_FLAGS | WakeLockHolder.ON_AFTER_RELEASE, TAG);
    }

    private void releaseWakelock() {
        wakeLockHolder.releaseWakeLock();
    }
}
