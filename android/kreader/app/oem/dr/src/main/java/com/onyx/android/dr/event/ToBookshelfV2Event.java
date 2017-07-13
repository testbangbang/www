package com.onyx.android.dr.event;

import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.model.Metadata;

/**
 * Created by hehai on 17-7-11.
 */

public class ToBookshelfV2Event {
    QueryArgs args;
    private String title;

    public QueryArgs getArgs() {
        return args;
    }

    public void setArgs(QueryArgs args) {
        this.args = args;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
