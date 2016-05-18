package com.onyx.android.sdk.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.data.GAdapter;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.data.util.GAdapterUtil;
import com.onyx.android.sdk.ui.data.ScribbleFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Solskjaer49 on 2014/4/11.
 */
public final class ScribblePopupTableMenu extends LinearLayout {

    public static abstract class MenuCallback {
        public abstract void dismiss();
        public abstract void setMode(Mode mMode);
        public abstract void erasePage();
        public abstract void onLayoutStateChanged();
    }

    private enum ScribbleMenuID {
        Thickness_Group, Thickness_Ultra_Light, Thickness_Light, Thickness_Normal, Thickness_Bold, Thickness_Ultra_Bold,
        Color_Group, Color_White, Color_Light_Grey, Color_Grey, Color_Dark_Grey, Color_Black,
        Erase_Group, Erase_Partially, Erase_Totally,
        Move,
        Derive_Group, Derive_Current_Page, Derive_All,
        MinimizeMenu,
        Close
    }
    public enum Mode { Normal, Scribble ,Erase }

    private final MenuCallback mMenuCallback;

    private Mode mMode = Mode.Scribble;

    private Context mContext;
    private LinkedHashMap<GObject, GAdapter> scribbleMenus;
    private ReaderMenuLayout mReaderMenuLayout;
    private View mSubMenuTopBoundary,mMenuBottomBoundary;
    private static GObject defaultMenu;
    private int mPositionID;
    private ReaderMenuLayout.ReaderMenuCallback mReaderMenuCallBack;
    private ImageView mSwitchIcon;

    private void notifyOnMenuItemClickedListener(GObject scribbleMenu) {
        this.invokeMenuCallback(scribbleMenu);
        boolean close = getMenuCloseAfterClick(scribbleMenu);
        if (close) {
            this.hide();
        }
    }

    private void setNormalMode(){
        mMode=Mode.Normal;
        mMenuCallback.setMode(mMode);
    }

    private void setScribbleMode() {
        mMode = Mode.Scribble;
        mMenuCallback.setMode(mMode);
    }

    private void setErasePartialMode() {
        mMode = Mode.Erase;
        mMenuCallback.setMode(mMode);
    }

    private void setEraseAllMode() {
        mMode = Mode.Scribble;
        mMenuCallback.erasePage();
    }

    private void invokeMenuCallback(GObject scribbleMenu) {
        ScribbleMenuID mScribbleMenuID = getMenuId(scribbleMenu);
        mReaderMenuLayout.hideSubMenu();
        switch (mScribbleMenuID) {
            case Thickness_Group:
                setScribbleMode();
                mReaderMenuLayout.showSubMenu();
                break;
            case Color_Group:
                setScribbleMode();
                mReaderMenuLayout.showSubMenu();
                break;
            case Thickness_Ultra_Light:
                ScribbleFactory.singleton().setThickness(getContext(), 2);
                updateButtonFocus();
                setScribbleMode();
                break;
            case Thickness_Light:
                ScribbleFactory.singleton().setThickness(getContext(), 4);
                updateButtonFocus();
                setScribbleMode();
                break;
            case Thickness_Normal:
                ScribbleFactory.singleton().setThickness(getContext(), 6);
                updateButtonFocus();
                setScribbleMode();
                break;
            case Thickness_Bold:
                ScribbleFactory.singleton().setThickness(getContext(), 7);
                updateButtonFocus();
                setScribbleMode();
                break;
            case Thickness_Ultra_Bold:
                ScribbleFactory.singleton().setThickness(getContext(), 8);
                updateButtonFocus();
                setScribbleMode();
                break;
            case Color_White:
                ScribbleFactory.singleton().setColor(Color.WHITE);
                updateButtonFocus();
                setScribbleMode();
                break;
            case Color_Light_Grey:
                ScribbleFactory.singleton().setColor(Color.LTGRAY);
                updateButtonFocus();
                setScribbleMode();
                break;
            case Color_Grey:
                ScribbleFactory.singleton().setColor(Color.GRAY);
                updateButtonFocus();
                setScribbleMode();
                break;
            case Color_Dark_Grey:
                ScribbleFactory.singleton().setColor(Color.DKGRAY);
                updateButtonFocus();
                setScribbleMode();
                break;
            case Color_Black:
                ScribbleFactory.singleton().setColor(Color.BLACK);
                updateButtonFocus();
                setScribbleMode();
                break;
            case Move:
                setNormalMode();
                updateButtonFocus();
                break;
            case Erase_Partially:
                setErasePartialMode();
                updateButtonFocus();
                break;
            case Erase_Totally:
                setEraseAllMode();
                updateButtonFocus();
                break;
            case Derive_Current_Page:
                Toast.makeText(mContext, "No Such Function now.", Toast.LENGTH_SHORT).show();
                updateButtonFocus();
                break;
            case Derive_All:
                Toast.makeText(mContext, "No Such Function now.", Toast.LENGTH_SHORT).show();
                updateButtonFocus();
                break;
            case MinimizeMenu:
                minimizeMenu();
                break;
            case Close:
                setNormalMode();
                break;
            case Erase_Group:
                setErasePartialMode();
                mReaderMenuLayout.showSubMenu();
                break;
            default:
                mReaderMenuLayout.showSubMenu();
                break;
        }
    }

    /**
     * default return false
     *
     * @param menu
     * @return
     */
    private static boolean getMenuCloseAfterClick(GObject menu) {
        return menu.getBoolean(GAdapterUtil.TAG_MENU_CLOSE_AFTER_CLICK);
    }

    private static void saveMenuCloseAfterClick(GObject menu, boolean close) {
        menu.putBoolean(GAdapterUtil.TAG_MENU_CLOSE_AFTER_CLICK, close);
    }


    public ScribblePopupTableMenu(Context context, RelativeLayout parentLayout,MenuCallback callback,int positionID,boolean isShowStatusBar) {
        super(context);
        final LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.scribble_popuptablemenu, this, true);
        mReaderMenuLayout = (ReaderMenuLayout)findViewById(R.id.layout_reader_menu);
        mReaderMenuLayout.setmMainMenuViewColumns(getResources().getInteger(R.integer.scribble_menu_columns));
        mPositionID = positionID;
        parentLayout.addView(this, setMenuPosition(isShowStatusBar));
        mSubMenuTopBoundary = findViewById(R.id.subMenuTopBoundary);
        mSwitchIcon = (ImageView)findViewById(R.id.switch_icon);
        mSwitchIcon.setVisibility(GONE);
        mSwitchIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(mReaderMenuLayout.getVisibility() == VISIBLE)) {
                    maximizeMenu();
                    v.setVisibility(GONE);
                }
            }
        });
        mMenuBottomBoundary = findViewById(R.id.menuBottomBoundary);
        mMenuCallback = callback;
        mContext = context;
        scribbleMenus = createScribbleMenus();
        mReaderMenuCallBack = new ReaderMenuLayout.ReaderMenuCallback() {
            @Override
            public void onMenuItemClicked(GObject item) {
                notifyOnMenuItemClickedListener(item);
            }

            @Override
            public void preMenuItemClicked(GObject item) {

            }

            @Override
            public void hideSubMenu() {
                super.hideSubMenu();
                mMenuCallback.onLayoutStateChanged();
            }

            @Override
            public void showSubMenu() {
                super.showSubMenu();
                showSubMenuTopBoundary();
                mMenuCallback.onLayoutStateChanged();
            }
        };
        mReaderMenuLayout.setupMenu(scribbleMenus, defaultMenu, mReaderMenuCallBack,false);
        setVisibility(View.GONE);
    }

    private GAdapter getChildAdapter(final LinkedHashMap<GObject, GAdapter> map, final ScribbleMenuID menuId) {
        for(Map.Entry<GObject, GAdapter> entry : map.entrySet()) {
            ScribbleMenuID id = (ScribbleMenuID)entry.getKey().getObject(GAdapterUtil.TAG_MENU_ID);
            if (id == menuId) {
                return entry.getValue();
            }
        }
        return null;
    }

    public void show(final Mode mode) {
        ScribbleMenuID menu_id = mode == Mode.Erase ?
                ScribbleMenuID.Erase_Group :
                ScribbleMenuID.Thickness_Group;
        GObject menu_group = mReaderMenuLayout.searchFirstMenuByTag(GAdapterUtil.TAG_MENU_ID, menu_id);
        if (menu_group != null) {
            mReaderMenuLayout.updateItemIndicator(menu_group);
        }
        mMenuCallback.setMode(mode);
        setFocusable(true);
        mReaderMenuLayout.mMainMenuView.requestFocus();
        mMode = mode;
        updateButtonFocus();
        setVisibility(View.VISIBLE);
        mMenuCallback.onLayoutStateChanged();
    }

    public void move(int selectionEndY) {
        if (this == null) {
            return;
        }

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        final int verticalPosition;
        final int screenHeight = ((View)this.getParent()).getHeight();
        final int diffTop = screenHeight - selectionEndY;
        if (diffTop > selectionEndY) {
            verticalPosition = diffTop > this.getHeight() + 20
                    ? RelativeLayout.ALIGN_PARENT_BOTTOM : RelativeLayout.CENTER_VERTICAL;
        } else {
            verticalPosition = selectionEndY > this.getHeight() + 20
                    ? RelativeLayout.ALIGN_PARENT_TOP : RelativeLayout.CENTER_VERTICAL;
        }

        layoutParams.addRule(verticalPosition);
        setLayoutParams(layoutParams);

        mMenuCallback.onLayoutStateChanged();
    }

    public void hide() {
        setVisibility(GONE);
        if (mMenuCallback != null) {
            mMenuCallback.dismiss();
            mMenuCallback.onLayoutStateChanged();
        }
    }

    public boolean isShow() {
        return getVisibility() == VISIBLE;
    }

    private void updateButtonFocus() {
//        mBlackPenModeButton.setBackgroundResource(R.drawable.imagebtn_bg);
//        mWhitePenModeButton.setBackgroundResource(R.drawable.imagebtn_bg);
//        mNormalPenModeButton.setBackgroundResource(R.drawable.imagebtn_bg);
//        mBoldPenModeButton.setBackgroundResource(R.drawable.imagebtn_bg);
//        mUltraBoldPenModeButton.setBackgroundResource(R.drawable.imagebtn_bg);
//        mNormalModeButton.setBackgroundResource(R.drawable.imagebtn_bg);
//        mScribbleModeButton.setBackgroundResource(R.drawable.imagebtn_bg);
//
//        switch (ScribbleFactory.singleton().getColor()) {
//            case Color.BLACK:
//                mBlackPenModeButton.setBackgroundResource(R.drawable.imagebtn_focused);
//                break;
//            case Color.WHITE:
//                mWhitePenModeButton.setBackgroundResource(R.drawable.imagebtn_focused);
//                break;
//            default:
//                break;
//        }
//        switch (ScribbleFactory.singleton().getThickness()) {
//            case 3:
//                mNormalPenModeButton.setBackgroundResource(R.drawable.imagebtn_focused);
//                break;
//            case 5:
//                mBoldPenModeButton.setBackgroundResource(R.drawable.imagebtn_focused);
//                break;
//            case 7:
//                mUltraBoldPenModeButton.setBackgroundResource(R.drawable.imagebtn_focused);
//                break;
//            default:
//                break;
//        }
//        switch (mMode) {
//            case Normal:
//                mNormalModeButton.setBackgroundResource(R.drawable.imagebtn_focused);
//                break;
//            case Scribble:
//                mScribbleModeButton.setBackgroundResource(R.drawable.imagebtn_focused);
//                break;
//            default:
//                break;
//        }
    }



    private static GObject createImageButtonMenu(int imageResource, ScribbleMenuID action, boolean closeAfterClick) {
        final int layout_id = R.layout.onyx_reader_menu_imagebutton;
        HashMap<String, Integer> mapping = new HashMap<String, Integer>();
        mapping.put(GAdapterUtil.TAG_IMAGE_RESOURCE, R.id.imageview_icon);
        GObject item = GAdapterUtil.createTableItem("", "", imageResource, layout_id, mapping);
        saveMenuId(item, action);
        saveMenuCloseAfterClick(item, closeAfterClick);
        return item;
    }

    private static GObject createImageFocusButtonMenu(int imageResource, ScribbleMenuID action, boolean closeAfterClick) {
        final int layout_id = R.layout.onyx_reader_menu_imagebutton;
        HashMap<String, Integer> mapping = new HashMap<String, Integer>();
        int selectionCheckedViewId=R.id.imageview_icon;
        int selectionUncheckedViewId=R.id.imageview_icon;
        mapping.put(GAdapterUtil.TAG_IMAGE_RESOURCE, R.id.imageview_icon);
        mapping.put(GAdapterUtil.TAG_SELECTION_CHECKED_VIEW_ID,selectionCheckedViewId);
        mapping.put(GAdapterUtil.TAG_SELECTION_UNCHECKED_VIEW_ID,selectionUncheckedViewId);
        GObject item = GAdapterUtil.createTableItem("", "", imageResource,layout_id,
                selectionCheckedViewId,selectionUncheckedViewId, null);
        saveMenuId(item, action);
        saveMenuCloseAfterClick(item, closeAfterClick);
        return item;
    }

    private static ScribbleMenuID getMenuId(GObject menu) {
        return (ScribbleMenuID)menu.getObject(GAdapterUtil.TAG_MENU_ID);
    }

    private static void saveMenuId(GObject menu, ScribbleMenuID menuId) {
        menu.putObject(GAdapterUtil.TAG_MENU_ID, menuId);
    }

    private static LinkedHashMap<GObject, GAdapter> createScribbleMenus() {
        final LinkedHashMap<GObject, GAdapter> scribbleMenu = new LinkedHashMap<GObject, GAdapter>();
        addThicknessMenu(scribbleMenu);
        addColorMenu(scribbleMenu);
        addEraseMenu(scribbleMenu);
        addMoveMenu(scribbleMenu);
        //addDeriveMenu(scribbleMenu);
        addMinimizeMenu(scribbleMenu);
        addCloseMenu(scribbleMenu);
        return scribbleMenu;
    }

     private static void addThicknessMenu(final LinkedHashMap<GObject, GAdapter> menu){
         GObject thickness_menu = createImageButtonMenu(R.drawable.thickness, ScribbleMenuID.Thickness_Group, false);
         GAdapter thickness_sub_menus = new GAdapter();
         saveLayoutGravity(thickness_sub_menus, Gravity.LEFT);
         thickness_sub_menus.addObject(createImageButtonMenu(R.drawable.thickness_ultra_light, ScribbleMenuID.Thickness_Ultra_Light, false));
         thickness_sub_menus.addObject(createImageButtonMenu(R.drawable.thickness_light, ScribbleMenuID.Thickness_Light, false));
         thickness_sub_menus.addObject(createImageButtonMenu(R.drawable.thickness_normal,ScribbleMenuID.Thickness_Normal, false));
         thickness_sub_menus.addObject(createImageButtonMenu(R.drawable.thickness_bold, ScribbleMenuID.Thickness_Bold, false));
         thickness_sub_menus.addObject(createImageButtonMenu(R.drawable.thickness_ultra_bold,ScribbleMenuID.Thickness_Ultra_Bold, false));
         defaultMenu=thickness_menu;
         menu.put(thickness_menu,thickness_sub_menus);
     }

     private static void addColorMenu(final LinkedHashMap<GObject, GAdapter> menu){
         GObject color_menu = createImageButtonMenu(R.drawable.color, ScribbleMenuID.Color_Group, false);
         GAdapter color_sub_menus = new GAdapter();
         saveLayoutGravity(color_sub_menus, Gravity.LEFT);
         color_sub_menus.addObject(createImageButtonMenu(R.drawable.white, ScribbleMenuID.Color_White, false));
         //color_sub_menus.addObject(createImageButtonMenu(R.drawable.light_grey, ScribbleMenuID.Color_Light_Grey, false));
         //color_sub_menus.addObject(createImageButtonMenu(R.drawable.grey,ScribbleMenuID.Color_Grey, false));
         //color_sub_menus.addObject(createImageButtonMenu(R.drawable.dark_grey, ScribbleMenuID.Color_Dark_Grey, false));
         color_sub_menus.addObject(createImageButtonMenu(R.drawable.black,ScribbleMenuID.Color_Black, false));
         menu.put(color_menu,color_sub_menus);
     }

      private static void addEraseMenu(final LinkedHashMap<GObject, GAdapter> menu){
          GObject erase_menu = createImageButtonMenu(R.drawable.erase, ScribbleMenuID.Erase_Group, false);
          GAdapter erase_sub_menus = new GAdapter();
          saveLayoutGravity(erase_sub_menus, Gravity.LEFT);
          erase_sub_menus.addObject(createImageButtonMenu(R.drawable.erase_partially, ScribbleMenuID.Erase_Partially, false));
          erase_sub_menus.addObject(createImageButtonMenu(R.drawable.erase_totally, ScribbleMenuID.Erase_Totally, false));
          menu.put(erase_menu,erase_sub_menus);
      }

    private static void addMoveMenu(final LinkedHashMap<GObject, GAdapter> menu){
        GObject move_menu = createImageButtonMenu(R.drawable.move, ScribbleMenuID.Move, false);
        menu.put(move_menu,null);
    }

    private static void addDeriveMenu(final LinkedHashMap<GObject, GAdapter> menu){
        GObject derive_menu = createImageButtonMenu(R.drawable.derive, ScribbleMenuID.Derive_Group, false);
        GAdapter derive_sub_menus = new GAdapter();
        saveLayoutGravity(derive_sub_menus, Gravity.LEFT);
        derive_sub_menus.addObject(createImageButtonMenu(R.drawable.derive_current_page, ScribbleMenuID.Derive_Current_Page, false));
        derive_sub_menus.addObject(createImageButtonMenu(R.drawable.derive_all, ScribbleMenuID.Derive_All, false));
        menu.put(derive_menu,derive_sub_menus);
    }

    private static void addMinimizeMenu(final LinkedHashMap<GObject, GAdapter> menu){
        GObject minimize_menu = createImageButtonMenu(R.drawable.button_exit, ScribbleMenuID.MinimizeMenu, false);
        menu.put(minimize_menu,null);
    }

     private static void addCloseMenu(final LinkedHashMap<GObject, GAdapter> menu){
         GObject close_menu = createImageButtonMenu(R.drawable.button_exit, ScribbleMenuID.Close, true);
         menu.put(close_menu,null);
     }


    private static int getLayoutGravity(GAdapter adapter) {
        return adapter.getOptions().getInt(GAdapterUtil.TAG_LAYOUT_GRAVITY, Gravity.CENTER);
    }

    private static void saveLayoutGravity(GAdapter adapter, int gravity) {
        adapter.getOptions().putInt(GAdapterUtil.TAG_LAYOUT_GRAVITY, gravity);
    }

    public void rePositionAfterNewConfiguration(boolean isShowStatusBar){
        setLayoutParams(setMenuPosition(isShowStatusBar));
        mMenuCallback.onLayoutStateChanged();
    }

    private void minimizeMenu(){
        hideMenuBottomBoundary();
        hideSubMenuTopBoundary();
        hideInsideScribbleMenu();
        mSwitchIcon.setVisibility(VISIBLE);
    }

    private void maximizeMenu(){
        showSubMenuTopBoundary();
        showMenuBottomBoundary();
        showInsideScribbleMenu();
    }

    private void hideInsideScribbleMenu(){
        mReaderMenuLayout.setVisibility(GONE);
    }

    private void showInsideScribbleMenu(){
        mReaderMenuLayout.setVisibility(VISIBLE);
    }

    private void hideSubMenuTopBoundary(){
        mSubMenuTopBoundary.setVisibility(GONE);
    }

    private void showSubMenuTopBoundary(){
        mSubMenuTopBoundary.setVisibility(VISIBLE);
    }

    private void hideMenuBottomBoundary(){
        mMenuBottomBoundary.setVisibility(GONE);
    }

    private void showMenuBottomBoundary(){
        mMenuBottomBoundary.setVisibility(VISIBLE);
    }

    private RelativeLayout.LayoutParams setMenuPosition(boolean isShowStatusBar){
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        if (isShowStatusBar){
            p.addRule(RelativeLayout.ABOVE,mPositionID);
        } else {
            p.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        }
        p.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        return p;
    }
}
