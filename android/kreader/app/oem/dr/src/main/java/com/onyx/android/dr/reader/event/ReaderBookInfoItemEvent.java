package com.onyx.android.dr.reader.event;


import com.onyx.android.dr.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by huxiaomao on 17/5/10.
 */

public class ReaderBookInfoItemEvent {
    private static Map<Integer, Object> defaultMenuItemEventList = new HashMap<>();

    public static void bindItemEvent() {
        bindReaderBookInfoTabItemEvent();
    }

    private static void bindReaderBookInfoTabItemEvent(){
        defaultMenuItemEventList.put(R.id.book_info_catalog, new ReaderBookInfoCatalogEvent());
        defaultMenuItemEventList.put(R.id.book_info_bookmark, new ReaderBookInfoBookmarkEvent());
        defaultMenuItemEventList.put(R.id.book_info_note, new ReaderBookInfoNoteEvent());
    }

    public static Object getItemEvent(int viewID){
        return defaultMenuItemEventList.get(viewID);
    }
}
