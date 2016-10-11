package org.apache.lucene.analysis.cn;

import android.content.Context;
import android.util.Log;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.hhmm.BigramDictionary;
import org.apache.lucene.analysis.cn.smart.hhmm.WordDictionary;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by joy on 6/23/16.
 */
public class AnalyzerAndroidWrapper {
    public static final String TAG = AnalyzerAndroidWrapper.class.getSimpleName();

    private static Context context;
    private static AtomicBoolean initialized = new AtomicBoolean(false);
    private static AtomicBoolean initializing = new AtomicBoolean(false);

    public static void initialize(final Context context, boolean background) {
        if (isInitialized()) {
            return;
        }
        if (isInitializing()) {
            return;
        }
        initializing.set(true);
        final Runnable runnable = initializeRunnable(context);
        if (background) {
            new Thread(runnable).start();
        } else {
            runnable.run();
        }
    }

    private static Runnable initializeRunnable(final Context context) {
        return new Runnable() {
            @Override
            public void run() {
                long start = System.currentTimeMillis();
                AnalyzerAndroidWrapper.context = context;
                WordDictionary.getInstance();
                BigramDictionary.getInstance();
                initialized.set(true);
                long end = System.currentTimeMillis();
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "Initialize data takes: " + (end - start) + " ms.");
                }
            }
        };
    }

    public static boolean isInitialized() {
        return initialized.get();
    }

    private static boolean isInitializing() {
        return initializing.get();
    }

    public static InputStream openAssetFile(String fileName) throws IOException {
        return context.getAssets().open(fileName);
    }

    public static ArrayList<String> analyze(String sentence) {
        ArrayList<String> list = new ArrayList<>();
        if (!isInitialized()) {
            return list;
        }
        try {
            Token nt = new Token();
            Analyzer ca = new SmartChineseAnalyzer(true);
            TokenStream ts = ca.tokenStream("sentence", new StringReader(sentence));
            nt = ts.next(nt);
            while (nt != null) {
                list.add(nt.term());
                nt = ts.next(nt);
            }
            ts.close();
        } catch (Throwable tr) {
            Log.w(TAG, tr);
        }
        return list;
    }
}
