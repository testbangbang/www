package com.onyx.android.dr.reader.dialog;

import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.onyx.android.dr.R;
import com.onyx.android.dr.reader.common.ReadPageInfo;
import com.onyx.android.dr.reader.common.ReadPhysicalKeyConfig;
import com.onyx.android.dr.reader.common.ReadSettingFontFaceConfig;
import com.onyx.android.dr.reader.common.ReadSettingFontSizeConfig;
import com.onyx.android.dr.reader.common.ReadSettingMarginConfig;
import com.onyx.android.dr.reader.common.ReadSettingSpaceConfig;
import com.onyx.android.dr.reader.common.ReadSettingTtsConfig;
import com.onyx.android.dr.reader.common.ReaderBookInfoDialogConfig;
import com.onyx.android.dr.reader.event.ChangeSpeechRateEvent;
import com.onyx.android.dr.reader.event.DisplayStatusBarEvent;
import com.onyx.android.dr.reader.event.DocumentInfoRequestResultEvent;
import com.onyx.android.dr.reader.event.GotoPositionActionResultEvent;
import com.onyx.android.dr.reader.event.NotifyTtsStateChangedEvent;
import com.onyx.android.dr.reader.event.ReaderMainMenuBottomBookMarkEvent;
import com.onyx.android.dr.reader.event.ReaderMainMenuBottomCatalogueEvent;
import com.onyx.android.dr.reader.event.ReaderMainMenuBottomPdfSettingEvent;
import com.onyx.android.dr.reader.event.ReaderMainMenuBottomPhysicalKeyEvent;
import com.onyx.android.dr.reader.event.ReaderMainMenuBottomProgressEvent;
import com.onyx.android.dr.reader.event.ReaderMainMenuBottomReadingSettingEvent;
import com.onyx.android.dr.reader.event.ReaderMainMenuBottomVoiceEvent;
import com.onyx.android.dr.reader.event.ReaderMainMenuDialogDismissZoneEvent;
import com.onyx.android.dr.reader.event.ReaderMainMenuItemEvent;
import com.onyx.android.dr.reader.event.ReaderMainMenuTopBackEvent;
import com.onyx.android.dr.reader.event.ReaderMainMenuTopBookStoreEvent;
import com.onyx.android.dr.reader.event.ReaderMainMenuTopBrightnessEvent;
import com.onyx.android.dr.reader.event.ReaderMainMenuTopMoreEvent;
import com.onyx.android.dr.reader.event.ReaderMainMenuTopSearchEvent;
import com.onyx.android.dr.reader.event.ReaderMainMenuTopShelfEvent;
import com.onyx.android.dr.reader.event.ReaderMainMenuTopUserEvent;
import com.onyx.android.dr.reader.event.ReaderMenuMorePressEvent;
import com.onyx.android.dr.reader.event.ReaderPageInfoMenuBackEvent;
import com.onyx.android.dr.reader.event.ReaderPageInfoMenuNextChapterEvent;
import com.onyx.android.dr.reader.event.ReaderPageInfoMenuPrevChapterEvent;
import com.onyx.android.dr.reader.event.ReaderPageInfoMenuReadingProcessEvent;
import com.onyx.android.dr.reader.event.ReaderPhysicalKeyMenuOneEvent;
import com.onyx.android.dr.reader.event.ReaderPhysicalKeyMenuThreeEvent;
import com.onyx.android.dr.reader.event.ReaderPhysicalKeyMenuTwoEvent;
import com.onyx.android.dr.reader.event.ReaderReadSettingMenuFontFaceFiveEvent;
import com.onyx.android.dr.reader.event.ReaderReadSettingMenuFontFaceFourEvent;
import com.onyx.android.dr.reader.event.ReaderReadSettingMenuFontFaceOneEvent;
import com.onyx.android.dr.reader.event.ReaderReadSettingMenuFontFaceSixEvent;
import com.onyx.android.dr.reader.event.ReaderReadSettingMenuFontFaceThreeEvent;
import com.onyx.android.dr.reader.event.ReaderReadSettingMenuFontFaceTwoEvent;
import com.onyx.android.dr.reader.event.ReaderReadSettingMenuFontFiveEvent;
import com.onyx.android.dr.reader.event.ReaderReadSettingMenuFontFourEvent;
import com.onyx.android.dr.reader.event.ReaderReadSettingMenuFontOneEvent;
import com.onyx.android.dr.reader.event.ReaderReadSettingMenuFontSevenEvent;
import com.onyx.android.dr.reader.event.ReaderReadSettingMenuFontSixEvent;
import com.onyx.android.dr.reader.event.ReaderReadSettingMenuFontThreeEvent;
import com.onyx.android.dr.reader.event.ReaderReadSettingMenuFontTwoEvent;
import com.onyx.android.dr.reader.event.ReaderReadSettingMenuMarginBigEvent;
import com.onyx.android.dr.reader.event.ReaderReadSettingMenuMarginMidEvent;
import com.onyx.android.dr.reader.event.ReaderReadSettingMenuMarginSmallEvent;
import com.onyx.android.dr.reader.event.ReaderReadSettingMenuSpaceBigEvent;
import com.onyx.android.dr.reader.event.ReaderReadSettingMenuSpaceMidEvent;
import com.onyx.android.dr.reader.event.ReaderReadSettingMenuSpaceSmallEvent;
import com.onyx.android.dr.reader.event.ReaderTTSMenuPlayEvent;
import com.onyx.android.dr.reader.event.ReaderTTSMenuQuitReadingEvent;
import com.onyx.android.dr.reader.event.TtsSpeakingStateEvent;
import com.onyx.android.dr.reader.event.TtsStopStateEvent;
import com.onyx.android.dr.reader.handler.HandlerManger;
import com.onyx.android.dr.reader.presenter.ReaderPresenter;
import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContent;
import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContentEntry;
import com.onyx.android.sdk.ui.view.TreeRecyclerView;
import com.onyx.android.sdk.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by huxiaomao on 17/5/10.
 */

public class ReaderMainMenuDialog extends Dialog implements View.OnClickListener {
    private static final String TAG = ReaderMainMenuDialog.class.getSimpleName();
    private List<Integer> childIdList = null;
    private int layoutID = R.layout.reader_main_menu;
    private View readerMainMenuTop;
    private View dismissZone;
    private View readerPageInfoMenu = null;
    private View readerPhysicalKeySettingMenu = null;
    private View readerTtsMenu = null;
    private View readerReadingSettingMenu = null;
    private View readerMainMenuBottom = null;
    private ReaderPresenter readerPresenter = null;

    private ImageView mainMenuBottomPhysicalKey;
    private ImageView mainMenuBottomVoice;
    private ImageView mainMenuBottomProgress;
    private ImageView mainMenuBottomReadSetting;
    private ImageView mainMenuBottomPdf;

    private ImageView readSettingFontSizeOne;
    private ImageView readSettingFontSizeTwo;
    private ImageView readSettingFontSizeThree;
    private ImageView readSettingFontSizeFour;
    private ImageView readSettingFontSizeFive;
    private ImageView readSettingFontSizeSix;
    private ImageView readSettingFontSizeSeven;

    private ImageView readSettingMenuMarginSmall;
    private ImageView readSettingMenuMarginMid;
    private ImageView readSettingMenuMarginBig;

    private ImageView readSettingMenuSpaceSmall;
    private ImageView readSettingMenuSpaceMid;
    private ImageView readSettingMenuSpaceBig;

    private RadioButton readSettingMenuFontFaceOne;
    private RadioButton readSettingMenuFontFaceTwo;
    private RadioButton readSettingMenuFontFaceThree;
    private RadioButton readSettingMenuFontFaceFour;
    private RadioButton readSettingMenuFontFaceFive;
    private RadioButton readSettingMenuFontFaceSix;

    private TextView pageInfoMenuBookName;
    private TextView pageInfoMenuPage;
    private ImageView pageInfoMenuBack;
    private TextView pageInfoMenuPrevChapter;
    private TextView pageInfoMenuNextChapter;
    private SeekBar pageInfoMenuReadProgress;

    private int currentPage = 0;
    private int initCurrentPage = 0;

    private RadioButton physicalKeyMenuOne;
    private RadioButton physicalKeyMenuTwo;
    private RadioButton physicalKeyMenuThree;

    private AudioManager audioManager;
    private SeekBar ttsMenuSpeechRate;
    private SeekBar ttsMenuVolume;
    private ImageView ttsMenuPlay;
    private int speechRate = 0;
    private int volume = 0;
    private View topMenu;
    private String title;
    private ArrayList<TreeRecyclerView.TreeNode> rootNodes;

    public ReaderMainMenuDialog(ReaderPresenter readerPresenter, @NonNull Context context, int layoutID, List<Integer> childIdList) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.readerPresenter = readerPresenter;
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

    private void initData() {
        readerPresenter.getBookOperate().getDocumentInfo();
    }

    public void initTtsData() {
        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
    }

    private void initThirdLibrary() {
        EventBus.getDefault().register(this);
    }

    private void initView() {
        if (childIdList != null && childIdList.size() > 0) {
            initCustomizeView();
        } else {
            initDefaultView();
        }
    }

    private void initDefaultView() {
        dismissZone = findViewById(R.id.dismiss_zone);
        dismissZone.setOnClickListener(this);
        topMenu = findViewById(R.id.reader_main_menu_top_id);

        ReaderMainMenuItemEvent.bindReaderDefaultMenuItemEvent();
        initMainMenuTopItemClickEvent();
        initChildMenu();
        initPhysicalKeyMenuItemClickEvent();
        initMainMenuBottomItemClickEvent();
        initPageInfoMenuItemClickEvent();
        initTtsMenuItemClickEvent();
        initReadSettingFontSizeClickEvent();
        initReadSettingMenuMarginClickEvent();
        initReadSettingMenuSpaceClickEvent();
        initReadSettingMenuFontFaceClickEvent();

        initDisableMenus();
    }

    private void initMainMenuTopItemClickEvent() {
        findViewById(R.id.reader_main_menu_top_shelf).setOnClickListener(this);
        findViewById(R.id.reader_main_menu_top_book_store).setOnClickListener(this);
        findViewById(R.id.reader_main_menu_top_user).setOnClickListener(this);
        findViewById(R.id.reader_main_menu_top_search).setOnClickListener(this);
        findViewById(R.id.reader_main_menu_top_back).setOnClickListener(this);
        findViewById(R.id.reader_main_menu_top_brightness).setOnClickListener(this);
        findViewById(R.id.reader_main_menu_top_more).setOnClickListener(this);
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

    private void initPhysicalKeyMenuItemClickEvent() {
        physicalKeyMenuOne = (RadioButton) findViewById(R.id.reader_physical_key_menu_one);
        physicalKeyMenuOne.setOnClickListener(this);

        physicalKeyMenuTwo = (RadioButton) findViewById(R.id.reader_physical_key_menu_two);
        physicalKeyMenuTwo.setOnClickListener(this);

        physicalKeyMenuThree = (RadioButton) findViewById(R.id.reader_physical_key_menu_three);
        physicalKeyMenuThree.setOnClickListener(this);

        //set default value
        int id = R.id.reader_physical_key_menu_one;
        int value = ReadPhysicalKeyConfig.getReadSettingFontFace(readerPresenter.getReaderView().getViewContext());
        switch (value) {
            case ReadPhysicalKeyConfig.READ_Physical_Key_ONE:
                id = R.id.reader_physical_key_menu_one;
                break;
            case ReadPhysicalKeyConfig.READ_Physical_Key_TWO:
                id = R.id.reader_physical_key_menu_two;
                break;
            case ReadPhysicalKeyConfig.READ_Physical_Key_THREE:
                id = R.id.reader_physical_key_menu_three;
                break;
        }
        setPhysicalKeyButtonState(id, value);
    }

    private void initChildMenu() {
        readerMainMenuTop = findViewById(R.id.reader_main_menu_top_id);
        readerPageInfoMenu = findViewById(R.id.reader_page_info_menu_id);

        readerPhysicalKeySettingMenu = findViewById(R.id.reader_physical_key_setting_menu_id);
        readerTtsMenu = findViewById(R.id.reader_tts_menu_id);

        readerReadingSettingMenu = findViewById(R.id.reader_reading_setting_menu_id);

        readerMainMenuBottom = findViewById(R.id.reader_main_menu_bottom_id);
        onShowAndHideChildMenu(R.id.reader_page_info_menu_id);
    }

    private void initMainMenuBottomItemClickEvent() {
        findViewById(R.id.reader_main_menu_bottom_catalogue).setOnClickListener(this);
        findViewById(R.id.reader_main_menu_bottom_bookmark).setOnClickListener(this);

        mainMenuBottomPhysicalKey = (ImageView) findViewById(R.id.reader_main_menu_bottom_physical_key);
        mainMenuBottomPhysicalKey.setOnClickListener(this);

        mainMenuBottomVoice = (ImageView) findViewById(R.id.reader_main_menu_bottom_voice);
        mainMenuBottomVoice.setOnClickListener(this);

        mainMenuBottomProgress = (ImageView) findViewById(R.id.reader_main_menu_bottom_progress);
        mainMenuBottomProgress.setOnClickListener(this);

        mainMenuBottomReadSetting = (ImageView) findViewById(R.id.reader_main_menu_bottom_reading_setting);
        mainMenuBottomReadSetting.setOnClickListener(this);

        mainMenuBottomPdf = (ImageView) findViewById(R.id.reader_main_menu_bottom_pdf_setting);
        mainMenuBottomPdf.setOnClickListener(this);

        setMainMenuBottomButtonState(R.id.reader_main_menu_bottom_progress);
    }

    private void initDisableMenus() {
        if (!ReadPageInfo.supportTextPage(readerPresenter)) {
            mainMenuBottomVoice.setVisibility(View.GONE);
        }
        if (!ReadPageInfo.supportNoteExport(readerPresenter)) {
        }
        if (!ReadPageInfo.isFixedPageDocument(readerPresenter)) {
        } else if (!ReadPageInfo.isFixedPageDocument(readerPresenter)) {
        }

        if (!ReadPageInfo.supportTypefaceAdjustment(readerPresenter)) {
            mainMenuBottomPdf.setVisibility(View.VISIBLE);
            mainMenuBottomReadSetting.setVisibility(View.GONE);
        }

        if (readerPresenter.getHandlerManger().isTtsModel()) {
            isTtsModel();
            onShowAndHideChildMenu(R.id.reader_tts_menu_id);
        }
    }

    private void initPageInfoMenuItemClickEvent() {
        pageInfoMenuBookName = (TextView) findViewById(R.id.reader_page_info_menu_book_name);

        pageInfoMenuPage = (TextView) findViewById(R.id.reader_page_info_menu_page);

        pageInfoMenuBack = (ImageView) findViewById(R.id.reader_page_info_menu_back);
        pageInfoMenuBack.setOnClickListener(this);

        pageInfoMenuPrevChapter = (TextView) findViewById(R.id.reader_page_info_menu_prev_chapter);
        pageInfoMenuPrevChapter.setOnClickListener(this);

        pageInfoMenuNextChapter = (TextView) findViewById(R.id.reader_page_info_menu_next_chapter);
        pageInfoMenuNextChapter.setOnClickListener(this);

        pageInfoMenuReadProgress = (SeekBar) findViewById(R.id.reader_page_info_menu_read_progress);
        pageInfoMenuReadProgress.setOnClickListener(this);

        currentPage = initCurrentPage = ReadPageInfo.getCurrentPage(readerPresenter);
        updatePageInfo();
        initPageProgress();
    }

    private void initPageProgress() {
        pageInfoMenuReadProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    return;
                }
                int page = Math.max(progress - 1, 0);
                currentPage = page;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                gotoPage();
            }
        });
    }

    private void gotoPage() {
        readerPresenter.getPageInformation().setCurrentPage(currentPage);
        readerPresenter.getBookOperate().gotoPage(false);
        updatePageInfo();
    }

    private void updatePageInfo() {
        pageInfoMenuReadProgress.setMax(ReadPageInfo.getTotalPage(readerPresenter));
        pageInfoMenuReadProgress.setProgress(currentPage);
        if (!StringUtils.isNullOrEmpty(title)) {
            pageInfoMenuBookName.setText(readerPresenter.isFluent() ? title : ReadPageInfo.getReadBookName(readerPresenter));
        }
        pageInfoMenuPage.setText(ReadPageInfo.getReadProgress(readerPresenter, currentPage));
        ReadPageInfo.setReadTime();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDocumentInfoRequestResultEvent(DocumentInfoRequestResultEvent event) {
        ReaderDocumentTableOfContent readerDocumentTableOfContent = event.getReaderDocumentTableOfContent();
        rootNodes = ReaderBookInfoDialogConfig.buildTreeNodesFromToc(readerDocumentTableOfContent);
        getAllNodes(rootNodes);
        setTitle();
    }

    private void getAllNodes(List<TreeRecyclerView.TreeNode> rootNodes) {
        ListIterator<TreeRecyclerView.TreeNode> iterator = rootNodes.listIterator();
        while (iterator.hasNext()) {
            TreeRecyclerView.TreeNode next = iterator.next();
            iteratorInner(iterator, next);
        }
    }

    private void iteratorInner(ListIterator<TreeRecyclerView.TreeNode> iterator, TreeRecyclerView.TreeNode next) {
        if (next.hasChildren()) {
            ArrayList<TreeRecyclerView.TreeNode> children = next.getChildren();
            for (TreeRecyclerView.TreeNode node : children) {
                iterator.add(node);
                iteratorInner(iterator, node);
            }
        }
    }

    private void setTitle() {
        String preTitle = "";
        int currentPagePosition = Integer.parseInt(readerPresenter.getCurrentPagePosition());
        for (TreeRecyclerView.TreeNode node : rootNodes) {
            ReaderDocumentTableOfContentEntry entry = (ReaderDocumentTableOfContentEntry) node.getTag();
            int position = Integer.parseInt(entry.getPosition());
            title = entry.getTitle();
            Log.d(TAG, "setTitle: " + title + "--" + position);
            if (position == -1) {
                break;
            }

            if (currentPagePosition == 0) {
                pageInfoMenuBookName.setText(readerPresenter.isFluent() ? preTitle : ReadPageInfo.getReadBookName(readerPresenter));
                return;
            }

            if (currentPagePosition >= position) {
                preTitle = title;
            }
        }
        pageInfoMenuBookName.setText(readerPresenter.isFluent() ? preTitle : ReadPageInfo.getReadBookName(readerPresenter));
    }

    private void initReadSettingMenuFontFaceClickEvent() {
        readSettingMenuFontFaceOne = (RadioButton) findViewById(R.id.read_setting_menu_font_face_one);
        readSettingMenuFontFaceOne.setOnClickListener(this);

        readSettingMenuFontFaceTwo = (RadioButton) findViewById(R.id.read_setting_menu_font_face_two);
        readSettingMenuFontFaceTwo.setOnClickListener(this);

        readSettingMenuFontFaceThree = (RadioButton) findViewById(R.id.read_setting_menu_font_face_three);
        readSettingMenuFontFaceThree.setOnClickListener(this);

        readSettingMenuFontFaceFour = (RadioButton) findViewById(R.id.read_setting_menu_font_face_four);
        readSettingMenuFontFaceFour.setOnClickListener(this);

        readSettingMenuFontFaceFive = (RadioButton) findViewById(R.id.read_setting_menu_font_face_five);
        readSettingMenuFontFaceFive.setOnClickListener(this);

        readSettingMenuFontFaceSix = (RadioButton) findViewById(R.id.read_setting_menu_font_face_six);
        readSettingMenuFontFaceSix.setOnClickListener(this);

        //set default value
        int value = ReadSettingFontFaceConfig.getReadSettingFontFace(readerPresenter);
        int id = R.id.read_setting_menu_font_face_one;
        switch (value) {
            case ReadSettingFontFaceConfig.READ_SETTING_FONT_FACE_ONE:
                id = R.id.read_setting_menu_font_face_one;
                break;
            case ReadSettingFontFaceConfig.READ_SETTING_FONT_FACE_TWO:
                id = R.id.read_setting_menu_font_face_two;
                break;
            case ReadSettingFontFaceConfig.READ_SETTING_FONT_FACE_THREE:
                id = R.id.read_setting_menu_font_face_three;
                break;
            case ReadSettingFontFaceConfig.READ_SETTING_FONT_FACE_FOUR:
                id = R.id.read_setting_menu_font_face_four;
                break;
            case ReadSettingFontFaceConfig.READ_SETTING_FONT_FACE_FIVE:
                id = R.id.read_setting_menu_font_face_five;
                break;
            case ReadSettingFontFaceConfig.READ_SETTING_FONT_FACE_SIX:
                id = R.id.read_setting_menu_font_face_six;
                break;
        }
        setReadSettingFontFaceButtonState(id, value);
    }

    private void initReadSettingMenuSpaceClickEvent() {
        readSettingMenuSpaceSmall = (ImageView) findViewById(R.id.reader_read_setting_menu_space_small);
        readSettingMenuSpaceSmall.setOnClickListener(this);

        readSettingMenuSpaceMid = (ImageView) findViewById(R.id.reader_read_setting_menu_space_mid);
        readSettingMenuSpaceMid.setOnClickListener(this);

        readSettingMenuSpaceBig = (ImageView) findViewById(R.id.reader_read_setting_menu_space_big);
        readSettingMenuSpaceBig.setOnClickListener(this);

        //set default value
        int id = R.id.reader_read_setting_menu_space_small;
        int value = ReadSettingSpaceConfig.getReadSettingLineSpace(readerPresenter.getReaderView().getViewContext());
        switch (value) {
            case ReadSettingSpaceConfig.READ_SETTING_SPACE_SMALL:
                id = R.id.reader_read_setting_menu_space_small;
                break;
            case ReadSettingSpaceConfig.READ_SETTING_SPACE_MID:
                id = R.id.reader_read_setting_menu_space_mid;
                break;
            case ReadSettingSpaceConfig.READ_SETTING_SPACE_BIG:
                id = R.id.reader_read_setting_menu_space_big;
                break;
        }
        setReadSettingSpaceButtonState(id, value);
    }

    private void initReadSettingMenuMarginClickEvent() {
        readSettingMenuMarginSmall = (ImageView) findViewById(R.id.reader_read_setting_menu_margin_small);
        readSettingMenuMarginSmall.setOnClickListener(this);

        readSettingMenuMarginMid = (ImageView) findViewById(R.id.reader_read_setting_menu_margin_mid);
        readSettingMenuMarginMid.setOnClickListener(this);

        readSettingMenuMarginBig = (ImageView) findViewById(R.id.reader_read_setting_menu_margin_big);
        readSettingMenuMarginBig.setOnClickListener(this);

        //set default value
        int id = R.id.reader_read_setting_menu_margin_mid;
        int value = ReadSettingMarginConfig.getReadSettingMargin(readerPresenter.getReaderView().getViewContext());
        switch (value) {
            case ReadSettingMarginConfig.READ_SETTING_MARGIN_SMALL:
                id = R.id.reader_read_setting_menu_margin_small;
                break;
            case ReadSettingMarginConfig.READ_SETTING_MARGIN_MID:
                id = R.id.reader_read_setting_menu_margin_mid;
                break;
            case ReadSettingMarginConfig.READ_SETTING_MARGIN_BIG:
                id = R.id.reader_read_setting_menu_margin_big;
                break;
        }
        setReadSettingMarginButtonState(id, value);
    }

    private void initReadSettingFontSizeClickEvent() {
        readSettingFontSizeOne = (ImageView) findViewById(R.id.reader_read_setting_menu_font_one);
        readSettingFontSizeOne.setOnClickListener(this);

        readSettingFontSizeTwo = (ImageView) findViewById(R.id.reader_read_setting_menu_font_two);
        readSettingFontSizeTwo.setOnClickListener(this);

        readSettingFontSizeThree = (ImageView) findViewById(R.id.reader_read_setting_menu_font_three);
        readSettingFontSizeThree.setOnClickListener(this);

        readSettingFontSizeFour = (ImageView) findViewById(R.id.reader_read_setting_menu_font_four);
        readSettingFontSizeFour.setOnClickListener(this);

        readSettingFontSizeFive = (ImageView) findViewById(R.id.reader_read_setting_menu_font_five);
        readSettingFontSizeFive.setOnClickListener(this);

        readSettingFontSizeSix = (ImageView) findViewById(R.id.reader_read_setting_menu_font_six);
        readSettingFontSizeSix.setOnClickListener(this);

        readSettingFontSizeSeven = (ImageView) findViewById(R.id.reader_read_setting_menu_font_seven);
        readSettingFontSizeSeven.setOnClickListener(this);

        //set default value
        int id = R.id.reader_read_setting_menu_font_one;
        int value = ReadSettingFontSizeConfig.getReadSettingFontSize(readerPresenter.getReaderView().getViewContext());
        switch (value) {
            case ReadSettingFontSizeConfig.READ_SETTING_FONT_SIZE_ONE:
                id = R.id.reader_read_setting_menu_font_one;
                break;
            case ReadSettingFontSizeConfig.READ_SETTING_FONT_SIZE_TWO:
                id = R.id.reader_read_setting_menu_font_two;
                break;
            case ReadSettingFontSizeConfig.READ_SETTING_FONT_SIZE_THREE:
                id = R.id.reader_read_setting_menu_font_three;
                break;
            case ReadSettingFontSizeConfig.READ_SETTING_FONT_SIZE_FOUR:
                id = R.id.reader_read_setting_menu_font_four;
                break;
            case ReadSettingFontSizeConfig.READ_SETTING_FONT_SIZE_FIVE:
                id = R.id.reader_read_setting_menu_font_five;
                break;
            case ReadSettingFontSizeConfig.READ_SETTING_FONT_SIZE_SIX:
                id = R.id.reader_read_setting_menu_font_six;
                break;
            case ReadSettingFontSizeConfig.READ_SETTING_FONT_SIZE_SEVEN:
                id = R.id.reader_read_setting_menu_font_seven;
                break;
        }
        setReadSettingFontSizeButtonState(id, value);
    }

    private void setPhysicalKeyButtonState(int id, int value) {
        physicalKeyMenuOne.setChecked(id == R.id.reader_physical_key_menu_one ? true : false);
        physicalKeyMenuTwo.setChecked(id == R.id.reader_physical_key_menu_two ? true : false);
        physicalKeyMenuThree.setChecked(id == R.id.reader_physical_key_menu_three ? true : false);

        ReadPhysicalKeyConfig.saveReadSettingFontFace(readerPresenter.getReaderView().getViewContext(),
                value);
    }

    private void setReadSettingFontFaceButtonState(int id, int value) {
        readSettingMenuFontFaceOne.setChecked(id == R.id.read_setting_menu_font_face_one ? true : false);
        readSettingMenuFontFaceTwo.setChecked(id == R.id.read_setting_menu_font_face_two ? true : false);
        readSettingMenuFontFaceThree.setChecked(id == R.id.read_setting_menu_font_face_three ? true : false);
        readSettingMenuFontFaceFour.setChecked(id == R.id.read_setting_menu_font_face_four ? true : false);
        readSettingMenuFontFaceFive.setChecked(id == R.id.read_setting_menu_font_face_five ? true : false);
        readSettingMenuFontFaceSix.setChecked(id == R.id.read_setting_menu_font_face_six ? true : false);

        ReadSettingFontFaceConfig.saveReadSettingFontFace(readerPresenter, value);
    }

    private void setReadSettingFontSizeButtonState(int id, int value) {
        readSettingFontSizeOne.setSelected(id == R.id.reader_read_setting_menu_font_one ? true : false);
        readSettingFontSizeOne.setImageResource(id == R.id.reader_read_setting_menu_font_one ?
                R.drawable.ic_reader_size_1_white : R.drawable.ic_reader_size_1_black);

        readSettingFontSizeTwo.setSelected(id == R.id.reader_read_setting_menu_font_two ? true : false);
        readSettingFontSizeTwo.setImageResource(id == R.id.reader_read_setting_menu_font_two ?
                R.drawable.ic_reader_size_2_white : R.drawable.ic_reader_size_2_black);

        readSettingFontSizeThree.setSelected(id == R.id.reader_read_setting_menu_font_three ? true : false);
        readSettingFontSizeThree.setImageResource(id == R.id.reader_read_setting_menu_font_three ?
                R.drawable.ic_reader_size_3_white : R.drawable.ic_reader_size_3_black);

        readSettingFontSizeFour.setSelected(id == R.id.reader_read_setting_menu_font_four ? true : false);
        readSettingFontSizeFour.setImageResource(id == R.id.reader_read_setting_menu_font_four ?
                R.drawable.ic_reader_size_4_white : R.drawable.ic_reader_size_4_black);

        readSettingFontSizeFive.setSelected(id == R.id.reader_read_setting_menu_font_five ? true : false);
        readSettingFontSizeFive.setImageResource(id == R.id.reader_read_setting_menu_font_five ?
                R.drawable.ic_reader_size_5_white : R.drawable.ic_reader_size_5_black);

        readSettingFontSizeSix.setSelected(id == R.id.reader_read_setting_menu_font_six ? true : false);
        readSettingFontSizeSix.setImageResource(id == R.id.reader_read_setting_menu_font_six ?
                R.drawable.ic_reader_size_6_white : R.drawable.ic_reader_size_6_black);

        readSettingFontSizeSeven.setSelected(id == R.id.reader_read_setting_menu_font_seven ? true : false);
        readSettingFontSizeSeven.setImageResource(id == R.id.reader_read_setting_menu_font_seven ?
                R.drawable.ic_reader_size_7_white : R.drawable.ic_reader_size_7_black);

        ReadSettingFontSizeConfig.saveReadSettingFontSize(readerPresenter, value);
    }

    private void setReadSettingMarginButtonState(int id, int value) {
        readSettingMenuMarginSmall.setImageResource(id == R.id.reader_read_setting_menu_margin_small ?
                R.drawable.ic_reader_margin_small_bold : R.drawable.ic_reader_margin_small);

        readSettingMenuMarginMid.setImageResource(id == R.id.reader_read_setting_menu_margin_mid ?
                R.drawable.ic_reader_margin_mid_bold : R.drawable.ic_reader_margin_mid);

        readSettingMenuMarginBig.setImageResource(id == R.id.reader_read_setting_menu_margin_big ?
                R.drawable.ic_reader_margin_big_bold : R.drawable.ic_reader_margin_big);

        ReadSettingMarginConfig.saveReadSettingMargin(readerPresenter, value);
    }

    private void setReadSettingSpaceButtonState(int id, int value) {
        readSettingMenuSpaceSmall.setImageResource(id == R.id.reader_read_setting_menu_space_small ?
                R.drawable.ic_reader_line_space_small_bold : R.drawable.ic_reader_line_space_small);

        readSettingMenuSpaceMid.setImageResource(id == R.id.reader_read_setting_menu_space_mid ?
                R.drawable.ic_reader_line_space_mid_bold : R.drawable.ic_reader_line_space_mid);

        readSettingMenuSpaceBig.setImageResource(id == R.id.reader_read_setting_menu_space_big ?
                R.drawable.ic_reader_line_space_big_bold : R.drawable.ic_reader_line_space_big);

        ReadSettingSpaceConfig.saveReadSettingLineSpace(readerPresenter, value);
    }


    private void initCustomizeView() {
        for (int i = 0; i < childIdList.size(); i++) {
            View view = findViewById(childIdList.get(i));
            if (view != null) {
                view.setOnClickListener(this);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int viewID = v.getId();
        Object event = ReaderMainMenuItemEvent.getDefaultMenuItemEvent(viewID);
        if (event != null) {
            EventBus.getDefault().post(event);
        }
        if (R.id.reader_main_menu_top_brightness == viewID) {
            onBrightnessMenuClick(v);
        } else if (R.id.reader_main_menu_top_more == viewID) {
            onMoreMenuClick(v);
        }
    }

    private void onShowAndHideChildMenu(int id) {
        readerPageInfoMenu.setVisibility(id == R.id.reader_page_info_menu_id ? View.VISIBLE : View.GONE);
        readerPhysicalKeySettingMenu.setVisibility(id == R.id.reader_physical_key_setting_menu_id ? View.VISIBLE : View.GONE);
        readerTtsMenu.setVisibility(id == R.id.reader_tts_menu_id ? View.VISIBLE : View.GONE);
        readerReadingSettingMenu.setVisibility(id == R.id.reader_reading_setting_menu_id ? View.VISIBLE : View.GONE);
    }

    private void setMainMenuBottomButtonState(int id) {
        mainMenuBottomPhysicalKey.setSelected(id == R.id.reader_main_menu_bottom_physical_key);
        mainMenuBottomVoice.setSelected(id == R.id.reader_main_menu_bottom_voice);
        mainMenuBottomProgress.setSelected(id == R.id.reader_main_menu_bottom_progress);
        mainMenuBottomReadSetting.setSelected(id == R.id.reader_main_menu_bottom_reading_setting);

        if (id == R.id.reader_main_menu_bottom_voice) {
            isTtsModel();
        }
    }

    private void isTtsModel() {
        readerMainMenuTop.setVisibility(View.GONE);
        readerMainMenuBottom.setVisibility(View.GONE);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        readerPresenter.getHandlerManger().onStop();
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().post(new DisplayStatusBarEvent(true));
        readerPresenter.getBookOperate().redrawPage();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderMainMenuTopBrightnessEvent(ReaderMainMenuTopBrightnessEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderMainMenuTopMoreEvent(ReaderMainMenuTopMoreEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderMainMenuBottomCatalogueEvent(ReaderMainMenuBottomCatalogueEvent event) {
        ReaderDialogManage.onShowBookInfoDialog(readerPresenter, ReaderBookInfoDialogConfig.CATALOG_MODE);
        dismiss();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderMainMenuBottomBookMarkEvent(ReaderMainMenuBottomBookMarkEvent event) {
        ReaderDialogManage.onShowBookInfoDialog(readerPresenter, ReaderBookInfoDialogConfig.BOOKMARK_MODE);
        dismiss();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderMainMenuBottomPhysicalKeyEvent(ReaderMainMenuBottomPhysicalKeyEvent event) {
        onShowAndHideChildMenu(R.id.reader_physical_key_setting_menu_id);
        setMainMenuBottomButtonState(R.id.reader_main_menu_bottom_physical_key);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderMainMenuBottomVoiceEvent(ReaderMainMenuBottomVoiceEvent event) {
        onShowAndHideChildMenu(R.id.reader_tts_menu_id);
        setMainMenuBottomButtonState(R.id.reader_main_menu_bottom_voice);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderMainMenuBottomProgressEvent(ReaderMainMenuBottomProgressEvent event) {
        onShowAndHideChildMenu(R.id.reader_page_info_menu_id);
        setMainMenuBottomButtonState(R.id.reader_main_menu_bottom_progress);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderMainMenuBottomReadingSettingEvent(ReaderMainMenuBottomReadingSettingEvent event) {
        onShowAndHideChildMenu(R.id.reader_reading_setting_menu_id);
        setMainMenuBottomButtonState(R.id.reader_main_menu_bottom_reading_setting);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderMainMenuBottomPdfSettingEvent(ReaderMainMenuBottomPdfSettingEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderReadSettingMenuFontOneEvent(ReaderReadSettingMenuFontOneEvent event) {
        setReadSettingFontSizeButtonState(R.id.reader_read_setting_menu_font_one,
                ReadSettingFontSizeConfig.READ_SETTING_FONT_SIZE_ONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderReadSettingMenuFontTwoEvent(ReaderReadSettingMenuFontTwoEvent event) {
        setReadSettingFontSizeButtonState(R.id.reader_read_setting_menu_font_two,
                ReadSettingFontSizeConfig.READ_SETTING_FONT_SIZE_TWO);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderReadSettingMenuFontThreeEvent(ReaderReadSettingMenuFontThreeEvent event) {
        setReadSettingFontSizeButtonState(R.id.reader_read_setting_menu_font_three,
                ReadSettingFontSizeConfig.READ_SETTING_FONT_SIZE_THREE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderReadSettingMenuFontFourEvent(ReaderReadSettingMenuFontFourEvent event) {
        setReadSettingFontSizeButtonState(R.id.reader_read_setting_menu_font_four,
                ReadSettingFontSizeConfig.READ_SETTING_FONT_SIZE_FOUR);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderReadSettingMenuFontFourEvent(ReaderReadSettingMenuFontFiveEvent event) {
        setReadSettingFontSizeButtonState(R.id.reader_read_setting_menu_font_five,
                ReadSettingFontSizeConfig.READ_SETTING_FONT_SIZE_FIVE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderReadSettingMenuFontSixEvent(ReaderReadSettingMenuFontSixEvent event) {
        setReadSettingFontSizeButtonState(R.id.reader_read_setting_menu_font_six,
                ReadSettingFontSizeConfig.READ_SETTING_FONT_SIZE_SIX);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderReadSettingMenuFontSevenEvent(ReaderReadSettingMenuFontSevenEvent event) {
        setReadSettingFontSizeButtonState(R.id.reader_read_setting_menu_font_seven,
                ReadSettingFontSizeConfig.READ_SETTING_FONT_SIZE_SEVEN);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderReadSettingMenuMarginSmallEvent(ReaderReadSettingMenuMarginSmallEvent event) {
        setReadSettingMarginButtonState(R.id.reader_read_setting_menu_margin_small,
                ReadSettingMarginConfig.READ_SETTING_MARGIN_SMALL);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderReadSettingMenuMarginMidEvent(ReaderReadSettingMenuMarginMidEvent event) {
        setReadSettingMarginButtonState(R.id.reader_read_setting_menu_margin_mid,
                ReadSettingMarginConfig.READ_SETTING_MARGIN_MID);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderReadSettingMenuMarginBigEvent(ReaderReadSettingMenuMarginBigEvent event) {
        setReadSettingMarginButtonState(R.id.reader_read_setting_menu_margin_big,
                ReadSettingMarginConfig.READ_SETTING_MARGIN_BIG);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderReadSettingMenuSpaceSmallEvent(ReaderReadSettingMenuSpaceSmallEvent event) {
        setReadSettingSpaceButtonState(R.id.reader_read_setting_menu_space_small,
                ReadSettingSpaceConfig.READ_SETTING_SPACE_SMALL);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderReadSettingMenuSpaceMidEvent(ReaderReadSettingMenuSpaceMidEvent event) {
        setReadSettingSpaceButtonState(R.id.reader_read_setting_menu_space_mid,
                ReadSettingSpaceConfig.READ_SETTING_SPACE_MID);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderReadSettingMenuSpaceBigEvent(ReaderReadSettingMenuSpaceBigEvent event) {
        setReadSettingSpaceButtonState(R.id.reader_read_setting_menu_space_big,
                ReadSettingSpaceConfig.READ_SETTING_SPACE_BIG);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderReadSettingMenuFontFaceOneEvent(ReaderReadSettingMenuFontFaceOneEvent event) {
        setReadSettingFontFaceButtonState(R.id.read_setting_menu_font_face_one,
                ReadSettingFontFaceConfig.READ_SETTING_FONT_FACE_ONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderReadSettingMenuFontFaceTwoEvent(ReaderReadSettingMenuFontFaceTwoEvent event) {
        setReadSettingFontFaceButtonState(R.id.read_setting_menu_font_face_two,
                ReadSettingFontFaceConfig.READ_SETTING_FONT_FACE_TWO);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderReadSettingMenuFontFaceThreeEvent(ReaderReadSettingMenuFontFaceThreeEvent event) {
        setReadSettingFontFaceButtonState(R.id.read_setting_menu_font_face_three,
                ReadSettingFontFaceConfig.READ_SETTING_FONT_FACE_THREE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderReadSettingMenuFontFaceFourEvent(ReaderReadSettingMenuFontFaceFourEvent event) {
        setReadSettingFontFaceButtonState(R.id.read_setting_menu_font_face_four,
                ReadSettingFontFaceConfig.READ_SETTING_FONT_FACE_FOUR);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderReadSettingMenuFontFaceFiveEvent(ReaderReadSettingMenuFontFaceFiveEvent event) {
        setReadSettingFontFaceButtonState(R.id.read_setting_menu_font_face_five,
                ReadSettingFontFaceConfig.READ_SETTING_FONT_FACE_FIVE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderReadSettingMenuFontFaceSixEvent(ReaderReadSettingMenuFontFaceSixEvent event) {
        setReadSettingFontFaceButtonState(R.id.read_setting_menu_font_face_six,
                ReadSettingFontFaceConfig.READ_SETTING_FONT_FACE_SIX);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderPageInfoMenuBackEvent(ReaderPageInfoMenuBackEvent event) {
        currentPage = initCurrentPage;
        gotoPage();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderPageInfoMenuPrevChapterEvent(ReaderPageInfoMenuPrevChapterEvent event) {
        readerPresenter.getBookOperate().prepareGotoChapter(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderPageInfoMenuNextChapterEvent(ReaderPageInfoMenuNextChapterEvent event) {
        readerPresenter.getBookOperate().prepareGotoChapter(false);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGotoPositionActionResultEvent(GotoPositionActionResultEvent event) {
        currentPage = ReadPageInfo.getCurrentPage(readerPresenter);
        updatePageInfo();
        setTitle();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderPageInfoMenuReadingProcessEvent(ReaderPageInfoMenuReadingProcessEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderMainMenuDialogDismissZoneEvent(ReaderMainMenuDialogDismissZoneEvent event) {
        dismiss();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderPhysicalKeyMenuOneEvent(ReaderPhysicalKeyMenuOneEvent event) {
        setPhysicalKeyButtonState(R.id.reader_physical_key_menu_one,
                ReadPhysicalKeyConfig.READ_Physical_Key_ONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderPhysicalKeyMenuTwoEvent(ReaderPhysicalKeyMenuTwoEvent event) {
        setPhysicalKeyButtonState(R.id.reader_physical_key_menu_two,
                ReadPhysicalKeyConfig.READ_Physical_Key_TWO);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderPhysicalKeyMenuThreeEvent(ReaderPhysicalKeyMenuThreeEvent event) {
        setPhysicalKeyButtonState(R.id.reader_physical_key_menu_three,
                ReadPhysicalKeyConfig.READ_Physical_Key_THREE);
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
    public void onReaderMainMenuTopShelfEvent(ReaderMainMenuTopShelfEvent event) {
        dismiss();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderMainMenuTopBookStoreEvent(ReaderMainMenuTopBookStoreEvent event) {
        dismiss();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderMainMenuTopUserEvent(ReaderMainMenuTopUserEvent event) {
        dismiss();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderMainMenuTopSearchEvent(ReaderMainMenuTopSearchEvent event) {
        dismiss();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderMainMenuTopBackEvent(ReaderMainMenuTopBackEvent event) {
        dismiss();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderMenuMorePressEvent(ReaderMenuMorePressEvent event) {
        dismiss();
    }

    private void onBrightnessMenuClick(View view) {
        ReaderMainMenuTopBrightnessEvent event = new ReaderMainMenuTopBrightnessEvent();
        event.setView(view);
        event.setOffset(view.getHeight());
        EventBus.getDefault().post(event);
    }

    private void onMoreMenuClick(View view) {
        ReaderMainMenuTopMoreEvent event = new ReaderMainMenuTopMoreEvent();
        event.setView(view);
        EventBus.getDefault().post(event);
    }
}
