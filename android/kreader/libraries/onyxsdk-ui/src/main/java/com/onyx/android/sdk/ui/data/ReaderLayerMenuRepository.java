package com.onyx.android.sdk.ui.data;

import com.onyx.android.sdk.data.ReaderMenuAction;
import com.onyx.android.sdk.data.ReaderMenuItem;
import com.onyx.android.sdk.ui.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.onyx.android.sdk.ui.data.ReaderLayerMenuRepository.FontSpacingLevel.decrease;
import static com.onyx.android.sdk.ui.data.ReaderLayerMenuRepository.FontSpacingLevel.increase;
import static com.onyx.android.sdk.ui.data.ReaderLayerMenuRepository.FontSpacingLevel.large;
import static com.onyx.android.sdk.ui.data.ReaderLayerMenuRepository.FontSpacingLevel.middle;
import static com.onyx.android.sdk.ui.data.ReaderLayerMenuRepository.FontSpacingLevel.small;

/**
 * Created by joy on 8/25/16.
 */
public class ReaderLayerMenuRepository {

    public enum FontSpacingLevel {
        small, middle, large, decrease, increase
    }

    public static ReaderLayerMenuItem[] fixedPageMenuItems = new ReaderLayerMenuItem[]{
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Group, ReaderMenuAction.FONT, null, R.string.font_face, "", R.drawable.ic_dialog_reader_menu_font),
            ReaderLayerMenuItem.createSimpleMenuItem(ReaderMenuAction.FONT_SET_FONT_FACE, R.id.font_face_group),
            ReaderLayerMenuItem.createSimpleMenuItem(ReaderMenuAction.FONT_SET_MORE_FONT_FACE, R.id.more_font),

            ReaderLayerMenuItem.createSimpleMenuItem(ReaderMenuAction.FONT_SET_FONT_SIZE, R.id.text_view_font_size_0),
            ReaderLayerMenuItem.createSimpleMenuItem(ReaderMenuAction.FONT_SET_FONT_SIZE, R.id.text_view_font_size_1),
            ReaderLayerMenuItem.createSimpleMenuItem(ReaderMenuAction.FONT_SET_FONT_SIZE, R.id.text_view_font_size_2),
            ReaderLayerMenuItem.createSimpleMenuItem(ReaderMenuAction.FONT_SET_FONT_SIZE, R.id.text_view_font_size_3),
            ReaderLayerMenuItem.createSimpleMenuItem(ReaderMenuAction.FONT_SET_FONT_SIZE, R.id.text_view_font_size_4),
            ReaderLayerMenuItem.createSimpleMenuItem(ReaderMenuAction.FONT_SET_FONT_SIZE, R.id.text_view_font_size_5),
            ReaderLayerMenuItem.createSimpleMenuItem(ReaderMenuAction.FONT_SET_FONT_SIZE, R.id.text_view_font_size_6),
            ReaderLayerMenuItem.createSimpleMenuItem(ReaderMenuAction.FONT_SET_FONT_SIZE, R.id.text_view_font_size_7),

            ReaderLayerMenuItem.createSimpleMenuItem(ReaderMenuAction.FONT_DECREASE_FONT_SIE, R.id.image_view_decrease_font_size),
            ReaderLayerMenuItem.createSimpleMenuItem(ReaderMenuAction.FONT_INCREASE_FONT_SIZE, R.id.image_view_increase_font_size),

            ReaderLayerMenuItem.createSimpleMenuItem(ReaderMenuAction.FONT_SET_LINE_SPACING, R.id.image_view_small_line_spacing, small),
            ReaderLayerMenuItem.createSimpleMenuItem(ReaderMenuAction.FONT_SET_LINE_SPACING, R.id.image_view_middle_line_spacing, middle),
            ReaderLayerMenuItem.createSimpleMenuItem(ReaderMenuAction.FONT_SET_LINE_SPACING, R.id.image_view_large_line_spacing, large),
            ReaderLayerMenuItem.createSimpleMenuItem(ReaderMenuAction.FONT_SET_LINE_SPACING, R.id.image_view_decrease_line_spacing, decrease),
            ReaderLayerMenuItem.createSimpleMenuItem(ReaderMenuAction.FONT_SET_LINE_SPACING, R.id.image_view_increase_line_spacing, increase),

            ReaderLayerMenuItem.createSimpleMenuItem(ReaderMenuAction.FONT_SET_PAGE_MARGINS, R.id.image_view_small_page_margins, small),
            ReaderLayerMenuItem.createSimpleMenuItem(ReaderMenuAction.FONT_SET_PAGE_MARGINS, R.id.image_view_middle_page_margins, middle),
            ReaderLayerMenuItem.createSimpleMenuItem(ReaderMenuAction.FONT_SET_PAGE_MARGINS, R.id.image_view_large_page_margins, large),
            ReaderLayerMenuItem.createSimpleMenuItem(ReaderMenuAction.FONT_SET_PAGE_MARGINS, R.id.image_view_decrease_page_margins, decrease),
            ReaderLayerMenuItem.createSimpleMenuItem(ReaderMenuAction.FONT_SET_PAGE_MARGINS, R.id.image_view_increase_page_margins, increase),

            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Group, ReaderMenuAction.ZOOM, null, R.string.reader_layer_menu_zoom, "", R.drawable.ic_dialog_reader_menu_scale),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.ZOOM_IN, null, R.string.reader_layer_menu_zoom_in, "", R.drawable.ic_dialog_reader_menu_scale_magnify),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.ZOOM_OUT, null, R.string.reader_layer_menu_zoom_out, "", R.drawable.ic_dialog_reader_menu_scale_shrink),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.ZOOM_TO_PAGE, null, R.string.reader_layer_menu_zoom_to_page, "", R.drawable.ic_dialog_reader_menu_scale_fit_page),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.ZOOM_BY_CROP_PAGE, null, R.string.reader_layer_menu_zoom_by_crop_page, "", R.drawable.ic_dialog_reader_menu_scale_cut_four),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.ZOOM_BY_CROP_WIDTH, null, R.string.reader_layer_menu_zoom_by_crop_width, "", R.drawable.ic_dialog_reader_menu_scale_cut_two),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.ZOOM_BY_RECT, null, R.string.reader_layer_menu_zoom_by_rect, "", R.drawable.ic_dialog_reader_menu_scale_choose),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Group, ReaderMenuAction.NAVIGATION, null, R.string.reader_layer_menu_navigation, "", R.drawable.ic_dialog_reader_menu_browse),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.IMAGE_REFLOW, null, R.string.reader_layer_menu_font_reflow, "", R.drawable.ic_dialog_reader_menu_scale_reset),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.GAMMA_CORRECTION, null, R.string.gamma_correction, "", R.drawable.ic_dialog_reader_menu_browse_gamma),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.NAVIGATION_COMIC_MODE, null, R.string.reader_layer_menu_navigation_comic_mode, "", R.drawable.ic_dialog_reader_menu_browse_cartoon),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.NAVIGATION_ARTICLE_MODE, null, R.string.reader_layer_menu_navigation_article_mode, "", R.drawable.ic_dialog_reader_menu_browse_thesis),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.NAVIGATION_RESET, null, R.string.reader_layer_menu_navigation_reset, "", R.drawable.ic_dialog_reader_menu_browse_reset),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.NAVIGATION_MORE_SETTINGS, null, R.string.reader_layer_menu_navigation_more_settings, "", R.drawable.ic_dialog_reader_menu_browse_more),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Group, ReaderMenuAction.NOTE, null, R.string.reader_layer_menu_notes, "", R.drawable.ic_dialog_reader_menu_note),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.DIRECTORY_SCRIBBLE, null, R.string.reader_layer_menu_directory_scribble, "", R.drawable.ic_dialog_reader_menu_browse_write),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.SHOW_NOTE, null, R.string.reader_layer_menu_show_scribble, "", R.drawable.ic_dialog_reader_menu_note_show),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.NOTE_EXPORT, null, R.string.reader_layer_menu_directory_export, "", R.drawable.ic_dialog_reader_menu_browse_export),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Group, ReaderMenuAction.ROTATION, null, R.string.reader_layer_menu_rotation, "", R.drawable.ic_dialog_reader_menu_revolve),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.ROTATION_ROTATE_90, null, 0, "", R.drawable.ic_dialog_reader_menu_revolve_left),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.ROTATION_ROTATE_0, null, 0, "", R.drawable.ic_dialog_reader_menu_revolve_up),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.ROTATION_ROTATE_270, null, 0, "", R.drawable.ic_dialog_reader_menu_revolve_right),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.ROTATION_ROTATE_180, null, 0, "", R.drawable.ic_dialog_reader_menu_revolve_down),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Group, ReaderMenuAction.MORE, null, R.string.reader_layer_menu_more, "", R.drawable.ic_dialog_reader_menu_more),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.TTS, null, R.string.reader_layer_menu_tts, "", R.drawable.ic_dialog_reader_menu_tts),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.FRONT_LIGHT, null, R.string.reader_layer_menu_front_light, "", R.drawable.ic_dialog_reader_menu_frontlight),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.REFRESH, null, R.string.reader_layer_menu_refresh, "", R.drawable.ic_dialog_reader_menu_refresh_black),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.SETTINGS, null, R.string.reader_layer_menu_settings, "", R.drawable.ic_dialog_reader_menu_setting),
    };

    public static List<ReaderLayerMenuItem> createFromArray(ReaderLayerMenuItem[] flattenArray) {
        ArrayList<ReaderLayerMenuItem> menuGroupList = new ArrayList<>();
        ReaderLayerMenuItem currentGroup = null;
        for (ReaderLayerMenuItem item : flattenArray) {
            if (item.getItemType() == ReaderMenuItem.ItemType.Group) {
                ReaderLayerMenuItem group = new ReaderLayerMenuItem(item);
                menuGroupList.add(group);
                currentGroup = group;
            } else {
                (currentGroup.getChildren()).add(new ReaderLayerMenuItem(item.getItemType(),
                        item.getAction(), currentGroup, item.getTitleResourceId(), item.getTitle(), item.getDrawableResourceId(), item.getItemId(), item.getValue()));
            }
        }
        return menuGroupList;
    }

    public static List<ReaderLayerMenuItem> createFromArray(ReaderLayerMenuItem[] flattenArray, Set<ReaderMenuAction> excludingSet) {
        ArrayList<ReaderLayerMenuItem> menuGroupList = new ArrayList<>();
        ReaderLayerMenuItem currentGroup = null;
        for (int i = 0; i < flattenArray.length; i++) {
            ReaderLayerMenuItem item = flattenArray[i];
            if (excludingSet.contains(item.getAction())) {
                if (item.getItemType() != ReaderMenuItem.ItemType.Group) {
                    continue;
                }
                do {
                    i++;
                    item = flattenArray[i];
                } while (item.getItemType() != ReaderMenuItem.ItemType.Group);
            }
            if (item.getItemType() == ReaderMenuItem.ItemType.Group) {
                ReaderLayerMenuItem group = new ReaderLayerMenuItem(item);
                menuGroupList.add(group);
                currentGroup = group;
            } else {
                (currentGroup.getChildren()).add(new ReaderLayerMenuItem(item.getItemType(),
                        item.getAction(), currentGroup, item.getTitleResourceId(), item.getTitle(), item.getDrawableResourceId(), item.getItemId(), item.getValue()));
            }
        }
        return menuGroupList;
    }
}
