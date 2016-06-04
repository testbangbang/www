package com.onyx.kreader.scribble.data;

import android.graphics.RectF;
import com.onyx.kreader.dataprovider.ReaderDatabase;
import com.raizlabs.android.dbflow.annotation.*;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Date;
import java.util.UUID;

/**
 * Created by zhuzeng on 6/3/16.
 * Data model for all shapes.
 */
@Table(database = ReaderDatabase.class)
public class ShapeModel extends BaseModel {

    private static final String TAG = ShapeModel.class.getSimpleName();
    public static final int INVALID_ID = -1;

    @Column
    @PrimaryKey(autoincrement = true)
    @Index
    long id;

    @Column
    String md5 ;

    @Column
    @Unique
    String uniqueId;

    @Column
    Date createdAt = null;

    @Column
    Date updatedAt = null;

    @Column
    String pageName;

    @Column
    String subPageName;

    @Column
    int color;

    @Column
    float thickness;

    @Column
    int zorder;

    @Column(typeConverter = TouchPointListConverter.class)
    TouchPointList points;

    @Column
    String position;

    @Column(typeConverter = RectangleConverter.class)
    RectF boundingRect = null;

    @Column
    int shapeType;

    @Column
    String extraAttributes;

    public ShapeModel() {
    }

    public long getId() {
        return id;
    }

    public void setId(long value) {
        id = value;
    }

    public final String getMd5() {
        return md5;
    }

    public void setMd5(final String value) {
        md5 = value;
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

    public String getPageName() {
        return pageName;
    }

    public void setPageName(final String name) {
        pageName = name;
    }

    public String getSubPageName() {
        return subPageName;
    }

    public void setSubPageName(final String spn) {
        subPageName = spn;
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

    public String getPosition() {
        return position;
    }

    public void setPosition(final String pos) {
        position = pos;
    }

    public final String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(final String id) {
        uniqueId = id;
    }

    public String generateUniqueId() {
        return uniqueId = UUID.randomUUID().toString();
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
}