package com.onyx.kreader.ui.actions;

import android.database.Cursor;
import android.net.Uri;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.kreader.R;
import com.onyx.kreader.host.request.ContentResolverQueryRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by ming on 16/10/14.
 */

public class DictionaryQueryAction extends BaseAction {

    private String token;
    private String expString = "";

    public DictionaryQueryAction(String token) {
        this.token = token;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        expString = readerDataHolder.getContext().getString(R.string.dictionary_error);
        final ContentResolverQueryRequest resolverQueryRequest = new ContentResolverQueryRequest(readerDataHolder,
                Uri.parse("content://com.onyx.android.dict.OnyxDictProvider"),
                "token=\'" + token + "\'");
        readerDataHolder.submitNonRenderRequest(resolverQueryRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                getExplanation(resolverQueryRequest.getCursor());
                BaseCallback.invoke(callback, request, e);
            }
        });
    }

    private void getExplanation(Cursor cursor) {
        try {
            if (cursor == null || cursor.getCount() == 0) {
                return;
            }
            int count = cursor.getCount();
            int index = 0;
            expString = "";
            while (cursor.moveToNext()) {
                expString += cursor.getString(3);
                if (index >= 0 && index < count - 1)
                    expString += "<br><br><br><br>";
                index++;
            }
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public String getExpString() {
        return expString;
    }

}
