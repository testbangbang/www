package com.onyx.edu.reader.ui.actions.form;

import android.graphics.Color;
import android.graphics.RectF;
import android.view.View;
import android.widget.ImageView;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.ReaderMenuAction;
import com.onyx.android.sdk.ui.data.ReaderMenuViewData;
import com.onyx.android.sdk.ui.data.ReaderMenuViewHolder;
import com.onyx.android.sdk.ui.view.CommonViewHolder;
import com.onyx.android.sdk.ui.view.OnyxToolbar;
import com.onyx.edu.reader.R;
import com.onyx.edu.reader.note.actions.RestoreShapeAction;
import com.onyx.edu.reader.note.data.ReaderNoteDataInfo;
import com.onyx.edu.reader.ui.actions.ShowReaderMenuAction;
import com.onyx.edu.reader.ui.actions.ShowScribbleMenuAction;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;
import com.onyx.edu.reader.ui.events.ScribbleMenuChangedEvent;

import java.util.ArrayList;
import java.util.List;

import static com.onyx.android.sdk.data.ReaderMenuAction.EXIT;
import static com.onyx.android.sdk.data.ReaderMenuAction.FETCH_REVIEW_DATA;
import static com.onyx.android.sdk.data.ReaderMenuAction.SCRIBBLE_CIRCLE;
import static com.onyx.android.sdk.data.ReaderMenuAction.SCRIBBLE_COLOR;
import static com.onyx.android.sdk.data.ReaderMenuAction.SCRIBBLE_ERASER;
import static com.onyx.android.sdk.data.ReaderMenuAction.SCRIBBLE_ERASER_ALL;
import static com.onyx.android.sdk.data.ReaderMenuAction.SCRIBBLE_ERASER_PART;
import static com.onyx.android.sdk.data.ReaderMenuAction.SCRIBBLE_LINE;
import static com.onyx.android.sdk.data.ReaderMenuAction.SCRIBBLE_PENCIL;
import static com.onyx.android.sdk.data.ReaderMenuAction.SCRIBBLE_REDO;
import static com.onyx.android.sdk.data.ReaderMenuAction.SCRIBBLE_SAVE;
import static com.onyx.android.sdk.data.ReaderMenuAction.SCRIBBLE_SHAPE;
import static com.onyx.android.sdk.data.ReaderMenuAction.SCRIBBLE_SQUARE;
import static com.onyx.android.sdk.data.ReaderMenuAction.SCRIBBLE_TRIANGLE_45;
import static com.onyx.android.sdk.data.ReaderMenuAction.SCRIBBLE_TRIANGLE_60;
import static com.onyx.android.sdk.data.ReaderMenuAction.SCRIBBLE_TRIANGLE_90;
import static com.onyx.android.sdk.data.ReaderMenuAction.SCRIBBLE_UNDO;
import static com.onyx.android.sdk.data.ReaderMenuAction.SCRIBBLE_WIDTH;
import static com.onyx.android.sdk.data.ReaderMenuAction.SCRIBBLE_WIDTH1;
import static com.onyx.android.sdk.data.ReaderMenuAction.SCRIBBLE_WIDTH2;
import static com.onyx.android.sdk.data.ReaderMenuAction.SCRIBBLE_WIDTH3;
import static com.onyx.android.sdk.data.ReaderMenuAction.SCRIBBLE_WIDTH4;
import static com.onyx.android.sdk.data.ReaderMenuAction.SCRIBBLE_WIDTH5;
import static com.onyx.android.sdk.data.ReaderMenuAction.SUBMIT;

/**
 * Created by ming on 2017/7/27.
 */

public class ShowFormMenuAction extends BaseFormMenuAction {

    public ShowFormMenuAction(ReaderMenuViewData menuViewData, ShowScribbleMenuAction.ActionCallback actionCallback) {
        super(menuViewData, actionCallback);
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, BaseCallback baseCallback) {
        super.execute(readerDataHolder, baseCallback);
        show(readerDataHolder);
    }

    @Override
    public void show(final ReaderDataHolder readerDataHolder) {
        super.show(readerDataHolder);
        bottomToolbar.setOnSizeChangeListener(new OnyxToolbar.OnSizeChangeListener() {
            @Override
            public void onSizeChanged(int w, int h, int oldw, int oldh) {
                postMenuChangedEvent(readerDataHolder);
            }
        });
        updateStrokeColor(readerDataHolder);
    }

    @Override
    public List<ReaderMenuViewHolder> getBottomMenuViewHolders(final ReaderDataHolder readerDataHolder) {
        List<ReaderMenuViewHolder> bottomMenuViewHolders = new ArrayList<>();

        bottomMenuViewHolders.add(createImageViewTitleHolder(readerDataHolder.getContext(), R.drawable.ic_exit, EXIT, R.string.exit));
        bottomMenuViewHolders.add(createImageViewTitleHolder(readerDataHolder.getContext(), R.drawable.ic_submit, SUBMIT, R.string.submit));
        bottomMenuViewHolders.add(createImageViewTitleHolder(readerDataHolder.getContext(), R.drawable.ic_download, FETCH_REVIEW_DATA, R.string.fetch));
        if (getReaderMenuViewData().isShowScribbleMenu()) {
            bottomMenuViewHolders.addAll(getScribbleMenuViewHolders(readerDataHolder));
        }

        bottomMenuViewHolders.add(ReaderMenuViewHolder.create(OnyxToolbar.Builder.createSpaceView(readerDataHolder.getContext(), 1f)));

        bottomMenuViewHolders.addAll(getPageTextViewHolder());
        return bottomMenuViewHolders;
    }

    @Override
    public List<ReaderMenuViewHolder> getTopMenuViewHolders(ReaderDataHolder readerDataHolder) {
        return null;
    }

    protected List<ReaderMenuViewHolder> getScribbleMenuViewHolders(final ReaderDataHolder readerDataHolder) {
        List<ReaderMenuViewHolder> menuViewHolders = new ArrayList<>();
        menuViewHolders.add(createImageViewTitleHolder(readerDataHolder.getContext(), R.drawable.ic_scribble_shape, SCRIBBLE_SHAPE, R.string.shape));
        menuViewHolders.add(createImageViewTitleHolder(readerDataHolder.getContext(), R.drawable.ic_scribble_width, SCRIBBLE_WIDTH, R.string.width));
        menuViewHolders.add(createImageViewTitleHolder(readerDataHolder.getContext(), R.drawable.ic_scribble_eraser_part, SCRIBBLE_ERASER, R.string.eraser));
        menuViewHolders.add(createImageViewTitleHolder(readerDataHolder.getContext(), R.drawable.ic_scribble_undo, SCRIBBLE_UNDO, R.string.undo));
        menuViewHolders.add(createImageViewTitleHolder(readerDataHolder.getContext(), R.drawable.ic_scribble_redo, SCRIBBLE_REDO, R.string.redo));
        menuViewHolders.add(createImageViewTitleHolder(readerDataHolder.getContext(), R.drawable.ic_scribble_save, SCRIBBLE_SAVE, R.string.save));
        return menuViewHolders;
    }

    @Override
    public List<ReaderMenuViewHolder> getChildrenViewHolders(final ReaderDataHolder readerDataHolder, final ReaderMenuAction parent) {
        if (parent == null) {
            return null;
        }
        switch (parent) {
            case SCRIBBLE_WIDTH:
                return getWidthMenuViewHolders(readerDataHolder);
            case SCRIBBLE_SHAPE:
                return getShapeMenuViewHolders(readerDataHolder);
            case SCRIBBLE_ERASER:
                return getEraserMenuViewHolders(readerDataHolder);
        }
        return null;
    }

    private List<ReaderMenuViewHolder> getWidthMenuViewHolders(final ReaderDataHolder readerDataHolder) {
        List<ReaderMenuViewHolder> widthViewHolders = new ArrayList<>();
        widthViewHolders.add(createMarkerViewHolder(readerDataHolder.getContext(), R.drawable.ic_scribble_width_1, R.drawable.ic_scribble_dot, R.layout.scribble_expand_menu_item_view, SCRIBBLE_WIDTH1));
        widthViewHolders.add(createMarkerViewHolder(readerDataHolder.getContext(), R.drawable.ic_scribble_width_2, R.drawable.ic_scribble_dot, R.layout.scribble_expand_menu_item_view, SCRIBBLE_WIDTH2));
        widthViewHolders.add(createMarkerViewHolder(readerDataHolder.getContext(), R.drawable.ic_scribble_width_3, R.drawable.ic_scribble_dot, R.layout.scribble_expand_menu_item_view, SCRIBBLE_WIDTH3));
        widthViewHolders.add(createMarkerViewHolder(readerDataHolder.getContext(), R.drawable.ic_scribble_width_4, R.drawable.ic_scribble_dot, R.layout.scribble_expand_menu_item_view, SCRIBBLE_WIDTH4));
        widthViewHolders.add(createMarkerViewHolder(readerDataHolder.getContext(), R.drawable.ic_scribble_width_5, R.drawable.ic_scribble_dot, R.layout.scribble_expand_menu_item_view, SCRIBBLE_WIDTH5));
        return widthViewHolders;
    }

    private List<ReaderMenuViewHolder> getShapeMenuViewHolders(final ReaderDataHolder readerDataHolder) {
        List<ReaderMenuViewHolder> shapeViewHolders = new ArrayList<>();
        shapeViewHolders.add(createMarkerViewHolder(readerDataHolder.getContext(), R.drawable.ic_scribble_shape_pencil, R.drawable.ic_scribble_dot, R.layout.scribble_expand_menu_item_view, SCRIBBLE_PENCIL));
        shapeViewHolders.add(createMarkerViewHolder(readerDataHolder.getContext(), R.drawable.ic_scribble_shape_line, R.drawable.ic_scribble_dot, R.layout.scribble_expand_menu_item_view, SCRIBBLE_LINE));
        shapeViewHolders.add(createMarkerViewHolder(readerDataHolder.getContext(), R.drawable.ic_scribble_shape_trigon_45, R.drawable.ic_scribble_dot, R.layout.scribble_expand_menu_item_view, SCRIBBLE_TRIANGLE_45));
        shapeViewHolders.add(createMarkerViewHolder(readerDataHolder.getContext(), R.drawable.ic_scribble_shape_trigon_60, R.drawable.ic_scribble_dot, R.layout.scribble_expand_menu_item_view, SCRIBBLE_TRIANGLE_60));
        shapeViewHolders.add(createMarkerViewHolder(readerDataHolder.getContext(), R.drawable.ic_scribble_shape_trigon_90, R.drawable.ic_scribble_dot, R.layout.scribble_expand_menu_item_view, SCRIBBLE_TRIANGLE_90));
        shapeViewHolders.add(createMarkerViewHolder(readerDataHolder.getContext(), R.drawable.ic_scribble_shape_circle, R.drawable.ic_scribble_dot, R.layout.scribble_expand_menu_item_view, SCRIBBLE_CIRCLE));
        shapeViewHolders.add(createMarkerViewHolder(readerDataHolder.getContext(), R.drawable.ic_scribble_shape_square, R.drawable.ic_scribble_dot, R.layout.scribble_expand_menu_item_view, SCRIBBLE_SQUARE));
        return shapeViewHolders;
    }

    private List<ReaderMenuViewHolder> getEraserMenuViewHolders(final ReaderDataHolder readerDataHolder) {
        List<ReaderMenuViewHolder> eraserViewHolders = new ArrayList<>();
        eraserViewHolders.add(createMarkerViewHolder(readerDataHolder.getContext(), R.drawable.ic_scribble_eraser_part, R.drawable.ic_scribble_dot, R.layout.scribble_expand_menu_item_view, SCRIBBLE_ERASER_PART));
        eraserViewHolders.add(createMarkerViewHolder(readerDataHolder.getContext(), R.drawable.ic_scribble_eraser_all, R.drawable.ic_scribble_dot, R.layout.scribble_expand_menu_item_view, SCRIBBLE_ERASER_ALL));
        return eraserViewHolders;
    }

    @Override
    public void onMenuClicked(final ReaderDataHolder readerDataHolder, final ReaderMenuAction action) {
        super.onMenuClicked(readerDataHolder, action);
        if (action == null) {
            return;
        }

        switch (action) {
            case SCRIBBLE_WIDTH:
                break;
            case SCRIBBLE_SHAPE:
                break;
            case SCRIBBLE_ERASER_PART:
                onSelectEraser(readerDataHolder, false);
                break;
            case SCRIBBLE_ERASER_ALL:
                onSelectEraser(readerDataHolder, true);
                break;
            case SCRIBBLE_PENCIL:
            case SCRIBBLE_BRUSH:
            case SCRIBBLE_LINE:
            case SCRIBBLE_TRIANGLE:
            case SCRIBBLE_CIRCLE:
            case SCRIBBLE_SQUARE:
                onSelectShape(readerDataHolder);
                break;
            case SCRIBBLE_WIDTH1:
            case SCRIBBLE_WIDTH2:
            case SCRIBBLE_WIDTH3:
            case SCRIBBLE_WIDTH4:
            case SCRIBBLE_WIDTH5:
                onSelectWidth(readerDataHolder);
                break;
        }
    }

    @Override
    public ReaderMenuAction getChildrenSelectedAction(ReaderMenuAction parent) {
        if (parent == null) {
            return null;
        }
        switch (parent) {
            case SCRIBBLE_WIDTH:
                return getSelectWidthAction();
            case SCRIBBLE_SHAPE:
                return getSelectShapeAction();
            case SCRIBBLE_ERASER:
                return getSelectEraserAction();
        }
        return getSelectShapeAction();
    }

    @Override
    public void updateChildrenMenuStateOnCreated(ReaderDataHolder readerDataHolder, ReaderMenuAction selectAction, List<ReaderMenuViewHolder> viewHolders, ReaderMenuViewHolder parent) {
        updateMarkerView(selectAction, viewHolders, parent);
    }

    @Override
    public void updateChildrenMenuStateOnClicked(ReaderDataHolder readerDataHolder, ReaderMenuAction selectAction, List<ReaderMenuViewHolder> viewHolders, ReaderMenuViewHolder parent) {
        updateMarkerView(selectAction, viewHolders, parent);
    }

    public void setSelectWidthAction(ReaderMenuAction selectWidthAction) {
        getReaderMenuViewData().setSelectWidthAction(selectWidthAction);
    }

    public void setSelectShapeAction(ReaderMenuAction selectShapeAction) {
        getReaderMenuViewData().setSelectShapeAction(selectShapeAction);
    }

    public void setSelectEraserAction(ReaderMenuAction selectEraserAction) {
        getReaderMenuViewData().setSelectEraserAction(selectEraserAction);
    }

    public ReaderMenuAction getSelectEraserAction() {
        return getReaderMenuViewData().getSelectEraserAction();
    }

    public ReaderMenuAction getSelectShapeAction() {
        return getReaderMenuViewData().getSelectShapeAction();
    }

    public ReaderMenuAction getSelectWidthAction() {
        return getReaderMenuViewData().getSelectWidthAction();
    }

    private void onSelectEraser(final ReaderDataHolder readerDataHolder, boolean wholeEraser) {
        if (!wholeEraser) {
            setSelectShapeAction(null);
            setSelectWidthAction(null);
        }
    }

    private void onSelectWidth(final ReaderDataHolder readerDataHolder) {
        if (getSelectShapeAction() == null) {
            new RestoreShapeAction().execute(readerDataHolder, new BaseCallback() {
                @Override
                public void done(BaseRequest request, Throwable e) {
                    int currentShapeType = readerDataHolder.getNoteManager().getNoteDataInfo().getCurrentShapeType();
                    setSelectShapeAction(ShowReaderMenuAction.createShapeAction(currentShapeType));
                }
            });
        }
    }

    private void onSelectShape(final ReaderDataHolder readerDataHolder) {
        setSelectEraserAction(null);
        if (getSelectWidthAction() == null) {
            float strokeWidth =  readerDataHolder.getNoteManager().getNoteDataInfo().getStrokeWidth();
            setSelectWidthAction(ShowReaderMenuAction.menuIdFromStrokeWidth(strokeWidth));
        }
    }

    private void updateStrokeColor(final ReaderDataHolder readerDataHolder) {
        final CommonViewHolder viewHolder = getMenuViewHolder(SCRIBBLE_COLOR);
        if (viewHolder == null) {
            return;
        }
        ReaderNoteDataInfo noteDataInfo = readerDataHolder.getNoteManager().getNoteDataInfo();
        boolean isBlack = noteDataInfo.getStrokeColor() == Color.BLACK;
        ImageView view = (ImageView) viewHolder.itemView;
        view.setImageResource(isBlack ? R.drawable.ic_scribble_black : R.drawable.ic_scribble_white);
    }

    private void postMenuChangedEvent(final ReaderDataHolder readerDataHolder) {
        int bottomOfTopToolBar = 0;
        int topOfBottomToolBar = 0;
        RectF excludeRect = new RectF();
        topOfBottomToolBar = bottomToolbar.getTop();
        readerDataHolder.getEventBus().post(ScribbleMenuChangedEvent.create(bottomOfTopToolBar, topOfBottomToolBar, excludeRect));
    }

    private void updateMarkerView(ReaderMenuAction selectAction, List<ReaderMenuViewHolder> viewHolders, final ReaderMenuViewHolder parent) {
        if (selectAction == null) {
            return;
        }
        for (ReaderMenuViewHolder viewHolder : viewHolders) {
            viewHolder.setVisibility(R.id.marker_view, selectAction == viewHolder.getMenuAction() ? View.VISIBLE : View.INVISIBLE);
        }
        switch (parent.getMenuAction()) {
            case SCRIBBLE_WIDTH:
                getReaderMenuViewData().setSelectWidthAction(selectAction);
                break;
            case SCRIBBLE_SHAPE:
                getReaderMenuViewData().setSelectShapeAction(selectAction);
                break;
            case SCRIBBLE_ERASER:
                getReaderMenuViewData().setSelectEraserAction(selectAction);
                break;
        }
    }
}
