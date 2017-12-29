package com.onyx.jdread.shop.cloud.entity.jdbean;

import java.util.List;

/**
 * Created by 12 on 2017/4/5.
 */

public class BookCommentsResultBean {
    private String code;
    private String currentTime;
    private CommentBean reviews;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public CommentBean getReviews() {
        return reviews;
    }

    public void setReviews(CommentBean reviews) {
        this.reviews = reviews;
    }

    public static class CommentBean {
        private int totalCount;
        private List<CommentEntity> list;

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        public List<CommentEntity> getList() {
            return list;
        }

        public void setList(List<CommentEntity> list) {
            this.list = list;
        }
    }
}
