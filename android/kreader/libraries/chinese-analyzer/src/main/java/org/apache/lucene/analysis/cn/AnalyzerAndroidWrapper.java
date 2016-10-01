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

/**
 * Created by joy on 6/23/16.
 */
public class AnalyzerAndroidWrapper {
    public static final String TAG = AnalyzerAndroidWrapper.class.getSimpleName();

    private static Context context;

    private static Object lock = new Object();
    private static boolean initializing;
    private static boolean initialized;

    public static void lazyInit(final Context context) {
        synchronized (lock) {
            if (initialized || initializing) {
                return;
            }
            initializing = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    AnalyzerAndroidWrapper.context = context;
                    WordDictionary.getInstance();
                    BigramDictionary.getInstance();
                    synchronized (lock) {
                        initialized = true;
                        initializing = false;
                    }
                }
            }).start();
        }
    }

    public static boolean isInitialized() {
        synchronized (lock) {
            return initialized;
        }
    }

    public static InputStream openAssetFile(String fileName) throws IOException {
        return context.getAssets().open(fileName);
    }

    public static ArrayList<String> analyze(String sentence) {
        synchronized (lock) {
            ArrayList<String> list = new ArrayList<>();
            if (!initialized) {
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
}
