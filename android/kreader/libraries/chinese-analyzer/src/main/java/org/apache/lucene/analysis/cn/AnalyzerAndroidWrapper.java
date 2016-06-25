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

    public static void init(Context context) {
        AnalyzerAndroidWrapper.context = context;

        WordDictionary.getInstance();
        BigramDictionary.getInstance();
    }

    public static InputStream openAssetFile(String fileName) throws IOException {
        return context.getAssets().open(fileName);
    }

    public static ArrayList<String> analyze(String sentence) {
        ArrayList<String> list = new ArrayList<>();
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
