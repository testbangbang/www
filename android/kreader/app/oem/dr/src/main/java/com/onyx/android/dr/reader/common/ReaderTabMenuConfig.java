package com.onyx.android.dr.reader.common;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.data.ReaderMenuBean;
import com.onyx.android.dr.device.DeviceConfig;
import com.onyx.android.dr.reader.event.ReaderAfterReadingMenuEvent;
import com.onyx.android.dr.reader.event.ReaderAnnotationMenuEvent;
import com.onyx.android.dr.reader.event.ReaderGoodSentenceMenuEvent;
import com.onyx.android.dr.reader.event.ReaderListenMenuEvent;
import com.onyx.android.dr.reader.event.ReaderPostilMenuEvent;
import com.onyx.android.dr.reader.event.ReaderWordQueryMenuEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by hehai on 17-6-28.
 */

public class ReaderTabMenuConfig {
    private static List<ReaderMenuBean> defaultReaderMenus = new ArrayList<>();

    static {
        ReaderMenuBean readerMenuBean = new ReaderMenuBean(DeviceConfig.ReaderMenuInfo.MENU_POSTIL, DRApplication.getInstance().getString(R.string.postil), R.drawable.ic_annotation, new ReaderPostilMenuEvent());
        defaultReaderMenus.add(readerMenuBean);

        readerMenuBean = new ReaderMenuBean(DeviceConfig.ReaderMenuInfo.MENU_MARK, DRApplication.getInstance().getString(R.string.annotation), R.drawable.ic_annotation, new ReaderAnnotationMenuEvent());
        defaultReaderMenus.add(readerMenuBean);

        readerMenuBean = new ReaderMenuBean(DeviceConfig.ReaderMenuInfo.MENU_WORD_QUERY, DRApplication.getInstance().getString(R.string.word_query), R.drawable.ic_word_query, new ReaderWordQueryMenuEvent());
        defaultReaderMenus.add(readerMenuBean);

        readerMenuBean = new ReaderMenuBean(DeviceConfig.ReaderMenuInfo.MENU_GOOD_SENTENCE_EXTRACT, DRApplication.getInstance().getString(R.string.good_sentence_excerpt), R.drawable.ic_good_sentence, new ReaderGoodSentenceMenuEvent());
        defaultReaderMenus.add(readerMenuBean);

        readerMenuBean = new ReaderMenuBean(DeviceConfig.ReaderMenuInfo.MENU_LISTEN_BOOKS, DRApplication.getInstance().getString(R.string.listen_books), R.drawable.ic_listen, new ReaderListenMenuEvent());
        defaultReaderMenus.add(readerMenuBean);

        readerMenuBean = new ReaderMenuBean(DeviceConfig.ReaderMenuInfo.MENU_AFTER_READING, DRApplication.getInstance().getString(R.string.after_reading), R.drawable.ic_essay, new ReaderAfterReadingMenuEvent());
        defaultReaderMenus.add(readerMenuBean);
    }


    public static List<ReaderMenuBean> getMenuData() {
        getJsonConfig(defaultReaderMenus);
        return defaultReaderMenus;
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
