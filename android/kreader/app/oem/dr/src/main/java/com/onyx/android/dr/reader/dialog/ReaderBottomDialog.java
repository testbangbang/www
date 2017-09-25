package com.onyx.android.dr.reader.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.data.ReaderMenuBean;
import com.onyx.android.dr.reader.adapter.ReaderTabMenuAdapter;
import com.onyx.android.dr.reader.common.ReadSettingFontFaceConfig;
import com.onyx.android.dr.reader.common.ReadSettingFontSizeConfig;
import com.onyx.android.dr.reader.common.ReadSettingMarginConfig;
import com.onyx.android.dr.reader.common.ReadSettingSpaceConfig;
import com.onyx.android.dr.reader.common.ReadSettingTtsConfig;
import com.onyx.android.dr.reader.common.ReaderBookInfoDialogConfig;
import com.onyx.android.dr.reader.common.ReaderTabMenuConfig;
import com.onyx.android.dr.reader.common.ToastManage;
import com.onyx.android.dr.reader.event.AfterReadingMenuEvent;
import com.onyx.android.dr.reader.event.ChangeSpeechRateEvent;
import com.onyx.android.dr.reader.event.NotifyTtsStateChangedEvent;
import com.onyx.android.dr.reader.event.ReaderAfterReadingMenuEvent;
import com.onyx.android.dr.reader.event.ReaderAnnotationMenuEvent;
import com.onyx.android.dr.reader.event.ReaderCategoryMenuEvent;
import com.onyx.android.dr.reader.event.ReaderFontMenuEvent;
import com.onyx.android.dr.reader.event.ReaderListenMenuEvent;
import com.onyx.android.dr.reader.event.ReaderMainMenuItemEvent;
import com.onyx.android.dr.reader.event.ReaderPostilMenuEvent;
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
import com.onyx.android.dr.reader.event.ReaderSettingMenuEvent;
import com.onyx.android.dr.reader.event.ReaderTTSMenuPlayEvent;
import com.onyx.android.dr.reader.event.ReaderTTSMenuQuitReadingEvent;
import com.onyx.android.dr.reader.event.ReadingSummaryMenuEvent;
import com.onyx.android.dr.reader.event.ScribbleMenuEvent;
import com.onyx.android.dr.reader.event.TtsSpeakingStateEvent;
import com.onyx.android.dr.reader.event.TtsStopStateEvent;
import com.onyx.android.dr.reader.handler.HandlerManger;
import com.onyx.android.dr.reader.presenter.ReaderPresenter;
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
    private View readerReadingSettingMenu;
    private int fontFaceValue;
    private int fontSizeValue;
    private int settingMarginValue;
    private int settingLineValue;

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

    private ImageView readSettingFontSizeOne;
    private ImageView readSettingFontSizeTwo;
    private ImageView readSettingFontSizeThree;
    private ImageView readSettingFontSizeFour;
    private ImageView readSettingFontSizeFive;
    private ImageView readSettingFontSizeSix;
    private ImageView readSettingFontSizeSeven;

    private ImageView readSettingMenuSpaceSmall;
    private ImageView readSettingMenuSpaceMid;
    private ImageView readSettingMenuSpaceBig;

    private ImageView readSettingMenuMarginSmall;
    private ImageView readSettingMenuMarginMid;
    private ImageView readSettingMenuMarginBig;

    private RadioButton readSettingMenuFontFaceOne;
    private RadioButton readSettingMenuFontFaceTwo;
    private RadioButton readSettingMenuFontFaceThree;
    private RadioButton readSettingMenuFontFaceFour;
    private RadioButton readSettingMenuFontFaceFive;
    private RadioButton readSettingMenuFontFaceSix;

    private View readSettingCancel;
    private View readSettingConfirm;

    private AudioManager audioManager;
    private View readerTtsMenu;
    private SeekBar ttsMenuSpeechRate;
    private SeekBar ttsMenuVolume;
    private ImageView ttsMenuPlay;
    private int speechRate = 0;
    private int volume = 0;
    private DialogAnnotation.AnnotationAction action;
    private ImageView tabMenuPrev;
    private ImageView tabMenuNext;

    public ReaderBottomDialog(ReaderPresenter readerPresenter, @NonNull Context context, int layoutID, List<Integer> childIdList) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.readerPresenter = readerPresenter;
        this.childIdList = childIdList;
        setCanceledOnTouchOutside(true);
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

        dismissZone = findViewById(R.id.dismiss_zone);
        dismissZone.setOnClickListener(this);
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
        initChildMenu();
        initTtsMenuItemClickEvent();
        initReadSettingFontSizeClickEvent();
        initReadSettingMenuMarginClickEvent();
        initReadSettingMenuSpaceClickEvent();
        initReadSettingMenuFontFaceClickEvent();
        initReadSettingMenuSaveClickEvent();
    }

    private void initReadSettingMenuSaveClickEvent() {
        readSettingCancel = findViewById(R.id.read_setting_menu_cancel);
        readSettingConfirm = findViewById(R.id.read_setting_menu_confirm);
        readSettingCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readerReadingSettingMenu.setVisibility(View.GONE);
                dismissZone.setVisibility(View.VISIBLE);
            }
        });

        readSettingConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveReadSetting();
                readerReadingSettingMenu.setVisibility(View.GONE);
                dismissZone.setVisibility(View.VISIBLE);
            }
        });
    }

    private void saveReadSetting() {
        ReadSettingFontFaceConfig.saveReadSettingFontFace(readerPresenter, fontFaceValue);
        ReadSettingFontSizeConfig.saveReadSettingFontSize(readerPresenter, fontSizeValue);
        ReadSettingMarginConfig.saveReadSettingMargin(readerPresenter, settingMarginValue);
        ReadSettingSpaceConfig.saveReadSettingLineSpace(readerPresenter, settingLineValue);
    }

    private void initChildMenu() {
        readerReadingSettingMenu = findViewById(R.id.reader_reading_setting_menu_id);
    }

    public void setReaderTabMenu(List<ReaderMenuBean> menuData) {
        readerTabMenuAdapter.setMenuDataList(menuData);
        readerTabMenuAdapter.notifyDataSetChanged();
        tabMenuNext.setVisibility(menuData.size() > readerTabMenuAdapter.getColumnCount() ? View.VISIBLE : View.GONE);
    }

    private void initCustomizeView() {
        for (int i = 0; i < childIdList.size(); i++) {
            View view = findViewById(childIdList.get(i));
            if (view != null) {
                view.setOnClickListener(this);
            }
        }
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

    private void setReadSettingFontFaceButtonState(int id, int value) {
        readSettingMenuFontFaceOne.setChecked(id == R.id.read_setting_menu_font_face_one);
        readSettingMenuFontFaceTwo.setChecked(id == R.id.read_setting_menu_font_face_two);
        readSettingMenuFontFaceThree.setChecked(id == R.id.read_setting_menu_font_face_three);
        readSettingMenuFontFaceFour.setChecked(id == R.id.read_setting_menu_font_face_four);
        readSettingMenuFontFaceFive.setChecked(id == R.id.read_setting_menu_font_face_five);
        readSettingMenuFontFaceSix.setChecked(id == R.id.read_setting_menu_font_face_six);

        fontFaceValue = value;
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

        fontSizeValue = value;
    }

    private void setReadSettingMarginButtonState(int id, int value) {
        readSettingMenuMarginSmall.setImageResource(id == R.id.reader_read_setting_menu_margin_small ?
                R.drawable.ic_reader_margin_small_bold : R.drawable.ic_reader_margin_small);

        readSettingMenuMarginMid.setImageResource(id == R.id.reader_read_setting_menu_margin_mid ?
                R.drawable.ic_reader_margin_mid_bold : R.drawable.ic_reader_margin_mid);

        readSettingMenuMarginBig.setImageResource(id == R.id.reader_read_setting_menu_margin_big ?
                R.drawable.ic_reader_margin_big_bold : R.drawable.ic_reader_margin_big);
        settingMarginValue = value;
    }

    private void setReadSettingSpaceButtonState(int id, int value) {
        readSettingMenuSpaceSmall.setImageResource(id == R.id.reader_read_setting_menu_space_small ?
                R.drawable.ic_reader_line_space_small_bold : R.drawable.ic_reader_line_space_small);

        readSettingMenuSpaceMid.setImageResource(id == R.id.reader_read_setting_menu_space_mid ?
                R.drawable.ic_reader_line_space_mid_bold : R.drawable.ic_reader_line_space_mid);

        readSettingMenuSpaceBig.setImageResource(id == R.id.reader_read_setting_menu_space_big ?
                R.drawable.ic_reader_line_space_big_bold : R.drawable.ic_reader_line_space_big);

        settingLineValue = value;
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnReaderFontMenuEvent(ReaderFontMenuEvent event) {
        readerReadingSettingMenu.setVisibility(readerReadingSettingMenu.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        dismissZone.setVisibility(View.INVISIBLE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnReaderCategoryMenuEvent(ReaderCategoryMenuEvent event) {
        ReaderDialogManage.onShowBookInfoDialog(readerPresenter, ReaderBookInfoDialogConfig.CATALOG_MODE);
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
    public void onReadingSummaryMenuEvent(ReadingSummaryMenuEvent event) {
        ToastManage.showMessage(getContext(), getContext().getString(R.string.read_summary));
        String[] strings = new String[2];
        strings[0] = readerPresenter.getBookInfo().getBookName();
        strings[1] = String.valueOf(readerPresenter.getReaderViewInfo().getFirstVisiblePage().getName());
        ActivityManager.startReadSummaryActivity(getContext(), strings);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAfterReadingMenuEvent(AfterReadingMenuEvent event) {
        ToastManage.showMessage(getContext(), getContext().getString(R.string.after_reading));
        String bookName = readerPresenter.getBookInfo().getBookName();
        Intent intent = new Intent();
        intent.putExtra(Constants.BOOK_NAME, readerPresenter.getBookInfo().getBookName());
        intent.putExtra(Constants.BOOK_PAGE, String.valueOf(readerPresenter.getReaderViewInfo().getFirstVisiblePage().getName()));
        intent.putExtra(Constants.BOOK_ID, readerPresenter.getBookInfo().getBookId());
        ActivityManager.startReadingReportActivity(getContext(), intent);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScribbleMenuEvent(ScribbleMenuEvent event) {
        // TODO: 17-9-15  
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_back:
                dismiss();
                break;
            case R.id.dismiss_zone:
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

    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }
}
