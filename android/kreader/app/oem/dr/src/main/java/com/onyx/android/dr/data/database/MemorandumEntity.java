package com.onyx.android.dr.data.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by zhouzhiming on 2017/7/5.
 */
@Table(database = DRDatabase.class)
public class MemorandumEntity extends BaseModel{
    @Column
    @PrimaryKey(autoincrement = true)
    public long id;
    @Column
    public String timeQuantum;
    @Column
    public String matter;
    @Column
    public long currentTime;
    @Column
    public String date;
    public String dayOfWeek;

    public String getTimeQuantum() {
        return timeQuantum;
    }

    public void setTimeQuantum(String timeQuantum) {
        this.timeQuantum = timeQuantum;
    }

    public String getMatter() {
        return matter;
    }

    public void setMatter(String matter) {
        this.matter = matter;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}