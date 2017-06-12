package com.onyx.kreader.ui.data;

import android.content.Context;
import android.graphics.*;
import com.onyx.kreader.R;

/**
 * Created by joy on 7/7/16.
 */
public class BookmarkIconFactory {
    private static Bitmap sBookmarkActivated;
    private static Bitmap sBookmarkDeactivated;

    public static Bitmap getBookmarkIcon(Context context, boolean activated) {
        return activated ? getBookmarkActivated(context) : getBookmarkDeactivated(context);
    }

    public static Point bookmarkPosition(int displayWidth, Bitmap bitmap) {
        Point point = new Point();
        point.set(displayWidth - bitmap.getWidth(), 10);
        return point;
    }

    private static Bitmap getBookmarkActivated(Context context) {
        if (sBookmarkActivated == null) {
            sBookmarkActivated = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_dialog_reader_bookmark_mark_black);
        }
        return sBookmarkActivated;
    }

    private static Bitmap getBookmarkDeactivated(Context context) {
        if (sBookmarkDeactivated == null) {
            sBookmarkDeactivated = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_dialog_reader_bookmark_mark);
        }
        return sBookmarkDeactivated;
    }

    private static Bitmap generateBookmarkDrawable(Context context, boolean activated) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(3);
        paint.setAlpha(255);
        if (activated) {
            paint.setStyle(Paint.Style.FILL);
        } else {
            paint.setStyle(Paint.Style.STROKE);
        }
        int width = (int) context.getResources().getDimension(R.dimen.reader_bookmark_width);
        int height = (int) context.getResources().getDimension(R.dimen.reader_bookmark_height);
        int moveto_x = (int) context.getResources().getDimension(R.dimen.reader_bookmark_moveto_x);
        int moveto_y = (int) context.getResources().getDimension(R.dimen.reader_bookmark_moveto_y);
        int lineto_x = (int) context.getResources().getDimension(R.dimen.reader_bookmark_lineto_x);
        int lineto_y = (int) context.getResources().getDimension(R.dimen.reader_bookmark_lineto_y);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        int [] allpixels = new int [ bitmap.getHeight()*bitmap.getWidth()];
        Canvas c = new Canvas(bitmap);
        Path path = new Path();
        if (activated) {
            path.moveTo(moveto_x, moveto_y);
            path.lineTo(lineto_x, lineto_y);
            path.lineTo(moveto_x, lineto_y);
            path.lineTo(moveto_x, moveto_y);
        } else {
            path.moveTo(moveto_x, moveto_y);
            path.lineTo(lineto_x, lineto_y);
            path.lineTo(lineto_x, moveto_y);
            path.lineTo(moveto_x, moveto_y);
        }
        path.close();
        c.drawPath(path, paint);
        return bitmap;
    }
}
