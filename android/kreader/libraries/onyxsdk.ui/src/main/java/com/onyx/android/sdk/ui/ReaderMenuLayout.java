package com.onyx.android.sdk.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.data.GAdapter;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.ui.data.DefaultTableViewCallback;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Joy on 2014/4/17.
 */
public class ReaderMenuLayout extends LinearLayout {
    public static abstract class ReaderMenuCallback {
        public abstract void onMenuItemClicked(GObject item);
        public abstract void preMenuItemClicked(GObject item);
        public void hideSubMenu(){}
        public void showSubMenu(){}
    }

    private ReaderMenuCallback mCallback = null;
    public void setCallback(ReaderMenuCallback callback) {
        mCallback = callback;
    }
    private void notifyMenuItemClicked(GObject item) {
        if (mCallback != null) {
            mCallback.onMenuItemClicked(item);
        }
    }

    private void notifyPreMenuItemClicked(GObject item) {
        if (mCallback != null) {
            mCallback.preMenuItemClicked(item);
        }
    }

    public OnyxTableView mMainMenuView = null;
    private OnyxTableView mSubMenuView = null;
    private LinkedHashMap<GObject, GAdapter> mMenus = null;
    private GObject mCurrentMenuGroup = null;
    private ImageView mItemIndicator;
    private TextView mTextViewIndicator;
    private GAdapter subMenuAdapter;
    final int rows = getContext().getResources().getInteger(R.integer.onyx_reader_menu_rows);
    final int mainMenuColumns=getContext().getResources().getInteger(R.integer.onyx_reader_main_menu_columns);
    final int subMenuColumns = getContext().getResources().getInteger(R.integer.onyx_reader_sub_menu_columns);
    public ReaderMenuLayout(Context context) {
        super(context);
        init();
    }

    public ReaderMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);

        mSubMenuView = new OnyxTableView(getContext());
        mSubMenuView.setupAsGridView(rows, subMenuColumns);
        addView(mSubMenuView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int)getContext().getResources().getDimension(R.dimen.onyx_reader_menu_sub_height)));

        mItemIndicator = new ImageView(getContext());
        mItemIndicator.setBackgroundResource(R.drawable.item_selected_6);
//        addView(mItemIndicator, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                (int)getContext().getResources().getDimension(R.dimen.onyx_reader_menu_indicator_height)));

        mTextViewIndicator = new TextView(getContext());
        mTextViewIndicator.setBackgroundColor(Color.BLACK);
        addView(mTextViewIndicator, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) getContext().getResources().getDimension(R.dimen.dialog_reader_menu_divider_height)));

        mMainMenuView = new OnyxTableView(getContext());
        mMainMenuView.setupAsGridView(rows, mainMenuColumns);
        addView(mMainMenuView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int)getContext().getResources().getDimension(R.dimen.onyx_reader_menu_main_height)));

        mMainMenuView.setCallback(new DefaultTableViewCallback() {
            @Override
            public void onItemClick(final OnyxTableItemView view) {
                showSubMenu();
                mCallback.showSubMenu();
                notifyPreMenuItemClicked(view.getData());
                if (mMenus.containsKey(view.getData())) {
                    updateItemIndicator(view.getData());
                    subMenuAdapter = mMenus.get(view.getData());
                    if (mMenus.get(view.getData()) != null) {
                        if (mSubMenuView.getVisibility() == View.VISIBLE) {
                            mSubMenuView.setAdapter(subMenuAdapter);
                        } else {
                            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                @Override
                                public void onGlobalLayout() {
                                    ReaderMenuLayout.this.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                    mSubMenuView.setAdapter(subMenuAdapter);
                                }
                            });
                        }
                    } else {
                        hideSubMenu();
                        mCallback.hideSubMenu();
                    }
                    notifyMenuItemClicked(view.getData());
                }
            }
        });

        mSubMenuView.setCallback(new DefaultTableViewCallback() {
            @Override
            public void onItemClick(OnyxTableItemView view) {
                notifyMenuItemClicked(view.getData());
            }
        });
    }

    public void setupMenu(LinkedHashMap<GObject, GAdapter> menus, GObject defaultMenu, ReaderMenuCallback callback,boolean isShowSubMenu) {
        mMenus = menus;
        mCallback = callback;
        GAdapter main_menu_adapter = GAdapter.createFromGObjectList(menus.keySet());
        mMainMenuView.setAdapter(main_menu_adapter);
        subMenuAdapter = menus.get(defaultMenu);
        mSubMenuView.setAdapter(subMenuAdapter);
        if (isShowSubMenu) {
            showSubMenu();
        }else {
            hideSubMenu();
        }
        updateItemIndicator(defaultMenu);
    }

    public GObject getCurrentMenuGroup() {
        return mCurrentMenuGroup;
    }

    public void updateItemIndicator(GObject object) {
        if (mMenus.get(object) == null) {
            return;
        }
        int keyIndex = 0;
        for (GObject temp : mMenus.keySet()) {
            mCurrentMenuGroup = temp;
            if (temp == object) {
                setIndicator(keyIndex);
                break;
            }
            keyIndex += 1;
        }
    }

    private void setIndicator(int index){
        switch (index) {
            case 0:
                mItemIndicator.setBackgroundResource(R.drawable.item_selected_1);
                break;
            case 1:
                mItemIndicator.setBackgroundResource(R.drawable.item_selected_2);
                break;
            case 2:
                mItemIndicator.setBackgroundResource(R.drawable.item_selected_3);
                break;
            case 3:
                mItemIndicator.setBackgroundResource(R.drawable.item_selected_4);
                break;
            case 4:
                mItemIndicator.setBackgroundResource(R.drawable.item_selected_5);
                break;
            case 5:
                mItemIndicator.setBackgroundResource(R.drawable.item_selected_6);
                break;
            default:
                break;
        }
    }

    public void hideSubMenu(){
        mSubMenuView.setVisibility(GONE);
        mTextViewIndicator.setVisibility(GONE);
        if (mCallback!=null){
            mCallback.hideSubMenu();
        }
    }

    public void showSubMenu(){
        mSubMenuView.setVisibility(VISIBLE);
        mTextViewIndicator.setVisibility(VISIBLE);
        this.requestLayout();
        if (mCallback!=null){
            mCallback.showSubMenu();
        }
    }

    /**
     * never return null
     *
     * @param tag
     * @param pattern
     * @return
     */
    public ArrayList<GObject> searchMenuByTag(String tag, Object pattern) {
        ArrayList<GObject> result = new ArrayList<GObject>();
        for (Map.Entry<GObject, GAdapter> entry : mMenus.entrySet()) {
            if (entry.getKey().matches(tag, pattern)) {
                result.add(entry.getKey());
            }
            if (entry.getValue() != null) {
                result.addAll(entry.getValue().searchByTag(tag, pattern));
            }
        }
        return result;
    }

    public GObject searchFirstMenuByTag(String tag, Object pattern) {
        ArrayList<GObject> result = searchMenuByTag(tag, pattern);
        if (result.size() <= 0) {
            return null;
        }
        return result.get(0);
    }

    public void setmMainMenuViewColumns(int mainMenuColumns){
        mMainMenuView.setupAsGridView(rows,mainMenuColumns);
    }
}
