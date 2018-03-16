package com.onyx.edu.homework.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.Html;
import android.text.Spanned;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.onyx.android.sdk.data.model.homework.Question;
import com.onyx.android.sdk.data.model.homework.QuestionOption;
import com.onyx.android.sdk.utils.Base64ImageParser;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.edu.homework.R;

import java.util.List;

/**
 * <pre>
 *     author : liao lin tao
 *     time   : 2018/3/16 15:00
 *     desc   : draw objective question layout to bitmap
 * </pre>
 */

public class ObjectiveQuestionBitmapUtil {

    public static Bitmap createObjectiveQuestionBitmap(Context context, Question question) {
        View view = View.inflate(context, R.layout.layout_objective_question, null);
        TextView tvContent = (TextView) view.findViewById(R.id.content);
        RadioGroup rgOption = (RadioGroup) view.findViewById(R.id.option);

        Spanned content = TextUtils.fromHtml(question.content, new Base64ImageParser(context), null);
        tvContent.setText(content);
        bindQuestionOption(context, rgOption, question);

        int width = (int) context.getResources().getDimension(R.dimen.scribble_view_width);
        int height = (int) context.getResources().getDimension(R.dimen.scribble_view_height);
        view.measure(width, height);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        Bitmap bmp = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        bmp.eraseColor(Color.TRANSPARENT);
        view.draw(new Canvas(bmp));
        return bmp;
    }

    private static void bindQuestionOption(Context context, RadioGroup group, Question question) {
        group.removeAllViews();
        if (!question.isChoiceQuestion()) {
            group.setVisibility(View.GONE);
            return;
        }
        group.setVisibility(View.VISIBLE);
        List<QuestionOption> options = question.options;
        for (QuestionOption option : options) {
            CompoundButton button = createCompoundButton(context, question, option);
            RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup
                    .LayoutParams.WRAP_CONTENT);
            int margin = (int) context.getResources().getDimension(R.dimen.question_option_margin);
            lp.setMargins(0, margin, 0, margin);
            group.addView(button, lp);
            if (option.checked) {
                group.check(button.getId());
            }
        }
    }

    private static CompoundButton createCompoundButton(Context context, final Question question, final QuestionOption
            option) {
        final boolean single = question.isSingleChoiceQuestion();
        final CompoundButton button = single ? new RadioButton(context) : new CheckBox(context);
        button.setText(Html.fromHtml(StringUtils.filterHtmlWrapChar(option.value), new Base64ImageParser(context),
                null));
        button.setTextSize(context.getResources().getDimension(R.dimen.question_option_text_size));
        button.setChecked(option.checked);
        button.setGravity(Gravity.TOP);
        button.setTextColor(Color.BLACK);
        return button;
    }

}
