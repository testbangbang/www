/**
 * 
 */
package com.onyx.android.sdk.reader;

/**
 * @author joy
 *
 */
interface IMetadataService
{

    /**
     * 
     * @param filePath
     * @param timeout milliseconds
     * @return
     */
    boolean extractMetadataAndThumbnail(String filePath, int timeout);

    /**
     *
     * @param fileList
     * @param timeout milliseconds
     * @return
     */
    boolean extractMetadata(in List<String>  fileList, int timeout);

    void interrupt();
}
