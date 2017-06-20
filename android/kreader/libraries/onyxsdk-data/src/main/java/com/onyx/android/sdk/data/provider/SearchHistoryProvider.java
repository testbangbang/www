package com.onyx.android.sdk.data.provider;

import com.onyx.android.sdk.data.model.SearchHistory;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.onyx.android.sdk.data.model.SearchHistory_Table;

import java.util.List;

/**
 * Created by ming on 16/8/8.
 */
public class SearchHistoryProvider {

    public static final List<SearchHistory> getLatestSearchHistory(String md5, int count){
        return new Select().from(SearchHistory.class).where(SearchHistory_Table.idString.eq(md5))
                .limit(count)
                .orderBy(SearchHistory_Table.updatedAt,false)
                .queryList();
    }

    public static void addSearchHistory(final SearchHistory searchHistory) {
        searchHistory.save();
    }

    public static void deleteSearchHistory(String md5) {
        SQLite.delete(SearchHistory.class).where(SearchHistory_Table.idString.eq(md5)).query();
    }

    public static SearchHistory getSearchHistory(String md5,String content){
        return new Select().from(SearchHistory.class).where(SearchHistory_Table.idString.eq(md5))
                .and(SearchHistory_Table.content.eq(content))
                .querySingle();
    }

}
