package com.onyx.android.dr.bean;

/**
 * Created by hehai on 17-8-1.
 */

public class MemberParameterBean {
    private String offset;
    private String limit;
    private String sortBy;
    private String order;

    public MemberParameterBean(String offset, String limit, String sortBy, String order) {
        this.offset = offset;
        this.limit = limit;
        this.sortBy = sortBy;
        this.order = order;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
