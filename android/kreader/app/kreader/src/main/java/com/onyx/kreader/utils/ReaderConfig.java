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

    private Map<String, Map<String, JSONObject>> key_binding = null;

    private boolean delete_acsm_after_fulfillment = false;
    private boolean support_zip_compressed_books = false;
    private boolean disable_dictionary_func = false;
    private boolean disable_font_func = false;
    private boolean disable_note_func = false;
    private boolean disable_rotation_func = false;
    private boolean use_big_pen = false;
    private boolean default_use_system_status_bar = false;
    private boolean default_use_reader_status_bar = false;
    private boolean default_show_doc_title_in_status_bar = false;
    private boolean disable_navigation = false;
    private boolean hide_selection_mode_ui_option = false;
    private boolean hide_control_settings = false;

    private int rotation_offset = 0;
    private int dialog_navigation_settings_subscreen_landscape_rows = -1;
    private int dialog_navigation_settings_subscreen_landscape_columns = -1;
    private int selection_offset_x = 15;
    private int default_font_size = 0;
    private int selection_move_distance_threshold = 8;
    private int gc_interval = 0;

    private String default_annotation_highlight_style = "Highlight";

    public Map<String, Map<String, JSONObject>> getKey_binding() {
        return key_binding;
    }

    public void setKey_binding(Map<String, Map<String, JSONObject>> key_binding) {
        this.key_binding = key_binding;
    }

    public boolean isDelete_acsm_after_fulfillment() {
        return delete_acsm_after_fulfillment;
    }

    public void setDelete_acsm_after_fulfillment(boolean delete_acsm_after_fulfillment) {
        this.delete_acsm_after_fulfillment = delete_acsm_after_fulfillment;
    }

    public boolean isSupport_zip_compressed_books() {
        return support_zip_compressed_books;
    }

    public void setSupport_zip_compressed_books(boolean support_zip_compressed_books) {
        this.support_zip_compressed_books = support_zip_compressed_books;
    }

    public boolean isDisable_dictionary_func() {
        return disable_dictionary_func;
    }

    public void setDisable_dictionary_func(boolean disable_dictionary_func) {
        this.disable_dictionary_func = disable_dictionary_func;
    }

    public boolean isDisable_font_func() {
        return disable_font_func;
    }

    public void setDisable_font_func(boolean disable_font_func) {
        this.disable_font_func = disable_font_func;
    }

    public boolean isDisable_note_func() {
        return disable_note_func;
    }

    public void setDisable_note_func(boolean disable_note_func) {
        this.disable_note_func = disable_note_func;
    }

    public boolean isDisable_rotation_func() {
        return disable_rotation_func;
    }

    public void setDisable_rotation_func(boolean disable_rotation_func) {
        this.disable_rotation_func = disable_rotation_func;
    }

    public boolean isUse_big_pen() {
        return use_big_pen;
    }

    public void setUse_big_pen(boolean use_big_pen) {
        this.use_big_pen = use_big_pen;
    }

    public boolean isDefault_use_system_status_bar() {
        return default_use_system_status_bar;
    }

    public void setDefault_use_system_status_bar(boolean default_use_system_status_bar) {
        this.default_use_system_status_bar = default_use_system_status_bar;
    }

    public boolean isDefault_use_reader_status_bar() {
        return default_use_reader_status_bar;
    }

    public void setDefault_use_reader_status_bar(boolean default_use_reader_status_bar) {
        this.default_use_reader_status_bar = default_use_reader_status_bar;
    }

    public boolean isDefault_show_doc_title_in_status_bar() {
        return default_show_doc_title_in_status_bar;
    }

    public void setDefault_show_doc_title_in_status_bar(boolean default_show_doc_title_in_status_bar) {
        this.default_show_doc_title_in_status_bar = default_show_doc_title_in_status_bar;
    }

    public boolean isDisable_navigation() {
        return disable_navigation;
    }

    public void setDisable_navigation(boolean disable_navigation) {
        this.disable_navigation = disable_navigation;
    }

    public boolean isHide_selection_mode_ui_option() {
        return hide_selection_mode_ui_option;
    }

    public void setHide_selection_mode_ui_option(boolean hide_selection_mode_ui_option) {
        this.hide_selection_mode_ui_option = hide_selection_mode_ui_option;
    }

    public boolean isHide_control_settings() {
        return hide_control_settings;
    }

    public void setHide_control_settings(boolean hide_control_settings) {
        this.hide_control_settings = hide_control_settings;
    }

    public int getRotation_offset() {
        return rotation_offset;
    }

    public void setRotation_offset(int rotation_offset) {
        this.rotation_offset = rotation_offset;
    }

    public int getDialog_navigation_settings_subscreen_landscape_rows() {
        return dialog_navigation_settings_subscreen_landscape_rows;
    }

    public void setDialog_navigation_settings_subscreen_landscape_rows(int dialog_navigation_settings_subscreen_landscape_rows) {
        this.dialog_navigation_settings_subscreen_landscape_rows = dialog_navigation_settings_subscreen_landscape_rows;
    }

    public int getDialog_navigation_settings_subscreen_landscape_columns() {
        return dialog_navigation_settings_subscreen_landscape_columns;
    }

    public void setDialog_navigation_settings_subscreen_landscape_columns(int dialog_navigation_settings_subscreen_landscape_columns) {
        this.dialog_navigation_settings_subscreen_landscape_columns = dialog_navigation_settings_subscreen_landscape_columns;
    }

    public int getSelection_offset_x() {
        return selection_offset_x;
    }

    public void setSelection_offset_x(int selection_offset_x) {
        this.selection_offset_x = selection_offset_x;
    }

    public int getDefault_font_size() {
        return default_font_size;
    }

    public void setDefault_font_size(int default_font_size) {
        this.default_font_size = default_font_size;
    }

    public int getSelection_move_distance_threshold() {
        return selection_move_distance_threshold;
    }

    public void setSelection_move_distance_threshold(int selection_move_distance_threshold) {
        this.selection_move_distance_threshold = selection_move_distance_threshold;
    }

    public int getGc_interval() {
        return gc_interval;
    }

    public void setGc_interval(int gc_interval) {
        this.gc_interval = gc_interval;
    }

    public String getDefault_annotation_highlight_style() {
        return default_annotation_highlight_style;
    }

    public void setDefault_annotation_highlight_style(String default_annotation_highlight_style) {
        this.default_annotation_highlight_style = default_annotation_highlight_style;
    }

    public SingletonSharedPreference.AnnotationHighlightStyle defaultAnnotationHighlightStyle() {
        SingletonSharedPreference.AnnotationHighlightStyle style = SingletonSharedPreference.AnnotationHighlightStyle.Underline;
        try {
            style = Enum.valueOf(SingletonSharedPreference.AnnotationHighlightStyle.class, default_annotation_highlight_style);
        } catch (Exception e) {
        }
        return style;
    }
}
