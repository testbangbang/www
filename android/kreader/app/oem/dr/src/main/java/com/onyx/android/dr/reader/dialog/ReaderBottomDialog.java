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
import com.onyx.android.dr.bean.GoodSentenceBean;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.data.ReaderMenuBean;
import com.onyx.android.dr.device.DeviceConfig;
import com.onyx.android.dr.reader.action.ShowReaderBottomMenuDialogAction;
import com.onyx.android.dr.reader.adapter.ReaderTabMenuAdapter;
import com.onyx.android.dr.reader.common.ReadSettingTtsConfig;
import com.onyx.android.dr.reader.common.ReaderTabMenuConfig;
import com.onyx.android.dr.reader.common.ToastManage;
import com.onyx.android.dr.reader.event.ChangeSpeechRateEvent;
import com.onyx.android.dr.reader.event.ManagePostilDialogEvent;
import com.onyx.android.dr.reader.event.NotifyTtsStateChangedEvent;
import com.onyx.android.dr.reader.event.ReaderAfterReadingMenuEvent;
import com.onyx.android.dr.reader.event.ReaderAnnotationMenuEvent;
import com.onyx.android.dr.reader.event.ReaderGoodSentenceMenuEvent;
import com.onyx.android.dr.reader.event.ReaderListenMenuEvent;
import com.onyx.android.dr.reader.event.ReaderMainMenuItemEvent;
import com.onyx.android.dr.reader.event.ReaderPostilMenuEvent;
import com.onyx.android.dr.reader.event.ReaderSettingMenuEvent;
import com.onyx.android.dr.reader.event.ReaderTTSMenuPlayEvent;
import com.onyx.android.dr.reader.event.ReaderTTSMenuQuitReadingEvent;
import com.onyx.android.dr.reader.event.ReaderWordQueryMenuEvent;
import com.onyx.android.dr.reader.event.TtsSpeakingStateEvent;
import com.onyx.android.dr.reader.event.TtsStopStateEvent;
import com.onyx.android.dr.reader.handler.HandlerManger;
import com.onyx.android.dr.reader.presenter.ReaderPresenter;
import com.onyx.android.dr.manager.OperatingDataManager;
import com.onyx.android.sdk.reader.common.PageAnnotation;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by hehai on 17-7-14.
 */

public class ReaderBottomDialog extends Dialog implements View.OnClickListener {
    private static final String TAG = ReaderBottomDialog.class.getSimpleName();

    public void setMode(boolean isWord) {
        this.isWorld = isWord;
        initDefaultView();
    }

    public static abstract class MenuCallback {
        public abstract void resetSelection();

        public abstract String getSelectionText();

        public abstract void copy();

        public abstract void onLineation();

        public abstract void addAnnotation();

        public abstract void showDictionary();

        public abstract void startTts();

        public abstract boolean supportSelectionMode();

        public abstract void closeMenu();

        public abstract void deleteAnnotation();
    }

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
    private DialogAnnotation.AnnotationAction action;
    private MenuCallback menuCallback;
    private ImageView tabMenuPrev;
    private ImageView tabMenuNext;

    public ReaderBottomDialog(ReaderPresenter readerPresenter, @NonNull Context context, int layoutID, List<Integer> childIdList, boolean isWord, MenuCallback callback) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.readerPresenter = readerPresenter;
        this.childIdList = childIdList;
        this.isWorld = isWord;
        this.menuCallback = callback;
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

    public void setAction(DialogAnnotation.AnnotationAction action) {
        this.action = action;
    }

    public DialogAnnotation.AnnotationAction getAction() {
        return action;
    }


    private void initTtsData() {
        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
    }

    private void initData() {
        readerPresenter.getBookOperate().getDocumentInfo();
        defaultMenuData = ReaderTabMenuConfig.getMenuData();
    }

    private void initThirdLibrary() {

    }

    private void initView() {
        menuBack = (LinearLayout) findViewById(R.id.menu_back);
        title = (TextView) findViewById(R.id.title_bar_title);
        title.setTextColor(Color.BLACK);
        title.setText(getContext().getString(R.string.dialog_reader_menu_back));
        mainTabMenu = findViewById(R.id.reader_main_tab_menu);
        readerTabMenu = (PageRecyclerView) findViewById(R.id.tab_menu);
        tabMenuPrev = (ImageView) mainTabMenu.findViewById(R.id.tab_menu_prev);
        tabMenuNext = (ImageView) mainTabMenu.findViewById(R.id.tab_menu_next);
        tabMenuPrev.setOnClickListener(this);
        tabMenuNext.setOnClickListener(this);
        readerTabMenu.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL);
        readerTabMenu.addItemDecoration(dividerItemDecoration);
        readerTabMenuAdapter = new ReaderTabMenuAdapter();
        readerTabMenu.setAdapter(readerTabMenuAdapter);
        menuBack.setOnClickListener(this);
        readerTabMenu.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPageChange(int position, int itemCount, int pageSize) {
                setPrevAndNextButtonVisible();
            }
        });

        readerTtsMenu = findViewById(R.id.reader_tts_menu_id);

        if (childIdList != null && childIdList.size() > 0) {
            initCustomizeView();
        } else {
            initDefaultView();
        }
    }

    private void setPrevAndNextButtonVisible() {
        int currentPage = readerTabMenu.getPaginator().getCurrentPage();
        int pages = readerTabMenu.getPaginator().pages();
        tabMenuPrev.setVisibility(currentPage == 0 ? View.GONE : View.VISIBLE);
        tabMenuNext.setVisibility(currentPage < pages - 1 ? View.VISIBLE : View.GONE);
    }

    private void initDefaultView() {
        setReaderTabMenu(defaultMenuData);

        ReaderMainMenuItemEvent.bindReaderDefaultMenuItemEvent();
        initTtsMenuItemClickEvent();
    }

    private boolean haveAnnotation() {
        List<PageAnnotation> annotations = ShowReaderBottomMenuDialogAction.getAnnotations(readerPresenter);
        return annotations != null && annotations.size() > 0;
    }

    public void setReaderTabMenu(List<ReaderMenuBean> menuData) {
        readerTabMenuAdapter.setMenuDataList(menuData);
        readerTabMenuAdapter.notifyDataSetChanged();
        tabMenuNext.setVisibility(menuData.size() > readerTabMenuAdapter.getColumnCount() ? View.VISIBLE : View.GONE);
    }

    private boolean isHide(ReaderMenuBean menu) {
        return menu.getTabKey().equals(DeviceConfig.ReaderMenuInfo.MENU_POSTIL) ||
                menu.getTabKey().equals(DeviceConfig.ReaderMenuInfo.MENU_MARK) ||
                menu.getTabKey().equals(DeviceConfig.ReaderMenuInfo.MENU_WORD_QUERY);
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
        if (!isHaveAnnotationOrSelection()) {
            return;
        }
        if (isWorld) {
            menuCallback.addAnnotation();
        } else {
            EventBus.getDefault().post(new ManagePostilDialogEvent());
            dismiss();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnReaderAnnotationMenuEvent(ReaderAnnotationMenuEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnReaderWordQueryMenuEvent(ReaderWordQueryMenuEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnReaderGoodSentenceMenuEvent(ReaderGoodSentenceMenuEvent event) {
        addGoodSentence();
    }

    private void addGoodSentence() {
        String selectionText = readerPresenter.getBookOperate().getSelectionText();
        if (StringUtils.isNotBlank(selectionText)) {
            GoodSentenceBean bean = new GoodSentenceBean();
            bean.setDetails(selectionText);
            bean.setReadingMatter(readerPresenter.getBookInfo().getBookName());
            bean.setPageNumber(String.valueOf(readerPresenter.getPageInformation().getCurrentPage()));
            bean.setGoodSentenceType(getGoodSentenceType(readerPresenter.getBookInfo().getLanguage()));
            OperatingDataManager.getInstance().insertGoodSentence(bean);
        } else {
            ToastManage.showMessage(getContext(), getContext().getString(R.string.Please_press_to_select_the_sentence_you_want_to_include));
        }
        dismiss();
        readerPresenter.getBookOperate().redrawPage();
        readerPresenter.getHandlerManger().updateActionProviderType(HandlerManger.READING_PROVIDER);
        readerPresenter.getReaderSelectionManager().clear();
    }

    private int getGoodSentenceType(String language) {
        int type;
        if (StringUtils.isNotBlank(language)) {
            switch (language) {
                case Constants.CHINESE:
                    type = Constants.CHINESE_TYPE;
                    break;
                case Constants.ENGLISH:
                    type = Constants.ENGLISH_TYPE;
                    break;
                default:
                    type = Constants.OTHER_TYPE;
            }
        } else {
            type = Constants.OTHER_TYPE;
        }
        return type;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnReaderListenMenuEvent(ReaderListenMenuEvent event) {
        readerTtsMenu.setVisibility(View.VISIBLE);
        mainTabMenu.setVisibility(View.GONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnReaderAfterReadingMenuEvent(ReaderAfterReadingMenuEvent event) {
        ReaderDialogManage.onShowAfterReadingMenu(readerPresenter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnReaderSettingMenuEvent(ReaderSettingMenuEvent event) {
        ReaderDialogManage.onShowSettingMenu(readerPresenter);
        dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_back:
                dismiss();
                break;
            case R.id.tab_menu_prev:
                readerTabMenu.prevPage();
                break;
            case R.id.tab_menu_next:
                readerTabMenu.nextPage();
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStart();
        EventBus.getDefault().unregister(this);
    }

    private boolean isHaveAnnotationOrSelection() {
        return isWorld || haveAnnotation();
    }
}
