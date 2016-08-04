package com.onyx.android.note.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.onyx.android.note.R;
import com.onyx.android.note.data.ScribbleMenuCategory;
import com.onyx.android.note.data.ScribbleMode;
import com.onyx.android.note.data.ScribbleSubMenuID;
import com.onyx.android.sdk.data.GAdapter;
import com.onyx.android.sdk.data.GAdapterUtil;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.ui.view.ContentItemView;
import com.onyx.android.sdk.ui.view.ContentView;

import java.util.HashMap;


/**
 * Created by solskjaer49 on 16/8/4 15:05.
 */
public class ScribbleSubMenu extends LinearLayout {

    public static abstract class MenuCallback {
        public abstract void dismiss();

        public abstract void setMode(@ScribbleMode.ScribbleModeDef int mMode);

        public abstract void erasePage();

        public abstract void onLayoutStateChanged();

        public boolean supportColor() {
            return false;
        }
    }

    private final MenuCallback mMenuCallback;
    private Context mContext;
    GAdapter mThicknessAdapter, mColorAdapter, mEraseAdapter, mPenStyleAdapter = new GAdapter();
    private ContentView mMenuContentView;
    static HashMap<String, Integer> mapping = null;
    private int mPositionID;
    private
    @ScribbleMenuCategory.ScribbleMenuCategoryDef
    int currentCategory;

    private void notifyOnMenuItemClickedListener(GObject scribbleMenu) {
        updateButtonIndicator(scribbleMenu);
        boolean close = getMenuCloseAfterClick(scribbleMenu);
        if (close) {
            this.hide();
        }
        this.invokeMenuCallback(scribbleMenu);
    }

    private void invokeMenuCallback(GObject scribbleMenu) {
        @ScribbleSubMenuID.ScribbleSubMenuIDDef int mScribbleMenuID = getMenuUniqueId(scribbleMenu);
        switch (mScribbleMenuID) {
            case ScribbleSubMenuID.THICKNESS_ULTRA_LIGHT:
                setScribbleMode();
                break;
            case ScribbleSubMenuID.THICKNESS_LIGHT:
                setScribbleMode();
                break;
            case ScribbleSubMenuID.THICKNESS_NORMAL:
                setScribbleMode();
                break;
            case ScribbleSubMenuID.THICKNESS_BOLD:
                setScribbleMode();
                break;
            case ScribbleSubMenuID.THICKNESS_ULTRA_BOLD:
                setScribbleMode();
                break;
            case ScribbleSubMenuID.NORMAL_PEN_STYLE:
                break;
            case ScribbleSubMenuID.BRUSH_PEN_STYLE:
                setErasePartialMode();
                break;
            case ScribbleSubMenuID.LINE_STYLE:
                setEraseAllMode();
                break;
            case ScribbleSubMenuID.TRIANGLE_STYLE:
                Toast.makeText(mContext, "No Such Function now.", Toast.LENGTH_SHORT).show();
                break;
            case ScribbleSubMenuID.CIRCLE_STYLE:
                Toast.makeText(mContext, "No Such Function now.", Toast.LENGTH_SHORT).show();
                break;
            case ScribbleSubMenuID.RECT_STYLE:
                break;
            case ScribbleSubMenuID.ERASE_PARTIALLY:
                setScribbleMode();
                break;
            case ScribbleSubMenuID.ERASE_TOTALLY:
                setScribbleMode();
                break;
            case ScribbleSubMenuID.BG_EMPTY:
                setScribbleMode();
                break;
            case ScribbleSubMenuID.BG_GRID:
                setScribbleMode();
                break;
            case ScribbleSubMenuID.BG_LINE:
                setScribbleMode();
                break;
            default:
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


    public ScribbleSubMenu(Context context, RelativeLayout parentLayout, MenuCallback callback, int positionID, boolean isShowStatusBar) {
        super(context);
        final LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.scribble_submenu, this, true);
        mMenuContentView = (ContentView) findViewById(R.id.layout_sub_menu);
        initContentView();
        mPositionID = positionID;
        mMenuCallback = callback;
        mContext = context;
        mMenuContentView.setCallback(new ContentView.ContentViewCallback() {
            @Override
            public void onItemClick(ContentItemView view) {
                super.onItemClick(view);
                notifyOnMenuItemClickedListener(view.getData());
            }
        });
        createAdapter();
        parentLayout.addView(this, setMenuPosition(isShowStatusBar));
        this.setVisibility(View.GONE);
    }

    public
    @ScribbleMenuCategory.ScribbleMenuCategoryDef
    int getCurrentCategory() {
        return currentCategory;
    }

    public void show(final @ScribbleMenuCategory.ScribbleMenuCategoryDef
                             int mode) {
        currentCategory = mode;
        switch (mode) {
            case ScribbleMenuCategory.PEN_WIDTH:
                mMenuContentView.setAdapter(mThicknessAdapter, 0);
                break;
            case ScribbleMenuCategory.PEN_STYLE:
                mMenuContentView.setAdapter(mColorAdapter, 0);
                break;
            case ScribbleMenuCategory.ERASER:
                mMenuContentView.setAdapter(mEraseAdapter, 0);
                break;
            case ScribbleMenuCategory.BG:
                mMenuContentView.setAdapter(mPenStyleAdapter, 0);
                break;

        }
        updateSubMenuIndicatorByCurrentStatus(mode);
        setFocusable(true);
        setVisibility(View.VISIBLE);
    }

    public void hide() {
        if (mMenuCallback != null) {
            mMenuCallback.dismiss();
        }
        setVisibility(GONE);
    }

    private void updateButtonIndicator(GObject menuItem) {
        int dataIndex = mMenuContentView.getCurrentAdapter().getGObjectIndex(menuItem);
        setSelected(menuItem, true);
        mMenuContentView.getCurrentAdapter().setObject(dataIndex, menuItem);
        mMenuContentView.unCheckOtherViews(dataIndex);
        mMenuContentView.updateCurrentPage();
    }

    private void updateSubMenuIndicatorByCurrentStatus(final @ScribbleMenuCategory.ScribbleMenuCategoryDef int mode) {
        GObject object;
        int dataIndex;
        Object targetPattern = null;
        switch (mode) {
            case ScribbleMenuCategory.PEN_WIDTH:
                break;
            case ScribbleMenuCategory.PEN_STYLE:
                break;
            case ScribbleMenuCategory.ERASER:
                break;
            case ScribbleMenuCategory.BG:
                break;
            default:
                return;
        }
        object = mMenuContentView.getCurrentAdapter().searchFirstByTag(GAdapterUtil.TAG_UNIQUE_ID,
                targetPattern);
        setSelected(object, true);
        dataIndex = mMenuContentView.getCurrentAdapter().getGObjectIndex(object);
        mMenuContentView.getCurrentAdapter().setObject(dataIndex, object);
        mMenuContentView.unCheckOtherViews(dataIndex);
        mMenuContentView.updateCurrentPage();
    }

    private void createAdapter() {
        mThicknessAdapter = createThicknessAdapter();
        mEraseAdapter = createEraseAdapter();
        mPenStyleAdapter = createPenStyleAdapter();
    }

    public boolean isShow() {
        return getVisibility() == VISIBLE;
    }

    private void setScribbleMode() {
        mMenuCallback.setMode(ScribbleMode.MODE_SCRIBBLE);
    }

    private void setErasePartialMode() {
        mMenuCallback.setMode(ScribbleMode.MODE_ERASE);
    }

    private void setEraseAllMode() {
        mMenuCallback.erasePage();
    }

    private static
    @ScribbleSubMenuID.ScribbleSubMenuIDDef
    int getMenuUniqueId(GObject menu) {
        return ScribbleSubMenuID.translate(menu.getInt(GAdapterUtil.TAG_UNIQUE_ID));
    }

    private static GObject createImageButtonMenu(int imageResource, @ScribbleSubMenuID.ScribbleSubMenuIDDef int action, boolean closeAfterClick) {
        GObject item = GAdapterUtil.createTableItem(0, 0, imageResource, 0, null);
        item.putInt(GAdapterUtil.TAG_UNIQUE_ID, action);
        item.putBoolean(GAdapterUtil.TAG_MENU_CLOSE_AFTER_CLICK, closeAfterClick);
        return item;
    }

    private GAdapter createThicknessAdapter() {
        GAdapter thickness_menus = new GAdapter();
        thickness_menus.addObject(createImageButtonMenu(R.drawable.ic_width_1, ScribbleSubMenuID.THICKNESS_ULTRA_LIGHT, true));
        thickness_menus.addObject(createImageButtonMenu(R.drawable.ic_width_2, ScribbleSubMenuID.THICKNESS_LIGHT, true));
        thickness_menus.addObject(createImageButtonMenu(R.drawable.ic_width_3, ScribbleSubMenuID.THICKNESS_NORMAL, true));
        thickness_menus.addObject(createImageButtonMenu(R.drawable.ic_width_4, ScribbleSubMenuID.THICKNESS_BOLD, true));
        thickness_menus.addObject(createImageButtonMenu(R.drawable.ic_width_5, ScribbleSubMenuID.THICKNESS_ULTRA_BOLD, true));
        return thickness_menus;
    }

    private GAdapter createEraseAdapter() {
        GAdapter erase_menus = new GAdapter();
        erase_menus.addObject(createImageButtonMenu(R.drawable.ic_eraser_part, ScribbleSubMenuID.ERASE_PARTIALLY, true));
        erase_menus.addObject(createImageButtonMenu(R.drawable.ic_eraser_all, ScribbleSubMenuID.ERASE_TOTALLY, true));
        return erase_menus;
    }

    private GAdapter createPenStyleAdapter() {
        GAdapter styleMenus = new GAdapter();
        styleMenus.addObject(createImageButtonMenu(R.drawable.ic_shape_pencil, ScribbleSubMenuID.NORMAL_PEN_STYLE, true));
        styleMenus.addObject(createImageButtonMenu(R.drawable.ic_shape_brush, ScribbleSubMenuID.BRUSH_PEN_STYLE, true));
        styleMenus.addObject(createImageButtonMenu(R.drawable.ic_shape_line, ScribbleSubMenuID.LINE_STYLE, true));
        styleMenus.addObject(createImageButtonMenu(R.drawable.ic_shape_triangle, ScribbleSubMenuID.TRIANGLE_STYLE, true));
        styleMenus.addObject(createImageButtonMenu(R.drawable.ic_shape_circle, ScribbleSubMenuID.CIRCLE_STYLE, true));
        styleMenus.addObject(createImageButtonMenu(R.drawable.ic_shape_square, ScribbleSubMenuID.RECT_STYLE, true));
        return styleMenus;
    }

    private GAdapter createBGAdapter() {
        GAdapter bgMenus = new GAdapter();
        bgMenus.addObject(createImageButtonMenu(R.drawable.ic_template_white, ScribbleSubMenuID.BG_EMPTY, true));
        bgMenus.addObject(createImageButtonMenu(R.drawable.ic_template_matts, ScribbleSubMenuID.BG_GRID, true));
        bgMenus.addObject(createImageButtonMenu(R.drawable.ic_template_four, ScribbleSubMenuID.BG_LINE, true));
        return bgMenus;
    }

    public void rePositionAfterNewConfiguration(boolean isShowStatusBar) {
        setLayoutParams(setMenuPosition(isShowStatusBar));
    }

    private void initContentView() {
        mMenuContentView.setupGridLayout((getResources().getInteger(R.integer.note_menu_rows)),
                (getResources().getInteger(R.integer.note_menu_columns)));
        mMenuContentView.setSubLayoutParameter(R.layout.pen_style_item, getMapping());
        mMenuContentView.setShowPageInfoArea(false);
        mMenuContentView.setBlankAreaAnswerLongClick(false);
    }

    private static HashMap<String, Integer> getMapping() {
        if (mapping == null) {
            mapping = new HashMap<>();
            mapping.put(GAdapterUtil.TAG_IMAGE_RESOURCE, R.id.pen_img);
            mapping.put(GAdapterUtil.TAG_SELECTABLE, R.id.pen_indicator);
        }
        return mapping;
    }

    private RelativeLayout.LayoutParams setMenuPosition(boolean isShowStatusBar) {
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        if (isShowStatusBar) {
            p.addRule(RelativeLayout.ABOVE, mPositionID);
        } else {
            p.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        }
        p.addRule(RelativeLayout.CENTER_HORIZONTAL);
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
