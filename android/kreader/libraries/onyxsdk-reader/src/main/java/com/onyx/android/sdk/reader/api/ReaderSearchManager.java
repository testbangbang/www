package com.onyx.android.sdk.reader.api;

import java.util.List;

/**
 * Created by zhuzeng on 10/3/15.
 */
public interface ReaderSearchManager {

    public boolean searchPrevious(final ReaderSearchOptions options);

    public boolean searchNext(final ReaderSearchOptions options);

    public boolean searchInPage(final int currentPage, final ReaderSearchOptions options, boolean clear);

    public List<ReaderSelection> searchResults();

}
