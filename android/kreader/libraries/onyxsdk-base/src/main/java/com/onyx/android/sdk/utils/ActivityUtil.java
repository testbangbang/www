/**
 * 
 */
package com.onyx.android.sdk.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;


/**
 * @author joy
 * 
 */
public class ActivityUtil {

    private static final String TAG = ActivityUtil.class.getSimpleName();
    
    public static boolean startActivitySafely(Context from, Intent intent) {
        try {
            from.startActivity(intent);
            return true;
        } catch (Throwable tr) {
            Log.e(TAG, "", tr);
        }
        
        return false;
    }

    public static Intent createIntent(final String packageName, final String activityClassName) {
        Intent intent = new Intent();
        final String className;
        boolean differentPackage = activityClassName.contains(packageName);
        if (differentPackage) {
            className = activityClassName;
        } else {
            className = packageName + activityClassName;
        }
        intent.setComponent(new ComponentName(packageName, className));
        return intent;
    }

    public static boolean startActivityForResultSafely(Activity from, Intent intent, int requestCode) {
        try {
            from.startActivityForResult(intent, requestCode);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
        return false;
    }

    public static boolean startActivitySafely(Context from, Intent intent,
            ActivityInfo appInfo)
    {
        @SuppressWarnings("unused")
        CharSequence app_name = appInfo.applicationInfo.loadLabel(from
                .getPackageManager());

        try {
            intent.setPackage(appInfo.packageName);
            intent.setClassName(appInfo.packageName, appInfo.name);

            from.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
        } catch (SecurityException e) {
        }

        return false;
    }

    public static boolean startActivityForResultSafely(Activity from, Intent intent, ActivityInfo appInfo, int requestCode) {
        @SuppressWarnings("unused")
        CharSequence app_name = appInfo.applicationInfo.loadLabel(from.getPackageManager());
        try {
            intent.setPackage(appInfo.packageName);
            intent.setClassName(appInfo.packageName, appInfo.name);
            from.startActivityForResult(intent, requestCode);
            return true;
        } catch (ActivityNotFoundException e) {
        } catch (SecurityException e) {
        }
        return false;
    }

    public static Intent getLaunchIntentForPackage(PackageManager pm, ActivityInfo activityInfo) {
        Intent intent = pm.getLaunchIntentForPackage(activityInfo.packageName);
        if (intent == null) {
            intent = new Intent();
            intent.setComponent(new ComponentName(activityInfo.packageName, activityInfo.name));
        }
        return intent;
    }


    /**
     * Help Preference Screen To Enable Back Function on Action Bar.
     * Sets up the action bar for an {@link PreferenceScreen}
     * Use this method in onPreferenceTreeClick();
     */
    public static void enableActionBarBackFunc(PreferenceScreen preferenceScreen) {
        final Dialog dialog = preferenceScreen.getDialog();

        if (dialog != null) {
            // Initialize the action bar
            dialog.getActionBar().setHomeButtonEnabled(true);
            dialog.getActionBar().setDisplayHomeAsUpEnabled(true);

            // Apply custom home button area click listener to close the PreferenceScreen because PreferenceScreens are dialogs which swallow
            // events instead of passing to the activity
            // Related Issue: https://code.google.com/p/android/issues/detail?id=4611
            View homeBtn = dialog.findViewById(android.R.id.home);

            if (homeBtn != null) {
                View.OnClickListener dismissDialogClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                };

                // Prepare yourselves for some hacky programming
                ViewParent homeBtnContainer = homeBtn.getParent();
                // The home button is an ImageView inside a FrameLayout
                if (homeBtnContainer instanceof FrameLayout) {
                    ViewGroup containerParent = (ViewGroup) homeBtnContainer.getParent();

                    if (containerParent instanceof LinearLayout) {
                        // This view also contains the title text, set the whole view as clickable
                        ((LinearLayout) containerParent).setOnClickListener(dismissDialogClickListener);
                    } else {
                        // Just set it on the home button
                        ((FrameLayout) homeBtnContainer).setOnClickListener(dismissDialogClickListener);
                    }
                } else {
                    // The 'If all else fails' default case
                    homeBtn.setOnClickListener(dismissDialogClickListener);
                }
            }
        }
    }

    /**
     * Help Preference Activity To Enable Back Function on Action Bar.
     * Sets up the action bar for an {@link PreferenceActivity}
     * Use this method in onResume();
     */
    public static void enableActionBarBackFunc(final PreferenceActivity activity) {
        View homeBtn = activity.findViewById(android.R.id.home);
        if (homeBtn != null) {
            View.OnClickListener backPressedClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.onBackPressed();
                }
            };

            // Prepare yourselves for some hacky programming
            ViewParent homeBtnContainer = homeBtn.getParent();

            // The home button is an ImageView inside a FrameLayout
            if (homeBtnContainer instanceof FrameLayout) {
                ViewGroup containerParent = (ViewGroup) homeBtnContainer.getParent();
                for (int i = 0; i < ((FrameLayout) homeBtnContainer).getChildCount(); i++) {
                    ((FrameLayout) homeBtnContainer).getChildAt(i).setVisibility(View.VISIBLE);
                }

                if (containerParent instanceof LinearLayout) {
                    // This view also contains the title text, set the whole view as clickable
                    containerParent.setEnabled(true);
                    containerParent.setClickable(true);
                    ((LinearLayout) containerParent).setOnClickListener(backPressedClickListener);
                } else {
                    // Just set it on the home button
                    ((FrameLayout) homeBtnContainer).setEnabled(true);
                    ((FrameLayout) homeBtnContainer).setClickable(true);
                    ((FrameLayout) homeBtnContainer).setOnClickListener(backPressedClickListener);
                }
            } else {
                // The 'If all else fails' default case
                homeBtn.setClickable(true);
                homeBtn.setEnabled(true);
                homeBtn.setOnClickListener(backPressedClickListener);
            }
        }
    }

}
