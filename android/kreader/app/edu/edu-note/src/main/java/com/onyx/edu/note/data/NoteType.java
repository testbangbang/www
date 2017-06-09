package com.onyx.edu.note.data;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by solskjaer49 on 2017/6/2 12:11.
 */

public class NoteType {
    public static final int CREATE_NOTE = 0;
    public static final int NOTE = 1;
    public static final int FOLDER = 2;

    // ... type definitions
    // Describes when the annotation will be discarded
    @Retention(RetentionPolicy.SOURCE)
    // Enumerate valid values for this interface
    @IntDef({CREATE_NOTE, NOTE, FOLDER})
    // Create an interface for validating int types
    public @interface NoteTypeDef {
    }

    public
    @NoteTypeDef
    static int translate(int val) {
        return val;
    }
}
