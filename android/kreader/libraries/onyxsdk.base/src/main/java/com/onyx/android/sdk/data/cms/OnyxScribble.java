package com.onyx.android.sdk.data.cms;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.net.Uri;
import android.provider.BaseColumns;

import com.onyx.android.sdk.data.util.CursorUtil;
import com.onyx.android.sdk.data.util.SerializationUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * each file's scribbles will be saved to a separate db file identified by the file's md5,
 * so every update/delete db operations on the db should carry md5 in the selection condition
 *
 * Created by Joy on 14-2-7.
 */
public class OnyxScribble {
    private static final String TAG = OnyxScribble.class.getSimpleName();

    public static final String DB_TABLE_NAME = "library_scribble";
    public static final Uri CONTENT_URI = Uri.parse("content://" + OnyxCmsCenter.PROVIDER_AUTHORITY + "/" + DB_TABLE_NAME);


    public static class Columns implements BaseColumns {
        public static String MD5 = "MD5";
        public static String PAGE = "Page";
        public static String COLOR = "Color";
        public static String THICKNESS = "Thickness";
        public static String POINTS = "Points";
        public static String UPDATE_TIME = "UpdateTime";
        public static String APPLICATION = "Application";
        public static String POSITION = "Position";
        public static String POINTS_BLOB = "PointsBlob";
        public static String UNIQUE_ID = "uniqueId";

        // need read at runtime
        private static boolean sColumnIndexesInitialized = false;
        private static int sColumnID = -1;
        private static int sColumnMD5 = -1;
        private static int sColumnPage = -1;
        private static int sColumnColor = -1;
        private static int sColumnThickness = -1;
        private static int sColumnPoints = -1;
        private static int sColumnUpdateTime = -1;
        private static int sColumnApplication = -1;
        private static int sColumnPosition = -1;
        private static int sColumnPointsBlob = -1;
        private static int sColumnUniqueId = -1;

        public static ContentValues createColumnData(OnyxScribble scribble)
        {
            ContentValues values = new ContentValues();
            values.put(MD5, scribble.getMD5());
            values.put(PAGE, scribble.getPage());
            values.put(COLOR, scribble.getColor());
            values.put(THICKNESS, scribble.getThickness());
            values.put(POINTS, "");
            values.put(UPDATE_TIME, SerializationUtil.dateToString(scribble.getUpdateTime()));
            values.put(APPLICATION, scribble.getApplication());
            values.put(POSITION, scribble.getPosition());
            values.put(POINTS_BLOB, SerializationUtil.pointsToByteArray(scribble.getPoints()));
            values.put(UNIQUE_ID, scribble.getUniqueId());
            return values;
        }

        public static OnyxScribble readColumnData(Cursor c)
        {
            if (!sColumnIndexesInitialized) {
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
                sColumnIndexesInitialized = true;
            }

            long id = CursorUtil.getLong(c, sColumnID);
            String md5 = CursorUtil.getString(c, sColumnMD5);
            Integer page = CursorUtil.getInt(c, sColumnPage);
            Integer color = CursorUtil.getInt(c, sColumnColor);
            double thickness = c.getDouble(sColumnThickness);
            String update_time = CursorUtil.getString(c, sColumnUpdateTime);
            String application = CursorUtil.getString(c, sColumnApplication);
            String position = CursorUtil.getString(c, sColumnPosition);
            byte[] pts = CursorUtil.getBlob(c, sColumnPointsBlob);
            String uniqueId = CursorUtil.getString(c, sColumnUniqueId);

            OnyxScribble scribble = new OnyxScribble();
            scribble.setId(id);
            scribble.setMD5(md5);
            scribble.setPage(page == null ? 0 : page);
            scribble.setColor(color == null ? Color.BLACK : color);
            scribble.setThickness(thickness);
            scribble.setUpdateTime(SerializationUtil.dateFromString(update_time));
            scribble.setApplication(application);
            scribble.setPosition(position);

            final ArrayList<OnyxScribblePoint> points = SerializationUtil.pointsFromByteArray(pts);
            scribble.setPoints(points);
            scribble.setUniqueId(uniqueId);
            for(OnyxScribblePoint point : points) {
                scribble.updateBoundingRect(point.x, point.y);
            }
            return scribble;
        }
    }

    // -1 should never be valid DB value
    private static final int INVALID_ID = -1;

    private long mId = INVALID_ID;
    private String mMD5 = null;
    private int mPage = -1;
    /**
     * color in ARGB
     */
    private int mColor = Color.BLACK;
    private double mThickness = 3.0;
    private ArrayList<OnyxScribblePoint> mPoints = new ArrayList<OnyxScribblePoint>();
    private Date mUpdateTime = null;
    private String mApplication = null;
    private String mPosition = null;
    private String uniqueId  = null;
    private RectF boundingRect = null;

    public OnyxScribble()
    {

    }

    public long getId() {
        return mId;
    }

    public void setId(long mId) {
        this.mId = mId;
    }

    public String getMD5() {
        return mMD5;
    }

    public void setMD5(String mMD5) {
        this.mMD5 = mMD5;
    }

    public int getPage()
    {
        return mPage;
    }

    public void setPage(int page)
    {
        mPage = page;
    }

    public int getColor()
    {
        return mColor;
    }

    public void setColor(int color)
    {
        mColor = color;
    }

    public double getThickness()
    {
        return mThickness;
    }

    public void setThickness(double thickness)
    {
        mThickness = thickness;
    }

    public ArrayList<OnyxScribblePoint> getPoints() {
        return mPoints;
    }

    public ArrayList<OnyxScribblePoint> allocatePoints(int size) {
        mPoints = new ArrayList<OnyxScribblePoint>(size);
        return mPoints;
    }

    public PointF[] getPointsArray() {
        PointF[] points = new PointF[mPoints.size()];
        for(int i = 0; i < mPoints.size(); ++i) {
            points[i] = new PointF(mPoints.get(i).getX(), mPoints.get(i).getY());
        }
        return points;
    }

    public void setPoints(ArrayList<OnyxScribblePoint> mPoints) {
        this.mPoints = mPoints;
    }

    public Date getUpdateTime() {
        return mUpdateTime;
    }

    public void setUpdateTime(Date mUpdateTime) {
        this.mUpdateTime = mUpdateTime;
    }

    public String getApplication() {
        return mApplication;
    }

    public void setApplication(String mApplication) {
        this.mApplication = mApplication;
    }

    public String getPosition() {
        return mPosition;
    }

    public void setPosition(String position) {
        mPosition = position;
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

    public final RectF getBoundingRect() {
        return boundingRect;
    }
}

