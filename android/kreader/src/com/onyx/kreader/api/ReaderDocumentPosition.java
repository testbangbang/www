package com.onyx.kreader.api;

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
     * Get persistent string representation of object. For example, plugin may use json to persistent
     * object into json string.
     * @return position persistent representation.
     */
    public String save();

    /**
     * Restore the position from persistent string.
     * @param string the persistent representation.
     */
    public boolean restore(final String string);


    /**
     * Compare this position with another position.
     * @param another another position.
     * @return -1 if this position is before another position.
     * 0 if they are equal
     * 1 if this position after another position.
     */
    public int compare(final ReaderDocumentPosition another);



}
