package com.onyx.android.dr.reader.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.onyx.android.dr.R;
import com.onyx.android.dr.reader.common.ReaderDeviceManager;
import com.onyx.android.dr.reader.presenter.ReaderPresenter;
import com.onyx.android.sdk.ui.dialog.OnyxBaseDialog;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.File;
import java.io.FileInputStream;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ming on 16/8/12.
 */
public class DialogAudioPlay extends OnyxBaseDialog implements View.OnClickListener {
    public static final String TAG = DialogAudioPlay.class.getSimpleName();
    public static final int VOLUME_SPAN = 2;
    public static final int UPDATE_PLAY_PROGRESS = 0x1000;
    public static final int UPDATE_PLAY_PROGRESS_TIME = 1000;

    @Bind(R.id.audio_play)
    ImageButton audioPlay;
    @Bind(R.id.audio_stop)
    ImageButton audioStop;
    @Bind(R.id.audio_voice)
    ImageButton audioVoice;
    @Bind(R.id.audio_close)
    ImageButton audioClose;
    @Bind(R.id.audio_button_layout)
    LinearLayout audioButtonLayout;
    @Bind(R.id.seek_bar_audio)
    SeekBar seekBarAudio;
    @Bind(R.id.voice_size_layout)
    LinearLayout voiceSizeLayout;
    @Bind(R.id.minus_voice)
    ImageButton minusVoice;
    @Bind(R.id.plus_voice)
    ImageButton plusVoice;
    @Bind(R.id.content_view)
    RelativeLayout contentView;
    @Bind(R.id.audio_play_progress)
    SeekBar audioPlayProgress;

    private int maxVolume;
    private AudioManager audioMgr;
    private ReaderPresenter readerPresenter;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private String audioPath;
    Handler handler = new Handler(){
        @Override
        public void dispatchMessage(Message msg) {
            switch (msg.what){
                case UPDATE_PLAY_PROGRESS:
                    updatePlayProgress();
                    break;
            }
            super.dispatchMessage(msg);
        }
    };

    public DialogAudioPlay(ReaderPresenter readerDataHolder) {
        super(readerDataHolder.getReaderView().getViewContext(), R.style.dialog_transparent_no_title);
        setContentView(R.layout.dialog_audio_play);
        this.readerPresenter = readerDataHolder;
        ButterKnife.bind(this);
        fitDialogToWindow();
        initView();
        initData();

        setCanceledOnTouchOutside(false);
    }

    private void fitDialogToWindow() {
        Window mWindow = getWindow();
        WindowManager.LayoutParams mParams = mWindow.getAttributes();
        mParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mParams.gravity = Gravity.BOTTOM;
        mWindow.setAttributes(mParams);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    public void show() {
        if (prepareMediaPlayer()) {
            startMediaPlayer();
        }
        super.show();
    }

    private void initView() {
        voiceSizeLayout.setVisibility(View.INVISIBLE);

        audioVoice.setOnClickListener(this);
        audioClose.setOnClickListener(this);
        audioPlay.setOnClickListener(this);
        audioStop.setOnClickListener(this);
        contentView.setOnClickListener(this);

        minusVoice.setOnClickListener(this);
        plusVoice.setOnClickListener(this);

        seekBarAudio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                controlVolume(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        audioPlayProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setPlayProgress(seekBar.getProgress());
            }
        });

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });

        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    if (event.getKeyCode() == KeyEvent.KEYCODE_PAGE_DOWN) {
                        readerPresenter.getBookOperate().nextScreen();
                        return true;
                    }
                    if (event.getKeyCode() == KeyEvent.KEYCODE_PAGE_UP) {
                        readerPresenter.getBookOperate().prevScreen();
                        return true;
                    }
                }
                return false;
            }
        });

    }

    private void initData() {
        audioMgr = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int curVolume = audioMgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        seekBarAudio.setMax(maxVolume);
        seekBarAudio.setProgress(curVolume);

        ReaderDeviceManager.setGcInterval(Integer.MAX_VALUE);
    }

    private void onClickVoice(View v) {
        voiceSizeLayout.setVisibility(voiceSizeLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        setViewCoordinate();
    }

    private void setViewCoordinate(){
        int width = voiceSizeLayout.getMeasuredWidth();
        int height = voiceSizeLayout.getMeasuredHeight();
        voiceSizeLayout.setY((voiceSizeLayout.getY() - width / 2 + height / 2));
    }

    private void onClickClose(View v) {
        closeMediaPlayer();
        dismiss();
    }

    private void onClickPlay(View v) {
        if (mediaPlayer == null) {
            prepareMediaPlayer();
            startMediaPlayer();
            return;
        }
        if (mediaPlayer.isPlaying()) {
            pauseMediaPlayer();
        } else {
            startMediaPlayer();
        }
        onAudioStateChanged();
    }

    private void onClickStop(View v) {
        closeMediaPlayer();
        onAudioStateChanged();
    }

    private void onClickMinusVoice(View v) {
        setSeekBarValue(false);
    }

    private void onClickPlusVoice(View v) {
        setSeekBarValue(true);
    }

    private void onClickContentView(View v) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.audio_voice:
                onClickVoice(v);
                break;
            case R.id.audio_close:
                onClickClose(v);
                break;
            case R.id.audio_play:
                onClickPlay(v);
                break;
            case R.id.audio_stop:
                onClickStop(v);
                break;
            case R.id.minus_voice:
                onClickMinusVoice(v);
                break;
            case R.id.plus_voice:
                onClickPlusVoice(v);
                break;
            case R.id.content_view:
                onClickContentView(v);
                break;
        }
    }


    private void hideVoiceControlLayout() {
        voiceSizeLayout.setVisibility(View.GONE);
    }

    private void setSeekBarValue(boolean plus) {
        int volumeProgress = seekBarAudio.getProgress();
        if (plus) {
            volumeProgress = volumeProgress + VOLUME_SPAN;
            volumeProgress = Math.min(maxVolume, volumeProgress);
        } else {
            volumeProgress = volumeProgress - VOLUME_SPAN;
            volumeProgress = Math.max(0, volumeProgress);
        }
        seekBarAudio.setProgress(volumeProgress);
    }

    public void onAudioStateChanged() {
        if (mediaPlayer == null) {
            audioPlay.setImageResource(R.drawable.ic_dialog_tts_play);
            return;
        }
        if (mediaPlayer.isPlaying()) {
            audioPlay.setImageResource(R.drawable.ic_dialog_tts_suspend);
        } else {
            audioPlay.setImageResource(R.drawable.ic_dialog_tts_play);
        }
    }

    public void updatePlayProgress(){
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            audioPlayProgress.setMax(mediaPlayer.getDuration());
            audioPlayProgress.setProgress(mediaPlayer.getCurrentPosition());
            handler.sendEmptyMessageDelayed(UPDATE_PLAY_PROGRESS,UPDATE_PLAY_PROGRESS_TIME);
        }
    }

    //调节音量
    private void controlVolume(int volume) {
        audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, volume,
                AudioManager.FLAG_PLAY_SOUND);
    }

    private void setPlayProgress(int progress){
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.seekTo(progress);
        }
    }

    private void startMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            onAudioStateChanged();
            handler.sendEmptyMessageDelayed(UPDATE_PLAY_PROGRESS,UPDATE_PLAY_PROGRESS_TIME);
        }
    }

    private String getAudioPath() {
        String path = readerPresenter.getReaderView().getViewContext().getFilesDir().getAbsolutePath() + File.separator + "audio";
        String name = audioPath.substring(audioPath.lastIndexOf(File.separator), audioPath.length());
        return path + name;
    }

    private boolean prepareMediaPlayer() {
        try {
            if (StringUtils.isNullOrEmpty(audioPath)) {
                Log.w(TAG, "audio file not exists: " + audioPath);
                return false;
            }
            audioPlayProgress.setProgress(0);
            if (mediaPlayer != null) {
                closeMediaPlayer();
            }
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.i(TAG, "media player onCompletion");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            closeMediaPlayer();
                            onAudioStateChanged();
                        }
                    });
                }
            });
            Log.i(TAG, "play audio file: " + getAudioPath());
            File file = new File(getAudioPath());
            FileInputStream fis = new FileInputStream(file);
            mediaPlayer.setDataSource(fis.getFD());
            mediaPlayer.prepare();
            if (mediaPlayer.getDuration() <= 64) {
                // even pico audio failed, it may still synthesize a blank wav file with 64ms duration
                Log.e(TAG, "audio speech duration too short: " + mediaPlayer.getDuration());
                return false;
            }
            return true;
        } catch (Exception e) {
            Log.w(TAG, e);
            return false;
        }
    }

    private void pauseMediaPlayer() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    private void closeMediaPlayer() {
        try {
            if (mediaPlayer == null) {
                return;
            }
            mediaPlayer.setOnCompletionListener(null);
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        } catch (Throwable tr) {
            Log.i(TAG, tr.toString());
        }
    }
}
