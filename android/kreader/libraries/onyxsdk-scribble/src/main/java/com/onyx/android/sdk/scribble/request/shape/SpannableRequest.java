package com.onyx.android.sdk.scribble.request.shape;

import android.text.SpannableStringBuilder;
import android.text.Spanned;

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

    private static List<List<Shape>> shapeList = new ArrayList<>();
    private volatile SpannableStringBuilder spannableStringBuilder;

    public SpannableRequest(final List<List<Shape>> list) {
        shapeList.addAll(list);
        setPauseInputProcessor(true);
    }

    public void execute(final NoteViewHelper helper) throws Exception {
        setResumeInputProcessor(helper.useDFBForCurrentState());
        if (CollectionUtils.isEmpty(shapeList)) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("                                                          ");
        spannableStringBuilder = new SpannableStringBuilder(builder.toString());
        for (int i = 0; i < shapeList.size(); i++) {
            spannableStringBuilder.setSpan(new ShapeSpan(shapeList.get(i)), i, i+1,
                    Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }

    }

    public SpannableStringBuilder getSpannableStringBuilder() {
        return spannableStringBuilder;
    }
}
