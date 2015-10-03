package com.onyx.reader.plugin;

import java.util.List;

/**
 * Created by zhuzeng on 10/3/15.
 */
public interface ReaderDocumentTableOfContentEntry {

    public String title();

    public ReaderDocumentPosition position();

    public List<ReaderDocumentTableOfContentEntry> child();

}
