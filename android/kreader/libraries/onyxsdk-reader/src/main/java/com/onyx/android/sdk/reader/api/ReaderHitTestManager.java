package com.onyx.android.sdk.reader.api;

/**
 * Created by zhuzeng on 10/3/15.
 */
public interface ReaderHitTestManager {

    /**
     * Select word by the point. The plugin should automatically extend the selection to word boundary.
     * @param hitTest the user input point in document coordinates system.
     * @param splitter the text splitter.
     * @return the selection.
     */
    public ReaderSelection selectWordOnScreen(final ReaderHitTestArgs hitTest, final ReaderTextSplitter splitter);

    /**
     * Get document position for specified point.
     * @param hitTest the hit test args.
     * @return
     */
    public String position(final ReaderHitTestArgs hitTest);

    /**
     * Select text between start point and end point.
     * @param start The start view point.
     * @param hitTestOptions
     * @return the selection.
     */
    public ReaderSelection selectOnScreen(final ReaderHitTestArgs start, final ReaderHitTestArgs end, final ReaderHitTestOptions hitTestOptions);

    public ReaderSelection selectOnScreen(String pagePosition, final String startPosition, final String endPosition);


}
