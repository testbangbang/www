package com.onyx.reader.api;

import java.util.List;

/**
 * Created by zhuzeng on 10/2/15.
 */
public interface ReaderDocumentMetadata {


    public List<String> getAuthors();

    public String getTitle();

    public String getDescription();

    public List<String> getCreator();

    public List<String> getPublisher();



}
