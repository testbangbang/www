package com.onyx.kreader.ui.actions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.ReaderMenuAction;
import com.onyx.android.sdk.ui.view.CommonViewHolder;
import com.onyx.android.sdk.ui.view.OnyxToolbar;
import com.onyx.android.sdk.utils.DimenUtils;
import com.onyx.kreader.R;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.events.CloseScribbleMenuEvent;
import com.onyx.kreader.ui.events.ScribbleMenuChangedEvent;
import com.onyx.kreader.ui.handler.HandlerManager;

import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.List;

/**
 * Created by ming on 16/9/22.
 */
public class ShowScribbleMenuAction extends BaseAction implements View.OnClickListener {

    public static abstract class ActionCallback {
        public abstract void onClicked(final ReaderMenuAction action);
    }

    public static abstract class MenuCallback {
        public abstract List<ReaderMenuAction> getIgnoreMenu();
    }

    private ViewGroup parent;
    private OnyxToolbar bottomToolbar;
    private OnyxToolbar topToolbar;
    private View fullToolbar;
    private HashMap<ReaderMenuAction, CommonViewHolder> scribbleViewHolderMap = new HashMap<>();
    private BaseCallback callback;
    private ReaderMenuAction selectWidthAction = ReaderMenuAction.SCRIBBLE_WIDTH1;
    private ReaderMenuAction selectShapeAction = ReaderMenuAction.SCRIBBLE_PENCIL;
    private ReaderMenuAction selectEraserAction = ReaderMenuAction.SCRIBBLE_ERASER_PART;
    private ActionCallback actionCallback;
    private ReaderDataHolder readerDataHolder;
    private boolean isDrag = false;
    private List<ReaderMenuAction> disableMenuActions;

    public ShowScribbleMenuAction(ViewGroup parent,
                                  final ActionCallback actionCallback, List<ReaderMenuAction> disableMenuActions) {
        this.parent = parent;
        this.actionCallback = actionCallback;
        this.disableMenuActions = disableMenuActions;
    }

    public void execute(ReaderDataHolder readerDataHolder, BaseCallback callback) {
        this.callback = callback;
        readerDataHolder.getHandlerManager().setActiveProvider(HandlerManager.SCRIBBLE_PROVIDER);
        readerDataHolder.getEventBus().register(this);
        show(readerDataHolder);
    }

    public void show(final ReaderDataHolder readerDataHolder) {
        this.readerDataHolder = readerDataHolder;
        topToolbar = createScribbleTopToolbar(readerDataHolder);
        parent.addView(topToolbar);

        bottomToolbar = createScribbleBottomToolbar(readerDataHolder);
        parent.addView(bottomToolbar);

        fullToolbar = createFullScreenToolbar(readerDataHolder);
        parent.addView(fullToolbar);
        fullToolbar.setVisibility(View.GONE);

        topToolbar.setOnSizeChangeListener(new OnyxToolbar.OnSizeChangeListener() {
            @Override
            public void onSizeChanged(int w, int h, int oldw, int oldh) {
                postMenuChangedEvent(readerDataHolder);
            }
        });

        bottomToolbar.setOnSizeChangeListener(new OnyxToolbar.OnSizeChangeListener() {
            @Override
            public void onSizeChanged(int w, int h, int oldw, int oldh) {
                postMenuChangedEvent(readerDataHolder);
            }
        });
    }

    private OnyxToolbar createScribbleBottomToolbar(final ReaderDataHolder readerDataHolder) {
        OnyxToolbar toolbar = new OnyxToolbar(readerDataHolder.getContext(), OnyxToolbar.Direction.Bottom, OnyxToolbar.FillStyle.WrapContent);
        final ReaderMenuAction[] expandedActions = {ReaderMenuAction.SCRIBBLE_WIDTH, ReaderMenuAction.SCRIBBLE_SHAPE, ReaderMenuAction.SCRIBBLE_ERASER};

        addMarkerViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_width, R.drawable.ic_triangle, R.layout.scribble_bottom_menu_item_view, ReaderMenuAction.SCRIBBLE_WIDTH);
        addMarkerViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_shape, R.drawable.ic_triangle, R.layout.scribble_bottom_menu_item_view, ReaderMenuAction.SCRIBBLE_SHAPE);
        addMarkerViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_eraser, R.drawable.ic_triangle, R.layout.scribble_bottom_menu_item_view, ReaderMenuAction.SCRIBBLE_ERASER);
        addMarkerViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_drag_forbid, R.drawable.ic_triangle, R.layout.scribble_bottom_menu_item_view, ReaderMenuAction.SCRIBBLE_DRAG);
        addMarkerViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_pack_up, R.drawable.ic_triangle, R.layout.scribble_bottom_menu_item_view, ReaderMenuAction.SCRIBBLE_MINIMIZE);

        toolbar.addViewHolder(new CommonViewHolder(OnyxToolbar.Builder.createSpaceView(readerDataHolder.getContext(), 1f)));

        addMarkerViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_page_arrow_left, R.drawable.ic_triangle, R.layout.scribble_bottom_menu_item_view, ReaderMenuAction.SCRIBBLE_PREV_PAGE);
        addMarkerViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_page_arrow_right, R.drawable.ic_triangle, R.layout.scribble_bottom_menu_item_view, ReaderMenuAction.SCRIBBLE_NEXT_PAGE);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        int margin = DimenUtils.dip2px(readerDataHolder.getContext(), 10);
        toolbar.setMenuViewMargin(margin, 0, margin, 0);
        toolbar.setLayoutParams(lp);

        toolbar.setOnMenuClickListener(new OnyxToolbar.OnMenuClickListener() {
            @Override
            public OnyxToolbar OnClickListener(View view) {
                ReaderMenuAction action = (ReaderMenuAction) view.getTag();
                handleClickListener(action);
                return handleBottomMenuView(readerDataHolder, action, expandedActions);
            }
        });
        return toolbar;
    }

    private OnyxToolbar createScribbleTopToolbar(ReaderDataHolder readerDataHolder) {
        OnyxToolbar toolbar = new OnyxToolbar(readerDataHolder.getContext(), OnyxToolbar.Direction.Top, OnyxToolbar.FillStyle.WrapContent);

        addImageViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_undo, ReaderMenuAction.SCRIBBLE_UNDO);
        addImageViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_save, ReaderMenuAction.SCRIBBLE_SAVE);
        addImageViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_redo, ReaderMenuAction.SCRIBBLE_REDO);

        toolbar.addViewHolder(new CommonViewHolder(OnyxToolbar.Builder.createSpaceView(readerDataHolder.getContext(), 1f)));

        addImageViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_dialog_reader_page_closed, ReaderMenuAction.SCRIBBLE_CLOSE);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        int margin = DimenUtils.dip2px(readerDataHolder.getContext(), 10);
        toolbar.setMenuViewMargin(margin, 0, margin, 0);
        toolbar.setLayoutParams(lp);

        toolbar.setOnMenuClickListener(new OnyxToolbar.OnMenuClickListener() {
            @Override
            public OnyxToolbar OnClickListener(View view) {
                ReaderMenuAction action = (ReaderMenuAction) view.getTag();
                handleClickListener(action);
                return null;
            }
        });
        return toolbar;
    }

    private OnyxToolbar handleBottomMenuView(ReaderDataHolder readerDataHolder, ReaderMenuAction clickedAction, ReaderMenuAction[] expandedActions) {
        updateMarkerView(clickedAction, expandedActions);

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

    private void updateMarkerView(ReaderMenuAction selectAction, ReaderMenuAction[] actions) {
        for (int i = 0; i < actions.length; i++) {
            scribbleViewHolderMap.get(actions[i]).setVisibility(R.id.marker_view, View.INVISIBLE);
            if (selectAction == actions[i]) {
                scribbleViewHolderMap.get(actions[i]).setVisibility(R.id.marker_view, View.VISIBLE);
            }
        }
    }

    private void addMarkerViewHolder(OnyxToolbar toolbar, Context context, int imageResId, int selectResId, int layoutId, final ReaderMenuAction action) {
        if (disableMenuActions.contains(action)) {
            return;
        }
        CommonViewHolder markerViewHolder = OnyxToolbar.Builder.createMarkerViewHolder(context, R.id.content_view, R.id.marker_view, imageResId, selectResId, layoutId, action);
        toolbar.addViewHolder(markerViewHolder);
        scribbleViewHolderMap.put(action, markerViewHolder);
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

    private OnyxToolbar createWidthToolbar(ReaderDataHolder readerDataHolder) {
        final OnyxToolbar toolbar = new OnyxToolbar(readerDataHolder.getContext());
        toolbar.setClickedDismissToolbar(true);
        final ReaderMenuAction[] selectActions = {ReaderMenuAction.SCRIBBLE_WIDTH1, ReaderMenuAction.SCRIBBLE_WIDTH2, ReaderMenuAction.SCRIBBLE_WIDTH3, ReaderMenuAction.SCRIBBLE_WIDTH4, ReaderMenuAction.SCRIBBLE_WIDTH5};

        addMarkerViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_width_1, R.drawable.ic_dot, R.layout.scribble_expand_menu_item_view, ReaderMenuAction.SCRIBBLE_WIDTH1);
        addMarkerViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_width_2, R.drawable.ic_dot, R.layout.scribble_expand_menu_item_view, ReaderMenuAction.SCRIBBLE_WIDTH2);
        addMarkerViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_width_3, R.drawable.ic_dot, R.layout.scribble_expand_menu_item_view, ReaderMenuAction.SCRIBBLE_WIDTH3);
        addMarkerViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_width_4, R.drawable.ic_dot, R.layout.scribble_expand_menu_item_view, ReaderMenuAction.SCRIBBLE_WIDTH4);
        addMarkerViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_width_5, R.drawable.ic_dot, R.layout.scribble_expand_menu_item_view, ReaderMenuAction.SCRIBBLE_WIDTH5);
        updateMarkerView(selectWidthAction, selectActions);

        toolbar.setOnMenuClickListener(new OnyxToolbar.OnMenuClickListener() {
            @Override
            public OnyxToolbar OnClickListener(View view) {
                ReaderMenuAction action = (ReaderMenuAction) view.getTag();
                selectWidthAction = action;
                handleClickListener(action);
                updateMarkerView(action, selectActions);
                return null;
            }
        });
        return toolbar;
    }

    private OnyxToolbar createShapeToolbar(ReaderDataHolder readerDataHolder) {
        final OnyxToolbar toolbar = new OnyxToolbar(readerDataHolder.getContext());
        toolbar.setClickedDismissToolbar(true);
        final ReaderMenuAction[] selectActions = {ReaderMenuAction.SCRIBBLE_PENCIL, ReaderMenuAction.SCRIBBLE_BRUSH, ReaderMenuAction.SCRIBBLE_LINE, ReaderMenuAction.SCRIBBLE_TRIANGLE, ReaderMenuAction.SCRIBBLE_CIRCLE, ReaderMenuAction.SCRIBBLE_SQUARE};

        addMarkerViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_shape_pencil, R.drawable.ic_dot, R.layout.scribble_expand_menu_item_view, ReaderMenuAction.SCRIBBLE_PENCIL);
        addMarkerViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_shape_brush, R.drawable.ic_dot, R.layout.scribble_expand_menu_item_view, ReaderMenuAction.SCRIBBLE_BRUSH);
        addMarkerViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_shape_line, R.drawable.ic_dot, R.layout.scribble_expand_menu_item_view, ReaderMenuAction.SCRIBBLE_LINE);
        addMarkerViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_shape_trigon, R.drawable.ic_dot, R.layout.scribble_expand_menu_item_view, ReaderMenuAction.SCRIBBLE_TRIANGLE);
        addMarkerViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_shape_circle, R.drawable.ic_dot, R.layout.scribble_expand_menu_item_view, ReaderMenuAction.SCRIBBLE_CIRCLE);
        addMarkerViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_shape_square, R.drawable.ic_dot, R.layout.scribble_expand_menu_item_view, ReaderMenuAction.SCRIBBLE_SQUARE);
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
        toolbar.setClickedDismissToolbar(true);
        final ReaderMenuAction[] selectActions = {ReaderMenuAction.SCRIBBLE_ERASER_PART, ReaderMenuAction.SCRIBBLE_ERASER_ALL};

        addMarkerViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_eraser_part, R.drawable.ic_dot, R.layout.scribble_expand_menu_item_view, ReaderMenuAction.SCRIBBLE_ERASER_PART);
        addMarkerViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_eraser_all, R.drawable.ic_dot, R.layout.scribble_expand_menu_item_view, ReaderMenuAction.SCRIBBLE_ERASER_ALL);
        updateMarkerView(selectEraserAction, selectActions);

        toolbar.setOnMenuClickListener(new OnyxToolbar.OnMenuClickListener() {
            @Override
            public OnyxToolbar OnClickListener(View view) {
                ReaderMenuAction action = (ReaderMenuAction) view.getTag();
                handleClickListener(action);
                updateMarkerView(action, selectActions);
                return null;
            }
        });
        return toolbar;
    }

    private View createFullScreenToolbar(ReaderDataHolder readerDataHolder) {
        View view = LayoutInflater.from(readerDataHolder.getContext()).inflate(R.layout.scribble_full_screen_tool_bar_layout, null, false);
        ImageView leftPage = (ImageView) view.findViewById(R.id.left_page);
        ImageView rightPage = (ImageView) view.findViewById(R.id.right_page);
        ImageView restore = (ImageView) view.findViewById(R.id.restore);
        leftPage.setTag(ReaderMenuAction.SCRIBBLE_PREV_PAGE);
        rightPage.setTag(ReaderMenuAction.SCRIBBLE_NEXT_PAGE);
        restore.setTag(ReaderMenuAction.SCRIBBLE_MAXIMIZE);

        leftPage.setOnClickListener(this);
        rightPage.setOnClickListener(this);
        restore.setOnClickListener(this);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        int margin = DimenUtils.dip2px(readerDataHolder.getContext(), 10);
        lp.setMargins(0, 0, margin, margin);
        view.setLayoutParams(lp);
        return view;
    }

    @Override
    public void onClick(View v) {
        ReaderMenuAction action = (ReaderMenuAction) v.getTag();
        handleClickListener(action);
    }

    private void handleClickListener(ReaderMenuAction action) {
        if (action == null) {
            return;
        }

        actionCallback.onClicked(action);
        switch (action) {
            case SCRIBBLE_DRAG:
                changeDragIcon();
                break;
            case SCRIBBLE_WIDTH:
                break;
            case SCRIBBLE_SHAPE:
                break;
            case SCRIBBLE_CLOSE:
                if (callback != null) {
                    removeToolbar(null);
                    callback.done(null, null);
                }
                break;
            case SCRIBBLE_MINIMIZE:
                changeToolBarVisibility(true);
                break;
            case SCRIBBLE_MAXIMIZE:
                changeToolBarVisibility(false);
        }
    }

    private void changeDragIcon(){
        isDrag = !isDrag;
        scribbleViewHolderMap.get(ReaderMenuAction.SCRIBBLE_DRAG).setImageResource(R.id.content_view,
                isDrag ? R.drawable.ic_drag : R.drawable.ic_drag_forbid);
    }

    @Subscribe
    public void removeToolbar(CloseScribbleMenuEvent event) {
        parent.removeView(topToolbar);
        parent.removeView(bottomToolbar);
        parent.removeView(fullToolbar);
    }

    private void changeToolBarVisibility(boolean packUp) {
        topToolbar.setVisibility(packUp ? View.GONE : View.VISIBLE);
        bottomToolbar.setVisibility(packUp ? View.GONE : View.VISIBLE);
        fullToolbar.setVisibility(packUp ? View.VISIBLE : View.GONE);
        postMenuChangedEvent(readerDataHolder);
    }

    public boolean isDrag() {
        return isDrag;
    }

    private void postMenuChangedEvent(final ReaderDataHolder readerDataHolder) {
        int bottomOfTopToolBar = 0;
        int topOfBottomToolBar = 0;
        if (bottomToolbar.getVisibility() == View.VISIBLE) {
            bottomOfTopToolBar = topToolbar.getBottom();
        }
        if (topToolbar.getVisibility() == View.VISIBLE) {
            topOfBottomToolBar = bottomToolbar.getTop();
        }
        readerDataHolder.getEventBus().post(ScribbleMenuChangedEvent.create(bottomOfTopToolBar, topOfBottomToolBar));
    }

}
