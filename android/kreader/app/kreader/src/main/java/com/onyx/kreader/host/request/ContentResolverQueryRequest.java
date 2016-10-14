package com.onyx.kreader.host.request;

import android.database.Cursor;
import android.net.Uri;

import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by ming on 16/10/14.
 */

public class ContentResolverQueryRequest extends BaseReaderRequest{

    private ReaderDataHolder readerDataHolder;
    private Cursor cursor;

    private Uri uri;
    private String selection = null;
    private String[] projection= null;
    private String[] selectionArgs = null;
    private String sortOrder = null;

    public ContentResolverQueryRequest(ReaderDataHolder readerDataHolder, Uri uri, String selection) {
        this.readerDataHolder = readerDataHolder;
        this.uri = uri;
        this.selection = selection;
    }

    public ContentResolverQueryRequest(ReaderDataHolder readerDataHolder, Uri uri, String selection, String[] projection, String[] selectionArgs, String sortOrder) {
        this.readerDataHolder = readerDataHolder;
        this.uri = uri;
        this.selection = selection;
        this.projection = projection;
        this.selectionArgs = selectionArgs;
        this.sortOrder = sortOrder;
    }

    @Override
    public void execute(Reader reader) throws Exception {
        try {
            cursor = readerDataHolder.getContext().getContentResolver().query(
                    uri, projection, selection, selectionArgs,
                    sortOrder);
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public Cursor getCursor() {
        return cursor;
    }
}
