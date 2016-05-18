package com.onyx.android.sdk.ui.dialog;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.data.GAdapter;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.data.sys.OnyxDictionaryInfo;
import com.onyx.android.sdk.data.sys.OnyxSysCenter;
import com.onyx.android.sdk.data.util.GAdapterUtil;
import com.onyx.android.sdk.device.DeviceInfo;
import com.onyx.android.sdk.ui.ReaderMenuLayout;
import com.onyx.android.sdk.ui.dialog.data.AbstractReaderMenuCallback;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by joy on 6/13/14.
 */
public class DialogReaderTableMenuEx  extends Dialog {

    private static final String TAG_MENU_ID = "menu_id";
    private static final String TAG_MENU_CLOSE_AFTER_CLICK = "menu_close_after_click";
    private static final String TAG_LAYOUT_GRAVITY = "layout_gravity";
    private static final String TAG_FONT_FACE_NAME = "font_face_name";

    private static enum MenuId {
        Font_Group, Font_Increase, Font_Decrease, Font_Embolden, Font_Smart_Reflow, Font_Family_Set,
        Zoom_Group, Zoom_In, Zoom_Out, Zoom_To_Width, Zoom_To_Page, Zoom_Crop, Zoom_By_Selection,
        Spacing_Group, Spacing_Increase, Spacing_Decrease, Spacing_Normal, Spacing_Small, Spacing_Big,
        Directory_Group, Directory_TOC, Directory_Bookmarks, Directory_Annotations, Directory_Scribbles, Directory_Scribbles_Erase,
        Rotation_Group, Rotation_0, Rotation_90, Rotation_180, Rotation_270,
        Navigation_Group, Navigation_Single_Page_Mode, Navigation_Single_Column, Navigation_Single_Coulmn_Auto_Crop_Page,
        Navigation_Single_Coulmn_Auto_Crop_Width, Navigation_Rows_Left_To_Right_Mode, Navigation_Rows_Right_To_Left_Mode,
        Navigation_Columns_Left_To_Right_Mode, Navigation_Columns_Right_To_Left_Mode,
        TTS_Group, TTS_Stop, TTS_Play, TTS_Volume_Down, TTS_Volume_Up, TTS_Volume_Seek,
        Page_Seek, Option_Group,
        More_Group, Dictionary, Search, Screen_Refresh_Set, Reading_Mode, Config_Margins, Front_Light_Set, Settings,
    }

    private void notifyOnMenuItemClickedListener(GObject menu) {
        this.invokeMenuCallback(menu);
        boolean close = getMenuCloseAfterClick(menu);
        if (close) {
            this.hide();
        }
    }

    private ReaderMenuLayout mMenuLayout = null;
    private AbstractReaderMenuCallback mMenuCallback = null;

    private DialogReaderTableMenuEx(Context context,
                                  final AbstractReaderMenuCallback menuCallback) {
        super(context, R.style.dialog_menu);

        this.setContentView(R.layout.onyx_dialog_reader_table_menu_ex);
        this.fitDialogToWindow();
        this.setCanceledOnTouchOutside(true);

        mMenuCallback = menuCallback;
    }

    public static DialogReaderTableMenuEx create(Context context, AbstractReaderMenuCallback menuCallback) {
        return new DialogReaderTableMenuEx(context, menuCallback);
    }

    @Override
    public void show() {
        this.fitDialogToWindow();

        mMenuLayout = (ReaderMenuLayout) this.findViewById(R.id.layout_reader_menu);
        MenuId current_menu_group = mMenuLayout.getCurrentMenuGroup() != null ?
                getMenuId(mMenuLayout.getCurrentMenuGroup()) :
                MenuId.More_Group;
        LinkedHashMap<GObject, GAdapter> menus = createDefaultMainMenus(this.getContext(),
                mMenuCallback);
        GObject default_menu = null;
        for (GObject o : menus.keySet()) {
            default_menu = o;
            if (o.matches(TAG_MENU_ID, current_menu_group)) {
                break;
            }
        }
        mMenuLayout.setupMenu(menus, default_menu,
                new ReaderMenuLayout.ReaderMenuCallback() {
                    @Override
                    public void onMenuItemClicked(GObject item) {
                        notifyOnMenuItemClickedListener(item);
                    }

                    @Override
                    public void preMenuItemClicked(GObject item) {

                    }
                }
        ,true);

        ((TextView)this.findViewById(R.id.textview_book_name)).setText(mMenuCallback.getBookName());
        ((TextView)this.findViewById(R.id.textview_current_page)).setText(String.valueOf(mMenuCallback.getCurrentPage()));
        ((TextView)this.findViewById(R.id.textview_total_page)).setText(String.valueOf(mMenuCallback.getTotalPage()));

        updateFontEmboldenMenu();
        updateTtsPlayMenu();

        mMenuLayout.hideSubMenu();
        mMenuLayout.setFocusable(true);
        mMenuLayout.setFocusableInTouchMode(true);
        mMenuLayout.mMainMenuView.requestFocus();
        super.show();
    }

    private void fitDialogToWindow() {
        Window mWindow = getWindow();
        WindowManager.LayoutParams mParams = mWindow.getAttributes();
        mParams.width = mWindow.getWindowManager().getDefaultDisplay().getWidth();
        mParams.y = mWindow.getWindowManager().getDefaultDisplay().getHeight();
        mWindow.setAttributes(mParams);
    }

    private void updateFontEmboldenMenu() {
        GObject font_embolden = mMenuLayout.searchFirstMenuByTag(TAG_MENU_ID, MenuId.Font_Embolden);
        if (font_embolden != null) {
            this.updateImage(font_embolden, mMenuCallback.isFontEmbolden() ? R.drawable.font_embolden :
                    R.drawable.font_standard);
        }
    }

    private void updateTtsPlayMenu() {
        GObject tts_play = mMenuLayout.searchFirstMenuByTag(TAG_MENU_ID, MenuId.TTS_Play);
        if (tts_play != null) {
            this.updateImage(tts_play, mMenuCallback.isTtsPlaying() ? R.drawable.tts_pause :
                    R.drawable.tts_start);
        }
    }

    private void updateImage(GObject menu, int imageResource) {
        menu.putInt(GAdapterUtil.TAG_IMAGE_RESOURCE, imageResource);
    }

    private void invokeMenuCallback(GObject menu) {
        MenuId menu_id = getMenuId(menu);

        switch (menu_id) {
            case Page_Seek:
                mMenuCallback.startSeekPage();
                break;
            case Font_Increase:
                mMenuCallback.increaseFontSize();
                break;
            case Font_Decrease:
                mMenuCallback.decreaseFontSize();
                break;
            case Font_Embolden:
                mMenuCallback.setFontEmbolden(!mMenuCallback.isFontEmbolden());
                this.updateFontEmboldenMenu();
                break;
            case Font_Smart_Reflow:
                mMenuCallback.setSmartReflow(!mMenuCallback.isSmartReflow());
                break;
            case Font_Family_Set:
                mMenuCallback.selectFontFace();
                break;
            case Zoom_In:
                mMenuCallback.zoomIn();
                break;
            case Zoom_Out:
                mMenuCallback.zoomOut();
                break;
            case Zoom_To_Width:
                mMenuCallback.zoomToWidth();
                break;
            case Zoom_To_Page:
                mMenuCallback.zoomToPage();
                break;
            case Zoom_Crop:
                mMenuCallback.zoomByCrop();
                break;
            case Zoom_By_Selection:
                mMenuCallback.zoomBySelection();
                break;
            case Spacing_Increase:
                mMenuCallback.increaseLineSpacing();
                break;
            case Spacing_Decrease:
                mMenuCallback.decreaseLineSpacing();
                break;
            case Spacing_Normal:
                mMenuCallback.setLineSpacing(AbstractReaderMenuCallback.LineSpacingScale.Normal);
                break;
            case Spacing_Small:
                mMenuCallback.setLineSpacing(AbstractReaderMenuCallback.LineSpacingScale.Small);
                break;
            case Spacing_Big:
                mMenuCallback.setLineSpacing(AbstractReaderMenuCallback.LineSpacingScale.Large);
                break;
            case Directory_TOC:
                mMenuCallback.showToc();
                break;
            case Directory_Bookmarks:
                mMenuCallback.showBookmarks();
                break;
            case Directory_Annotations:
                mMenuCallback.showAnnotations();
                break;
            case Directory_Scribbles:
                mMenuCallback.startScribble();
                break;
            case Directory_Scribbles_Erase:
                mMenuCallback.startScribbleErase();
                break;
            case Rotation_0:
                break;
            case Rotation_90:  // fall through
            case Rotation_180: // fall through
            case Rotation_270: // fall through
                int orientation = this.rotateScreen(menu_id, mMenuCallback.getScreenOrientation());
                mMenuCallback.setScreenOrientation(orientation);
                break;
            case Navigation_Single_Page_Mode:
            case Navigation_Single_Column:
            case Navigation_Single_Coulmn_Auto_Crop_Page:
            case Navigation_Single_Coulmn_Auto_Crop_Width:
            case Navigation_Rows_Left_To_Right_Mode:
            case Navigation_Rows_Right_To_Left_Mode:
            case Navigation_Columns_Left_To_Right_Mode:
            case Navigation_Columns_Right_To_Left_Mode:
                mMenuCallback.setNavigationMode(getNavigationModeFromMenuId(menu_id));
                break;
            case TTS_Group:
                mMenuCallback.ttsInit();
                break;
            case TTS_Stop:
                mMenuCallback.ttsStop();
                this.updateTtsPlayMenu();
                break;
            case TTS_Play:
                if (mMenuCallback.isTtsPlaying()) {
                    mMenuCallback.ttsPause();
                } else {
                    mMenuCallback.ttsPlay();
                }
                this.updateTtsPlayMenu();
                break;
            case TTS_Volume_Down:
                break;
            case TTS_Volume_Up:
                break;
            case TTS_Volume_Seek:
                break;
            case Dictionary:
                if (!mMenuCallback.startDictionaryApp()) {
                    startDictionaryApp();
                }
                break;
            case Search:
                mMenuCallback.startSearch();
                break;
            case Screen_Refresh_Set: {
                DialogScreenRefresh dlg = new DialogScreenRefresh(this.getContext());
                dlg.setOnScreenRefreshListener(new DialogScreenRefresh.onScreenRefreshListener() {
                    @Override
                    public void screenRefresh(int pageTurning) {
                        mMenuCallback.setScreenRefreshInterval(pageTurning);
                    }
                });
                dlg.show();
                break;
            }
            case Reading_Mode:
                break;
            case Front_Light_Set:
                new DialogBrightness(this.getContext()).show();
                break;
            case Config_Margins:
                mMenuCallback.configMargins();
                break;
            case Settings:
                mMenuCallback.showReaderSettings();
                break;
        }
    }


    private static void addFontMenu(Context context, final LinkedHashMap<GObject, GAdapter> menu, AbstractReaderMenuCallback callback) {
        GObject fontTab = createButtonMenu(R.string.menu_item_font, R.drawable.font, MenuId.Font_Group, false);
        GAdapter subMenus = new GAdapter();
        saveLayoutGravity(subMenus, Gravity.LEFT);
        subMenus.addObject(createButtonMenu(R.string.menu_item_font_increase, R.drawable.font_increase, MenuId.Font_Increase, false));
        subMenus.addObject(createButtonMenu(R.string.menu_item_font_decrease, R.drawable.font_decrease, MenuId.Font_Decrease, false));
        subMenus.addObject(createButtonMenu(R.string.menu_item_font_bold, R.drawable.font_embolden, MenuId.Font_Embolden, true));
        if (callback != null && callback.supportSmartReflow()) {
            subMenus.addObject(createButtonMenu(R.string.menu_item_smart_reflow, R.drawable.font_type, MenuId.Font_Smart_Reflow, true));
        }
        if (callback != null && callback.supportSetFontFace()) {
            String font = callback.getFontFace();
            if (font == null) {
                font = context.getString(R.string.default_font);
            }
            subMenus.addObject(createFontFaceButton(font, MenuId.Font_Family_Set, true));
        }

        if (menu != null) {
            menu.put(fontTab, subMenus);
        }
    }

    private static void addZoomingMenu(final LinkedHashMap<GObject, GAdapter> menu, AbstractReaderMenuCallback callback) {
        GObject zoomTab = createButtonMenu(R.string.menu_item_zoom, R.drawable.zoom, MenuId.Zoom_Group, false);
        GAdapter subMenus = new GAdapter();
        saveLayoutGravity(subMenus, Gravity.CENTER);
        subMenus.addObject(createButtonMenu(R.string.menu_item_zoom_in, R.drawable.zoom_enlarge, MenuId.Zoom_In, false));
        subMenus.addObject(createButtonMenu(R.string.menu_item_zoom_out, R.drawable.zoom_narrow, MenuId.Zoom_Out, false));
        subMenus.addObject(createButtonMenu(R.string.menu_item_to_width, R.drawable.zoom_to_width, MenuId.Zoom_To_Width, false));
        subMenus.addObject(createButtonMenu(R.string.menu_item_to_page, R.drawable.zoom_to_page, MenuId.Zoom_To_Page, false));
        if (callback != null && callback.supportSelectionZoom()) {
            subMenus.addObject(createButtonMenu(R.string.menu_item_selection, R.drawable.zoom_two_points_enlarge, MenuId.Zoom_By_Selection, true));
        }
        subMenus.addObject(createButtonMenu(R.string.menu_item_crop, R.drawable.zoom_two_points_enlarge, MenuId.Zoom_Crop, true));
        menu.put(zoomTab, subMenus);
    }

    private static void addSpacingMenu(final LinkedHashMap<GObject, GAdapter> menu, AbstractReaderMenuCallback callback) {
        GObject spacingTab = createButtonMenu(R.string.menu_item_line_spacing, R.drawable.line_spacing, MenuId.Spacing_Group, false);
        GAdapter subMenus = new GAdapter();
        saveLayoutGravity(subMenus, Gravity.CENTER);
        subMenus.addObject(createButtonMenu(R.string.menu_item_font_decrease, R.drawable.line_spacing_decreases, MenuId.Spacing_Decrease, false));
        subMenus.addObject(createButtonMenu(R.string.menu_item_font_increase, R.drawable.line_spacing_enlarge, MenuId.Spacing_Increase, false));
        subMenus.addObject(createButtonMenu(R.string.menu_item_normal, R.drawable.line_spacing_normal, MenuId.Spacing_Normal, false));
        subMenus.addObject(createButtonMenu(R.string.menu_item_small, R.drawable.line_spacing_small, MenuId.Spacing_Small, false));
        subMenus.addObject(createButtonMenu(R.string.menu_item_big, R.drawable.line_spacing_big, MenuId.Spacing_Big, false));
        menu.put(spacingTab, subMenus);
    }

    private static void addNoteMenu(final LinkedHashMap<GObject, GAdapter> menu, AbstractReaderMenuCallback callback) {
        GObject directoryTab = createButtonMenu(R.string.directory_title, R.drawable.reader_directory, MenuId.Directory_Group, false);
        GAdapter subMenus = new GAdapter();
        saveLayoutGravity(subMenus, Gravity.LEFT);
        subMenus.addObject(createButtonMenu(R.string.menu_item_toc, R.drawable.toc, MenuId.Directory_TOC, true));
        subMenus.addObject(createButtonMenu(R.string.menu_item_bookmark, R.drawable.menu_bookmark, MenuId.Directory_Bookmarks, true));
        subMenus.addObject(createButtonMenu(R.string.menu_item_annotation, R.drawable.note, MenuId.Directory_Annotations, true));
        if (callback.supportScribble()) {
            subMenus.addObject(createButtonMenu(R.string.menu_item_scribble, R.drawable.sketch_color_black, MenuId.Directory_Scribbles, true));
        }
        menu.put(directoryTab, subMenus);
    }

    private static void addRotationMenu(final LinkedHashMap<GObject, GAdapter> menu, AbstractReaderMenuCallback callback) {
        GObject rotationTab = createButtonMenu(R.string.menu_item_rotation, R.drawable.rotation, MenuId.Rotation_Group, false);
        GAdapter subMenus = new GAdapter();
        saveLayoutGravity(subMenus, Gravity.CENTER);
        subMenus.addObject(createImageButtonMenu(R.drawable.rotation_90, MenuId.Rotation_90, true));
        subMenus.addObject(createImageButtonMenu(R.drawable.rotation_0, MenuId.Rotation_0, true));
        subMenus.addObject(createImageButtonMenu(R.drawable.rotation_270, MenuId.Rotation_270, true));
        subMenus.addObject(createImageButtonMenu(R.drawable.rotation_180, MenuId.Rotation_180, true));
        menu.put(rotationTab, subMenus);
    }

    private static void addNavigationMenu(final LinkedHashMap<GObject, GAdapter> menu, AbstractReaderMenuCallback callback) {
        GObject navigationTab = createButtonMenu(R.string.menu_item_navigation, R.drawable.navigation, MenuId.Navigation_Group, false);
        GAdapter subMenus = new GAdapter();
        saveLayoutGravity(subMenus, Gravity.CENTER);
        subMenus.addObject(createImageButtonMenu(R.drawable.crop_to_page, MenuId.Navigation_Single_Coulmn_Auto_Crop_Page, true));
        subMenus.addObject(createImageButtonMenu(R.drawable.crop_to_width, MenuId.Navigation_Single_Coulmn_Auto_Crop_Width, true));
        subMenus.addObject(createImageButtonMenu(R.drawable.rows_left_to_right, MenuId.Navigation_Rows_Left_To_Right_Mode, true));
        subMenus.addObject(createImageButtonMenu(R.drawable.rows_right_to_left, MenuId.Navigation_Rows_Right_To_Left_Mode, true));
        subMenus.addObject(createImageButtonMenu(R.drawable.columns_left_to_right, MenuId.Navigation_Columns_Left_To_Right_Mode, true));
        subMenus.addObject(createImageButtonMenu(R.drawable.columns_right_to_left, MenuId.Navigation_Columns_Right_To_Left_Mode, true));
        menu.put(navigationTab, subMenus);
    }

    private static void addTTSMenu(final LinkedHashMap<GObject, GAdapter> menu , AbstractReaderMenuCallback callback) {
        GObject ttsTab = createButtonMenu(R.string.menu_item_tts, R.drawable.tts, MenuId.TTS_Group, false);
        GAdapter subMenus = new GAdapter();
        saveLayoutGravity(subMenus, Gravity.CENTER);
        subMenus.addObject(createImageButtonMenu(R.drawable.tts_stop, MenuId.TTS_Stop, false));
        subMenus.addObject(createImageButtonMenu(R.drawable.tts_start, MenuId.TTS_Play, false));
        subMenus.addObject(createImageButtonMenu(R.drawable.tts_volume_down, MenuId.TTS_Volume_Down, false));
        subMenus.addObject(createTtsVolumeSeekBar());
        subMenus.addObject(createImageButtonMenu(R.drawable.tts_volume_increase, MenuId.TTS_Volume_Up, false));
        menu.put(ttsTab, subMenus);
    }

    private static void addOptionMenu(final LinkedHashMap<GObject, GAdapter> menu, AbstractReaderMenuCallback callback) {
        GObject fontTab = createButtonMenu(R.string.menu_item_font, R.drawable.font, MenuId.Option_Group, false);
        GAdapter subMenus = new GAdapter();
        saveLayoutGravity(subMenus, Gravity.LEFT);
        subMenus.addObject(createButtonMenu(R.string.menu_item_font_increase, R.drawable.font_increase, MenuId.Font_Increase, false));
        subMenus.addObject(createButtonMenu(R.string.menu_item_font_decrease, R.drawable.font_decrease, MenuId.Font_Decrease, false));
        subMenus.addObject(createButtonMenu(R.string.menu_item_font_bold, R.drawable.font_embolden, MenuId.Font_Embolden, true));

        if (menu != null) {
            menu.put(fontTab, subMenus);
        }
    }

    private static void addMoreMenu(final LinkedHashMap<GObject, GAdapter> menu, AbstractReaderMenuCallback callback,boolean hasFrontLight) {
        GObject moreTab = createButtonMenu(R.string.menu_item_more, R.drawable.more, MenuId.More_Group, false);
        GAdapter subMenus = new GAdapter();
        saveLayoutGravity(subMenus, Gravity.CENTER);
        subMenus.addObject(createButtonMenu(R.string.menu_item_dictionary, R.drawable.menu_dictionary, MenuId.Dictionary, true));
        subMenus.addObject(createButtonMenu(R.string.menu_item_search, R.drawable.search, MenuId.Search, true));
        subMenus.addObject(createButtonMenu(R.string.menu_item_refresh, R.drawable.menu_screen_refresh, MenuId.Screen_Refresh_Set, true));
        if (callback.supportReadingMode()) {
            subMenus.addObject(createButtonMenu(R.string.menu_item_reading_mode, R.drawable.reading_mode, MenuId.Reading_Mode, true));
        }
        if (hasFrontLight) {
            subMenus.addObject(createButtonMenu(R.string.menu_item_brightness, R.drawable.light_settings, MenuId.Front_Light_Set, true));
        }
        if (callback.supportMarginsConfig())  {
            subMenus.addObject(createButtonMenu(R.string.menu_item_margin, R.drawable.config_margin, MenuId.Config_Margins, true));
        }
        subMenus.addObject(createButtonMenu(R.string.menu_item_settings, R.drawable.settings, MenuId.Settings, true));
        menu.put(moreTab, subMenus);
    }

    protected LinkedHashMap<GObject, GAdapter> createDefaultMainMenus(Context context, AbstractReaderMenuCallback menuCallback) {
        final LinkedHashMap<GObject, GAdapter> parent = new LinkedHashMap<GObject, GAdapter>();
        parent.put(createButtonMenu(R.string.menu_item_toc, R.drawable.toc, MenuId.Directory_TOC, true), null);
        if (menuCallback.supportScribble()) {
            parent.put(createButtonMenu(R.string.menu_item_scribble, R.drawable.sketch_color_black, MenuId.Directory_Scribbles, true), null);
//            parent.put(createButtonMenu(R.string.menu_item_scribble_erase, R.drawable.scribble_erase, MenuId.Directory_Scribbles_Erase, true), null);
        }
        parent.put(createButtonMenu(R.string.menu_item_goto_page, R.drawable.reading_menu_bottom_view__seek_page_selected, MenuId.Page_Seek, true), null);
//        if (menuCallback.supportTts()) {
//            addTTSMenu(parent, menuCallback);
//        }
//        addFontMenu(context, parent, menuCallback);
        addZoomingMenu(parent, menuCallback);
        addMoreMenu(parent, menuCallback, DeviceInfo.currentDevice.hasFrontLight(context));
        return parent;
    }

    private static GObject createButtonMenu(int titleResource, int imageResource, MenuId action, boolean closeAfterClick) {
        final int layout_id = R.layout.onyx_reader_menu_button;
        GObject item = GAdapterUtil.createTableItem(titleResource, -1, imageResource, layout_id, null);
        saveMenuId(item, action);
        saveMenuCloseAfterClick(item, closeAfterClick);
        return item;
    }

    private static GObject createImageButtonMenu(int imageResource, MenuId action, boolean closeAfterClick) {
        final int layout_id = R.layout.onyx_reader_menu_imagebutton;
        GObject item = GAdapterUtil.createTableItem("", "", imageResource, layout_id, null);
        saveMenuId(item, action);
        saveMenuCloseAfterClick(item, closeAfterClick);
        return item;
    }

    private static GObject createTtsVolumeSeekBar() {
        GObject item = GAdapterUtil.createTableItem("", "", -1, R.layout.onyx_reader_menu_tts_seekbar, null);
        saveMenuId(item, MenuId.TTS_Volume_Seek);
        return item;
    }

    private static GObject createFontFaceButton(String fontName, MenuId action, boolean closeAfterClick) {
        final int layout_id = R.layout.onyx_reader_menu_font_face_button;
        Map<String, Integer> mapping = new HashMap<String, Integer>();
        mapping.put(TAG_FONT_FACE_NAME, R.id.button_font_face);
        GObject item = GAdapterUtil.createTableItem("", "", -1, layout_id, mapping);
        item.putString(TAG_FONT_FACE_NAME, fontName);
        saveMenuId(item, action);
        saveMenuCloseAfterClick(item, closeAfterClick);
        return item;
    }

    private static MenuId getMenuId(GObject menu) {
        return (MenuId)menu.getObject(TAG_MENU_ID);
    }

    private static void saveMenuId(GObject menu, MenuId menuId) {
        menu.putObject(TAG_MENU_ID, menuId);
    }

    /**
     * default return false
     *
     * @param menu
     * @return
     */
    private static boolean getMenuCloseAfterClick(GObject menu) {
        return menu.getBoolean(TAG_MENU_CLOSE_AFTER_CLICK);
    }

    private static void saveMenuCloseAfterClick(GObject menu, boolean close) {
        menu.putBoolean(TAG_MENU_CLOSE_AFTER_CLICK, close);
    }

    /**
     * return Gravity.CENTER as default value
     * @param adapter
     * @return
     */
    private static int getLayoutGravity(GAdapter adapter) {
        return adapter.getOptions().getInt(TAG_LAYOUT_GRAVITY, Gravity.CENTER);
    }

    private static void saveLayoutGravity(GAdapter adapter, int gravity) {
        adapter.getOptions().putInt(TAG_LAYOUT_GRAVITY, gravity);
    }

    private int rotateScreen(MenuId rotationMenuId, int current_orientation) {
        int orientation;

        switch (rotationMenuId) {
            case Rotation_90:
                if (current_orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                } else if (current_orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                } else if (current_orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                } else if (current_orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                } else {
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                }
                break;
            case Rotation_180:
                if (current_orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                } else if (current_orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                } else if (current_orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                } else if (current_orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                } else {
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                }
                break;
            case Rotation_270:
                if (current_orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                } else if (current_orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                } else if (current_orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                } else if (current_orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                } else {
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                }
                break;
            default:
                throw new IllegalAccessError();
        }

        return orientation;
    }

    public boolean startDictionaryApp() {
        OnyxDictionaryInfo info = OnyxSysCenter.getDictionary(this.getContext());
        if (info == null) {
            Toast.makeText(this.getContext(), R.string.did_not_find_the_dictionary, Toast.LENGTH_LONG).show();
            return false;
        }
        Intent intent = new Intent(info.action).setComponent(new ComponentName(info.packageName, info.className));
        try {
            getContext().startActivity(intent);
        } catch ( ActivityNotFoundException e ) {
            Toast.makeText(this.getContext(), R.string.did_not_find_the_dictionary, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private AbstractReaderMenuCallback.NavigationMode getNavigationModeFromMenuId(MenuId menu) {
        switch (menu) {
            case Navigation_Single_Page_Mode:
                return AbstractReaderMenuCallback.NavigationMode.SINGLE_PAGE_MODE;
            case Navigation_Single_Column:
                return AbstractReaderMenuCallback.NavigationMode.SINGLE_COLUMN;
            case Navigation_Single_Coulmn_Auto_Crop_Page:
                return AbstractReaderMenuCallback.NavigationMode.AUTO_CROP_PAGE_MODE;
            case Navigation_Single_Coulmn_Auto_Crop_Width:
                return AbstractReaderMenuCallback.NavigationMode.AUTO_CROP_WIDTH_MODE;
            case Navigation_Rows_Left_To_Right_Mode:
                return AbstractReaderMenuCallback.NavigationMode.ROWS_LEFT_TO_RIGHT_MODE;
            case Navigation_Columns_Left_To_Right_Mode:
                return AbstractReaderMenuCallback.NavigationMode.COLUMNS_LEFT_TO_RIGHT_MODE;
            case Navigation_Columns_Right_To_Left_Mode:
                return AbstractReaderMenuCallback.NavigationMode.COLUMNS_RIGHT_TO_LEFT_MODE;
            case Navigation_Rows_Right_To_Left_Mode:
                return AbstractReaderMenuCallback.NavigationMode.ROWS_RIGHT_TO_LEFT_MODE;
            default:
                return AbstractReaderMenuCallback.NavigationMode.SINGLE_PAGE_MODE;
        }
    }

}
