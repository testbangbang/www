package com.onyx.kreader.host.request;

import android.database.Cursor;
import android.net.Uri;

import com.onyx.kreader.R;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by ming on 16/10/14.
 */

public class DictionaryQueryRequest extends BaseReaderRequest{

    private ReaderDataHolder readerDataHolder;
    private String expString;

    private Uri uri;
    private String selection = null;

    public DictionaryQueryRequest(ReaderDataHolder readerDataHolder, Uri uri, String selection) {
        this.readerDataHolder = readerDataHolder;
        this.uri = uri;
        this.selection = selection;
    }

    @Override
    public void execute(Reader reader) throws Exception {
        Cursor cursor = null;
        try {
            cursor = readerDataHolder.getContext().getContentResolver().query(
                    uri, null, selection, null,
                    null);
            if (cursor == null) {
                expString = readerDataHolder.getContext().getString(R.string.dictionary_error);
                return;
            }
            if (cursor.getCount() == 0) {
                expString = readerDataHolder.getContext().getString(R.string.no_data);
                return;
            }
            int count = cursor.getCount();
            int index = 0;
            while (cursor.moveToNext()) {
                expString += cursor.getString(3);
                if (index >= 0 && index < count - 1)
                    expString += "<br><br><br><br>";
                index++;
            }
        } catch (Exception e) {
            expString = readerDataHolder.getContext().getString(R.string.dictionary_error);
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
