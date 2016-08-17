package com.onyx.android.sdk.scribble.request.shape;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeSpan;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 8/6/16.
 */
public class SpannableRequest extends BaseNoteRequest {

    private static List<List<Shape>> historyShapeList = new ArrayList<>();
    private volatile SpannableStringBuilder spannableStringBuilder;

    public SpannableRequest(final List<Shape> list) {
        historyShapeList.add(list);
        setPauseInputProcessor(true);
    }

    public void execute(final NoteViewHelper helper) throws Exception {
        setResumeInputProcessor(helper.useDFBForCurrentState());
        if (CollectionUtils.isEmpty(historyShapeList)) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i <= historyShapeList.size(); ++i) {
            builder.append(" ");
        }
        spannableStringBuilder = new SpannableStringBuilder(builder.toString());
        for (int i = 0; i < historyShapeList.size(); i++) {
            ShapeSpan span = new ShapeSpan(historyShapeList.get(i));
            spannableStringBuilder.setSpan(span, i, i + 1,
                    Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            Log.e("TAG", "execute (" + i + ")");
            if ((i + 1) % 7 == 0) {
                Log.e("TAG", "add line changer");
                spannableStringBuilder = spannableStringBuilder.
                        insert(spannableStringBuilder.getSpanEnd(span) + 1, System.getProperty("line.separator"));
            }
        }

    }

    public SpannableStringBuilder getSpannableStringBuilder() {
        return spannableStringBuilder;
    }

    public static void cleanHistoryShapeList(){
        historyShapeList = new ArrayList<>();
    }
}
