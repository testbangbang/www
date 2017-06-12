package com.onyx.edu.reader.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.util.Pair;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.ui.dialog.OnyxBaseDialog;
import com.onyx.edu.reader.R;
import com.onyx.edu.reader.device.ReaderDeviceManager;
import com.onyx.android.sdk.reader.host.request.ScaleToPageRequest;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;
import com.onyx.edu.reader.ui.events.TtsErrorEvent;
import com.onyx.edu.reader.ui.events.TtsRequestSentenceEvent;
import com.onyx.edu.reader.ui.events.TtsStateChangedEvent;
import com.onyx.edu.reader.ui.handler.HandlerManager;
import com.onyx.edu.reader.ui.handler.TtsHandler;

import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ming on 16/8/12.
 */
public class DialogTts extends OnyxBaseDialog implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
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
    @Bind(R.id.content_view)
    RelativeLayout contentView;

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
        readerDataHolder.getEventBus().register(this);

        ttsHandler = (TtsHandler) readerDataHolder.getHandlerManager().getActiveProvider();

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
        contentView.setOnClickListener(this);

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
                int btnWidth = ttsSpeed.getMeasuredWidth();
                voiceSpeedLayout.setX(voiceSpeedLayout.getX() + btnWidth + getContext().getResources().getDimension(R.dimen.tts_button_margin) * 2);
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
                readerDataHolder.removeActiveDialog(DialogTts.this);
                readerDataHolder.getEventBus().unregister(DialogTts.this);
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

    private void initData() {
        audioMgr = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int curVolume = audioMgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        seekBarTts.setMax(maxVolume);
        seekBarTts.setProgress(curVolume);

        readerDataHolder.getTtsManager().setSpeechRate(ttsHandler.getSpeechRate());
        updateSpeedCheckBoxCheckedStatus(getCheckBoxSpeedByRate(ttsHandler.getSpeechRate()));

        gcInterval = ReaderDeviceManager.getGcInterval();
        ReaderDeviceManager.setGcInterval(Integer.MAX_VALUE);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onTtsStateChanged(final TtsStateChangedEvent event) {
        onTtsStateChanged();
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onTtsRequestSentence(final TtsRequestSentenceEvent event) {
        ttsHandler.requestSentenceForTts();
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onTtsError(final TtsErrorEvent event) {
        ttsHandler.onError();
        onTtsStateChanged();
        Toast.makeText(readerDataHolder.getContext(), R.string.tts_play_failed, Toast.LENGTH_LONG);
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
        } else if (v.equals(ttsPlay)) {
            hideVoiceControlLayout();
            if (readerDataHolder.getTtsManager().isSpeaking()) {
                ttsHandler.ttsPause();
            } else {
                ttsHandler.ttsPlay();
            }
        } else if (v.equals(ttsStop)) {
            hideVoiceControlLayout();
            ttsHandler.ttsStop();
        } else if (v.equals(minusVoice)) {
            setSeekBarValue(false);
        } else if (v.equals(plusVoice)) {
            setSeekBarValue(true);
        } else if (v.equals(contentView)) {
            hideVoiceControlLayout();
        }
    }

    private void hideVoiceControlLayout() {
        voiceSpeedLayout.setVisibility(View.GONE);
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!buttonView.isPressed()) {
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

    public void onTtsStateChanged() {
        if (readerDataHolder.getTtsManager().isSpeaking()) {
            ttsPlay.setImageResource(R.drawable.ic_dialog_tts_suspend);
        } else {
            ttsPlay.setImageResource(R.drawable.ic_dialog_tts_play);
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

    private int getCheckBoxSpeedByRate(final float rate) {
        for (int i = 0; i < speedCheckBoxCollection.length; i++) {
            if (getCheckBoxRate(i) == rate) {
                return getCheckBoxSpeed(i);
            }
        }
        return 3;
    }

    private void updateSpeedCheckBoxCheckedStatus(int targetSpeed) {
        for (int i = 0; i < speedCheckBoxCollection.length; i++) {
            final int speed = getCheckBoxSpeed(i);
            if (speed <= targetSpeed) {
                ((CompoundButton) findViewById(getCheckBoxId(i))).setChecked(true);
            } else {
                ((CompoundButton) findViewById(getCheckBoxId(i))).setChecked(false);
            }
        }
    }

    //调节音量
    private void controlVolume(int volume) {
        audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, volume,
                AudioManager.FLAG_PLAY_SOUND);
    }

    //调节声音速度(1-5档)
    private void controlSpeedOfSound(final float rate) {
        ttsHandler.setSpeechRate(rate);
    }

}
