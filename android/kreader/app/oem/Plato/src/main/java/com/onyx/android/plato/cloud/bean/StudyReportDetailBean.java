package com.onyx.android.plato.cloud.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jackdeng on 2017/10/26.
 */

public class StudyReportDetailBean {
    public int score;
    public int classRank;
    public int classNum;
    public int gradeRank;
    public int gradeNum;
    public int SN;
    public int STP;
    public int STS;
    public int ON;
    public int OC;
    public int OS;
    public List<CompetenceBean> competence;
    public List<DataBean> data;

    public static class CompetenceBean {
        public String name;
        public PointsBean points;

        public static class PointsBean {
            public float Class;
            public float own;
        }
    }

    public static class DataBean {
        public String KNId;
        public String KN;
        public double process;
        public List<MapBean> titleList;

        public static class MapBean {
            public String id;
            public String NO;
            public int points;
            public int score;
            public double avg;
            public String no;
        }
    }
}

