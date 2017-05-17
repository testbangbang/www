package com.onyx.phone.reader.reader.opengl;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by ming on 2017/5/3.
 */

public interface IOpenGLObject {
    void draw(GL10 gl, float[] mvpMatrix, int program);
}
