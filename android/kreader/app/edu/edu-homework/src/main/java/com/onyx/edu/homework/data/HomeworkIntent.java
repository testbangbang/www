package com.onyx.edu.homework.data;

import java.sql.Date;

/**
 * Created by lxm on 2017/12/7.
 */

public class HomeworkIntent {

    public String _id;
    public HomeworkDetail child;
    //review
    public boolean checked;
    //publish answer
    public boolean readActive;
    public Date beginTime;
    public Date endTime;
}
