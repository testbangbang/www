package com.onyx.android.edu.db.model;


import com.onyx.android.edu.utils.JsonUtils;

import java.util.List;

/**
 * Created by ming on 16/7/5.
 */
public class AtomicQuiz{

    public ResourceBundle title; //标题
    public String options; //选项列表（可为null)
    public ResourceBundle answer; //回答(学生的回答，与标准答案区分
    public ResourceBundle analysis; //分析
    public ResourceBundle score; //得分
    public ResourceBundle standardAnswer; //标准答案
    public Textbook textbook; //教材
    public Subject subject; //科目
    public LearnSection learnSection;  //分段

    public List<AtomicOption> getOptions() {
        return JsonUtils.toBean(options, List.class);

    }
}
