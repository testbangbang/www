package com.onyx.kreader.utils;

import android.content.Context;
import android.os.Build;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.utils.RawResourceUtil;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.BuildConfig;
import com.onyx.kreader.ui.data.SingletonSharedPreference;

import java.util.Map;

/**
 * Created by ming on 16/10/18.
 */
public class ReaderConfig {

    private static ReaderConfig ourInstance;
    private static final boolean useDebugConfig = false;

    private ReaderConfig(Context context) {
        String content = readConfig(context);
        if (!StringUtils.isNullOrEmpty(content)) {
            ourInstance = JSON.parseObject(content, ReaderConfig.class);
        }
        if (ourInstance == null) {
            ourInstance = new ReaderConfig();
        }
    }

    private String readConfig(Context context) {
        String content = readFromDebugModel(context);

        if (StringUtils.isNullOrEmpty(content)) {
            content = readFromManufactureAndModel(context);
        }

        if (StringUtils.isNullOrEmpty(content)) {
            content = readFromModel(context);
        }

        if (StringUtils.isNullOrEmpty(content)) {
            content = readFromBrand(context);
        }

        if (StringUtils.isNullOrEmpty(content)) {
            content = readFromDefaultOnyxConfig(context);
        }
        return content;
    }

    private String readFromBrand(Context context) {
        return contentFromRawResource(context, Build.BRAND);
    }

    private String readFromManufactureAndModel(Context context) {
        final String name = Build.MANUFACTURER + "_" + Build.MODEL;
        return contentFromRawResource(context, name);
    }

    private String readFromDebugModel(Context context) {
        if (BuildConfig.DEBUG && useDebugConfig) {
            return contentFromRawResource(context, "debug");
        }
        return null;
    }

    private String readFromModel(Context context) {
        final String name = Build.MODEL;
        return contentFromRawResource(context, name);
    }

    private String readFromDefaultOnyxConfig(Context context) {
        return contentFromRawResource(context, "onyx");
    }

    private String contentFromRawResource(Context context, String name) {
        String content = "";
        try {
            int res = context.getResources().getIdentifier(name.toLowerCase(), "raw", context.getPackageName());
            content = RawResourceUtil.contentOfRawResource(context, res);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return content;
        }
    }

    public ReaderConfig() {
    }

    static public ReaderConfig sharedInstance(Context context) {
        if (ourInstance == null) {
            new ReaderConfig(context);
        }
        return ourInstance;
    }

    private Map<String, Map<String, JSONObject>> keyBinding = null;

    private boolean deleteAcsmAfterFulfillment = false;
    private boolean supportZipCompressedBooks = false;
    private boolean disableDictionaryFunc = false;
    private boolean disableFontFunc = false;
    private boolean disableNoteFunc = false;
    private boolean disableRotationFunc = false;
    private boolean useBigPen = false;
    private boolean defaultUseSystemStatusBar = false;
    private boolean defaultUseReaderStatusBar = false;
    private boolean defaultShowDocTitleInStatusBar = false;
    private boolean disableNavigation = false;
    private boolean hideSelectionModeUiOption = false;
    private boolean hideControlSettings = false;

    private int rotationOffset = 0;
    private int dialogNavigationSettingsSubScreenLandscapeRows = -1;
    private int dialogNavigationSettingsSubScreenLandscapeColumns = -1;
    private int selectionOffsetX = 15;
    private int defaultFontSize = 0;
    private int selectionMoveDistanceThreshold = 8;
    private int gcInterval = 0;

    private String defaultAnnotationHighlightStyle = "Highlight";

    public Map<String, Map<String, JSONObject>> getKeyBinding() {
        return keyBinding;
    }

    public void setKeyBinding(Map<String, Map<String, JSONObject>> keyBinding) {
        this.keyBinding = keyBinding;
    }

    public boolean isDeleteAcsmAfterFulfillment() {
        return deleteAcsmAfterFulfillment;
    }

    public void setDeleteAcsmAfterFulfillment(boolean deleteAcsmAfterFulfillment) {
        this.deleteAcsmAfterFulfillment = deleteAcsmAfterFulfillment;
    }

    public boolean isSupportZipCompressedBooks() {
        return supportZipCompressedBooks;
    }

    public void setSupportZipCompressedBooks(boolean supportZipCompressedBooks) {
        this.supportZipCompressedBooks = supportZipCompressedBooks;
    }

    public boolean isDisableDictionaryFunc() {
        return disableDictionaryFunc;
    }

    public void setDisableDictionaryFunc(boolean disableDictionaryFunc) {
        this.disableDictionaryFunc = disableDictionaryFunc;
    }

    public boolean isDisableFontFunc() {
        return disableFontFunc;
    }

    public void setDisableFontFunc(boolean disableFontFunc) {
        this.disableFontFunc = disableFontFunc;
    }

    public boolean isDisableNoteFunc() {
        return disableNoteFunc;
    }

    public void setDisableNoteFunc(boolean disableNoteFunc) {
        this.disableNoteFunc = disableNoteFunc;
    }

    public boolean isDisableRotationFunc() {
        return disableRotationFunc;
    }

    public void setDisableRotationFunc(boolean disableRotationFunc) {
        this.disableRotationFunc = disableRotationFunc;
    }

    public boolean isUseBigPen() {
        return useBigPen;
    }

    public void setUseBigPen(boolean useBigPen) {
        this.useBigPen = useBigPen;
    }

    public boolean isDefaultUseSystemStatusBar() {
        return defaultUseSystemStatusBar;
    }

    public void setDefaultUseSystemStatusBar(boolean defaultUseSystemStatusBar) {
        this.defaultUseSystemStatusBar = defaultUseSystemStatusBar;
    }

    public boolean isDefaultUseReaderStatusBar() {
        return defaultUseReaderStatusBar;
    }

    public void setDefaultUseReaderStatusBar(boolean defaultUseReaderStatusBar) {
        this.defaultUseReaderStatusBar = defaultUseReaderStatusBar;
    }

    public boolean isDefaultShowDocTitleInStatusBar() {
        return defaultShowDocTitleInStatusBar;
    }

    public void setDefaultShowDocTitleInStatusBar(boolean defaultShowDocTitleInStatusBar) {
        this.defaultShowDocTitleInStatusBar = defaultShowDocTitleInStatusBar;
    }

    public boolean isDisableNavigation() {
        return disableNavigation;
    }

    public void setDisableNavigation(boolean disableNavigation) {
        this.disableNavigation = disableNavigation;
    }

    public boolean isHideSelectionModeUiOption() {
        return hideSelectionModeUiOption;
    }

    public void setHideSelectionModeUiOption(boolean hideSelectionModeUiOption) {
        this.hideSelectionModeUiOption = hideSelectionModeUiOption;
    }

    public boolean isHideControlSettings() {
        return hideControlSettings;
    }

    public void setHideControlSettings(boolean hideControlSettings) {
        this.hideControlSettings = hideControlSettings;
    }

    public int getRotationOffset() {
        return rotationOffset;
    }

    public void setRotationOffset(int rotationOffset) {
        this.rotationOffset = rotationOffset;
    }

    public int getDialogNavigationSettingsSubScreenLandscapeRows() {
        return dialogNavigationSettingsSubScreenLandscapeRows;
    }

    public void setDialogNavigationSettingsSubScreenLandscapeRows(int dialogNavigationSettingsSubScreenLandscapeRows) {
        this.dialogNavigationSettingsSubScreenLandscapeRows = dialogNavigationSettingsSubScreenLandscapeRows;
    }

    public int getDialogNavigationSettingsSubScreenLandscapeColumns() {
        return dialogNavigationSettingsSubScreenLandscapeColumns;
    }

    public void setDialogNavigationSettingsSubScreenLandscapeColumns(int dialogNavigationSettingsSubScreenLandscapeColumns) {
        this.dialogNavigationSettingsSubScreenLandscapeColumns = dialogNavigationSettingsSubScreenLandscapeColumns;
    }

    public int getSelectionOffsetX() {
        return selectionOffsetX;
    }

    public void setSelectionOffsetX(int selectionOffsetX) {
        this.selectionOffsetX = selectionOffsetX;
    }

    public int getDefaultFontSize() {
        return defaultFontSize;
    }

    public void setDefaultFontSize(int defaultFontSize) {
        this.defaultFontSize = defaultFontSize;
    }

    public int getSelectionMoveDistanceThreshold() {
        return selectionMoveDistanceThreshold;
    }

    public void setSelectionMoveDistanceThreshold(int selectionMoveDistanceThreshold) {
        this.selectionMoveDistanceThreshold = selectionMoveDistanceThreshold;
    }

    public int getGcInterval() {
        return gcInterval;
    }

    public void setGcInterval(int gcInterval) {
        this.gcInterval = gcInterval;
    }

    public String getDefaultAnnotationHighlightStyle() {
        return defaultAnnotationHighlightStyle;
    }

    public void setDefaultAnnotationHighlightStyle(String defaultAnnotationHighlightStyle) {
        this.defaultAnnotationHighlightStyle = defaultAnnotationHighlightStyle;
    }

    public SingletonSharedPreference.AnnotationHighlightStyle defaultAnnotationHighlightStyle() {
        SingletonSharedPreference.AnnotationHighlightStyle style = SingletonSharedPreference.AnnotationHighlightStyle.Underline;
        try {
            style = Enum.valueOf(SingletonSharedPreference.AnnotationHighlightStyle.class, defaultAnnotationHighlightStyle);
        } catch (Exception e) {
        }
        return style;
    }
}
