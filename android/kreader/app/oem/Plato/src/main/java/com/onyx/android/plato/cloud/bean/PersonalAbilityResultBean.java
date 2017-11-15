package com.onyx.android.plato.cloud.bean;

import java.util.List;

/**
 * Created by hehai on 17-10-10.
 */

public class PersonalAbilityResultBean {

    public int code;
    public String msg;
    public DataBean data;

    public static class DataBean {

        public int score;
        public int totalPoints;
        public int classRank;
        public int classSize;
        public int gradeRank;
        public int gredeSize;
        public List<SubjectAbilityDtoListBean> subjectAbilityDtoList;

        public static class SubjectAbilityDtoListBean {

            public String course;
            public float value;
        }
    }
}
