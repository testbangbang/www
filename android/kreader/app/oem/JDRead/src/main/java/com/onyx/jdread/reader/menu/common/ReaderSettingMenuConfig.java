package com.onyx.jdread.reader.menu.common;

import com.onyx.jdread.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huxiaomao on 2018/1/4.
 */

public class ReaderSettingMenuConfig {
    public static final List<Integer> progressMenuGroup = new ArrayList<>();
    public static final List<Integer> brightnessMenuGroup = new ArrayList<>();
    public static final List<Integer> textMenuGroup = new ArrayList<>();
    public static final List<Integer> imageMenuGroup = new ArrayList<>();
    public static final List<Integer> customMenuGroup = new ArrayList<>();

    static {
        progressMenuGroup.add(R.id.reader_setting_system_bar);
        progressMenuGroup.add(R.id.reader_setting_title_bar);
        progressMenuGroup.add(R.id.reader_setting_page_info_bar);
        progressMenuGroup.add(R.id.reader_setting_function_bar);
    }

    static {
        brightnessMenuGroup.add(R.id.reader_setting_system_bar);
        brightnessMenuGroup.add(R.id.reader_setting_title_bar);
        brightnessMenuGroup.add(R.id.reader_setting_brightness_bar);
        brightnessMenuGroup.add(R.id.reader_setting_function_bar);
    }

    static {
        textMenuGroup.add(R.id.reader_setting_text_setting_bar);
    }

    //reader_image_setting_bar
    static {
        imageMenuGroup.add(R.id.reader_setting_image_setting_bar);
    }

    //reader_customize_format_bar
    static {
        imageMenuGroup.add(R.id.reader_setting_image_setting_bar);
    }

    static {
        customMenuGroup.add(R.id.reader_setting_customize_format_bar);
    }

}
