package com.onyx.android.sample;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;

import com.onyx.android.sdk.utils.TestUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by wangxu on 17-8-5.
 */

public class TextSelectionActivity extends AppCompatActivity {

    @Bind(R.id.text_select_tv)
    SelectableTextView textView;

    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean tag = true;
    private int start;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_selection);

        ButterKnife.bind(this);
        textView.setText("abcdefghijklmn123456789中国dkadjfgajcmvkajiojgabcdefghijklmn123456789中国dkadj" +
                "fgajcmvkajiojgabcdefghijklmn123456789中国dkadjfgajcmvkajiojgabcdefghijklmn123456789中国" +
                "dkadjfgajcmvkajiojgabcdefghijklmn123456789中国dkadjfgajcmvkajiojgabcdefghijklmn12345678" +
                "9中国dkadjfgajcmvkajiojgjdhgdhgs;v*/-*&&^%%$##@;sjkgpakgkd'hk,[pow[pitgpeorutsj-0809784" +
                "8927785628950209534689027582738957-0680xvsmnvslfkJHJKRKTHR;'BKOEROPAKFSJCKVM'GEJKRAAKL;" +
                "LKCKSKJFWEHFWGAHSDHJFJLKGNFLKGNLKSNFLKAHNFJKHFJEHFGJLSNGSLKNLKSDFHNJLKAHFJKJHJDS");
        triggerUpdate();
    }

    private void triggerUpdate() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final int len = textView.getText().length();
                if (tag) {
                    start = TestUtils.randInt(1, len - 1);
                    textView.clearSelection();
                    tag = !tag;
                }
                int end = start + 10;
                if (end >= len) {
                    tag = !tag;
                    end = len;
                }
                textView.setSelection(start, end);
                start = end;
                triggerUpdate();
            }
        }, 1);
    }
}
