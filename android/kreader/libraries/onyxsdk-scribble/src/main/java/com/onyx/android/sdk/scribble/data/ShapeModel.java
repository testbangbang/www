package com.onyx.android.sdk.scribble.data;

import android.graphics.RectF;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.annotation.*;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Date;

/**
 * Created by zhuzeng on 6/3/16.
 * Data model for all shapes.
 */
@Table(database = ShapeDatabase.class)
public class ShapeModel extends BaseModel {

    private static final String TAG = ShapeModel.class.getSimpleName();
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
    String subPageName;

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

    @Column
    String groupId;

    @Column
    int layoutType;

    @Column
    float orientation = 0f;

    public ShapeModel() {
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

    public void setExtraAttributesBean(final ShapeExtraAttributes shapeExtraAttributes) {
        if (shapeExtraAttributes != null) {
            this.extraAttributes = JSON.toJSONString(shapeExtraAttributes);
        }

    }

    public ShapeExtraAttributes getExtraAttributesBean() {
        if (!StringUtils.isNullOrEmpty(extraAttributes)) {
            return JSON.parseObject(extraAttributes, ShapeExtraAttributes.class);
        }
        return null;
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

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public int getLayoutType() {
        return layoutType;
    }

    public void setLayoutType(int layoutType) {
        this.layoutType = layoutType;
    }

    public float getOrientation() {
        return orientation;
    }

    public void setOrientation(float orientation) {
        this.orientation = orientation;
    }
}