package com.onyx.android.sdk.scribble.utils;

import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.utils.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Created by zhuzeng on 7/4/16.
 */
public class SerializationUtils {

    /**
     *
     * @param touchPointList
     * @return return null if failed
     */
    public static byte[] pointsToByteArray(TouchPointList touchPointList) {
        if (touchPointList == null) {
            return new byte[0];
        }

        DataOutputStream dout = null;
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            dout = new DataOutputStream(bout);
            for (TouchPoint  p : touchPointList.getPoints()) {
                dout.writeFloat(p.getX());
                dout.writeFloat(p.getY());
                dout.writeFloat(p.getPressure());
                dout.writeFloat(p.getSize());
                dout.writeLong(p.getTimestamp());
            }
            return bout.toByteArray();
        } catch (Throwable tr) {
            tr.printStackTrace();
        } finally {
            FileUtils.closeQuietly(dout);
        }
        return new byte[0];
    }

    /**
     *
     * @param blob
     * @return return null if failed
     */
    public static TouchPointList pointsFromByteArray(byte[] blob) {
        TouchPointList points = new TouchPointList(100);
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
                points.add(new TouchPoint(x, y, pressure, size, event_time));
            }

            return points;
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            FileUtils.closeQuietly(bin);
        }

        return points;
    }
}
