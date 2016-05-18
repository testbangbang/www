package com.onyx.android.sdk.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.data.GAdapter;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.data.util.GAdapterUtil;
import com.onyx.android.sdk.ui.data.ScribbleFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by solskjaer49 on 15/7/13 11:54.
 */
public class ScribblePopupContentMenu extends LinearLayout {
    private enum ScribbleMenuID {
        Thickness_Group, Thickness_Ultra_Light, Thickness_Light, Thickness_Normal, Thickness_Bold, Thickness_Ultra_Bold,
        Color_Group, Color_White, Color_Light_Grey, Color_Grey, Color_Dark_Grey, Color_Black,
        Erase_Group, Erase_Partially, Erase_Totally,
        Move,
        Derive_Group, Derive_Current_Page, Derive_All,
        MinimizeMenu,
        Close
    }

    public static abstract class MenuCallback {
        public abstract void dismiss();

        public abstract void setMode(Mode mMode);

        public abstract void erasePage();

        public abstract void onLayoutStateChanged();
    }

    public enum Mode {Normal, Scribble, Erase}

    private MenuCallback mMenuCallback;

    private Mode mMode = Mode.Scribble;
    private Context mContext;
    private ReaderMenuContentLayout mReaderMenuLayout;
    private View mSubMenuTopBoundary, mMenuBottomBoundary;
    private static GObject defaultMenu;
    private int mPositionID;
    private ReaderMenuContentLayout.ReaderMenuCallback mReaderMenuCallBack;
    private ImageView mSwitchIcon;
    static HashMap<String, Integer> mapping = null;
    static LinkedHashMap<GObject, GAdapter> scribbleMenus = new LinkedHashMap<GObject, GAdapter>();
    private Handler handler = new Handler(Looper.getMainLooper());
    final static int DELAY = 300;

    private void notifyOnMenuItemClickedListener(GObject menuItem) {
        updateButtonIndicator(menuItem);
        invokeMenuCallback(menuItem);
        boolean close = menuItem.getBoolean(GAdapterUtil.TAG_MENU_CLOSE_AFTER_CLICK);
        if (close) {
            this.hide();
        }
    }

    public void hide() {
        setVisibility(GONE);
        if (mMenuCallback != null) {
            mMenuCallback.dismiss();
        }
    }

    public void show(final Mode mode) {
        ScribbleMenuID menu_id = mode == Mode.Erase ?
                ScribbleMenuID.Erase_Group :
                ScribbleMenuID.Thickness_Group;
        GObject menuItem = mReaderMenuLayout.getMainMenuView().getCurrentAdapter().searchFirstByTag(GAdapterUtil.TAG_MENU_ID,menu_id);
        int dataIndex = mReaderMenuLayout.getMainMenuView().getCurrentAdapter().getGObjectIndex(menuItem);
        setSelected(menuItem, true);
        mReaderMenuLayout.getMainMenuView().getCurrentAdapter().setObject(dataIndex, menuItem);
        mReaderMenuLayout.getMainMenuView().unCheckOtherViews(dataIndex);
        mReaderMenuLayout.getMainMenuView().updateCurrentPage();
        mMenuCallback.setMode(mode);
        setFocusable(true);
        mReaderMenuLayout.getMainMenuView().requestFocus();
        mMode = mode;
        setVisibility(View.VISIBLE);
    }

    private void setNormalMode() {
        mMode = Mode.Normal;
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

    private void invokeMenuCallback(GObject menuItem) {
        ScribbleMenuID mScribbleMenuID = getMenuId(menuItem);
        mReaderMenuLayout.hideSubMenu();
        switch (mScribbleMenuID) {
            case Thickness_Group:
            case Color_Group:
                setScribbleMode();
                mReaderMenuLayout.showSubMenu();
                break;
            case Thickness_Ultra_Light:
                ScribbleFactory.singleton().setThickness(getContext(), 3);
                setScribbleMode();
                break;
            case Thickness_Light:
                ScribbleFactory.singleton().setThickness(getContext(), 4);
                setScribbleMode();
                break;
            case Thickness_Normal:
                ScribbleFactory.singleton().setThickness(getContext(), 6);
                setScribbleMode();
                break;
            case Thickness_Bold:
                ScribbleFactory.singleton().setThickness(getContext(), 7);
                setScribbleMode();
                break;
            case Thickness_Ultra_Bold:
                ScribbleFactory.singleton().setThickness(getContext(), 8);
                setScribbleMode();
                break;
            case Color_White:
                ScribbleFactory.singleton().setColor(Color.WHITE);
                setScribbleMode();
                break;
            case Color_Light_Grey:
                ScribbleFactory.singleton().setColor(Color.LTGRAY);
                setScribbleMode();
                break;
            case Color_Grey:
                ScribbleFactory.singleton().setColor(Color.GRAY);
                setScribbleMode();
                break;
            case Color_Dark_Grey:
                ScribbleFactory.singleton().setColor(Color.DKGRAY);
                setScribbleMode();
                break;
            case Color_Black:
                ScribbleFactory.singleton().setColor(Color.BLACK);
                setScribbleMode();
                break;
            case Move:
                setNormalMode();
                break;
            case Erase_Partially:
                setErasePartialMode();
                break;
            case Erase_Totally:
                setEraseAllMode();
                break;
            case Derive_Current_Page:
                Toast.makeText(mContext, "No Such Function now.", Toast.LENGTH_SHORT).show();
                break;
            case Derive_All:
                Toast.makeText(mContext, "No Such Function now.", Toast.LENGTH_SHORT).show();
                break;
            case MinimizeMenu:
                setNormalMode();
                minimizeMenu();
                setScribbleMode();
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

    private void updateButtonIndicator(GObject menuItem) {
        int dataIndex;
        switch (getMenuId(menuItem)){
            case Thickness_Group:
            case Color_Group:
            case Move:
            case Erase_Group:
                dataIndex = mReaderMenuLayout.getMainMenuView().getCurrentAdapter().getGObjectIndex(menuItem);
                setSelected(menuItem, true);
                mReaderMenuLayout.getMainMenuView().getCurrentAdapter().setObject(dataIndex, menuItem);
                mReaderMenuLayout.getMainMenuView().unCheckOtherViews(dataIndex);
                mReaderMenuLayout.getMainMenuView().updateCurrentPage();
                updateSubMenuIndicatorByCurrentStatus(getMenuId(menuItem));
                break;
            case Thickness_Ultra_Light:
            case Thickness_Light:
            case Thickness_Normal:
            case Thickness_Bold:
            case Thickness_Ultra_Bold:
            case Color_White:
            case Color_Light_Grey:
            case Color_Grey:
            case Color_Dark_Grey:
            case Color_Black:
                dataIndex = mReaderMenuLayout.getSubMenuView().getCurrentAdapter().getGObjectIndex(menuItem);
                setSelected(menuItem, true);
                mReaderMenuLayout.getSubMenuView().getCurrentAdapter().setObject(dataIndex, menuItem);
                mReaderMenuLayout.getSubMenuView().unCheckOtherViews(dataIndex);
                mReaderMenuLayout.getSubMenuView().updateCurrentPage();
                break;
        }
    }

    private void updateSubMenuIndicatorByCurrentStatus(ScribbleMenuID menuID) {
        GObject object;
        int dataIndex;
        Object targetPattern = null;
        switch (menuID) {
            case Thickness_Group:
                switch (ScribbleFactory.singleton().getThickness(getContext())) {
                    case 3:
                        targetPattern = ScribbleMenuID.Thickness_Ultra_Light;
                        break;
                    case 4:
                        targetPattern = ScribbleMenuID.Thickness_Light;
                        break;
                    case 6:
                        targetPattern = ScribbleMenuID.Thickness_Normal;
                        break;
                    case 7:
                        targetPattern = ScribbleMenuID.Thickness_Bold;
                        break;
                    case 8:
                        targetPattern = ScribbleMenuID.Thickness_Ultra_Bold;
                        break;
                }
                break;
            case Color_Group:
                switch (ScribbleFactory.singleton().getColor()) {
                    case Color.WHITE:
                        targetPattern = ScribbleMenuID.Color_White;
                        break;
                    case Color.LTGRAY:
                        targetPattern = ScribbleMenuID.Color_Light_Grey;
                        break;
                    case Color.GRAY:
                        targetPattern = ScribbleMenuID.Color_Grey;
                        break;
                    case Color.DKGRAY:
                        targetPattern = ScribbleMenuID.Color_Dark_Grey;
                        break;
                    case Color.BLACK:
                        targetPattern = ScribbleMenuID.Color_Black;
                        break;
                }
                break;
            default:
                return;
        }
        object = mReaderMenuLayout.getSubMenuView().getCurrentAdapter().searchFirstByTag(GAdapterUtil.TAG_MENU_ID,
                targetPattern);
        setSelected(object, true);
        dataIndex = mReaderMenuLayout.getSubMenuView().getCurrentAdapter().getGObjectIndex(object);
        mReaderMenuLayout.getSubMenuView().getCurrentAdapter().setObject(dataIndex, object);
        mReaderMenuLayout.getSubMenuView().unCheckOtherViews(dataIndex);
        mReaderMenuLayout.getSubMenuView().updateCurrentPage();
    }

    //Todo:could config by json file depend on different device.
    private ArrayList<ScribbleMenuID> buildSubMenuIDList() {
        ArrayList<ScribbleMenuID> idList = new ArrayList<ScribbleMenuID>();
        idList.add(ScribbleMenuID.Thickness_Group);
        idList.add(ScribbleMenuID.Color_Group);
        idList.add(ScribbleMenuID.Erase_Group);
        idList.add(ScribbleMenuID.Move);
        idList.add(ScribbleMenuID.MinimizeMenu);
        idList.add(ScribbleMenuID.Close);
        return idList;
    }

    private void createScribbleMenus() {
        scribbleMenus.clear();
        for (ScribbleMenuID menuID : buildSubMenuIDList()) {
            buildMenuByID(menuID);
        }
    }

    private static void buildMenuByID(ScribbleMenuID id) {
        GObject mainMenuItem = null;
        GAdapter subMenuAdapter = null;
        switch (id) {
            case Thickness_Group:
                mainMenuItem = createImageButtonMenu(R.drawable.thickness, ScribbleMenuID.Thickness_Group, false);
                setSelected(mainMenuItem,true);
                subMenuAdapter = new GAdapter();
                subMenuAdapter.addObject(createImageButtonMenu(R.drawable.thickness_ultra_light, ScribbleMenuID.Thickness_Ultra_Light, false));
                subMenuAdapter.addObject(createImageButtonMenu(R.drawable.thickness_light, ScribbleMenuID.Thickness_Light, false));
                subMenuAdapter.addObject(createImageButtonMenu(R.drawable.thickness_normal, ScribbleMenuID.Thickness_Normal, false));
                subMenuAdapter.addObject(createImageButtonMenu(R.drawable.thickness_bold, ScribbleMenuID.Thickness_Bold, false));
                subMenuAdapter.addObject(createImageButtonMenu(R.drawable.thickness_ultra_bold, ScribbleMenuID.Thickness_Ultra_Bold, false));
                defaultMenu = mainMenuItem;
                break;
            case Color_Group:
                mainMenuItem = createImageButtonMenu(R.drawable.color, ScribbleMenuID.Color_Group, false);
                subMenuAdapter = new GAdapter();
                subMenuAdapter.addObject(createImageButtonMenu(R.drawable.white, ScribbleMenuID.Color_White, false));
                subMenuAdapter.addObject(createImageButtonMenu(R.drawable.black, ScribbleMenuID.Color_Black, false));
                //subMenuAdapter.addObject(createImageButtonMenu(R.drawable.light_grey, ScribbleMenuID.Color_Light_Grey, false));
                //subMenuAdapter.addObject(createImageButtonMenu(R.drawable.grey,ScribbleMenuID.Color_Grey, false));
                //subMenuAdapter.addObject(createImageButtonMenu(R.drawable.dark_grey, ScribbleMenuID.Color_Dark_Grey, false));
                break;
            case Erase_Group:
                mainMenuItem = createImageButtonMenu(R.drawable.erase, ScribbleMenuID.Erase_Group, false);
                subMenuAdapter = new GAdapter();
                subMenuAdapter.addObject(createImageButtonMenu(R.drawable.erase_partially, ScribbleMenuID.Erase_Partially, false));
                subMenuAdapter.addObject(createImageButtonMenu(R.drawable.erase_totally, ScribbleMenuID.Erase_Totally, false));
                break;
            case Move:
                mainMenuItem = createImageButtonMenu(R.drawable.move, ScribbleMenuID.Move, false);
                break;
            case Derive_Group:
                mainMenuItem = createImageButtonMenu(R.drawable.derive, ScribbleMenuID.Derive_Group, false);
                subMenuAdapter = new GAdapter();
                subMenuAdapter.addObject(createImageButtonMenu(R.drawable.derive_current_page, ScribbleMenuID.Derive_Current_Page, false));
                subMenuAdapter.addObject(createImageButtonMenu(R.drawable.derive_all, ScribbleMenuID.Derive_All, false));
                break;
            case MinimizeMenu:
                mainMenuItem = createImageButtonMenu(R.drawable.ic_hide_scribble_menu, ScribbleMenuID.MinimizeMenu, false);
                break;
            case Close:
                mainMenuItem = createImageButtonMenu(R.drawable.button_exit, ScribbleMenuID.Close, true);
                break;
        }
        scribbleMenus.put(mainMenuItem, subMenuAdapter);
    }

    private static GObject createImageButtonMenu(int imageResource, ScribbleMenuID action, boolean closeAfterClick) {
        GObject item = GAdapterUtil.createTableItem(0, 0, imageResource, 0, null);
        item.putObject(GAdapterUtil.TAG_MENU_ID, action);
        item.putBoolean(GAdapterUtil.TAG_MENU_CLOSE_AFTER_CLICK, closeAfterClick);
        return item;
    }

    private static HashMap<String, Integer> getMapping() {
        if (mapping == null) {
            mapping = new HashMap<String, Integer>();
            mapping.put(GAdapterUtil.TAG_IMAGE_RESOURCE, R.id.imageview_icon);
            mapping.put(GAdapterUtil.TAG_SELECTABLE, R.id.checkbox_current_select);
        }
        return mapping;
    }

    private static ScribbleMenuID getMenuId(GObject menu) {
        return (ScribbleMenuID) menu.getObject(GAdapterUtil.TAG_MENU_ID);
    }

    public ScribblePopupContentMenu(Context context) {
        this(context, null);
    }

    public ScribblePopupContentMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScribblePopupContentMenu(Context context, RelativeLayout parentLayout, MenuCallback callback, int positionID, boolean isShowStatusBar) {
        this(context);
        final LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.scribble_popup_contentmenu, this, true);
        mReaderMenuLayout = (ReaderMenuContentLayout) findViewById(R.id.layout_reader_menu);
        initContentView();
        mPositionID = positionID;
        parentLayout.addView(this, setMenuPosition(isShowStatusBar));
        mSubMenuTopBoundary = findViewById(R.id.subMenuTopBoundary);
        mSwitchIcon = (ImageView) findViewById(R.id.switch_icon);
        mSwitchIcon.setVisibility(GONE);
        mSwitchIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (!(mReaderMenuLayout.getVisibility() == VISIBLE)) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setNormalMode();
                            maximizeMenu();
                            v.setVisibility(GONE);
                            setScribbleMode();
                        }
                    }, DELAY);
                }
            }
        });
        mMenuBottomBoundary = findViewById(R.id.menuBottomBoundary);
        mMenuCallback = callback;
        mContext = context;
        createScribbleMenus();
        mReaderMenuCallBack = new ReaderMenuContentLayout.ReaderMenuCallback() {
            @Override
            public void onMenuItemClicked(GObject item) {
                notifyOnMenuItemClickedListener(item);
            }

            @Override
            public void hideSubMenu() {
                super.hideSubMenu();
            }

            @Override
            public void showSubMenu() {
                super.showSubMenu();
                showSubMenuTopBoundary();
            }
        };
        mReaderMenuLayout.setupMenu(scribbleMenus, defaultMenu, mReaderMenuCallBack, false);
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mMenuCallback.onLayoutStateChanged();
            }
        });
        setVisibility(View.GONE);
    }

    private void initContentView(){
        mReaderMenuLayout.setMainMenuViewColumns(getResources().getInteger(R.integer.scribble_menu_columns));
        mReaderMenuLayout.setSubMenuViewColumns(getResources().getInteger(R.integer.scribble_menu_columns));
        mReaderMenuLayout.getMainMenuView().setSubLayoutParameter(R.layout.onyx_reader_menu_imagebutton, getMapping());
        mReaderMenuLayout.getSubMenuView().setSubLayoutParameter(R.layout.onyx_reader_menu_imagebutton, getMapping());
    }

    public void rePositionAfterNewConfiguration(boolean isShowStatusBar) {
        setLayoutParams(setMenuPosition(isShowStatusBar));
    }

    private void minimizeMenu() {
        hideMenuBottomBoundary();
        hideSubMenuTopBoundary();
        hideInsideScribbleMenu();
        mSwitchIcon.setVisibility(VISIBLE);
    }

    private void maximizeMenu() {
        showSubMenuTopBoundary();
        showMenuBottomBoundary();
        showInsideScribbleMenu();
    }

    private void hideInsideScribbleMenu() {
        mReaderMenuLayout.setVisibility(GONE);
    }

    private void showInsideScribbleMenu() {
        mReaderMenuLayout.setVisibility(VISIBLE);
    }

    private void hideSubMenuTopBoundary() {
        mSubMenuTopBoundary.setVisibility(GONE);
    }

    private void showSubMenuTopBoundary() {
        mSubMenuTopBoundary.setVisibility(VISIBLE);
    }

    private void hideMenuBottomBoundary() {
        mMenuBottomBoundary.setVisibility(GONE);
    }

    private void showMenuBottomBoundary() {
        mMenuBottomBoundary.setVisibility(VISIBLE);
    }

    private RelativeLayout.LayoutParams setMenuPosition(boolean isShowStatusBar) {
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        if (isShowStatusBar) {
            p.addRule(RelativeLayout.ABOVE, mPositionID);
        } else {
            p.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        }
        p.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        return p;
    }

    private static GObject getSelectedObject(GAdapter adapter) {
        for (GObject o : adapter.getList()) {
            if (isSelected(o)) {
                return o;
            }
        }
        return null;
    }

    private static void setSelected(GObject object, boolean value) {
        object.putBoolean(GAdapterUtil.TAG_SELECTABLE, value);
    }

    private static boolean isSelected(GObject object) {
        return object.getBoolean(GAdapterUtil.TAG_SELECTABLE);
    }

}
