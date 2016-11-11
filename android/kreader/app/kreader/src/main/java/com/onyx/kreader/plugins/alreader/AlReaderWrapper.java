package com.onyx.kreader.plugins.alreader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.neverland.engbook.bookobj.AlBookEng;
import com.neverland.engbook.bookobj.AlUtilFunc;
import com.neverland.engbook.forpublic.AlBitmap;
import com.neverland.engbook.forpublic.AlBookOptions;
import com.neverland.engbook.forpublic.AlCurrentPosition;
import com.neverland.engbook.forpublic.AlEngineNotifyForUI;
import com.neverland.engbook.forpublic.AlEngineOptions;
import com.neverland.engbook.forpublic.AlOneSearchResult;
import com.neverland.engbook.forpublic.AlPublicProfileOptions;
import com.neverland.engbook.forpublic.EngBookMyType;
import com.neverland.engbook.forpublic.TAL_CODE_PAGES;
import com.neverland.engbook.forpublic.TAL_RESULT;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.api.ReaderDocumentOptions;
import com.onyx.kreader.api.ReaderPluginOptions;
import com.onyx.kreader.api.ReaderSearchOptions;

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
    private AlPublicProfileOptions profileCurrent = null;
    private AlPublicProfileOptions profileDay = new AlPublicProfileOptions();
    private AlPublicProfileOptions profileNight = new AlPublicProfileOptions();


    public AlReaderWrapper(final Context context, final ReaderPluginOptions pluginOptions) {
        bookEng = new AlBookEng();
        bookEng.initializeBookEngine(createEngineOptions(context, pluginOptions));
        bookEng.initializeOwner(getEngineNotifyForUI());
        bookEng.setNewProfileParameters(getProfileDay(pluginOptions));
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
        profileDay.background = null;
        profileDay.backgroundMode = AlPublicProfileOptions.BACK_TILE_NONE;
        profileDay.bold = false;
        profileDay.font_name = "XZ";
        profileDay.font_monospace = "Monospace";
        profileDay.font_size = 36;
        profileDay.setMargins(5); // in percent
        profileDay.twoColumn = false;
        profileDay.colorText = 0x000000;
        profileDay.colorTitle = 0x9c27b0;
        profileDay.colorBack = 0xf0f0f0;
        profileDay.interline = 0;
        profileDay.specialModeRoll = false;
        profileDay.sectionNewScreen = true;
        profileDay.justify = true;
        profileDay.notesOnPage = true;
        return profileDay;
    }

    public void draw(final Bitmap bitmap, final int width, final int height) {
        AlBitmap bmp = bookEng.getPageBitmap(EngBookMyType.TAL_PAGE_INDEX.CURR, width, height);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(bmp.bmp, 0, 0, new Paint());
    }

    public int getTotalPage() {
        return bookEng.getPageCount(new AlCurrentPosition());
    }

    public boolean nextPage() {
        int ret = bookEng.gotoPosition(EngBookMyType.TAL_GOTOCOMMAND.NEXTPAGE, 0);
        return ret == TAL_RESULT.OK;
    }

    public boolean prevPage() {
        int ret = bookEng.gotoPosition(EngBookMyType.TAL_GOTOCOMMAND.PREVPAGE, 0);
        return ret == TAL_RESULT.OK;
    }

    public boolean gotoPage(int pos) {
        int ret = bookEng.gotoPosition(EngBookMyType.TAL_GOTOCOMMAND.POSITION, pos);
        return ret == TAL_RESULT.OK;
    }

    public List<AlOneSearchResult> search(final String text) {
        bookEng.findText(text);
        return bookEng.getFindTextResult();
    }

}
