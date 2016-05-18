/**
 *
 */
package com.onyx.android.sdk.res;

/**
 * @author jim
 *
 */
public interface IResource {

    public String getName();

    public ResourceType getType();

    /**
     * The base path is the place for storing the top level directory (like dictionary/, ivona/).
     * Each resource will have a related directory.
     * @return the base path for the resource
     */
    public String getBasePath();

    public boolean hasDefaultFiles();

    /**
     * The archive file of download link should contain the top level directory of the resource.
     * @return the download link
     */
    public String getDefaultFilesDownloadLink();

    public int getNotFoundMessageID();

}
