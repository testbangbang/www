package com.onyx.android.dr.reader.common;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.data.ReaderMenuBean;
import com.onyx.android.dr.device.DeviceConfig;
import com.onyx.android.dr.reader.event.AfterReadingMenuEvent;
import com.onyx.android.dr.reader.event.CropToPageMenuEvent;
import com.onyx.android.dr.reader.event.CropWidthPageMenuEvent;
import com.onyx.android.dr.reader.event.ReaderAfterReadingMenuEvent;
import com.onyx.android.dr.reader.event.ReaderAnnotationMenuEvent;
import com.onyx.android.dr.reader.event.ReaderCategoryMenuEvent;
import com.onyx.android.dr.reader.event.ReaderFitPageMenuEvent;
import com.onyx.android.dr.reader.event.ReaderFontMenuEvent;
import com.onyx.android.dr.reader.event.ReaderGoodSentenceMenuEvent;
import com.onyx.android.dr.reader.event.ReaderListenMenuEvent;
import com.onyx.android.dr.reader.event.ReaderPostilMenuEvent;
import com.onyx.android.dr.reader.event.ReaderSettingMenuEvent;
import com.onyx.android.dr.reader.event.ReaderWordQueryMenuEvent;
import com.onyx.android.dr.reader.event.ReadingSummaryMenuEvent;
import com.onyx.android.dr.reader.event.ScribbleMenuEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by hehai on 17-6-28.
 */

public class ReaderTabMenuConfig {
    public List<ReaderMenuBean> defaultReaderMenus = new ArrayList<>();
    private static List<ReaderMenuBean> afterReaderMenus = new ArrayList<>();

    public void loadMenuData() {
        ReaderMenuBean readerMenuBean = new ReaderMenuBean(DeviceConfig.ReaderMenuInfo.MENU_READER_FONT, DRApplication.getInstance().getString(R.string.font), R.drawable.ic_reader_menu_font, new ReaderFontMenuEvent());
        defaultReaderMenus.add(readerMenuBean);

        readerMenuBean = new ReaderMenuBean(DeviceConfig.ReaderMenuInfo.MENU_READER_FIT_PAGE, DRApplication.getInstance().getString(R.string.fit_page), R.drawable.ic_dialog_reader_menu_scale_fit_page, new ReaderFitPageMenuEvent());
        defaultReaderMenus.add(readerMenuBean);

        readerMenuBean = new ReaderMenuBean(DeviceConfig.ReaderMenuInfo.MENU_READER_CROP_TO_PAGE, DRApplication.getInstance().getString(R.string.reader_layer_menu_zoom_by_crop_page), R.drawable.ic_dialog_reader_menu_scale_cut_four, new CropToPageMenuEvent());
        defaultReaderMenus.add(readerMenuBean);

        readerMenuBean = new ReaderMenuBean(DeviceConfig.ReaderMenuInfo.MENU_READER_CROP_WIDTH, DRApplication.getInstance().getString(R.string.reader_layer_menu_zoom_by_crop_width), R.drawable.ic_dialog_reader_menu_scale_cut_two, new CropWidthPageMenuEvent());

        readerMenuBean = new ReaderMenuBean(DeviceConfig.ReaderMenuInfo.MENU_READER_CATALOG, DRApplication.getInstance().getString(R.string.catalog), R.drawable.ic_reader_menu_list, new ReaderCategoryMenuEvent());
        defaultReaderMenus.add(readerMenuBean);

        readerMenuBean = new ReaderMenuBean(DeviceConfig.ReaderMenuInfo.MENU_LISTEN_BOOKS, DRApplication.getInstance().getString(R.string.listen_books), R.drawable.ic_reader_menu_tts, new ReaderListenMenuEvent());
        defaultReaderMenus.add(readerMenuBean);

        readerMenuBean = new ReaderMenuBean(DeviceConfig.AfterReadingInfo.MENU_READING_SUMMARY, DRApplication.getInstance().getString(R.string.read_summary), R.drawable.ic_reader_top_main, new ReadingSummaryMenuEvent());
        defaultReaderMenus.add(readerMenuBean);

        readerMenuBean = new ReaderMenuBean(DeviceConfig.AfterReadingInfo.MENU_AFTER_READING, DRApplication.getInstance().getString(R.string.after_reading), R.drawable.ic_reader_menu_idea, new AfterReadingMenuEvent());
        defaultReaderMenus.add(readerMenuBean);

        readerMenuBean = new ReaderMenuBean(DeviceConfig.ReaderMenuInfo.MENU_WRITE_REMARKS, DRApplication.getInstance().getString(R.string.write_remarks), R.drawable.ic_reader_menu_idea, new ScribbleMenuEvent());
        defaultReaderMenus.add(readerMenuBean);

        readerMenuBean = new ReaderMenuBean(DeviceConfig.ReaderMenuInfo.MENU_POSTIL, DRApplication.getInstance().getString(R.string.postil), R.drawable.ic_postil, new ReaderPostilMenuEvent());

        readerMenuBean = new ReaderMenuBean(DeviceConfig.ReaderMenuInfo.MENU_MARK, DRApplication.getInstance().getString(R.string.annotation), R.drawable.ic_postil, new ReaderAnnotationMenuEvent());

        readerMenuBean = new ReaderMenuBean(DeviceConfig.ReaderMenuInfo.MENU_WORD_QUERY, DRApplication.getInstance().getString(R.string.new_word_query), R.drawable.ic_word_query, new ReaderWordQueryMenuEvent());

        readerMenuBean = new ReaderMenuBean(DeviceConfig.ReaderMenuInfo.MENU_GOOD_SENTENCE_EXTRACT, DRApplication.getInstance().getString(R.string.good_sentence_excerpt), R.drawable.ic_good_sentence, new ReaderGoodSentenceMenuEvent());

        readerMenuBean = new ReaderMenuBean(DeviceConfig.ReaderMenuInfo.MENU_AFTER_READING, DRApplication.getInstance().getString(R.string.after_reading), R.drawable.ic_essay, new ReaderAfterReadingMenuEvent());

        readerMenuBean = new ReaderMenuBean(DeviceConfig.ReaderMenuInfo.MENU_READER_SETTING, DRApplication.getInstance().getString(R.string.read_setting), R.drawable.ic_settings, new ReaderSettingMenuEvent());
    }


    public List<ReaderMenuBean> getMenuData() {
        getJsonConfig(defaultReaderMenus);
        return defaultReaderMenus;
    }

    public static List<ReaderMenuBean> getAfterReaderMenus() {
        return afterReaderMenus;
    }

    private static void getJsonConfig(List<ReaderMenuBean> menuData) {
        Iterator<ReaderMenuBean> it = menuData.iterator();
        while (it.hasNext()) {
            ReaderMenuBean next = it.next();
            if (!DeviceConfig.sharedInstance(DRApplication.getInstance()).getReaderMenuItem(next.getTabKey())) {
                it.remove();
            }
        }
    }
}
