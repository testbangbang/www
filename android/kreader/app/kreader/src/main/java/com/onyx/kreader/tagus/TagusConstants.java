package com.onyx.kreader.tagus;

import android.net.Uri;

/**
 * Created by jim on 17-4-20.
 */

public class TagusConstants {

    public static final String DB_TABLE_NAME = "doc_crypto";
    public static final String PROVIDER_AUTHORITY = "com.onyx.android.bookstore.provider";
    public static final String CONTENT_URI_STRING = "content://" + PROVIDER_AUTHORITY + "/" + DB_TABLE_NAME;
    public static final Uri CONTENT_URI = Uri.parse(CONTENT_URI_STRING);

}
