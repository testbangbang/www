package com.onyx.edu.reader.ui.handler.form;

import android.graphics.Rect;

import com.alibaba.fastjson.TypeReference;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.im.Constant;
import com.onyx.android.sdk.im.IMConfig;
import com.onyx.android.sdk.im.data.JoinModel;
import com.onyx.android.sdk.im.data.Message;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.edu.reader.device.DeviceConfig;
import com.onyx.edu.reader.note.data.ReaderShapeFactory;
import com.onyx.edu.reader.note.model.ReaderFormShapeModel;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;
import com.onyx.edu.reader.ui.handler.HandlerManager;
import com.onyx.edu.reader.ui.service.ReaderFormIMService;

import java.util.List;

import io.socket.client.Socket;

import static com.onyx.android.sdk.data.Constant.MEETING_API_BASE;

/**
 * Created by ming on 2017/8/1.
 */

public class FormMeetingHandler extends FormBaseHandler {

    public FormMeetingHandler(HandlerManager parent) {
        super(parent);
    }

    @Override
    protected boolean isEnableNoteWhenHaveScribbleForm() {
        return false;
    }

    @Override
    public Rect getFormScribbleRect() {
        return null;
    }

    @Override
    public void onActivate(ReaderDataHolder readerDataHolder, HandlerInitialState initialState) {
        super.onActivate(readerDataHolder, initialState);
    }

    @Override
    public void activeIMService() {
        startFormIMService(MEETING_API_BASE, Constant.EVENT_MEETING);
    }

    @Override
    protected void onReceivedIMMessage(Message message) {
        Debug.d(getClass(), "meeting message:" + message.getAction());
        switch (message.getAction()) {
            case Socket.EVENT_CONNECT:
                getImService().joinRoom(JoinModel.create(Constant.MEETING, getReaderDataHolder().getAccount().name, NetworkUtil.getMacAddress(getContext())));
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
    public void onShapeAdded(Shape shape) {
        Debug.d(getClass(), "add shape");
        Message message = Message.create(Constant.EVENT_ADD_SHAPE, JSONObjectParseUtils.toJson(ReaderShapeFactory.modelFromShape(shape)));
        getImService().emit(message);
    }

    @Override
    public void onShapesRemoved(List<String> shapeIds) {
        Debug.d(getClass(), "remove shapes:" + JSONObjectParseUtils.toJson(shapeIds));
        Message message = Message.create(Constant.EVENT_REMOVE_SHAPE, JSONObjectParseUtils.toJson(shapeIds));
        getImService().emit(message);
    }
}
