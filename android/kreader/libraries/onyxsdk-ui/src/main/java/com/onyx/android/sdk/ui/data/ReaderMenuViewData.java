package com.onyx.android.sdk.ui.data;

import android.view.ViewGroup;

import com.onyx.android.sdk.data.ReaderMenuAction;

import java.util.Set;

import static com.onyx.android.sdk.data.ReaderMenuAction.SCRIBBLE_PENCIL;
import static com.onyx.android.sdk.data.ReaderMenuAction.SCRIBBLE_WIDTH1;

/**
 * Created by ming on 2017/7/27.
 */

public class ReaderMenuViewData {

    private ReaderMenuAction selectWidthAction = SCRIBBLE_WIDTH1;
    private ReaderMenuAction selectShapeAction = SCRIBBLE_PENCIL;
    private ReaderMenuAction selectEraserAction = null;
    private Set<ReaderMenuAction> disableMenuActions;
    private boolean showScribbleMenu = false;
    private ViewGroup parent;

    public ReaderMenuViewData(ReaderMenuAction selectShapeAction, Set<ReaderMenuAction> disableMenuActions) {
        this.selectShapeAction = selectShapeAction;
        this.disableMenuActions = disableMenuActions;
    }

    public ReaderMenuViewData(Set<ReaderMenuAction> disableMenuActions, ViewGroup parent) {
        this.disableMenuActions = disableMenuActions;
        this.parent = parent;
    }

    public ReaderMenuViewData(Set<ReaderMenuAction> disableMenuActions, boolean showScribbleMenu, ViewGroup parent) {
        this.disableMenuActions = disableMenuActions;
        this.showScribbleMenu = showScribbleMenu;
        this.parent = parent;
    }

    public ReaderMenuViewData(Set<ReaderMenuAction> disableMenuActions) {
        this.disableMenuActions = disableMenuActions;
    }

    public ReaderMenuAction getSelectWidthAction() {
        return selectWidthAction;
    }

    public void setSelectWidthAction(ReaderMenuAction selectWidthAction) {
        this.selectWidthAction = selectWidthAction;
    }

    public ReaderMenuAction getSelectShapeAction() {
        return selectShapeAction;
    }

    public void setSelectShapeAction(ReaderMenuAction selectShapeAction) {
        this.selectShapeAction = selectShapeAction;
    }

    public ReaderMenuAction getSelectEraserAction() {
        return selectEraserAction;
    }

    public void setSelectEraserAction(ReaderMenuAction selectEraserAction) {
        this.selectEraserAction = selectEraserAction;
    }

    public Set<ReaderMenuAction> getDisableMenuActions() {
        return disableMenuActions;
    }

    public void setDisableMenuActions(Set<ReaderMenuAction> disableMenuActions) {
        this.disableMenuActions = disableMenuActions;
    }

    public ViewGroup getParent() {
        return parent;
    }

    public boolean isShowScribbleMenu() {
        return showScribbleMenu;
    }

    public void setShowScribbleMenu(boolean showScribbleMenu) {
        this.showScribbleMenu = showScribbleMenu;
    }

    public static ReaderMenuViewData create(ReaderMenuAction selectShapeAction, Set<ReaderMenuAction> disableMenuActions) {
        return new ReaderMenuViewData(selectShapeAction, disableMenuActions);
    }

    public static ReaderMenuViewData create(Set<ReaderMenuAction> disableMenuActions, ViewGroup parent) {
        return new ReaderMenuViewData(disableMenuActions, parent);
    }

    public static ReaderMenuViewData create(Set<ReaderMenuAction> disableMenuActions) {
        return new ReaderMenuViewData(disableMenuActions);
    }

    public static ReaderMenuViewData create(Set<ReaderMenuAction> disableMenuActions, boolean showScribbleMenu, ViewGroup parent) {
        return new ReaderMenuViewData(disableMenuActions, showScribbleMenu, parent);
    }
}
