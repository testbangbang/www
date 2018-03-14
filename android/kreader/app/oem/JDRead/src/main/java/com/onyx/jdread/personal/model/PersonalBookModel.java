package com.onyx.jdread.personal.model;

import android.content.res.TypedArray;

import com.onyx.android.sdk.data.GPaginator;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.model.Metadata_Table;
import com.onyx.android.sdk.data.utils.QueryBuilder;
import com.onyx.jdread.R;
import com.onyx.jdread.library.model.PopMenuModel;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.personal.event.FilterAllEvent;
import com.onyx.jdread.personal.event.FilterEvent;
import com.onyx.jdread.personal.event.FilterHaveBoughtEvent;
import com.onyx.jdread.personal.event.FilterReadVipEvent;
import com.onyx.jdread.personal.event.FilterSelfImportEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by li on 2018/1/4.
 */

public class PersonalBookModel {
    private List<PopMenuModel> menus = new ArrayList<>();
    private GPaginator queryPagination;
    private int queryLimit = 5;
    private final QueryArgs queryArgs;

    public PersonalBookModel() {
        queryArgs = QueryBuilder.allBooksQuery(SortBy.None, SortOrder.Asc);
        queryArgs.conditionGroup.and(Metadata_Table.cloudId.isNull());
        queryArgs.limit = queryLimit;
    }

    public QueryArgs getQueryArgs(int offset) {
        queryArgs.offset = offset;
        return queryArgs;
    }

    public int getOffset(int currentPage) {
        int itemsPerPage = queryPagination.itemsPerPage();
        int offset = currentPage * itemsPerPage;
        return offset;
    }

    public int getOffset() {
        return getOffset(queryPagination.getCurrentPage());
    }

    public void setQueryPagination(GPaginator pagination) {
        this.queryPagination = pagination;
    }

    private FilterEvent[] filterEvents = new FilterEvent[]{
            new FilterAllEvent(),
            new FilterHaveBoughtEvent(),
            new FilterReadVipEvent(),
            new FilterSelfImportEvent()
    };

    public void loadPopupData() {
        String[] bookFilters = ResManager.getStringArray(R.array.book_filter);
        TypedArray typedArray = ResManager.getTypedArray(R.array.book_filter);
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
