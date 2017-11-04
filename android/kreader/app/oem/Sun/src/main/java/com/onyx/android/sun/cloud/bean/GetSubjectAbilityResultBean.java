package com.onyx.android.sun.cloud.bean;

import java.util.List;

/**
 * Created by jackdeng on 2017/11/2.
 */

public class GetSubjectAbilityResultBean {

    public int code;
    public String msg;
    public AbilityBean data;

    public static class AbilityBean {

        public int term;
        public String course;
        public int ability;
        public int classRank;
        public int gradeRank;
        public List<ModulesBean> modules;

        public static class ModulesBean {
            public int value;
            public int id;
            public String name;
            public int score;
        }
    }
}
