package com.onyx.edu.homework.ui;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;

import com.onyx.android.sdk.data.model.Question;
import com.onyx.android.sdk.ui.dialog.OnyxBaseDialog;
import com.onyx.android.sdk.utils.Base64ImageParser;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.edu.homework.R;
import com.onyx.edu.homework.databinding.DialogAnalysisBinding;
import com.onyx.edu.homework.utils.TextUtils;

/**
 * Created by lxm on 2017/12/21.
 */

public class AnalysisDialog extends OnyxBaseDialog {

    private DialogAnalysisBinding binding;
    private Question question;

    public AnalysisDialog(Context context, Question question) {
        super(context, R.style.NoTitleDialog);
        this.question = question;
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_analysis, null, false);
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        if (question.answers != null) {
            binding.answers.setText(TextUtils.fromHtml(StringUtils.filterHtmlWrapChar(question.answers), new Base64ImageParser(getContext())));
        }
        if (question.analysis != null) {
            binding.analysis.setText(TextUtils.fromHtml(StringUtils.filterHtmlWrapChar(question.analysis), new Base64ImageParser(getContext())));
        }
    }


}
