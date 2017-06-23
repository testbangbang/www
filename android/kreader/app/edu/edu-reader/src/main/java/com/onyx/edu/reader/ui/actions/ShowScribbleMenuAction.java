package com.onyx.edu.reader.ui.actions;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.RectF;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.ReaderMenuAction;
import com.onyx.android.sdk.scribble.data.NoteDrawingArgs;
import com.onyx.android.sdk.ui.compat.AppCompatUtils;
import com.onyx.android.sdk.ui.dialog.DialogCustomLineWidth;
import com.onyx.android.sdk.ui.view.CommonViewHolder;
import com.onyx.android.sdk.ui.view.OnyxToolbar;
import com.onyx.android.sdk.utils.DimenUtils;
import com.onyx.edu.reader.R;
import com.onyx.edu.reader.note.actions.ChangeStrokeWidthAction;
import com.onyx.edu.reader.note.actions.FlushNoteAction;
import com.onyx.edu.reader.note.actions.RestoreShapeAction;
import com.onyx.edu.reader.note.data.ReaderNoteDataInfo;
import com.onyx.edu.reader.note.request.PauseDrawingRequest;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;
import com.onyx.edu.reader.ui.events.CloseScribbleMenuEvent;
import com.onyx.edu.reader.ui.events.ScribbleMenuChangedEvent;
import com.onyx.edu.reader.ui.events.UpdatePagePositionEvent;
import com.onyx.edu.reader.ui.events.UpdateScribbleMenuEvent;
import com.onyx.edu.reader.ui.handler.HandlerManager;

import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by ming on 16/9/22.
 */
public class ShowScribbleMenuAction extends BaseAction implements View.OnClickListener {

    public static abstract class ActionCallback {
        public abstract void onClicked(final ReaderMenuAction action);
        public abstract void onToggle(final ReaderMenuAction action, boolean expand);
    }

    private ViewGroup parent;
    private OnyxToolbar bottomToolbar;
    private OnyxToolbar topToolbar;
    private View fullToolbar;
    private HashMap<ReaderMenuAction, CommonViewHolder> scribbleViewHolderMap = new HashMap<>();
    private BaseCallback callback;
    private ReaderMenuAction selectWidthAction = ReaderMenuAction.SCRIBBLE_WIDTH1;
    private ReaderMenuAction selectShapeAction = ReaderMenuAction.SCRIBBLE_PENCIL;
    private ReaderMenuAction selectColorAction = ReaderMenuAction.SCRIBBLE_BLACK;
    private ReaderMenuAction selectEraserAction = null;
    private ActionCallback actionCallback;
    private ReaderDataHolder readerDataHolder;
    private boolean isDrag = false;
    private Set<ReaderMenuAction> disableMenuActions;
    private boolean showFullToolbar = false;
    private CommonViewHolder pagePositionHolder;

    public ShowScribbleMenuAction(ViewGroup parent,
                                  final ActionCallback actionCallback,
                                  Set<ReaderMenuAction> disableMenuActions,
                                  boolean showFullToolbar) {
        this.parent = parent;
        this.actionCallback = actionCallback;
        this.disableMenuActions = disableMenuActions;
        this.showFullToolbar = showFullToolbar;
    }

    public void execute(ReaderDataHolder readerDataHolder,  BaseCallback callback) {
        this.callback = callback;
        readerDataHolder.getEventBus().register(this);
        show(readerDataHolder);
        readerDataHolder.getHandlerManager().setActiveProvider(HandlerManager.SCRIBBLE_PROVIDER);
    }

    public void show(final ReaderDataHolder readerDataHolder) {
        this.readerDataHolder = readerDataHolder;
        topToolbar = createScribbleTopToolbar(readerDataHolder);
        parent.addView(topToolbar);

        bottomToolbar = createScribbleBottomToolbar(readerDataHolder);
        parent.addView(bottomToolbar);

        fullToolbar = createFullScreenToolbar(readerDataHolder);
        parent.addView(fullToolbar);

        fullToolbar.setVisibility(showFullToolbar ? View.VISIBLE : View.GONE);
        topToolbar.setVisibility(showFullToolbar ? View.GONE : View.VISIBLE);
        bottomToolbar.setVisibility(showFullToolbar ? View.GONE : View.VISIBLE);

        fullToolbar.post(new Runnable() {
            @Override
            public void run() {
                postMenuChangedEvent(readerDataHolder);
            }
        });

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
        toolbar.setAdjustLayoutForColorDevices(AppCompatUtils.isColorDevice(readerDataHolder.getContext()));
        final ReaderMenuAction[] expandedActions = {ReaderMenuAction.SCRIBBLE_WIDTH, ReaderMenuAction.SCRIBBLE_SHAPE, ReaderMenuAction.SCRIBBLE_ERASER};

        addImageViewTitleHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_scribble_shape, ReaderMenuAction.SCRIBBLE_SHAPE, R.string.shape);
        addImageViewTitleHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_scribble_width, ReaderMenuAction.SCRIBBLE_WIDTH, R.string.width);
        addImageViewTitleHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_scribble_eraser_part, ReaderMenuAction.SCRIBBLE_ERASER, R.string.eraser);
        addImageViewTitleHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_scribble_black, ReaderMenuAction.SCRIBBLE_COLOR, R.string.black);
        addImageViewTitleHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_scribble_undo, ReaderMenuAction.SCRIBBLE_UNDO, R.string.undo);
        addImageViewTitleHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_scribble_redo, ReaderMenuAction.SCRIBBLE_REDO, R.string.redo);
        addImageViewTitleHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_scribble_pack_up, ReaderMenuAction.SCRIBBLE_MINIMIZE, R.string.pack_up);

        toolbar.addViewHolder(new CommonViewHolder(OnyxToolbar.Builder.createSpaceView(readerDataHolder.getContext(), 1f)));

        addImageViewTitleHolder(toolbar, readerDataHolder.getContext(), R.drawable.prevbtn_bg, ReaderMenuAction.SCRIBBLE_PREV_PAGE, R.string.prev_page);

        String positionText = (readerDataHolder.getCurrentPage() + 1) + "/" + readerDataHolder.getPageCount();
        addPageTextViewHolder(toolbar, readerDataHolder.getContext(), readerDataHolder.getContext().getResources().getDimension(R.dimen.scribble_page_position_size), positionText, ReaderMenuAction.SCRIBBLE_PAGE_POSITION);
        addImageViewTitleHolder(toolbar, readerDataHolder.getContext(), R.drawable.nextbtn_bg, ReaderMenuAction.SCRIBBLE_NEXT_PAGE, R.string.next_page);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        int margin = (int) readerDataHolder.getContext().getResources().getDimension(R.dimen.menu_item_view_margin);
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

    private OnyxToolbar createScribbleTopToolbar(ReaderDataHolder readerDataHolder) {
        OnyxToolbar toolbar = new OnyxToolbar(readerDataHolder.getContext(), OnyxToolbar.Direction.Top, OnyxToolbar.FillStyle.WrapContent);

        toolbar.addViewHolder(new CommonViewHolder(OnyxToolbar.Builder.createSpaceView(readerDataHolder.getContext(), 1f)));
        addImageViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_scribble_save, ReaderMenuAction.SCRIBBLE_SAVE);
        addImageViewHolder(toolbar, readerDataHolder.getContext(), R.drawable.ic_exit, ReaderMenuAction.SCRIBBLE_CLOSE);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        int margin = (int) readerDataHolder.getContext().getResources().getDimension(R.dimen.menu_item_view_margin);
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

    private CommonViewHolder addMarkerViewHolder(OnyxToolbar toolbar, Context context, int imageResId, int selectResId, int layoutId, final ReaderMenuAction action) {
        if (disableMenuActions.contains(action)) {
            return null;
        }
        CommonViewHolder markerViewHolder = OnyxToolbar.Builder.createMarkerViewHolder(context, R.id.content_view, R.id.marker_view, imageResId, selectResId, layoutId, action);
        toolbar.addViewHolder(markerViewHolder);
        scribbleViewHolderMap.put(action, markerViewHolder);
        return markerViewHolder;
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

    private void addImageViewTitleHolder(OnyxToolbar toolbar, Context context, int imageResId, final ReaderMenuAction action, int titleResId) {
        if (disableMenuActions.contains(action)) {
            return;
        }
        CommonViewHolder viewHolder = OnyxToolbar.Builder.createImageViewTitleHolder(context, R.id.content_view, imageResId, R.id.title, titleResId, R.layout.tool_bar_image_title_view, action);
        toolbar.addViewHolder(viewHolder);
        scribbleViewHolderMap.put(action, viewHolder);
    }

    private void addPageTextViewHolder(OnyxToolbar toolbar, Context context, float textSize, String text, final ReaderMenuAction action) {
        if (disableMenuActions.contains(action)) {
            return;
        }
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(textSize);
        OnyxToolbar.Builder.setLayoutParams(context, textView, 0, 0);
        textView.setPadding(0, 0, 0, (int) context.getResources().getDimension(R.dimen.menu_text_padding_bottom));
        CommonViewHolder viewHolder = new CommonViewHolder(textView);
        textView.setTag(action);
        toolbar.addViewHolder(viewHolder);
        scribbleViewHolderMap.put(action, viewHolder);
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
        lp.addRule(RelativeLayout.ALIGN_LEFT);
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
                switchDragFunc(false);
                break;
            case SCRIBBLE_COLOR:
                switchStrokeColor();
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
                fullToolbar.post(new Runnable() {
                    @Override
                    public void run() {
                        postMenuChangedEvent(readerDataHolder);
                    }
                });
                break;
            case SCRIBBLE_MAXIMIZE:
                changeToolBarVisibility(false);
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
            case SCRIBBLE_CUSTOM_WIDTH:
                showCustomLineWidthDialog();
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
        switchDragFunc(true);
    }

    private void onSelectShape() {
        switchDragFunc(true);
        selectEraserAction = null;
        if (selectWidthAction == null) {
            float strokeWidth =  readerDataHolder.getNoteManager().getNoteDataInfo().getStrokeWidth();
            setSelectWidthAction(ShowReaderMenuAction.menuIdFromStrokeWidth(strokeWidth));
        }
    }

    private void onSelectWidth() {
        if (selectShapeAction == null || isDrag) {
            new RestoreShapeAction().execute(readerDataHolder, new BaseCallback() {
                @Override
                public void done(BaseRequest request, Throwable e) {
                    int currentShapeType = readerDataHolder.getNoteManager().getNoteDataInfo().getCurrentShapeType();
                    selectShapeAction = ShowReaderMenuAction.createShapeAction(currentShapeType);
                }
            });
        }
        switchDragFunc(true);
    }

    private void showCustomLineWidthDialog() {
        final PauseDrawingRequest pauseDrawingRequest = new PauseDrawingRequest(readerDataHolder.getVisiblePages());
        readerDataHolder.getNoteManager().submit(readerDataHolder.getContext(), pauseDrawingRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                DialogCustomLineWidth customLineWidth = new DialogCustomLineWidth(readerDataHolder.getContext(),
                        (int) readerDataHolder.getNoteManager().getNoteDataInfo().getStrokeWidth(),
                        NoteDrawingArgs.MAX_STROKE_WIDTH,
                        Color.BLACK, new DialogCustomLineWidth.Callback() {
                    @Override
                    public void done(int lineWidth) {
                        setSelectWidthAction(ReaderMenuAction.SCRIBBLE_CUSTOM_WIDTH);
                        useStrokeWidth(readerDataHolder, lineWidth);
                        onSelectWidth();
                    }
                });
                customLineWidth.show();
                customLineWidth.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        final List<PageInfo> pages = readerDataHolder.getVisiblePages();
                        new FlushNoteAction(pages, true, true, false, false).execute(readerDataHolder, null);
                    }
                });
            }
        });
    }

    private void switchDragFunc(boolean alwaysDisable){
        CommonViewHolder dragViewHolder = scribbleViewHolderMap.get(ReaderMenuAction.SCRIBBLE_DRAG);
        if (dragViewHolder == null) {
            return;
        }
        isDrag = !alwaysDisable && !isDrag;
        dragViewHolder.setImageResource(R.id.content_view,
                isDrag ? R.drawable.ic_drag : R.drawable.ic_drag_forbid);
    }

    private void switchStrokeColor(){
        ReaderNoteDataInfo noteDataInfo = readerDataHolder.getNoteManager().getNoteDataInfo();
        boolean isBlack = noteDataInfo.getStrokeColor() == Color.BLACK;
        ShowReaderMenuAction.useColor(readerDataHolder, isBlack ? Color.WHITE : Color.BLACK, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                updateStrokeColor();
            }
        });
    }

    private void updateStrokeColor() {
        final CommonViewHolder viewHolder = scribbleViewHolderMap.get(ReaderMenuAction.SCRIBBLE_COLOR);
        if (viewHolder == null) {
            return;
        }
        ReaderNoteDataInfo noteDataInfo = readerDataHolder.getNoteManager().getNoteDataInfo();
        boolean isBlack = noteDataInfo.getStrokeColor() == Color.BLACK;
        viewHolder.setImageResource(R.id.content_view, isBlack ? R.drawable.ic_scribble_black : R.drawable.ic_scribble_white);
        viewHolder.setText(R.id.title, isBlack ? R.string.black : R.string.white);
    }

    @Subscribe
    public void removeToolbar(CloseScribbleMenuEvent event) {
        parent.removeView(topToolbar);
        parent.removeView(bottomToolbar);
        parent.removeView(fullToolbar);
    }

    @Subscribe
    public void updateScribbleMenu(UpdateScribbleMenuEvent event) {
        postMenuChangedEvent(readerDataHolder);
    }

    @Subscribe
    public void updatePagePosition(UpdatePagePositionEvent event) {
        CommonViewHolder viewHolder = scribbleViewHolderMap.get(ReaderMenuAction.SCRIBBLE_PAGE_POSITION);
        if (viewHolder == null) {
            return;
        }
        TextView textView = (TextView) viewHolder.itemView.findViewWithTag(ReaderMenuAction.SCRIBBLE_PAGE_POSITION);
        if (textView == null) {
            return;
        }
        textView.setText((event.getPosition() + 1) + "/" + String.valueOf(event.getTotal()));
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
        RectF excludeRect = new RectF();
        if (bottomToolbar.getVisibility() == View.VISIBLE) {
            bottomOfTopToolBar = topToolbar.getBottom();
        }
        if (topToolbar.getVisibility() == View.VISIBLE) {
            topOfBottomToolBar = bottomToolbar.getTop();
        }

        if (fullToolbar.getVisibility() == View.VISIBLE) {
            excludeRect = new RectF(fullToolbar.getLeft(), fullToolbar.getTop(), fullToolbar.getRight(), fullToolbar.getBottom());
        }
        readerDataHolder.getEventBus().post(ScribbleMenuChangedEvent.create(bottomOfTopToolBar, topOfBottomToolBar, excludeRect));
    }

    private static void useStrokeWidth(final ReaderDataHolder readerDataHolder, float width) {
        final ActionChain actionChain = new ActionChain();
        final List<PageInfo> pages = readerDataHolder.getVisiblePages();
        actionChain.addAction(new FlushNoteAction(pages, true, true, false, false));
        actionChain.addAction(new ChangeStrokeWidthAction(width, true));
        actionChain.execute(readerDataHolder, null);
    }

    public void setSelectShapeAction(ReaderMenuAction selectShapeAction) {
        this.selectShapeAction = selectShapeAction;
    }

    public void setSelectWidthAction(ReaderMenuAction selectWidthAction) {
        this.selectWidthAction = selectWidthAction;
    }
}
