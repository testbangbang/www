package com.onyx.edu.homework.data;

/**
 * Created by lxm on 2017/12/17.
 */

public class Config {
    private static final Config ourInstance = new Config();

    public static Config getInstance() {
        return ourInstance;
    }

    private Config() {
    }

    private boolean showScore = false;

    public boolean isShowScore() {
        return showScore;
    }
}
