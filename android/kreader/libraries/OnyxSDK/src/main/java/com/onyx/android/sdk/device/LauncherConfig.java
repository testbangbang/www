/**
 *
 */
package com.onyx.android.sdk.device;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author jim
 *
 */
public class LauncherConfig {

    private final static String CONFIG_PATH = "/system/launcher.conf";
    private final static String STARTUP_ACTIVITY_QUALIFIED_NAME = "startupActivity";
    private final static String DEFAULT_STARTUP_ACTIVITY
            = "com.onyx.android.launcher.LauncherMCActivity";

    private static Properties getLauncherConfig() throws IOException {
        Properties launcherConfig = new Properties();
        FileInputStream in = new FileInputStream(CONFIG_PATH);
        launcherConfig.load(in);
        in.close();
        return launcherConfig;
    }

    public static String getStartupActivityQualifiedName() {
        String activityQualifiedName = DEFAULT_STARTUP_ACTIVITY;
        try {
            String qualifiedName = getLauncherConfig().getProperty(STARTUP_ACTIVITY_QUALIFIED_NAME,
                    DEFAULT_STARTUP_ACTIVITY);
            if (!qualifiedName.isEmpty()) {
                activityQualifiedName = qualifiedName;
            }
        } catch (IOException ex) {
            // if file not found or read errors, use default startup activity
        }
        return activityQualifiedName;
    }

}
