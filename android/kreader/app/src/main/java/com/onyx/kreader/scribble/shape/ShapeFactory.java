package com.onyx.kreader.scribble.shape;

/**
 * Created by zhuzeng on 4/20/16.
 */
public class ShapeFactory {

    static public final int SHAPE_INVALID = -1;
    static public final int SHAPE_CIRCLE = 0;
    static public final int SHAPE_RECTANGLE = 1;
    static public final int SHAPE_NORMAL_SCRIBBLE = 2;
    static public final int SHAPE_VARY_SCRIBBLE = 3;
    static public final int SHAPE_TEXT = 4;

    // create from pool if needed.
    static public final Shape createShape(int type) {
        return null;
    }
}
