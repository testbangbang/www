package com.onyx.android.sdk.scribble.shape;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

import com.onyx.android.sdk.scribble.data.MirrorType;
import com.onyx.android.sdk.scribble.data.ShapeExtraAttributes;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.formshape.FormValue;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;

import java.util.List;

/**
 * Created by zhuzeng on 6/23/16.
 */
public class BaseShape implements Shape {

    private RectF boundingRect;
    private TouchPointList normalizedPoints = new TouchPointList(400);
    private TouchPoint downPoint = new TouchPoint();
    private TouchPoint currentPoint = new TouchPoint();
    private TouchPoint currentScreenPoint = new TouchPoint();
    private String uniqueId;
    private String documentUniqueId;
    private String pageUniqueId;
    private String subPageUniqueId;
    private String groupId;
    private ShapeExtraAttributes shapeExtraAttributes;
    private int layoutType;
    private int color = Color.BLACK;
    private float strokeWidth;
    private float displayStrokeWidth;
    private Path originDisplayPath;
    private int originWidth;
    private int originHeight;

    private boolean isFormShape;
    private String formId;
    private Integer formType;
    private RectF formRect;
    private FormValue formValue;
    private boolean lock;
    private boolean review;
    private int revision;

    private boolean selected = false;
    private float scale = 1.0f;
    private float orientation = 0f;
    private float selectRectOrientation = 0f;

    /**
     * rectangle, circle, etc.
     * @return
     */
    public int getType() {
        return ShapeFactory.SHAPE_INVALID;
    }

    public void setDocumentUniqueId(final String id) {
        documentUniqueId = id;
    }

    public String getDocumentUniqueId() {
        return documentUniqueId;
    }

    public void setPageUniqueId(final String pageId) {
        pageUniqueId = pageId;
    }

    public String getPageUniqueId() {
        return pageUniqueId;
    }

    public String getSubPageUniqueId() {
        return subPageUniqueId;
    }

    public void setSubPageUniqueId(String subPageUniqueId) {
        this.subPageUniqueId = subPageUniqueId;
    }

    public void setShapeUniqueId(final String id) {
        uniqueId = id;
    }

    public String getShapeUniqueId() {
        return uniqueId;
    }

    public void ensureShapeUniqueId() {
        if (uniqueId == null || uniqueId.trim().isEmpty()) {
            uniqueId = ShapeUtils.generateUniqueId();
        }
    }

    public int getZOrder() {
        return 0;
    }

    public void setZOrder(int order) {
    }

    public boolean isAddMovePoint() {
        return true;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(final float width) {
        strokeWidth = width;
    }

    public float getDisplayStrokeWidth() {
        return displayStrokeWidth;
    }

    public void setDisplayStrokeWidth(float displayStrokeWidth) {
        this.displayStrokeWidth = displayStrokeWidth;
    }

    @Override
    public void setScale(float targetScaleValue) {
        scale = targetScaleValue;
    }

    @Override
    public float getScale() {
        return scale;
    }

    @Override
    public void setSelected(boolean isSelected) {
        selected = isSelected;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    public float getDisplayScale(final RenderContext renderContext) {
        if (renderContext == null || renderContext.matrix == null) {
            return scale;
        }
        return renderContext.displayScale;
    }

    public boolean supportDFB() {
        return false;
    }

    public void updateBoundingRect() {
        List<TouchPoint> list = normalizedPoints.getPoints();
        for(TouchPoint touchPoint: list) {
            if (boundingRect == null) {
                boundingRect = new RectF(touchPoint.x, touchPoint.y, touchPoint.x, touchPoint.y);
            } else {
                boundingRect.union(touchPoint.x, touchPoint.y);
            }
        }
    }

    public void resetBoundingRect() {
        boundingRect = null;
    }

    public RectF getBoundingRect() {
        return boundingRect;
    }

    public void moveTo(final float x, final float y) {
    }

    public void resize(final float width, final float height) {
    }

    public float getOrientation() {
        return orientation;
    }

    @Override
    public void setOrientation(float orientation) {
        this.orientation = orientation;
    }

    public void onDown(final TouchPoint normalizedPoint, final TouchPoint screenPoint) {
        addDownPoint(normalizedPoint, screenPoint);
    }

    public void addDownPoint(final TouchPoint normalizedPoint, final TouchPoint screenPoint) {
        downPoint.x = normalizedPoint.x;
        downPoint.y = normalizedPoint.y;
        currentPoint.x = normalizedPoint.x;
        currentPoint.y = normalizedPoint.y;
        currentScreenPoint.x = screenPoint.x;
        currentScreenPoint.y = screenPoint.y;
        normalizedPoints.add(normalizedPoint);
        updateBoundingRect();
    }

    public void onMove(final TouchPoint normalizedPoint, final TouchPoint screenPoint) {
        addMovePoint(normalizedPoint, screenPoint);
    }

    public void addMovePoint(final TouchPoint normalizedPoint, final TouchPoint screenPoint) {
        currentPoint.x = normalizedPoint.x;
        currentPoint.y = normalizedPoint.y;
        currentScreenPoint.x = screenPoint.x;
        currentScreenPoint.y = screenPoint.y;
        if (isAddMovePoint()) {
            normalizedPoints.add(normalizedPoint);
        }
    }

    public void onUp(final TouchPoint normalizedPoint, final TouchPoint screenPoint) {
        addUpPoint(normalizedPoint, screenPoint);
    }

    @Override
    public void onTranslate(final float dx, final float dy) {
        normalizedPoints.translateAllPoints(dx, dy);
        updatePoints();
    }

    @Override
    public void onScale(final float scale) {
        normalizedPoints.scaleAllPoints(scale);
        updatePoints();
    }

    @Override
    public void onRotate(final float angle, PointF pointF) {
        normalizedPoints.rotateAllPoints(angle, pointF);
        setSelectRectOrientation((getSelectRectOrientation() + angle) % 360);
        updatePoints();
    }

    @Override
    public void onMirror(final MirrorType type, int translateDistance) {
        normalizedPoints.mirrorAllPoints(type, translateDistance);
        updatePoints();
    }

    @Override
    public void updatePoints() {
        if (normalizedPoints.size() > 0) {
            downPoint.set(normalizedPoints.get(0));
        }
        if (normalizedPoints.size() > 1) {
            currentPoint.set(normalizedPoints.get(1));
        }
        resetBoundingRect();
        updateBoundingRect();
    }

    public void addUpPoint(final TouchPoint normalizedPoint, final TouchPoint screenPoint) {
        currentPoint.x = normalizedPoint.x;
        currentPoint.y = normalizedPoint.y;
        currentScreenPoint.x = screenPoint.x;
        currentScreenPoint.y = screenPoint.y;
        normalizedPoints.add(normalizedPoint);
        updateBoundingRect();
    }

    public void addPoints(final TouchPointList points) {
        normalizedPoints.addAll(points);
        updateBoundingRect();
        if (points.size() < 2) {
            return;
        }
        downPoint.set(points.get(0));
        currentPoint.set(points.get(1));
    }

    public TouchPointList getPoints() {
        return normalizedPoints;
    }

    public void render(final RenderContext renderContext) {
    }

    public boolean fastHitTest(final float x, final float y, final float radius) {
        if (isReview()) {
            return false;
        }
        final RectF boundingRect = getBoundingRect();
        if (boundingRect == null) {
            return false;
        }
        final float limit = radius * radius;
        return ShapeUtils.contains(boundingRect, x, y, limit);
    }

    public boolean hitTest(final float x, final float y, final float radius) {
        final float limit = radius;
        float x1, y1, x2, y2;
        boolean hit = false;
        int first, second;
        final List<TouchPoint> points = normalizedPoints.getPoints();
        for (int i = 0; i < points.size() - 1; ++i) {
            first = i;
            second = i + 1;

            x1 = points.get(first).getX();
            y1 = points.get(first).getY();

            x2 = points.get(second).getX();
            y2 = points.get(second).getY();

            boolean isIntersect = ShapeUtils.hitTest(x1, y1, x2, y2, x, y, limit);
            if (isIntersect) {
                hit = true;
                break;
            }
        }
        return hit;

    }

    public final TouchPoint getCurrentPoint() {
        return currentPoint;
    }

    public TouchPoint getCurrentScreenPoint() {
        return currentScreenPoint;
    }

    public final TouchPoint getDownPoint() {
        return downPoint;
    }

    public final TouchPointList getNormalizedPoints() {
        return normalizedPoints;
    }

    public void applyStrokeStyle(final Paint paint, final float displayScale) {
        paint.setStrokeWidth(getStrokeWidth() * displayScale);
        paint.setColor(getColor());
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setDither(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeMiter(4.0f);
    }

    public Path getOriginDisplayPath() {
        return originDisplayPath;
    }

    public void setOriginDisplayPath(final Path p) {
        originDisplayPath = p;
    }

    public void clear() {
        originDisplayPath = null;
    }

    @Override
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public String getGroupId() {
        return groupId;
    }

    @Override
    public void setLayoutType(int layoutType) {
        this.layoutType = layoutType;
    }

    @Override
    public int getLayoutType() {
        return layoutType;
    }

    public ShapeExtraAttributes getShapeExtraAttributes() {
        if (shapeExtraAttributes == null) {
            shapeExtraAttributes = new ShapeExtraAttributes();
        }
        return shapeExtraAttributes;
    }

    public void setShapeExtraAttributes(ShapeExtraAttributes attributes) {
        shapeExtraAttributes = attributes;
    }

    public void setPageOriginWidth(int width) {
        originWidth = width;
    }

    public int getPageOriginWidth() {
        return originWidth;
    }

    public void setPageOriginHeight(int height) {
        originHeight = height;
    }

    public int getPageOriginHeight() {
        return originHeight;
    }

    @Override
    public boolean isFreePosition() {
        return getLayoutType() == ShapeFactory.POSITION_FREE;
    }

    @Override
    public String getFormId() {
        return formId;
    }

    @Override
    public Integer getFormType() {
        return formType;
    }

    @Override
    public RectF getFormRect() {
        return formRect;
    }

    @Override
    public FormValue getFormValue() {
        return formValue;
    }

    @Override
    public void setFormId(String formId) {
        this.formId = formId;
    }

    @Override
    public void setFormType(Integer formType) {
        this.formType = formType;
    }

    @Override
    public void setFormRect(RectF formRect) {
        this.formRect = formRect;
    }

    @Override
    public void setFormValue(FormValue formValue) {
        this.formValue = formValue;
    }

    @Override
    public boolean isFormShape() {
        return isFormShape;
    }

    @Override
    public void setFormShape(boolean formShape) {
        isFormShape = formShape;
    }

    public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    public boolean isReview() {
        return review;
    }

    public void setReview(boolean review) {
        this.review = review;
    }

    @Override
    public boolean inVisibleDrawRectF(RectF rect) {
        return true;
    }

    public int getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }

    @Override
    public boolean canModified(int documentReviewRevision) {
        return revision >= documentReviewRevision;
    }

    @Override
    public float getSelectRectOrientation() {
        return selectRectOrientation;
    }

    @Override
    public void setSelectRectOrientation(float selectRectOrientation) {
        this.selectRectOrientation = selectRectOrientation;
    }
}
