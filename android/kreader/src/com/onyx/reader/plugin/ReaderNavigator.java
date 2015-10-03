package com.onyx.reader.plugin;

/**
 * Created by zhuzeng on 10/3/15.
 */
public interface ReaderNavigator {


    /**
     * Navigate to next screen.
     */
    public boolean nextScreen();

    /**
     * Navigate to prev screen.
     */
    public boolean prevScreen();

    /**
     * Navigate to next page.
     * @return
     */
    public boolean nextPage();

    /**
     * Navigate to prev page.
     * @return
     */
    public boolean prevPage();

    /**
     * Navigate to first page.
     * @return
     */
    public boolean firstPage();

    /**
     * Navigate to last page.
     * @return
     */
    public boolean lastPage();

    /**
     * Navigate to specified position.
     * @return
     */
    public boolean gotoPosition(final ReaderDocumentPosition position);



}
