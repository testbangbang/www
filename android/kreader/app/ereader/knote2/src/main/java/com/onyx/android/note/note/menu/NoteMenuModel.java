package com.onyx.android.note.note.menu;

import android.databinding.ObservableField;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.onyx.android.note.NoteDataBundle;
import com.onyx.android.note.action.ClearAllFreeShapesAction;
import com.onyx.android.note.action.PenWidthChangeAction;
import com.onyx.android.note.action.RedoAction;
import com.onyx.android.note.action.UndoAction;
import com.onyx.android.note.action.menu.BackgroundChangeAction;
import com.onyx.android.note.action.menu.ToggleTopMenuAction;
import com.onyx.android.note.common.StrokeWidth;
import com.onyx.android.note.common.base.BaseViewModel;
import com.onyx.android.note.handler.HandlerManager;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.note.NoteManager;
import com.onyx.android.sdk.note.data.ScribbleMode;
import com.onyx.android.sdk.pen.EpdPenManager;
import com.onyx.android.sdk.scribble.data.NoteBackgroundType;
import com.onyx.android.sdk.scribble.data.NoteDrawingArgs;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lxm on 2018/2/2.
 */

public class NoteMenuModel extends BaseViewModel {

    private static final String TAG = "NoteMenuModel";

    public ObservableField<Boolean> menuExpanded = new ObservableField<>(true);
    
    public NoteMenuModel(@NonNull EventBus eventBus) {
        super(eventBus);
    }

    public void setMenuExpanded(boolean expand) {
        this.menuExpanded.set(expand);
    }

    public void toggle() {
        boolean expanded = !menuExpanded.get();
        setMenuExpanded(expanded);
    }

    public void onToggle(final View view) {
        new ToggleTopMenuAction(getNoteManager(), this).execute(null);
    }

    public void onPenWidth1(View view) {
        new PenWidthChangeAction(getNoteManager())
                .setPenWidth(StrokeWidth.LIGHT.getWidth())
                .execute(null);
    }

    public void onPenWidth2(View view) {
        new PenWidthChangeAction(getNoteManager())
                .setPenWidth(StrokeWidth.ULTRA_BOLD.getWidth())
                .execute(null);
    }

    public void onEmptyBackground(View view) {
        new BackgroundChangeAction(getNoteManager())
                .setBackground(NoteBackgroundType.EMPTY)
                .execute(null);
    }

    public void onGridBackground(View view) {
        new BackgroundChangeAction(getNoteManager())
                .setBackground(NoteBackgroundType.GRID)
                .execute(null);
    }

    public void onEnterErase(View view) {
        getNoteBundle().getHandlerManager().activeProvider(HandlerManager.ERASE_OVERLAY_PROVIDER);
    }

    public void onExitErase(View view) {
        getNoteBundle().getHandlerManager().activeProvider(HandlerManager.EPD_SHAPE_PROVIDER);
    }

    public void onRedo(View view) {
        new RedoAction(getNoteManager()).execute(null);
    }

    public void onUndo(View view) {
        new UndoAction(getNoteManager()).execute(null);
    }

    public void onErasePage(View view) {
        new ClearAllFreeShapesAction(getNoteManager()).execute(null);
    }

    public void onCircleShape(View view) {
        NoteDataBundle.getInstance().getDrawingArgs().setCurrentShapeType(ShapeFactory.SHAPE_RECTANGLE);
        getNoteBundle().getHandlerManager().activeProvider(HandlerManager.NORMAL_SHAPE_PROVIDER);
    }

    private NoteManager getNoteManager() {
        return getNoteBundle().getNoteManager();
    }

    private NoteDataBundle getNoteBundle() {
        return NoteDataBundle.getInstance();
    }
}
