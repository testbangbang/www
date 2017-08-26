package com.onyx.android.sdk.scribble.asyncrequest.note;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.net.Uri;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.data.PageNameList;
import com.onyx.android.sdk.scribble.data.ShapeDataProvider;
import com.onyx.android.sdk.scribble.data.ShapeModel;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.shape.NormalPencilShape;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.utils.CursorUtil;
import com.onyx.android.sdk.scribble.utils.SerializationUtils;
import com.onyx.android.sdk.utils.DateTimeUtil;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.provider.BaseColumns._ID;

/**
 * Created by ming on 2016/12/2.
 */

public class ImportScribbleRequest extends AsyncBaseNoteRequest {

    public final static String OLD_SCRIBBLE_URL = "content://com.onyx.android.sdk.OnyxCmsProvider/library_scribble";
    private final String OLD_SCRIBBLE_APPLICATION = "com.onyx.android.scribbler";

    private Context context;
    private int maxCount;
    private List<ShapeModel> newShapeModels = new ArrayList<>();
    private Map<String, NoteModel> noteModelMap = new HashMap<>();
    private Set<String> existDocIds = new HashSet<>();
    private float scaleValue = 0.9f;

    private String MD5 = "MD5";
    private String PAGE = "Page";
    private String COLOR = "Color";
    private String THICKNESS = "Thickness";
    private String POINTS = "Points";
    private String UPDATE_TIME = "UpdateTime";
    private String APPLICATION = "Application";
    private String POSITION = "Position";
    private String POINTS_BLOB = "PointsBlob";
    private String UNIQUE_ID = "uniqueId";

    private int sColumnID = -1;
    private int sColumnMD5 = -1;
    private int sColumnPage = -1;
    private int sColumnColor = -1;
    private int sColumnThickness = -1;
    private int sColumnPoints = -1;
    private int sColumnUpdateTime = -1;
    private int sColumnApplication = -1;
    private int sColumnPosition = -1;
    private int sColumnPointsBlob = -1;
    private int sColumnUniqueId = -1;

    private boolean columnIndexesInitialized = false;

    public ImportScribbleRequest(Context context) {
        this.context = context;
    }

    @Override
    public void execute(final NoteManager helper) throws Exception {
        Cursor cursor = null;
        try {
            Uri uri = Uri.parse(OLD_SCRIBBLE_URL);
            cursor = context.getContentResolver().query(
                    uri, null, null, null,
                    null);
            if (cursor == null) {
                return;
            }

            maxCount = cursor.getCount();
            int index = 0;
            while (cursor.moveToNext()) {
                readColumnData(helper, cursor, maxCount, index);
                index++;
            }
            saveShapeAndNote(context);
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void readColumnData(final NoteManager helper, Cursor c, int count, int index) {
        if (!columnIndexesInitialized) {
            sColumnID = c.getColumnIndex(_ID);
            sColumnMD5 = c.getColumnIndex(MD5);
            sColumnPage = c.getColumnIndex(PAGE);
            sColumnColor = c.getColumnIndex(COLOR);
            sColumnThickness = c.getColumnIndex(THICKNESS);
            sColumnPoints = c.getColumnIndex(POINTS);
            sColumnUpdateTime = c.getColumnIndex(UPDATE_TIME);
            sColumnApplication = c.getColumnIndex(APPLICATION);
            sColumnPosition = c.getColumnIndex(POSITION);
            sColumnPointsBlob = c.getColumnIndex(POINTS_BLOB);
            sColumnUniqueId = c.getColumnIndex(UNIQUE_ID);
            columnIndexesInitialized = true;
        }

        Long id = CursorUtil.getLong(c, sColumnID);
        String md5 = CursorUtil.getString(c, sColumnMD5);
        Integer page = CursorUtil.getInt(c, sColumnPage);
        Integer color = CursorUtil.getInt(c, sColumnColor);
        double thickness = c.getDouble(sColumnThickness);
        String update_time = CursorUtil.getString(c, sColumnUpdateTime);
        String application = CursorUtil.getString(c, sColumnApplication);
        String position = CursorUtil.getString(c, sColumnPosition);
        byte[] pts = CursorUtil.getBlob(c, sColumnPointsBlob);
        String uniqueId = CursorUtil.getString(c, sColumnUniqueId);

        if (StringUtils.isNullOrEmpty(uniqueId)
                || StringUtils.isNullOrEmpty(md5)
                || StringUtils.isNullOrEmpty(position)) {
            return;
        }
        if (StringUtils.isNullOrEmpty(application) || !application.equals(OLD_SCRIBBLE_APPLICATION)) {
            return;
        }

        ShapeModel shapeModel = new ShapeModel();

        shapeModel.setDocumentUniqueId(md5);
        shapeModel.setPageUniqueId(position);
        shapeModel.setThickness((float) thickness);
        shapeModel.setAppId(application);
        shapeModel.setColor(color != null ? color : Color.BLACK);
        if (!StringUtils.isNullOrEmpty(update_time)) {
            Date date = new Date(Long.valueOf(update_time));
            shapeModel.setUpdatedAt(date);
            shapeModel.setCreatedAt(date);
        }

        final TouchPointList points = SerializationUtils.pointsFromByteArray(pts);
        points.scaleAllPoints(scaleValue);
        shapeModel.setPoints(points);
        Shape shape = new NormalPencilShape();
        shapeModel.generateShapeUniqueId();
        shapeModel.setShapeType(shape.getType());
        for (TouchPoint touchPoint : points.getPoints()) {
            shapeModel.updateBoundingRect(touchPoint.x, touchPoint.y);
        }

        if (!hasImported(md5)) {
            newShapeModels.add(shapeModel);
        }

        BaseCallback.ProgressInfo progressInfo = new BaseCallback.ProgressInfo();
        progressInfo.progress = index;
        progressInfo.totalBytes = count;
        BaseCallback.invokeProgress(helper.getRequestManager().getLooperHandler(), getCallback(), this, progressInfo);
    }

    private List<NoteModel> createNoteModes() {
        List<NoteModel> newNoteModes = new ArrayList<>();
        for (ShapeModel shapeModel : newShapeModels) {
            String shapeUniqueId = shapeModel.getShapeUniqueId();
            String documentUniqueId = shapeModel.getDocumentUniqueId();
            String pageUniqueId = shapeModel.getPageUniqueId();

            String title = "";
            Date createDate = shapeModel.getCreatedAt();
            if (createDate != null) {
                title = DateTimeUtil.formatDate(createDate);
            }

            NoteModel noteModel = noteModelMap.get(documentUniqueId);
            if (noteModel == null) {
                noteModel = NoteModel.createNote(documentUniqueId, null, title);
            }

            PageNameList pageNameList = noteModel.getPageNameList();
            if (pageNameList == null ) {
                pageNameList = new PageNameList();
            }
            pageNameList.add(pageUniqueId);
            noteModel.setPageNameList(pageNameList);
            noteModel.setCreatedAt(shapeModel.getCreatedAt());
            newNoteModes.add(noteModel);
            noteModelMap.put(documentUniqueId, noteModel);
        }
        return newNoteModes;
    }

    private boolean hasImported(String documentUniqueId) {
        if (existDocIds.contains(documentUniqueId)) {
            return true;
        }
        NoteModel model = NoteDataProvider.load(context, documentUniqueId);
        if (model != null) {
            existDocIds.add(documentUniqueId);
            return true;
        }
        return false;
    }

    private void saveShapeAndNote(final Context context) {
        List<NoteModel> newNoteModels = createNoteModes();
        ShapeDataProvider.saveShapeList(context, newShapeModels);
        NoteDataProvider.saveNoteList(context, newNoteModels);
    }

    public int getImportCount() {
        return newShapeModels.size();
    }

    public int getMaxCount() {
        return maxCount;
    }
}
