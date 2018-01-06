package com.onyx.jdread.shop.cloud.entity.jdbean;

/**
 * Created by li on 2018/1/5.
 */

public class ShoppingCartBookIdsBean {
    private String code;
    private ResultBean result;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        private String bookList;
        private int count;
        private String pin;
        private String updateTime;

        public String getBookList() {
            return bookList;
        }

        public void setBookList(String bookList) {
            this.bookList = bookList;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getPin() {
            return pin;
        }

        public void setPin(String pin) {
            this.pin = pin;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }
    }
}
