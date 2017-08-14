package com.onyx.android.sdk.reader.api;

/**
 * Created by ming on 2017/8/5.
 */

public class ReaderFormAttributes {

    private String id;

    private String type;

    private String action;

    private String use;

    private String extraAttributes;

    public ReaderFormAttributes() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExtraAttributes() {
        return extraAttributes;
    }

    public void setExtraAttributes(String extraAttributes) {
        this.extraAttributes = extraAttributes;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getUse() {
        return use;
    }

    public void setUse(String use) {
        this.use = use;
    }
}
