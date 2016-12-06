package com.onyx.kreader.plugins.alreader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

import com.alibaba.fastjson.JSON;
import com.neverland.engbook.bookobj.AlBookEng;
import com.neverland.engbook.forpublic.AlBitmap;
import com.neverland.engbook.forpublic.AlBookOptions;
import com.neverland.engbook.forpublic.AlBookProperties;
import com.neverland.engbook.forpublic.AlCurrentPosition;
import com.neverland.engbook.forpublic.AlEngineNotifyForUI;
import com.neverland.engbook.forpublic.AlEngineOptions;
import com.neverland.engbook.forpublic.AlOneContent;
import com.neverland.engbook.forpublic.AlOneSearchResult;
import com.neverland.engbook.forpublic.AlPublicProfileOptions;
import com.neverland.engbook.forpublic.AlRect;
import com.neverland.engbook.forpublic.AlTapInfo;
import com.neverland.engbook.forpublic.AlTextOnScreen;
import com.neverland.engbook.forpublic.EngBookMyType;
import com.neverland.engbook.forpublic.EngSelectionCorrecter;
import com.neverland.engbook.forpublic.TAL_CODE_PAGES;
import com.neverland.engbook.forpublic.TAL_RESULT;
import com.neverland.engbook.util.TTFInfo;
import com.neverland.engbook.util.TTFScan;
import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.api.ReaderDocumentOptions;
import com.onyx.kreader.api.ReaderDocumentTableOfContent;
import com.onyx.kreader.api.ReaderDocumentTableOfContentEntry;
import com.onyx.kreader.api.ReaderPluginOptions;
import com.onyx.kreader.api.ReaderSelection;
import com.onyx.kreader.common.Debug;
import com.onyx.kreader.host.impl.ReaderSelectionImpl;
import com.onyx.kreader.utils.PagePositionUtils;
import com.onyx.kreader.utils.RectUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 29/10/2016.
 */

public class AlReaderWrapper {

    static public long NO_ERROR = 0;
    static public long ERROR_UNKNOWN = 1;
    static public long ERROR_FILE_NOT_FOUND = 2;
    static public long ERROR_FILE_INVALID = 3;
    static public long ERROR_PASSWORD_INVALID = 4;
    static public long ERROR_SECURITY = 5;
    static public long ERROR_PAGE_NOT_FOUND = 6;

    private AlBookEng bookEng;
    private AlEngineOptions engineOptions;
    private AlPublicProfileOptions profile = new AlPublicProfileOptions();
    private ReaderTextStyle textStyle = null;

    private AlTextOnScreen screenText;

    public AlReaderWrapper(final Context context, final ReaderPluginOptions pluginOptions) {
        bookEng = new AlBookEng();
        bookEng.initializeBookEngine(createEngineOptions(context, pluginOptions));
        bookEng.initializeOwner(getEngineNotifyForUI());
        bookEng.setNewProfileParameters(getProfileDay(pluginOptions));
        setStyle(ReaderTextStyle.defaultStyle());
    }

    public void setViewSize(int width, int height) {
        bookEng.setNewScreenSize(width, height);
    }

    public long openDocument(final String path,  final ReaderDocumentOptions documentOptions) {
        AlBookOptions bookOpt = new AlBookOptions();
        bookOpt.codePage = TAL_CODE_PAGES.AUTO;
        bookOpt.codePageDefault = TAL_CODE_PAGES.CP936;
        bookOpt.formatOptions = 0;
        bookOpt.readPosition = 0;
        bookEng.openBook(path, bookOpt);
        return NO_ERROR;
    }

    public void closeDocument() {
        if (bookEng == null) {
            return;
        }
        bookEng.closeBook();
    }

    public String metadataString(final String tag) {
        byte [] data  = new byte[4096];
        return StringUtils.utf16le(data).trim();
    }

    public ReaderTextStyle getStyle() {
        return textStyle;
    }

    public void setStyle(final ReaderTextStyle style) {
        updateFontFace(style.getFontFace());
        updateFontSize(style.getFontSize().getValue());
        updateLineSpacing(style.getLineSpacing());
        updatePageMargins(style.getPageMargin().getLeftMargin(),
                style.getPageMargin().getTopMargin(),
                style.getPageMargin().getRightMargin(),
                style.getPageMargin().getBottomMargin());
        bookEng.setNewProfileParameters(profile);
        textStyle = style;
    }

    private AlEngineOptions createEngineOptions(final Context context, final ReaderPluginOptions pluginOptions) {
        engineOptions = new AlEngineOptions();
        engineOptions.appInstance = context;
        engineOptions.runInOneThread = true;
        engineOptions.font_catalog = pluginOptions.getFontDirectories().get(0);
        engineOptions.hyph_lang = EngBookMyType.TAL_HYPH_LANG.ENGRUS;
        engineOptions.useScreenPages = EngBookMyType.TAL_SCREEN_PAGES_COUNT.SIZE;
        engineOptions.pageSize4Use = AlEngineOptions.AL_USEAUTO_PAGESIZE;
        engineOptions.chinezeFormatting = true;

        float dpiMultiplex = context.getResources().getDisplayMetrics().density;
        engineOptions.DPI = EngBookMyType.TAL_SCREEN_DPI.TAL_SCREEN_DPI_160;
        if (dpiMultiplex >= 4.0f) {
            engineOptions.DPI = EngBookMyType.TAL_SCREEN_DPI.TAL_SCREEN_DPI_640;
        } else if (dpiMultiplex >= 3.0f) {
            engineOptions.DPI = EngBookMyType.TAL_SCREEN_DPI.TAL_SCREEN_DPI_480;
        } else if (dpiMultiplex >= 2.0f) {
            engineOptions.DPI = EngBookMyType.TAL_SCREEN_DPI.TAL_SCREEN_DPI_320;
        }
        engineOptions.textMultiplexer = dpiMultiplex;
        return engineOptions;
    }

    private AlEngineNotifyForUI getEngineNotifyForUI() {
        AlEngineNotifyForUI engUI = new AlEngineNotifyForUI();
        engUI.appInstance = null;
        engUI.hWND = null;
        return engUI;
    }

    private AlPublicProfileOptions getProfileDay(final ReaderPluginOptions pluginOptions) {
        profile.background = null;
        profile.backgroundMode = AlPublicProfileOptions.BACK_TILE_NONE;
        profile.bold = false;
        profile.font_name = "XZ";
        profile.font_monospace = "Monospace";
        profile.font_size = 36;
        profile.setMargins(5); // in percent
        profile.twoColumn = false;
        profile.colorText = 0x000000;
        profile.colorTitle = 0x9c27b0;
        profile.colorBack = 0xffffff;
        profile.interline = 0;
        profile.specialModeRoll = false;
        profile.sectionNewScreen = true;
        profile.justify = true;
        profile.notesOnPage = true;
        return profile;
    }

    private void updateFontFace(final String fontface) {
        if (StringUtils.isNullOrEmpty(fontface)) {
            return;
        }
        File file = new File(fontface);
        if (file.exists() && file.isFile()) {
            TTFInfo ttf = TTFScan.getTTFInfo(new File(fontface), false);
            if (ttf != null) {
                profile.font_name = ttf.Name;
            }
        }
    }

    public void updateFontSize(final float fontSize) {
        profile.font_size = (int)fontSize;
    }

    public void updateLineSpacing(final ReaderTextStyle.Percentage lineSpacing) {
        profile.interline = (int)(100 * (lineSpacing.getPercent() - 100) / (float)100);
    }

    public void updatePageMargins(final ReaderTextStyle.Percentage left,
                                  final ReaderTextStyle.Percentage top,
                                  final ReaderTextStyle.Percentage right,
                                  final ReaderTextStyle.Percentage bottom) {
        profile.setMarginLeft(left.getPercent());
        profile.setMarginTop(top.getPercent());
        profile.setMarginRight(right.getPercent());
        profile.setMarginBottom(bottom.getPercent());
    }

    public void draw(final Bitmap bitmap, final int width, final int height) {
        AlBitmap bmp = bookEng.getPageBitmap(EngBookMyType.TAL_PAGE_INDEX.CURR, width, height);
        Debug.e(getClass(), "draw bitmap: %d, %d, %s", bmp.bmp.getWidth(), bmp.bmp.getHeight(), bmp.bmp);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(bmp.bmp, 0, 0, new Paint());

        resetScreenState();
    }

    public int getTotalPage() {
        AlCurrentPosition position = new AlCurrentPosition();
        if (bookEng.getPageCount(position) != TAL_RESULT.OK) {
            return -1;
        }
        return position.pageCount;
    }

    public int getCurrentPage() {
        AlCurrentPosition position = new AlCurrentPosition();
        if (bookEng.getPageCount(position) != TAL_RESULT.OK) {
            return -1;
        }
        return getPageNumberOfPosition(position.readPositionStart) - 1;
    }

    public int getCurrentPosition() {
        AlCurrentPosition position = new AlCurrentPosition();
        if (bookEng.getPageCount(position) != TAL_RESULT.OK) {
            return -1;
        }
        return position.readPositionStart;
    }

    public boolean isFirstPage() {
        AlCurrentPosition position = new AlCurrentPosition();
        if (bookEng.getPageCount(position) != TAL_RESULT.OK) {
            return false;
        }
        return position.isFirstPage;
    }

    public boolean isLastPage() {
        AlCurrentPosition position = new AlCurrentPosition();
        if (bookEng.getPageCount(position) != TAL_RESULT.OK) {
            return false;
        }
        return position.isLastPage;
    }

    public boolean nextPage() {
        int ret = bookEng.gotoPosition(EngBookMyType.TAL_GOTOCOMMAND.NEXTPAGE, 0);
        return ret == TAL_RESULT.OK;
    }

    public boolean prevPage() {
        int ret = bookEng.gotoPosition(EngBookMyType.TAL_GOTOCOMMAND.PREVPAGE, 0);
        return ret == TAL_RESULT.OK;
    }

    public boolean gotoPosition(int pos) {
        int ret = bookEng.gotoPosition(EngBookMyType.TAL_GOTOCOMMAND.POSITION, pos);
        return ret == TAL_RESULT.OK;
    }

    public boolean gotoPage(int page) {
        int ret = bookEng.gotoPage(page + 1);
        return ret == TAL_RESULT.OK;
    }

    public boolean readTableOfContent(final ReaderDocumentTableOfContent toc) {
        AlBookProperties properties = bookEng.getBookProperties(true);
        if (properties.content == null) {
            return false;
        }
        for (AlOneContent content : properties.content) {
            if (content.isBookmark) {
                continue;
            }
             ReaderDocumentTableOfContentEntry.addEntry(toc.getRootEntry(), content.name,
                     content.pageNum, PagePositionUtils.fromPosition(content.positionS));
        }
        return true;
    }

    public boolean search(final String text, final List<ReaderSelection> list) {
        try {
            if (bookEng.findText(text) != TAL_RESULT.OK) {
                return false;
            }
            ArrayList<AlOneSearchResult> searchResults = bookEng.getFindTextResult();
            if (searchResults == null || searchResults.size() <= 0) {
                return false;
            }
            for (AlOneSearchResult result : searchResults) {
                ReaderSelectionImpl selection = new ReaderSelectionImpl();
                selection.setPageName(PagePositionUtils.fromPageNumber(getPageNumberOfPosition(result.pos_start)));
                selection.setPagePosition(PagePositionUtils.fromPosition(result.pos_start));
                selection.setStartPosition(PagePositionUtils.fromPosition(result.pos_start));
                selection.setEndPosition(PagePositionUtils.fromPosition(result.pos_end));
                selection.setText(text);
                selection.setDisplayRects(new ArrayList<RectF>());
                list.add(selection);
            }
        } finally {
            // clear finds in book engine
            bookEng.findText(null);
        }

        return true;
    }

    public ReaderSelection selectText(PointF start, PointF end) {
        Debug.d(getClass(), "start point: %s, end point: %s", JSON.toJSONString(start),
                JSON.toJSONString(end));

        AlTextOnScreen screenText = getTextOnScreen();
        if (screenText == null) {
            Debug.w(getClass(), "get text on screen failed!");
            return null;
        }

        int startPos = hitTest((int)start.x, (int)start.y);
        if (startPos == -1) {
            return null;
        }
        int endPos = hitTest((int)end.x, (int)end.y);
        if (endPos == -1) {
            return null;
        }
        return combineSelection(screenText, startPos, endPos);
    }

    public ReaderSelection selectText(int startPos, int endPos) {
        AlTextOnScreen screenText = getTextOnScreen();
        if (screenText == null) {
            Debug.w(getClass(), "get text on screen failed!");
            return null;
        }

        return combineSelection(screenText, startPos, endPos);
    }

    private void resetScreenState() {
        screenText = null;
    }

    private AlTextOnScreen getTextOnScreen() {
        if (screenText == null) {
            screenText = bookEng.getTextOnScreen();
        }
        return screenText;
    }

    private int hitTest(int x, int y) {
        try {
            AlTapInfo tapInfo = bookEng.getInfoByTap(x, y, EngBookMyType.TAL_SCREEN_SELECTION_MODE.DICTIONARY);
            Debug.d(getClass(), "tap info: " + JSON.toJSONString(tapInfo));
            return tapInfo == null ? -1 : tapInfo.pos;
        } finally {
            bookEng.setSelectionMode(EngBookMyType.TAL_SCREEN_SELECTION_MODE.NONE);
        }
    }

    private int getPageNumberOfPosition(int position) {
        return bookEng.getPageOfPosition(position);
    }

    private ReaderSelection combineSelection(AlTextOnScreen textOnScreen, int startPos, int endPos) {
        Debug.d(getClass(), "start pos: %d, end pos: %d", startPos, endPos);
        int startIndex = textOnScreen.findWordByPos(startPos);
        int endIndex = textOnScreen.findWordByPos(endPos);
        if (startIndex == -1 || endIndex == -1) {
            return null;
        }
        if (startIndex > endIndex) {
            int tmp = startIndex;
            startIndex = endIndex;
            endIndex = tmp;
        }
        final AlTextOnScreen.AlPieceOfText startPiece = screenText.regionList.get(startIndex);
        final AlTextOnScreen.AlPieceOfText endPiece = screenText.regionList.get(endIndex);
        Debug.d(getClass(), JSON.toJSONString(startPiece));
        ReaderSelectionImpl selection = new ReaderSelectionImpl();
        selection.setPageName(PagePositionUtils.fromPageNumber(getPageNumberOfPosition(getPieceStart(startPiece))));
        selection.setPagePosition(PagePositionUtils.fromPosition(getCurrentPosition()));
        selection.setText(combineSelectionText(textOnScreen, startIndex, endIndex));
        selection.setStartPosition(PagePositionUtils.fromPosition(getPieceStart(startPiece)));
        selection.setEndPosition(PagePositionUtils.fromPosition(getPieceEnd(endPiece)));
        selection.setDisplayRects(combineSelectionRectangles(textOnScreen, startIndex, endIndex));
        return selection;
    }

    private int getPieceStart(AlTextOnScreen.AlPieceOfText piece) {
        return piece.positions[0];
    }

    private int getPieceEnd(AlTextOnScreen.AlPieceOfText piece) {
        return piece.positions[piece.positions.length - 1];
    }

    private String combineSelectionText(AlTextOnScreen textOnScreen, int startIndex, int endIndex) {
        StringBuilder builder = new StringBuilder(textOnScreen.regionList.get(startIndex).word);
        for (int i = startIndex + 1; i <= endIndex; i++) {
            builder.append(" ").append(textOnScreen.regionList.get(i).word);
        }
        return builder.toString();
    }

    private List<RectF> combineSelectionRectangles(AlTextOnScreen textOnScreen, int startIndex, int endIndex) {
        ArrayList<RectF> rectList = new ArrayList<>();
        for (int i = startIndex; i <= endIndex; i++) {
            rectList.add(createRect(textOnScreen.regionList.get(i).rect));
        }
        return RectUtils.mergeRectanglesByBaseLine(rectList);
    }

    private RectF createRect(AlRect rect) {
        return new RectF(rect.x0, rect.y0, rect.x1, rect.y1);
    }

}
