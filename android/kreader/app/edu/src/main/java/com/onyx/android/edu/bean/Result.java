package com.onyx.android.edu.bean;

import java.io.Serializable;

/**
 * Created by ming on 15/8/24.
 * 网络数据类
 */
public class Result<T> implements Serializable {

    private T content;

    private int code;

    private String msg = "";

    public int getCode() {return code;}

    public void setCode(int code) {this.code = code;}

    public String getMsg(){return msg;}

    public String getErrorInfo() {
        switch (code){
            case 300:
                return msg;
            case 500:
                return "";
            default:
                return msg;
        }
    }

    public boolean OK(){
        return code == 200;
    }

    public T getContent(){
        return content;
    }


}
