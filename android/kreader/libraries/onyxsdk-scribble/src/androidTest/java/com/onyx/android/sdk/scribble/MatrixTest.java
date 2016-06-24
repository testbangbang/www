package com.onyx.android.sdk.scribble;

import android.app.Application;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.test.ApplicationTestCase;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.utils.TestUtils;

/**
 * Created by zhuzeng on 6/24/16.
 */
public class MatrixTest  extends ApplicationTestCase<Application> {


    public MatrixTest() {
        super(Application.class);
    }

    public void testMap() {
        int width = TestUtils.randInt(1000, 3000);
        int height = TestUtils.randInt(1000, 3000);
        int px = TestUtils.randInt(10, 30);
        int py = TestUtils.randInt(10, 30);
        int dw = width - px;
        int dh = height - py;
        PageInfo pageInfo = new PageInfo("1", width, height);
        pageInfo.updateDisplayRect(new RectF(px, py, px + dw, py + dh));
        Matrix matrix = pageInfo.normalizeMatrix();

        int screenX = TestUtils.randInt(40, 900);
        int screenY = TestUtils.randInt(40, 900);
        float xNormalized = (float)(screenX - px) / (float)(dw);
        float yNormalized = (float)(screenY - py) / (float)(dh);

        float src[] = new float[2];
        src[0] = screenX;
        src[1] = screenY;
        float dst[] = new float[2];
        matrix.mapPoints(dst, src);
        assertEquals(dst[0], xNormalized);
        assertEquals(dst[1], yNormalized);
    }


    public void testMap2() {
        int width = TestUtils.randInt(1000, 3000);
        int height = TestUtils.randInt(1000, 3000);
        int px = TestUtils.randInt(10, 30);
        int py = TestUtils.randInt(10, 30);
        int dw = width - px;
        int dh = height - py;
        float actualScale = 2.0f;
        PageInfo pageInfo = new PageInfo("1", width, height);
        pageInfo.setScale(actualScale);
        pageInfo.updateDisplayRect(new RectF(px, py, px + dw, py + dh));
        Matrix matrix = pageInfo.normalizeMatrix();

        int screenX = TestUtils.randInt(40, 900);
        int screenY = TestUtils.randInt(40, 900);
        float xNormalized = (float)(screenX - px) / (float)(dw) / actualScale;
        float yNormalized = (float)(screenY - py) / (float)(dh) / actualScale;

        float src[] = new float[2];
        src[0] = screenX;
        src[1] = screenY;
        float dst[] = new float[2];
        matrix.mapPoints(dst, src);
        assertEquals(dst[0], xNormalized);
        assertEquals(dst[1], yNormalized);
    }

}
