package com.onyx.kreader.reflow;

import com.alibaba.fastjson.JSON;
import com.onyx.kreader.utils.HashUtils;

/**
 * @author joy
 *
 */
public class ImageReflowSettings {
    public int dev_dpi = 265;
    public int dev_width = 758;
    public int dev_height = 1024;

    transient public int page_width = 758;
    transient public int page_height = 1024;

    public int trim = 1;
    public int wrap = 1;
    public int columns = 2;
    public int indent = 1;
    public int straighten = 0;
    /**
     * values of 0, 90, 180, 270
     */
    public int rotate = 0;
    /**
     * auto: -1
     * left: 0
     * center: 1
     * right: 2
     * full: 3
     */
    public int justification = -1;

    /**
     * small: 0.05
     * medium: 0.15
     * large: 0.375
     */
    public double word_spacing = 0.05;
    /**
     * small: 1.0
     * medium: 8.0
     * large: 15.0
     */
    public double defect_size = 1.0;
    /**
     * small: 1.0
     * medium: 1.2
     * large: 1.4
     */
    public double line_spacing = 1.0;
    /**
     * small: 0.05
     * medium: 0.10
     * high: 0.15
     */
    public double margin = 0.05;
    /**
     * result image DPI: quality * dev_dpi
     * John: change to output size.
     * largest: 0.4
     * large: 0.5
     * medium: 0.6
     * small: 0.7
     */
    public double quality = 0.6;
    /**
     * lightest: 2.0
     * lighter: 1.5
     * default: 1.0
     * darker: 0.5
     * darkest: 0.2
     */
    public double contrast = 1.0;

    static public ImageReflowSettings createSettings() {
        ImageReflowSettings settings = new ImageReflowSettings();
        return settings;
    }

    static public ImageReflowSettings copy(final ImageReflowSettings settings) {
        return new ImageReflowSettings(settings);
    }

    static public ImageReflowSettings fromJsonString(final String settings) {
        ImageReflowSettings object = JSON.parseObject(settings, ImageReflowSettings.class);
        return object;
    }

    public String jsonString() {
        String jsonString = JSON.toJSONString(this);
        return jsonString;
    }

    public String md5() {
        return HashUtils.md5(jsonString());
    }

    private ImageReflowSettings() {
        super();
    }

    private ImageReflowSettings(ImageReflowSettings settings) {
        super();
        update(settings);
    }

    public void update(final ImageReflowSettings newSettings) {
        dev_dpi = newSettings.dev_dpi;
        dev_width = newSettings.dev_width;
        dev_height = newSettings.dev_height;

        page_width = newSettings.page_width;
        page_height = newSettings.page_height;

        trim = newSettings.trim;
        wrap = newSettings.wrap;
        columns = newSettings.columns;
        indent = newSettings.indent;
        straighten = newSettings.straighten;
        justification = newSettings.justification;

        word_spacing = newSettings.word_spacing;
        defect_size = newSettings.defect_size;
        line_spacing = newSettings.line_spacing;
        margin = newSettings.margin;
        quality = newSettings.quality;
        contrast = newSettings.contrast;
        rotate = newSettings.rotate;
    }

}
