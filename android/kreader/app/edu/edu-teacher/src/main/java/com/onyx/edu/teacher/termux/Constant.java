package com.onyx.edu.teacher.termux;

import android.annotation.SuppressLint;

/**
 * Created by lxm on 2017/9/11.
 */

public class Constant {

    @SuppressLint("SdCardPath")
    public static final String FILES_PATH = "/data/data/com.onyx.edu.teacher/files";
    public static final String PREFIX_PATH = FILES_PATH + "/usr";
    public static final String HOME_PATH = FILES_PATH + "/home";
    public static final String START_WEB_SERVER_COMMAND = "cd ../usr/web && npm start\r";
    public static final String START_WEB_SERVER_SUCCESS_EVENT = "=================fff";
    public static final String WEB_URL = "http://localhost:3000";

    public static final String ROOT_SYSTEM_SH_PATH = "/system/bin/sh";
    public static final String ROW_START_CHAR = "$";

    public static final int MSG_NEW_INPUT = 1;
    public static final int MSG_PROCESS_EXITED = 4;

    // command action
    public static final String COMMAND_INVALID_ACTION = "invalid";
    public static final String COMMAND_LOGIN_SHELL_ACTION = "login_shell";
    public static final String COMMAND_START_WEB_ACTION = "start_web";
}
