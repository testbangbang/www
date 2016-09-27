package com.onyx.kreader.ui.actions;

import android.content.Context;
import android.graphics.RectF;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.ScribbleMenuAction;
import com.onyx.android.sdk.ui.view.OnyxToolbar;
import com.onyx.android.sdk.ui.view.viewholder.BaseViewHolder;
import com.onyx.android.sdk.ui.view.viewholder.SimpleSelectViewHolder;
import com.onyx.android.sdk.utils.DimenUtils;
import com.onyx.kreader.R;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.events.CloseScribbleMenuEvent;
import com.onyx.kreader.ui.events.ScribbleMenuChangedEvent;
import com.onyx.kreader.ui.handler.HandlerManager;

import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;

/**
 * Created by ming on 16/9/22.
 */
public class ShowScribbleMenuAction extends BaseAction implements View.OnClickListener {

    private static final String TAG = ShowScribbleMenuAction.class.getSimpleName();

    public static abstract class ActionCallback {
        public abstract void onClicked(final ScribbleMenuAction action);
    }

    private ViewGroup parent;
    private OnyxToolbar bottomToolbar;
    private OnyxToolbar topToolbar;
    private View fullToolbar;
    private HashMap<ScribbleMenuAction, BaseViewHolder> scribbleViewHolderMap = new HashMap<>();
    private BaseCallback callback;
    private ScribbleMenuAction selectWidthAction = ScribbleMenuAction.WIDTH1;
    private ScribbleMenuAction selectShapeAction = ScribbleMenuAction.PENCIL;
    private ScribbleMenuAction selectEraserAction = ScribbleMenuAction.ERASER_PART;
    private ActionCallback actionCallback;
    private ReaderDataHolder readerDataHolder;

    public ShowScribbleMenuAction(ViewGroup parent, final ActionCallback actionCallback) {
        this.parent = parent;
        this.actionCallback = actionCallback;
    }

    public void execute(ReaderDataHolder readerDataHolder,  BaseCallback callback) {
        this.callback = callback;
        readerDataHolder.getHandlerManager().setActiveProvider(HandlerManager.SCRIBBLE_PROVIDER);
        readerDataHolder.getEventBus().register(this);
        show(readerDataHolder);
        readerDataHolder.getEventBus().post(ScribbleMenuChangedEvent.create());
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
                updateVisibleDrawRect(readerDataHolder);
            }
        });

        bottomToolbar.setOnSizeChangeListener(new OnyxToolbar.OnSizeChangeListener() {
            @Override
            public void onSizeChanged(int w, int h, int oldw, int oldh) {
                updateVisibleDrawRect(readerDataHolder);
            }
        });
    }

    private OnyxToolbar createScribbleBottomToolbar(final ReaderDataHolder readerDataHolder) {
        OnyxToolbar toolbar = new OnyxToolbar(readerDataHolder.getContext(), OnyxToolbar.Direction.Bottom, OnyxToolbar.FillStyle.WrapContent);
        final ScribbleMenuAction[] expandedActions = {ScribbleMenuAction.WIDTH, ScribbleMenuAction.SHAPE, ScribbleMenuAction.ERASER};

        addSelectViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_width, R.drawable.ic_triangle, R.layout.scribble_bottom_menu_item_view, ScribbleMenuAction.WIDTH);
        addSelectViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_shape, R.drawable.ic_triangle, R.layout.scribble_bottom_menu_item_view, ScribbleMenuAction.SHAPE);
        addSelectViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_txt, R.drawable.ic_triangle, R.layout.scribble_bottom_menu_item_view, ScribbleMenuAction.TEXT);
        addSelectViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_eraser, R.drawable.ic_triangle, R.layout.scribble_bottom_menu_item_view, ScribbleMenuAction.ERASER);
        addSelectViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_drag, R.drawable.ic_triangle, R.layout.scribble_bottom_menu_item_view, ScribbleMenuAction.DRAG);
        addSelectViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_pack_up, R.drawable.ic_triangle, R.layout.scribble_bottom_menu_item_view, ScribbleMenuAction.MINIMIZE);

        toolbar.addViewHolder(new BaseViewHolder(OnyxToolbar.Builder.createSpaceView(readerDataHolder.getContext(), 1f)));

        addSelectViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_page_arrow_left, R.drawable.ic_triangle, R.layout.scribble_bottom_menu_item_view, ScribbleMenuAction.PREV_PAGE);
        addSelectViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_page_arrow_right, R.drawable.ic_triangle, R.layout.scribble_bottom_menu_item_view, ScribbleMenuAction.NEXT_PAGE);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        int margin = DimenUtils.dip2px(readerDataHolder.getContext(), 10);
        toolbar.setMenuViewMargin(margin, 0, margin, 0);
        toolbar.setLayoutParams(lp);

        toolbar.setOnMenuClickListener(new OnyxToolbar.OnMenuClickListener() {
            @Override
            public OnyxToolbar OnClickListener(View view) {
                ScribbleMenuAction action = (ScribbleMenuAction) view.getTag();
                handleClickListener(action);
                return handleBottomMenuView(readerDataHolder, action, expandedActions);
            }
        });
        return toolbar;
    }

    private OnyxToolbar createScribbleTopToolbar(ReaderDataHolder readerDataHolder) {
        OnyxToolbar toolbar = new OnyxToolbar(readerDataHolder.getContext(), OnyxToolbar.Direction.Top, OnyxToolbar.FillStyle.WrapContent);

        addImageViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_undo, ScribbleMenuAction.UNDO);
        addImageViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_save, ScribbleMenuAction.SAVE);
        addImageViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_redo, ScribbleMenuAction.REDO);

        toolbar.addViewHolder(new BaseViewHolder(OnyxToolbar.Builder.createSpaceView(readerDataHolder.getContext(), 1f)));

        addImageViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_dialog_reader_page_closed, ScribbleMenuAction.CLOSE);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        int margin = DimenUtils.dip2px(readerDataHolder.getContext(), 10);
        toolbar.setMenuViewMargin(margin, 0, margin, 0);
        toolbar.setLayoutParams(lp);

        toolbar.setOnMenuClickListener(new OnyxToolbar.OnMenuClickListener() {
            @Override
            public OnyxToolbar OnClickListener(View view) {
                ScribbleMenuAction action = (ScribbleMenuAction) view.getTag();
                handleClickListener(action);
                return null;
            }
        });
        return toolbar;
    }

    private OnyxToolbar handleBottomMenuView(ReaderDataHolder readerDataHolder, ScribbleMenuAction clickedAction, ScribbleMenuAction[] expandedActions) {
        updateSelectTriangle(clickedAction, expandedActions);

        switch (clickedAction) {
            case WIDTH:
                return createWidthToolbar(readerDataHolder);
            case SHAPE:
                return createShapeToolbar(readerDataHolder);
            case ERASER:
                return createEraserToolbar(readerDataHolder);
        }
        return null;
    }

    private void updateSelectTriangle(ScribbleMenuAction selectAction, ScribbleMenuAction[] actions) {
        for (int i = 0; i < actions.length; i++) {
            ((SimpleSelectViewHolder) scribbleViewHolderMap.get(actions[i])).selectView.setVisibility(View.INVISIBLE);
            if (selectAction == actions[i]) {
                ((SimpleSelectViewHolder) scribbleViewHolderMap.get(selectAction)).selectView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void addSelectViewHolder(OnyxToolbar toolbar, Context context, int imageResId, int selectResId, int layoutId, final ScribbleMenuAction action) {
        SimpleSelectViewHolder simpleSelectViewHolder = OnyxToolbar.Builder.createSelectImageView(context, imageResId, selectResId, layoutId, action);
        toolbar.addViewHolder(simpleSelectViewHolder);
        scribbleViewHolderMap.put(action, simpleSelectViewHolder);
    }

    private void addImageViewHolder(OnyxToolbar toolbar, Context context, int imageResId, final ScribbleMenuAction action) {
        ImageView imageView = OnyxToolbar.Builder.createImageView(context, imageResId);
        BaseViewHolder viewHolder = new BaseViewHolder(imageView);
        imageView.setTag(action);
        toolbar.addViewHolder(viewHolder);
        scribbleViewHolderMap.put(action, viewHolder);
    }

    private OnyxToolbar createWidthToolbar(ReaderDataHolder readerDataHolder) {
        final OnyxToolbar toolbar = new OnyxToolbar(readerDataHolder.getContext());
        toolbar.setClickedDismissToolbar(true);
        final ScribbleMenuAction[] selectActions = {ScribbleMenuAction.WIDTH1, ScribbleMenuAction.WIDTH2, ScribbleMenuAction.WIDTH3, ScribbleMenuAction.WIDTH4, ScribbleMenuAction.WIDTH5};

        addSelectViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_width_1, R.drawable.ic_dot, R.layout.scribble_expand_menu_item_view, ScribbleMenuAction.WIDTH1);
        addSelectViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_width_2, R.drawable.ic_dot, R.layout.scribble_expand_menu_item_view, ScribbleMenuAction.WIDTH2);
        addSelectViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_width_3, R.drawable.ic_dot, R.layout.scribble_expand_menu_item_view, ScribbleMenuAction.WIDTH3);
        addSelectViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_width_4, R.drawable.ic_dot, R.layout.scribble_expand_menu_item_view, ScribbleMenuAction.WIDTH4);
        addSelectViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_width_5, R.drawable.ic_dot, R.layout.scribble_expand_menu_item_view, ScribbleMenuAction.WIDTH5);
        updateSelectTriangle(selectWidthAction, selectActions);

        toolbar.setOnMenuClickListener(new OnyxToolbar.OnMenuClickListener() {
            @Override
            public OnyxToolbar OnClickListener(View view) {
                ScribbleMenuAction action = (ScribbleMenuAction) view.getTag();
                selectWidthAction = action;
                handleClickListener(action);
                updateSelectTriangle(action, selectActions);
                return null;
            }
        });
        return toolbar;
    }

    private OnyxToolbar createShapeToolbar(ReaderDataHolder readerDataHolder) {
        final OnyxToolbar toolbar = new OnyxToolbar(readerDataHolder.getContext());
        toolbar.setClickedDismissToolbar(true);
        final ScribbleMenuAction[] selectActions = {ScribbleMenuAction.PENCIL, ScribbleMenuAction.BRUSH, ScribbleMenuAction.LINE, ScribbleMenuAction.TRIANGLE, ScribbleMenuAction.CIRCLE, ScribbleMenuAction.SQUARE};

        addSelectViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_shape_pencil, R.drawable.ic_dot, R.layout.scribble_expand_menu_item_view, ScribbleMenuAction.PENCIL);
        addSelectViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_shape_brush, R.drawable.ic_dot, R.layout.scribble_expand_menu_item_view, ScribbleMenuAction.BRUSH);
        addSelectViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_shape_line, R.drawable.ic_dot, R.layout.scribble_expand_menu_item_view, ScribbleMenuAction.LINE);
        addSelectViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_shape_trigon, R.drawable.ic_dot, R.layout.scribble_expand_menu_item_view, ScribbleMenuAction.TRIANGLE);
        addSelectViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_shape_circle, R.drawable.ic_dot, R.layout.scribble_expand_menu_item_view, ScribbleMenuAction.CIRCLE);
        addSelectViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_shape_square, R.drawable.ic_dot, R.layout.scribble_expand_menu_item_view, ScribbleMenuAction.SQUARE);
        updateSelectTriangle(selectShapeAction, selectActions);

        toolbar.setOnMenuClickListener(new OnyxToolbar.OnMenuClickListener() {
            @Override
            public OnyxToolbar OnClickListener(View view) {
                ScribbleMenuAction action = (ScribbleMenuAction) view.getTag();
                selectShapeAction = action;
                handleClickListener(action);
                updateSelectTriangle(action, selectActions);
                return null;
            }
        });
        return toolbar;
    }

    private OnyxToolbar createEraserToolbar(ReaderDataHolder readerDataHolder) {
        final OnyxToolbar toolbar = new OnyxToolbar(readerDataHolder.getContext());
        toolbar.setClickedDismissToolbar(true);
        final ScribbleMenuAction[] selectActions = {ScribbleMenuAction.ERASER_PART, ScribbleMenuAction.ERASER_ALL};

        addSelectViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_eraser_part, R.drawable.ic_dot, R.layout.scribble_expand_menu_item_view, ScribbleMenuAction.ERASER_PART);
        addSelectViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_eraser_all, R.drawable.ic_dot, R.layout.scribble_expand_menu_item_view, ScribbleMenuAction.ERASER_ALL);
        updateSelectTriangle(selectEraserAction, selectActions);

        toolbar.setOnMenuClickListener(new OnyxToolbar.OnMenuClickListener() {
            @Override
            public OnyxToolbar OnClickListener(View view) {
                ScribbleMenuAction action = (ScribbleMenuAction) view.getTag();
                selectEraserAction = action;
                handleClickListener(action);
                updateSelectTriangle(action, selectActions);
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
        leftPage.setTag(ScribbleMenuAction.PREV_PAGE);
        rightPage.setTag(ScribbleMenuAction.NEXT_PAGE);
        restore.setTag(ScribbleMenuAction.MAXIMIZE);

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
        ScribbleMenuAction action = (ScribbleMenuAction) v.getTag();
        handleClickListener(action);
    }

    private void handleClickListener(ScribbleMenuAction action) {
        if (action == null) {
            return;
        }

        actionCallback.onClicked(action);
        switch (action) {
            case WIDTH:
                break;
            case SHAPE:
                break;
            case CLOSE:
                if (callback != null) {
                    removeToolbar(null);
                    callback.done(null, null);
                }
                break;
            case MINIMIZE:
                changeToolBarVisibility(true);
                break;
            case MAXIMIZE:
                changeToolBarVisibility(false);
        }
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
        updateVisibleDrawRect(readerDataHolder);
    }

    public void setSelectWidthAction(ScribbleMenuAction selectWidthAction) {
        this.selectWidthAction = selectWidthAction;
    }

    public void setSelectShapeAction(ScribbleMenuAction selectShapeAction) {
        this.selectShapeAction = selectShapeAction;
    }

    public void setSelectEraserAction(ScribbleMenuAction selectEraserAction) {
        this.selectEraserAction = selectEraserAction;
    }

    private RectF getVisibleDrawRect(ReaderDataHolder readerDataHolder, OnyxToolbar bottomToolbar, OnyxToolbar topToolbar){
        // TODO: 16/9/27  only process first page
        PageInfo pageInfo = readerDataHolder.getReaderViewInfo().getFirstVisiblePage();
        RectF displayRect = pageInfo.getDisplayRect();
        if (bottomToolbar.getVisibility() != View.VISIBLE && topToolbar.getVisibility() != View.VISIBLE){
            return displayRect;
        }

        float top = Math.max(displayRect.top, topToolbar.getBottom());
        float bottom = Math.min(displayRect.bottom, bottomToolbar.getTop());
        RectF visibleRectF = new RectF(displayRect.left, top, displayRect.right, bottom);
        return visibleRectF;
    }

    private void updateVisibleDrawRect(final ReaderDataHolder readerDataHolder){
        readerDataHolder.getNoteManager().setVisibleDrawRectF(getVisibleDrawRect(readerDataHolder, bottomToolbar, topToolbar));
    }

}
