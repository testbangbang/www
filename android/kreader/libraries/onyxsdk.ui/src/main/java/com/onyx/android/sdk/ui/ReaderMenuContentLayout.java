package com.onyx.android.sdk.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.data.GAdapter;
import com.onyx.android.sdk.data.GObject;

import java.util.LinkedHashMap;

/**
 * Created by solskjaer49 on 15/7/13 12:14.
 */
public class ReaderMenuContentLayout extends LinearLayout {

    public void setMainMenuViewColumns(int integer) {
        if (mMainMenuView != null && integer != mainMenuColumns) {
            mMainMenuView.setupGridLayout(rows, integer);
        }
    }

    public void setSubMenuViewColumns(int integer) {
        if (mSubMenuView != null && integer != subMenuColumns) {
            mSubMenuView.setupGridLayout(rows, integer);
        }
    }

    public static abstract class ReaderMenuCallback {
        public abstract void onMenuItemClicked(GObject item);

        public void hideSubMenu() {
        }

        public void showSubMenu() {
        }
    }

    private ReaderMenuCallback mCallback = null;

    public ContentView getMainMenuView() {
        return mMainMenuView;
    }

    private ContentView mMainMenuView;

    public ContentView getSubMenuView() {
        return mSubMenuView;
    }

    private ContentView mSubMenuView;
    private View mMenuDivider;
    private LinkedHashMap<GObject, GAdapter> mMainMenuHashMap = null;
    private GObject mCurrentMenuGroup = null;
    private GAdapter subMenuAdapter;
    final int rows = getContext().getResources().getInteger(R.integer.onyx_reader_menu_rows);
    final int mainMenuColumns = getContext().getResources().getInteger(R.integer.onyx_reader_main_menu_columns);
    final int subMenuColumns = getContext().getResources().getInteger(R.integer.onyx_reader_sub_menu_columns);

    public void setCallback(ReaderMenuCallback callback) {
        mCallback = callback;
    }

    private void notifyMenuItemClicked(GObject item) {
        if (mCallback != null) {
            mCallback.onMenuItemClicked(item);
        }
    }

    public ReaderMenuContentLayout(Context context) {
        this(context, null);
    }

    public ReaderMenuContentLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        final LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.reader_menu_content_layout, this, true);
        mMainMenuView = (ContentView) findViewById(R.id.main_menu_content_view);
        mSubMenuView = (ContentView) findViewById(R.id.submenu_content_view);
        mMenuDivider = findViewById(R.id.menu_divider);
        mMainMenuView.setShowPageInfoArea(false);
        mSubMenuView.setShowPageInfoArea(false);
        mMainMenuView.setBlankAreaAnswerLongClick(false);
        mSubMenuView.setBlankAreaAnswerLongClick(false);
        mMainMenuView.setupGridLayout(rows, mainMenuColumns);
        mSubMenuView.setupGridLayout(rows, subMenuColumns);
        mMainMenuView.setCallback(new ContentView.ContentViewCallback() {
            @Override
            public void onItemClick(ContentItemView view) {
                super.onItemClick(view);
                showSubMenu();
                mCallback.showSubMenu();
                if (mMainMenuHashMap.containsKey(view.getData())) {
                    subMenuAdapter = mMainMenuHashMap.get(view.getData());
                    if (subMenuAdapter != null) {
//                        if (mSubMenuView.getVisibility() == View.VISIBLE) {
                        mSubMenuView.setAdapter(subMenuAdapter, 0);
//                        } else {
//                            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                                @Override
//                                public void onGlobalLayout() {
//                                    ReaderMenuLayout.this.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                                    mSubMenuView.setAdapter(subMenuAdapter,0);
//                                }
//                            });
//                        }
                    } else {
                        hideSubMenu();
                        mCallback.hideSubMenu();
                    }
                    mCallback.onMenuItemClicked(view.getData());
                }
            }
        });
        mSubMenuView.setCallback(new ContentView.ContentViewCallback() {
            @Override
            public void onItemClick(ContentItemView view) {
                super.onItemClick(view);
                mCallback.onMenuItemClicked(view.getData());
            }
        });
    }

    public void hideSubMenu() {
        mSubMenuView.setVisibility(GONE);
        mMenuDivider.setVisibility(GONE);
        if (mCallback != null) {
            mCallback.hideSubMenu();
        }
    }

    public void showSubMenu() {
        mSubMenuView.setVisibility(VISIBLE);
        mMenuDivider.setVisibility(VISIBLE);
        this.requestLayout();
        if (mCallback != null) {
            mCallback.showSubMenu();
        }
    }

    public void setupMenu(LinkedHashMap<GObject, GAdapter> menus, GObject defaultMenu, ReaderMenuCallback callback, boolean isShowSubMenu) {
        mMainMenuHashMap = menus;
        mCallback = callback;
        GAdapter main_menu_adapter = GAdapter.createFromGObjectList(menus.keySet());
        mMainMenuView.setAdapter(main_menu_adapter, 0);
        subMenuAdapter = menus.get(defaultMenu);
        mSubMenuView.setAdapter(subMenuAdapter, 0);
        if (isShowSubMenu) {
            showSubMenu();
        } else {
            hideSubMenu();
        }
    }

}
