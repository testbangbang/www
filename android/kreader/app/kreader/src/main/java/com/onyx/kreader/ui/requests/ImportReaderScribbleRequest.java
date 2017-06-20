package com.onyx.kreader.ui.requests;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.reader.host.wrapper.Reader;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.shape.NormalPencilShape;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.utils.CursorUtil;
import com.onyx.android.sdk.scribble.utils.SerializationUtils;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;
import com.onyx.android.sdk.utils.DateTimeUtil;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.kreader.note.data.ReaderNotePageNameMap;
import com.onyx.kreader.note.model.ReaderNoteDataProvider;
import com.onyx.kreader.note.model.ReaderNoteDocumentModel;
import com.onyx.kreader.note.model.ReaderNoteShapeModel;
import com.onyx.kreader.ui.data.ReaderDataHolder;

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

public class ImportReaderScribbleRequest extends BaseReaderRequest {

    private final String url = "content://com.onyx.android.sdk.OnyxCmsProvider/library_scribble";
    private final String OLD_READER_APPLICATION = "com.onyx.reader";
    private final String EXIST_APPLICATION = "com.onyx.reader.exist";

    private Context context;
    private int maxCount;
    private List<ReaderNoteShapeModel> readShapeModels = new ArrayList<>();
    private Set<String> existDocIds = new HashSet<>();
    private ReaderDataHolder readerDataHolder;

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

    public ImportReaderScribbleRequest(ReaderDataHolder readerDataHolder) {
        this.context = readerDataHolder.getContext();
        this.readerDataHolder = readerDataHolder;
    }

    public void execute(Reader reader) throws Exception {
        Cursor cursor = null;
        try {
            Uri uri = Uri.parse(url);
            cursor = context.getContentResolver().query(
                    uri, null, null, null,
                    null);
            if (cursor == null) {
                return;
            }

            maxCount = cursor.getCount();
            int index = 0;
            while (cursor.moveToNext()) {
                readColumnData(cursor, maxCount, index);
                index++;
            }
            saveShapeAndNote(context);
            readerDataHolder.getNoteManager().getNoteDocument().reloadPageIndex(readerDataHolder.getContext());
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void readColumnData(Cursor c, int count, int index) {
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

        BaseCallback.ProgressInfo progressInfo = new BaseCallback.ProgressInfo();
        progressInfo.progress = index;
        progressInfo.totalBytes = count;
        BaseCallback.invokeProgress(readerDataHolder.getReader().getLooperHandler(), getCallback(), this, progressInfo);

        if (StringUtils.isNullOrEmpty(application)) {
            return;
        }

        if (application.equals(EXIST_APPLICATION)) {
            existDocIds.add(md5);
        }

        if (!application.equals(OLD_READER_APPLICATION)) {
            return;
        }


        ReaderNoteShapeModel shapeModel = new ReaderNoteShapeModel();

        shapeModel.setDocumentUniqueId(md5);
        shapeModel.setPageUniqueId(position);
        shapeModel.setThickness((float) thickness);
        shapeModel.setAppId(application);
        shapeModel.setColor(color);
        if (!StringUtils.isNullOrEmpty(update_time)) {
            Date date = new Date(Long.valueOf(update_time));
            shapeModel.setUpdatedAt(date);
            shapeModel.setCreatedAt(date);
        }

        final TouchPointList points = SerializationUtils.pointsFromByteArray(pts);
        shapeModel.setPoints(points);
        Shape shape = new NormalPencilShape();
        if (StringUtils.isNullOrEmpty(uniqueId)) {
            shapeModel.generateShapeUniqueId();
        }else {
            shapeModel.setShapeUniqueId(uniqueId);
        }

        shapeModel.setSubPageUniqueId(getSubPageUniqueId(md5, position));
        shapeModel.setShapeType(shape.getType());
        for (TouchPoint touchPoint : points.getPoints()) {
            shapeModel.updateBoundingRect(touchPoint.x, touchPoint.y);
        }

        readShapeModels.add(shapeModel);
    }

    private String getSubPageUniqueId(String documentUniqueId, String pageUniqueId) {
        for (ReaderNoteShapeModel shapeModel : readShapeModels) {
            String docId = shapeModel.getDocumentUniqueId();
            String pageId = shapeModel.getPageUniqueId();
            if (docId.equals(documentUniqueId) && pageId.equals(pageUniqueId)) {
                return shapeModel.getSubPageUniqueId();
            }
        }

        List<ReaderNoteShapeModel> shapeModels = ReaderNoteDataProvider.loadShapeList(context, documentUniqueId, pageUniqueId, null);
        if (shapeModels.size() > 0) {
            ReaderNoteShapeModel shape = shapeModels.get(0);
            return shape.getSubPageUniqueId();
        }

        return ShapeUtils.generateUniqueId();
    }

    private void filterExistShape() {
        List<ReaderNoteShapeModel> newShapeModels = new ArrayList<>();
        for (ReaderNoteShapeModel shapeModel : readShapeModels) {
            String documentUniqueId = shapeModel.getDocumentUniqueId();
            if (!existDocIds.contains(documentUniqueId)) {
                newShapeModels.add(shapeModel);
            }
        }
        readShapeModels = newShapeModels;
    }

    private Map<String, ReaderNoteDocumentModel> createNoteModes() {
        Map<String, ReaderNoteDocumentModel> noteModelMap = new HashMap<>();
        for (ReaderNoteShapeModel shapeModel : readShapeModels) {
            String shapeUniqueId = shapeModel.getShapeUniqueId();
            String documentUniqueId = shapeModel.getDocumentUniqueId();
            String pageUniqueId = shapeModel.getPageUniqueId();
            String subPageUniqueId = shapeModel.getSubPageUniqueId();

            String title = "";
            Date createDate = shapeModel.getCreatedAt();
            if (createDate != null) {
                title = DateTimeUtil.formatDate(createDate);
            }

            ReaderNoteDocumentModel noteModel = getNoteDocument(noteModelMap, documentUniqueId, title);

            ReaderNotePageNameMap pageNameMap = noteModel.getReaderNotePageNameMap();
            if (pageNameMap == null ) {
                pageNameMap = new ReaderNotePageNameMap();
            }
            pageNameMap.add(pageUniqueId, subPageUniqueId);
            noteModel.setReaderNotePageNameMap(pageNameMap);
            noteModel.setCreatedAt(shapeModel.getCreatedAt());
        }
        return noteModelMap;
    }

    private ReaderNoteDocumentModel getNoteDocument(Map<String, ReaderNoteDocumentModel> noteModelMap, String documentUniqueId, String title) {
        ReaderNoteDocumentModel noteModel = null;
        noteModel = noteModelMap.get(documentUniqueId);
        if (noteModel == null) {
            noteModel = ReaderNoteDataProvider.loadDocument(context, documentUniqueId);
        }
        if (noteModel == null) {
            noteModel = ReaderNoteDocumentModel.createNote(documentUniqueId, null, title);
        }
        noteModelMap.put(documentUniqueId, noteModel);
        return noteModel;
    }

    private void saveShapeAndNote(final Context context) {
        filterExistShape();
        Map<String, ReaderNoteDocumentModel> noteModelMap = createNoteModes();
        List<ReaderNoteDocumentModel> newNoteModels = new ArrayList<>();
        for (String key : noteModelMap.keySet()) {
            newNoteModels.add(noteModelMap.get(key));
        }

        ReaderNoteDataProvider.saveShapeList(context, readShapeModels);
        ReaderNoteDataProvider.saveDocumentList(context, newNoteModels);

        for (ReaderNoteDocumentModel newNoteModel : newNoteModels) {
            String documentUniqueId = newNoteModel.getUniqueId();
            markerImportedData(documentUniqueId);
        }
    }

    private void markerImportedData(String documentUniqueId) {
        Uri uri = Uri.parse(url);
        ContentValues values = new ContentValues();
        values.put(MD5, documentUniqueId);
        values.put(APPLICATION, EXIST_APPLICATION);
        context.getContentResolver().insert(uri, values);
    }

    public int getImportCount() {
        return readShapeModels.size();
    }

    public int getMaxCount() {
        return maxCount;
    }

}
