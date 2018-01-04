package com.onyx.jdread.personal.model;

import android.content.res.TypedArray;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.library.model.PopMenuModel;
import com.onyx.jdread.personal.event.FilterEvent;
import com.onyx.jdread.personal.event.FilterHaveBoughtEvent;
import com.onyx.jdread.personal.event.FilterAllEvent;
import com.onyx.jdread.personal.event.FilterReadVipEvent;
import com.onyx.jdread.personal.event.FilterSelfImportEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by li on 2018/1/4.
 */

public class PersonalBookModel {
    private List<PopMenuModel> menus = new ArrayList<>();
    private FilterEvent[] filterEvents = new FilterEvent[] {
            new FilterAllEvent(),
            new FilterHaveBoughtEvent(),
            new FilterReadVipEvent(),
            new FilterSelfImportEvent()
    };

    public void loadPopupData() {
        String[] bookFilters = JDReadApplication.getInstance().getResources().getStringArray(R.array.book_filter);
        TypedArray typedArray = JDReadApplication.getInstance().getResources().obtainTypedArray(R.array.book_filter);
        int length = typedArray.length();
        int[] resIds = new int[length];
        for (int i = 0; i < bookFilters.length; i++) {
            resIds[i] = typedArray.getResourceId(i, 0);
            FilterEvent filterEvent = filterEvents[i];
            filterEvent.setResId(resIds[i]);
            PopMenuModel model = new PopMenuModel(bookFilters[i], filterEvent);
            menus.add(model);
        }
        typedArray.recycle();
    }

    public List<PopMenuModel> getMenus() {
        return menus;
    }
}
