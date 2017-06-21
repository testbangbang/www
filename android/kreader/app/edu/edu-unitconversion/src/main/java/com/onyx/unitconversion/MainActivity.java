package com.onyx.unitconversion;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;


import butterknife.Bind;
import butterknife.ButterKnife;

import static com.onyx.unitconversion.Config.UNIT_NAME_MAP;

public class MainActivity extends AppCompatActivity {

    @Bind(android.R.id.tabs)
    TabWidget tabWidget;
    @Bind(android.R.id.tabcontent)
    FrameLayout tabcontent;
    @Bind(R.id.tab_host)
    TabHost tabHost;
    @Bind(R.id.real_tab_content)
    FrameLayout realTabContent;

    private ConversionFragment conversionFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        getSupportActionBar().hide();
        initTabHost();
        initContent();
    }

    private void initContent() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        conversionFragment = ConversionFragment.newInstance();
        transaction.add(R.id.real_tab_content, conversionFragment);
        transaction.commit();
    }

    private void initTabHost() {
        tabHost.setup();
        tabHost.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                int index = getTabIndex(tabId);
                changeTabViewState(index);
                if (conversionFragment != null) {
                    conversionFragment.changeCategory(index);
                }
            }
        });
        for (Pair<UnitType, Integer> unitTypeIntegerPair : UNIT_NAME_MAP) {
            UnitType type = unitTypeIntegerPair.first;
            String name = getString(unitTypeIntegerPair.second);
            addTabToHost(type, name);
        }

        tabHost.post(new Runnable() {
            @Override
            public void run() {
                changeTabViewState(0);
            }
        });
    }

    private void changeTabViewState(int currentIndex) {
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            View tabIndicator = tabHost.getTabWidget().getChildTabViewAt(i);
            tabIndicator.findViewById(R.id.background_view).setVisibility(currentIndex == i ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private int getTabIndex(final String tabId) {
        for (int i = 0; i < UNIT_NAME_MAP.length; i++) {
            Pair<UnitType, Integer> pair = UNIT_NAME_MAP[i];
            UnitType type = pair.first;
            if (tabId.equals(type.name())) {
                return i;
            }
        }
        return 0;
    }

    private void addTabToHost(UnitType type, String name) {
        View tabIndicator = LayoutInflater.from(this).inflate(R.layout.view_unit_host_tab_indicator, null);
        TextView textView = (TextView) tabIndicator.findViewById(R.id.name);
        textView.setText(name);

        tabHost.addTab(tabHost.newTabSpec(type.name())
                .setIndicator(tabIndicator)
                .setContent(android.R.id.tabcontent));
    }

}
