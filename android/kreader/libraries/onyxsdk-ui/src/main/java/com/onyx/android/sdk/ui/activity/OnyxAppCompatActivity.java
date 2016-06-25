package com.onyx.android.sdk.ui.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.onyx.android.sdk.ui.R;


/**
 * Created by solskjaer49 on 16/6/22 16:32.
 */

public abstract class OnyxAppCompatActivity extends AppCompatActivity {
    final static String TAG = OnyxAppCompatActivity.class.getSimpleName();
    ActionBar actionBar;
    private boolean isCustomBackFunctionLayout = true;

    protected void initSupportActionBarWithCustomBackFunction() {
        initSupportActionBar(R.id.tool_bar, true, false, null);
    }

    protected void initSupportActionBarWithCustomBackFunction(int toolbarLayoutID) {
        initSupportActionBar(toolbarLayoutID, true, false, null);
    }

    protected void initSupportActionBar() {
        initSupportActionBar(R.id.tool_bar);
    }

    protected void initSupportActionBar(int toolbarLayoutID) {
        initSupportActionBar(toolbarLayoutID, false, false, null);
    }

    protected void initSupportActionBar(boolean customTitleEllipsize, TextUtils.TruncateAt truncateAtType) {
        initSupportActionBar(R.id.tool_bar, false, true, truncateAtType);
    }

    protected void initSupportActionBar(int toolbarLayoutID, boolean customBackFunctionLayout,
                              boolean customTitleEllipsize, TextUtils.TruncateAt truncateAtType) {
        Toolbar toolbar = (Toolbar) findViewById(toolbarLayoutID);
        if (toolbar == null) {
            return;
        }
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            actionBar = getSupportActionBar();
            isCustomBackFunctionLayout = customBackFunctionLayout;
            if (isCustomBackFunctionLayout) {
                actionBar.setDisplayShowTitleEnabled(false);
                actionBar.setDisplayHomeAsUpEnabled(false);
                actionBar.setDisplayShowHomeEnabled(false);
                actionBar.setDisplayUseLogoEnabled(false);
            } else {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (!isCustomBackFunctionLayout) {
                    onBackPressed();
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                if (actionBar == null) {
                    break;
                }
                actionBar.openOptionsMenu();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
