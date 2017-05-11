package com.onyx.phone.reader.reader.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;


/**
 * Created by ming on 2017/5/3.
 */

public class Rectangle implements IOpenGLObject {

    private FloatBuffer vertex;

    public Rectangle(float[] vertexArray) {

        ByteBuffer vbb
                = ByteBuffer.allocateDirect(vertexArray.length*4);
        vbb.order(ByteOrder.nativeOrder());
        vertex = vbb.asFloatBuffer();
        vertex.put(vertexArray);
        vertex.position(0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glLoadIdentity();
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertex);
        gl.glColor4f(0f, 0f, 0f, 0f);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }

    public static Rectangle create(float[] vertexArray) {
        return new Rectangle(vertexArray);
    }
}
