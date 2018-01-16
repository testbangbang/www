package com.onyx.edu.reader.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.ui.dialog.OnyxBaseDialog;
import com.onyx.android.sdk.utils.DateTimeUtil;
import com.onyx.edu.reader.R;
import com.onyx.edu.reader.device.ReaderDeviceManager;
import com.onyx.edu.reader.ui.actions.NextScreenAction;
import com.onyx.edu.reader.ui.events.CloseMediaPlayDialogEvent;
import com.onyx.edu.reader.ui.events.MediaPlayCompleteEvent;
import com.onyx.edu.reader.ui.events.MediaPlayStartEvent;
import com.onyx.edu.reader.ui.handler.HandlerManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ming on 18/1/12.
 */
public class DialogMediaPlay extends OnyxBaseDialog implements View.OnClickListener {
    public static final int VOLUME_SPAN = 2;

    public interface MediaPlayListener {
        void startMedia();
        void resumeMedia();
        void stopMedia();
        void pauseMedia();
        void quitMedia();
        void closeDialog(Dialog dialog);
        boolean keyUp(int keyCode, KeyEvent event);
        void seekTo(int msec);
        void nextPage();
    }

    @Bind(R.id.tts_play)
    ImageButton ttsPlay;
    @Bind(R.id.tts_stop)
    ImageButton ttsStop;
    @Bind(R.id.tts_voice)
    ImageButton ttsVoice;
    @Bind(R.id.tts_close)
    ImageButton ttsClose;
    @Bind(R.id.tts_button_layout)
    LinearLayout ttsButtonLayout;
    @Bind(R.id.seek_bar_tts)
    SeekBar seekBarTts;
    @Bind(R.id.voice_size_layout)
    LinearLayout voiceSizeLayout;
    @Bind(R.id.minus_voice)
    ImageButton minusVoice;
    @Bind(R.id.plus_voice)
    ImageButton plusVoice;
    @Bind(R.id.content_view)
    RelativeLayout contentView;
    @Bind(R.id.progress)
    TextView progress;
    @Bind(R.id.media_seek_bar)
    SeekBar seekBarMedia;
    @Bind(R.id.length)
    TextView mediaLength;

    private int maxVolume;
    private AudioManager audioMgr;
    private CountDownTimer timer;
    private MediaPlayListener mediaPlayListener;
    private EventBus eventBus;
    private MediaPlayer mediaPlayer;
    private boolean stop = false;

    private int gcInterval = 0;

    public DialogMediaPlay(Context context,
                           EventBus bus,
                           MediaPlayer player,
                           MediaPlayListener listener) {
        super(context, R.style.dialog_transparent_no_title);
        setContentView(R.layout.dialog_media_play);

        eventBus = bus;
        mediaPlayListener = listener;
        mediaPlayer = player;

        eventBus.register(this);
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

    public void show() {
        super.show();
        mediaPlayListener.startMedia();
    }

    private void initView() {
        voiceSizeLayout.setVisibility(View.INVISIBLE);

        ttsVoice.setOnClickListener(this);
        ttsClose.setOnClickListener(this);
        ttsPlay.setOnClickListener(this);
        ttsStop.setOnClickListener(this);
        contentView.setOnClickListener(this);

        minusVoice.setOnClickListener(this);
        plusVoice.setOnClickListener(this);
        seekBarMedia.setProgress(0);

        voiceSizeLayout.post(new Runnable() {
            @Override
            public void run() {
                int width = voiceSizeLayout.getMeasuredWidth();
                int height = voiceSizeLayout.getMeasuredHeight();
                voiceSizeLayout.setY(voiceSizeLayout.getY() - width / 2 + height / 2);
            }
        });

        seekBarTts.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
        seekBarMedia.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekTo(seekBar.getProgress());
            }
        });

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                cancelTimer();
                resetSeekBar();
                ReaderDeviceManager.setGcInterval(gcInterval);
                eventBus.unregister(DialogMediaPlay.this);
                mediaPlayListener.closeDialog(DialogMediaPlay.this);
            }
        });

        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    mediaPlayListener.keyUp(keyCode, event);
                }
                return false;
            }
        });

    }

    private void initData() {
        audioMgr = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int curVolume = audioMgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        seekBarTts.setMax(maxVolume);
        seekBarTts.setProgress(curVolume);

        gcInterval = ReaderDeviceManager.getGcInterval();
        ReaderDeviceManager.setGcInterval(Integer.MAX_VALUE);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(ttsVoice)) {
            voiceSizeLayout.setVisibility(voiceSizeLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        } else if (v.equals(ttsClose)) {
            mediaPlayListener.quitMedia();
            dismiss();
        } else if (v.equals(ttsPlay)) {
            onPlay();
        } else if (v.equals(ttsStop)) {
            hideVoiceControlLayout();
            mediaPlayListener.stopMedia();
            setStop(true);
            onMediaPlayStateChanged();
        } else if (v.equals(minusVoice)) {
            setSeekBarValue(false);
        } else if (v.equals(plusVoice)) {
            setSeekBarValue(true);
        } else if (v.equals(contentView)) {
            hideVoiceControlLayout();
        }
    }

    private void onPlay() {
        hideVoiceControlLayout();
        if (isStop()) {
            mediaPlayListener.startMedia();
            setStop(false);
        }else {
            if (getMediaPlayer().isPlaying()) {
                mediaPlayListener.pauseMedia();
                cancelTimer();
            } else {
                mediaPlayListener.resumeMedia();
                countTime();
            }
            onMediaPlayStateChanged();
        }
    }

    private void countTime() {
        cancelTimer();
        int length = getMediaPlayer().getDuration() - getMediaPlayer().getCurrentPosition();
        timer = new CountDownTimer(length, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                seekBarMedia.setProgress(getMediaPlayer().getCurrentPosition());
                updateTime(getMediaPlayer().getDuration() - millisUntilFinished);
            }

            @Override
            public void onFinish() {
                seekBarMedia.setProgress(getMediaPlayer().getDuration());
                updateTime(getMediaPlayer().getDuration());
            }
        }.start();
    }

    private void resetSeekBar() {
        seekBarMedia.setProgress(0);
        seekBarMedia.setMax(0);
        progress.setText(R.string.init_time);
        mediaLength.setText(R.string.init_time);
    }

    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void updateTime(long p) {
        progress.setText(DateTimeUtil.formatTime(p));
        mediaLength.setText(DateTimeUtil.formatTime(getMediaPlayer().getDuration()));
    }

    private MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public boolean isStop() {
        return stop;
    }

    private void hideVoiceControlLayout() {
        voiceSizeLayout.setVisibility(View.GONE);
    }

    private void setSeekBarValue(boolean plus) {
        int volumeProgress = seekBarTts.getProgress();
        if (plus) {
            volumeProgress = volumeProgress + VOLUME_SPAN;
            volumeProgress = Math.min(maxVolume, volumeProgress);
        } else {
            volumeProgress = volumeProgress - VOLUME_SPAN;
            volumeProgress = Math.max(0, volumeProgress);
        }
        seekBarTts.setProgress(volumeProgress);
    }

    @Subscribe
    public void onMediaPlayStartEvent(MediaPlayStartEvent event) {
        resetSeekBar();
        seekBarMedia.setMax(getMediaPlayer().getDuration());
        countTime();
        onMediaPlayStateChanged();
    }

    @Subscribe
    public void onMediaPlayCompleteEvent(MediaPlayCompleteEvent event) {
        seekBarMedia.setProgress(seekBarMedia.getMax());
        mediaPlayListener.stopMedia();
        mediaPlayListener.nextPage();
    }

    @Subscribe
    public void onCloseMediaPlayDialogEvent(CloseMediaPlayDialogEvent event) {
        mediaPlayListener.quitMedia();
        dismiss();
    }

    private void onMediaPlayStateChanged() {
        if (getMediaPlayer().isPlaying()) {
            ttsPlay.setImageResource(R.drawable.ic_dialog_tts_suspend);
        } else {
            ttsPlay.setImageResource(R.drawable.ic_dialog_tts_play);
        }
    }

    private void controlVolume(int volume) {
        audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, volume,
                AudioManager.FLAG_PLAY_SOUND);
    }

    private void seekTo(int msec) {
        mediaPlayListener.seekTo(msec);
        if (getMediaPlayer().isPlaying()) {
            countTime();
        }
    }
}
