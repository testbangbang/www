/**
 *
 */
package com.onyx.android.sdk.reader;

/**
 * @author joy
 *
 */
interface IDocumentTocService
{

    /**
     *
     * @param filePath
     * @return
     */
    ParcelFileDescriptor getToc(String filePath);
    void interrupt();
}