package com.onyx.android.monitor.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.onyx.android.monitor.R;
import com.onyx.android.monitor.SingletonSharedPreference;
import com.onyx.android.monitor.event.ChangeOrientationEvent;
import com.onyx.android.monitor.event.DismissMenuEvent;
import com.onyx.android.monitor.event.FullRefreshEvent;
import com.onyx.android.monitor.event.SettingsChangedEvent;
import com.onyx.android.monitor.view.MenuItem;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.ui.view.ReaderLayerMenuLayout;
import com.onyx.android.sdk.ui.view.SeekBarWithEditTextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static com.onyx.android.monitor.view.MenuItem.MenuId.*;

/**
 * Created by wangxu on 17-7-24.
 */

public class DialogPreviewMenu extends Dialog {

    public static abstract class PreviewMenuCallback {
        public abstract void onMenuItemClicked(MenuItem menuItem);
        public abstract void MenuItemValueChanged(MenuItem menuItem, Object newValue);
    }

    private ReaderLayerMenuLayout menuLayout;
    private View mainMenuContainerView;
    private View subMenuContainerView;
    private MenuItem currentParentMenuItem = null;
    private PreviewMenuCallback privetCallback = new PreviewMenuCallback() {
        @Override
        public void onMenuItemClicked(MenuItem menuItem) {
            handleMenuItemClicked(menuItem);
        }

        @Override
        public void MenuItemValueChanged(MenuItem menuItem, Object newValue) {
            if (menuItem.getId() == A2) {
                SingletonSharedPreference.setGcIntervalTime(getContext(), (int)newValue);
            }
            EventBus.getDefault().post(new SettingsChangedEvent(menuItem.getId()));
        }
    };

    public DialogPreviewMenu(@NonNull Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);

        setContentView(R.layout.dialog_preview_menu);
        this.setCanceledOnTouchOutside(true);
        initContent();
    }

    private void handleMenuItemClicked(MenuItem menuItem) {
        switch (menuItem.getId()) {
            case FULL_REFRESH:
                EventBus.getDefault().post(new FullRefreshEvent());
                break;
            case EXIT:
                EventBus.getDefault().post(new DismissMenuEvent());
                break;
            case ORIENTATION:
                dismiss();
                EventBus.getDefault().post(new ChangeOrientationEvent());
                break;
            default:
                currentParentMenuItem = menuItem;
                updateContent();
                break;
        }
    }

    private void updateContent() {
        mainMenuContainerView = createMainMenuContainerView(initMainMenuContent(), privetCallback);
        subMenuContainerView = createSubMenuContainerView(privetCallback);
        menuLayout.updateMenuContent(mainMenuContainerView, subMenuContainerView);
    }

    private void initContent() {
        menuLayout = (ReaderLayerMenuLayout) findViewById(R.id.layout_preview_menu);
    }

    private List<MenuItem> initMainMenuContent() {
        List<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem(ORIENTATION, R.drawable.ic_rotate));
        menuItems.add(new MenuItem(A2, R.drawable.ic_a2));
        menuItems.add(new MenuItem(FULL_REFRESH, R.drawable.ic_regal));
        //menuItems.add(new MenuItem(BRIGHTNESS, R.drawable.ic_light));
        menuItems.add(new MenuItem(EXIT, R.drawable.ic_close));
        return menuItems;
    }

    private View createMainMenuContainerView(final List<MenuItem> items, final PreviewMenuCallback callback) {
        final PageRecyclerView view = (PageRecyclerView) LayoutInflater.from(getContext()).inflate(com.onyx.android.sdk.ui.R.layout.reader_layer_menu_simple_button_container_recylerview, null);
        GridLayoutManager gridLayoutManager = new DisableScrollGridManager(getContext(), 1);
        view.setDefaultMoveKeyBinding();
        view.setLayoutManager(gridLayoutManager);
        final LayoutInflater inflater = LayoutInflater.from(getContext());
        view.setAdapter(new PageRecyclerView.PageAdapter() {
            @Override
            public int getRowCount() {
                return 1;
            }

            @Override
            public int getColumnCount() {
                return items.size();
            }

            @Override
            public int getDataCount() {
                return items.size();
            }

            @Override
            public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                return new MainMenuItemViewHolder(inflater.inflate(R.layout.preview_menu_button_item, parent, false), callback);
            }

            @Override
            public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                MainMenuItemViewHolder viewHolder = (MainMenuItemViewHolder) holder;
                viewHolder.setMenuItem(items.get(position));
            }
        });
        return view;
    }

    private View createSubMenuContainerView(final PreviewMenuCallback callback) {
        if (currentParentMenuItem == null) {
            return null;
        }
        switch (currentParentMenuItem.getId()) {
            case CONTRAST:
                return createSubSeekBarMenuView(currentParentMenuItem, R.string.contrast_value, 20, 0, 100, callback);
            case A2:
                return createSubSeekBarMenuView(currentParentMenuItem, R.string.refresh_time, SingletonSharedPreference.getGcIntervalTime(getContext()), 1, 20, callback);
            case BRIGHTNESS:
                break;
            default:
                break;
        }
        return null;
    }


    private View createSubSeekBarMenuView(final MenuItem menuItem , final int stringResId, int initialValue, int minValue, int maxValue, final PreviewMenuCallback callback) {
        SeekBarWithEditTextView seekBarWithEditTextView = (SeekBarWithEditTextView) LayoutInflater.from(getContext()).inflate(R.layout.contrast_menu_layout, null);
        seekBarWithEditTextView.updateValue(stringResId, initialValue, minValue, maxValue);
        seekBarWithEditTextView.setCallback(new SeekBarWithEditTextView.Callback() {
            @Override
            public void valueChange(int newValue) {
                callback.MenuItemValueChanged(menuItem, newValue);
            }
        });
        return seekBarWithEditTextView;
    }

    private static class MainMenuItemViewHolder extends RecyclerView.ViewHolder {
        private View view;

        public MainMenuItemViewHolder(View itemView, final PreviewMenuCallback callback) {
            super(itemView);
            view = itemView;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onMenuItemClicked((MenuItem) v.getTag());
                }
            });
        }

        public void setMenuItem(MenuItem item) {
            ((ImageView) view.findViewById(R.id.imageview_icon)).setImageResource(item.getDrawableResourceId());
            view.setTag(item);
        }
    }

    @Override
    public void show() {
        updateContent();
        super.show();
    }
}
