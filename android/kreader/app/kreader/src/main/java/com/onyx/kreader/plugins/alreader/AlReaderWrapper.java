package com.onyx.kreader.plugins.alreader;

import android.content.Context;
import android.graphics.Bitmap;
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
import com.neverland.engbook.forpublic.TAL_CODE_PAGES;
import com.neverland.engbook.forpublic.TAL_RESULT;
import com.neverland.engbook.util.EngBitmap;
import com.neverland.engbook.util.TTFInfo;
import com.neverland.engbook.util.TTFScan;
import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.utils.Benchmark;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.api.ReaderDocumentOptions;
import com.onyx.kreader.api.ReaderDocumentTableOfContent;
import com.onyx.kreader.api.ReaderDocumentTableOfContentEntry;
import com.onyx.kreader.api.ReaderPluginOptions;
import com.onyx.kreader.api.ReaderSelection;
import com.onyx.kreader.api.ReaderSentence;
import com.onyx.kreader.common.Debug;
import com.onyx.kreader.host.impl.ReaderSelectionImpl;
import com.onyx.kreader.host.impl.ReaderTextSplitterImpl;
import com.onyx.kreader.utils.PagePositionUtils;
import com.onyx.kreader.utils.RectUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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

    private static String DEFAULT_EN_FONT_NAME = "XZ";
    private static String DEFAULT_ZH_FONT_NAME = "FZLanTingHei-R-GBK";

    private static String DEFAULT_ZH_FONT_FILE = "/system/fonts/OnyxCustomFont-Regular.ttf";

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
        initDefaultTextStyle();
    }

    private void initDefaultTextStyle() {
        ReaderTextStyle style = ReaderTextStyle.defaultStyle();
        style.setFontFace(getDefaultFontName());
        setStyle(style);
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
        updateFontFace(style, style.getFontFace());
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

    private String getDefaultFontName() {
        if (Locale.getDefault().equals(Locale.CHINA) ||
                Locale.getDefault().equals(Locale.CHINESE) ||
                Locale.getDefault().equals(Locale.SIMPLIFIED_CHINESE) ||
                Locale.getDefault().equals(Locale.TRADITIONAL_CHINESE)) {
            return DEFAULT_ZH_FONT_NAME;
        }
        return DEFAULT_EN_FONT_NAME;
    }

    private AlPublicProfileOptions getProfileDay(final ReaderPluginOptions pluginOptions) {
        profile.background = null;
        profile.backgroundMode = AlPublicProfileOptions.BACK_TILE_NONE;
        profile.bold = false;
        profile.font_name = getDefaultFontName();
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

    private void updateFontFace(final ReaderTextStyle style, final String fontface) {
        if (StringUtils.isNullOrEmpty(fontface)) {
            profile.font_name = getDefaultFontName();
            if (profile.font_name.compareTo(DEFAULT_ZH_FONT_NAME) == 0) {
                // update style's font face, so outside can see the font being used
                style.setFontFace(DEFAULT_ZH_FONT_FILE);
            }
            return;
        }
        File file = new File(fontface);
        if (file.exists() && file.isFile()) {
            TTFInfo ttf = TTFScan.getTTFInfo(new File(fontface), false);
            if (ttf != null) {
                profile.font_name = ttf.Name;
                return;
            }
        }
        profile.font_name = fontface;
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
        Benchmark benchmark = new Benchmark();
        AlBitmap bmp = EngBitmap.createBookBitmap(bitmap);
        engineOptions.externalBitmap = bmp;
        bookEng.getPageBitmap(EngBookMyType.TAL_PAGE_INDEX.CURR, width, height);
        benchmark.report("getPageBitmap");

        resetScreenState();
    }

    public String getScreenText() {
        AlTextOnScreen screenText = getTextOnScreen();
        if (screenText == null) {
            Debug.w(getClass(), "get text on screen failed!");
            return null;
        }
        return combineSelectionText(screenText, 0, screenText.regionList.size() - 1);
    }

    public ReaderSentence getSentence(final int startPosition) {
        if (startPosition < getScreenStartPosition() ||
                startPosition >= getScreenEndPosition()) {
            return null;
        }
        AlTextOnScreen screenText = getTextOnScreen();
        if (screenText == null) {
            Debug.w(getClass(), "get text on screen failed!");
            return null;
        }
        final int MAX_SENTENCE_LENGTH = 200;
        int endPos = -1;
        boolean found = false;
        // sentence in the range [startPos, endPos]
        for (AlTextOnScreen.AlPieceOfText piece : screenText.regionList) {
            if (found) {
                break;
            }
            int textEndPos = getPieceEnd(piece);
            if (textEndPos < startPosition) {
                continue;
            }
            for (int i = 0; i < piece.positions.length; i++) {
                endPos = piece.positions[i];
                if (endPos <= startPosition) {
                    continue;
                }
                if ((endPos - startPosition + 1) >= MAX_SENTENCE_LENGTH) {
                    found = true;
                    break;
                }
                char ch = piece.word.charAt(i);
                if (ReaderTextSplitterImpl.isSentenceBreakSplitter(ch)) {
                    found = true;
                    break;
                }
            }
        }

        ReaderSelectionImpl selection = combineSelection(screenText, startPosition, endPos);
        boolean endOfScreen = endPos == getPieceEnd(lastPiece(screenText));
        boolean endOfDocument = endOfScreen && isLastPage();
        int nextPos = nextTextPosition(screenText, endPos);
        ReaderSentence sentence = ReaderSentence.create(selection, nextPos, endOfScreen, endOfDocument);
        return sentence;
    }

    public List<ReaderSelection> getPageLinks() {
        List<ReaderSelection> result = new ArrayList<>();

        AlTextOnScreen screenText = getTextOnScreen();
        if (screenText == null) {
            Debug.w(getClass(), "get text on screen failed!");
            return result;
        }
        for (AlTextOnScreen.AlPieceOfLink link : screenText.linkList) {
            AlTapInfo tapInfo = bookEng.getInfoByLinkPos(link.pos);
            if (!tapInfo.isLocalLink) {
                continue;
            }
            ReaderSelectionImpl selection = new ReaderSelectionImpl();
            selection.setPageName(PagePositionUtils.fromPageNumber(getPageNumberOfPosition(tapInfo.linkLocalPosition)));
            selection.setPagePosition(PagePositionUtils.fromPosition(tapInfo.linkLocalPosition));
            selection.setText("");
            selection.setStartPosition(PagePositionUtils.fromPosition(link.pos));
            selection.setEndPosition(PagePositionUtils.fromPosition(link.pos));
            selection.setDisplayRects(Arrays.asList(new RectF[]{ createRect(link.rect) }));
            result.add(selection);
        }
        return result;
    }

    public int getTotalPage() {
        AlCurrentPosition position = new AlCurrentPosition();
        if (bookEng.getPageCount(position) != TAL_RESULT.OK) {
            return -1;
        }
        return position.pageCount;
    }

    public int getScreenStartPage() {
        AlCurrentPosition position = new AlCurrentPosition();
        if (bookEng.getPageCount(position) != TAL_RESULT.OK) {
            return -1;
        }
        return getPageNumberOfPosition(position.readPositionStart);
    }

    public int getScreenEndPage() {
        AlCurrentPosition position = new AlCurrentPosition();
        if (bookEng.getPageCount(position) != TAL_RESULT.OK) {
            return -1;
        }
        return getPageNumberOfPosition(position.readPositionEnd);
    }

    public int getScreenStartPosition() {
        AlCurrentPosition position = new AlCurrentPosition();
        if (bookEng.getPageCount(position) != TAL_RESULT.OK) {
            return -1;
        }
        return position.readPositionStart;
    }

    public int getScreenEndPosition() {
        AlCurrentPosition position = new AlCurrentPosition();
        if (bookEng.getPageCount(position) != TAL_RESULT.OK) {
            return -1;
        }
        return position.readPositionEnd - 1;
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

    public int getPositionOfPageNumber(int page) {
        return bookEng.getPositionOfPage(page + 1);
    }

    public int getPageNumberOfPosition(int position) {
        return bookEng.getPageOfPosition(position) - 1;
    }

    public boolean readTableOfContent(final ReaderDocumentTableOfContent toc) {
        AlBookProperties properties = bookEng.getBookProperties(true);
        if (properties.content == null) {
            return false;
        }
        buildTableOfContentTree(toc.getRootEntry(), 0, properties.content, 0);
        return true;
    }

    private int buildTableOfContentTree(ReaderDocumentTableOfContentEntry root,
                                         int currentLevel,
                                         ArrayList<AlOneContent> contentList,
                                         int contentIndex) {
        AlOneContent content;
        ReaderDocumentTableOfContentEntry entry = null;
        int i = contentIndex;
        for (; i < contentList.size(); ) {
            content = contentList.get(i);
            if (content.isBookmark) {
                i++;
                continue;
            }
            if (content.iType == currentLevel) {
                entry = ReaderDocumentTableOfContentEntry.addEntry(root, content.name,
                        content.pageNum, PagePositionUtils.fromPosition(content.positionS));
                i++;
            } else if (content.iType > currentLevel) {
                if (entry == null) {
                    entry = root;
                }
                i += buildTableOfContentTree(entry, content.iType, contentList, i);
            } else {
                return i - contentIndex;
            }
        }
        return i - contentIndex;
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

    public ReaderSelection selectTextOnScreen(PointF start, PointF end) {
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

    public ReaderSelection selectTextOnScreen(int startPos, int endPos) {
        AlTextOnScreen screenText = getTextOnScreen();
        if (screenText == null) {
            Debug.w(getClass(), "get text on screen failed!");
            return null;
        }

        AlTextOnScreen.AlPieceOfText firstPiece = screenText.regionList.get(0);
        AlTextOnScreen.AlPieceOfText lastPiece = screenText.regionList.get(screenText.regionList.size() - 1);
        int start = Math.max(getPieceStart(firstPiece), startPos);
        int end = Math.min(getPieceEnd(lastPiece), endPos);
        return combineSelection(screenText, start, end);
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
            AlTapInfo tapInfo = bookEng.getInfoByTap(x, y, EngBookMyType.TAL_SCREEN_SELECTION_MODE.NONE);
            Debug.d(getClass(), "tap info: " + JSON.toJSONString(tapInfo));
            return tapInfo == null ? -1 : tapInfo.pos;
        } finally {
            bookEng.setSelectionMode(EngBookMyType.TAL_SCREEN_SELECTION_MODE.NONE);
        }
    }

    private ReaderSelectionImpl combineSelection(AlTextOnScreen textOnScreen, int startPos, int endPos) {
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
        selection.setPagePosition(PagePositionUtils.fromPosition(getScreenStartPosition()));
        selection.setText(combineSelectionText(textOnScreen, startIndex, endIndex));
        selection.setStartPosition(PagePositionUtils.fromPosition(getPieceStart(startPiece)));
        selection.setEndPosition(PagePositionUtils.fromPosition(getPieceEnd(endPiece)));
        selection.setDisplayRects(combineSelectionRectangles(textOnScreen, startIndex, endIndex));
        return selection;
    }

    private AlTextOnScreen.AlPieceOfText lastPiece(AlTextOnScreen textOnScreen) {
        return textOnScreen.regionList.get(textOnScreen.regionList.size() - 1);
    }

    private int getPieceStart(AlTextOnScreen.AlPieceOfText piece) {
        return piece.positions[0];
    }

    private int getPieceEnd(AlTextOnScreen.AlPieceOfText piece) {
        return piece.positions[piece.positions.length - 1];
    }

    private int nextTextPosition(AlTextOnScreen textOnScreen, int pos) {
        int index = textOnScreen.findWordByPos(pos);
        if (index == -1) {
            return -1;
        }
        AlTextOnScreen.AlPieceOfText piece = textOnScreen.regionList.get(index);
        int i = 0;
        for (; i < piece.positions.length; i++) {
            if (piece.positions[i] == pos) {
                break;
            }
        }
        if (i < piece.positions.length - 1) {
            return piece.positions[i + 1];
        }

        return index < textOnScreen.regionList.size() - 1 ?
                textOnScreen.regionList.get(index + 1).positions[0] :
                getScreenEndPosition();
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
