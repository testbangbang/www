package com.onyx.android.sample.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sample.R;
import com.onyx.android.sample.view.SelectableTextView;
import com.onyx.android.sdk.utils.TestUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by wangxu on 17-8-5.
 */

public class TextSelectionFragment extends BaseTestFragment {

    @Bind(R.id.text_select_tv)
    SelectableTextView textView;

    private int start;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            final int len = textView.getText().length();
            start = TestUtils.randInt(1, len - 1);
            textView.clearSelection();
            int end = start + TestUtils.randInt(1, len / 2);
            if (end >= len) {
                end = len;
            }
            textView.setSelection(start, end);
            start = end;
            startTest();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_text_selection, container, false);
        ButterKnife.bind(this, view);
        setText();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        startTest();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void setText() {
        textView.setText("abcdefghijklmn123456789中国dkadjfgajcmvkajiojgabcdefghijklmn123456789中国dkadj" +
                "fgajcmvkajiojgabcdefghijklmn123456789中国dkadjfgajcmvkajiojgabcdefghijklmn123456789中国" +
                "dkadjfgajcmvkajiojgabcdefghijklmn123456789中国dkadjfgajcmvkajiojgabcdefghijklmn12345678" +
                "9中国dkadjfgajcmvkajiojgjdhgdhgs;v*/-*&&^%%$##@;sjkgpakgkd'hk,[pow[pitgpeorutsj-0809784" +
                "8927785628950209534689027582738957-0680xvsmnvslfkJHJKRKTHR;'BKOEROPAKFSJCKVM'GEJKRAAKL;" +
                "LKCKSKJFWEHFWGAHSDHJFJLKGNFLKGNLKSNFLKAHNFJKHFJEHFGJLSNGSLKNLKSDFHNJLKAHFJKJHJDSabcdefghijklmn123456789中国dkadjfgajcmvkajiojgabcdefghijklmn123456789中国dkadj" +
                "fgajcmvkajiojgabcdefghijklmn123456789中国dkadjfgajcmvkajiojgabcdefghijklmn123456789中国" +
                "dkadjfgajcmvkajiojgabcdefghijklmn123456789中国dkadjfgajcmvkajiojgabcdefghijklmn12345678" +
                "9中国dkadjfgajcmvkajiojgjdhgdhgs;v*/-*&&^%%$##@;sjkgpakgkd'hk,[pow[pitgpeorutsj-0809784" +
                "8927785628950209534689027582738957-0680xvsmnvslfkJHJKRKTHR;'BKOEROPAKFSJCKVM'GEJKRAAKL;" +
                "LKCKSKJFWEHFWGAHSDHJFJLKGNFLKGNLKSNFLKAHNFJKHFJEHFGJLSNGSLKNLKSDFHNJLKAHFJKJHJDSabcdefghijklmn123456789中国dkadjfgajcmvkajiojgabcdefghijklmn123456789中国dkadj" +
                "fgajcmvkajiojgabcdefghijklmn123456789中国dkadjfgajcmvkajiojgabcdefghijklmn123456789中国" +
                "dkadjfgajcmvkajiojgabcdefghijklmn123456789中国dkadjfgajcmvkajiojgabcdefghijklmn12345678" +
                "9中国dkadjfgajcmvkajiojgjdhgdhgs;v*/-*&&^%%$##@;sjkgpakgkd'hk,[pow[pitgpeorutsj-0809784" +
                "8927785628950209534689027582738957-0680xvsmnvslfkJHJKRKTHR;'BKOEROPAKFSJCKVM'GEJKRAAKL;" +
                "LKCKSKJFWEHFWGAHSDHJFJLKGNFLKGNLKSNFLKAHNFJKHFJEHFGJLSNGSLKNLKSDFHNJLKAHFJKJHJDS");
    }

    public void startTest() {
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, 1);
    }

    public void stopTest() {
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
