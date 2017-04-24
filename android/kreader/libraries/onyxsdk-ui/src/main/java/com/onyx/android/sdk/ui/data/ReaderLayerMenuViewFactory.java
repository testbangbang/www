package com.onyx.android.sdk.ui.data;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.sdk.data.ReaderMenu;
import com.onyx.android.sdk.data.ReaderMenuAction;
import com.onyx.android.sdk.data.ReaderMenuItem;
import com.onyx.android.sdk.data.ReaderMenuState;
import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joy on 6/28/16.
 */
public class ReaderLayerMenuViewFactory {

    public static int mainMenuContainerViewHeight = 0;
    private static ReaderMenuAction  selectedAction = null;

    private static class MainMenuItemViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private ReaderLayerMenuItem selectedItem;

        public MainMenuItemViewHolder(View itemView, final ReaderMenu.ReaderMenuCallback callback) {
            super(itemView);
            view = itemView;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedItem = (ReaderLayerMenuItem) v.getTag();
                    callback.onMenuItemClicked(selectedItem);
                }
            });
        }

        public void setMenuItem(ReaderLayerMenuItem item) {
            ImageView indicator = ((ImageView) view.findViewById(R.id.imageview_indicator));
            if(item !=null && item.getAction().equals(selectedAction)){
                indicator.setVisibility(View.VISIBLE);
            } else {
                indicator.setVisibility(View.INVISIBLE);
            }

            ((ImageView) view.findViewById(R.id.imageview_icon)).setImageResource(item.getDrawableResourceId());
            int titleResId = item.getTitleResourceId();
            TextView titleView = ((TextView) view.findViewById(R.id.textview_title));
            if (titleResId > 0) {
                titleView.setText(titleResId);
            } else {
                titleView.setVisibility(View.GONE);
            }
            view.setTag(item);
        }
    }

    public static View createMainMenuContainerView(final Context context, final List<ReaderLayerMenuItem> items, ReaderMenuState state, final ReaderMenu.ReaderMenuCallback callback, final boolean ignoreEmptyChildMenu) {
        List<ReaderLayerMenuItem> visibleItems = collectVisibleItems(items, ignoreEmptyChildMenu);
        final View view = createSimpleButtonContainerView(context, visibleItems, state, callback);
        view.post(new Runnable() {
            @Override
            public void run() {
                mainMenuContainerViewHeight = view.getMeasuredHeight();
            }
        });
        return view;
    }

    public static View createSubMenuContainerView(final Context context, final ReaderLayerMenuItem parent, final List<ReaderLayerMenuItem> items, final ReaderMenuState state, final boolean ignoreEmptyChildMenu, final ReaderMenu.ReaderMenuCallback callback) {
        if (parent.getChildren().size() == 0) {
            return null;
        }
        selectedAction = parent.getAction();
        List<ReaderLayerMenuItem> visibleItems = collectVisibleItems(items, ignoreEmptyChildMenu);
        View subView = createSimpleButtonContainerView(context, visibleItems, state, callback);
        if (mainMenuContainerViewHeight > 0) {
            subView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mainMenuContainerViewHeight));
        }
        return subView;
    }

    private static List<ReaderLayerMenuItem> collectVisibleItems(final List<? extends ReaderLayerMenuItem> items, final boolean ignoreEmptyChildMenu) {
        List<ReaderLayerMenuItem> result = new ArrayList<>();
        for (ReaderLayerMenuItem item : items) {
            if (!item.isVisible()) {
                continue;
            }
            if (!ignoreEmptyChildMenu &&
                    (item.getItemType() == ReaderMenuItem.ItemType.Group &&
                    collectVisibleItems(item.getChildren(), ignoreEmptyChildMenu).size() <= 0)) {
                continue;
            }
            result.add(item);
        }
        return result;
    }

    private static View createSimpleButtonContainerView(final Context context, final List<ReaderLayerMenuItem> items, final ReaderMenuState state, final ReaderMenu.ReaderMenuCallback callback) {
        final PageRecyclerView view = (PageRecyclerView) LayoutInflater.from(context).inflate(R.layout.reader_layer_menu_simple_button_container_recylerview, null);
        GridLayoutManager gridLayoutManager = new DisableScrollGridManager(context, 1);
        view.setDefaultMoveKeyBinding();
        view.setLayoutManager(gridLayoutManager);
        final LayoutInflater inflater = LayoutInflater.from(context);
        view.setAdapter(new PageRecyclerView.PageAdapter() {
            @Override
            public int getRowCount() {
                return 1;
            }

            @Override
            public int getColumnCount() {
                return 6;
            }

            @Override
            public int getDataCount() {
                return items.size();
            }

            @Override
            public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                return new MainMenuItemViewHolder(inflater.inflate(R.layout.reader_layer_menu_button_item, parent, false), callback);
            }

            @Override
            public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                MainMenuItemViewHolder viewHolder = ((MainMenuItemViewHolder) holder);
                viewHolder.setMenuItem(items.get(position));
            }
        });
        return view;
    }

    private static ReaderLayerMenuItem findItem(final List<ReaderLayerMenuItem> items, final ReaderMenuAction action, final int itemId) {
        for (ReaderLayerMenuItem item : items) {
            if (item.getAction() == action && item.getItemId() == itemId) {
                return item;
            }
        }
        return null;
    }
}
