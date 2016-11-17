package com.onyx.android.sdk.ui.data;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.onyx.android.sdk.data.ReaderMenu;
import com.onyx.android.sdk.data.ReaderMenuAction;
import com.onyx.android.sdk.data.ReaderMenuItem;
import com.onyx.android.sdk.data.ReaderMenuState;
import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.ui.dialog.DialogFontChoose;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.OnyxRadioButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by joy on 6/28/16.
 */
public class ReaderLayerMenuViewFactory {

    public static int mainMenuContainerViewHeight = 0;
    public static int maxFontFaceCountShow = 6;

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

    private static ReaderLayerMenuItem findItem(final List<ReaderLayerMenuItem> items, final ReaderMenuAction action, final int itemId) {
        for (ReaderLayerMenuItem item : items) {
            if (item.getAction() == action && item.getItemId() == itemId) {
                return item;
            }
        }
        return null;
    }

    private static void mapFontStyleViewMenuItemFunction(final Context context, final View fontStyleView, final List<ReaderLayerMenuItem> items, final ReaderMenuState state, final ReaderMenu.ReaderMenuCallback callback) {
        for (final ReaderMenuItem fontMenuItem : items) {
            final View view = fontStyleView.findViewById(fontMenuItem.getItemId());
            final ReaderLayerMenuItem item = findItem(items, fontMenuItem.getAction(), fontMenuItem.getItemId());
            if (view != null) {
                view.setVisibility(item == null ? View.GONE : View.VISIBLE);
            }

            if (view == null || item == null) {
                continue;
            }
            view.setActivated(item.isSelected());

            final ReaderMenuAction action = fontMenuItem.getAction();
            if (action == ReaderMenuAction.FONT_SET_FONT_SIZE) {
                TextView textView = (TextView) view;
                Object value = fontMenuItem.getValue();
                if (value != null) {
                    textView.setTextSize((float) value);
                }
            }
            if (action == ReaderMenuAction.FONT_SET_FONT_FACE) {
                RadioGroup fontFace = (RadioGroup) view;
                fillFontFaces(context, fontFace, state.getFontFaces());
                fontFace.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                        callback.onMenuItemValueChanged(item, null, radioButton.getText());
                    }
                });
                continue;
            }
            if (action == ReaderMenuAction.FONT_SET_MORE_FONT_FACE) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showMoreFontDialog(context, state.getFontFaces(), new DialogFontChoose.OnChooseListener() {
                            @Override
                            public void onFinishChoose(String fontFace) {
                                callback.onMenuItemValueChanged(item, null, fontFace);
                            }
                        });
                    }
                });
                continue;
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clearMenuItemState(items, action);
                    item.setSelected(true);
                    updateFontStyleView(fontStyleView, items);
                    callback.onMenuItemValueChanged(item, null, item.getValue());
                }
            });
        }
    }

    private static void clearMenuItemState(final List<ReaderLayerMenuItem> items, ReaderMenuAction action) {
        for (ReaderLayerMenuItem item : items) {
            if (item.getAction() == action) {
                item.setSelected(false);
            }
        }
    }

    private static void updateFontStyleView(final View fontStyleView, final List<ReaderLayerMenuItem> items) {
        for (final ReaderLayerMenuItem fontMenuItem : items) {
            final View view = fontStyleView.findViewById(fontMenuItem.getItemId());
            final ReaderLayerMenuItem item = findItem(items, fontMenuItem.getAction(), fontMenuItem.getItemId());
            if (view != null && item != null) {
                view.setActivated(item.isSelected());
            }
        }
    }

    private static View createFontStyleView(final Context context, final List<ReaderLayerMenuItem> items, final ReaderMenuState state, final ReaderMenu.ReaderMenuCallback callback) {
        View view = LayoutInflater.from(context).inflate(R.layout.reader_layer_menu_font_style_view, null);
        mapFontStyleViewMenuItemFunction(context, view, items, state, callback);
        return view;
    }

    private static void fillFontFaces(Context context, RadioGroup radioGroup, List<String> fontFaces) {
        if (fontFaces == null) {
            return;
        }
        for (int i = 0; i < maxFontFaceCountShow; i++) {
            if (i < fontFaces.size()) {
                OnyxRadioButton radioButton = OnyxRadioButton.Create(context, fontFaces.get(i));
                radioGroup.addView(radioButton);
            }
        }
    }

    private static void showMoreFontDialog(final Context context, final List<String> fontFaces, final DialogFontChoose.OnChooseListener onChooseListener) {
        DialogFontChoose fontChoose  = new DialogFontChoose(context, fontFaces, onChooseListener);
        fontChoose.show();
    }
}
