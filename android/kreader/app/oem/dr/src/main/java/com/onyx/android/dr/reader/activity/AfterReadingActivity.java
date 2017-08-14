package com.onyx.android.dr.reader.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.dr.R;
import com.onyx.android.dr.reader.base.AfterReadingView;
import com.onyx.android.dr.reader.common.ReaderConstants;
import com.onyx.android.dr.reader.data.AfterReadingEntity;
import com.onyx.android.dr.reader.presenter.AfterReadingActivityPresenter;
import com.onyx.android.dr.reader.view.CustomDialog;
import com.onyx.android.sdk.utils.StringUtils;
import com.umeng.analytics.MobclickAgent;

public class AfterReadingActivity extends Activity implements AfterReadingView {

    private EditText edTitle;
    private EditText edContents;
    private AfterReadingActivityPresenter presenter;
    private AfterReadingEntity afterReadingEntity;
    private LinearLayout back;
    private TextView title;
    private TextView modifiedFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_reading);
        loadData();
        initView();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getSimpleName());
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getSimpleName());
        MobclickAgent.onPause(this);
    }

    private void initListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getNoteContent()) {
                    showSaveDialog();
                } else {
                    finish();
                }
            }
        });
        modifiedFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 17-8-12  
            }
        });
    }

    private void initView() {
        edTitle = (EditText) findViewById(R.id.edit_after_reading_title);
        edContents = (EditText) findViewById(R.id.edit_after_reading_contents);
        back = (LinearLayout) findViewById(R.id.menu_back);
        title = (TextView) findViewById(R.id.title_bar_title);
        modifiedFile = (TextView) findViewById(R.id.edit_after_reading_modified_file);
        title.setText(getString(R.string.after_reading));
    }

    private void loadData() {
        presenter = new AfterReadingActivityPresenter(this);
        Intent intent = getIntent();
        if (intent != null) {
            String md5 = intent.getStringExtra(ReaderConstants.AFTER_READING_ID);
            presenter.getAfterReadingEntity(md5);
        }
    }

    @Override
    public void setAfterReading(AfterReadingEntity entity) {
        if (entity != null) {
            setNoteDetail(entity);
        }
    }

    private void setNoteDetail(AfterReadingEntity entity) {
        this.afterReadingEntity = entity;
        edTitle.setText(entity.title);
        edContents.setText(entity.contents);
        edTitle.setSelection(entity.title.length());
        edContents.setSelection(entity.contents.length());
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (getNoteContent()) {
                showSaveDialog();
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private boolean getNoteContent() {
        boolean titleChange = StringUtils.isNotBlank(edTitle.getText().toString()) && !edTitle.getText().toString().equals(afterReadingEntity.title);
        boolean contentChange = StringUtils.isNotBlank(edContents.getText().toString()) && !edContents.getText().toString().equals(afterReadingEntity.contents);
        return titleChange || contentChange;
    }

    private void showSaveDialog() {
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setTitle(getString(R.string.prompt));
        builder.setMessage(getString(R.string.is_it_saved));
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        }).setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                saveNote();
            }
        }).create().show();
    }

    private void saveNote() {
        if (!TextUtils.isEmpty(edTitle.getText())) {
            afterReadingEntity.title = edTitle.getText().toString();
        } else {
            afterReadingEntity.title = getString(R.string.no_title);
        }
        if (!TextUtils.isEmpty(edContents.getText())) {
            afterReadingEntity.contents = edContents.getText().toString();
        }
        presenter.saveAfterReading(afterReadingEntity);
        finish();
    }
}
