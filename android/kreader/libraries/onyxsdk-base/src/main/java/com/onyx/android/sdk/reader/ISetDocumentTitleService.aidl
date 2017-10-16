
package com.onyx.android.sdk.reader;

/**
 * @author jim
 */

interface ISetDocumentTitleService {

    /**
     *
     * @param path the path of the document
     * @param titleToSet the document title to set
     * @return true if succeeded, false otherwise
     */
    boolean setTitle(String path, String titleToSet);

}
