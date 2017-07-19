package com.onyx.android.dr.reader.event;


import com.onyx.android.dr.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by huxiaomao on 17/5/10.
 */

public class ReaderMainMenuItemEvent {
    private static Map<Integer, Object> defaultMenuItemEventList = new HashMap<>();

    public static void bindReaderDefaultMenuItemEvent() {
        defaultMenuItemEventList.put(R.id.dismiss_zone,new ReaderMainMenuDialogDismissZoneEvent());

        bindReaderMainMenuTopItemEvent();
        bindReaderMainMenuBottomItemEvent();
        bindReaderPageInfoMenuItemEvent();
        bindReaderPhysicalKeyMenuItemEvent();
        bindReaderTtsMenuItemEvent();

        bindReadSettingMenuItemEvent();

    }

    private static void bindReadSettingMenuItemEvent(){
        defaultMenuItemEventList.put(R.id.reader_read_setting_menu_font_one, new ReaderReadSettingMenuFontOneEvent());
        defaultMenuItemEventList.put(R.id.reader_read_setting_menu_font_two, new ReaderReadSettingMenuFontTwoEvent());
        defaultMenuItemEventList.put(R.id.reader_read_setting_menu_font_three, new ReaderReadSettingMenuFontThreeEvent());
        defaultMenuItemEventList.put(R.id.reader_read_setting_menu_font_four, new ReaderReadSettingMenuFontFourEvent());
        defaultMenuItemEventList.put(R.id.reader_read_setting_menu_font_five, new ReaderReadSettingMenuFontFiveEvent());
        defaultMenuItemEventList.put(R.id.reader_read_setting_menu_font_six, new ReaderReadSettingMenuFontSixEvent());
        defaultMenuItemEventList.put(R.id.reader_read_setting_menu_font_seven, new ReaderReadSettingMenuFontSevenEvent());

        defaultMenuItemEventList.put(R.id.reader_read_setting_menu_margin_small, new ReaderReadSettingMenuMarginSmallEvent());
        defaultMenuItemEventList.put(R.id.reader_read_setting_menu_margin_mid, new ReaderReadSettingMenuMarginMidEvent());
        defaultMenuItemEventList.put(R.id.reader_read_setting_menu_margin_big, new ReaderReadSettingMenuMarginBigEvent());

        defaultMenuItemEventList.put(R.id.reader_read_setting_menu_space_small, new ReaderReadSettingMenuSpaceSmallEvent());
        defaultMenuItemEventList.put(R.id.reader_read_setting_menu_space_mid, new ReaderReadSettingMenuSpaceMidEvent());
        defaultMenuItemEventList.put(R.id.reader_read_setting_menu_space_big, new ReaderReadSettingMenuSpaceBigEvent());

        defaultMenuItemEventList.put(R.id.read_setting_menu_font_face_one, new ReaderReadSettingMenuFontFaceOneEvent());
        defaultMenuItemEventList.put(R.id.read_setting_menu_font_face_two, new ReaderReadSettingMenuFontFaceTwoEvent());
        defaultMenuItemEventList.put(R.id.read_setting_menu_font_face_three, new ReaderReadSettingMenuFontFaceThreeEvent());
        defaultMenuItemEventList.put(R.id.read_setting_menu_font_face_four, new ReaderReadSettingMenuFontFaceFourEvent());
        defaultMenuItemEventList.put(R.id.read_setting_menu_font_face_five, new ReaderReadSettingMenuFontFaceFiveEvent());
        defaultMenuItemEventList.put(R.id.read_setting_menu_font_face_six, new ReaderReadSettingMenuFontFaceSixEvent());
    }

    private static void bindReaderTtsMenuItemEvent(){
        defaultMenuItemEventList.put(R.id.reader_tts_menu_speed_rate, new ReaderTTSMenuSpeedSpeechEvent());
        defaultMenuItemEventList.put(R.id.reader_tts_menu_volume, new ReaderTTSMenuVolumeEvent());
        defaultMenuItemEventList.put(R.id.reader_tts_menu_play, new ReaderTTSMenuPlayEvent());
        defaultMenuItemEventList.put(R.id.reader_tts_menu_quit_reading, new ReaderTTSMenuQuitReadingEvent());
    }

    private static void bindReaderPhysicalKeyMenuItemEvent(){
        defaultMenuItemEventList.put(R.id.reader_physical_key_menu_one, new ReaderPhysicalKeyMenuOneEvent());
        defaultMenuItemEventList.put(R.id.reader_physical_key_menu_two, new ReaderPhysicalKeyMenuTwoEvent());
        defaultMenuItemEventList.put(R.id.reader_physical_key_menu_three, new ReaderPhysicalKeyMenuThreeEvent());
    }

    private static void bindReaderPageInfoMenuItemEvent(){
        defaultMenuItemEventList.put(R.id.reader_page_info_menu_back, new ReaderPageInfoMenuBackEvent());
        defaultMenuItemEventList.put(R.id.reader_page_info_menu_prev_chapter, new ReaderPageInfoMenuPrevChapterEvent());
        defaultMenuItemEventList.put(R.id.reader_page_info_menu_next_chapter, new ReaderPageInfoMenuNextChapterEvent());
        defaultMenuItemEventList.put(R.id.reader_page_info_menu_read_progress, new ReaderPageInfoMenuReadingProcessEvent());
    }

    private static void bindReaderMainMenuBottomItemEvent(){
        defaultMenuItemEventList.put(R.id.reader_main_menu_bottom_catalogue, new ReaderMainMenuBottomCatalogueEvent());
        defaultMenuItemEventList.put(R.id.reader_main_menu_bottom_bookmark, new ReaderMainMenuBottomBookMarkEvent());
        defaultMenuItemEventList.put(R.id.reader_main_menu_bottom_physical_key, new ReaderMainMenuBottomPhysicalKeyEvent());
        defaultMenuItemEventList.put(R.id.reader_main_menu_bottom_voice, new ReaderMainMenuBottomVoiceEvent());
        defaultMenuItemEventList.put(R.id.reader_main_menu_bottom_progress, new ReaderMainMenuBottomProgressEvent());
        defaultMenuItemEventList.put(R.id.reader_main_menu_bottom_reading_setting, new ReaderMainMenuBottomReadingSettingEvent());
        defaultMenuItemEventList.put(R.id.reader_main_menu_bottom_pdf_setting, new ReaderMainMenuBottomPdfSettingEvent());
    }

    private static void bindReaderMainMenuTopItemEvent(){
        defaultMenuItemEventList.put(R.id.reader_main_menu_top_shelf, new ReaderMainMenuTopShelfEvent());
        defaultMenuItemEventList.put(R.id.reader_main_menu_top_book_store, new ReaderMainMenuTopBookStoreEvent());
        defaultMenuItemEventList.put(R.id.reader_main_menu_top_user, new ReaderMainMenuTopUserEvent());
        defaultMenuItemEventList.put(R.id.reader_main_menu_top_search, new ReaderMainMenuTopSearchEvent(null));
        defaultMenuItemEventList.put(R.id.reader_main_menu_top_back, new ReaderMainMenuTopBackEvent());
    }

    public static Object getDefaultMenuItemEvent(int viewID){
        return defaultMenuItemEventList.get(viewID);
    }
}
