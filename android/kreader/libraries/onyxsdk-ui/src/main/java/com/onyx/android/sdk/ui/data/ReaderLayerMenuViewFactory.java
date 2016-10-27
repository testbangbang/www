package com.onyx.android.sdk.ui.data;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by joy on 6/28/16.
 */
public class ReaderLayerMenuViewFactory {

    public static int mainMenuContainerViewHeight = 0;

    private static class MainMenuItemViewHolder extends RecyclerView.ViewHolder {
        private View view;

        public MainMenuItemViewHolder(View itemView, final ReaderMenu.ReaderMenuCallback callback) {
            super(itemView);
            view = itemView;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onMenuItemClicked((ReaderLayerMenuItem) v.getTag());
                }
            });
        }

        public void setMenuItem(ReaderLayerMenuItem item) {
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

    public static View createMainMenuContainerView(final Context context, final List<ReaderLayerMenuItem> items, ReaderMenuState state, final ReaderMenu.ReaderMenuCallback callback) {
        List<ReaderLayerMenuItem> visibleItems = collectVisibleItems(items);
        final View view = createSimpleButtonContainerView(context, visibleItems, state, callback);
        view.post(new Runnable() {
            @Override
            public void run() {
                mainMenuContainerViewHeight = view.getMeasuredHeight();
            }
        });
        return view;
    }

    public static View createSubMenuContainerView(final Context context, final ReaderLayerMenuItem parent, final List<ReaderLayerMenuItem> items, final ReaderMenuState state, final ReaderMenu.ReaderMenuCallback callback) {
        if (parent.getAction() == ReaderMenuAction.FONT) {
            return createFontStyleView(context, items, state, callback);
        }

        List<ReaderLayerMenuItem> visibleItems = collectVisibleItems(items);
        View subView = createSimpleButtonContainerView(context, visibleItems, state, callback);
        if (mainMenuContainerViewHeight > 0) {
            subView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mainMenuContainerViewHeight));
        }
        return subView;
    }

    private static List<ReaderLayerMenuItem> collectVisibleItems(final List<? extends ReaderLayerMenuItem> items) {
        List<ReaderLayerMenuItem> result = new ArrayList<>();
        for (ReaderLayerMenuItem item : items) {
            if (!item.isVisible()) {
                continue;
            }
            if (item.getItemType() == ReaderMenuItem.ItemType.Group &&
                    collectVisibleItems(item.getChildren()).size() <= 0) {
                continue;
            }
            result.add(item);
        }
        return result;
    }

    private static View createSimpleButtonContainerView(final Context context, final List<ReaderLayerMenuItem> items, final ReaderMenuState state, final ReaderMenu.ReaderMenuCallback callback) {
        final RecyclerView view = (RecyclerView) LayoutInflater.from(context).inflate(R.layout.reader_layer_menu_simple_button_container_recylerview, null);
        GridLayoutManager gridLayoutManager = new DisableScrollGridManager(context, 1);
        gridLayoutManager.setSpanCount(6);
        view.setLayoutManager(gridLayoutManager);
        final LayoutInflater inflater = LayoutInflater.from(context);
        view.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new MainMenuItemViewHolder(inflater.inflate(R.layout.reader_layer_menu_button_item, parent, false), callback);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                ((MainMenuItemViewHolder) holder).setMenuItem(items.get(position));
            }

            @Override
            public int getItemCount() {
                return items.size();
            }
        });
        return view;
    }

    private static ReaderLayerMenuItem findItem(final List<ReaderLayerMenuItem> items, final ReaderMenuAction action) {
        for (ReaderLayerMenuItem item : items) {
            if (item.getAction() == action) {
                return item;
            }
        }
        return null;
    }

    private static final HashMap<Integer, ReaderMenuAction> fontSizeViewItemMap;
    private static final HashMap<Integer, ReaderMenuAction> fontStyleViewItemMap;

    static {
        fontSizeViewItemMap = new HashMap<>();
        fontSizeViewItemMap.put(R.id.text_view_font_size_0, ReaderMenuAction.FONT_SET_FONT_SIZE);
        fontSizeViewItemMap.put(R.id.text_view_font_size_1, ReaderMenuAction.FONT_SET_FONT_SIZE);
        fontSizeViewItemMap.put(R.id.text_view_font_size_2, ReaderMenuAction.FONT_SET_FONT_SIZE);
        fontSizeViewItemMap.put(R.id.text_view_font_size_3, ReaderMenuAction.FONT_SET_FONT_SIZE);
        fontSizeViewItemMap.put(R.id.text_view_font_size_4, ReaderMenuAction.FONT_SET_FONT_SIZE);
        fontSizeViewItemMap.put(R.id.text_view_font_size_5, ReaderMenuAction.FONT_SET_FONT_SIZE);
        fontSizeViewItemMap.put(R.id.text_view_font_size_6, ReaderMenuAction.FONT_SET_FONT_SIZE);
        fontSizeViewItemMap.put(R.id.text_view_font_size_7, ReaderMenuAction.FONT_SET_FONT_SIZE);

        fontStyleViewItemMap = new HashMap<>();
        fontStyleViewItemMap.putAll(fontSizeViewItemMap);
        fontStyleViewItemMap.put(R.id.image_view_decrease_font_size, ReaderMenuAction.FONT_DECREASE_FONT_SIE);
        fontStyleViewItemMap.put(R.id.image_view_increase_font_size, ReaderMenuAction.FONT_INCREASE_FONT_SIZE);
        fontStyleViewItemMap.put(R.id.button_set_font_face, ReaderMenuAction.FONT_SET_FONT_FACE);
        fontStyleViewItemMap.put(R.id.image_view_indent, ReaderMenuAction.FONT_SET_INTENT);
        fontStyleViewItemMap.put(R.id.image_view_no_indent, ReaderMenuAction.FONT_SET_NO_INTENT);
        fontStyleViewItemMap.put(R.id.image_view_small_line_spacing, ReaderMenuAction.FONT_SET_SMALL_LINE_SPACING);
        fontStyleViewItemMap.put(R.id.image_view_middle_line_spacing, ReaderMenuAction.FONT_SET_MIDDLE_LINE_SPACING);
        fontStyleViewItemMap.put(R.id.image_view_large_line_spacing, ReaderMenuAction.FONT_SET_LARGE_LINE_SPACING);
        fontStyleViewItemMap.put(R.id.image_view_decrease_line_spacing, ReaderMenuAction.FONT_DECREASE_LINE_SPACING);
        fontStyleViewItemMap.put(R.id.image_view_increase_line_spacing, ReaderMenuAction.FONT_INCREASE_LINE_SPACING);
        fontStyleViewItemMap.put(R.id.image_view_small_page_margins, ReaderMenuAction.FONT_SET_SMALL_PAGE_MARGINS);
        fontStyleViewItemMap.put(R.id.image_view_middle_page_margins, ReaderMenuAction.FONT_SET_MIDDLE_LINE_SPACING);
        fontStyleViewItemMap.put(R.id.image_view_large_page_margins, ReaderMenuAction.FONT_SET_LARGE_LINE_SPACING);
        fontStyleViewItemMap.put(R.id.image_view_decrease_page_margins, ReaderMenuAction.FONT_DECREASE_PAGE_MARGINS);
        fontStyleViewItemMap.put(R.id.image_view_increase_page_margins, ReaderMenuAction.FONT_INCREASE_PAGE_MARGINS);
    }

    private static void mapFontStyleViewMenuItemFunction(final View fontStyleView, final List<ReaderLayerMenuItem> items, final ReaderMenuState state, final ReaderMenu.ReaderMenuCallback callback) {
        for (final HashMap.Entry<Integer, ReaderMenuAction> entry : fontStyleViewItemMap.entrySet()) {
            final View view = fontStyleView.findViewById(entry.getKey());
            final ReaderLayerMenuItem item = findItem(items, entry.getValue());
            if (view == null || item == null) {
                assert false;
                continue;
            }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fontSizeViewItemMap.containsKey(entry.getKey())) {
                        // TODO pass back font size value
                        callback.onMenuItemClicked(item);
                    } else {
                        callback.onMenuItemClicked(item);
                    }
                }
            });
        }
    }

    private static View createFontStyleView(final Context context, final List<ReaderLayerMenuItem> items, final ReaderMenuState state, final ReaderMenu.ReaderMenuCallback callback) {
        View view = LayoutInflater.from(context).inflate(R.layout.reader_layer_menu_font_style_view, null);
        mapFontStyleViewMenuItemFunction(view, items, state, callback);
        return view;
    }

}
