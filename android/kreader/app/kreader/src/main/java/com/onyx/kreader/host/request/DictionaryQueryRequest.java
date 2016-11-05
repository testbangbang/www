package com.onyx.kreader.host.request;

import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.onyx.kreader.R;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.ui.data.ReaderDataHolder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ming on 16/10/14.
 */

public class DictionaryQueryRequest extends BaseReaderRequest{
    public static final int DICT_STATE_PARAM_ERROR = -1;
    public static final int DICT_STATE_QUERY_SUCCESSFUL = 0;
    public static final int DICT_STATE_QUERY_FAILED = 1;
    public static final int DICT_STATE_LOADING = 2;

    private static final String TAG = "DictionaryQueryRequest";
    private ReaderDataHolder readerDataHolder;
    private String expString = "";

    private String url = "content://com.onyx.dict.DictionaryProvider";
    private String query = null;

    public DictionaryQueryRequest(ReaderDataHolder readerDataHolder, String query) {
        this.readerDataHolder = readerDataHolder;
        this.query = query;
    }

    @Override
    public void execute(Reader reader) throws Exception {
        Cursor cursor = null;
        try {
            Uri uri = Uri.parse(url);
            String []selectionArgs = new String[]{query};
            cursor = readerDataHolder.getContext().getContentResolver().query(
                    uri, null, null, selectionArgs,
                    null);
            if (cursor == null) {
                expString = readerDataHolder.getContext().getString(R.string.dictionary_error);
                return;
            }
            if (cursor.getCount() == 0) {
                expString = readerDataHolder.getContext().getString(R.string.no_data);
                return;
            }
            if (cursor.moveToFirst()) {
                int state = cursor.getInt(1);
                switch (state){
                    case DICT_STATE_PARAM_ERROR:
                        expString = readerDataHolder.getContext().getString(R.string.dictionary_error);
                        break;
                    case DICT_STATE_QUERY_SUCCESSFUL:
                        expString += cursor.getString(3);
                        break;
                    case DICT_STATE_QUERY_FAILED:
                        expString = readerDataHolder.getContext().getString(R.string.no_data);
                        break;
                    case DICT_STATE_LOADING:
                        expString = readerDataHolder.getContext().getString(R.string.dictionary_data_no_find);
                        break;
                }
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
