package com.onyx.phone.reader.reader.opengl;

import android.graphics.Color;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;


/**
 * Created by ming on 2017/5/3.
 */

public class Highlight implements IOpenGLObject {

    private final int COORDS_PER_VERTEX = 3;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    private FloatBuffer vertexBuffer;
    private int color = Color.BLACK;
    private float alpha = 0.5f;
    private float colorArray[] = { 0f, 0f, 0f, 0f };

    public Highlight(float[] vertexArray) {
        ByteBuffer bb = ByteBuffer.allocateDirect(
                vertexArray.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertexArray);
        vertexBuffer.position(0);
    }

    public Highlight(float[] vertexArray, int color, float alpha) {
        this(vertexArray);
        setColor(color);
        setAlpha(alpha);
    }

    @Override
    public void draw(GL10 gl, float[] mvpMatrix, int program) {
        GLES20.glUseProgram(program);
        int positionHandle = GLES20.glGetAttribLocation(program, Constants.V_POSITION);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(
                positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        int colorHandle = GLES20.glGetUniformLocation(program, Constants.V_COLOR);
        GLES20.glUniform4fv(colorHandle, 1, colorArray, 0);
        int mvpMatrixHandle = GLES20.glGetUniformLocation(program, Constants.U_MVP_MATRIX);
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisable(GLES20.GL_BLEND);
    }

    public void setColor(int color) {
        this.color = color;
        updateColorArray();
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
        updateColorArray();
    }

    private void updateColorArray() {
        colorArray[0] = Color.red(color) / 255f;
        colorArray[1] = Color.green(color) / 255f;
        colorArray[2] = Color.blue(color) / 255f;
        colorArray[3] = alpha;
    }

    public static Highlight create(float[] vertexArray) {
        return new Highlight(vertexArray);
    }

    public static Highlight create(float[] vertexArray, int color, float alpha) {
        return new Highlight(vertexArray, color, alpha);
    }
}
