package com.onyx.edu.note.ui.view;

import android.content.Context;
import android.widget.RelativeLayout;

import com.onyx.edu.note.data.ScribbleSubMenuID;


/**
 * Created by solskjaer49 on 16/8/4 15:05.
 */
public class ScribbleSubMenu extends RelativeLayout {
    public ScribbleSubMenu(Context context) {
        super(context);
    }

    /**
     * onItemSelected(int item):let operator(current is Activity) to handle different function.
     *
     * onCancel():let operator know the no operate but user dismiss the menu.
     *
     * onLayoutStateChanged():use in custom pos.
     */
    public static abstract class MenuCallback {
        public abstract void onItemSelect(@ScribbleSubMenuID.ScribbleSubMenuIDDef int item);

        public abstract void onLayoutStateChanged();

        public abstract void onCancel();
    }

//    private final MenuCallback mMenuCallback;
//    GAdapter mThicknessAdapter, mBGAdapter, mLineLayoutBGAdapter,mEraseAdapter, mPenStyleAdapter, mColoAdapter = new GAdapter();
//    private ContentView mMenuContentView;
//    static HashMap<String, Integer> mapping = null;
//    private int mPositionID;
//    private ShapeDataInfo curShapeDataInfo;
//    private NoteAppConfig config;
//    private
//    @ScribbleMainMenuID.ScribbleMainMenuDef
//    int currentCategory;
//
//    private void notifyOnMenuItemClickedListener(GObject scribbleMenu) {
//        updateButtonIndicator(scribbleMenu);
//        mMenuCallback.onItemSelect(getMenuUniqueId(scribbleMenu));
//        boolean close = getMenuCloseAfterClick(scribbleMenu);
//        if (close) {
//            this.dismiss(false);
//        }
//    }
//
//    public void setCurShapeDataInfo(ShapeDataInfo curShapeDataInfo) {
//        this.curShapeDataInfo = curShapeDataInfo;
//    }
//
//    /**
//     * default return false
//     *
//     * @param menu
//     * @return
//     */
//    private static boolean getMenuCloseAfterClick(GObject menu) {
//        return menu.getBoolean(GAdapterUtil.TAG_MENU_CLOSE_AFTER_CLICK);
//    }
//
//    private static void saveMenuCloseAfterClick(GObject menu, boolean close) {
//        menu.putBoolean(GAdapterUtil.TAG_MENU_CLOSE_AFTER_CLICK, close);
//    }
//
//    public ScribbleSubMenu(Context context, ShapeDataInfo shapeDataInfo, RelativeLayout parentLayout, MenuCallback callback, int positionID, boolean isShowStatusBar) {
//        super(context);
//        final LayoutInflater inflater = LayoutInflater.from(context);
//        inflater.inflate(R.layout.scribble_submenu, this, true);
//        setBackgroundColor(Color.TRANSPARENT);
//        mMenuContentView = (ContentView) findViewById(R.id.layout_sub_menu);
//        curShapeDataInfo = shapeDataInfo;
//        config = NoteAppConfig.sharedInstance(context);
//        View dismissZone = findViewById(R.id.dismiss_zone);
//        dismissZone.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dismiss(true);
//            }
//        });
//        mPositionID = positionID;
//        mMenuCallback = callback;
//        mMenuContentView.setCallback(new ContentView.ContentViewCallback() {
//            @Override
//            public void onItemClick(ContentItemView view) {
//                super.onItemClick(view);
//                notifyOnMenuItemClickedListener(view.getData());
//            }
//        });
//        createAdapter();
//        parentLayout.addView(this, setMenuPosition(isShowStatusBar));
//        this.setVisibility(View.GONE);
//    }
//
//    public
//    @ScribbleMainMenuID.ScribbleMainMenuDef
//    int getCurrentCategory() {
//        return currentCategory;
//    }
//
//    public void show(final @ScribbleMainMenuID.ScribbleMainMenuDef
//                             int category, final boolean isLineLayoutMode) {
//        currentCategory = category;
//        requestContentViewLayout(category, isLineLayoutMode, new BaseCallback() {
//            @Override
//            public void done(BaseRequest request, Throwable e) {
//                updateContentView(category, isLineLayoutMode);
//            }
//        });
//    }
//
//    private void updateContentView(final @ScribbleMainMenuID.ScribbleMainMenuDef
//                                           int category, final boolean isLineLayoutMode) {
//        switch (category) {
//            case ScribbleMainMenuID.PEN_WIDTH:
//                mMenuContentView.setAdapter(mThicknessAdapter, 0);
//                break;
//            case ScribbleMainMenuID.PEN_STYLE:
//                mMenuContentView.setAdapter(mPenStyleAdapter, 0);
//                break;
//            case ScribbleMainMenuID.ERASER:
//                mMenuContentView.setAdapter(mEraseAdapter, 0);
//                break;
//            case ScribbleMainMenuID.BG:
//                mMenuContentView.setAdapter(isLineLayoutMode ? mLineLayoutBGAdapter : mBGAdapter, 0);
//                break;
//            case ScribbleMainMenuID.COLOR:
//                mMenuContentView.setAdapter(mColoAdapter,0);
//                break;
//        }
//        updateSubMenuIndicatorByCurrentStatus(category, isLineLayoutMode);
//        setFocusable(true);
//        setVisibility(View.VISIBLE);
//    }
//
//    public void dismiss() {
//        dismiss(true);
//    }
//
//    /**
//     * @param isCancel if no submenu item was previous selected -> true,otherwise false.
//     */
//    private void dismiss(boolean isCancel) {
//        if (mMenuCallback != null && isCancel) {
//            mMenuCallback.onCancel();
//        }
//        setVisibility(GONE);
//    }
//
//    private void updateButtonIndicator(GObject menuItem) {
//        int dataIndex = mMenuContentView.getCurrentAdapter().getGObjectIndex(menuItem);
//        setSelected(menuItem, true);
//        mMenuContentView.getCurrentAdapter().setObject(dataIndex, menuItem);
//        mMenuContentView.unCheckOtherViews(dataIndex);
//        mMenuContentView.updateCurrentPage();
//    }
//
//    private void updateSubMenuIndicatorByCurrentStatus(final @ScribbleMainMenuID.ScribbleMainMenuDef int category, boolean isLineLayoutMode) {
//        GObject object = null;
//        int dataIndex = -1;
//        Object targetPattern = null;
//        switch (category) {
//            case ScribbleMainMenuID.PEN_WIDTH:
//                targetPattern = ScribbleSubMenuID.menuIdFromStrokeWidth(curShapeDataInfo.getStrokeWidth());
//                break;
//            case ScribbleMainMenuID.PEN_STYLE:
//                switch (curShapeDataInfo.getCurrentShapeType()) {
//                    case SHAPE_PENCIL_SCRIBBLE:
//                        targetPattern = ScribbleSubMenuID.NORMAL_PEN_STYLE;
//                        break;
//                    case SHAPE_BRUSH_SCRIBBLE:
//                        targetPattern = ScribbleSubMenuID.BRUSH_PEN_STYLE;
//                        break;
//                    case SHAPE_LINE:
//                        targetPattern = ScribbleSubMenuID.LINE_STYLE;
//                        break;
//                    case SHAPE_TRIANGLE:
//                        targetPattern = ScribbleSubMenuID.TRIANGLE_STYLE;
//                        break;
//                    case SHAPE_CIRCLE:
//                        targetPattern = ScribbleSubMenuID.CIRCLE_STYLE;
//                        break;
//                    case SHAPE_RECTANGLE:
//                        targetPattern = ScribbleSubMenuID.RECT_STYLE;
//                        break;
//                    case SHAPE_TRIANGLE_45:
//                        targetPattern = ScribbleSubMenuID.TRIANGLE_45_STYLE;
//                        break;
//                    case SHAPE_TRIANGLE_60:
//                        targetPattern = ScribbleSubMenuID.TRIANGLE_60_STYLE;
//                        break;
//                    case SHAPE_TRIANGLE_90:
//                        targetPattern = ScribbleSubMenuID.TRIANGLE_90_STYLE;
//                        break;
//                    default:
//                        targetPattern = "";
//                        break;
//                }
//                break;
//            case ScribbleMainMenuID.ERASER:
//                if (curShapeDataInfo.isInUserErasing()) {
//                    targetPattern = ScribbleSubMenuID.ERASE_PARTIALLY;
//                } else {
//                    targetPattern = "";
//                }
//                break;
//            case ScribbleMainMenuID.BG:
//                int bg = isLineLayoutMode ? curShapeDataInfo.getLineLayoutBackground() : curShapeDataInfo.getBackground();
//                switch (bg) {
//                    case NoteBackgroundType.EMPTY:
//                        targetPattern = ScribbleSubMenuID.BG_EMPTY;
//                        break;
//                    case NoteBackgroundType.LINE:
//                        targetPattern = ScribbleSubMenuID.BG_LINE;
//                        break;
//                    case NoteBackgroundType.GRID:
//                        targetPattern = ScribbleSubMenuID.BG_GRID;
//                        break;
//                    case NoteBackgroundType.ENGLISH:
//                        targetPattern = ScribbleSubMenuID.BG_ENGLISH;
//                        break;
//                    case NoteBackgroundType.MUSIC:
//                        targetPattern = ScribbleSubMenuID.BG_MUSIC;
//                        break;
//                    case NoteBackgroundType.MATS:
//                        targetPattern = ScribbleSubMenuID.BG_MATS;
//                        break;
//                    case NoteBackgroundType.TABLE:
//                        targetPattern = ScribbleSubMenuID.BG_TABLE_GRID;
//                        break;
//                    case NoteBackgroundType.COLUMN:
//                        targetPattern = ScribbleSubMenuID.BG_LINE_COLUMN;
//                        break;
//                    case NoteBackgroundType.LEFT_GRID:
//                        targetPattern = ScribbleSubMenuID.BG_LEFT_GRID;
//                        break;
//                    case NoteBackgroundType.GRID_5_5:
//                        targetPattern = ScribbleSubMenuID.BG_GRID_5_5;
//                        break;
//                    case NoteBackgroundType.GRID_POINT:
//                        targetPattern = ScribbleSubMenuID.BG_GRID_POINT;
//                        break;
//                    case NoteBackgroundType.LINE_1_6:
//                        targetPattern = ScribbleSubMenuID.BG_LINE_1_6;
//                        break;
//                    case NoteBackgroundType.LINE_2_0:
//                        targetPattern = ScribbleSubMenuID.BG_LINE_2_0;
//                        break;
//                    case NoteBackgroundType.CALENDAR:
//                        targetPattern = ScribbleSubMenuID.BG_CALENDAR;
//                        break;
//                }
//                break;
//            case ScribbleMainMenuID.COLOR:
//                switch (curShapeDataInfo.getStrokeColor()) {
//                    case Color.BLACK:
//                        targetPattern = ScribbleSubMenuID.PEN_COLOR_BLACK;
//                        break;
//                    case Color.YELLOW:
//                        targetPattern = ScribbleSubMenuID.PEN_COLOR_YELLOW;
//                        break;
//                    case Color.RED:
//                        targetPattern = ScribbleSubMenuID.PEN_COLOR_RED;
//                        break;
//                    case Color.BLUE:
//                        targetPattern = ScribbleSubMenuID.PEN_COLOR_BLUE;
//                        break;
//                    case Color.GREEN:
//                        targetPattern = ScribbleSubMenuID.PEN_COLOR_GREEN;
//                        break;
//                    case Color.MAGENTA:
//                        targetPattern = ScribbleSubMenuID.PEN_COLOR_MAGENTA;
//                        break;
//                }
//                break;
//            default:
//                return;
//        }
//        object = mMenuContentView.getCurrentAdapter().searchFirstByTag(GAdapterUtil.TAG_UNIQUE_ID,
//                targetPattern);
//        if (object != null) {
//            setSelected(object, true);
//            dataIndex = mMenuContentView.getCurrentAdapter().getGObjectIndex(object);
//            mMenuContentView.getCurrentAdapter().setObject(dataIndex, object);
//            mMenuContentView.unCheckOtherViews(dataIndex);
//        } else {
//            mMenuContentView.unCheckAllViews();
//        }
//        mMenuContentView.updateCurrentPage();
//    }
//
//    private void createAdapter() {
//        mThicknessAdapter = createThicknessAdapter();
//        mEraseAdapter = createEraseAdapter();
//        mPenStyleAdapter = createPenStyleAdapter();
//        mBGAdapter = createBGAdapter();
//        mLineLayoutBGAdapter = createLineLayoutBGAdapter();
//        mColoAdapter = createPenColorAdapter();
//    }
//
//    public boolean isShow() {
//        return getVisibility() == VISIBLE;
//    }
//
//    private static
//    @ScribbleSubMenuID.ScribbleSubMenuIDDef
//    int getMenuUniqueId(GObject menu) {
//        return ScribbleSubMenuID.translate(menu.getInt(GAdapterUtil.TAG_UNIQUE_ID));
//    }
//
//    private static GObject createImageButtonMenu(int imageResource, @ScribbleSubMenuID.ScribbleSubMenuIDDef int action, boolean closeAfterClick) {
//        GObject item = GAdapterUtil.createTableItem(0, 0, imageResource, 0, null);
//        item.putInt(GAdapterUtil.TAG_UNIQUE_ID, action);
//        item.putBoolean(GAdapterUtil.TAG_SELECTABLE, false);
//        item.putBoolean(GAdapterUtil.TAG_MENU_CLOSE_AFTER_CLICK, closeAfterClick);
//        return item;
//    }
//
//    private GAdapter createThicknessAdapter() {
//        GAdapter thickness_menus = new GAdapter();
//        thickness_menus.addObject(createImageButtonMenu(R.drawable.ic_width_1, ScribbleSubMenuID.THICKNESS_ULTRA_LIGHT, true));
//        thickness_menus.addObject(createImageButtonMenu(R.drawable.ic_width_2, ScribbleSubMenuID.THICKNESS_LIGHT, true));
//        thickness_menus.addObject(createImageButtonMenu(R.drawable.ic_width_3, ScribbleSubMenuID.THICKNESS_NORMAL, true));
//        thickness_menus.addObject(createImageButtonMenu(R.drawable.ic_width_4, ScribbleSubMenuID.THICKNESS_BOLD, true));
//        thickness_menus.addObject(createImageButtonMenu(R.drawable.ic_width_5, ScribbleSubMenuID.THICKNESS_ULTRA_BOLD, true));
//        thickness_menus.addObject(createImageButtonMenu(R.drawable.ic_width_6, ScribbleSubMenuID.THICKNESS_CUSTOM_BOLD, true));
//        return thickness_menus;
//    }
//
//    private GAdapter createEraseAdapter() {
//        GAdapter erase_menus = new GAdapter();
//        erase_menus.addObject(createImageButtonMenu(R.drawable.ic_eraser_part, ScribbleSubMenuID.ERASE_PARTIALLY, true));
//        erase_menus.addObject(createImageButtonMenu(R.drawable.ic_eraser_all, ScribbleSubMenuID.ERASE_TOTALLY, true));
//        return erase_menus;
//    }
//
//    private GAdapter createPenStyleAdapter() {
//        GAdapter styleMenus = new GAdapter();
//        styleMenus.addObject(createImageButtonMenu(R.drawable.ic_shape_pencil, ScribbleSubMenuID.NORMAL_PEN_STYLE, true));
//        if (NoteAppConfig.sharedInstance(getContext()).isEnablePressStressDetect()) {
//            styleMenus.addObject(createImageButtonMenu(R.drawable.ic_shape_brush, ScribbleSubMenuID.BRUSH_PEN_STYLE, true));
//        }
//        styleMenus.addObject(createImageButtonMenu(R.drawable.ic_shape_line, ScribbleSubMenuID.LINE_STYLE, true));
//        styleMenus.addObject(createImageButtonMenu(R.drawable.ic_shape_triangle, ScribbleSubMenuID.TRIANGLE_STYLE, true));
//        if (config.useEduConfig()) {
//            styleMenus.addObject(createImageButtonMenu(R.drawable.ic_shape_triangle_45, ScribbleSubMenuID.TRIANGLE_45_STYLE, true));
//            styleMenus.addObject(createImageButtonMenu(R.drawable.ic_shape_triangle_60, ScribbleSubMenuID.TRIANGLE_60_STYLE, true));
//            styleMenus.addObject(createImageButtonMenu(R.drawable.ic_shape_triangle_90, ScribbleSubMenuID.TRIANGLE_90_STYLE, true));
//        }
//        styleMenus.addObject(createImageButtonMenu(R.drawable.ic_shape_circle, ScribbleSubMenuID.CIRCLE_STYLE, true));
//        styleMenus.addObject(createImageButtonMenu(R.drawable.ic_shape_square, ScribbleSubMenuID.RECT_STYLE, true));
//        return styleMenus;
//    }
//
//    private GAdapter createBGAdapter() {
//        GAdapter bgMenus = new GAdapter();
//        if (config.useEduConfig()) {
//            bgMenus.addObject(createImageButtonMenu(R.drawable.ic_template_white, ScribbleSubMenuID.BG_EMPTY, true));
//            bgMenus.addObject(createImageButtonMenu(R.drawable.ic_template_line, ScribbleSubMenuID.BG_LINE, true));
//            bgMenus.addObject(createImageButtonMenu(R.drawable.ic_template_left_grid, ScribbleSubMenuID.BG_LEFT_GRID, true));
//            bgMenus.addObject(createImageButtonMenu(R.drawable.ic_template_grid_5_5, ScribbleSubMenuID.BG_GRID_5_5, true));
//            bgMenus.addObject(createImageButtonMenu(R.drawable.ic_template_grid, ScribbleSubMenuID.BG_GRID, true));
//            bgMenus.addObject(createImageButtonMenu(R.drawable.ic_template_mats, ScribbleSubMenuID.BG_MATS, true));
//            bgMenus.addObject(createImageButtonMenu(R.drawable.ic_template_music, ScribbleSubMenuID.BG_MUSIC, true));
//            bgMenus.addObject(createImageButtonMenu(R.drawable.ic_template_english, ScribbleSubMenuID.BG_ENGLISH, true));
//        } else {
//            bgMenus.addObject(createImageButtonMenu(R.drawable.ic_template_white, ScribbleSubMenuID.BG_EMPTY, true));
//            bgMenus.addObject(createImageButtonMenu(R.drawable.ic_template_line, ScribbleSubMenuID.BG_LINE, true));
//            bgMenus.addObject(createImageButtonMenu(R.drawable.ic_template_line_1_6, ScribbleSubMenuID.BG_LINE_1_6, true));
//            bgMenus.addObject(createImageButtonMenu(R.drawable.ic_template_line_2_0, ScribbleSubMenuID.BG_LINE_2_0, true));
//            bgMenus.addObject(createImageButtonMenu(R.drawable.ic_template_line_column, ScribbleSubMenuID.BG_LINE_COLUMN, true));
//            bgMenus.addObject(createImageButtonMenu(R.drawable.ic_template_left_grid, ScribbleSubMenuID.BG_LEFT_GRID, true));
//            bgMenus.addObject(createImageButtonMenu(R.drawable.ic_template_table_grid, ScribbleSubMenuID.BG_TABLE_GRID, true));
//            bgMenus.addObject(createImageButtonMenu(R.drawable.ic_template_line_calendar, ScribbleSubMenuID.BG_CALENDAR, true));
//            bgMenus.addObject(createImageButtonMenu(R.drawable.ic_template_grid_5_5, ScribbleSubMenuID.BG_GRID_5_5, true));
//            bgMenus.addObject(createImageButtonMenu(R.drawable.ic_template_grid, ScribbleSubMenuID.BG_GRID, true));
//            bgMenus.addObject(createImageButtonMenu(R.drawable.ic_template_mats, ScribbleSubMenuID.BG_MATS, true));
//            bgMenus.addObject(createImageButtonMenu(R.drawable.ic_template_grid_point, ScribbleSubMenuID.BG_GRID_POINT, true));
//            bgMenus.addObject(createImageButtonMenu(R.drawable.ic_template_music, ScribbleSubMenuID.BG_MUSIC, true));
//            bgMenus.addObject(createImageButtonMenu(R.drawable.ic_template_english, ScribbleSubMenuID.BG_ENGLISH, true));
//        }
//        return bgMenus;
//    }
//
//    private GAdapter createLineLayoutBGAdapter() {
//        GAdapter bgMenus = new GAdapter();
//        bgMenus.addObject(createImageButtonMenu(R.drawable.ic_template_white, ScribbleSubMenuID.BG_EMPTY, true));
//        bgMenus.addObject(createImageButtonMenu(R.drawable.ic_template_line_layout_line, ScribbleSubMenuID.BG_LINE, true));
//        return bgMenus;
//    }
//
//    private GAdapter createPenColorAdapter() {
//        GAdapter penColorMenus = new GAdapter();
//        penColorMenus.addObject(createImageButtonMenu(R.drawable.ic_color_black, ScribbleSubMenuID.PEN_COLOR_BLACK, true));
//        penColorMenus.addObject(createImageButtonMenu(R.drawable.ic_color_red, ScribbleSubMenuID.PEN_COLOR_RED, true));
//        penColorMenus.addObject(createImageButtonMenu(R.drawable.ic_color_yellow, ScribbleSubMenuID.PEN_COLOR_YELLOW, true));
//        penColorMenus.addObject(createImageButtonMenu(R.drawable.ic_color_blue, ScribbleSubMenuID.PEN_COLOR_BLUE, true));
//        penColorMenus.addObject(createImageButtonMenu(R.drawable.ic_color_green, ScribbleSubMenuID.PEN_COLOR_GREEN, true));
//        penColorMenus.addObject(createImageButtonMenu(R.drawable.ic_color_magenta, ScribbleSubMenuID.PEN_COLOR_MAGENTA, true));
//        return penColorMenus;
//    }
//
//    public void rePositionAfterNewConfiguration(boolean isShowStatusBar) {
//        setLayoutParams(setMenuPosition(isShowStatusBar));
//    }
//
//    private void requestContentViewLayout(final @ScribbleMainMenuID.ScribbleMainMenuDef
//                                         int category, boolean isLineLayoutMode, final BaseCallback callback) {
//        int rows;
//        if ((category == ScribbleMainMenuID.BG && !isLineLayoutMode) ||
//                (category == ScribbleMainMenuID.PEN_STYLE && config.useEduConfig())) {
//            rows = 2;
//        } else {
//            rows = getResources().getInteger(R.integer.note_menu_rows);
//        }
//
//        mMenuContentView.setupGridLayout(rows,
//                (getResources().getInteger(R.integer.note_menu_columns)));
//
//        int height = (int) getContext().getResources().getDimension(R.dimen.onyx_sub_note_menu_height);
//        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height * rows);
//        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        mMenuContentView.setLayoutParams(layoutParams);
//
//        mMenuContentView.setSubLayoutParameter(R.layout.onyx_sub_function_item, getMapping());
//        mMenuContentView.setShowPageInfoArea(false);
//        mMenuContentView.setBlankAreaAnswerLongClick(false);
//
//        mMenuContentView.post(new Runnable() {
//            @Override
//            public void run() {
//                callback.done(null, null);
//            }
//        });
//    }
//
//    private static HashMap<String, Integer> getMapping() {
//        if (mapping == null) {
//            mapping = new HashMap<>();
//            mapping.put(GAdapterUtil.TAG_IMAGE_RESOURCE, R.id.item_img);
//            mapping.put(GAdapterUtil.TAG_SELECTABLE, R.id.item_indicator);
//        }
//        return mapping;
//    }
//
//    private LayoutParams setMenuPosition(boolean isShowStatusBar) {
//        LayoutParams p = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT);
//        if (isShowStatusBar) {
//            p.addRule(RelativeLayout.ABOVE, mPositionID);
//        } else {
//            p.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        }
//        p.addRule(RelativeLayout.BELOW, R.id.tool_bar);
//        p.addRule(RelativeLayout.CENTER_HORIZONTAL);
//        return p;
//    }
//
//
//    private static GObject getSelectedObject(GAdapter adapter) {
//        for (GObject o : adapter.getList()) {
//            if (isSelected(o)) {
//                return o;
//            }
//        }
//        return null;
//    }
//
//    private static void setSelected(GObject object, boolean value) {
//        object.putBoolean(GAdapterUtil.TAG_SELECTABLE, value);
//    }
//
//    private static boolean isSelected(GObject object) {
//        return object.getBoolean(GAdapterUtil.TAG_SELECTABLE);
//    }
}
