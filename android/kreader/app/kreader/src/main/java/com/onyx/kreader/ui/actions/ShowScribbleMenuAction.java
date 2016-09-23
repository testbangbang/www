package com.onyx.kreader.ui.actions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.onyx.android.sdk.data.ScribbleMenuAction;
import com.onyx.android.sdk.ui.view.OnyxToolbar;
import com.onyx.android.sdk.ui.view.viewholder.BaseViewHolder;
import com.onyx.android.sdk.ui.view.viewholder.SimpleSelectViewHolder;
import com.onyx.android.sdk.utils.DimenUtils;
import com.onyx.kreader.R;
import com.onyx.kreader.ui.data.ReaderDataHolder;

import java.util.HashMap;

/**
 * Created by ming on 16/9/22.
 */
public class ShowScribbleMenuAction implements View.OnClickListener {

    private static final String TAG = ShowScribbleMenuAction.class.getSimpleName();

    public interface CallBack {
        void onDismiss();
    }

    private ViewGroup parent;
    private OnyxToolbar bottomToolbar;
    private OnyxToolbar topToolbar;
    private View fullToolbar;
    private HashMap<ScribbleMenuAction, BaseViewHolder> scribbleViewHolderMap = new HashMap<>();
    private CallBack callback;
    private ScribbleMenuAction selectWidthAction = ScribbleMenuAction.WIDTH1;
    private ScribbleMenuAction selectShapeAction = ScribbleMenuAction.PENCIL;
    private ScribbleMenuAction selectEraserAction = ScribbleMenuAction.ERASER_PART;

    public ShowScribbleMenuAction(ViewGroup parent, CallBack callback) {
        this.parent = parent;
        this.callback = callback;
    }

    public static void showScribbleMenu(ReaderDataHolder readerDataHolder, ViewGroup parent, CallBack callback) {
        ShowScribbleMenuAction showScribbleMenuAction = new ShowScribbleMenuAction(parent, callback);
        showScribbleMenuAction.show(readerDataHolder);
    }

    public void show(ReaderDataHolder readerDataHolder) {
        topToolbar = createScribbleTopToolbar(readerDataHolder);
        parent.addView(topToolbar);

        bottomToolbar = createScribbleBottomToolbar(readerDataHolder);
        parent.addView(bottomToolbar);

        fullToolbar = createFullScreenToolbar(readerDataHolder);
        parent.addView(fullToolbar);
        fullToolbar.setVisibility(View.GONE);
    }

    private OnyxToolbar createScribbleBottomToolbar(final ReaderDataHolder readerDataHolder) {
        OnyxToolbar toolbar = new OnyxToolbar(readerDataHolder.getContext(), OnyxToolbar.Direction.Bottom, OnyxToolbar.FillStyle.WrapContent);
        final ScribbleMenuAction[] expandedActions = {ScribbleMenuAction.WIDTH, ScribbleMenuAction.SHAPE, ScribbleMenuAction.ERASER};

        addSelectViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_width, R.drawable.ic_triangle, R.layout.scribble_bottom_menu_item_view, ScribbleMenuAction.WIDTH);
        addSelectViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_shape, R.drawable.ic_triangle, R.layout.scribble_bottom_menu_item_view, ScribbleMenuAction.SHAPE);
        addSelectViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_txt, R.drawable.ic_triangle, R.layout.scribble_bottom_menu_item_view, ScribbleMenuAction.TEXT);
        addSelectViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_eraser, R.drawable.ic_triangle, R.layout.scribble_bottom_menu_item_view, ScribbleMenuAction.ERASER);
        addSelectViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_drag, R.drawable.ic_triangle, R.layout.scribble_bottom_menu_item_view, ScribbleMenuAction.DRAG);
        addSelectViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_pack_up, R.drawable.ic_triangle, R.layout.scribble_bottom_menu_item_view, ScribbleMenuAction.PACK_UP);

        toolbar.addViewHolder(new BaseViewHolder(OnyxToolbar.Builder.createSpaceView(readerDataHolder.getContext(), 1f)));

        addSelectViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_page_arrow_left, R.drawable.ic_triangle, R.layout.scribble_bottom_menu_item_view, ScribbleMenuAction.LEFT_PAGE);
        addSelectViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_page_arrow_right, R.drawable.ic_triangle, R.layout.scribble_bottom_menu_item_view, ScribbleMenuAction.RIGHT_PAGE);

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
        final ScribbleMenuAction[] selectActions = {ScribbleMenuAction.PENCIL, ScribbleMenuAction.BRUSH, ScribbleMenuAction.LINE, ScribbleMenuAction.TRIGON, ScribbleMenuAction.CIRCLE, ScribbleMenuAction.SQUARE};

        addSelectViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_shape_pencil, R.drawable.ic_dot, R.layout.scribble_expand_menu_item_view, ScribbleMenuAction.PENCIL);
        addSelectViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_shape_brush, R.drawable.ic_dot, R.layout.scribble_expand_menu_item_view, ScribbleMenuAction.BRUSH);
        addSelectViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_shape_line, R.drawable.ic_dot, R.layout.scribble_expand_menu_item_view, ScribbleMenuAction.LINE);
        addSelectViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_shape_trigon, R.drawable.ic_dot, R.layout.scribble_expand_menu_item_view, ScribbleMenuAction.TRIGON);
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
        leftPage.setTag(ScribbleMenuAction.LEFT_PAGE);
        rightPage.setTag(ScribbleMenuAction.RIGHT_PAGE);
        restore.setTag(ScribbleMenuAction.RESTORE);

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

        switch (action) {
            case WIDTH:
                break;
            case SHAPE:
                break;
            case CLOSE:
                if (callback != null) {
                    removeToolbar();
                    callback.onDismiss();
                }
                break;
            case PACK_UP:
                changeToolBarVisibility(true);
                break;
            case RESTORE:
                changeToolBarVisibility(false);
        }
    }

    private void removeToolbar() {
        parent.removeView(topToolbar);
        parent.removeView(bottomToolbar);
        parent.removeView(fullToolbar);
    }

    private void changeToolBarVisibility(boolean packUp) {
        topToolbar.setVisibility(packUp ? View.GONE : View.VISIBLE);
        bottomToolbar.setVisibility(packUp ? View.GONE : View.VISIBLE);
        fullToolbar.setVisibility(packUp ? View.VISIBLE : View.GONE);
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
}
