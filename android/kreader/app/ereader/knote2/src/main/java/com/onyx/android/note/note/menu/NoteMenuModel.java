package com.onyx.android.note.note.menu;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.onyx.android.note.NoteDataBundle;
import com.onyx.android.note.action.PenWidthChangeAction;
import com.onyx.android.note.common.StrokeWidth;
import com.onyx.android.note.common.base.BaseViewModel;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.note.NoteManager;
import com.onyx.android.sdk.utils.FileUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

/**
 * Created by lxm on 2018/2/2.
 */

public class NoteMenuModel extends BaseViewModel {

    private static final String TAG = "NoteMenuModel";
    
    public NoteMenuModel(@NonNull EventBus eventBus) {
        super(eventBus);
    }

    public void onPenWidth1(View view) {
        Log.d(TAG, "onPenWidth1: ");
        new PenWidthChangeAction(getNoteManager())
                .setPenWidth(StrokeWidth.LIGHT.getWidth())
                .setResumeRawDraw(true)
                .execute(null);
    }

    public void onPenWidth2(View view) {
        Log.d(TAG, "onPenWidth2: ");
        new PenWidthChangeAction(getNoteManager())
                .setPenWidth(StrokeWidth.ULTRA_BOLD.getWidth())
                .setResumeRawDraw(true)
                .execute(null);
    }

    private NoteManager getNoteManager() {
        return NoteDataBundle.getInstance().getNoteManager();
    }
}
