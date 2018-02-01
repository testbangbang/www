package com.onyx.android.note.test;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.onyx.android.note.NoteDataBundle;
import com.onyx.android.note.NoteUIBundle;
import com.onyx.android.note.R;
import com.onyx.android.note.common.base.BaseActivity;
import com.onyx.android.note.databinding.ActivityTestBinding;

public class TestActivity extends BaseActivity {

    private ActivityTestBinding binding;
    private TestActivityHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_test);
        binding.setModel(NoteUIBundle.getInstance().getTestViewModel());
        handler = new TestActivityHandler(NoteDataBundle.getInstance().getEventBus());
        handler.subscribe();
    }

    @Override
    public boolean isFullScreen() {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.unsubscribe();
    }
}
