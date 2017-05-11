package com.onyx.kreader.ui.events;

import android.content.Context;

/**
 * Created by ming on 9/23/16.
 */
public class DocumentFinishEvent {
    private Context context;
    private String comment;
    private int score;

    public DocumentFinishEvent( Context context, String comment, int score) {
        this.comment = comment;
        this.context = context;
        this.score = score;
    }

    public DocumentFinishEvent(final Context c) {
        context = c;
    }

    public Context getContext() {
        return context;
    }

    public String getComment() {
        return comment;
    }

    public int getScore() {
        return score;
    }
}
