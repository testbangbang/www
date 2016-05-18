package com.onyx.android.sdk.data.util;

import android.graphics.Rect;
import android.text.TextUtils;
import android.util.Log;

import com.onyx.android.sdk.data.cms.OnyxScribblePoint;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by Joy on 14-2-10.
 */
public abstract class SerializationUtil {
    private static final String TAG = SerializationUtil.class.getSimpleName();

    public static final String SEPERATOR = ", ";

    public static String dateToString(Date d)
    {
        if (d == null) {
            return "";
        } else {
            return d.getTime() + "";
        }
    }

    public static Date dateFromString(String str)
    {
        if (StringUtils.isNullOrEmpty(str) || "null".equals(str)) {
            return null;
        }
        else {
            try {
                return new Date(Long.parseLong(str));
            }
            catch (NumberFormatException e) {
                Log.w(TAG, e);
            }
            return null;
        }
    }

    public static String authorsToString(ArrayList<String> authors) {
        if (authors == null) {
            return null;
        }
        if (authors.size() <= 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(authors.get(0));
        for (int i = 1; i < authors.size(); i++) {
            sb.append(SEPERATOR).append(authors.get(i));
        }
        return sb.toString();
    }

    public static ArrayList<String> authorsFromString(String authorsString)
    {
        if (authorsString == null) {
            return null;
        }

        String[] authors = authorsString.split(SEPERATOR);
        if ((authors == null) || (authors.length <= 0)) {
            return null;
        }

        ArrayList<String> result = new ArrayList<String>();
        for (String a : authors) {
            result.add(a);
        }
        return result;
    }

    public static String tagsToString(ArrayList<String> tags) {
        if (tags == null) {
            return null;
        }
        if (tags.size() <= 0) {
            return "";
        }
        return TextUtils.join(SEPERATOR, tags);
    }

    public static ArrayList<String> tagsFromString(String tagsString)
    {
        if (tagsString == null) {
            return null;
        }

        String[] tags = tagsString.split(SEPERATOR);
        if ((tags == null) || (tags.length <= 0)) {
            return null;
        }

        ArrayList<String> result = new ArrayList<String>();
        for (String a : tags) {
            result.add(a);
        }
        return result;
    }

    public static String setToString(Collection<String> set) {
        if (set == null) {
            return null;
        }
        if (set.size() <= 0) {
            return "";
        }

        return TextUtils.join(SEPERATOR, set);
    }

    public static Set<String> setFromString(String string) {
        if (string == null) {
            return null;
        }

        String[] array = string.split(SEPERATOR);
        if ((array == null) || (array.length <= 0)) {
            return null;
        }

        return new HashSet<String>(Arrays.asList(array));
    }

    public static String pointsToString(ArrayList<Float> points)
    {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Float f : points) {
            if (first) {
                first = false;
            }
            else {
                sb.append(" ");
            }
            sb.append(f.toString());
        }
        return sb.toString();
    }

    public static ArrayList<Float> pointsFromString(String str)
    {
        ArrayList<Float> pts = new ArrayList<Float>();

        if (StringUtils.isNullOrEmpty(str)) {
            return pts;
        }

        String[] array = str.trim().split(" ");
        for (String s : array) {
            pts.add(Float.parseFloat(s));
        }

        return pts;
    }

    /**
     *
     * @param points
     * @return return null if failed
     */
    public static byte[] pointsToByteArray(ArrayList<OnyxScribblePoint> points) {
        if (points == null) {
            return new byte[0];
        }

        DataOutputStream dout = null;
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            dout = new DataOutputStream(bout);
            for (OnyxScribblePoint p : points) {
                dout.writeFloat(p.getX());
                dout.writeFloat(p.getY());
                dout.writeFloat(p.getPressure());
                dout.writeFloat(p.getSize());
                dout.writeLong(p.getEventTime());
            }
            return bout.toByteArray();
        } catch (Throwable tr) {
            Log.e(TAG, "", tr);
        } finally {
            if (dout != null) {
                try {
                    dout.close();
                } catch (IOException e) {
                    Log.e(TAG, "", e);
                }
            }
        }
        return new byte[0];
    }

    /**
     *
     * @param blob
     * @return return null if failed
     */
    public static ArrayList<OnyxScribblePoint> pointsFromByteArray(byte[] blob)
    {
        ArrayList<OnyxScribblePoint> points = new ArrayList<OnyxScribblePoint>();
        if (blob == null) {
            return points;
        }

        ByteArrayInputStream bin = null;
        try {
            bin = new ByteArrayInputStream(blob);
            DataInputStream din = new DataInputStream(bin);
            for (int i = 0; i < blob.length / 24; i++) {
                float x = din.readFloat();
                float y = din.readFloat();
                float pressure = din.readFloat();
                float size = din.readFloat();
                long event_time = din.readLong();
                points.add(new OnyxScribblePoint(x, y, pressure,
                        size, event_time));
            }

            return points;
        } catch (Throwable e) {
            Log.e(TAG, "", e);
        } finally {
            if (bin != null) {
                try {
                    bin.close();
                } catch (IOException e) {
                    Log.e(TAG, "", e);
                }
            }
        }

        return points;
    }

    public static byte[] rectsToByteArray(Collection<Rect> rects)
    {
        if (rects == null) {
            return new byte[0];
        }

        DataOutputStream dout = null;
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            dout = new DataOutputStream(bout);
            try {
                for (Rect rect : rects) {
                    dout.writeInt(rect.left);
                    dout.writeInt(rect.top);
                    dout.writeInt(rect.right);
                    dout.writeInt(rect.bottom);
                }
            } finally {
                dout.close();
            }
            return bout.toByteArray();
        } catch (Throwable tr) {
            Log.e(TAG, "", tr);
        }
        return new byte[0];
    }

    public static ArrayList<Rect> rectsFromByteArray(byte[] blob)
    {
        ArrayList<Rect> rects = new ArrayList<Rect>();
        if (blob == null) {
            return rects;
        }

        ByteArrayInputStream bin = null;
        try {
            bin = new ByteArrayInputStream(blob);
            DataInputStream din = new DataInputStream(bin);
            try {
                for (int i = 0; i < blob.length / 16; i++) {
                    int left = din.readInt();
                    int top = din.readInt();
                    int right = din.readInt();
                    int bottom = din.readInt();
                    rects.add(new Rect(left, top, right, bottom));
                }
            } finally {
                din.close();
            }

            return rects;
        } catch (Throwable e) {
            Log.e(TAG, "", e);
        }

        return rects;
    }
}
