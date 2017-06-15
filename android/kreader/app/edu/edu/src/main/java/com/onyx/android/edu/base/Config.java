package com.onyx.android.edu.base;

import com.onyx.android.edu.R;

/**
 * Created by ming on 16/6/24.
 */
public class Config {

    public static final String[] Letter = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N"};

    public static final String BASE_URL = "";

    public static final String[] subjectNames = {"语文", "数学", "英语", "物理", "化学",
            "生物", "历史", "地理", "政治"};

    public static final int[] subjectResIds = {R.drawable.ic_student_syllabus_chinese_gray
            , R.drawable.ic_student_syllabus_math_gray
            , R.drawable.ic_student_syllabus_english_gray
            , R.drawable.ic_student_syllabus_physics_gray
            , R.drawable.ic_student_syllabus_chemistry_gray
            , R.drawable.ic_student_syllabus_biology_gray
            , R.drawable.ic_student_syllabus_history_gray
            , R.drawable.ic_student_syllabus_geography_gray
            , R.drawable.ic_student_syllabus_political_gray};

    public static final int COMMON_COLUMNS = 4;

    public static boolean useLocalData = true;
}
