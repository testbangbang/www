package com.onyx.android.note.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.onyx.android.note.NoteApplication;
import com.onyx.android.note.R;
import com.onyx.android.note.utils.NoteAppConfig;
import com.onyx.android.sdk.utils.ActivityUtil;

public class StartupActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NoteApplication.initWithAppConfig(this);
        setContentView(R.layout.activity_startup);
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        startHomeActivityByDeviceConfig(NoteAppConfig.sharedInstance(this));
    }

    private boolean startHomeActivityByDeviceConfig(final NoteAppConfig deviceConfig) {
        Intent intent = deviceConfig.getNoteHomeIntent(this);
        if (ActivityUtil.startActivitySafely(this, intent)) {
            finish();
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_startup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
