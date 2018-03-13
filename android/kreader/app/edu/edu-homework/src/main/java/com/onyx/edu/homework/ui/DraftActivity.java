package com.onyx.edu.homework.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.Subject;
import com.onyx.android.sdk.data.model.homework.Question;
import com.onyx.android.sdk.utils.DateTimeUtil;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.edu.homework.DataBundle;
import com.onyx.edu.homework.R;
import com.onyx.edu.homework.action.ShowAnalysisAction;
import com.onyx.edu.homework.base.BaseActivity;
import com.onyx.edu.homework.data.Constant;
import com.onyx.edu.homework.data.SaveDocumentOption;
import com.onyx.edu.homework.databinding.ActivityAnswerBinding;
import com.onyx.edu.homework.event.CloseScribbleEvent;
import com.onyx.edu.homework.event.SaveNoteEvent;

import org.greenrobot.eventbus.Subscribe;

import java.util.Date;

/**
 * Created by lxm on 2017/12/6.
 */

public class DraftActivity extends BaseActivity {

    private ActivityAnswerBinding binding;
    private Question question;
    private ScribbleFragment scribbleFragment;
    private NoteToolFragment toolFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EpdController.invalidate(getWindow().getDecorView(), UpdateMode.GC);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_answer);
        question = (Question) getIntent().getSerializableExtra(Constant.TAG_QUESTION);
        initView(question);
    }

    private void initView(final Question question) {
        initFragment();
        initToolbar();
        DataBundle.getInstance().register(this);
    }

    private void initToolbar() {
        String title = getDataBundle().getHomework().title;
        Subject subject = getDataBundle().getHomework().subject;
        Date beginTime = getDataBundle().getHomework().beginTime;
        if (subject != null && !StringUtils.isNullOrEmpty(subject.name)) {
            title += "  " + getString(R.string.subject, subject.name);
        }
        if (beginTime != null) {
            String time = DateTimeUtil.formatGMTDate(beginTime, DateTimeUtil.DATE_FORMAT_YYYYMMDD_HHMM);
            title += "  " + getString(R.string.publish_time, time);
        }
        binding.toolbar.title.setText(title);
        binding.toolbar.backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        binding.tvDraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        } );
        binding.analysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAnalysisDialog();
            }
        } );
    }

    private DataBundle getDataBundle() {
        return DataBundle.getInstance();
    }

    private void initFragment() {
        DataBundle.getInstance().resetNoteViewHelper();
        scribbleFragment = ScribbleFragment.newInstance(question, true);
        getSupportFragmentManager().beginTransaction().replace(R.id.scribble_layout, scribbleFragment).commit();
        toolFragment = NoteToolFragment.newInstance(binding.subMenuLayout, 1, RelativeLayout.ALIGN_PARENT_BOTTOM);
        getSupportFragmentManager().beginTransaction().replace(R.id.tool_layout, toolFragment).commit();
    }

    @Override
    public boolean isFullScreen() {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataBundle.getInstance().unregister(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressedSupport() {
        DataBundle.getInstance().post(new SaveNoteEvent(true));
    }

    @Subscribe
    public void onCloseScribbleEvent(CloseScribbleEvent event) {
        finish();
    }

    private void showAnalysisDialog() {
        SaveDocumentOption option = SaveDocumentOption.onStopSaveOption();
        scribbleFragment.saveDocument(option.finishAfterSave,
                option.resumeDrawing,
                option.render,
                option.showLoading,
                new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        new ShowAnalysisAction(question).execute(DraftActivity.this, null);
                    }
                });
    }
}
