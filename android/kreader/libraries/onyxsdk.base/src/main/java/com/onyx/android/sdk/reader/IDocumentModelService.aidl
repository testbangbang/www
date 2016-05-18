package com.onyx.android.sdk.reader;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.onyx.android.sdk.reader.PageLayout;
import com.onyx.android.sdk.reader.PagingMode;
import com.onyx.android.sdk.reader.TextSelection;
import com.onyx.android.sdk.reader.Size;
import com.onyx.android.sdk.reader.TOCItem;

/**
 * @author joy
 *
 */
interface IDocumentModelService
{
    boolean canOpen(String path);
    boolean isOpened();
    
    /**
     * currently opened file path
     * @return
     */
    String getFilePath();
    boolean openFile(String path);
    boolean close();
    
    /**
     * interrupt all task being processed until resume() being called
     */
    void interrupt();
    void resume();
    
    int getPageCount();
    
    PageLayout getPageLayout();
    PagingMode getPagingMode();
    boolean setPagingMode(in PagingMode mode);
        
    double getPagePosition();
    double getPagePositionOfLocation(String location);
    boolean gotoPagePosition(double page);
    boolean gotoDocLocation(String location);
    
    /**
     * reflowable document can be navigated by screen
     * 
     * @return
     */
    boolean previousScreen();
    boolean nextScreen();

    boolean isLocationInCurrentScreen(String location);
    String getScreenBeginningLocation();
    String getScreenEndLocation();
    
    Size getPageNaturalSize();
    Rect getPageContentArea();
    
    String getScreenText();
    
    /**
     * navigate the page using specified navigation arguments, but no rendering
     * 
     * @param zoom
     * @param scrollX
     * @param scrollY
     * @return
     */
    boolean navigatePage(double zoom, int scrollX, int scrollY);
        
    /**
     * return null when failed
     * 
     * @param page
     * @param zoom
     * @param left
     * @param top
     * @param width
     * @param height
     * @return
     */
    byte[] renderPage(double zoom, int left, int top, int width, int height, boolean isPrefetch);
    
    boolean hasTOC();
    /**
     * return null if failed
     * 
     * @return
     */
    TOCItem[] getTOC();
    
    boolean setFontSize(double size);
    
    boolean isGlyphEmboldenEnabled();
    boolean setGlyphEmboldenEnabled(boolean enable);
    
    String hitTest(int x, int y);
    TextSelection hitTestWord(int x, int y);
    TextSelection moveSelectionBegin(int x, int y);
    TextSelection moveSelectionEnd(int x, int y);
    
    TextSelection measureSelection(String locationBegin, String locationEnd);
    
    List<TextSelection> searchInCurrentScreen(String pattern);
    String searchForwardAfterCurrentScreen(String pattern);
    String searchBackwardBeforeCurrentScreen(String pattern);
    String[] searchAllInDocument(String pattern);
}