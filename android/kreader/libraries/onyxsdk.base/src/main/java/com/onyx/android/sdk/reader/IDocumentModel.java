/**
 * 
 */
package com.onyx.android.sdk.reader;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;

import com.onyx.android.sdk.data.cms.OnyxMetadata;

/**
 * @author joy
 *
 */
public interface IDocumentModel
{
    /**
     * interface to handle all callbacks
     * 
     * @author joy
     *
     */
    static interface DocumentCallbackListener {
        void onRequestDocumentPassword();
        
        /**
         * extensible callback interface
         * 
         * @param r
         */
        void onCallback(Runnable r);
    }
    void setDocumentCallbackListener(DocumentCallbackListener l);
    
    boolean canOpen(String path);

    boolean openFile(String path);
    boolean close();
    void setDocumentPassword(String password);
    
    /**
     * interrupt model to prevent any task from being processed
     */
    void interrupt();
    /**
     * resume model to run
     */
    void resume();
    
    boolean isOpened();
    /**
     * currently opened file path
     * @return
     */
    String getFilePath();
    
    PageLayout getPageLayout();
    
    PagingMode getPagingMode();
    boolean setPagingMode(PagingMode mode);
    
    /**
     * return null if failed
     * 
     * @return
     */
    OnyxMetadata readMetadata();
    
    int getPageCount();
    
    String getCurrentLocation();
    boolean gotoLocation(String location);

    double getCurrentPagePosition();
    double getPagePositionFromLocation(String location);
    boolean gotoPagePosition(double page);

    int compareLocation(String loc1, String loc2);
    
    boolean isAtDocumentBeginning();
    boolean isAtDocumentEnd();
    boolean isLocationInCurrentScreen(String location);

    String getDocumentBeginningLocation();
    String getDocumentEndLocation();
    
    String getScreenBeginningLocation();
    String getScreenEndLocation();
    
    Size getPageNaturalSize();
    Rect getPageContentArea();
    Rect getPageRegionInScrollMode(int page);
    
    Point getPageScroll();
    
    double getFontSize();
    boolean setFontSize(double size);
    
    boolean isGlyphEmboldenEnabled();
    boolean setGlyphEmboldenEnabled(boolean enable);

    /**
     * reflowed document can be navigated by screen
     * 
     * @return
     */
    boolean previousScreen();
    boolean nextScreen();
    
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
     * @param zoom
     * @param left
     * @param top
     * @param width
     * @param height
     * @return
     */
    Bitmap renderPage(double zoom, int left, int top, int width, int height);
    
    String getText(String locationBegin, String locationEnd);
    String getScreenText();
    
    boolean hasTOC();
    /**
     * return null if failed
     * 
     * @return
     */
    TOCItem[] getTOC();
    
    /**
     * find all links in current screen,
     * return empty list when none, return null when failed
     * 
     * @return
     */
    List<LinkInfo> getScreenLinkList();
    
    String hitTest(int x, int y);
    TextSelection hitTestWord(int x, int y);
    TextSelection moveSelectionBegin(int x, int y);
    TextSelection moveSelectionEnd(int x, int y);
    
    TextSelection measureSelection(String locationBegin, String locationEnd);
    
    TextSelection hitTestSentence(String sentenceBegin);
    
    /**
     * return all occurrences of pattern in current screen,
     * return empty array when none, return null when failed
     * 
     * @param pattern
     * @return
     */
    List<TextSelection> searchInCurrentScreen(String pattern);
    /**
     * find document location of next occurrence of pattern after current screen,
     * return null if failed
     * 
     * @param pattern
     * @return
     */
    String searchForwardAfterCurrentScreen(String pattern);
    /**
     * find document location of previous occurrence of pattern before current screen,
     * return null if failed
     * 
     * @param pattern
     * @return
     */
    String searchBackwardBeforeCurrentScreen(String pattern);
    /**
     * find all occurrences of pattern in document,
     * return corresponding document location, return null if failed
     * 
     * @param pattern
     * @return
     */
    String[] searchAllInDocument(String pattern);
}
