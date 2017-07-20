package com.onyx.android.dr.reader.requests;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.onyx.android.dr.R;
import com.onyx.android.dr.reader.data.DictionaryQuery;
import com.onyx.android.dr.reader.presenter.ReaderPresenter;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 16/10/14.
 */

public class DictionaryQueryRequest extends BaseReaderRequest {

    private static final String TAG = "DictionaryQueryRequest";
    private static final String ID = "_id";
    private static final String STATE = "state";
    private static final String KEYWORD = "keyword";
    private static final String EXPLANATION = "explanation";
    private static final String SOUND_PATH = "soundPath";
    private static final String DICT_PATH = "dictPath";
    private static final String DICT_NAME = "dictName";
    private static final String ENTRY_INDEX = "entryIndex";

    private static final int DICT_COUNT_LIMIT = 5;
    private ReaderPresenter readerPresenter;
    private List<DictionaryQuery> dictionaryQueries = new ArrayList<>();
    private String errorInfo = "";

    private String url = "content://com.onyx.dict.DictionaryProvider";
    private String query = null;


    public DictionaryQueryRequest(ReaderPresenter readerPresenter, String query) {
        this.readerPresenter = readerPresenter;
        this.query = query;
    }

    @Override
    public void execute(Reader reader) throws Exception {
        Cursor cursor = null;
        Context context = readerPresenter.getReaderView().getViewContext();
        try {
            Uri uri = Uri.parse(url);
            query = StringUtils.trim(query);
            String []selectionArgs = new String[]{query, String.valueOf(DICT_COUNT_LIMIT)};
            cursor = context.getContentResolver().query(
                    uri, null, null, selectionArgs,
                    null);
            if (cursor == null) {
                errorInfo = context.getString(R.string.dictionary_error);
                return;
            }
            if (cursor.getCount() == 0) {
                errorInfo = context.getString(R.string.no_data);
                return;
            }
            if (cursor.moveToFirst()) {
                do {
                    assemblyQueryResult(cursor);
                }while (cursor.moveToNext());
            }

        } catch (Exception e) {
            errorInfo = context.getString(R.string.dictionary_error);
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void assemblyQueryResult(Cursor cursor) {
        String expString = "";
        String dictPath = "";

        Context context = readerPresenter.getReaderView().getViewContext();
        int state = cursor.getInt(cursor.getColumnIndex(STATE));
        int entryIndex = cursor.getInt(cursor.getColumnIndex(ENTRY_INDEX));
        String dictName = cursor.getString(cursor.getColumnIndex(DICT_NAME));
        String soundPath = cursor.getString(cursor.getColumnIndex(SOUND_PATH));
        switch (state) {
            case DictionaryQuery.DICT_STATE_PARAM_ERROR:
                expString = context.getString(R.string.dictionary_error);
                break;
            case DictionaryQuery.DICT_STATE_QUERY_SUCCESSFUL:
                expString += cursor.getString(cursor.getColumnIndex(EXPLANATION));
                dictPath = cursor.getString(cursor.getColumnIndex(DICT_PATH));
                break;
            case DictionaryQuery.DICT_STATE_QUERY_FAILED:
                expString = context.getString(R.string.no_data);
                break;
            case DictionaryQuery.DICT_STATE_LOADING:
                expString = context.getString(R.string.loading);
                break;
        }
        DictionaryQuery query = DictionaryQuery.create(dictName, dictPath, soundPath, expString, state);
        dictionaryQueries.add(query);
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public List<DictionaryQuery> getDictionaryQueries() {
        return dictionaryQueries;
    }
}
