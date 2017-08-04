package com.onyx.edu.reader.note.request;

import android.graphics.RectF;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.edu.reader.note.NoteManager;
import com.onyx.edu.reader.note.data.ReaderShapeFactory;
import com.onyx.edu.reader.note.model.ReaderNoteDataProvider;
import com.onyx.edu.reader.note.model.SignatureShapeModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 2017/8/1.
 */

public class LoadSignatureToFormRequest extends ReaderBaseNoteRequest {

    private String accountId;
    private RectF targetRect;
    private PageInfo pageInfo;

    public LoadSignatureToFormRequest(String accountId, RectF targetRect, PageInfo pageInfo) {
        this.accountId = accountId;
        this.targetRect = targetRect;
        this.pageInfo = pageInfo;
    }

    @Override
    public void execute(NoteManager noteManager) throws Exception {
        List<SignatureShapeModel> models = ReaderNoteDataProvider.loadSignatureShapeList(getContext(), accountId);
        List<Shape> shapes = new ArrayList<>();
        for (SignatureShapeModel model : models) {
            Shape shape = ReaderShapeFactory.shapeFromSignatureModel(model);
            transformShapePoints(shape);
            shapes.add(shape);
        }
        noteManager.addNewStashList(shapes);
    }

    private void transformShapePoints(final Shape shape) {
        RectF originRect = shape.getFormRect();
        TouchPointList pointList = shape.getPoints();
        List<TouchPoint> points = pointList.getPoints();
        for (TouchPoint point : points) {
            point.origin(pageInfo);
            point.x = (point.x - originRect.left) * targetRect.width() / originRect.width() + targetRect.left;
            point.y = (point.y - originRect.top) * targetRect.height() / originRect.height() + targetRect.top;
            point.normalize(pageInfo);
        }
        shape.updatePoints();
    }
}
