package com.onyx.kreader.host.request;

import android.database.Cursor;
import android.net.Uri;

import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.R;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.ui.data.DictionaryQuery;
import com.onyx.kreader.ui.data.ReaderDataHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 16/10/14.
 */

public class DictionaryQueryRequest extends BaseReaderRequest{

    private static final String TAG = "DictionaryQueryRequest";
    private static final int DICT_COUNT_LIMIT = 5;
    private ReaderDataHolder readerDataHolder;
    private List<DictionaryQuery> dictionaryQueries = new ArrayList<>();
    private String errorInfo = "";

    private String url = "content://com.onyx.dict.DictionaryProvider";
    private String[] columns = new String[] { "_id","state","keyword","explanation","dictPath","dictName","entryIndex"};
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
            query = StringUtils.filterUnusedChar(query, '\u0000');
            String []selectionArgs = new String[]{query, String.valueOf(DICT_COUNT_LIMIT)};
            cursor = readerDataHolder.getContext().getContentResolver().query(
                    uri, null, null, selectionArgs,
                    null);
            if (cursor == null) {
                errorInfo = readerDataHolder.getContext().getString(R.string.dictionary_error);
                return;
            }
            if (cursor.getCount() == 0) {
                errorInfo = readerDataHolder.getContext().getString(R.string.no_data);
                return;
            }
            if (cursor.moveToFirst()) {
                do {
                    assemblyQueryResult(cursor);
                }while (cursor.moveToNext());
            }

        } catch (Exception e) {
            errorInfo = readerDataHolder.getContext().getString(R.string.dictionary_error);
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void assemblyQueryResult(Cursor cursor) {
        String expString = "";
        String dictPath = "";
        int state;
        String dictName;
        int entryIndex;

        state = cursor.getInt(cursor.getColumnIndex(columns[1]));
        entryIndex = cursor.getInt(cursor.getColumnIndex(columns[6]));
        dictName = cursor.getString(cursor.getColumnIndex(columns[5]));
        switch (state) {
            case DictionaryQuery.DICT_STATE_PARAM_ERROR:
                expString = readerDataHolder.getContext().getString(R.string.dictionary_error);
                break;
            case DictionaryQuery.DICT_STATE_QUERY_SUCCESSFUL:
                expString += cursor.getString(cursor.getColumnIndex(columns[3]));
                dictPath = cursor.getString(cursor.getColumnIndex(columns[4]));
                break;
            case DictionaryQuery.DICT_STATE_QUERY_FAILED:
                expString = readerDataHolder.getContext().getString(R.string.no_data);
                break;
            case DictionaryQuery.DICT_STATE_LOADING:
                expString = readerDataHolder.getContext().getString(R.string.loading);
                break;
        }
        DictionaryQuery query = DictionaryQuery.create(dictName, dictPath, expString, state);
        dictionaryQueries.add(query);
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public List<DictionaryQuery> getDictionaryQueries() {
        return dictionaryQueries;
    }
}
