package com.onyx.demo.push.model;

import java.io.Serializable;

/**
 * Created by suicheng on 2017/3/3.
 */
public class PushContent implements Serializable {
    public String action;
    public String url;
    public String name;
    public int interval = 3; //unit seconds
}
