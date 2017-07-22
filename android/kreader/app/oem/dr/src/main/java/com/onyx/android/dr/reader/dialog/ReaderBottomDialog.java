package com.onyx.android.dr.reader.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.data.ReaderMenuBean;
import com.onyx.android.dr.reader.adapter.ReaderTabMenuAdapter;
import com.onyx.android.dr.reader.common.ReadSettingTtsConfig;
import com.onyx.android.dr.reader.common.ReaderTabMenuConfig;
import com.onyx.android.dr.reader.event.ChangeSpeechRateEvent;
import com.onyx.android.dr.reader.event.NotifyTtsStateChangedEvent;
import com.onyx.android.dr.reader.event.ReaderAfterReadingMenuEvent;
import com.onyx.android.dr.reader.event.ReaderAnnotationMenuEvent;
import com.onyx.android.dr.reader.event.ReaderGoodSentenceMenuEvent;
import com.onyx.android.dr.reader.event.ReaderListenMenuEvent;
import com.onyx.android.dr.reader.event.ReaderMainMenuItemEvent;
import com.onyx.android.dr.reader.event.ReaderPostilMenuEvent;
import com.onyx.android.dr.reader.event.ReaderTTSMenuPlayEvent;
import com.onyx.android.dr.reader.event.ReaderTTSMenuQuitReadingEvent;
import com.onyx.android.dr.reader.event.ReaderWordQueryMenuEvent;
import com.onyx.android.dr.reader.event.TtsSpeakingStateEvent;
import com.onyx.android.dr.reader.event.TtsStopStateEvent;
import com.onyx.android.dr.reader.handler.HandlerManger;
import com.onyx.android.dr.reader.presenter.ReaderPresenter;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by hehai on 17-7-14.
 */

public class ReaderBottomDialog extends Dialog implements View.OnClickListener {
    private boolean isWorld;
    PageRecyclerView readerTabMenu;
    private int layoutID = R.layout.reader_menu_bottom_dialog;
    private ReaderPresenter readerPresenter;
    private List<Integer> childIdList = null;
    private ReaderTabMenuAdapter readerTabMenuAdapter;
    private List<ReaderMenuBean> defaultMenuData;
    private View dismissZone;
    private LinearLayout menuBack;
    private TextView title;
    private View mainTabMenu;

    private AudioManager audioManager;
    private View readerTtsMenu;
    private SeekBar ttsMenuSpeechRate;
    private SeekBar ttsMenuVolume;
    private ImageView ttsMenuPlay;
    private int speechRate = 0;
    private int volume = 0;

    public ReaderBottomDialog(ReaderPresenter readerPresenter, @NonNull Context context, int layoutID, List<Integer> childIdList, boolean isWord) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.readerPresenter = readerPresenter;
        this.childIdList = childIdList;
        this.isWorld = isWord;
        setCanceledOnTouchOutside(false);
        if (layoutID != -1) {
            this.layoutID = layoutID;
        }
        initTtsData();
        setContentView(this.layoutID);
        initThirdLibrary();
        initData();
        initView();
    }

    private void initTtsData() {
        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
    }

    private void initData() {
        readerPresenter.getBookOperate().getDocumentInfo();
        defaultMenuData = ReaderTabMenuConfig.getMenuData();
    }

    private void initThirdLibrary() {
        EventBus.getDefault().register(this);
    }

    private void initView() {
        menuBack = (LinearLayout) findViewById(R.id.menu_back);
        title = (TextView) findViewById(R.id.title_bar_title);
        title.setTextColor(Color.BLACK);
        title.setText(getContext().getString(R.string.dialog_reader_menu_back));
        mainTabMenu = findViewById(R.id.reader_main_tab_menu);
        readerTabMenu = (PageRecyclerView) findViewById(R.id.tab_menu);
        readerTabMenu.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL);
        readerTabMenu.addItemDecoration(dividerItemDecoration);
        readerTabMenuAdapter = new ReaderTabMenuAdapter();
        readerTabMenu.setAdapter(readerTabMenuAdapter);

        dismissZone = findViewById(R.id.dismiss_zone);
        dismissZone.setOnClickListener(this);
        menuBack.setOnClickListener(this);

        readerTtsMenu = findViewById(R.id.reader_tts_menu_id);

        if (childIdList != null && childIdList.size() > 0) {
            initCustomizeView();
        } else {
            initDefaultView();
        }
    }

    private void initDefaultView() {
        if (!isWorld) {
            defaultMenuData.get(0).setEnable(false);
            defaultMenuData.get(2).setEnable(false);
            defaultMenuData.get(3).setEnable(false);
        }
        readerTabMenuAdapter.setMenuDataList(defaultMenuData);
        readerTabMenuAdapter.notifyDataSetChanged();

        ReaderMainMenuItemEvent.bindReaderDefaultMenuItemEvent();
        initTtsMenuItemClickEvent();
    }

    private void initCustomizeView() {
        for (int i = 0; i < childIdList.size(); i++) {
            View view = findViewById(childIdList.get(i));
            if (view != null) {
                view.setOnClickListener(this);
            }
        }
    }

    private void initTtsMenuItemClickEvent() {
        findViewById(R.id.reader_tts_menu_quit_reading).setOnClickListener(this);

        ttsMenuSpeechRate = (SeekBar) findViewById(R.id.reader_tts_menu_speed_rate);
        ttsMenuSpeechRate.setOnClickListener(this);
        speechRate = ReadSettingTtsConfig.getSpeechRate(readerPresenter.getReaderView().getViewContext());

        ttsMenuSpeechRate.setMax(ReadSettingTtsConfig.SPEED_RATE_FIVE);
        ttsMenuSpeechRate.setProgress(speechRate);
        initSpeechRateProgress();

        ttsMenuVolume = (SeekBar) findViewById(R.id.reader_tts_menu_volume);
        ttsMenuVolume.setOnClickListener(this);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        ttsMenuVolume.setMax(maxVolume);
        ttsMenuVolume.setProgress(curVolume);
        initVolumeProgress();

        ttsMenuPlay = (ImageView) findViewById(R.id.reader_tts_menu_play);
        ttsMenuPlay.setOnClickListener(this);

        float initSpeechRate = ReadSettingTtsConfig.getSaveSpeechRate(readerPresenter.getReaderView().getViewContext());
        EventBus.getDefault().post(new ChangeSpeechRateEvent(initSpeechRate));
    }

    private void initSpeechRateProgress() {
        ttsMenuSpeechRate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    return;
                }
                speechRate = Math.max(progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ReadSettingTtsConfig.saveSpeechRate(readerPresenter, speechRate);
                EventBus.getDefault().post(new ChangeSpeechRateEvent(ReadSettingTtsConfig.getSpeechRateValue(speechRate)));
            }
        });
    }

    private void initVolumeProgress() {
        ttsMenuVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    return;
                }
                volume = Math.max(progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume,
                        AudioManager.FLAG_PLAY_SOUND);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderTTSMenuPlayEvent(ReaderTTSMenuPlayEvent event) {
        readerPresenter.getHandlerManger().updateActionProviderType(HandlerManger.TTS_PROVIDER);
        readerPresenter.getBookOperate().startTtsPlay();
        dismiss();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderTTSMenuQuitReadingEvent(ReaderTTSMenuQuitReadingEvent event) {
        readerPresenter.getHandlerManger().updateActionProviderType(HandlerManger.READING_PROVIDER);
        EventBus.getDefault().post(new NotifyTtsStateChangedEvent().onQuitReading());
        dismiss();
    }

    @Subscribe
    public void onTtsSpeakingStateEvent(TtsSpeakingStateEvent event) {
        ttsMenuPlay.setImageResource(R.drawable.ic_reader_stop);
    }

    @Subscribe
    public void onTtsStopStateEvent(TtsStopStateEvent event) {
        ttsMenuPlay.setImageResource(R.drawable.ic_reader_play);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnReaderPostilMenuEvent(ReaderPostilMenuEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnReaderAnnotationMenuEvent(ReaderAnnotationMenuEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnReaderWordQueryMenuEvent(ReaderWordQueryMenuEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnReaderGoodSentenceMenuEvent(ReaderGoodSentenceMenuEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnReaderListenMenuEvent(ReaderListenMenuEvent event) {
        readerTtsMenu.setVisibility(View.VISIBLE);
        mainTabMenu.setVisibility(View.GONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnReaderAfterReadingMenuEvent(ReaderAfterReadingMenuEvent event) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_back:
            case R.id.dismiss_zone:
                dismiss();
                break;
            default:
                int viewID = v.getId();
                Object event = ReaderMainMenuItemEvent.getDefaultMenuItemEvent(viewID);
                if (event != null) {
                    EventBus.getDefault().post(event);
                }
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        readerPresenter.getHandlerManger().onStop();
        EventBus.getDefault().unregister(this);
        readerPresenter.getBookOperate().redrawPage();
    }
}
