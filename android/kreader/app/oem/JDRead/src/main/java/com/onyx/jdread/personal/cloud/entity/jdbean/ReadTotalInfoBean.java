package com.onyx.jdread.personal.cloud.entity.jdbean;

import java.util.List;

/**
 * Created by li on 2018/1/2.
 */

public class ReadTotalInfoBean {
    private int code;
    private String currentTime;
    private String firstAccessTime;
    private int totalCount;
    private List<ResultListBean> resultList;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getFirstAccessTime() {
        return firstAccessTime;
    }

    public void setFirstAccessTime(String firstAccessTime) {
        this.firstAccessTime = firstAccessTime;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<ResultListBean> getResultList() {
        return resultList;
    }

    public void setResultList(List<ResultListBean> resultList) {
        this.resultList = resultList;
    }

    public static class ResultListBean {
        private int bookCount;
        private int id;
        private String name;
        private double proportion;

        public int getBookCount() {
            return bookCount;
        }

        public void setBookCount(int bookCount) {
            this.bookCount = bookCount;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getProportion() {
            return proportion;
        }

        public void setProportion(double proportion) {
            this.proportion = proportion;
        }
    }
}
