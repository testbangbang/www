package com.onyx.android.sdk.ui.dialog.data;

import android.app.FragmentManager;

import com.onyx.android.sdk.reader.ReadingMode;

import java.util.ArrayList;

/**
 * Created by Joy on 14-3-19.
 */
public abstract class AbstractReaderMenuCallback {

    public enum NavigationMode {
        SINGLE_PAGE_MODE,
        SINGLE_COLUMN,
        AUTO_CROP_PAGE_MODE,
        AUTO_CROP_WIDTH_MODE,
        ROWS_LEFT_TO_RIGHT_MODE,
        ROWS_RIGHT_TO_LEFT_MODE,
        COLUMNS_LEFT_TO_RIGHT_MODE,
        COLUMNS_RIGHT_TO_LEFT_MODE,
    }

    public enum LineSpacingScale {Small, Normal, Large, Custom}

    public abstract boolean supportTts();

    public abstract boolean supportMarginsConfig();
    public abstract boolean supportZooming();
    public abstract boolean supportSpacing();
    public abstract boolean supportReadingMode();
    public abstract boolean supportSelectionZoom();
    public abstract boolean supportSmartReflow();

    public boolean isCustomSetScreenRefresh() {
        return false;
    }
    public boolean supportSetFontSize() { return false; }
    public boolean supportSetFontFace() { return false; }
    public boolean supportRotation() { return false; }
    public boolean supportNavigation() { return false; }
    public boolean supportAnnotation() { return false; }
    public boolean supportScribble() { return false; }
    public boolean supportDataExport() { return false;}
    public boolean supportDictionaryFunc() { return true;}
    public boolean supportFontFunc() {return true;}
    public boolean supportNoteFunc() {return true;}

    public abstract String getBookName();
    public abstract int getCurrentPage();
    public abstract int getTotalPage();

    public abstract void gotoPage(int page);

    public boolean canNavigateNext() { return false; }
    public boolean canNavigatePrevious() { return false; }
    public void nextNavigation() { }
    public void previousNavigation() { }

    public void setNavigationMode(NavigationMode mode) { }
    public void showNavigationModeSettings() {}
    public void resetNavigationSettings() {}
    public void setNavigationComicPreset() {}
    public void setNavigationArticlePreset() {}

    public abstract void increaseFontSize();
    public abstract void decreaseFontSize();
    public abstract void setFontSize(int fontSize);
    public abstract int getFontSize();

    public void setContrast() { }

    public abstract boolean isFontEmbolden();
    public abstract void setFontEmbolden(boolean embolden);

    public abstract boolean isSmartReflow();
    public abstract void setSmartReflow(boolean reflow);

    public abstract String getFontFace();
    public abstract void selectFontFace();

    public abstract void zoomIn();
    public abstract void zoomOut();
    public abstract void zoomToWidth();
    public abstract void zoomToPage();
    public abstract void zoomByCrop();
    public abstract void zoomBySelection();

    public abstract void increaseLineSpacing();
    public abstract void decreaseLineSpacing();
    public abstract void setLineSpacing(LineSpacingScale spacing);
    public abstract LineSpacingScale getLineSpacing();
    public abstract void toggleParagraphIndent();

    public abstract void showToc();
    public abstract void showBookmarks();
    public abstract void showAnnotations();

    public void startSeekPage() { }

    public void startScribble() { }
    public void exportUserData() {}
    public void finishScribble() { }
    public void startScribbleErase() { }
    public void finishScribbleErase() { }
    public void customSetScreenRefreshInterval() {
    }

    public abstract FragmentManager getFragmentManager();
    public abstract int getScreenOrientation();
    public abstract void setScreenOrientation(int orientation);

    public abstract boolean isTtsPlaying();
    public abstract void ttsInit();
    public abstract void ttsPlay();
    public abstract void ttsPause();
    public abstract void ttsStop();
    public abstract boolean supportContrast();

    public abstract void startSearch();

    public boolean startDictionaryApp() {
        return false;
    }

    public void setScreenRefreshInterval(int interval) {
        // do nothing
    }

    public abstract ReadingMode getReadingMode();
    public abstract void setReadingMode(ReadingMode mode);
    public abstract ArrayList<ReadingMode> getSupportedReadingModes();

    public abstract void configMargins();
    public abstract void showReaderSettings();
    public abstract void quitApplication();
    public abstract void saveCurrentFontStyleOption();
    public abstract void restoreFontStyleOption();
    public abstract boolean isShowActionBar();
    public abstract int getPageMargin();
    public abstract void setPageMargin(int pageMargin);
    public abstract void setIndent(int indent);
    public abstract int getIndent();
    public abstract ArrayList<Integer> getFontSizeArray();
    public abstract void increasePageMargin();
    public abstract void decreasePageMargin();
    public abstract void showGoToPageDialog();
}
