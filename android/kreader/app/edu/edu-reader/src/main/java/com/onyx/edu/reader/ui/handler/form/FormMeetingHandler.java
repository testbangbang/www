package com.onyx.edu.reader.ui.handler.form;

import android.content.DialogInterface;
import android.graphics.Rect;
import android.view.View;

import com.alibaba.fastjson.TypeReference;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.im.Constant;
import com.onyx.android.sdk.im.data.Message;
import com.onyx.android.sdk.reader.api.ReaderFormAction;
import com.onyx.android.sdk.reader.api.ReaderFormField;
import com.onyx.android.sdk.reader.api.ReaderFormPushButton;
import com.onyx.android.sdk.reader.api.ReaderFormUse;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.ui.dialog.OnyxCustomDialog;
import com.onyx.android.sdk.ui.view.RelativeRadioGroup;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.edu.reader.R;
import com.onyx.edu.reader.note.actions.LoadFormShapesAction;
import com.onyx.edu.reader.note.data.ReaderShapeFactory;
import com.onyx.edu.reader.note.model.ReaderFormShapeModel;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;
import com.onyx.edu.reader.ui.handler.HandlerManager;

import java.util.List;

import io.socket.client.Socket;

import static com.onyx.android.sdk.data.Constant.MEETING_API_BASE;

/**
 * Created by ming on 2017/8/1.
 */

public class FormMeetingHandler extends FormBaseHandler {

    private boolean lockVoteFormView = false;

    public FormMeetingHandler(HandlerManager parent) {
        super(parent);
    }

    @Override
    public boolean isEnableNoteWhenHaveScribbleForm() {
        return false;
    }

    @Override
    public void onActivate(ReaderDataHolder readerDataHolder, HandlerInitialState initialState) {
        super.onActivate(readerDataHolder, initialState);
        lockVoteFormView();
    }

    @Override
    public void activeIMService() {
        startFormIMService(MEETING_API_BASE, Constant.EVENT_MEETING);
    }

    @Override
    public void onReceivedIMMessage(Message message) {
        Debug.d(getClass(), "meeting message:" + message.getAction());
        switch (message.getAction()) {
            case Socket.EVENT_CONNECT:
                join(Constant.EVENT_MEETING);
                break;
            case Constant.ACTION_ADD_SHAPES:
                List<ReaderFormShapeModel> shapeModels = JSONObjectParseUtils.toBean(message.getContent(), new TypeReference<List<ReaderFormShapeModel>>() {}.getType());
                saveFormShapesFromCloud(shapeModels);
                break;
            case Constant.ACTION_REMOVE_SHAPES:
                List<String> shapeIds = JSONObjectParseUtils.toBean(message.getContent(), new TypeReference<List<String>>() {}.getType());
                removeFormShapesFromCloud(shapeIds);
                break;
        }
    }

    @Override
    protected void onFormButtonClicked(ReaderFormPushButton field) {
        ReaderFormUse use = field.getFormUse();
        ReaderFormAction action = field.getFormAction();
        if (use == ReaderFormUse.VOTE && action == ReaderFormAction.SUBMIT) {
            submitVoteForm();
        }else if (action == ReaderFormAction.EXIT) {
            close(getReaderDataHolder());
        }
    }

    @Override
    public void close(ReaderDataHolder readerDataHolder) {
        super.close(readerDataHolder);
    }

    private void submitVoteForm() {
        OnyxCustomDialog.getConfirmDialog(getContext(), getContext().getResources().getString(R.string.submit_vote_tips), true, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final LoadFormShapesAction shapesAction = new LoadFormShapesAction();
                shapesAction.execute(getReaderDataHolder(), new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        List<ReaderFormShapeModel> shapeModels = shapesAction.getReaderFormShapeModels();
                        submitVoteFormImpl(shapeModels);
                    }
                });
            }
        }, null).show();
    }

    private void submitVoteFormImpl(List<ReaderFormShapeModel> shapeModels) {
        if (shapeModels == null || shapeModels.size() == 0) {
            return;
        }
        getReaderDataHolder().emitIMMessage(Message.create(Constant.EVENT_MEETING, Constant.ACTION_SUBMIT_VOTE, JSONObjectParseUtils.toJson(shapeModels), Constant.EVENT_MEETING));
        setLockVoteFormView(true);
        lockVoteFormView();
    }

    private void lockVoteFormView() {
        if (!isLockVoteFormView()) {
            return;
        }
        List<View> formFieldControls = getFormFieldControls();
        if (formFieldControls == null) {
            return;
        }
        for (View formFieldControl : formFieldControls) {
            ReaderFormField formField = getReaderFormField(formFieldControl);
            if (formField.getFormUse() != ReaderFormUse.VOTE) {
                continue;
            }
            formFieldControl.setEnabled(false);
            if (formFieldControl instanceof RelativeRadioGroup) {
                lockRadioGroupChildView((RelativeRadioGroup) formFieldControl);
            }
        }
    }

    private void lockRadioGroupChildView(RelativeRadioGroup group) {
        int childCount = group.getChildCount();
        for (int i = 0; i < childCount; i++) {
            group.getChildAt(i).setEnabled(false);
        }
    }


    public boolean isLockVoteFormView() {
        return lockVoteFormView;
    }

    public void setLockVoteFormView(boolean lockVoteFormView) {
        this.lockVoteFormView = lockVoteFormView;
    }
}
