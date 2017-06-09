package com.onyx.android.sdk.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import com.onyx.android.sdk.ui.R;

/**
 * Created by joy on 6/28/16.
 */
public class ReaderLayerMenuLayout extends LinearLayout {

    private View menuDivider;
    private View subMenu;
    private IndicatorDividerView indicator;

    public ReaderLayerMenuLayout(Context context) {
        super(context);
    }

    public ReaderLayerMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReaderLayerMenuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void updateMenuContent(View mainMenu, View subMenu, int mainMenuPosition) {
        if (menuDivider == null) {
            menuDivider = LayoutInflater.from(getContext()).inflate(R.layout.reader_layer_menu_divider, null);
            indicator = (IndicatorDividerView) menuDivider.findViewById(R.id.indicator);
        }
        removeAllViewsInLayout();

        if (subMenu != null) {
            addView(subMenu);
            addView(menuDivider);
            indicator.setPosition(mainMenuPosition);
        }
        addView(mainMenu);
        this.subMenu = subMenu;
    }
}
