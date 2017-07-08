package com.onyx.android.dr.reader.event;

import android.content.Context;

import com.onyx.android.sdk.data.model.DocumentInfo;

/**
 * Created by zhuzeng on 7/29/16.
 */
public class DocumentOpenEvent {
    private Context context;
    private DocumentInfo documentInfo;

    public DocumentOpenEvent(final Context c, final DocumentInfo documentInfo) {
        context = c;
        this.documentInfo = documentInfo;
    }

    public DocumentInfo getDocumentInfo() {
        return documentInfo;
    }

    public Context getContext() {
        return context;
    }
}
