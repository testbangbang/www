package com.onyx.android.dr.activity;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.onyx.android.dr.R;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.dialog.AlertInfoDialog;
import com.onyx.android.dr.util.DictPreference;
import com.onyx.android.dr.util.Recorder;
import com.onyx.android.dr.util.RemainingTimeCalculator;
import com.onyx.android.dr.util.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.OnClick;

import static com.onyx.android.dr.util.Recorder.PROGRESS_VALUE;
import static com.onyx.android.dr.util.Recorder.SIXTY;
import static com.onyx.android.dr.util.Recorder.THIRTY;

public class SpeechRecordingActivity extends BaseActivity
        implements Button.OnClickListener, Recorder.OnStateChangedListener {
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.image_view_back)
    ImageView imageViewBack;
    @Bind(R.id.title_bar_title)
    TextView title;
    @Bind(R.id.speech_recording_activity_content)
    TextView content;
    @Bind(R.id.speech_recording_activity_start_lecture)
    ImageButton recordButton;
    @Bind(R.id.speech_recording_activity_stop)
    ImageButton stopButton;
    @Bind(R.id.speech_recording_activity_record_playback)
    ImageButton playButton;
    @Bind(R.id.record_time_activity_progressbar)
    ProgressBar stateProgressBar;
    @Bind(R.id.record_time_activity_timer_view)
    TextView mTimerView;
    @Bind(R.id.record_time_activity_max_value)
    TextView mMaxValue;
    @Bind(R.id.record_time_activity_min_value)
    TextView mMinValue;
    @Bind(R.id.speech_recording_activity_confirm)
    Button confirm;
    @Bind(R.id.speech_recording_activity_cancel)
    Button cancel;
    @Bind(R.id.speech_recording_activity_save_container)
    LinearLayout saveContainer;
    private static final String RECORDER_STATE_KEY = "recorder_state";
    private static final String SAMPLE_INTERRUPTED_KEY = "sample_interrupted";
    private static final String MAX_FILE_SIZE_KEY = "max_file_size";
    private static final String AUDIO_3GPP = "audio/3gpp";
    private static final String AUDIO_AMR = "audio/amr";
    private static final String AUDIO_ANY = "audio/*";
    private static final String ANY_ANY = "*/*";
    private static final String PLAYBACK_INTENT = "com.android.music.musicservicecommand";
    private static final String EXTRA_NAME = "command";
    private static final String WAKE_LOCK_TAG = "SoundRecorder";
    private static final String FILE_TYPE_AMR = ".amr";
    private static final String FILE_TYPE_3GPP = ".3gpp";
    private static final String CONTENT_URI_NAME = "external";
    private static final String IS_MUSIC = "0";
    private static final String SUFFIX = "=?";
    private static final int BITRATE_AMR = 5900;
    private static final int BITRATE_3GPP = 5900;
    private static final long MULTIPLE = 1000L;
    private WakeLock wakeLock;
    private String requestedType = AUDIO_ANY;
    private Recorder recorder;
    boolean sampleInterrupted = false;
    private String errorUiMessage = null;
    private long maxFileSize = -1;
    private RemainingTimeCalculator remainingTimeCalculator;
    private String timerFormat;
    private final Handler handler = new Handler();
    private BroadcastReceiver sdCardMountEventReceiver = null;
    private String extraValue = "pause";
    private String dataScheme = "file";
    private String colourString = "count(*)";
    private int millisecond = 1000;
    private int speechTime = 0;
    private int number = 0;
    private String informalEssayTitle;
    private AlertInfoDialog alertDialog;

    private Runnable updateTimer = new Runnable() {
        public void run() {
            updateTimerView();
        }
    };

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_speech_recording;
    }

    @Override
    protected void initConfig() {
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {
        DictPreference.init(this);
        getIntentData();
        initTitleData();
        initRecordData();
        initEvent();
    }

    private void getIntentData() {
        String informalEssayContent = getIntent().getStringExtra(Constants.INFORMAL_ESSAY_CONTENT);
        informalEssayTitle = getIntent().getStringExtra(Constants.INFORMAL_ESSAY_TITLE);
        content.setText(informalEssayContent);
    }

    private void initTitleData() {
        image.setImageResource(R.drawable.speech_recording);
        title.setText(getString(R.string.speech_recording) + "/" + informalEssayTitle);
    }

    private void initRecordData() {
        Intent intent = getIntent();
        if (intent != null) {
            String type = intent.getType();
            if (AUDIO_AMR.equals(type) || AUDIO_3GPP.equals(type) || AUDIO_ANY.equals(type)
                    || ANY_ANY.equals(type)) {
                requestedType = type;
            } else if (type != null) {
                // we only support amr and 3gpp formats right now
                setResult(RESULT_CANCELED);
                finish();
                return;
            }
            final String EXTRA_MAX_BYTES
                    = MediaStore.Audio.Media.EXTRA_MAX_BYTES;
            maxFileSize = intent.getLongExtra(EXTRA_MAX_BYTES, -1);
        }
        if (AUDIO_ANY.equals(requestedType) || ANY_ANY.equals(requestedType)) {
            requestedType = AUDIO_3GPP;
        }
        recorder = new Recorder();
        recorder.setOnStateChangedListener(this);
        remainingTimeCalculator = new RemainingTimeCalculator();
        PowerManager pm
                = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, WAKE_LOCK_TAG);
        timerFormat = getResources().getString(R.string.timer_format);
        setResult(RESULT_CANCELED);
        registerExternalStorageListener();
        updateUi();
    }

    public void initEvent() {
    }

    /**
     * Make sure we're not recording music playing in the background, ask
     * the MediaPlaybackService to pause playback.
     */
    private void stopAudioPlayback() {
        Intent intent = new Intent(PLAYBACK_INTENT);
        intent.putExtra(EXTRA_NAME, extraValue);
        sendBroadcast(intent);
    }

    @OnClick({R.id.image_view_back,
            R.id.speech_recording_activity_record_playback,
            R.id.speech_recording_activity_stop,
            R.id.speech_recording_activity_setting,
            R.id.speech_recording_activity_confirm,
            R.id.speech_recording_activity_cancel,
            R.id.speech_recording_activity_start_lecture})
    public void onClick(View view) {
        if (!view.isEnabled()) {
            return;
        }
        switch (view.getId()) {
            case R.id.speech_recording_activity_start_lecture:
                if (speechTime == 0) {
                    CommonNotices.showMessage(this, getString(R.string.set_speech_time));
                    return;
                }
                recordSpeechContent();
                break;
            case R.id.speech_recording_activity_record_playback:
                recorder.startPlayback();
                break;
            case R.id.speech_recording_activity_stop:
                recorder.stop();
                break;
            case R.id.speech_recording_activity_setting:
                settingSpeechTime();
                break;
            case R.id.speech_recording_activity_confirm:
                recorder.stop();
                saveSample();
                saveContainer.setVisibility(View.GONE);
                break;
            case R.id.speech_recording_activity_cancel:
                recorder.delete();
                saveContainer.setVisibility(View.GONE);
                CommonNotices.showMessage(this, getString(R.string.please_record_again));
                break;
        }
    }

    private void settingSpeechTime() {
        alertDialog = new AlertInfoDialog(this, getString(R.string.setting_record_time), false,
                getResources().getString(R.string.dialog_button_confirm), getResources().getString(R.string.dialog_button_cancel));
        Utils.setDialogAttributes(alertDialog);
        alertDialog.setOKOnClickListener(new AlertInfoDialog.OnOKClickListener() {
            @Override
            public void onOKClick(int value) {
                speechTime = value;
            }
        });
        alertDialog.setCancelOnClickListener(new AlertInfoDialog.OnCancelClickListener() {
            @Override
            public void onCancelClick() {
                if (alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
            }
        });
    }

    private void recordSpeechContent() {
        remainingTimeCalculator.reset();
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            sampleInterrupted = true;
            errorUiMessage = getResources().getString(R.string.insert_sd_card);
            updateUi();
        } else if (!remainingTimeCalculator.diskSpaceAvailable()) {
            sampleInterrupted = true;
            errorUiMessage = getResources().getString(R.string.storage_is_full);
            updateUi();
        } else {
            stopAudioPlayback();
            if (AUDIO_AMR.equals(requestedType)) {
                remainingTimeCalculator.setBitRate(BITRATE_AMR);
                recorder.startRecording(MediaRecorder.OutputFormat.AMR_NB, FILE_TYPE_AMR, this);
            } else if (AUDIO_3GPP.equals(requestedType)) {
                remainingTimeCalculator.setBitRate(BITRATE_3GPP);
                recorder.startRecording(MediaRecorder.OutputFormat.THREE_GPP, FILE_TYPE_3GPP,
                        this);
            } else {
                throw new IllegalArgumentException(getString(R.string.Invalid_output_file_type_requested));
            }
            if (maxFileSize != -1) {
                remainingTimeCalculator.setFileSizeLimit(
                        recorder.sampleFile(), maxFileSize);
            }
        }
    }

    /**
     * Handle the "back" hardware key.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            switch (recorder.state()) {
                case Recorder.IDLE_STATE:
                    if (recorder.sampleLength() > 0) {
                        saveSample();
                    }
                    finish();
                    break;
                case Recorder.PLAYING_STATE:
                    recorder.stop();
                    saveSample();
                    break;
                case Recorder.RECORDING_STATE:
                    recorder.clear();
                    break;
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * If we have just recorded a smaple, this adds it to the media data base
     * and sets the result to the sample's URI.
     */
    private void saveSample() {
        if (recorder.sampleLength() == 0) {
            return;
        }
        Uri uri = null;
        try {
            uri = this.addToMediaDB(recorder.sampleFile());
        } catch (UnsupportedOperationException ex) {
            return;
        }
        if (uri == null) {
            return;
        }
        setResult(RESULT_OK, new Intent().setData(uri));
        CommonNotices.showMessage(this, getString(R.string.saved_successfully));
    }

    /**
     * Called on destroy to unregister the SD card mount event receiver.
     */
    @Override
    public void onDestroy() {
        if (sdCardMountEventReceiver != null) {
            unregisterReceiver(sdCardMountEventReceiver);
            sdCardMountEventReceiver = null;
        }
        super.onDestroy();
    }

    /**
     * Registers an intent to listen for ACTION_MEDIA_EJECT/ACTION_MEDIA_MOUNTED
     * notifications.
     */
    private void registerExternalStorageListener() {
        if (sdCardMountEventReceiver == null) {
            sdCardMountEventReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
                        recorder.delete();
                    } else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
                        sampleInterrupted = false;
                        updateUi();
                    }
                }
            };
            IntentFilter iFilter = new IntentFilter();
            iFilter.addAction(Intent.ACTION_MEDIA_EJECT);
            iFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
            iFilter.addDataScheme(dataScheme);
            registerReceiver(sdCardMountEventReceiver, iFilter);
        }
    }

    /**
     * A simple utility to do a query into the databases.
     */
    private Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        try {
            ContentResolver resolver = getContentResolver();
            if (resolver == null) {
                return null;
            }
            return resolver.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (UnsupportedOperationException ex) {
            return null;
        }
    }

    /**
     * Add the given audioId to the playlist with the given playlistId; and maintain the
     * play_order in the playlist.
     */
    private void addToPlaylist(ContentResolver resolver, int audioId, long playlistId) {
        String[] cols = new String[]{colourString};
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri(CONTENT_URI_NAME, playlistId);
        Cursor cur = resolver.query(uri, cols, null, null, null);
        cur.moveToFirst();
        final int base = cur.getInt(0);
        cur.close();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, Integer.valueOf(base + audioId));
        values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, audioId);
        resolver.insert(uri, values);
    }

    /**
     * Obtain the id for the default play list from the audio_playlists table.
     */
    private int getPlaylistId(Resources res) {
        Uri uri = MediaStore.Audio.Playlists.getContentUri(CONTENT_URI_NAME);
        final String[] ids = new String[]{MediaStore.Audio.Playlists._ID};
        final String where = MediaStore.Audio.Playlists.NAME + SUFFIX;
        final String[] args = new String[]{res.getString(R.string.audio_db_playlist_name)};
        Cursor cursor = query(uri, ids, where, args, null);
        if (cursor == null) {
            CommonNotices.showMessage(this, getString(R.string.query_returns_null));
        }
        int id = -1;
        if (cursor != null) {
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                id = cursor.getInt(0);
            }
        }
        cursor.close();
        return id;
    }

    /**
     * Create a playlist with the given default playlist name, if no such playlist exists.
     */
    private Uri createPlaylist(Resources res, ContentResolver resolver) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(MediaStore.Audio.Playlists.NAME, res.getString(R.string.audio_db_playlist_name));
        Uri uri = resolver.insert(MediaStore.Audio.Playlists.getContentUri(CONTENT_URI_NAME), contentValue);
        if (uri == null) {
            CommonNotices.showMessage(this, getString(R.string.error_mediadb_new_record));
        }
        return uri;
    }

    /**
     * Adds file and returns content uri.
     */
    private Uri addToMediaDB(File file) {
        Resources res = getResources();
        ContentValues contentValue = new ContentValues();
        long current = System.currentTimeMillis();
        long modDate = file.lastModified();
        Date date = new Date(current);
        SimpleDateFormat formatter = new SimpleDateFormat(
                res.getString(R.string.audio_db_title_format));
        String title = formatter.format(date);
        long sampleLengthMillis = recorder.sampleLength() * MULTIPLE;
        // Lets label the recorded audio file as NON-MUSIC so that the file
        // won't be displayed automatically, except for in the playlist.
        contentValue.put(MediaStore.Audio.Media.IS_MUSIC, IS_MUSIC);
        contentValue.put(MediaStore.Audio.Media.TITLE, title);
        contentValue.put(MediaStore.Audio.Media.DATA, file.getAbsolutePath());
        contentValue.put(MediaStore.Audio.Media.DATE_ADDED, (int) (current / Recorder.CARDINAL_NUMBER));
        contentValue.put(MediaStore.Audio.Media.DATE_MODIFIED, (int) (modDate / Recorder.CARDINAL_NUMBER));
        contentValue.put(MediaStore.Audio.Media.DURATION, sampleLengthMillis);
        contentValue.put(MediaStore.Audio.Media.MIME_TYPE, requestedType);
        contentValue.put(MediaStore.Audio.Media.ARTIST,
                res.getString(R.string.audio_db_artist_name));
        contentValue.put(MediaStore.Audio.Media.ALBUM,
                res.getString(R.string.audio_db_album_name));
        ContentResolver resolver = getContentResolver();
        Uri base = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri result = resolver.insert(base, contentValue);
        if (result == null) {
            CommonNotices.showMessage(this, getString(R.string.error_mediadb_new_record));
            return null;
        }
        if (getPlaylistId(res) == -1) {
            createPlaylist(res, resolver);
        }
        int audioId = Integer.valueOf(result.getLastPathSegment());
        addToPlaylist(resolver, audioId, getPlaylistId(res));
        // Notify those applications such as Music listening to the
        // scanner events that a recorded audio file just created.
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, result));
        return result;
    }

    /**
     * Update the big MM:SS timer. If we are in playback, also update the
     * progress bar.
     */
    private void updateTimerView() {
        int state = recorder.state();
        boolean ongoing = state == Recorder.RECORDING_STATE || state == Recorder.PLAYING_STATE;
        long time = ongoing ? recorder.progress() : recorder.sampleLength();
        String timeStr = String.format(timerFormat, time / SIXTY, time % SIXTY);
        mTimerView.setText(timeStr);
        if (state == Recorder.PLAYING_STATE) {
            long value = PROGRESS_VALUE * time / recorder.sampleLength();
            stateProgressBar.setProgress((int) value);
            if (time >= speechTime * SIXTY) {
                mMaxValue.setText(speechTime * SIXTY + getString(R.string.second));
            }else {
                mMaxValue.setText(String.valueOf(recorder.sampleLength()) + getString(R.string.second));
            }
        } else if (state == Recorder.RECORDING_STATE) {
            if (speechTime * SIXTY - time < THIRTY) {
                Resources res = getResources();
                if (number == 0) {
                    CommonNotices.showMessage(this, String.format(res.getString(R.string.sec_available), THIRTY));
                    number++;
                }
            }
        }
        if (speechTime * SIXTY > time){
            if (ongoing) {
                handler.postDelayed(updateTimer, millisecond);
            }
        }else{
            mTimerView.setText(timeStr);
        }
    }

    /**
     * Shows/hides the appropriate child views for the new state.
     */
    private void updateUi() {
        Resources res = getResources();
        switch (recorder.state()) {
            case Recorder.IDLE_STATE:
                setIdleState();
                break;
            case Recorder.RECORDING_STATE:
                setRecordingState();
                break;
            case Recorder.PLAYING_STATE:
                setPlayingState();
                break;
        }
        updateTimerView();
    }

    private void setPlayingState() {
        recordButton.setEnabled(true);
        playButton.setEnabled(false);
        stopButton.setEnabled(true);
        stateProgressBar.setVisibility(View.VISIBLE);
        mMaxValue.setVisibility(View.VISIBLE);
        mMinValue.setVisibility(View.VISIBLE);
        mTimerView.setVisibility(View.VISIBLE);
        saveContainer.setVisibility(View.VISIBLE);
    }

    private void setRecordingState() {
        recordButton.setEnabled(false);
        playButton.setEnabled(false);
        stopButton.setEnabled(true);
        stateProgressBar.setVisibility(View.GONE);
        mMaxValue.setVisibility(View.INVISIBLE);
        mMinValue.setVisibility(View.INVISIBLE);
        saveContainer.setVisibility(View.GONE);
        mTimerView.setVisibility(View.VISIBLE);
    }

    private void setIdleState() {
        if (recorder.sampleLength() == 0) {
            recordButton.setEnabled(true);
            playButton.setEnabled(false);
            stopButton.setEnabled(false);
            recordButton.requestFocus();
            stateProgressBar.setVisibility(View.INVISIBLE);
            mTimerView.setVisibility(View.INVISIBLE);
            mMaxValue.setVisibility(View.INVISIBLE);
            mMinValue.setVisibility(View.INVISIBLE);
            saveContainer.setVisibility(View.GONE);
        } else {
            recordButton.setEnabled(true);
            playButton.setEnabled(true);
            stopButton.setEnabled(false);
            stateProgressBar.setVisibility(View.INVISIBLE);
            mTimerView.setVisibility(View.INVISIBLE);
            mMaxValue.setVisibility(View.INVISIBLE);
            mMinValue.setVisibility(View.INVISIBLE);
            saveContainer.setVisibility(View.GONE);
        }
    }

    /**
     * Called when Recorder changed it's state.
     */
    public void onStateChanged(int state) {
        if (state == Recorder.PLAYING_STATE || state == Recorder.RECORDING_STATE) {
            sampleInterrupted = false;
            errorUiMessage = null;
            // we don't want to go to sleep while recording or playing
            wakeLock.acquire();
        } else {
            if (wakeLock.isHeld()) {
                wakeLock.release();
            }
        }
        updateUi();
    }

    /**
     * Called when MediaPlayer encounters an error.
     */
    public void onError(int error) {
        Resources res = getResources();
        String message = null;
        switch (error) {
            case Recorder.SDCARD_ACCESS_ERROR:
                message = res.getString(R.string.error_sdcard_access);
                CommonNotices.showMessage(this, message);
                break;
            case Recorder.INTERNAL_ERROR:
                message = res.getString(R.string.error_app_internal);
                CommonNotices.showMessage(this, message);
                break;
        }
    }

    @Override
    public void onStop() {
        recorder.stop();
        super.onStop();
    }

    @Override
    protected void onPause() {
        sampleInterrupted = recorder.state() == Recorder.RECORDING_STATE;
        recorder.stop();
        super.onPause();
    }
}
