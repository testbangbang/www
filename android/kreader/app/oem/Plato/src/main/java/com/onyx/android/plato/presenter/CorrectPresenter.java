package com.onyx.android.plato.presenter;

import com.onyx.android.plato.cloud.bean.QuestionData;
import com.onyx.android.plato.data.CorrectData;
import com.onyx.android.plato.interfaces.CorrectView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by li on 2017/10/25.
 */

public class CorrectPresenter {
    private CorrectView correctView;
    private CorrectData correctData;

    public CorrectPresenter(CorrectView correctView) {
        this.correctView = correctView;
        correctData = new CorrectData();
    }

    public void getCorrectData() {
        List<QuestionData> list = new ArrayList<>();
        /*for (int i = 0; i < 8; i++) {
            QuestionData data = new QuestionData();
            Question question = new Question();
            question.id = i;
            question.question = "shi wan ge wei shen me?";
            if (i == 7) {
                question.type = "objective";
                data.exercise = question;
                list.add(data);
                break;
            }
            question.type = "choice";
            List<Map<String, String>> selection = new ArrayList<>();

            Map<String, String> map = new HashMap<>();
            map.put("key", "A");
            map.put("value", "aaaa");
            selection.add(map);

            map = new HashMap<>();
            map.put("key", "B");
            map.put("value", "bbbb");
            selection.add(map);

            map = new HashMap<>();
            map.put("key", "C");
            map.put("value", "cccc");
            selection.add(map);

            map = new HashMap<>();
            map.put("key", "D");
            map.put("value", "dddd");
            selection.add(map);
            question.selection = selection;
            data.exercise = question;
            list.add(data);
        }
        correctView.setCorrectList(list);*/
    }
}
