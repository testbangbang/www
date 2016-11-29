package com.onyx.android.sdk.ui.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.ui.dialog.DialogProgressHolder;
import com.onyx.android.sdk.ui.dialog.DialogProgressHolder.DialogCancelListener;
import com.onyx.android.sdk.ui.utils.ScreenSpecUtil;

/**
 * Created by solskjaer49 on 16/6/22 16:32.
 */

public abstract class OnyxAppCompatActivity extends AppCompatActivity {
    final static String TAG = OnyxAppCompatActivity.class.getSimpleName();
    ActionBar actionBar;
    private boolean isCustomBackFunctionLayout = true;
    protected RelativeLayout backFunctionLayout;

    protected DialogProgressHolder progressDialogHolder = new DialogProgressHolder();;

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
            if (ScreenSpecUtil.isXLargeTablet(this)) {
                toolbar.setPadding(0, 0, 0, 0);
            }
            if (isCustomBackFunctionLayout) {
                actionBar.setDisplayShowTitleEnabled(false);
                actionBar.setDisplayHomeAsUpEnabled(false);
                actionBar.setDisplayShowHomeEnabled(false);
                actionBar.setDisplayUseLogoEnabled(false);
                backFunctionLayout= (RelativeLayout) findViewById(R.id.back_function_layout);
                if (backFunctionLayout != null) {
                    backFunctionLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onBackPressed();
                        }
                    });
                }
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

    protected void showToast(int resId, int duration) {
        showToast(getString(resId), duration);
    }

    protected void showToast(String message, int duration) {
        Toast.makeText(this, message, duration).show();
    }

    protected void showWhiteToast(int resId, int duration) {
        showWhiteToast(getString(resId), duration);
    }

    protected void showWhiteToast(String message, int duration) {
        TextView textView = new TextView(this);
        textView.setBackgroundResource(R.drawable.toast_white_background);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23);
        textView.setText(message);
        Toast toast = new Toast(this);
        toast.setDuration(duration);
        toast.setView(textView);
        toast.show();
    }

    public void showProgressDialog(final Object object, int resId, DialogCancelListener listener) {
        if (progressDialogHolder == null) {
            return;
        }
        progressDialogHolder.showProgressDialog(this, object, resId, listener);
    }

    public void showProgressDialog(final Object object, DialogCancelListener listener) {
        showProgressDialog(object, R.string.loading, listener);
    }

    public void setProgressDialogProgressMessage(final Object object, int resourceId) {
        setProgressDialogProgressMessage(object, getString(resourceId));
    }

    public void showCloudProgressDialogWithMessage(final Object object, int resourceId, DialogCancelListener listener) {
        if (progressDialogHolder == null) {
            return;
        }
        progressDialogHolder.showProgressDialog(this, object, resourceId, listener).setProgressMessageInvisible();
    }

    public void setProgressDialogProgressMessage(final Object object, final String message) {
        if (progressDialogHolder != null && progressDialogHolder.getProgressDialogFromRequest(object) != null) {
            progressDialogHolder.getProgressDialogFromRequest(object).setProgressMessage(message);
        }
    }

    public void setProgressDialogToastMessage(final Object object, int resourceId) {
        setProgressDialogToastMessage(object, getString(resourceId));
    }

    public void setProgressDialogToastMessage(final Object object, final String message) {
        if (progressDialogHolder != null && progressDialogHolder.getProgressDialogFromRequest(object) != null) {
            progressDialogHolder.getProgressDialogFromRequest(object).setToastMessage(message);
        }
    }

    public void dismissProgressDialog(final Object object) {
        if (progressDialogHolder != null) {
            EpdController.waitForUpdateFinished();
            progressDialogHolder.dismissProgressDialog(object);
        }
    }

    public void dismissAllProgressDialog() {
        if (progressDialogHolder != null) {
            progressDialogHolder.dismissAllProgressDialog();
        }
    }

    public void destroyProgressDialogHolder() {
        if (progressDialogHolder != null) {
            progressDialogHolder.dismissAllProgressDialog();
            progressDialogHolder = null;
        }
    }
}
