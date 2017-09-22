package com.onyx.android.sdk.scribble.shape;

import android.graphics.PointF;
import android.graphics.RectF;

import com.onyx.android.sdk.scribble.data.MirrorType;
import com.onyx.android.sdk.scribble.data.ShapeExtraAttributes;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.formshape.FormValue;

/**
 * Created by zhuzeng on 4/19/16.
 * create in main thread and calculate render data in background.
 */
public interface Shape {

    static int STATE_NORMAL      = 0;
    static int STATE_SELECTED    = 1;
    static int STATE_MOVING      = 2;
    static int STATE_RESIZING    = 3;

    /**
     * rectangle, circle, etc.
     * @return
     */
    int getType();

    void setDocumentUniqueId(final String documentUniqueId);

    String getDocumentUniqueId();

    void setPageUniqueId(final String pageId);

    String getPageUniqueId();

    void setSubPageUniqueId(final String id);

    String getSubPageUniqueId();

    void setShapeUniqueId(final String uniqueId);

    String getShapeUniqueId();

    void ensureShapeUniqueId();

    void setPageOriginWidth(int width);

    int getPageOriginWidth();

    void setPageOriginHeight(int height);

    int getPageOriginHeight();

    int getZOrder();

    void setZOrder(int order);

    int getColor();

    void setColor(int color);

    float getStrokeWidth();

    void setStrokeWidth(final float width);

    void setDisplayStrokeWidth(final float width);

    RectF getBoundingRect();

    void updateBoundingRect();

    void resetBoundingRect();

    void moveTo(final float x, final float y);

    void resize(final float width, final float height);

    float getOrientation();

    void setOrientation(float orientation);

    void onDown(final TouchPoint normalizedPoint, final TouchPoint screenPoint);

    void onMove(final TouchPoint normalizedPoint, final TouchPoint screenPoint);

    void onUp(final TouchPoint normalizedPoint, final TouchPoint screenPoint);

    void onTranslate(final float dx, final float dy);

    void onRotate(final float angle, PointF originPoint);

    void onScale(final float scale);

    void onMirror(final MirrorType type,int translateDistance);

    void updatePoints();

    void addPoints(final TouchPointList points);

    TouchPointList getPoints();

    boolean supportDFB();

    void render(final RenderContext renderContext);

    boolean hitTest(final float x, final float y, final float radius);

    boolean fastHitTest(final float x, final float y, final float radius);

    void clear();

    void setGroupId(String groupId);

    String getGroupId();

    void setLayoutType(int layoutType);

    int getLayoutType();

    ShapeExtraAttributes getShapeExtraAttributes();

    void setShapeExtraAttributes(final ShapeExtraAttributes shapeExtraAttributes);

    boolean isFreePosition();

    String getFormId();

    Integer getFormType();

    RectF getFormRect();

    FormValue getFormValue();

    boolean isFormShape();

    void setFormShape(boolean formShape);

    void setFormId(String formId);

    void setFormType(Integer formType);

    void setFormRect(RectF formRect);

    void setFormValue(FormValue formValue);

    boolean isLock();

    void setLock(boolean lock);

    boolean isReview();

    void setReview(boolean review);

    boolean inVisibleDrawRectF(RectF rect);

    TouchPoint getCurrentPoint();

    TouchPoint getCurrentScreenPoint();

    void setScale(float targetScaleValue);

    float getScale();

    void setSelected(boolean isSelected);

    boolean isSelected();

    int getRevision();

    void setRevision(int revision);

    boolean canModified(int documentReviewRevision);

    float getSelectRectOrientation();

    void setSelectRectOrientation(float selectRectOrientation);

    float getRotationPointXCoordinate();

    void setRotationPointXCoordinate(float xCoordinate);

    float getRotationPointYCoordinate();

    void setRotationPointYCoordinate(float yCoordinate);

}
