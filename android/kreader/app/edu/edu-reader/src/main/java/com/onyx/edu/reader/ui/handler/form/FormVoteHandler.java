package com.onyx.edu.reader.ui.handler.form;

import android.content.DialogInterface;
import android.view.View;
import android.widget.RadioGroup;

import com.alibaba.fastjson.TypeReference;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.ReaderMenuAction;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.im.Constant;
import com.onyx.android.sdk.im.IMConfig;
import com.onyx.android.sdk.im.data.JoinModel;
import com.onyx.android.sdk.im.data.Message;
import com.onyx.android.sdk.reader.api.ReaderFormPushButton;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.ui.dialog.OnyxCustomDialog;
import com.onyx.android.sdk.ui.view.RelativeRadioGroup;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.edu.reader.R;
import com.onyx.edu.reader.note.actions.LoadFormShapesAction;
import com.onyx.edu.reader.note.model.ReaderFormShapeModel;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;
import com.onyx.edu.reader.ui.handler.HandlerManager;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;

import static com.onyx.android.sdk.data.Constant.MEETING_API_BASE;

/**
 * Created by ming on 2017/7/26.
 */

public class FormVoteHandler extends FormBaseHandler {

    private boolean lockFormView = false;

    public FormVoteHandler(HandlerManager parent) {
        super(parent);
    }

    @Override
    public boolean isEnableNoteDrawing() {
        return false;
    }

    @Override
    public void onActivate(ReaderDataHolder readerDataHolder, HandlerInitialState initialState) {
        super.onActivate(readerDataHolder, initialState);
        lockFormView();
    }

    @Override
    public void activeIMService() {
        startFormIMService(MEETING_API_BASE, Constant.EVENT_VOTE);
    }

    @Override
    public void onReceivedIMMessage(Message message) {
        Debug.d(getClass(), "vote message:" + message.getAction());
        switch (message.getAction()) {
            case Socket.EVENT_CONNECT:
                join(Constant.EVENT_VOTE);
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
    public void close(ReaderDataHolder readerDataHolder) {
        super.close(readerDataHolder);
    }

    @Override
    public boolean onMenuClicked(ReaderMenuAction action) {
        return false;
    }

    @Override
    protected void onFormButtonClicked(ReaderFormPushButton field) {
        switch (field.getFormAction()) {
            case SUBMIT:
                submit();
                break;
            case EXIT:
                close(getReaderDataHolder());
                break;
        }
    }

    private void submit() {
        OnyxCustomDialog.getConfirmDialog(getContext(), getContext().getResources().getString(R.string.submit_vote_tips), true, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final LoadFormShapesAction shapesAction = new LoadFormShapesAction();
                shapesAction.execute(getReaderDataHolder(), new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        List<ReaderFormShapeModel> shapeModels = shapesAction.getReaderFormShapeModels();
                        submitVoteForm(shapeModels);
                    }
                });
            }
        }, null).show();
    }

    private void submitVoteForm(List<ReaderFormShapeModel> shapeModels) {
        if (shapeModels == null || shapeModels.size() == 0) {
            return;
        }
        getReaderDataHolder().emitIMMessage(Message.create(Constant.EVENT_VOTE, JSONObjectParseUtils.toJson(shapeModels)));
        setLockFormView(true);
        lockFormView();
    }

    private void lockFormView() {
        if (!isLockFormView()) {
            return;
        }
        List<View> formFieldControls = getFormFieldControls();
        if (formFieldControls == null) {
            return;
        }
        for (View formFieldControl : formFieldControls) {
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


    public boolean isLockFormView() {
        return lockFormView;
    }

    public void setLockFormView(boolean lockFormView) {
        this.lockFormView = lockFormView;
    }
}
