package com.onyx.jdread.personal.cloud.entity.jdbean;

/**
 * Created by li on 2018/1/25.
 */

public class VerifySignBean {
    private DataBean data;
    private int result_code;
    private String message;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public int getResult_code() {
        return result_code;
    }

    public void setResult_code(int result_code) {
        this.result_code = result_code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class DataBean {
        /**
         * todaySign : false
         */

        private boolean todaySign;

        public boolean isTodaySign() {
            return todaySign;
        }

        public void setTodaySign(boolean todaySign) {
            this.todaySign = todaySign;
        }
    }
}
