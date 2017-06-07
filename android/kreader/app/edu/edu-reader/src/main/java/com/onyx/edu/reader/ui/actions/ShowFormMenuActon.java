package com.onyx.edu.reader.ui.actions;

import android.content.Context;
import android.graphics.Color;
import android.graphics.RectF;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.ReaderMenuAction;
import com.onyx.android.sdk.ui.compat.AppCompatUtils;
import com.onyx.android.sdk.ui.view.CommonViewHolder;
import com.onyx.android.sdk.ui.view.OnyxToolbar;
import com.onyx.android.sdk.utils.DimenUtils;
import com.onyx.edu.reader.R;
import com.onyx.edu.reader.note.actions.RestoreShapeAction;
import com.onyx.edu.reader.note.data.ReaderNoteDataInfo;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;
import com.onyx.edu.reader.ui.events.ScribbleMenuChangedEvent;
import com.onyx.edu.reader.ui.handler.HandlerManager;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by ming on 2017/6/7.
 */

public class ShowFormMenuActon extends BaseAction {

    private OnyxToolbar bottomToolbar;
    private HashMap<ReaderMenuAction, CommonViewHolder> scribbleViewHolderMap = new HashMap<>();
    private Set<ReaderMenuAction> disableMenuActions;
    private ViewGroup parent;
    private ShowScribbleMenuAction.ActionCallback actionCallback;
    private ReaderDataHolder readerDataHolder;
    private ReaderMenuAction selectWidthAction = ReaderMenuAction.SCRIBBLE_WIDTH1;
    private ReaderMenuAction selectShapeAction = ReaderMenuAction.SCRIBBLE_PENCIL;
    private ReaderMenuAction selectEraserAction = null;
    private boolean startNoteDrawing = false;

    public ShowFormMenuActon(Set<ReaderMenuAction> disableMenuActions, ViewGroup parent, boolean startNoteDrawing, ShowScribbleMenuAction.ActionCallback actionCallback) {
        this.disableMenuActions = disableMenuActions;
        this.parent = parent;
        this.startNoteDrawing = startNoteDrawing;
        this.actionCallback = actionCallback;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, BaseCallback baseCallback) {
        this.readerDataHolder = readerDataHolder;
        if (startNoteDrawing && !readerDataHolder.inNoteWritingProvider()) {
            readerDataHolder.getHandlerManager().setActiveProvider(HandlerManager.SCRIBBLE_PROVIDER);
        }
        show(readerDataHolder);
    }

    public void show(final ReaderDataHolder readerDataHolder) {

        bottomToolbar = createScribbleBottomToolbar(readerDataHolder);
        parent.addView(bottomToolbar);

        bottomToolbar.setOnSizeChangeListener(new OnyxToolbar.OnSizeChangeListener() {
            @Override
            public void onSizeChanged(int w, int h, int oldw, int oldh) {
                postMenuChangedEvent(readerDataHolder);
            }
        });
    }

    private OnyxToolbar createScribbleBottomToolbar(final ReaderDataHolder readerDataHolder) {
        OnyxToolbar toolbar = new OnyxToolbar(readerDataHolder.getContext(), OnyxToolbar.Direction.Bottom, OnyxToolbar.FillStyle.WrapContent);
        toolbar.setAdjustLayoutForColorDevices(AppCompatUtils.isColorDevice(readerDataHolder.getContext()));
        final ReaderMenuAction[] expandedActions = {ReaderMenuAction.SCRIBBLE_WIDTH, ReaderMenuAction.SCRIBBLE_SHAPE, ReaderMenuAction.SCRIBBLE_ERASER};

        addImageViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_submit, ReaderMenuAction.SUBMIT);
        addImageViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_exit, ReaderMenuAction.EXIT);
        if (readerDataHolder.inNoteWritingProvider()) {
            addImageViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_scribble_width, ReaderMenuAction.SCRIBBLE_WIDTH);
            addImageViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_scribble_shape, ReaderMenuAction.SCRIBBLE_SHAPE);
            addImageViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_scribble_eraser_part, ReaderMenuAction.SCRIBBLE_ERASER);
            addImageViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_scribble_undo, ReaderMenuAction.SCRIBBLE_UNDO);
            addImageViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_scribble_save, ReaderMenuAction.SCRIBBLE_SAVE);
        }

        toolbar.addViewHolder(new CommonViewHolder(OnyxToolbar.Builder.createSpaceView(readerDataHolder.getContext(), 1f)));

        addImageViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.prevbtn_bg, ReaderMenuAction.SCRIBBLE_PREV_PAGE);

        String positionText = (readerDataHolder.getCurrentPage() + 1) + "/" + readerDataHolder.getPageCount();
        addTextViewHolder(toolbar, readerDataHolder.getContext(), readerDataHolder.getContext().getResources().getDimension(R.dimen.scribble_page_position_size), positionText, ReaderMenuAction.SCRIBBLE_PAGE_POSITION);
        addImageViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.nextbtn_bg, ReaderMenuAction.SCRIBBLE_NEXT_PAGE);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        int margin = DimenUtils.dip2px(readerDataHolder.getContext(), 5);
        toolbar.setMenuViewMargin(margin, 0, margin, 0);
        toolbar.setLayoutParams(lp);
        updateStrokeColor();

        toolbar.setOnMenuClickListener(new OnyxToolbar.OnMenuClickListener() {
            @Override
            public OnyxToolbar OnClickListener(View view) {
                ReaderMenuAction action = (ReaderMenuAction) view.getTag();
                handleClickListener(action);
                return handleBottomMenuView(readerDataHolder, action, expandedActions);
            }
        });
        toolbar.setOnToggleToolbarListener(new OnyxToolbar.OnToggleToolbarListener() {
            @Override
            public void onToggle(Object tag, boolean expand) {
                ReaderMenuAction action = (ReaderMenuAction) tag;
                actionCallback.onToggle(action, expand);
            }
        });
        return toolbar;
    }

    private void addImageViewHolder(OnyxToolbar toolbar, Context context, int imageResId, final ReaderMenuAction action) {
        if (disableMenuActions.contains(action)) {
            return;
        }
        ImageView imageView = OnyxToolbar.Builder.createImageView(context, imageResId);
        CommonViewHolder viewHolder = new CommonViewHolder(imageView);
        imageView.setTag(action);
        toolbar.addViewHolder(viewHolder);
        scribbleViewHolderMap.put(action, viewHolder);
    }

    private void addTextViewHolder(OnyxToolbar toolbar, Context context, float textSize, String text, final ReaderMenuAction action) {
        if (disableMenuActions.contains(action)) {
            return;
        }
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextSize(textSize);
        OnyxToolbar.Builder.setLayoutParams(context, textView, 0, 0);
        CommonViewHolder viewHolder = new CommonViewHolder(textView);
        textView.setTag(action);
        toolbar.addViewHolder(viewHolder);
        scribbleViewHolderMap.put(action, viewHolder);
    }

    private void handleClickListener(ReaderMenuAction action) {
        if (action == null) {
            return;
        }

        actionCallback.onClicked(action);
        switch (action) {
            case SCRIBBLE_WIDTH:
                break;
            case SCRIBBLE_SHAPE:
                break;
            case SCRIBBLE_ERASER_PART:
                onSelectEraser(false);
                break;
            case SCRIBBLE_ERASER_ALL:
                onSelectEraser(true);
                break;
            case SCRIBBLE_PENCIL:
            case SCRIBBLE_BRUSH:
            case SCRIBBLE_LINE:
            case SCRIBBLE_TRIANGLE:
            case SCRIBBLE_CIRCLE:
            case SCRIBBLE_SQUARE:
                onSelectShape();
                break;
            case SCRIBBLE_WIDTH1:
            case SCRIBBLE_WIDTH2:
            case SCRIBBLE_WIDTH3:
            case SCRIBBLE_WIDTH4:
            case SCRIBBLE_WIDTH5:
                onSelectWidth();
                break;
        }
    }

    private void onSelectEraser(boolean wholeEraser) {
        if (!wholeEraser) {
            selectShapeAction = null;
            setSelectWidthAction(null);
        }
    }

    private void onSelectWidth() {
        if (selectShapeAction == null) {
            new RestoreShapeAction().execute(readerDataHolder, new BaseCallback() {
                @Override
                public void done(BaseRequest request, Throwable e) {
                    int currentShapeType = readerDataHolder.getNoteManager().getNoteDataInfo().getCurrentShapeType();
                    selectShapeAction = ShowReaderMenuAction.createShapeAction(currentShapeType);
                }
            });
        }
    }

    private void onSelectShape() {
        selectEraserAction = null;
        if (selectWidthAction == null) {
            float strokeWidth =  readerDataHolder.getNoteManager().getNoteDataInfo().getStrokeWidth();
            setSelectWidthAction(ShowReaderMenuAction.menuIdFromStrokeWidth(strokeWidth));
        }
    }

    private void updateStrokeColor() {
        final CommonViewHolder viewHolder = scribbleViewHolderMap.get(ReaderMenuAction.SCRIBBLE_COLOR);
        if (viewHolder == null) {
            return;
        }
        ReaderNoteDataInfo noteDataInfo = readerDataHolder.getNoteManager().getNoteDataInfo();
        boolean isBlack = noteDataInfo.getStrokeColor() == Color.BLACK;
        ImageView view = (ImageView) viewHolder.itemView;
        view.setImageResource(isBlack ? R.drawable.ic_scribble_black : R.drawable.ic_scribble_white);
    }

    private OnyxToolbar handleBottomMenuView(ReaderDataHolder readerDataHolder, final ReaderMenuAction clickedAction, ReaderMenuAction[] expandedActions) {
        switch (clickedAction) {
            case SCRIBBLE_WIDTH:
                return createWidthToolbar(readerDataHolder);
            case SCRIBBLE_SHAPE:
                return createShapeToolbar(readerDataHolder);
            case SCRIBBLE_ERASER:
                return createEraserToolbar(readerDataHolder);
        }

        return null;
    }

    private OnyxToolbar createWidthToolbar(ReaderDataHolder readerDataHolder) {
        final OnyxToolbar toolbar = new OnyxToolbar(readerDataHolder.getContext());
        toolbar.setAdjustLayoutForColorDevices(AppCompatUtils.isColorDevice(readerDataHolder.getContext()));
        toolbar.setClickedDismissToolbar(true);
        final ReaderMenuAction[] selectActions = {ReaderMenuAction.SCRIBBLE_WIDTH1, ReaderMenuAction.SCRIBBLE_WIDTH2, ReaderMenuAction.SCRIBBLE_WIDTH3, ReaderMenuAction.SCRIBBLE_WIDTH4, ReaderMenuAction.SCRIBBLE_WIDTH5, ReaderMenuAction.SCRIBBLE_CUSTOM_WIDTH};

        addMarkerViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_scribble_width_1, R.drawable.ic_scribble_dot, R.layout.scribble_expand_menu_item_view, ReaderMenuAction.SCRIBBLE_WIDTH1);
        addMarkerViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_scribble_width_2, R.drawable.ic_scribble_dot, R.layout.scribble_expand_menu_item_view, ReaderMenuAction.SCRIBBLE_WIDTH2);
        addMarkerViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_scribble_width_3, R.drawable.ic_scribble_dot, R.layout.scribble_expand_menu_item_view, ReaderMenuAction.SCRIBBLE_WIDTH3);
        addMarkerViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_scribble_width_4, R.drawable.ic_scribble_dot, R.layout.scribble_expand_menu_item_view, ReaderMenuAction.SCRIBBLE_WIDTH4);
        addMarkerViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_scribble_width_5, R.drawable.ic_scribble_dot, R.layout.scribble_expand_menu_item_view, ReaderMenuAction.SCRIBBLE_WIDTH5);
        updateMarkerView(selectWidthAction, selectActions);

        toolbar.setOnMenuClickListener(new OnyxToolbar.OnMenuClickListener() {
            @Override
            public OnyxToolbar OnClickListener(View view) {
                ReaderMenuAction action = (ReaderMenuAction) view.getTag();
                setSelectWidthAction(action);
                handleClickListener(action);
                updateMarkerView(action, selectActions);
                return null;
            }
        });
        return toolbar;
    }

    private OnyxToolbar createShapeToolbar(ReaderDataHolder readerDataHolder) {
        final OnyxToolbar toolbar = new OnyxToolbar(readerDataHolder.getContext());
        toolbar.setAdjustLayoutForColorDevices(AppCompatUtils.isColorDevice(readerDataHolder.getContext()));
        toolbar.setClickedDismissToolbar(true);
        final ReaderMenuAction[] selectActions = {ReaderMenuAction.SCRIBBLE_PENCIL,
                ReaderMenuAction.SCRIBBLE_BRUSH, ReaderMenuAction.SCRIBBLE_LINE,
                ReaderMenuAction.SCRIBBLE_TRIANGLE_45,
                ReaderMenuAction.SCRIBBLE_TRIANGLE_60,
                ReaderMenuAction.SCRIBBLE_TRIANGLE_90,
                ReaderMenuAction.SCRIBBLE_CIRCLE,
                ReaderMenuAction.SCRIBBLE_SQUARE};

        addMarkerViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_scribble_shape_pencil, R.drawable.ic_scribble_dot, R.layout.scribble_expand_menu_item_view, ReaderMenuAction.SCRIBBLE_PENCIL);
        addMarkerViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_scribble_shape_line, R.drawable.ic_scribble_dot, R.layout.scribble_expand_menu_item_view, ReaderMenuAction.SCRIBBLE_LINE);
        addMarkerViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_scribble_shape_trigon_45, R.drawable.ic_scribble_dot, R.layout.scribble_expand_menu_item_view, ReaderMenuAction.SCRIBBLE_TRIANGLE_45);
        addMarkerViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_scribble_shape_trigon_60, R.drawable.ic_scribble_dot, R.layout.scribble_expand_menu_item_view, ReaderMenuAction.SCRIBBLE_TRIANGLE_60);
        addMarkerViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_scribble_shape_trigon_90, R.drawable.ic_scribble_dot, R.layout.scribble_expand_menu_item_view, ReaderMenuAction.SCRIBBLE_TRIANGLE_90);
        addMarkerViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_scribble_shape_circle, R.drawable.ic_scribble_dot, R.layout.scribble_expand_menu_item_view, ReaderMenuAction.SCRIBBLE_CIRCLE);
        addMarkerViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_scribble_shape_square, R.drawable.ic_scribble_dot, R.layout.scribble_expand_menu_item_view, ReaderMenuAction.SCRIBBLE_SQUARE);
        updateMarkerView(selectShapeAction, selectActions);

        toolbar.setOnMenuClickListener(new OnyxToolbar.OnMenuClickListener() {
            @Override
            public OnyxToolbar OnClickListener(View view) {
                ReaderMenuAction action = (ReaderMenuAction) view.getTag();
                selectShapeAction = action;
                handleClickListener(action);
                updateMarkerView(action, selectActions);
                return null;
            }
        });
        return toolbar;
    }

    private OnyxToolbar createEraserToolbar(ReaderDataHolder readerDataHolder) {
        final OnyxToolbar toolbar = new OnyxToolbar(readerDataHolder.getContext());
        toolbar.setAdjustLayoutForColorDevices(AppCompatUtils.isColorDevice(readerDataHolder.getContext()));
        toolbar.setClickedDismissToolbar(true);
        final ReaderMenuAction[] selectActions = {ReaderMenuAction.SCRIBBLE_ERASER_PART, ReaderMenuAction.SCRIBBLE_ERASER_ALL};

        addMarkerViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_scribble_eraser_part, R.drawable.ic_scribble_dot, R.layout.scribble_expand_menu_item_view, ReaderMenuAction.SCRIBBLE_ERASER_PART);
        addMarkerViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_scribble_eraser_all, R.drawable.ic_scribble_dot, R.layout.scribble_expand_menu_item_view, ReaderMenuAction.SCRIBBLE_ERASER_ALL);
        updateMarkerView(selectEraserAction, selectActions);

        toolbar.setOnMenuClickListener(new OnyxToolbar.OnMenuClickListener() {
            @Override
            public OnyxToolbar OnClickListener(View view) {
                ReaderMenuAction action = (ReaderMenuAction) view.getTag();
                selectEraserAction = action;
                handleClickListener(action);
                updateMarkerView(action, selectActions);
                return null;
            }
        });
        return toolbar;
    }

    private CommonViewHolder addMarkerViewHolder(OnyxToolbar toolbar, Context context, int imageResId, int selectResId, int layoutId, final ReaderMenuAction action) {
        if (disableMenuActions.contains(action)) {
            return null;
        }
        CommonViewHolder markerViewHolder = OnyxToolbar.Builder.createMarkerViewHolder(context, R.id.content_view, R.id.marker_view, imageResId, selectResId, layoutId, action);
        toolbar.addViewHolder(markerViewHolder);
        scribbleViewHolderMap.put(action, markerViewHolder);
        return markerViewHolder;
    }

    private void updateMarkerView(ReaderMenuAction selectAction, ReaderMenuAction[] actions) {
        if (selectAction == null) {
            return;
        }
        for (ReaderMenuAction action : actions) {
            CommonViewHolder viewHolder = scribbleViewHolderMap.get(action);
            if (viewHolder == null) {
                continue;
            }
            viewHolder.setVisibility(R.id.marker_view, selectAction == action ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public void setSelectShapeAction(ReaderMenuAction selectShapeAction) {
        this.selectShapeAction = selectShapeAction;
    }

    public void setSelectWidthAction(ReaderMenuAction selectWidthAction) {
        this.selectWidthAction = selectWidthAction;
    }

    private void postMenuChangedEvent(final ReaderDataHolder readerDataHolder) {
        int bottomOfTopToolBar = 0;
        int topOfBottomToolBar = 0;
        RectF excludeRect = new RectF();
        topOfBottomToolBar = bottomToolbar.getTop();
        readerDataHolder.getEventBus().post(ScribbleMenuChangedEvent.create(bottomOfTopToolBar, topOfBottomToolBar, excludeRect));
    }
}
