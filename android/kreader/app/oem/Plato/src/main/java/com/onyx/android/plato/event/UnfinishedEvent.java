package com.onyx.android.plato.event;

/**
 * Created by li on 2017/10/12.
 */

public class UnfinishedEvent {
    private int practiceId;
    private String title;
    private String type;
    private int id;

    public UnfinishedEvent(int id, int practiceId, String type, String title) {
        this.id = id;
        this.practiceId = practiceId;
        this.type = type;
        this.title = title;
    }

    public int getPracticeId() {
        return practiceId;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }
}
