package com.onyx.kreader.plugins.alreader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
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
import com.neverland.engbook.forpublic.EngBookMyType;
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
import com.onyx.kreader.common.Debug;
import com.onyx.kreader.utils.PagePositionUtils;

import java.io.File;
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
        return position.pageCurrent;
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

    public List<AlOneSearchResult> search(final String text) {
        bookEng.findText(text);
        return bookEng.getFindTextResult();
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

}
