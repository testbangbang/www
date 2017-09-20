package com.onyx.android.sdk.reader.host.options;

import android.content.Context;
import android.graphics.RectF;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.neverland.engbook.forpublic.TAL_CODE_PAGES;
import com.onyx.android.sdk.data.PageConstants;
import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.api.ReaderChineseConvertType;
import com.onyx.android.sdk.reader.api.ReaderDocumentCategory;
import com.onyx.android.sdk.utils.LocaleUtils;
import com.onyx.android.sdk.reader.api.ReaderPluginOptions;
import com.onyx.android.sdk.reader.host.impl.ReaderDocumentOptionsImpl;
import com.onyx.android.sdk.reader.host.navigation.NavigationArgs;
import com.onyx.android.sdk.reader.utils.GObject;

import java.util.List;

/**
 * Key value map.
 * User: zhuzeng
 * Date: 12/27/13
 * Time: 9:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class BaseOptions {

    transient static public final String SPECIAL_SCALE_TAG = "special_scale";
    transient static public final String ACTUAL_SCALE_TAG = "actual_scale";
    transient static public final String MANUAL_CROP_REGION_TAG = "manual_crop_region";
    transient static public final String SCREEN_SPLIT_POINT_TAG = "screen_split_point";
    transient static public final String DOCUMENT_CATEGORY_TAG = "document_category";
    transient static public final String CODE_PAGE_TAG = "code_page";
    transient static public final String CHINESE_CONVERT_TYPE_TAG = "chinese_convert_type";
    transient static public final String CUSTOM_FORM_ENABLED_TAG = "custom_form_enabled";
    transient static public final String FONT_SIZE_TAG = "font_size";
    transient static public final String DEFAULT_FONT_SIZE = "default_font_size";
    transient static public final String FONT_FACE_TAG = "font_face";
    transient static public final String LINE_SPACING_TAG = "line_spacing";
    transient static public final String PARAGRAPH_INDENT_TAG = "para_indent";
    transient static public final String LAYOUT_TYPE_TAG = "layout_type";
    transient static public final String LOCATION_TAG = "location";
    transient static public final String PASSWORD_TAG = "ps";
    transient static public final String ZIP_PASSWORD_TAG = "zip_pass";
    transient static public final String PAGE_NUMBER_TAG = "pn";
    transient static public final String READER_MATRIX_TAG = "rm";
    transient static public final String READER_NAVIGATION_MATRIX_TAG = "nav_mode";
    transient static public final String CURRENT_PAGE_TAG = "current_page";
    transient static public final String TOTAL_PAGE_TAG = "total_page";
    transient static public final String VIEWPORT_TAG = "viewport";
    transient static public final String CROP_LIST = "crop_list";
    transient static public final String CROP_VALUE = "crop_value";
    transient static public final String GAMMA_LEVEL = "gamma_level";
    transient static public final String TEXT_GAMMA_LEVEL = "text_gamma_level";
    transient static public final String ENHANCE_LEVEL = "enhance_level";
    transient static public final String NAVIGATION_MODE = "navigation_mode";
    transient static public final String NAVIGATION_ROWS = "navigation_rows";
    transient static public final String NAVIGATION_COLUMNS = "navigation_columns";
    transient static public final String NAVIGATION_CURRENT_ROW = "current_navigation_row";
    transient static public final String NAVIGATION_CURRENT_COLUMN = "current_navigation_column";
    transient static public final String NAVIGATION_ARGUMENTS = "navigation_arguments";

    transient static public final String PAGE_LEFT_MARGIN = "page_left_margin";
    transient static public final String PAGE_TOP_MARGIN = "page_top_margin";
    transient static public final String PAGE_RIGHT_MARGIN = "page_right_margin";
    transient static public final String PAGE_BOTTOM_MARGIN = "page_bottom_margin";
    transient static public final String REFLOW_SETTINGS = "reflow_settings";
    transient static public final String WAVEFORM_MODE = "waveform";
    transient static public final String RESET_SCALE = "reset_scale";
    transient static public final String ORIENTATION = "orientation";
    transient static public final String MD5 = "md5";

    private static final float fallbackFontSize = 36.0f;
    public static float defaultFontSize = fallbackFontSize;

    transient private static int noGamma = 100;
    transient private static int globalDefaultGamma = 100;
    transient private static int globalDefaultTextGamma = 150;
    public static final float INVALID_FLOAT_VALUE = - 1;
    public static final int INVALID_INT_VALUE = - 1;

    private GObject backend;

    public BaseOptions() {
        super();
        backend = new GObject();
    }

    public void clear() {
        backend.getBackend().clear();
    }

    public GObject getBackend() {
        return backend;
    }

    public void setBackend(final GObject b) {
        backend = b;
    }

    public static BaseOptions optionsFromJSONString(final String jsonString) {
        BaseOptions options = new BaseOptions();
        GObject data = JSON.parseObject(jsonString, GObject.class);
        if (data != null) {
            options.setBackend(data);
        }
        return options;
    }

    public String toJSONString() {
        return JSON.toJSONString(backend);
    }

    public String getPassword() {
        if (backend.hasKey(PASSWORD_TAG)) {
            String password = backend.getString(PASSWORD_TAG);
            if (password == null) {
                return "";
            }
            return password;
        }
        return "";
    }

    public void setPassword(final String password) {
        backend.putString(PASSWORD_TAG, password);
    }

    public String getZipPassword() {
        if (backend.hasKey(ZIP_PASSWORD_TAG)) {
            String zipPassword = backend.getString(ZIP_PASSWORD_TAG);
            if (zipPassword == null) {
                return "";
            }
            return zipPassword;
        }
        return "";
    }

    public void setZipPassword(final String zipPassword) {
        backend.putString(ZIP_PASSWORD_TAG, zipPassword);
    }

    public void setLastAccessedLocation(final String location) {
        backend.putString(LOCATION_TAG, location);
    }

    public final String getLastAccessedLocation() {
        return backend.getString(LOCATION_TAG);
    }

    public void setCropThreshold(double value) {
        backend.putDouble(CROP_VALUE, value);
    }

    public double getCropThreshold() {
        if (!backend.hasKey(CROP_VALUE)) {
            return 0.0;
        }
        return backend.getDouble(CROP_VALUE);
    }

    static public double getDefaultCropValue() {
        return 0.01;
    }

    public static int getNoGamma() {
        return noGamma;
    }

    public static int getGlobalDefaultGamma() {
        return globalDefaultGamma;
    }

    public static void setGlobalDefaultGamma(int globalDefaultGamma) {
        BaseOptions.globalDefaultGamma = globalDefaultGamma;
    }

    public boolean isGammaCorrectionEnabled() {
        return getGammaLevel() > noGamma;
    }

    public float getGammaLevel() {
        if (!backend.hasKey(GAMMA_LEVEL)) {
            return getGlobalDefaultGamma();
        }
        return getGlobalDefaultGamma();
    }

    public void setGamma(float gamma) {
        backend.putFloat(GAMMA_LEVEL, gamma);
    }

    public static int getGlobalDefaultTextGamma() {
        return globalDefaultTextGamma;
    }

    public static void setGlobalDefaultTextGamma(int globalDefaultGamma) {
        BaseOptions.globalDefaultTextGamma = globalDefaultGamma;
    }

    public boolean isTextGamaCorrectionEnabled() {
        return getTextGammaLevel() > noGamma;
    }

    public float getTextGammaLevel() {
        if (!backend.hasKey(TEXT_GAMMA_LEVEL)) {
            return getGlobalDefaultTextGamma();
        }
        return getGlobalDefaultTextGamma();
    }

    public void setTextGamma(float gamma) {
        backend.putFloat(TEXT_GAMMA_LEVEL, gamma);
    }

    public boolean isEmboldenLevelEnabled() {
        return getEmboldenLevel() > 0;
    }

    public int getEmboldenLevel() {
        if (!backend.hasKey(ENHANCE_LEVEL)) {
            return 0;
        }
        return backend.getInt(ENHANCE_LEVEL);
    }

    public void setEmboldenLevel(int level) {
        backend.putInt(ENHANCE_LEVEL, level);
    }

    public String getLayoutType() {
        return backend.getString(LAYOUT_TYPE_TAG);
    }

    public void setLayoutType(final String type) {
        backend.putString(LAYOUT_TYPE_TAG, type);
    }

    public int getSpecialScale()  {
        if (backend.hasKey(SPECIAL_SCALE_TAG)) {
            return backend.getInt(SPECIAL_SCALE_TAG);
        }
        return getDefaultSpecialScale();
    }

    public void setSpecialScale(int value) {
        backend.putInt(SPECIAL_SCALE_TAG, value);
    }

    public float getActualScale() {
        if (backend.hasKey(ACTUAL_SCALE_TAG)) {
            try {
                return backend.getFloat(ACTUAL_SCALE_TAG);
            } catch (Throwable tr) {
            }
        }
        return 0f;
    }

    public void setActualScale(final float value) {
        backend.putFloat(ACTUAL_SCALE_TAG, value);
    }

    public List<RectF> getManualCropRegions() {
        try {
            return JSONObject.parseArray(backend.getString(MANUAL_CROP_REGION_TAG), RectF.class);
        } catch (Exception e) {
            return null;
        }
    }

    public void setManualCropRegions(List<RectF> cropRegions) {
        backend.putObject(MANUAL_CROP_REGION_TAG, cropRegions);
    }


    static public int getDefaultSpecialScale() {
        return PageConstants.SCALE_TO_PAGE;
    }

    static public double getScaleDelta() {
        return 0.5;
    }

    static public double getMaxFontSize() {
        return 15;
    }

    static public double getMinFontSize() {
        return 1.0;
    }

    public static double getDefaultFixedPageFontSize() {
        return 1.1;
    }

    public static void setDefaultStreamDocFontSize(float fontSize) {
        defaultFontSize = fontSize;
    }

    static public double getFontSizeDelta(boolean streamDoc) {
        if (streamDoc) {
            return getStreamDocFontSizeDelta();
        }
        return getFixedPageFontSizeDelta();
    }

    static public double getStreamDocFontSizeDelta() {
        return 1;
    }

    static public double getFixedPageFontSizeDelta() {
        return 1;
    }

    static public double fontSizeForStreamDoc(double size) {
        return size * 6 + 6;
    }

    static public double fontSizeFromActualPixelSize(int pixelSize) {
        return (pixelSize / 6) - 1;
    }

    public ReaderDocumentCategory getDocumentCategory() {
        if (!backend.hasKey(DOCUMENT_CATEGORY_TAG)) {
            return ReaderDocumentCategory.NORMAL;
        }
        return ReaderDocumentCategory.valueOf(backend.getString(DOCUMENT_CATEGORY_TAG));
    }

    public void setDocumentCategory(ReaderDocumentCategory documentType) {
        backend.putObject(DOCUMENT_CATEGORY_TAG, documentType.toString());
    }

    public int getCodePage() {
        return backend.getInt(CODE_PAGE_TAG, TAL_CODE_PAGES.AUTO);
    }

    public void setCodePage(int codePage) {
        backend.putInt(CODE_PAGE_TAG, codePage);
    }

    public ReaderChineseConvertType getChineseConvertType() {
        if (!backend.hasKey(CHINESE_CONVERT_TYPE_TAG)) {
            return ReaderChineseConvertType.NONE;
        }
        return ReaderChineseConvertType.valueOf(backend.getString(CHINESE_CONVERT_TYPE_TAG));
    }

    public void setChineseConvertType(ReaderChineseConvertType convertType) {
        backend.putObject(CHINESE_CONVERT_TYPE_TAG, convertType.toString());
    }

    public boolean isCustomFormEnabled () {
        if (!backend.hasKey(CUSTOM_FORM_ENABLED_TAG)) {
            return false;
        }
        return backend.getBoolean(CUSTOM_FORM_ENABLED_TAG);
    }

    public void setCustomFormEnabled(boolean enabled) {
        backend.putBoolean(CUSTOM_FORM_ENABLED_TAG, enabled);
    }

    public float getFontSize() {
        if (!backend.hasKey(FONT_SIZE_TAG)) {
            return INVALID_FLOAT_VALUE;
        }
        float size = backend.getFloat(FONT_SIZE_TAG);
        if (size <= 0) {
            return INVALID_FLOAT_VALUE;
        }
        return size;
    }

    public void setFontSize(float size) {
        backend.putDouble(FONT_SIZE_TAG, size);
    }

    public String getFontFace() {
        if (!backend.hasKey(FONT_FACE_TAG)) {
            return null;
        }
        return backend.getString(FONT_FACE_TAG);
    }

    public int getLineSpacing() {
        if (!backend.hasKey(LINE_SPACING_TAG)) {
            return INVALID_INT_VALUE;
        }
        return backend.getInt(LINE_SPACING_TAG);
    }

    public float getParagraphIndent() {
        if (!backend.hasKey(PARAGRAPH_INDENT_TAG)) {
            return ReaderTextStyle.DEFAULT_CHARACTER_INDENT.getIndent();
        }
        return backend.getFloat(PARAGRAPH_INDENT_TAG);
    }

    public void setFontFace(String fontName) {
        backend.putString(FONT_FACE_TAG, fontName);
    }

    public void setLineSpacing(int lineSpacing) {
        backend.putInt(LINE_SPACING_TAG, lineSpacing);
    }

    public void setParagraphIndent(final float indent) {
        backend.putFloat(PARAGRAPH_INDENT_TAG, indent);
    }

    public void setPageNumber(int pageNumber) {
        backend.putInt(PAGE_NUMBER_TAG, pageNumber);
    }

    public int getPageNumber() {
        return backend.getInt(PAGE_NUMBER_TAG);
    }


    public final String getReaderMatrix() {
        return  backend.getString(READER_MATRIX_TAG);
    }

    public final String getReaderNavigationMatrix() {
        return  backend.getString(READER_NAVIGATION_MATRIX_TAG);
    }

    public String getCurrentPage() {
        return backend.getString(CURRENT_PAGE_TAG);
    }

    public void setCurrentPage(final String pageName) {
        backend.putString(CURRENT_PAGE_TAG, pageName);
    }

    public int getTotalPage() {
        return backend.getInt(TOTAL_PAGE_TAG);
    }

    public void setTotalPage(int t) {
        backend.putInt(TOTAL_PAGE_TAG, t);
    }

    public int getValue(final String key, int low, int up, int defaultValue) {
        if (backend.hasKey(key)) {
            int value = backend.getInt(key);
            if (value >= low && value <= up) {
                return value;
            }
        }
        return defaultValue;
    }

    public void setViewport(final RectF rect) {
        backend.putString(VIEWPORT_TAG, JSON.toJSONString(rect));
    }

    public final RectF getViewport() {
        if (backend.hasKey(VIEWPORT_TAG)) {
            return JSON.parseObject(backend.getString(VIEWPORT_TAG), RectF.class);
        }
        return null;
    }

    public int getNavigationRows() {
        return getValue(NAVIGATION_ROWS, 1, 3, getDefaultNavigationRows());
    }

    public void setNavigationRows(int rows){
        backend.putInt(NAVIGATION_ROWS, rows);
    }

    public int getNavigationColumns() {
        return getValue(NAVIGATION_COLUMNS, 1,3, getDefaultNavigationRows());
    }

    public void setNavigationColumns(int columns){
        backend.putInt(NAVIGATION_COLUMNS, columns);
    }

    public int getCurrentNavigationRow() {
        return getValue(NAVIGATION_CURRENT_ROW, 0, 2, getDefaultNavigationInitRow());
    }

    public void setCurrentNavigationRow(int row) {
        backend.putInt(NAVIGATION_CURRENT_ROW, row);
    }

    public int getCurrentNavigationColumn() {
        return getValue(NAVIGATION_CURRENT_COLUMN, 0, 2, getDefaultNavigationInitColumn());
    }

    public void setCurrentNavigationColumn(int column) {
        backend.putInt(NAVIGATION_CURRENT_COLUMN, column);
    }

    public NavigationArgs getNavigationArgs() {
        return backend.getObject(NAVIGATION_ARGUMENTS, NavigationArgs.class);
    }

    public void setNavigationArgs(NavigationArgs args) {
        backend.putObject(NAVIGATION_ARGUMENTS, args);
    }

    static public int getDefaultNavigationRows() {
        return 1;
    }

    static public int getDefaultNavigationColumns() {
        return 1;
    }

    static public int getDefaultNavigationInitRow() {
        return 0;
    }

    static public int getDefaultNavigationInitColumn() {
        return 0;
    }

    static public int maxEmboldenLevel() {
        return 5;
    }

    static public int minEmboldenLevel() {
        return 0;
    }

    static public int maxGammaLevel() {
        return 200;
    }

    static public int minGammaLevel() {
        return 100;
    }

    public int getLeftMargin() {
        if (backend.hasKey(PAGE_LEFT_MARGIN)) {
            return backend.getInt(PAGE_LEFT_MARGIN);
        }
        return INVALID_INT_VALUE;
    }

    public void setLeftMargin(int value) {
        backend.putInt(PAGE_LEFT_MARGIN, value);
    }

    public int getTopMargin() {
        if (backend.hasKey(PAGE_TOP_MARGIN)) {
            return backend.getInt(PAGE_TOP_MARGIN);
        }
        return INVALID_INT_VALUE;
    }

    public void setTopMargin(int value) {
        backend.putInt(PAGE_TOP_MARGIN, value);
    }

    public int getRightMargin() {
        if (backend.hasKey(PAGE_RIGHT_MARGIN)) {
            return backend.getInt(PAGE_RIGHT_MARGIN);
        }
        return INVALID_INT_VALUE;
    }

    public void setRightMargin(int value) {
        backend.putInt(PAGE_RIGHT_MARGIN, value);
    }

    public int getBottomMargin() {
        if (backend.hasKey(PAGE_BOTTOM_MARGIN)) {
            return backend.getInt(PAGE_BOTTOM_MARGIN);
        }
        return INVALID_INT_VALUE;
    }

    public void setBottomMargin(int value) {
        backend.putInt(PAGE_BOTTOM_MARGIN, value);
    }

    public void setReflowOptions(final String settings) {
        backend.putString(REFLOW_SETTINGS, settings);
    }

    public final String getReflowSettings() {
        if (backend.hasKey(REFLOW_SETTINGS)) {
            return backend.getString(REFLOW_SETTINGS);
        }
        return null;
    }

    public final String getWaveformMode() {
        if (backend.hasKey(WAVEFORM_MODE)) {
            return backend.getString(WAVEFORM_MODE);
        }
        return null;
    }

    public boolean getResetScale() {
        if (backend.hasKey(RESET_SCALE)) {
            return backend.getBoolean(RESET_SCALE);
        }
        return false;
    }

    public int getOrientation() {
        if (backend.hasKey(ORIENTATION)) {
            return backend.getInt(ORIENTATION);
        }
        return -1;
    }

    public void setOrientation(int orientation) {
        backend.putInt(ORIENTATION, orientation);
    }

    public static double getFallbackFontSize(Context context){
        double fallFontSize = fallbackFontSize;
        fallFontSize = 0;//context.getResources().getInteger(R.integer.default_font_size);
        return fallFontSize;
    }

    public final ReaderDocumentOptionsImpl documentOptions() {
        return new ReaderDocumentOptionsImpl(getPassword(), getZipPassword(),
                getCodePage(), LocaleUtils.getLocaleDefaultCodePage(),
                getChineseConvertType(), isCustomFormEnabled());
    }

    public final ReaderPluginOptions pluginOptions() {
        return null;
    }

    public final String getMd5() {
        if (backend.hasKey(MD5)) {
            return backend.getString(MD5);
        }
        return null;
    }

    public void setMd5(final String value) {
        backend.putString(MD5, value);
    }
}
