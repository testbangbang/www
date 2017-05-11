package com.onyx.phone.reader.reader.opengl;

import android.graphics.Color;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;


/**
 * Created by ming on 2017/5/3.
 */

public class Highlight implements IOpenGLObject {

    private FloatBuffer vertex;
    private int color = Color.BLACK;
    private float alpha = 0.5f;

    public Highlight(float[] vertexArray) {

        ByteBuffer vbb
                = ByteBuffer.allocateDirect(vertexArray.length*4);
        vbb.order(ByteOrder.nativeOrder());
        vertex = vbb.asFloatBuffer();
        vertex.put(vertexArray);
        vertex.position(0);
    }

    public Highlight(float[] vertexArray, int color, float alpha) {
        this(vertexArray);
        this.color = color;
        this.alpha = alpha;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glLoadIdentity();
        gl.glEnable(GL10.GL_BLEND);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertex);
        gl.glColor4f(Color.red(color) / 255f,
                Color.green(color) / 255f,
                Color.blue(color) / 255f,
                alpha);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisable(GL10.GL_BLEND);
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public static Highlight create(float[] vertexArray) {
        return new Highlight(vertexArray);
    }

    public static Highlight create(float[] vertexArray, int color, float alpha) {
        return new Highlight(vertexArray, color, alpha);
    }
}
