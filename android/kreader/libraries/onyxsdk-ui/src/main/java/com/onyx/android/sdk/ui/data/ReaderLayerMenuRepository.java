package com.onyx.android.sdk.ui.data;

import com.onyx.android.sdk.data.ReaderMenuAction;
import com.onyx.android.sdk.data.ReaderMenuItem;
import com.onyx.android.sdk.ui.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * Created by joy on 8/25/16.
 */
public class ReaderLayerMenuRepository {

    public static ReaderLayerMenuItem[] fixedPageMenuItems = new ReaderLayerMenuItem[]{
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Group, ReaderMenuAction.FONT, null, R.string.font_face, "", R.drawable.ic_dialog_reader_menu_font),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Group, ReaderMenuAction.ZOOM, null, R.string.reader_layer_menu_zoom, "", R.drawable.ic_dialog_reader_menu_scale),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.ZOOM_IN, null, R.string.reader_layer_menu_zoom_in, "", R.drawable.ic_dialog_reader_menu_scale_magnify),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.ZOOM_OUT, null, R.string.reader_layer_menu_zoom_out, "", R.drawable.ic_dialog_reader_menu_scale_shrink),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.ZOOM_BY_RECT, null, R.string.reader_layer_menu_zoom_by_rect, "", R.drawable.ic_dialog_reader_menu_scale_choose),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.ZOOM_TO_PAGE, null, R.string.reader_layer_menu_zoom_to_page, "", R.drawable.ic_dialog_reader_menu_scale_fit_page),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.ZOOM_BY_CROP_PAGE, null, R.string.reader_layer_menu_zoom_by_crop_page, "", R.drawable.ic_dialog_reader_menu_scale_cut_four),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.ZOOM_BY_CROP_WIDTH, null, R.string.reader_layer_menu_zoom_by_crop_width, "", R.drawable.ic_dialog_reader_menu_scale_cut_two),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Group, ReaderMenuAction.NAVIGATION, null, R.string.reader_layer_menu_navigation, "", R.drawable.ic_dialog_reader_menu_browse),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.IMAGE_REFLOW, null, R.string.reader_layer_menu_font_reflow, "", R.drawable.ic_dialog_reader_menu_scale_reset),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.MANUAL_CROP, null, R.string.manual_crop, "", R.drawable.ic_dialog_reader_menu_cut),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.NAVIGATION_COMIC_MODE, null, R.string.reader_layer_menu_navigation_comic_mode, "", R.drawable.ic_dialog_reader_menu_browse_cartoon),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.NAVIGATION_ARTICLE_MODE, null, R.string.reader_layer_menu_navigation_article_mode, "", R.drawable.ic_dialog_reader_menu_browse_thesis),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.NAVIGATION_RESET, null, R.string.reader_layer_menu_navigation_reset, "", R.drawable.ic_dialog_reader_menu_browse_reset),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.NAVIGATION_MORE_SETTINGS, null, R.string.reader_layer_menu_navigation_more_settings, "", R.drawable.ic_dialog_reader_menu_browse_more),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Group, ReaderMenuAction.GAMMA_CORRECTION, null, R.string.gamma_correction, "", R.drawable.ic_dialog_reader_menu_browse_gamma),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Group, ReaderMenuAction.NOTE, null, R.string.reader_layer_menu_notes, "", R.drawable.ic_dialog_reader_menu_note),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.DIRECTORY_SCRIBBLE, null, R.string.reader_layer_menu_directory_scribble, "", R.drawable.ic_dialog_reader_menu_browse_write),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.SHOW_NOTE, null, R.string.reader_layer_menu_show_scribble, "", R.drawable.ic_dialog_reader_menu_note_show),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.NOTE_EXPORT, null, R.string.reader_layer_menu_directory_export, "", R.drawable.ic_dialog_reader_menu_browse_export),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.NOTE_IMPORT, null, R.string.reader_layer_menu_directory_import, "", R.drawable.ic_dialog_reader_menu_browse_import),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.NOTE_SIDE_NOTE, null, R.string.reader_layer_menu_note_side_note, "", R.drawable.ic_dialog_reader_menu_browse_write),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Group, ReaderMenuAction.ROTATION, null, R.string.reader_layer_menu_rotation, "", R.drawable.ic_dialog_reader_menu_revolve),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.ROTATION_ROTATE_90, null, 0, "", R.drawable.ic_dialog_reader_menu_revolve_left),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.ROTATION_ROTATE_0, null, 0, "", R.drawable.ic_dialog_reader_menu_revolve_up),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.ROTATION_ROTATE_270, null, 0, "", R.drawable.ic_dialog_reader_menu_revolve_right),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.ROTATION_ROTATE_180, null, 0, "", R.drawable.ic_dialog_reader_menu_revolve_down),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Group, ReaderMenuAction.MORE, null, R.string.reader_layer_menu_more, "", R.drawable.ic_dialog_reader_menu_more),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.TTS, null, R.string.reader_layer_menu_tts, "", R.drawable.ic_dialog_reader_menu_tts),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.FRONT_LIGHT, null, R.string.reader_layer_menu_front_light, "", R.drawable.ic_dialog_reader_menu_frontlight),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.NATURAL_LIGHT, null, R.string.reader_layer_menu_front_light, "", R.drawable.ic_dialog_reader_menu_frontlight),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.REFRESH, null, R.string.reader_layer_menu_refresh, "", R.drawable.ic_dialog_reader_menu_refresh_black),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.SLIDESHOW, null, R.string.reader_layer_menu_slideshow, "", R.drawable.ic_dialog_reader_menu_slideshow),
            new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, ReaderMenuAction.SETTINGS, null, R.string.reader_layer_menu_settings, "", R.drawable.ic_dialog_reader_menu_setting),
    };

    public static ReaderLayerMenuItem[] colorMenuItems = new ReaderLayerMenuItem[]{
            (ReaderLayerMenuItem) ReaderLayerMenuItem.createSimpleMenuItem(ReaderMenuAction.DIRECTORY_TOC, R.drawable.ic_dialog_reader_menu_topic),
            (ReaderLayerMenuItem) ReaderLayerMenuItem.createSimpleMenuItem(ReaderMenuAction.DIRECTORY_SCRIBBLE, R.drawable.ic_dialog_reader_menu_browse_write)
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
                        item.getAction(), currentGroup, item.getTitleResourceId(), item.getTitle(), item.getDrawableResourceId(), item.getItemId()));
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
            if (excludingSet.contains(item.getAction())) {
                continue;
            }
            if (item.getItemType() == ReaderMenuItem.ItemType.Group) {
                ReaderLayerMenuItem group = new ReaderLayerMenuItem(item);
                menuGroupList.add(group);
                currentGroup = group;
            } else if (isChildItem(currentGroup.getAction(), item.getAction())){
                (currentGroup.getChildren()).add(new ReaderLayerMenuItem(item.getItemType(),
                        item.getAction(), currentGroup, item.getTitleResourceId(), item.getTitle(), item.getDrawableResourceId(), item.getItemId()));
            }
        }
        return menuGroupList;
    }

    private static boolean isChildItem(ReaderMenuAction groupAction, ReaderMenuAction childAction) {
        boolean findGroup = false;
        for (ReaderLayerMenuItem fixedPageMenuItem : fixedPageMenuItems) {
            if (findGroup && fixedPageMenuItem.getItemType() == ReaderMenuItem.ItemType.Group) {
                break;
            }
            if (fixedPageMenuItem.getAction() == groupAction) {
                findGroup = true;
            }
            if (findGroup && fixedPageMenuItem.getAction() == childAction) {
                return true;
            }
        }
        return false;
    }
}
