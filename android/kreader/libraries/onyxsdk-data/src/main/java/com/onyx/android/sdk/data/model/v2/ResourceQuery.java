package com.onyx.android.sdk.data.model.v2;

/**
 * Created by suicheng on 2017/10/24.
 */

public class ResourceQuery {
    private int code;
    private String ref;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public ResourceQuery(int code, String ref) {
        this.ref = ref;
        this.code = code;
    }
}
