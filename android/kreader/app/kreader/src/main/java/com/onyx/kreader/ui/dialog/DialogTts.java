package com.onyx.kreader.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.kreader.R;
import com.onyx.kreader.device.ReaderDeviceManager;
import com.onyx.kreader.host.request.ScaleToPageRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.handler.HandlerManager;
import com.onyx.kreader.ui.handler.TtsHandler;

/**
 * Created by ming on 16/8/12.
 */
public class DialogTts extends Dialog implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    public static final int VOLUME_SPAN = 2;

    @Bind(R.id.tts_play)
    ImageButton ttsPlay;
    @Bind(R.id.tts_stop)
    ImageButton ttsStop;
    @Bind(R.id.tts_voice)
    ImageButton ttsVoice;
    @Bind(R.id.tts_speed)
    ImageButton ttsSpeed;
    @Bind(R.id.tts_close)
    ImageButton ttsClose;
    @Bind(R.id.tts_button_layout)
    LinearLayout ttsButtonLayout;
    @Bind(R.id.seek_bar_tts)
    SeekBar seekBarTts;
    @Bind(R.id.voice_size_layout)
    LinearLayout voiceSizeLayout;
    @Bind(R.id.fastest_speed)
    CheckBox fastSpeed;
    @Bind(R.id.faster_speed)
    CheckBox moreFastSpeed;
    @Bind(R.id.normal_speed)
    CheckBox normalSpeed;
    @Bind(R.id.slower_speed)
    CheckBox moreSlowSpeed;
    @Bind(R.id.slowest_speed)
    CheckBox slowSpeed;
    @Bind(R.id.voice_speed_layout)
    RadioGroup voiceSpeedLayout;
    @Bind(R.id.minus_voice)
    ImageButton minusVoice;
    @Bind(R.id.plus_voice)
    ImageButton plusVoice;

    private int maxVolume;
    private AudioManager audioMgr;
    private ReaderDataHolder readerDataHolder;
    private TtsHandler ttsHandler;

    private Pair<Integer, Pair<Integer, Float>>[] speedCheckBoxCollection = new Pair[] {
        new Pair(R.id.slowest_speed, new Pair<>(1, 0.5f)),
            new Pair(R.id.slower_speed, new Pair<>(2, 0.75f)),
            new Pair(R.id.normal_speed, new Pair<>(3, 1.0f)),
            new Pair(R.id.faster_speed, new Pair<>(4, 1.5f)),
            new Pair(R.id.fastest_speed, new Pair<>(5, 2.0f)),
    };

    private int gcInterval = 0;

    public DialogTts(ReaderDataHolder readerDataHolder) {
        super(readerDataHolder.getContext(), R.style.dialog_transparent_no_title);
        setContentView(R.layout.dialog_tts);

        this.readerDataHolder = readerDataHolder;
        ttsHandler = (TtsHandler)readerDataHolder.getHandlerManager().getActiveProvider();

        ButterKnife.bind(this);
        initView();
        initData();

        // force to be false, or else it will be turned on for unknown reason
        setCanceledOnTouchOutside(false);
    }

    public void show() {
        readerDataHolder.submitRenderRequest(new ScaleToPageRequest(readerDataHolder.getCurrentPageName()), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                ttsHandler.ttsPlay();
            }
        });
        super.show();
    }

    private void initView() {
        voiceSizeLayout.setVisibility(View.INVISIBLE);
        voiceSpeedLayout.setVisibility(View.INVISIBLE);

        ttsVoice.setOnClickListener(this);
        ttsSpeed.setOnClickListener(this);
        ttsClose.setOnClickListener(this);
        ttsPlay.setOnClickListener(this);
        ttsStop.setOnClickListener(this);

        minusVoice.setOnClickListener(this);
        plusVoice.setOnClickListener(this);

        fastSpeed.setOnCheckedChangeListener(this);
        moreFastSpeed.setOnCheckedChangeListener(this);
        normalSpeed.setOnCheckedChangeListener(this);
        slowSpeed.setOnCheckedChangeListener(this);
        moreSlowSpeed.setOnCheckedChangeListener(this);

        voiceSizeLayout.post(new Runnable() {
            @Override
            public void run() {
                int width = voiceSizeLayout.getMeasuredWidth();
                int height = voiceSizeLayout.getMeasuredHeight();
                voiceSizeLayout.setY(voiceSizeLayout.getY() - width / 2 + height / 2);
            }
        });

        voiceSpeedLayout.post(new Runnable() {
            @Override
            public void run() {
                int width = voiceSpeedLayout.getMeasuredWidth();
                int btnWidth = ttsSpeed.getMeasuredWidth();
                float speedButtonX = ttsSpeed.getX();
                voiceSpeedLayout.setX(speedButtonX - width / 2 + btnWidth / 2);
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

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                ReaderDeviceManager.setGcInterval(gcInterval);
                ttsHandler.ttsStop();
                readerDataHolder.getHandlerManager().setActiveProvider(HandlerManager.READING_PROVIDER);
            }
        });

        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    ttsHandler.onKeyUp(readerDataHolder, keyCode, event);
                }
                return false;
            }
        });

    }

    private void initData(){
        audioMgr = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int curVolume = audioMgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        seekBarTts.setMax(maxVolume);
        seekBarTts.setProgress(curVolume);

        // reset to default normal speed
        readerDataHolder.getTtsManager().setSpeechRate(1.0f);
        updateSpeedCheckBoxCheckedStatus(3);

        ttsHandler.registerCallback(new TtsHandler.Callback() {
            @Override
            public void onStateChanged() {
                if (readerDataHolder.getTtsManager().isSpeaking()) {
                    ttsPlay.setImageResource(R.drawable.ic_dialog_tts_suspend);
                } else {
                    ttsPlay.setImageResource(R.drawable.ic_dialog_tts_play);
                }
            }
        });

        gcInterval = ReaderDeviceManager.getGcInterval();
        ReaderDeviceManager.setGcInterval(Integer.MAX_VALUE);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(ttsVoice)) {
            voiceSizeLayout.setVisibility(voiceSizeLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            voiceSpeedLayout.setVisibility(View.GONE);
        } else if (v.equals(ttsSpeed)) {
            voiceSizeLayout.setVisibility(View.GONE);
            voiceSpeedLayout.setVisibility(voiceSpeedLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        } else if (v.equals(ttsClose)) {
            ttsHandler.ttsStop();
            dismiss();
        }else if (v.equals(ttsPlay)){
            if (readerDataHolder.getTtsManager().isSpeaking()) {
                ttsHandler.ttsPause();
            } else {
                ttsHandler.ttsPlay();
            }
        }else if (v.equals(ttsStop)){
            ttsHandler.ttsStop();
        }else if (v.equals(minusVoice)){
            setSeekBarValue(false);
        }else if (v.equals(plusVoice)){
            setSeekBarValue(true);
        }
    }

    private void setSeekBarValue(boolean plus){
        int volumeProgress = seekBarTts.getProgress();
        if (plus){
            volumeProgress = volumeProgress + VOLUME_SPAN;
            volumeProgress = Math.min(maxVolume,volumeProgress);
        }else {
            volumeProgress = volumeProgress - VOLUME_SPAN;
            volumeProgress = Math.max(0,volumeProgress);
        }
        seekBarTts.setProgress(volumeProgress);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(!buttonView.isPressed()){
            return;
        }
        for (int i = 0; i < speedCheckBoxCollection.length; i++) {
            if (buttonView.getId() == getCheckBoxId(i)) {
                final int speed = getCheckBoxSpeed(i);
                controlSpeedOfSound(getCheckBoxRate(i));
                updateSpeedCheckBoxCheckedStatus(speed);
                break;
            }
        }
    }

    private int getCheckBoxId(int index) {
        return speedCheckBoxCollection[index].first;
    }

    private int getCheckBoxSpeed(int index) {
        return speedCheckBoxCollection[index].second.first;
    }

    private float getCheckBoxRate(int index) {
        return speedCheckBoxCollection[index].second.second;
    }

    private void updateSpeedCheckBoxCheckedStatus(int targetSpeed) {
        for (int i = 0; i < speedCheckBoxCollection.length; i++) {
            final int speed = getCheckBoxSpeed(i);
            if (speed <= targetSpeed) {
                ((CompoundButton)findViewById(getCheckBoxId(i))).setChecked(true);
            } else {
                ((CompoundButton) findViewById(getCheckBoxId(i))).setChecked(false);
            }
        }
    }

    //调节音量
    private void controlVolume(int volume){
        audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, volume,
                AudioManager.FLAG_PLAY_SOUND);
    }

    //调节声音速度(1-5档)
    private void controlSpeedOfSound(final float rate){
        ttsHandler.setSpeechRate(rate);
    }

}
