package com.onyx.kreader.ui.data;

/**
 * Created by ming on 2016/11/9.
 */

public class DictionaryQuery {

    public static final int DICT_STATE_PARAM_ERROR = -1;
    public static final int DICT_STATE_QUERY_SUCCESSFUL = 0;
    public static final int DICT_STATE_QUERY_FAILED = 1;
    public static final int DICT_STATE_LOADING = 2;

    private String id;
    private String dictName;
    private String dictPath;
    private int state;
    private String explanation;
    private String keyword;
    private int entryIndex;
    private String soundPath;

    public DictionaryQuery(String dictName, String dictPath, String soundPath, String explanation, int state) {
        this.dictName = dictName;
        this.dictPath = dictPath;
        this.explanation = explanation;
        this.state = state;
        this.soundPath = soundPath;
    }

    public String getDictName() {
        return dictName;
    }

    public void setDictName(String dictName) {
        this.dictName = dictName;
    }

    public String getDictPath() {
        return dictPath;
    }

    public void setDictPath(String dictPath) {
        this.dictPath = dictPath;
    }

    public int getEntryIndex() {
        return entryIndex;
    }

    public void setEntryIndex(int entryIndex) {
        this.entryIndex = entryIndex;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getSoundPath() {
        return soundPath;
    }

    public void setSoundPath(String soundPath) {
        this.soundPath = soundPath;
    }

    public static DictionaryQuery create(String dictName, String dictPath, String soundPath, String explanation, int state) {
        DictionaryQuery query = new DictionaryQuery(dictName, dictPath, soundPath, explanation, state);
        return query;
    }
}
