package com.onyx.android.sdk.ui.data;

import com.onyx.android.sdk.data.ReaderMenuItem;
import com.onyx.android.sdk.ui.R;

import java.net.URI;

/**
 * Created by joy on 8/25/16.
 */
public class ReaderLayerMenuRepository {
    public static ReaderLayerMenuItem[] fixedPageMenuItems = new ReaderLayerMenuItem[] {
        new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Group, URI.create("/Zoom"), null, R.string.reader_layer_menu_zoom, "", R.drawable.ic_dialog_reader_menu_scale_black),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, URI.create("/Zoom/FontReflow"), null, R.string.reader_layer_menu_font_reflow, "", R.drawable.ic_dialog_reader_menu_scale_reset_black),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, URI.create("/Zoom/ZoomIn"), null, R.string.reader_layer_menu_zoom_in, "", R.drawable.ic_dialog_reader_menu_scale_magnify_black),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, URI.create("/Zoom/ZoomOut"), null, R.string.reader_layer_menu_zoom_out, "", R.drawable.ic_dialog_reader_menu_scale_shrink_black),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, URI.create("/Zoom/ToPage"), null, R.string.reader_layer_menu_zoom_to_page, "", R.drawable.ic_dialog_reader_menu_scale_fit_page_black),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, URI.create("/Zoom/ToWidth"), null, R.string.reader_layer_menu_zoom_to_width, "", R.drawable.ic_dialog_reader_menu_scale_fit_width_black),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, URI.create("/Zoom/ByRect"), null, R.string.reader_layer_menu_zoom_by_rect, "", R.drawable.ic_dialog_reader_menu_scale_choose_black),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Group, URI.create("/Navigation"), null, R.string.reader_layer_menu_navigation, "", R.drawable.ic_dialog_reader_menu_browse_black),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, URI.create("/Navigation/ComicMode"), null, R.string.reader_layer_menu_navigation_comic_mode, "", R.drawable.ic_dialog_reader_menu_browse_cartoon_black),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, URI.create("/Navigation/ArticleMode"), null, R.string.reader_layer_menu_navigation_article_mode, "", R.drawable.ic_dialog_reader_menu_browse_thesis_black),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, URI.create("/Navigation/Reset"), null, R.string.reader_layer_menu_navigation_reset, "", R.drawable.ic_dialog_reader_menu_browse_reset_black),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, URI.create("/Navigation/MoreSetting"), null, R.string.reader_layer_menu_navigation_more_settings, "", R.drawable.ic_dialog_reader_menu_browse_more_black),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Group, URI.create("/Directory"), null, R.string.reader_layer_menu_directory, "", R.drawable.ic_dialog_reader_menu_note_black),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, URI.create("/Directory/TOC"), null, R.string.reader_layer_menu_directory_toc, "", R.drawable.ic_dialog_reader_menu_browse_list_black),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, URI.create("/Directory/Bookmark"), null, R.string.reader_layer_menu_directory_bookmark, "", R.drawable.ic_dialog_reader_menu_browse_bookmark_black),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, URI.create("/Directory/Note"), null, R.string.reader_layer_menu_directory_note, "", R.drawable.ic_dialog_reader_menu_browse_label_black),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, URI.create("/Directory/Export"), null, R.string.reader_layer_menu_directory_export, "", R.drawable.ic_dialog_reader_menu_browse_export_black),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Group, URI.create("/Rotation"), null, R.string.reader_layer_menu_rotation, "", R.drawable.ic_dialog_reader_menu_revolve_black),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, URI.create("/Rotation/Rotation90"), null, R.string.reader_layer_menu_rotation_rotate90, "", R.drawable.ic_dialog_reader_menu_revolve_left_black),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, URI.create("/Rotation/Rotation0"), null, R.string.reader_layer_menu_rotation_rotate0, "", R.drawable.ic_dialog_reader_menu_revolve_up_black),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, URI.create("/Rotation/Rotation270"), null, R.string.reader_layer_menu_rotation_rotate270, "", R.drawable.ic_dialog_reader_menu_revolve_right_black),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, URI.create("/Rotation/Rotation180"), null, R.string.reader_layer_menu_rotation_rotate180, "", R.drawable.ic_dialog_reader_menu_revolve_down_black),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Group, URI.create("/More"), null, R.string.reader_layer_menu_more, "", R.drawable.ic_dialog_reader_menu_more),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, URI.create("/TTS"), null, R.string.reader_layer_menu_tts, "", R.drawable.ic_dialog_reader_menu_tts),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, URI.create("/FrontLight"), null, R.string.reader_layer_menu_front_light, "", R.drawable.ic_dialog_reader_menu_frontlight),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, URI.create("/Refresh"), null, R.string.reader_layer_menu_refresh, "", R.drawable.ic_dialog_reader_menu_refresh_black),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, URI.create("/Setting"), null, R.string.reader_layer_menu_settings, "", R.drawable.ic_dialog_reader_menu_setting),
    };
}
