package com.onyx.edu.homework.ui;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.ShapeDataInfo;
import com.onyx.android.sdk.scribble.request.navigation.PageAddRequest;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.ui.data.MenuClickEvent;
import com.onyx.android.sdk.ui.data.MenuId;
import com.onyx.android.sdk.ui.data.MenuItem;
import com.onyx.android.sdk.ui.data.MenuManager;
import com.onyx.android.sdk.ui.dialog.DialogCustomLineWidth;
import com.onyx.android.sdk.ui.dialog.OnyxCustomDialog;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.edu.homework.BR;
import com.onyx.edu.homework.DataBundle;
import com.onyx.edu.homework.R;
import com.onyx.edu.homework.action.note.ClearAllFreeShapesAction;
import com.onyx.edu.homework.action.note.DocumentAddNewPageAction;
import com.onyx.edu.homework.action.note.DocumentDeletePageAction;
import com.onyx.edu.homework.action.note.DocumentFlushAction;
import com.onyx.edu.homework.action.note.DocumentSaveAction;
import com.onyx.edu.homework.action.note.GotoNextPageAction;
import com.onyx.edu.homework.action.note.GotoPrevPageAction;
import com.onyx.edu.homework.action.note.NoteBackgroundChangeAction;
import com.onyx.edu.homework.action.note.NoteStrokeWidthChangeAction;
import com.onyx.edu.homework.action.note.RedoAction;
import com.onyx.edu.homework.action.note.UndoAction;
import com.onyx.edu.homework.base.BaseFragment;
import com.onyx.edu.homework.data.Constant;
import com.onyx.edu.homework.databinding.FragmentNoteToolBinding;
import com.onyx.edu.homework.event.PageChangeEvent;
import com.onyx.edu.homework.event.SaveNoteEvent;
import com.onyx.edu.homework.event.UpdatePagePositionEvent;
import com.onyx.edu.homework.note.ScribbleSubMenuMap;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import static com.onyx.android.sdk.scribble.shape.ShapeFactory.SHAPE_ERASER;

/**
 * Created by lxm on 2017/12/7.
 */

public class NoteToolFragment extends BaseFragment {

    private RelativeLayout subMenuLayout;
    private int initPageCount = 1;

    private MenuManager menuManager;
    private FragmentNoteToolBinding binding;

    public static NoteToolFragment newInstance(RelativeLayout subMenuLayout, int initPageCount) {
        NoteToolFragment fragment = new NoteToolFragment();
        fragment.setSubMenuLayout(subMenuLayout);
        fragment.setInitPageCount(initPageCount);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_note_tool, container, false);
        DataBundle.getInstance().register(this);
        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DataBundle.getInstance().unregister(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initMenu();
    }

    @Subscribe
    public void onUpdatePagePositionEvent(UpdatePagePositionEvent event) {
        menuManager.getMainMenu().setText(MenuId.PAGE, event.position);
    }

    @Subscribe
    public void onMenuClickEvent(MenuClickEvent event) {
        switch (event.getMenuId()) {
            case MenuId.PEN_STYLE:
            case MenuId.PEN_WIDTH:
            case MenuId.ERASER:
            case MenuId.BG:
                prepareShowSubMenu(event.getMenuId());
                return;
            case MenuId.ADD_PAGE:
                addPage();
                break;
            case MenuId.DELETE_PAGE:
                deletePage();
                break;
            case MenuId.PREV_PAGE:
                prevPage();
                break;
            case MenuId.NEXT_PAGE:
                nextPage();
                break;
            case MenuId.UNDO:
                undo();
                break;
            case MenuId.REDO:
                redo();
                break;
            case MenuId.SAVE:
                saveDocument();
                break;
        }
        if (!MenuId.isSubMenuId(event.getMenuId())) {
            hideSubMenu();
            return;
        }
        if (getDataBundle().isDoing() && MenuId.isSubMenuId(event.getMenuId()) && handleSubMenuEvent(event.getMenuId())) {
            prepareHideSubMenu();
        }
    }

    private boolean handleSubMenuEvent(int subMenuID) {
        if (MenuId.isThicknessGroup(subMenuID)) {
            return onStrokeWidthChanged(subMenuID);
        } else if (MenuId.isBackgroundGroup(subMenuID)) {
            return onBackgroundChanged(subMenuID);
        } else if (MenuId.isEraserGroup(subMenuID)) {
            return onEraserChanged(subMenuID);
        } else if (MenuId.isPenStyleGroup(subMenuID)) {
            return onShapeChanged(subMenuID);
        } else if (MenuId.isPenColorGroup(subMenuID)) {

        }
        return true;
    }

    private void saveDocument() {
        getDataBundle().post(new SaveNoteEvent(false));
    }

    private boolean onBackgroundChanged(int subMenuID) {
        int bgType = ScribbleSubMenuMap.bgFromMenuID(subMenuID);
        getShapeDataInfo().setBackground(bgType);
        flushDocument(true, shouldResume(), null);
        return true;
    }

    private boolean onShapeChanged(int subMenuID) {
        int shapeType = ScribbleSubMenuMap.shapeTypeFromMenuID(subMenuID);
        getShapeDataInfo().setCurrentShapeType(shapeType);
        flushDocument(true, shouldResume(), null);
        return true;
    }

    private boolean onEraserChanged(int subMenuID) {
        switch (subMenuID) {
            case MenuId.ERASE_PARTIALLY:
                getShapeDataInfo().setCurrentShapeType(SHAPE_ERASER);
                flushDocument(true, false, null);
                break;
            case MenuId.ERASE_TOTALLY:
                new ClearAllFreeShapesAction(shouldResume()).execute(getNoteViewHelper(), null);
                break;
        }
        return true;
    }

    private boolean onStrokeWidthChanged(int subMenuID) {
        if (subMenuID == MenuId.THICKNESS_CUSTOM_BOLD) {
            flushDocument(true, false, new BaseCallback() {
                @Override
                public void done(BaseRequest request, Throwable e) {
                    showCustomLineWidthDialog();
                }
            });
            return false;
        }else {
            updateStrokeWidth(ScribbleSubMenuMap.strokeWidthFromMenuId(subMenuID), null);
            return true;
        }
    }

    private void updateStrokeWidth(float width, BaseCallback callback) {
        getShapeDataInfo().setStrokeWidth(width);
        flushDocument(true, shouldResume(), callback);
    }

    private void showCustomLineWidthDialog() {
        DialogCustomLineWidth customLineWidth = new DialogCustomLineWidth(getActivity(),
                (int) getShapeDataInfo().getStrokeWidth(),
                20, Color.BLACK, new DialogCustomLineWidth.Callback() {
            @Override
            public void done(int lineWidth) {
                updateStrokeWidth(lineWidth, null);
            }
        });
        customLineWidth.show();
        customLineWidth.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                flushDocument(true, false, null);
            }
        });
    }

    private void addPage() {
        if (!getDataBundle().isDoing()) {
            return;
        }
        flushDocument(false, shouldResume(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                new DocumentAddNewPageAction(-1, Constant.MAX_SUB_NOTE_PAGE_COUNT).execute(getNoteViewHelper(), new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        PageAddRequest addRequest = (PageAddRequest) request;
                        if (addRequest.isOutOfMaxPageCount()) {
                            Toast.makeText(getActivity(), R.string.out_of_page_count, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void deletePage() {
        if (!getDataBundle().isDoing()) {
            return;
        }
        flushDocument(false, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                deletePageImpl();
            }
        });
    }

    private void deletePageImpl() {
        OnyxCustomDialog dialog = OnyxCustomDialog.getConfirmDialog(getActivity(), getString(R.string.ask_for_delete_page), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new DocumentDeletePageAction().execute(getNoteViewHelper(), null);
            }
        }, null);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                flushDocument(false, shouldResume(), null);
            }
        });
        dialog.show();
    }

    private void prevPage() {
        if (getDataBundle().isReview()) {
            getDataBundle().post(new PageChangeEvent(false));
            return;
        }
        flushDocument(false, shouldResume(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                new GotoPrevPageAction(shouldResume()).execute(getNoteViewHelper(), null);
            }
        });
    }

    private void nextPage() {
        if (getDataBundle().isReview()) {
            getDataBundle().post(new PageChangeEvent(true));
            return;
        }
        flushDocument(false, shouldResume(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                new GotoNextPageAction(shouldResume()).execute(getNoteViewHelper(), null);
            }
        });
    }

    private void undo() {
        flushDocument(false, shouldResume(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                new UndoAction(shouldResume()).execute(getNoteViewHelper(), null);
            }
        });
    }

    private void redo() {
        flushDocument(false, shouldResume(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                new RedoAction(shouldResume()).execute(getNoteViewHelper(), null);
            }
        });
    }

    private void initMenu() {
        menuManager = new MenuManager();
        menuManager.addMainMenu(binding.mainMenuLayout,
                DataBundle.getInstance().getEventBus(),
                R.layout.scribble_main_menu,
                BR.item,
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT),
                MenuItem.createVisibleMenus(buildMainMenuIds()));
        subMenuLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareHideSubMenu();
            }
        });
        subMenuLayout.setVisibility(View.GONE);
        menuManager.getMainMenu().setText(MenuId.PAGE, "1/" + initPageCount);
    }

    private void prepareHideSubMenu() {
        flushDocument(false, shouldResume(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                hideSubMenu();
            }
        });
    }

    private void hideSubMenu() {
        subMenuLayout.removeAllViews();
        subMenuLayout.setVisibility(View.GONE);
    }

    public List<Integer> buildMainMenuIds() {
        List<Integer> functionMenuIds = new ArrayList<>();
        if (getDataBundle().isDoing()) {
            functionMenuIds.add(MenuId.PEN_STYLE);
            functionMenuIds.add(MenuId.BG);
            functionMenuIds.add(MenuId.ERASER);
            functionMenuIds.add(MenuId.PEN_WIDTH);
            functionMenuIds.add(MenuId.SAVE);

            functionMenuIds.add(MenuId.ADD_PAGE);
            functionMenuIds.add(MenuId.DELETE_PAGE);
        }

        functionMenuIds.add(MenuId.PREV_PAGE);
        functionMenuIds.add(MenuId.NEXT_PAGE);
        functionMenuIds.add(MenuId.PAGE);

        return functionMenuIds;
    }

    private void prepareShowSubMenu(final int parentId) {
        if (!getDataBundle().isDoing()) {
            return;
        }
        flushDocument(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                showSubMenu(parentId);
            }
        });
    }

    private void showSubMenu(int parentId) {
        subMenuLayout.removeAllViews();
        subMenuLayout.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        menuManager.addSubMenu(subMenuLayout,
                DataBundle.getInstance().getEventBus(),
                getSubLayoutId(parentId),
                BR.item,
                lp,
                getSubItems(parentId));
        menuManager.getSubMenu().
                unCheckAll().
                check(getChosenSubMenuId(parentId));
    }

    public SparseArray<List<Integer>> buildSubMenuIds() {
        SparseArray<List<Integer>> functionBarSubMenuIDMap = new SparseArray<>();
        functionBarSubMenuIDMap.put(MenuId.PEN_WIDTH, buildSubMenuThicknessIDList());
        functionBarSubMenuIDMap.put(MenuId.BG, buildSubMenuBGIDList());
        functionBarSubMenuIDMap.put(MenuId.ERASER, buildSubMenuEraserIDList());
        functionBarSubMenuIDMap.put(MenuId.PEN_STYLE, buildSubMenuPenStyleIDList());
        return functionBarSubMenuIDMap;
    }

    private List<Integer> buildSubMenuThicknessIDList() {
        List<Integer> resultList = new ArrayList<>();
        resultList.add(MenuId.THICKNESS_ULTRA_LIGHT);
        resultList.add(MenuId.THICKNESS_LIGHT);
        resultList.add(MenuId.THICKNESS_NORMAL);
        resultList.add(MenuId.THICKNESS_BOLD);
        resultList.add(MenuId.THICKNESS_ULTRA_BOLD);
        resultList.add(MenuId.THICKNESS_CUSTOM_BOLD);
        return resultList;
    }

    private  List<Integer> buildSubMenuBGIDList() {
        List<Integer> resultList = new ArrayList<>();
        resultList.add(MenuId.BG_EMPTY);
        resultList.add(MenuId.BG_LINE);
        resultList.add(MenuId.BG_LEFT_GRID);
        resultList.add(MenuId.BG_GRID_5_5);
        resultList.add(MenuId.BG_GRID);
        resultList.add(MenuId.BG_MATS);
        resultList.add(MenuId.BG_MUSIC);
        resultList.add(MenuId.BG_ENGLISH);
        return resultList;
    }

    private List<Integer> buildSubMenuEraserIDList() {
        List<Integer> resultList = new ArrayList<>();
        resultList.add(MenuId.ERASE_PARTIALLY);
        resultList.add(MenuId.ERASE_TOTALLY);
        return resultList;
    }

    private List<Integer> buildSubMenuPenStyleIDList() {
        List<Integer> resultList = new ArrayList<>();
        resultList.add(MenuId.NORMAL_PEN_STYLE);
        resultList.add(MenuId.LINE_STYLE);
        resultList.add(MenuId.TRIANGLE_STYLE);
        resultList.add(MenuId.CIRCLE_STYLE);
        resultList.add(MenuId.RECT_STYLE);
        resultList.add(MenuId.TRIANGLE_45_STYLE);
        resultList.add(MenuId.TRIANGLE_60_STYLE);
        resultList.add(MenuId.TRIANGLE_90_STYLE);
        return resultList;
    }


    private int getSubLayoutId(int parentId) {
        switch (parentId) {
            case MenuId.PEN_STYLE:
                return R.layout.pen_style_menu;
            case MenuId.BG:
                return R.layout.scribble_bg_menu;
            case MenuId.PEN_WIDTH:
                return R.layout.pen_width_menu;
            case MenuId.ERASER:
                return R.layout.scribble_erase_menu;
        }
        return R.layout.pen_style_menu;
    }

    private SparseArray<MenuItem> getSubItems(int parentId) {
        List<Integer> subMenuIds = buildSubMenuIds().get(parentId);
        return MenuItem.createVisibleMenus(subMenuIds, 7);
    }

    public int getChosenSubMenuId(int mainMenuID) {
        int targetID = Integer.MIN_VALUE;
        switch (mainMenuID) {
            case MenuId.ERASER:
            case MenuId.PEN_STYLE:
                targetID = ScribbleSubMenuMap.menuIdFromShapeType(getShapeDataInfo().getCurrentShapeType());
                break;
            case MenuId.BG:
                targetID = ScribbleSubMenuMap.menuIdFromBg(getShapeDataInfo().getBackground());
                break;
            case MenuId.PEN_WIDTH:
                targetID = ScribbleSubMenuMap.menuIdFromStrokeWidth(getShapeDataInfo().getStrokeWidth());
                break;
        }
        return targetID;
    }

    public ShapeDataInfo getShapeDataInfo() {
        return getNoteViewHelper().getShapeDataInfo();
    }

    public void setSubMenuLayout(RelativeLayout subMenuLayout) {
        this.subMenuLayout = subMenuLayout;
    }

    public void setInitPageCount(int initPageCount) {
        this.initPageCount = initPageCount;
    }

    public NoteViewHelper getNoteViewHelper() {
        return DataBundle.getInstance().getNoteViewHelper();
    }

    public void flushDocument(boolean render,
                              boolean resume,
                              final BaseCallback callback) {
        final List<Shape> stash = getNoteViewHelper().detachStash();
        final DocumentFlushAction action = new DocumentFlushAction(stash,
                render,
                resume,
                getShapeDataInfo().getDrawingArgs());
        action.execute(getNoteViewHelper(), callback);
    }

    public DataBundle getDataBundle() {
        return DataBundle.getInstance();
    }

    public boolean shouldResume() {
        return !getNoteViewHelper().inUserErasing()
                && ShapeFactory.isDFBShape(getShapeDataInfo().getCurrentShapeType())
                && getDataBundle().isDoing();
    }
}
