package com.onyx.jdread.reader.menu.event;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;

/**
 * Created by huxiaomao on 2018/1/27.
 */

public class ReaderErrorEvent {
    private static final String TAG = "ReaderError";
    public Throwable throwable;
    public String from;

    public ReaderErrorEvent(Throwable throwable, String from) {
        this.throwable = throwable;
        this.from = from;
    }

    public static void onErrorHandle(Throwable throwable, String from, EventBus eventBus) {
        ReaderErrorEvent event = new ReaderErrorEvent(throwable, from);
        eventBus.post(event);
    }

    public static String[] getThrowableStringRep(Throwable throwable) {
        if (throwable == null) {
            return new String[0];
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        pw.flush();
        LineNumberReader reader = new LineNumberReader(new StringReader(
                sw.toString()));
        ArrayList lines = new ArrayList();
        try {
            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
        } catch (IOException ex) {
            lines.add(ex.toString());
        }
        String[] rep = new String[lines.size()];
        lines.toArray(rep);
        return rep;
    }

    public static void printThrowable(String[] errors) {
        for (int i = 0; i < errors.length; i++) {
            Log.e(TAG, errors[i]);
        }
    }
}
