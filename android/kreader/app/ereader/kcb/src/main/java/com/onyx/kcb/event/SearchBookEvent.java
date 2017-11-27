package com.onyx.kcb.event;

import com.onyx.android.sdk.data.QueryArgs;

/**
 * Created by hehai on 17-11-24.
 */

public class SearchBookEvent {
    private QueryArgs queryArgs;

    public SearchBookEvent(QueryArgs queryArgs) {
        this.queryArgs = queryArgs;
    }

    public QueryArgs getQueryArgs() {
        return queryArgs;
    }
}
