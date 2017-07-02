package com.onyx.android.sdk.qrcode;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.onyx.android.sdk.qrcode.camera.CameraManager;
import com.onyx.android.sdk.qrcode.decode.CaptureActivityHandler;
import com.onyx.android.sdk.qrcode.decode.DecodeImageCallback;
import com.onyx.android.sdk.qrcode.decode.DecodeImageThread;
import com.onyx.android.sdk.qrcode.decode.DecodeManager;
import com.onyx.android.sdk.qrcode.decode.InactivityTimer;
import com.onyx.android.sdk.qrcode.event.QrCodeEvent;
import com.onyx.android.sdk.qrcode.view.QrCodeFinderView;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class QrCodeActivity extends AppCompatActivity implements Callback, OnClickListener,
        EasyPermissions.PermissionCallbacks {
    private static final String REQUEST_QR_CODE_EXTRA_KEY = "qrCode";
    private static final int REQUEST_SYSTEM_PICTURE = 0;
    private static final int REQUEST_PICTURE = 1;

    private static final int NOTHING_PERMISSIONS_REQUEST = -1000;
    private static final int CAMERA_PERMS_REQUEST_CODE = 1011;
    private static final String[] CAMERA_PERMS = new String[]{
            Manifest.permission.CAMERA};

    public static final int MSG_DECODE_SUCCEED = 1;
    public static final int MSG_DECODE_FAIL = 2;
    public static final String EXTRA_USE_CUSTOM_BEEP = "extra_use_custom_beep";
    public static final String EXTRA_CONTINUOUSLY_SCAN = "extra_continuously_scan";
    public static final String EXTRA_CONTINUOUSLY_SCAN_INTERVAL = "extra_continuously_scan_interval";

    private CaptureActivityHandler mCaptureActivityHandler;
    private boolean mHasSurface;
    private boolean permissionGranted;
    private InactivityTimer mInactivityTimer;
    private QrCodeFinderView mQrCodeFinderView;
    private SurfaceView mSurfaceView;
    private View mLlFlashLight;
    private final DecodeManager mDecodeManager = new DecodeManager();

    private static final float BEEP_VOLUME = 0.10f;
    private static final long VIBRATE_DURATION = 200L;
    private MediaPlayer mMediaPlayer;
    private boolean useCustomBeep;
    private boolean mPlayBeep;
    private boolean mVibrate;
    private boolean mNeedFlashLightOpen = true;
    private ImageView mIvFlashLight;
    private TextView mTvFlashLightText;
    private Executor mQrCodeExecutor;
    private Handler mHandler;

    private static final int DEFAULT_SCAN_INTERVAL = 3000;
    private boolean continuouslyScan = true;
    private int continuouslyScanInterval = DEFAULT_SCAN_INTERVAL;
    private Handler continueHandler;

    public static Intent createIntent(Context context, boolean continuouslyScan, int continuouslyScanInterval) {
        Intent intent = new Intent(context, QrCodeActivity.class);
        intent.putExtra(EXTRA_CONTINUOUSLY_SCAN, continuouslyScan);
        intent.putExtra(EXTRA_CONTINUOUSLY_SCAN_INTERVAL, continuouslyScanInterval);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyKitKatTranslucency();
        setContentView(R.layout.activity_qr_code);
        initView();
        initData();
    }

    private void applyKitKatTranslucency() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager mTintManager = new SystemBarTintManager(this);
            mTintManager.setStatusBarTintEnabled(true);
            mTintManager.setStatusBarTintResource(R.color.colorPrimary);
        }
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    private void checkPermission() {
        if (!checkCameraHardWare(this)) {
            Toast.makeText(getApplicationContext(), R.string.qr_code_camera_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        requestPermission();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView tvBack = (TextView) findViewById(R.id.toolbar_back);
        TextView tvPic = (TextView) findViewById(R.id.qr_code_header_black_pic);
        mIvFlashLight = (ImageView) findViewById(R.id.qr_code_iv_flash_light);
        mTvFlashLightText = (TextView) findViewById(R.id.qr_code_tv_flash_light);
        mQrCodeFinderView = (QrCodeFinderView) findViewById(R.id.qr_code_view_finder);
        mSurfaceView = (SurfaceView) findViewById(R.id.qr_code_preview_view);
        mLlFlashLight = findViewById(R.id.qr_code_ll_flash_light);
        mHasSurface = false;
        mIvFlashLight.setOnClickListener(this);
        tvPic.setOnClickListener(this);
        tvBack.setOnClickListener(this);
        if (!hasCameraPermission()) {
            //the surfaceCreated method may not be called during the request permission period
            mSurfaceView.getHolder().addCallback(this);
        }
    }

    private void initData() {
        CameraManager.init(this);
        mInactivityTimer = new InactivityTimer(QrCodeActivity.this);
        mQrCodeExecutor = Executors.newSingleThreadExecutor();
        mHandler = new WeakHandler(this);

        continuouslyScan = getIntent().getBooleanExtra(EXTRA_CONTINUOUSLY_SCAN, false);
        continuouslyScanInterval = getIntent().getIntExtra(EXTRA_CONTINUOUSLY_SCAN_INTERVAL, DEFAULT_SCAN_INTERVAL);
        useCustomBeep = getIntent().getBooleanExtra(EXTRA_USE_CUSTOM_BEEP, false);
    }

    private boolean hasCameraPermission() {
        PackageManager pm = getPackageManager();
        return PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission(Manifest.permission.CAMERA, getPackageName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermission();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCaptureActivityHandler != null) {
            mCaptureActivityHandler.quitSynchronously();
            mCaptureActivityHandler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        if (null != mInactivityTimer) {
            mInactivityTimer.shutdown();
        }
        super.onDestroy();
    }

    /**
     * Handler scan result
     *
     * @param result
     */
    public void handleDecode(Result result) {
        mInactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        if (null == result) {
            mDecodeManager.showCouldNotReadQrCodeFromScanner(this, new DecodeManager.OnRefreshCameraListener() {
                @Override
                public void refresh() {
                    restartPreview();
                }
            });
        } else {
            String resultString = result.getText();
            handleResult(resultString);
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), getString(R.string.qr_code_camera_not_found), Toast.LENGTH_SHORT).show();
            finish();
            return;
        } catch (RuntimeException re) {
            re.printStackTrace();
            mDecodeManager.showPermissionDeniedDialog(this);
            return;
        }
        mQrCodeFinderView.setVisibility(View.VISIBLE);
        mSurfaceView.setVisibility(View.VISIBLE);
        mLlFlashLight.setVisibility(View.VISIBLE);
        findViewById(R.id.qr_code_view_background).setVisibility(View.GONE);
        if (mCaptureActivityHandler == null) {
            mCaptureActivityHandler = new CaptureActivityHandler(this);
        }
    }

    private void restartPreview() {
        if (null != mCaptureActivityHandler) {
            mCaptureActivityHandler.restartPreviewAndDecode();
        }
    }

    private void stopPreviewAndDecode() {
        if (null != mCaptureActivityHandler) {
            mCaptureActivityHandler.stopPreviewAndDecode();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    private boolean checkCameraHardWare(Context context) {
        PackageManager packageManager = context.getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!mHasSurface) {
            mHasSurface = true;
            if (hasCameraPermission()) {
                initCamera(holder);
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mHasSurface = false;
    }

    public Handler getCaptureActivityHandler() {
        return mCaptureActivityHandler;
    }

    private void initBeepSound() {
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        mPlayBeep = audioService.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
        if (!useCustomBeep && mPlayBeep && mMediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(mBeepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mMediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                mMediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mMediaPlayer.prepare();
            } catch (IOException e) {
                mMediaPlayer = null;
            }
        }
    }

    private void playBeepSoundAndVibrate() {
        if (mPlayBeep && mMediaPlayer != null) {
            mMediaPlayer.start();
        }
        if (mVibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener mBeepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.qr_code_iv_flash_light) {
            if (mNeedFlashLightOpen) {
                turnFlashlightOn();
            } else {
                turnFlashLightOff();
            }
        } else if (id == R.id.qr_code_header_black_pic) {
            if (!hasCameraPermission()) {
                mDecodeManager.showAppSettingsDialog(this, getPermissionRequestCode());
            } else {
                openSystemAlbum();
            }
        } else if (id == R.id.toolbar_back) {
            finish();
        }
    }

    private void openSystemAlbum() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_SYSTEM_PICTURE);
    }

    private void turnFlashlightOn() {
        turnFlashLight(true);
    }

    private void turnFlashLightOff() {
        turnFlashLight(false);
    }

    private void turnFlashLight(boolean on) {
        mNeedFlashLightOpen = !on;
        mTvFlashLightText.setText(getString(!on ? R.string.qr_code_open_flash_light : R.string.qr_code_close_flash_light));
        mIvFlashLight.setBackgroundResource(!on ? R.drawable.flashlight_turn_on : R.drawable.flashlight_turn_off);
        CameraManager.get().setFlashLight(on);
    }

    private void handleResult(String resultString) {
        if (TextUtils.isEmpty(resultString)) {
            mDecodeManager.showCouldNotReadQrCodeFromScanner(this, new DecodeManager.OnRefreshCameraListener() {
                @Override
                public void refresh() {
                    restartPreview();
                }
            });
        } else {
            processQrCodeScanned(resultString);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_PICTURE:
                finish();
                break;
            case REQUEST_SYSTEM_PICTURE:
                Uri uri = data.getData();
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                if (null != cursor) {
                    cursor.moveToFirst();
                    String imgPath = cursor.getString(1);
                    cursor.close();
                    if (null != mQrCodeExecutor && !TextUtils.isEmpty(imgPath)) {
                        mQrCodeExecutor.execute(new DecodeImageThread(imgPath, mDecodeImageCallback));
                    }
                }
                break;
        }
    }

    private Handler getContinueHandler() {
        if (continueHandler == null) {
            continueHandler = new Handler();
        }
        return continueHandler;
    }

    private void postRestartPreview() {
        getContinueHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                restartPreview();
            }
        }, continuouslyScanInterval);
    }

    private void postQrCodeEvent(String qrCodeMessage) {
        stopPreviewAndDecode();
        postRestartPreview();
        EventBus.getDefault().post(new QrCodeEvent(qrCodeMessage));
    }

    private void processQrCodeScanned(String qrCodeMessage) {
        if (continuouslyScan) {
            postQrCodeEvent(qrCodeMessage);
        } else {
            finishQrCode(qrCodeMessage);
        }
    }

    private void finishQrCode(String qrCodeMessage) {
        Intent intent = new Intent();
        intent.putExtra(REQUEST_QR_CODE_EXTRA_KEY, qrCodeMessage);
        setResult(RESULT_OK, intent);
        finish();
    }

    @AfterPermissionGranted(CAMERA_PERMS_REQUEST_CODE)
    private void requestPermission() {
        String[] perms = CAMERA_PERMS;
        if (EasyPermissions.hasPermissions(this, perms)) {
            afterPermissionGranted();
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.request_camera_permission_rationale),
                    CAMERA_PERMS_REQUEST_CODE, perms);
        }
    }

    private void afterPermissionGranted() {
        if (permissionGranted) {
            return;
        }
        permissionGranted = true;
        SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
        if (mHasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        turnFlashLightOff();
        initBeepSound();
        mVibrate = true;
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    protected int getPermissionRequestCode() {
        return 1;
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        if (getPermissionRequestCode() == NOTHING_PERMISSIONS_REQUEST) {
            Toast.makeText(getApplicationContext(), "Permission RequestCode must be override",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (EasyPermissions.somePermissionPermanentlyDenied(this, list)) {
            mDecodeManager.showAppSettingsDialog(QrCodeActivity.this, getPermissionRequestCode());
        } else {
            processTemporaryPermissionsDenied(requestCode, list);
        }
    }

    protected void processTemporaryPermissionsDenied(int requestCode, List<String> list) {
        Toast.makeText(getApplicationContext(), getString(R.string.warning_of_permissions_denied),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private DecodeImageCallback mDecodeImageCallback = new DecodeImageCallback() {
        @Override
        public void decodeSucceed(Result result) {
            mHandler.obtainMessage(MSG_DECODE_SUCCEED, result).sendToTarget();
        }

        @Override
        public void decodeFail(int type, String reason) {
            mHandler.sendEmptyMessage(MSG_DECODE_FAIL);
        }
    };

    private static class WeakHandler extends Handler {
        private WeakReference<QrCodeActivity> mWeakQrCodeActivity;
        private DecodeManager mDecodeManager = new DecodeManager();

        public WeakHandler(QrCodeActivity imagePickerActivity) {
            super();
            this.mWeakQrCodeActivity = new WeakReference<>(imagePickerActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            QrCodeActivity qrCodeActivity = mWeakQrCodeActivity.get();
            switch (msg.what) {
                case MSG_DECODE_SUCCEED:
                    Result result = (Result) msg.obj;
                    if (null == result) {
                        mDecodeManager.showCouldNotReadQrCodeFromPicture(qrCodeActivity);
                    } else {
                        String resultString = result.getText();
                        handleResult(resultString);
                    }
                    break;
                case MSG_DECODE_FAIL:
                    mDecodeManager.showCouldNotReadQrCodeFromPicture(qrCodeActivity);
                    break;
            }
            super.handleMessage(msg);
        }

        private void handleResult(String resultString) {
            QrCodeActivity imagePickerActivity = mWeakQrCodeActivity.get();
            imagePickerActivity.handleResult(resultString);
        }
    }
}