package com.onyx.android.sdk.reader.api;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by zhuzeng on 10/2/15.
 */
public interface ReaderDocumentMetadata {

    String getTitle();

    void setTitle(final String title);

    String getDescription();

    void setDescription(final String description);

    @NonNull List<String> getAuthors();

    String getPublisher();

    void setPublisher(final String publisher);

    String getOptions();

    void setOptions(final String options);

}
