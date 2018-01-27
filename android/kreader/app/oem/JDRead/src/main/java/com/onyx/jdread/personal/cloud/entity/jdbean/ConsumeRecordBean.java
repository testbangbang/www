package com.onyx.jdread.personal.cloud.entity.jdbean;

import java.util.List;

/**
 * Created by li on 2018/1/26.
 */

public class ConsumeRecordBean {
    private int result_code;
    private String message;
    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        private String created;
        private int yuedou;
        private String desc;
        private int voucher;

        public String getCreated() {
            return created;
        }

        public void setCreated(String created) {
            this.created = created;
        }

        public int getYuedou() {
            return yuedou;
        }

        public void setYuedou(int yuedou) {
            this.yuedou = yuedou;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public int getVoucher() {
            return voucher;
        }

        public void setVoucher(int voucher) {
            this.voucher = voucher;
        }
    }
}
