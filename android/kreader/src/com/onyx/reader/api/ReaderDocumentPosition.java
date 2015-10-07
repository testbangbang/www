package com.onyx.reader.api;

/**
 * Created by zhuzeng on 10/2/15.
 * Represents a document location, it could be a page or certain location inside a page.
 */
public interface ReaderDocumentPosition {

    /**
     * Get page number.
     * @return Return 0 based page number.
     */
    public int getPageNumber();

    /**
     * Get the page name.
     * @return get page name string.
     */
    public String getPageName();

    /**
     * Get persistent string representation of object. For example, plugin may use json to persistent
     * object into json string.
     * @return position persistent representation.
     */
    public String save();

    /**
     * Restore the position from persistent string.
     * @param string the persistent representation.
     */
    public void restore(final String string);



}
