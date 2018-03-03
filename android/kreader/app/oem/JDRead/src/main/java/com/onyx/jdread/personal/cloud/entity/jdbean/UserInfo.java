package com.onyx.jdread.personal.cloud.entity.jdbean;

import android.databinding.BaseObservable;

/**
 * Created by li on 2018/1/11.
 */

public class UserInfo extends BaseObservable{
    public String nickname;
    public int read_book_count;
    public int read_finish_book_count;
    public long read_time_length;
    public int user_days;
    public int notes_count;
    public String gendar;
    public int yuedou;
    public int voucher;
    public int vip_remain_days;
    public String yun_big_image_url;
    public String yun_mid_image_url;
    public String yun_small_image_url;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
        notifyChange();
    }

    public int getRead_book_count() {
        return read_book_count;
    }

    public void setRead_book_count(int read_book_count) {
        this.read_book_count = read_book_count;
        notifyChange();
    }

    public int getRead_finish_book_count() {
        return read_finish_book_count;
    }

    public void setRead_finish_book_count(int read_finish_book_count) {
        this.read_finish_book_count = read_finish_book_count;
        notifyChange();
    }

    public long getRead_time_length() {
        return read_time_length;
    }

    public void setRead_time_length(long read_time_length) {
        this.read_time_length = read_time_length;
        notifyChange();
    }

    public int getUser_days() {
        return user_days;
    }

    public void setUser_days(int user_days) {
        this.user_days = user_days;
        notifyChange();
    }

    public int getNotes_count() {
        return notes_count;
    }

    public void setNotes_count(int notes_count) {
        this.notes_count = notes_count;
        notifyChange();
    }

    public String getGendar() {
        return gendar;
    }

    public void setGendar(String gendar) {
        this.gendar = gendar;
        notifyChange();
    }

    public int getYuedou() {
        return yuedou;
    }

    public void setYuedou(int yuedou) {
        this.yuedou = yuedou;
        notifyChange();
    }

    public int getVoucher() {
        return voucher;
    }

    public void setVoucher(int voucher) {
        this.voucher = voucher;
        notifyChange();
    }

    public int getVip_remain_days() {
        return vip_remain_days;
    }

    public void setVip_remain_days(int vip_remain_days) {
        this.vip_remain_days = vip_remain_days;
        notifyChange();
    }

    public String getYun_big_image_url() {
        return yun_big_image_url;
    }

    public void setYun_big_image_url(String yun_big_image_url) {
        this.yun_big_image_url = yun_big_image_url;
        notifyChange();
    }

    public String getYun_mid_image_url() {
        return yun_mid_image_url;
    }

    public void setYun_mid_image_url(String yun_mid_image_url) {
        this.yun_mid_image_url = yun_mid_image_url;
        notifyChange();
    }

    public String getYun_small_image_url() {
        return yun_small_image_url;
    }

    public void setYun_small_image_url(String yun_small_image_url) {
        this.yun_small_image_url = yun_small_image_url;
        notifyChange();
    }
}
