package com.onyx.android.sdk.data.model;

import com.onyx.android.sdk.data.db.ContentDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by ming on 16/8/8.
 */
@Table(database = ContentDatabase.class)
public class SearchHistory extends BaseData {

    @Column
    private String content = null;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
