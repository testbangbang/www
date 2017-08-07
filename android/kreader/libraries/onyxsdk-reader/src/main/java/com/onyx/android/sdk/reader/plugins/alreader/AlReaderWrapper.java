package com.onyx.android.sdk.reader.plugins.alreader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Pair;

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
import com.neverland.engbook.forpublic.TAL_RESULT;
import com.neverland.engbook.unicode.AlUnicode;
import com.neverland.engbook.util.AlStyles;
import com.neverland.engbook.util.EngBitmap;
import com.neverland.engbook.util.TTFInfo;
import com.neverland.engbook.util.TTFScan;
import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.api.ReaderChineseConvertType;
import com.onyx.android.sdk.reader.api.ReaderImage;
import com.onyx.android.sdk.utils.Benchmark;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.reader.api.ReaderDocumentOptions;
import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContent;
import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContentEntry;
import com.onyx.android.sdk.reader.api.ReaderPluginOptions;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.reader.api.ReaderSentence;
import com.onyx.android.sdk.reader.api.ReaderTextSplitter;
import com.onyx.android.sdk.reader.host.impl.ReaderSelectionImpl;
import com.onyx.android.sdk.reader.host.impl.ReaderTextSplitterImpl;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;
import com.onyx.android.sdk.utils.RectUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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

     static String DEFAULT_FONT_NAME = "Serif";

    private String filePath;
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
        setStyle(style);
    }

    public void setViewSize(int width, int height) {
        bookEng.setNewScreenSize(width, height);
    }

    public void activateDeviceDRM(String deviceId, String certificate) {
        bookEng.activateDeviceDRM(deviceId, certificate);
    }

    public long openDocument(final String path,  final ReaderDocumentOptions documentOptions) {
        filePath = path;
        AlBookOptions bookOpt = new AlBookOptions();
        bookOpt.codePage = documentOptions.getCodePage();
        bookOpt.codePageDefault = documentOptions.getCodePageFallback();
        bookOpt.formatOptions = 0;
        bookOpt.readPosition = 0;
        if (bookEng.openBook(path, bookOpt) != TAL_RESULT.OK) {
            return ERROR_FILE_INVALID;
        }
        setChineseConvertType(documentOptions.getChineseConvertType());
        return NO_ERROR;
    }

    public void updateDocumentOptions(ReaderDocumentOptions documentOptions, ReaderPluginOptions pluginOptions) {
        int readPosition = getScreenStartPosition();
        closeDocument();
        openDocument(filePath, documentOptions);
        gotoPosition(readPosition);
    }

    public void closeDocument() {
        if (bookEng == null) {
            return;
        }
        resetScreenState();
        bookEng.closeBook();
    }

    public Bitmap readCover() {
        AlBookProperties bookProperties = bookEng.getBookProperties(false);
        if (bookProperties.coverImageData == null) {
            return null;
        }
        try {
            return BitmapFactory.decodeByteArray(bookProperties.coverImageData, 0,
                    bookProperties.coverImageData.length);
        } catch (Throwable tr) {
            return null;
        }
    }

    public String metadataString(final String tag) {
        AlBookProperties bookProperties = bookEng.getBookProperties(false);
        if ("Title".compareTo(tag) == 0) {
            return bookProperties.title;
        } else if ("Author".compareTo(tag) == 0) {
            if (bookProperties.authors == null) {
                return "";
            }
            return StringUtils.join(bookProperties.authors, ", ");
        } else {
            Debug.d(getClass(), "metadataString: unknown tag -> " + tag);
        }
        return "";
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

        resetScreenState();
    }

    public void setChineseConvertType(ReaderChineseConvertType convertType) {
        switch (convertType) {
            case NONE:
                bookEng.chineseConvert = AlBookEng.SimplifiedAndTraditionalChineseConvert.NONE;
                break;
            case SIMPLIFIED_TO_TRADITIONAL:
                bookEng.chineseConvert = AlBookEng.SimplifiedAndTraditionalChineseConvert.SIMPLIFIED_TO_TRADITIONAL;
                break;
            case TRADITIONAL_TO_SIMPLIFIED:
                bookEng.chineseConvert = AlBookEng.SimplifiedAndTraditionalChineseConvert.TRADITIONAL_TO_SIMPLIFIED;
                break;
        }
    }

    private AlEngineOptions createEngineOptions(final Context context, final ReaderPluginOptions pluginOptions) {
        engineOptions = new AlEngineOptions();
        engineOptions.appInstance = context;
        engineOptions.runInOneThread = true;
        engineOptions.font_catalogs_addon = pluginOptions.getFontDirectories().toArray(new String[0]);
        engineOptions.hyph_lang = EngBookMyType.TAL_HYPH_LANG.ENGRUS;
        engineOptions.useScreenPages = EngBookMyType.TAL_SCREEN_PAGES_COUNT.SIZE;
        engineOptions.pageSize4Use = AlEngineOptions.AL_USEAUTO_PAGESIZE;
        engineOptions.chinezeFormatting = true;
        engineOptions.drawLinkInternal = false;
        engineOptions.externalBitmap = new AlBitmap();

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
        profile.font_name = DEFAULT_FONT_NAME;
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
            profile.font_name = DEFAULT_FONT_NAME;
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
        engineOptions.externalBitmap.release();
        engineOptions.externalBitmap = null;
        benchmark.report("getPageBitmap");

        resetScreenState();
    }

    public String getScreenText() {
        AlTextOnScreen screenText = getTextOnScreen();
        if (!checkScreenText(screenText)) {
            return null;
        }
        return combineSelectionText(screenText, 0, screenText.regionList.size() - 1);
    }

    public ReaderSentence getSentence(final int startPosition) {
        if (startPosition < getScreenStartPosition() ||
                startPosition > getScreenEndPosition()) {
            return null;
        }
        AlTextOnScreen screenText = getTextOnScreen();
        if (!checkScreenText(screenText)) {
            return null;
        }
        final int MAX_SENTENCE_LENGTH = 200;
        int startPos = -1;
        int endPos = -1;
        int count = 0;
        boolean firstPos = true;
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
                count++;
                endPos = piece.positions[i];
                if (firstPos || endPos <= startPosition) {
                    firstPos = false;
                    startPos = endPos;
                    continue;
                }
                if (count >= MAX_SENTENCE_LENGTH) {
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

        ReaderSelectionImpl selection = combineSelection(screenText, startPos, endPos);
        boolean endOfScreen = endPos == getPieceEnd(lastPiece(screenText));
        boolean endOfDocument = endOfScreen && isLastPage();
        int nextPos = nextTextPosition(screenText, endPos);
        ReaderSentence sentence = ReaderSentence.create(selection, nextPos, endOfScreen, endOfDocument);
        return sentence;
    }

    public List<ReaderSelection> getPageLinks() {
        List<ReaderSelection> result = new ArrayList<>();

        AlTextOnScreen screenText = getTextOnScreen();
        if (!checkScreenText(screenText)) {
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

    public List<ReaderImage> getPageImages() {
        List<ReaderImage> result = new ArrayList<>();
        AlTextOnScreen screenText = getTextOnScreen();
        if (!checkScreenImage(screenText)) {
            return result;
        }
        for (final AlTextOnScreen.AlPieceOfImage image : screenText.imageList) {
            result.add(new ReaderImage() {
                @Override
                public RectF getRectangle() {
                    return new RectF(image.rect.x0, image.rect.y0,
                            image.rect.x1, image.rect.y1);
                }

                @Override
                public Bitmap getBitmap() {
                    return image.bitmap;
                }
            });
        }
        return result;
    }

    public int getTotalPage() {
        AlCurrentPosition position = new AlCurrentPosition();
        if (bookEng.getPageCount(position) != TAL_RESULT.OK) {
            Debug.w(getClass(), "getTotalPage: get page count failed!");
            return -1;
        }
        return position.pageCount;
    }

    public int getScreenStartPage() {
        AlCurrentPosition position = new AlCurrentPosition();
        if (bookEng.getPageCount(position) != TAL_RESULT.OK) {
            Debug.w(getClass(), "getScreenStartPage: get page count failed!");
            return -1;
        }
        return getPageNumberOfPosition(position.readPositionStart);
    }

    public int getScreenEndPage() {
        AlCurrentPosition position = new AlCurrentPosition();
        if (bookEng.getPageCount(position) != TAL_RESULT.OK) {
            Debug.w(getClass(), "getScreenEndPage: get page count failed!");
            return -1;
        }
        return getPageNumberOfPosition(position.readPositionEnd);
    }

    public int getScreenStartPosition() {
        AlCurrentPosition position = new AlCurrentPosition();
        if (bookEng.getPageCount(position) != TAL_RESULT.OK) {
            Debug.w(getClass(), "getScreenStartPosition: get page count failed!");
            return -1;
        }
        return position.readPositionStart;
    }

    public int getScreenEndPosition() {
        AlCurrentPosition position = new AlCurrentPosition();
        if (bookEng.getPageCount(position) != TAL_RESULT.OK) {
            Debug.w(getClass(), "getScreenEndPosition: get page count failed!");
            return -1;
        }
        return position.readPositionEnd - 1;
    }

    public boolean isFirstPage() {
        AlCurrentPosition position = new AlCurrentPosition();
        if (bookEng.getPageCount(position) != TAL_RESULT.OK) {
            Debug.w(getClass(), "isFirstPage: get page count failed!");
            return false;
        }
        return position.isFirstPage;
    }

    public boolean isLastPage() {
        AlCurrentPosition position = new AlCurrentPosition();
        if (bookEng.getPageCount(position) != TAL_RESULT.OK) {
            Debug.w(getClass(), "isLastPage: get page count failed!");
            return false;
        }
        return position.isLastPage;
    }

    public boolean nextPage() {
        int ret = bookEng.gotoPosition(EngBookMyType.TAL_GOTOCOMMAND.NEXTPAGE, 0);
        resetScreenState();
        return ret == TAL_RESULT.OK;
    }

    public boolean prevPage() {
        int ret = bookEng.gotoPosition(EngBookMyType.TAL_GOTOCOMMAND.PREVPAGE, 0);
        resetScreenState();
        return ret == TAL_RESULT.OK;
    }

    public boolean gotoPosition(int pos) {
        int ret = bookEng.gotoPosition(EngBookMyType.TAL_GOTOCOMMAND.POSITION, pos);
        resetScreenState();
        return ret == TAL_RESULT.OK;
    }

    public boolean gotoPage(int page) {
        int ret = bookEng.gotoPage(page + 1);
        resetScreenState();
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
                selection.setText(getTextOfSearchResult(result));
                selection.setLeftText(getLeftContextOfSearchResult(result));
                selection.setRightText(getRightContextOfSearchResult(result));
                selection.setDisplayRects(new ArrayList<RectF>());
                list.add(selection);
            }
        } finally {
            // clear finds in book engine
            bookEng.findText(null);
        }

        return true;
    }

    private String getTextOfSearchResult(AlOneSearchResult searchResult) {
        if (StringUtils.isNullOrEmpty(searchResult.context)) {
            return "";
        }
        int leftIndex = searchResult.context.indexOf((char) AlStyles.CHAR_MARKER_FIND_S);
        if (leftIndex == -1 || leftIndex >= searchResult.context.length() - 1) {
            return "";
        }
        int rightIndex = searchResult.context.indexOf((char) AlStyles.CHAR_MARKER_FIND_E);
        if (rightIndex == -1 || rightIndex >= searchResult.context.length() - 1) {
            return "";
        }
        return searchResult.context.substring(leftIndex + 1, rightIndex);
    }

    private String getLeftContextOfSearchResult(AlOneSearchResult searchResult) {
        if (StringUtils.isNullOrEmpty(searchResult.context)) {
            return "";
        }
        int idx = searchResult.context.indexOf((char) AlStyles.CHAR_MARKER_FIND_S);
        if (idx == -1) {
            return "";
        }
        return searchResult.context.substring(0, idx);
    }

    private String getRightContextOfSearchResult(AlOneSearchResult searchResult) {
        if (StringUtils.isNullOrEmpty(searchResult.context)) {
            return "";
        }
        int idx = searchResult.context.indexOf((char) AlStyles.CHAR_MARKER_FIND_E);
        if (idx == -1) {
            return "";
        }
        if (idx >= searchResult.context.length() - 1) {
            return "";
        }
        return searchResult.context.substring(idx + 1);
    }

    public ReaderSelection selectTextOnScreen(PointF start, PointF end) {
        AlTextOnScreen screenText = getTextOnScreen();
        if (!checkScreenText(screenText)) {
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

    public ReaderSelection selectWordOnScreen(PointF point, final ReaderTextSplitter splitter) {
        AlTextOnScreen screenText = getTextOnScreen();
        if (!checkScreenText(screenText)) {
            return null;
        }
        int pos = hitTest((int)point.x, (int)point.y);
        if (pos == -1) {
            return null;
        }

        String ch = getCharAtPos(screenText, pos);
        if (!AlUnicode.isChineze(ch.charAt(0))) {
            // simplifying latter work
            return selectTextOnScreen(point, point);
        }

        String leftText = getLeftText(screenText, pos, 50);
        String rightText = getRightText(screenText, pos, 50);
        if (ch == null || leftText == null || rightText == null) {
            return null;
        }

        int leftOffset = splitter.getTextLeftBoundary(ch, leftText, rightText);
        int leftPos = previousTextPosition(screenText, pos, leftOffset);
        int rightOffset = splitter.getTextRightBoundary(ch, leftText, rightText);
        int rightPos = nextTextPosition(screenText, pos, rightOffset);
        return combineSelection(screenText, leftPos, rightPos);
    }

    private String getCharAtPos(AlTextOnScreen textOnScreen, int pos) {
        Pair<Integer, Integer> index = findWordByPos(textOnScreen, pos);
        if (index == null) {
            return null;
        }

        AlTextOnScreen.AlPieceOfText piece = textOnScreen.regionList.get(index.first);
        for (int i = 0; i < piece.positions.length; i++) {
            if (piece.positions[i] == pos) {
                return String.valueOf(piece.word.charAt(i));
            }
        }
        return null;
    }

    private String getLeftText(AlTextOnScreen textOnScreen, int pos, int length) {
        if (isTextBeginningPosition(textOnScreen, pos)) {
            return "";
        }
        int prevPos = previousTextPosition(textOnScreen, pos);
        if (prevPos < 0) {
            return null;
        }
        Pair<Integer, Integer> index = findWordByPos(textOnScreen, prevPos);
        if (index == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (int i = index.first; i >= 0; i--) {
            AlTextOnScreen.AlPieceOfText piece = textOnScreen.regionList.get(i);
            int j = piece.positions.length - 1;
            if (first) {
                j = index.second;
                first = false;
            }
            for (; j >= 0; j--) {
                builder.insert(0, piece.word.charAt(j));
                if (builder.length() >= length) {
                    return builder.toString();
                }
            }
        }
        return builder.toString();
    }

    private String getRightText(AlTextOnScreen textOnScreen, int pos, int length) {
        if (isTextEndPosition(textOnScreen, pos)) {
            return "";
        }
        int nextPos = nextTextPosition(textOnScreen, pos);
        if (nextPos < 0) {
            return null;
        }
        Pair<Integer, Integer> index = findWordByPos(textOnScreen, nextPos);
        if (index == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (int i = index.first; i <= textOnScreen.regionList.size() - 1; i++) {
            AlTextOnScreen.AlPieceOfText piece = textOnScreen.regionList.get(i);
            int j = 0;
            if (first) {
                j = index.second;
                first = false;
            }
            for (; j <= piece.positions.length - 1; j++) {
                builder.append(piece.word.charAt(j));
                if (builder.length() >= length) {
                    return builder.toString();
                }
            }
        }
        return builder.toString();
    }

    public ReaderSelection selectTextOnScreen(int startPos, int endPos) {
        AlTextOnScreen screenText = getTextOnScreen();
        if (!checkScreenText(screenText)) {
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

    private boolean checkScreenText(AlTextOnScreen textOnScreen) {
        return textOnScreen != null && textOnScreen.regionList.size() > 0;
    }

    private boolean checkScreenImage(AlTextOnScreen textOnScreen) {
        return textOnScreen != null && textOnScreen.imageList != null;
    }

    private int hitTest(int x, int y) {
        try {
            AlTapInfo tapInfo = bookEng.getInfoByTap(x, y, EngBookMyType.TAL_SCREEN_SELECTION_MODE.NONE);
            return tapInfo == null ? -1 : tapInfo.pos;
        } finally {
            bookEng.setSelectionMode(EngBookMyType.TAL_SCREEN_SELECTION_MODE.NONE);
        }
    }

    private Pair<Integer, Integer> findWordByPos(AlTextOnScreen textOnScreen, int pos) {
        for (int i = 0; i < textOnScreen.regionList.size(); i++) {
            AlTextOnScreen.AlPieceOfText a = textOnScreen.regionList.get(i);
            int s = a.positions[0];
            int e = a.positions[a.positions.length - 1];
            if (pos >= s && pos <= e) {
                int offset = getOffsetInPiece(a, pos);
                if (offset == -1) {
                    assert false;
                    return null;
                }
                return new Pair<>(i, offset);
            }
        }

        return null;
    }

    private ReaderSelectionImpl combineSelection(AlTextOnScreen textOnScreen, int startPos, int endPos) {
        Pair<Integer, Integer> startIndex = findWordByPos(textOnScreen, startPos);
        Pair<Integer, Integer> endIndex = findWordByPos(textOnScreen, endPos);
        if (startIndex == null || endIndex == null) {
            return null;
        }
        if (startIndex.first > endIndex.first) {
            Pair<Integer, Integer> tmp = startIndex;
            startIndex = endIndex;
            endIndex = tmp;
        }
        final AlTextOnScreen.AlPieceOfText startPiece = screenText.regionList.get(startIndex.first);
        final AlTextOnScreen.AlPieceOfText endPiece = screenText.regionList.get(endIndex.first);
        ReaderSelectionImpl selection = new ReaderSelectionImpl();
        selection.setPageName(PagePositionUtils.fromPageNumber(getPageNumberOfPosition(getPieceStart(startPiece))));
        selection.setPagePosition(PagePositionUtils.fromPosition(getScreenStartPosition()));
        selection.setText(combineSelectionText(textOnScreen, startIndex.first, endIndex.first));
        selection.setStartPosition(PagePositionUtils.fromPosition(getPieceStart(startPiece)));
        selection.setEndPosition(PagePositionUtils.fromPosition(getPieceEnd(endPiece)));
        selection.setDisplayRects(combineSelectionRectangles(textOnScreen, startIndex.first, endIndex.first));
        return selection;
    }


    private AlTextOnScreen.AlPieceOfText firstPiece(AlTextOnScreen textOnScreen) {
        return textOnScreen.regionList.get(0);
    }

    private AlTextOnScreen.AlPieceOfText lastPiece(AlTextOnScreen textOnScreen) {
        return textOnScreen.regionList.get(textOnScreen.regionList.size() - 1);
    }

    private boolean isFirstPiece(AlTextOnScreen textOnScreen, int index) {
        return index == 0;
    }

    private boolean isLastPiece(AlTextOnScreen textOnScreen, int index) {
        return index == textOnScreen.regionList.size() - 1;
    }

    private boolean isTextBeginningPosition(AlTextOnScreen textOnScreen, int pos) {
        Pair<Integer, Integer> index = findWordByPos(textOnScreen, pos);
        if (index == null) {
            return false;
        }
        return index.first == 0 && index.second == 0;
    }

    private boolean isTextEndPosition(AlTextOnScreen textOnScreen, int pos) {
        Pair<Integer, Integer> index = findWordByPos(textOnScreen, pos);
        if (index == null) {
            return false;
        }
        return isLastPiece(textOnScreen, index.first) &&
                getPieceEnd(lastPiece(textOnScreen)) == index.second;
    }

    private int getPieceStart(AlTextOnScreen.AlPieceOfText piece) {
        return piece.positions[0];
    }

    private int getPieceEnd(AlTextOnScreen.AlPieceOfText piece) {
        return piece.positions[piece.positions.length - 1];
    }

    private int getOffsetInPiece(AlTextOnScreen.AlPieceOfText piece, int pos) {
        for (int i = 0; i < piece.positions.length; i++) {
            if (piece.positions[i] == pos) {
                return i;
            }
        }
        return -1;
    }

    private int previousTextPosition(AlTextOnScreen textOnScreen, int pos, int offset) {
        Pair<Integer, Integer> index = findWordByPos(textOnScreen, pos);
        if (index == null) {
            return -1;
        }
        boolean first = true;
        for (int i = index.first; i >= 0; i--) {
            AlTextOnScreen.AlPieceOfText piece = textOnScreen.regionList.get(i);
            int anchor = piece.positions.length - 1;
            if (first) {
                first = false;
                if (index.second >= offset) {
                    return piece.positions[index.second - offset];
                }
                anchor = index.second - 1;
                if (anchor < 0) {
                    continue;
                }
            }
            int length = anchor + 1;
            if (length >= offset) {
                return piece.positions[anchor - offset + 1];
            } else {
                offset -= length;
            }
        }
        return getPieceStart(firstPiece(textOnScreen));
    }

    private int previousTextPosition(AlTextOnScreen textOnScreen, int pos) {
        Pair<Integer, Integer> index = findWordByPos(textOnScreen, pos);
        if (index == null) {
            return -1;
        }

        AlTextOnScreen.AlPieceOfText piece = textOnScreen.regionList.get(index.first);
        if (index.second > 0) {
            return piece.positions[index.second - 1];
        }
        return isFirstPiece(textOnScreen, index.first) ? -1 :
                getPieceEnd(textOnScreen.regionList.get(index.first -1));
    }

    private int nextTextPosition(AlTextOnScreen textOnScreen, int pos, int offset) {
        Pair<Integer, Integer> index = findWordByPos(textOnScreen, pos);
        if (index == null) {
            return -1;
        }
        boolean first = true;
        for (int i = index.first; i >= 0; i++) {
            AlTextOnScreen.AlPieceOfText piece = textOnScreen.regionList.get(i);
            int anchor = 0;
            if (first) {
                first = false;
                if (index.second + offset < piece.positions.length) {
                    return piece.positions[index.second + offset];
                }
                anchor = index.second + 1;
                if (anchor >= piece.positions.length) {
                    continue;
                }
            }
            int length = piece.positions.length - anchor;
            if (length >= offset) {
                return piece.positions[anchor + offset - 1];
            } else {
                offset -= length;
            }
        }
        return getPieceStart(firstPiece(textOnScreen));
    }

    private int nextTextPosition(AlTextOnScreen textOnScreen, int pos) {
        Pair<Integer, Integer> index = findWordByPos(textOnScreen, pos);
        if (index == null) {
            return -1;
        }

        AlTextOnScreen.AlPieceOfText piece = textOnScreen.regionList.get(index.first);
        if (index.second < piece.positions.length - 1) {
            return piece.positions[index.second + 1];
        }

        return isLastPiece(textOnScreen, index.first) ? -1 :
                textOnScreen.regionList.get(index.first + 1).positions[0];
    }

    private String combineSelectionText(AlTextOnScreen textOnScreen, int startIndex, int endIndex) {
        StringBuilder builder = new StringBuilder(textOnScreen.regionList.get(startIndex).word);
        for (int i = startIndex + 1; i <= endIndex; i++) {
            if (builder.length() > 0 && !AlUnicode.isChineze(builder.charAt(builder.length() - 1)) &&
                    !AlUnicode.isChineze(builder.charAt(builder.length() - 1))) {
                // don't insert space between Chinese characters
                builder.append(" ");
            }
            builder.append(textOnScreen.regionList.get(i).word);
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
