package com.onyx.reader.plugin;

import java.util.List;

/**
 * Created by zhuzeng on 10/2/15.
 */
public interface ReaderPlugin {

    /**
     * Return the plugin display name.
     * @return
     */
    public String displayName();

    /**
     * Tell file type list the plugin supports.
     * @return file extension name list.
     */
    public List<String> supportedFileList();

    /**
     * Try to open the document specified by the path.
     * @param path The path in local file system.
     * @param options The opening options.
     * @return Reader document instance.
     * @throws ReaderException
     */
    public ReaderDocument open(final String path, final ReaderOpenOptions options) throws ReaderException;

    /**
     * Close the specified document.
     * @param document the document opened by this plugin.
     */
    public void close(final ReaderDocument document);


    /**
     * DRM support
     */
    public ReaderDrmManager createDrmManager();


}
