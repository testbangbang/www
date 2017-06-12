package com.onyx.demo.push.events;

import com.onyx.demo.push.model.PushContent;

import java.io.Serializable;

/**
 * Created by suicheng on 2017/3/3.
 */
public class PushLoadFinishedEvent implements Serializable{
    public PushContent product;

    public PushLoadFinishedEvent(PushContent product) {
        this.product = product;
    }
}
