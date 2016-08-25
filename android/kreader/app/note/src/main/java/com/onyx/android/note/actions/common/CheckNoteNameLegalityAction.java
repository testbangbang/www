package com.onyx.android.note.actions.common;

import android.app.Activity;

import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.note.activity.BaseManagerActivity;
import com.onyx.android.note.activity.BaseScribbleActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.note.NoteNameCheckLegalityRequest;

/**
 * Created by solskjaer49 on 16/7/20 19:30.
 */

public class CheckNoteNameLegalityAction<T extends Activity> extends BaseNoteAction<T> {
    public CheckNoteNameLegalityAction(String targetName, String parentID, int curType,
                                       boolean checkThisLevelOnly, boolean distinguishFileType) {
        this.targetName = targetName;
        this.parentID = parentID;
        this.checkThisLevelOnly = checkThisLevelOnly;
        this.distinguishFileType = distinguishFileType;
        this.curType = curType;
    }

    private String targetName;
    private String parentID;
    private boolean checkThisLevelOnly;
    private boolean distinguishFileType;
    private int curType;

    public boolean isLegal() {
        return isLegal;
    }

    private boolean isLegal;

    @Override
    public void execute(T activity, final BaseCallback callback) {
        final NoteNameCheckLegalityRequest legalityRequest = new NoteNameCheckLegalityRequest(
                targetName, parentID, curType, checkThisLevelOnly, distinguishFileType);
        if (activity instanceof BaseScribbleActivity) {
            ((BaseScribbleActivity) activity).submitRequest(legalityRequest, new BaseCallback() {
                @Override
                public void done(BaseRequest request, Throwable e) {
                    isLegal = legalityRequest.isLegal();
                    callback.done(request, e);
                }
            });
        } else if (activity instanceof BaseManagerActivity) {
            ((BaseManagerActivity) activity).submitRequest(legalityRequest, new BaseCallback() {
                @Override
                public void done(BaseRequest request, Throwable e) {
                    isLegal = legalityRequest.isLegal();
                    callback.done(request, e);
                }
            });
        }
    }
}
