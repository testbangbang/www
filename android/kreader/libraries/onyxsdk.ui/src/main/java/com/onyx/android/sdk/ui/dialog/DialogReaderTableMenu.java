/**
 *
 */
package com.onyx.android.sdk.ui.dialog;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
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
 * @author Joy
 */
public final class DialogReaderTableMenu extends Dialog {

    private static final String TAG_MENU_ID = "menu_id";
    private static final String TAG_MENU_CLOSE_AFTER_CLICK = "menu_close_after_click";
    private static final String TAG_LAYOUT_GRAVITY = "layout_gravity";
    private static final String TAG_FONT_FACE_NAME = "font_face_name";
    private static final String TAG_SEEKBAR_PROGRESS_VALUE = "seekbar_progress_value";
    private LinearLayout statusPageButton;
    private TextView currentPage,totalPage;

    private enum MenuId {
        Font_Group, Font_Style, Layout_Group, Layout_Style, Font_Increase, Font_Decrease, Font_Contrast, Font_Embolden, Font_Smart_Reflow, Font_Family_Set,
        Zoom_Group, Zoom_In, Zoom_Out, Zoom_To_Width, Zoom_To_Page, Zoom_Crop, Zoom_By_Selection,
        Navigation_Group,Navigation_Comic_Preset,Navigation_Article_Preset,Navigation_Reset, Navigation_MoreSetting,
        Spacing_Group, Spacing_Increase, Spacing_Decrease, Spacing_Normal, Spacing_Small, Spacing_Big, Paragraph_Indent,
        Directory_Group, Directory_TOC, Directory_Bookmarks, Directory_Annotations, Directory_Scribbles,  Directory_Export,
        Rotation_Group, Rotation_0, Rotation_90, Rotation_180, Rotation_270,
        TTS_Group, TTS_Stop, TTS_Play, TTS_Volume_Down, TTS_Volume_Up, TTS_Volume_Seek,
        More_Group, Dictionary, Search, Screen_Refresh_Set, Reading_Mode, Config_Margins, Front_Light_Set, Settings,
    }

    private void notifyOnMenuItemClickedListener(GObject menu) {
        this.invokeMenuCallback(menu);
        Log.d("TAG", getMenuId(menu) + "");
        boolean close = getMenuCloseAfterClick(menu);
        if (close) {
            dismiss();
        }
    }

    private void notifyPreMenuItemClickedListener(GObject menu) {
        invokePreMenuCallback(menu);
    }

    private ReaderMenuLayout mMenuLayout = null;
    private AbstractReaderMenuCallback mMenuCallback = null;
    LinkedHashMap<GObject, GAdapter> menus;
    static GObject scribbleMenu = null;

    private DialogReaderTableMenu(Context context,
                                 final AbstractReaderMenuCallback menuCallback) {
        super(context, R.style.CustomDialog);

        this.setContentView(R.layout.onyx_dialog_reader_table_menu);
        this.fitDialogToWindow();
        this.setCanceledOnTouchOutside(true);

        mMenuCallback = menuCallback;
        statusPageButton = (LinearLayout)findViewById(R.id.layout_page_info);
        currentPage = (TextView)findViewById(R.id.textview_current_page);
        totalPage = (TextView)findViewById(R.id.textview_total_page);
        mMenuLayout = (ReaderMenuLayout) this.findViewById(R.id.layout_reader_menu);
        MenuId currentMenuGroup = mMenuLayout.getCurrentMenuGroup() != null ?
                getMenuId(mMenuLayout.getCurrentMenuGroup()) :
                MenuId.Layout_Group;
        menus = createDefaultMainMenus(this.getContext(), mMenuCallback);
        GObject defaultMenu = menus.keySet().iterator().next();
        for (GObject o : menus.keySet()) {
            if (o.matches(TAG_MENU_ID, currentMenuGroup)) {
                defaultMenu = o;
                break;
            }
        }
        mMenuLayout.setupMenu(menus, defaultMenu,
                new ReaderMenuLayout.ReaderMenuCallback() {
                    @Override
                    public void onMenuItemClicked(GObject item) {
                        notifyOnMenuItemClickedListener(item);
                    }

                    @Override
                    public void preMenuItemClicked(GObject item) {
                        notifyPreMenuItemClickedListener(item);
                    }
                }
                ,true);
        initReaderBar();
        statusPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenuCallback.showGoToPageDialog();
            }
        });
    }


    private void initReaderBar(){
        View dismissZone = findViewById(R.id.dismiss_zone);
        RelativeLayout readerFunctionBar = (RelativeLayout) findViewById(R.id.reader_function_bar);
        if (!mMenuCallback.isShowActionBar()){
            readerFunctionBar.setVisibility(View.GONE);
            findViewById(R.id.reader_function_bar_divider).setVisibility(View.GONE);
        }
        if (dismissZone !=null){
            dismissZone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
        RelativeLayout quitButtonLayout = (RelativeLayout) findViewById(R.id.quit_button_layout);
        if (quitButtonLayout != null) {
            quitButtonLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMenuCallback != null) {
                        mMenuCallback.quitApplication();
                    }
                }
            });
        }
        ImageButton frontLightButton = (ImageButton) findViewById(R.id.button_front_light);
        if (frontLightButton != null) {
            frontLightButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DialogBrightness(DialogReaderTableMenu.this.getContext()).show();
                    DialogReaderTableMenu.this.dismiss();
                }
            });
            if (!DeviceInfo.currentDevice.hasFrontLight(getContext())){
                frontLightButton.setVisibility(View.GONE);
                findViewById(R.id.front_light_divider).setVisibility(View.GONE);
            }
        }
        ImageButton screenRefreshButton = (ImageButton) findViewById(R.id.button_screen_refresh);
        if (screenRefreshButton != null) {
            screenRefreshButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMenuCallback != null) {
                        if (mMenuCallback.isCustomSetScreenRefresh()) {
                            mMenuCallback.customSetScreenRefreshInterval();
                        } else {
                            DialogScreenRefresh dlg = new DialogScreenRefresh(DialogReaderTableMenu.this.getContext());
                            dlg.setOnScreenRefreshListener(new DialogScreenRefresh.onScreenRefreshListener() {
                                @Override
                                public void screenRefresh(int pageTurning) {
                                    mMenuCallback.setScreenRefreshInterval(pageTurning);
                                }
                            });
                            dlg.show();
                        }
                    }
                    DialogReaderTableMenu.this.dismiss();
                }
            });
        }
        ImageButton dictButton = (ImageButton) findViewById(R.id.button_dict);
        if (dictButton != null) {
            dictButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mMenuCallback.startDictionaryApp()) {
                        startDictionaryApp();
                    }
                    DialogReaderTableMenu.this.dismiss();
                }
            });
            if ((!mMenuCallback.supportDictionaryFunc())) {
                dictButton.setVisibility(View.GONE);
                findViewById(R.id.dict_divider).setVisibility(View.GONE);
            }
        }
        ImageButton searchButton = (ImageButton) findViewById(R.id.button_search);
        if (searchButton != null) {
            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMenuCallback != null) {
                        mMenuCallback.startSearch();
                    }
                    DialogReaderTableMenu.this.dismiss();
                }
            });
        }
        ImageButton settingButton = (ImageButton) findViewById(R.id.button_setting);
        if (settingButton != null) {
            settingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMenuCallback != null) {
                        mMenuCallback.showReaderSettings();
                    }
                    DialogReaderTableMenu.this.dismiss();
                }
            });
        }
    }
    public static DialogReaderTableMenu create(Context context, AbstractReaderMenuCallback menuCallback) {
        return new DialogReaderTableMenu(context, menuCallback);
    }

    @Override
    public void show() {
        this.fitDialogToWindow();

        ((TextView) this.findViewById(R.id.textview_book_name)).setText(mMenuCallback.getBookName());
        currentPage.setText(String.valueOf(mMenuCallback.getCurrentPage()));
        totalPage.setText(String.valueOf(mMenuCallback.getTotalPage()));

        updateFontEmboldenMenu();
        updateTtsPlayMenu();
        updateScribbleMenu();

        super.show();
    }

    public void updatePageStatus(int currentPageNum,int totalPageNum){
        currentPage.setText(String.valueOf(mMenuCallback.getCurrentPage()));
        totalPage.setText(String.valueOf(mMenuCallback.getTotalPage()));
    }

    private void fitDialogToWindow() {
        Window mWindow = getWindow();
        WindowManager.LayoutParams mParams = mWindow.getAttributes();
        mParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mParams.gravity = Gravity.BOTTOM;
        mWindow.setAttributes(mParams);
        //force use all space in the screen.
        mWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    private void updateFontEmboldenMenu() {
        GObject font_embolden = mMenuLayout.searchFirstMenuByTag(TAG_MENU_ID, MenuId.Font_Embolden);
        if (font_embolden != null) {
            this.updateImage(font_embolden, mMenuCallback.isFontEmbolden() ? R.drawable.font_embolden :
                    R.drawable.font_standard);
        }
    }

    private void updateScribbleMenu() {
        GAdapter subAdapter = null;
        for (GObject object : menus.keySet()) {
            if (object.matches(TAG_MENU_ID, MenuId.Directory_Group)) {
                subAdapter = menus.get(object);
            }
        }
        if (subAdapter != null) {
            if (mMenuCallback.supportScribble()) {
               if (!subAdapter.getList().contains(scribbleMenu)){
                   if(mMenuCallback.supportDataExport()){
                       subAdapter.addObject(subAdapter.getList().size() - 1, scribbleMenu);
                   }else {
                       subAdapter.addObject(scribbleMenu);
                   }
               }
            } else {
                if (subAdapter.getList().contains(scribbleMenu)){
                    subAdapter.getList().remove(scribbleMenu);
                }
            }
        }
    }

    public void updateTtsPlayMenu() {
        GObject tts_play = mMenuLayout.searchFirstMenuByTag(TAG_MENU_ID, MenuId.TTS_Play);
        if (tts_play != null) {
            this.updateImage(tts_play, mMenuCallback.isTtsPlaying() ? R.drawable.tts_pause :
                    R.drawable.tts_start);
        }
        updateTtsVolumeSeekBar();
    }

    private void updateTtsVolumeSeekBar() {
        AudioManager am = (AudioManager)getContext().getSystemService(Context.AUDIO_SERVICE);
        if (am != null) {
            int max_volume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int value = am.getStreamVolume(AudioManager.STREAM_MUSIC) * 100 / max_volume;
            GObject tts_seek_bar = mMenuLayout.searchFirstMenuByTag(TAG_MENU_ID, MenuId.TTS_Volume_Seek);
            if (tts_seek_bar != null) {
                updateSeekBarValue(tts_seek_bar, value);
            }
        }
    }

    private void adjustTtsVolume(Context context, boolean increase) {
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        if (am != null) {
            if (increase) {
                am.adjustSuggestedStreamVolume(AudioManager.ADJUST_RAISE, AudioManager.STREAM_MUSIC,
                        AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            } else {
                am.adjustSuggestedStreamVolume(AudioManager.ADJUST_LOWER, AudioManager.STREAM_MUSIC,
                        AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            }

            updateTtsVolumeSeekBar();
        }
    }

    private void updateImage(GObject menu, int imageResource) {
        menu.putInt(GAdapterUtil.TAG_IMAGE_RESOURCE, imageResource);
    }

    private void updateSeekBarValue(GObject menu, int value) {
        menu.putInt(TAG_SEEKBAR_PROGRESS_VALUE, value);
    }

    private void showLayoutStyleDialog() {
        mMenuCallback.saveCurrentFontStyleOption();
        DialogFontStyleSettings dlgFontStyleSetting = new DialogFontStyleSettings();
        dlgFontStyleSetting.setMenuCallback(mMenuCallback);
        dlgFontStyleSetting.show(mMenuCallback.getFragmentManager());
    }

    private void invokePreMenuCallback(GObject menu){
        MenuId menu_id = getMenuId(menu);
        switch (menu_id) {
            case Directory_Group:
                updateScribbleMenu();
                break;
            default:
                break;
        }
    }
    private void invokeMenuCallback(GObject menu) {
        MenuId menu_id = getMenuId(menu);
        switch (menu_id) {
            case Layout_Group:
                break;
            case Layout_Style:
                showLayoutStyleDialog();
                break;
            case Font_Group:
                break;
            case Directory_Group:
                break;
            case More_Group:
                break;
            case Rotation_Group:
                break;
            case Zoom_Group:
                break;
            case TTS_Group:
                mMenuCallback.ttsInit();
                break;
            case Font_Increase:
                mMenuCallback.increaseFontSize();
                break;
            case Font_Decrease:
                mMenuCallback.decreaseFontSize();
                break;
            case Font_Contrast:
                mMenuCallback.setContrast();
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
            case Paragraph_Indent:
                mMenuCallback.toggleParagraphIndent();
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
            case Directory_Export:
                mMenuCallback.exportUserData();
                break;
            case Rotation_0:
                break;
            case Rotation_90:  // fall through
            case Rotation_180: // fall through
            case Rotation_270: // fall through
                int orientation = this.rotateScreen(menu_id, mMenuCallback.getScreenOrientation());
                mMenuCallback.setScreenOrientation(orientation);
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
                this.adjustTtsVolume(getContext(), false);
                break;
            case TTS_Volume_Up:
                this.adjustTtsVolume(getContext(), true);
                break;
            case TTS_Volume_Seek:
                break;
            case Navigation_MoreSetting:
                mMenuCallback.showNavigationModeSettings();
                break;
            case Navigation_Comic_Preset:
                mMenuCallback.setNavigationComicPreset();
                break;
            case Navigation_Article_Preset:
                mMenuCallback.setNavigationArticlePreset();
                break;
            case Navigation_Reset:
                mMenuCallback.resetNavigationSettings();
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
                if (mMenuCallback != null) {
                    if (mMenuCallback.isCustomSetScreenRefresh()) {
                        mMenuCallback.customSetScreenRefreshInterval();
                    }else {
                        DialogScreenRefresh dlg = new DialogScreenRefresh(this.getContext());
                        dlg.setOnScreenRefreshListener(new DialogScreenRefresh.onScreenRefreshListener() {
                            @Override
                            public void screenRefresh(int pageTurning) {
                                mMenuCallback.setScreenRefreshInterval(pageTurning);
                            }
                        });
                        dlg.show();
                    }
                }
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

    private static void addLayoutStyleMenu(Context context, final LinkedHashMap<GObject, GAdapter> menu, AbstractReaderMenuCallback callback) {
        GObject layoutTab;
        layoutTab = createButtonMenu(R.string.menu_item_font_style, R.drawable.ic_style_setting, MenuId.Layout_Group, false);
        GAdapter subMenus = new GAdapter();
        saveLayoutGravity(subMenus, Gravity.LEFT);
        subMenus.addObject(createButtonMenu(R.string.menu_item_font, R.drawable.font, MenuId.Layout_Style, true));
        if (callback.supportContrast()) {
            subMenus.addObject(createButtonMenu(R.string.menu_item_font_contrast, R.drawable.gamma, MenuId.Font_Contrast, true));
        }
        subMenus.addObject(createButtonMenu(R.string.menu_item_font_bold, R.drawable.font_embolden, MenuId.Font_Embolden, true));
        if (menu != null) {
            menu.put(layoutTab, subMenus);
        }
    }

    private static void addFontMenu(Context context, final LinkedHashMap<GObject, GAdapter> menu, AbstractReaderMenuCallback callback) {
        GObject fontTab;
        fontTab = createButtonMenu(R.string.menu_item_font, R.drawable.font, MenuId.Font_Group, false);
        GAdapter subMenus = new GAdapter();
        saveLayoutGravity(subMenus, Gravity.LEFT);
        if (callback != null && callback.supportSetFontSize()) {
            subMenus.addObject(createButtonMenu(R.string.menu_item_font_increase, R.drawable.font_increase, MenuId.Font_Increase, false));
            subMenus.addObject(createButtonMenu(R.string.menu_item_font_decrease, R.drawable.font_decrease, MenuId.Font_Decrease, false));
        }
        if (callback != null && callback.supportContrast()) {
            subMenus.addObject(createButtonMenu(R.string.menu_item_font_contrast, R.drawable.gamma, MenuId.Font_Contrast, true));
        }
        subMenus.addObject(createButtonMenu(R.string.menu_item_font_bold, R.drawable.font_embolden, MenuId.Font_Embolden, true));
        if (callback != null && callback.supportSmartReflow()) {
            subMenus.addObject(createSmallTextButtonMenu(R.string.menu_item_smart_reflow, R.drawable.font_type, MenuId.Font_Smart_Reflow, true));
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
        menu.put(zoomTab, subMenus);
    }

    private static void addNavigationMenu(final LinkedHashMap<GObject, GAdapter> menu, AbstractReaderMenuCallback callback) {
        GObject zoomTab = createButtonMenu(R.string.menu_item_navigation, R.drawable.navigation, MenuId.Navigation_Group, false);
        GAdapter subMenus = new GAdapter();
        saveLayoutGravity(subMenus, Gravity.CENTER);
        subMenus.addObject(createButtonMenu(R.string.menu_item_navigation_comic, R.drawable.navigation_comic_preset, MenuId.Navigation_Comic_Preset, true));
        subMenus.addObject(createButtonMenu(R.string.menu_item_navigation_article, R.drawable.navigation_article_preset, MenuId.Navigation_Article_Preset, true));
        subMenus.addObject(createButtonMenu(R.string.menu_item_navigation_reset, R.drawable.navigation_reset, MenuId.Navigation_Reset, true));
        subMenus.addObject(createButtonMenu(R.string.menu_item_navigation_more_setting, R.drawable.navigation_more_setting, MenuId.Navigation_MoreSetting, true));
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
        subMenus.addObject(createButtonMenu(R.string.menu_item_indent, R.drawable.indent, MenuId.Paragraph_Indent, false));
        menu.put(spacingTab, subMenus);
    }

    private static void addNoteMenu(final LinkedHashMap<GObject, GAdapter> menu, AbstractReaderMenuCallback callback) {
        GObject directoryTab = createButtonMenu(R.string.directory_title, R.drawable.reader_directory, MenuId.Directory_Group, false);
        GAdapter subMenus = new GAdapter();
        saveLayoutGravity(subMenus, Gravity.LEFT);
        subMenus.addObject(createSmallTextButtonMenu(R.string.menu_item_toc, R.drawable.toc, MenuId.Directory_TOC, true));
        subMenus.addObject(createButtonMenu(R.string.menu_item_bookmark, R.drawable.menu_bookmark, MenuId.Directory_Bookmarks, true));
        if (callback != null && callback.supportAnnotation()) {
            subMenus.addObject(createButtonMenu(R.string.menu_item_annotation, R.drawable.note, MenuId.Directory_Annotations, true));
        }
        if (callback.supportScribble()) {
            if (scribbleMenu == null) {
                scribbleMenu = createButtonMenu(R.string.menu_item_scribble, R.drawable.sketch_color_black, MenuId.Directory_Scribbles, true);
            }
            subMenus.addObject(scribbleMenu);
        }
        if (callback.supportDataExport()) {
            subMenus.addObject(createButtonMenu(R.string.menu_item_export, R.drawable.export, MenuId.Directory_Export, true));
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

        if (menuCallback.supportFontFunc()) {
            if (menuCallback.supportSpacing() && menuCallback.supportSetFontFace()) {
                addLayoutStyleMenu(context, parent, menuCallback);
            } else {
                addFontMenu(context, parent, menuCallback);
            }
        }

        if (menuCallback.supportZooming()) {
            addZoomingMenu(parent, menuCallback);
        }
        if (menuCallback.supportNavigation()) {
            addNavigationMenu(parent, menuCallback);
        }
        if (menuCallback.supportNoteFunc()) {
            addNoteMenu(parent, menuCallback);
        }
        if (menuCallback.supportRotation()) {
            addRotationMenu(parent, menuCallback);
        }
        if (menuCallback.supportTts() && DeviceInfo.currentDevice.hasAudio(getContext())) {
            addTTSMenu(parent, menuCallback);
        }
        return parent;
    }

    private static GObject createButtonMenu(int titleResource, int imageResource, MenuId action, boolean closeAfterClick) {
        final int layout_id = R.layout.onyx_reader_menu_button;
        GObject item = GAdapterUtil.createTableItem(titleResource, -1, imageResource, layout_id, null);
        saveMenuId(item, action);
        saveMenuCloseAfterClick(item, closeAfterClick);
        return item;
    }

    private static GObject createSmallTextButtonMenu(int titleResource, int imageResource, MenuId action, boolean closeAfterClick) {
        final int layout_id = R.layout.onyx_reader_menu_small_text_button;
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
        Map<String, Integer> valueMapping = new HashMap<String, Integer>();
        valueMapping.put(TAG_SEEKBAR_PROGRESS_VALUE, R.id.seekbar_tts);

        Map<Integer, Map<Class<?>, Object>> callbackMapping = new HashMap<Integer, Map<Class<?>, Object>>();
        Map<Class<?>, Object> seekbar_callbacks = new HashMap<Class<?>, Object>();
        seekbar_callbacks.put(SeekBar.OnSeekBarChangeListener.class, new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                AudioManager am = (AudioManager)seekBar.getContext().getSystemService(Context.AUDIO_SERVICE);
                if (am != null) {
                    int max_volume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                    int volume = (progress * max_volume) / 100;
                    if (0 <= volume && volume <= max_volume) {
                        am.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        callbackMapping.put(R.id.seekbar_tts, seekbar_callbacks);
        GObject item = GAdapterUtil.createTableItem("", "", -1, R.layout.onyx_reader_menu_tts_seekbar, valueMapping, callbackMapping);
        saveMenuId(item, MenuId.TTS_Volume_Seek);
        saveMenuCloseAfterClick(item, false);
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
}
