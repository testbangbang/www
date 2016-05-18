/**
 * 
 */
package com.onyx.android.sdk.ui.data;

import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.data.util.ActivityUtil;
import com.onyx.android.sdk.ui.dialog.DialogScreenRotation;
import com.onyx.android.sdk.ui.menu.OnyxMenuItem;
import com.onyx.android.sdk.ui.menu.OnyxMenuItem.OnMenuItemClickListener;
import com.onyx.android.sdk.ui.menu.OnyxMenuRow;
import com.onyx.android.sdk.ui.menu.OnyxMenuSuite;

/**
 * @author joy
 *
 */
public class SystemMenuFactory
{
    private static final String TAG = "SystemMenuFactory";
    
    public enum SystemMenuItem {ScreenRotation, SafelyRemoveSD, Search, Exit, Notifications}
    
    public static OnyxMenuSuite getAllSystemMenuSuite(Activity activity)
    {
        return getSystemMenuSuite(activity, new SystemMenuItem[] {SystemMenuItem.ScreenRotation, 
                SystemMenuItem.SafelyRemoveSD, SystemMenuItem.Search,
                SystemMenuItem.Exit, SystemMenuItem.Notifications});
    }
    public static OnyxMenuSuite getSystemMenuSuite(final Activity activity, SystemMenuItem[] enabledItems) {
        OnyxMenuSuite suite = new OnyxMenuSuite(-1, -1);
        OnyxMenuRow row = new OnyxMenuRow();
        boolean enabled = false;

        OnyxMenuItem item = null;

        enabled = testContains(enabledItems, SystemMenuItem.ScreenRotation);
        item = new OnyxMenuItem(R.string.Screen_Rotation, R.drawable.screen_rotation, enabled);
        item.setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override
            public void onClick() {
                DialogScreenRotation rotation = new DialogScreenRotation(activity);
                rotation.show();
            }
        });
        row.getMenuItems().add(item);

        enabled = testContains(enabledItems, SystemMenuItem.SafelyRemoveSD);
        item = new OnyxMenuItem(R.string.Safely_Remove_SD, R.drawable.sd, enabled);
        item.setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override
            public void onClick() {
                Intent i = new Intent(android.provider.Settings.ACTION_MEMORY_CARD_SETTINGS);
                ActivityUtil.startActivitySafely(activity, i);
            }
        });
        row.getMenuItems().add(item);

        enabled = testContains(enabledItems, SystemMenuItem.Search);
        item = new OnyxMenuItem(R.string.Search, R.drawable.file_search, enabled);
        item.setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override
            public void onClick() {
                activity.onSearchRequested();
            }
        });
        row.getMenuItems().add(item);

        enabled = testContains(enabledItems, SystemMenuItem.Exit);
        item = new OnyxMenuItem(R.string.Exit, R.drawable.exit, enabled);
        item.setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override
            public void onClick() {
                activity.finish();
            }
        });
        row.getMenuItems().add(item);

        enabled = testContains(enabledItems, SystemMenuItem.Notifications);
        item = new OnyxMenuItem(R.string.Notifications, R.drawable.ic_menu_notifications, enabled);
        item.setOnMenuItemClickListener(new OnMenuItemClickListener()
        {
            
            @Override
            public void onClick()
            {
                try {
                    Object service  = activity.getSystemService("statusbar");
                    Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
                    Method expand = statusbarManager.getMethod("expand");
                    expand.invoke(service);
                 } catch (Exception e) {
                     Log.w(TAG, e);
                 }

            }
        });
        row.getMenuItems().add(item);

        suite.getMenuRows().add(row);
        return suite;
    }
    
    private static <T> boolean testContains(T[] array, T value)
    {
        for (T e : array) {
            if (e.equals(value)) {
                return true;
            }
        }
        
        return false;
    }
}
