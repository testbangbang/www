package com.onyx.android.dr.reader.note.model;

import android.graphics.RectF;
import com.onyx.android.sdk.scribble.data.ConverterRectangle;
import com.onyx.android.sdk.scribble.data.ConverterTouchPointList;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;
import com.raizlabs.android.dbflow.annotation.*;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Date;

/**
 * Created by zhuzeng on 9/16/16.
 */
@Table(database = ReaderNoteDatabase.class)
public class ReaderNoteShapeModel extends BaseModel {

    private static final String TAG = ReaderNoteShapeModel.class.getSimpleName();
    public static final int INVALID_ID = -1;

    @Column
    @PrimaryKey(autoincrement = true)
    @Index
    long id;

    @Column
    @Unique
    String shapeUniqueId;

    @Column
    Date createdAt = null;

    @Column
    Date updatedAt = null;

    @Column
    String documentUniqueId;

    @Column
    String pageUniqueId;

    @Column
    String subPageUniqueId;

    @Column
    String appId;

    @Column
    int pageOriginWidth;

    @Column
    int pageOriginHeight;

    @Column
    int color;

    @Column
    float thickness;

    @Column
    int zorder;

    @Column(typeConverter = ConverterTouchPointList.class)
    TouchPointList points;

    @Column(typeConverter = ConverterRectangle.class)
    RectF boundingRect = null;

    @Column
    int shapeType;

    @Column
    String extraAttributes;

    public ReaderNoteShapeModel() {
    }

    public long getId() {
        return id;
    }

    public void setId(long value) {
        id = value;
    }

    public void setCreatedAt(final Date d) {
        createdAt = d;
    }

    public final Date getCreatedAt() {
        return createdAt;
    }

    public final Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(final Date d) {
        updatedAt = d;
    }

    public String getDocumentUniqueId() {
        return documentUniqueId;
    }

    public void setDocumentUniqueId(final String id) {
        documentUniqueId = id;
    }

    public String getPageUniqueId() {
        return pageUniqueId;
    }

    public void setPageUniqueId(final String name) {
        pageUniqueId = name;
    }

    public String getSubPageUniqueId() {
        return subPageUniqueId;
    }

    public void setSubPageUniqueId(final String spn) {
        subPageUniqueId = spn;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int c) {
        color = c;
    }

    public float getThickness() {
        return thickness;
    }

    public void setThickness(float t) {
        thickness = t;
    }

    public TouchPointList  getPoints() {
        return points;
    }

    public TouchPointList allocatePoints(int size) {
        points = new TouchPointList(size);
        return points;
    }

    public void setPoints(final TouchPointList pts) {
        points = pts;
    }

    public final String getShapeUniqueId() {
        return shapeUniqueId;
    }

    public void setShapeUniqueId(final String id) {
        shapeUniqueId = id;
    }

    public String generateShapeUniqueId() {
        return shapeUniqueId = ShapeUtils.generateUniqueId();
    }

    public void updateBoundingRect(final float x, final float y) {
        if (boundingRect == null) {
            boundingRect = new RectF(x, y, x, y);
        } else {
            boundingRect.union(x, y);
        }
    }

    public void setBoundingRect(final RectF rect) {
        boundingRect = rect;
    }

    public final RectF getBoundingRect() {
        return boundingRect;
    }

    public int getShapeType() {
        return shapeType;
    }

    public void setShapeType(int t) {
        shapeType = t;
    }

    public String getExtraAttributes() {
        return extraAttributes;
    }

    public void setExtraAttributes(final String attributes) {
        extraAttributes = attributes;
    }

    public int getZorder() {
        return zorder;
    }

    public void setZorder(int order) {
        zorder = order;
    }

    public void setAppId(final String id) {
        appId = id;
    }

    public String getAppId() {
        return appId;
    }

    public int getPageOriginWidth() {
        return pageOriginWidth;
    }

    public void setPageOriginWidth(int pageOriginWidth) {
        this.pageOriginWidth = pageOriginWidth;
    }

    public int getPageOriginHeight() {
        return pageOriginHeight;
    }

    public void setPageOriginHeight(int pageOriginHeight) {
        this.pageOriginHeight = pageOriginHeight;
    }
}
