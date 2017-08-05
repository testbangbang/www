package com.onyx.edu.reader.ui.actions.form;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.ReaderMenuAction;
import com.onyx.android.sdk.ui.compat.AppCompatUtils;
import com.onyx.android.sdk.ui.data.ReaderMenuViewData;
import com.onyx.android.sdk.ui.data.ReaderMenuViewHolder;
import com.onyx.android.sdk.ui.view.OnyxToolbar;
import com.onyx.edu.reader.R;
import com.onyx.edu.reader.device.DeviceConfig;
import com.onyx.edu.reader.ui.actions.ShowScribbleMenuAction;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;
import com.onyx.edu.reader.ui.events.CloseFormMenuEvent;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import static com.onyx.android.sdk.data.ReaderMenuAction.SCRIBBLE_NEXT_PAGE;
import static com.onyx.android.sdk.data.ReaderMenuAction.SCRIBBLE_PAGE_POSITION;
import static com.onyx.android.sdk.data.ReaderMenuAction.SCRIBBLE_PREV_PAGE;

/**
 * Created by ming on 2017/7/28.
 */

public abstract class BaseFormMenuAction extends BaseMenuAction {

    private List<ReaderMenuViewHolder> menuViewHolders = new ArrayList<>();
    private ReaderMenuViewData menuViewData;
    private ReaderDataHolder readerDataHolder;

    public OnyxToolbar bottomToolbar;
    private ShowScribbleMenuAction.ActionCallback actionCallback;

    public BaseFormMenuAction(ReaderMenuViewData menuViewData, ShowScribbleMenuAction.ActionCallback actionCallback) {
        this.menuViewData = menuViewData;
        this.actionCallback = actionCallback;
    }

    @Override
    public void show(ReaderDataHolder readerDataHolder) {
        menuViewHolders.clear();
        bottomToolbar = createScribbleBottomToolbar(readerDataHolder);
        getParent().removeAllViews();
        getParent().addView(bottomToolbar);
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, BaseCallback baseCallback) {
        super.execute(readerDataHolder, baseCallback);
        this.readerDataHolder = readerDataHolder;
        readerDataHolder.getEventBus().register(this);
    }

    protected OnyxToolbar createScribbleBottomToolbar(final ReaderDataHolder readerDataHolder) {
        OnyxToolbar toolbar = new OnyxToolbar(readerDataHolder.getContext(), OnyxToolbar.Direction.Bottom, OnyxToolbar.FillStyle.WrapContent);
        List<ReaderMenuViewHolder> bottomMenuViewHolders = getBottomMenuViewHolders(readerDataHolder, toolbar);
        if (bottomMenuViewHolders == null || bottomMenuViewHolders.size() == 0) {
            return null;
        }
        menuViewHolders.addAll(bottomMenuViewHolders);
        toolbar.setDividerViewHeight((int) readerDataHolder.getContext().getResources().getDimension(R.dimen.divider_view_height));
        toolbar.setAdjustLayoutForColorDevices(AppCompatUtils.isColorDevice(readerDataHolder.getContext()));

        for (ReaderMenuViewHolder bottomMenuViewHolder : bottomMenuViewHolders) {
            if (getReaderMenuViewData().getDisableMenuActions().contains(bottomMenuViewHolder.getMenuAction())) {
                continue;
            }
            toolbar.addViewHolder(bottomMenuViewHolder);
        }

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        int margin = (int) readerDataHolder.getContext().getResources().getDimension(R.dimen.menu_item_view_margin);
        toolbar.setMenuViewMargin(margin, 0, margin, 0);
        toolbar.setLayoutParams(lp);

        toolbar.setOnMenuClickListener(new OnyxToolbar.OnMenuClickListener() {
            @Override
            public OnyxToolbar OnClickListener(View view) {
                ReaderMenuAction action = (ReaderMenuAction) view.getTag();
                onMenuClicked(readerDataHolder, action);
                ReaderMenuViewHolder viewHolder = getMenuViewHolder(action);
                return createChildrenToolbar(readerDataHolder, getChildrenViewHolders(readerDataHolder, action), viewHolder, getChildrenSelectedAction(action));
            }
        });
        toolbar.setOnToggleToolbarListener(new OnyxToolbar.OnToggleToolbarListener() {
            @Override
            public void onToggle(Object tag, boolean expand) {
                ReaderMenuAction action = (ReaderMenuAction) tag;
                onToggleMenuGroup(readerDataHolder, action, expand);
            }
        });
        return toolbar;
    }

    @Override
    public void onToggleMenuGroup(ReaderDataHolder readerDataHolder, ReaderMenuAction action, boolean expand) {
        if (actionCallback != null) {
            actionCallback.onToggle(action, expand);
        }
    }

    @Override
    public void onMenuClicked(ReaderDataHolder readerDataHolder, ReaderMenuAction action) {
        if (actionCallback != null) {
            actionCallback.onClicked(action);
        }
    }

    private OnyxToolbar createChildrenToolbar(final ReaderDataHolder readerDataHolder, final List<ReaderMenuViewHolder> viewHolders, final ReaderMenuViewHolder parent, final ReaderMenuAction selectAction) {
        if (viewHolders == null || viewHolders.size() == 0) {
            return null;
        }

        menuViewHolders.addAll(viewHolders);
        final OnyxToolbar toolbar = new OnyxToolbar(readerDataHolder.getContext());
        toolbar.setAdjustLayoutForColorDevices(AppCompatUtils.isColorDevice(readerDataHolder.getContext()));
        toolbar.setClickedDismissToolbar(true);

        for (ReaderMenuViewHolder viewHolder : viewHolders) {
            if (getReaderMenuViewData().getDisableMenuActions().contains(viewHolder.getMenuAction())) {
                continue;
            }
            toolbar.addViewHolder(viewHolder);
        }

        updateChildrenMenuStateOnCreated(readerDataHolder, selectAction, viewHolders, parent);

        toolbar.setOnMenuClickListener(new OnyxToolbar.OnMenuClickListener() {
            @Override
            public OnyxToolbar OnClickListener(View view) {
                ReaderMenuAction action = (ReaderMenuAction) view.getTag();
                onMenuClicked(readerDataHolder, action);
                updateChildrenMenuStateOnClicked(readerDataHolder, action, viewHolders, parent);
                return null;
            }
        });
        return toolbar;
    }

    public ReaderMenuViewHolder createImageViewTitleHolder(Context context, int imageResId, final ReaderMenuAction action, int titleResId) {
        ReaderMenuViewHolder viewHolder = ReaderMenuViewHolder.create(OnyxToolbar.Builder.createImageViewTitleHolder(context, R.id.content_view, imageResId, R.id.title, titleResId, R.layout.tool_bar_image_title_view, action), action);
        viewHolder.setVisibility(R.id.title, DeviceConfig.sharedInstance(context).isShowMenuTitle() ? View.VISIBLE : View.GONE);
        viewHolder.setMenuAction(action);
        return viewHolder;
    }

    public ReaderMenuViewHolder createImageViewTitleHolder(Context context, int imageResId, final ReaderMenuAction action, int titleResId, boolean enable) {
        ReaderMenuViewHolder viewHolder = createImageViewTitleHolder(context, imageResId, action, titleResId);
        viewHolder.setEnabled(R.id.content_view, enable);
        viewHolder.setEnabled(R.id.title, enable);
        viewHolder.itemView.setEnabled(enable);
        return viewHolder;
    }

    public ReaderMenuViewHolder createPageTextViewHolder(Context context, float textSize, String text, final ReaderMenuAction action) {
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(textSize);

        OnyxToolbar.Builder.setLayoutParams(context, textView, 0, 0);
        textView.setPadding(0, 0, 0, DeviceConfig.sharedInstance(context).isShowMenuTitle() ? (int) context.getResources().getDimension(R.dimen.menu_text_padding_bottom) : 0);
        textView.setTag(action);
        return ReaderMenuViewHolder.create(textView, action);
    }

    public ReaderMenuViewHolder createMarkerViewHolder(Context context, int imageResId, int selectResId, int layoutId, final ReaderMenuAction action) {
        return ReaderMenuViewHolder.create(OnyxToolbar.Builder.createMarkerViewHolder(context, R.id.content_view, R.id.marker_view, imageResId, selectResId, layoutId, action), action);
    }

    public ReaderMenuViewHolder getMenuViewHolder(ReaderMenuAction action) {
        for (ReaderMenuViewHolder menuViewHolder : menuViewHolders) {
            ReaderMenuAction menuAction = menuViewHolder.getMenuAction();
            if (menuAction == null) {
                continue;
            }
            if (menuAction.equals(action)) {
                return menuViewHolder;
            }
        }
        return null;
    }

    public ReaderMenuViewData getReaderMenuViewData() {
        return menuViewData;
    }

    public ReaderDataHolder getReaderDataHolder() {
        return readerDataHolder;
    }

    protected ViewGroup getParent() {
        return getReaderMenuViewData().getParent();
    }

    @Subscribe
    public void close(CloseFormMenuEvent event) {
        getParent().removeView(bottomToolbar);
        getReaderDataHolder().getEventBus().unregister(this);
    }

    protected List<ReaderMenuViewHolder> getPageTextViewHolder() {
        List<ReaderMenuViewHolder> viewHolders = new ArrayList<>();
        viewHolders.add(createImageViewTitleHolder(readerDataHolder.getContext(), R.drawable.prevbtn_bg, SCRIBBLE_PREV_PAGE, R.string.prev_page));
        String positionText = (readerDataHolder.getCurrentPage() + 1) + "/" + readerDataHolder.getPageCount();
        viewHolders.add(createPageTextViewHolder(readerDataHolder.getContext(), readerDataHolder.getContext().getResources().getDimension(R.dimen.scribble_page_position_size), positionText, SCRIBBLE_PAGE_POSITION));
        viewHolders.add(createImageViewTitleHolder(readerDataHolder.getContext(), R.drawable.nextbtn_bg, SCRIBBLE_NEXT_PAGE, R.string.next_page));
        return viewHolders;
    }
}
