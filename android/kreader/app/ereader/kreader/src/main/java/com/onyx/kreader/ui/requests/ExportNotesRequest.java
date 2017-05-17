package com.onyx.kreader.ui.requests;

import android.graphics.Color;
import android.graphics.RectF;

import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.shape.BrushScribbleShape;
import com.onyx.android.sdk.scribble.shape.CircleShape;
import com.onyx.android.sdk.scribble.shape.LineShape;
import com.onyx.android.sdk.scribble.shape.NormalPencilShape;
import com.onyx.android.sdk.scribble.shape.RectangleShape;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.TriangleShape;
import com.onyx.android.sdk.reader.api.ReaderException;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;
import com.onyx.kreader.ui.data.SingletonSharedPreference;
import com.onyx.android.sdk.utils.ExportUtils;
import com.onyx.android.sdk.reader.utils.PdfWriterUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 10/15/15.
 */
public class ExportNotesRequest extends BaseReaderRequest {

    public enum BrushColor { Original, Red, Black, Green, White, Blue }

    private List<Annotation> annotations = new ArrayList<>();
    private List<Shape> shapes = new ArrayList<>();


    public ExportNotesRequest(List<Annotation> annotations, List<Shape> shapes) {
        if (annotations != null) {
            this.annotations.addAll(annotations);
        }
        if (shapes != null) {
            this.shapes.addAll(shapes);
        }

    }

    public ExportNotesRequest(List<Annotation> annotations) {
        if (annotations != null) {
            this.annotations.addAll(annotations);
        }
    }

    public void execute(final Reader reader) throws Exception {
        if (!exportNotes(reader)) {
            throw ReaderException.exceptionFromCode(ReaderException.UNKNOWN_EXCEPTION,
                    "export notes failed!");
        }
    }

    private boolean exportNotes(final Reader reader) {
        if (!PdfWriterUtils.openExistingDocument(reader.getDocumentPath())) {
            return false;
        }

        try {
            if (!writeShapes(shapes)) {
                return false;
            }
            if (!writeAnnotations(annotations)) {
                return false;
            }
            boolean exportOnlyPagesWithAnnotations = !SingletonSharedPreference.isExportAllPages();
            if (exportOnlyPagesWithAnnotations && shapes.size() == 0 && annotations.size() == 0) {
                return false;
            }
            if (!PdfWriterUtils.saveAs(ExportUtils.getExportPdfPath(reader.getDocumentPath()), exportOnlyPagesWithAnnotations)) {
                return false;
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            PdfWriterUtils.close();
        }
    }

    private boolean writeAnnotations(final List<Annotation> annotations) {
        for (Annotation annotation : annotations) {
            float[] quadPoints = new float[annotation.getRectangles().size() * 4];
            for (int i = 0; i < annotation.getRectangles().size(); i++) {
                RectF rect = annotation.getRectangles().get(i);
                quadPoints[i * 4] = rect.left;
                quadPoints[(i * 4) + 1] = rect.top;
                quadPoints[(i * 4) + 2] = rect.right;
                quadPoints[(i * 4) + 3] = rect.bottom;
            }
            if (!PdfWriterUtils.writeHighlight(annotation.getPageNumber(), annotation.getNote(), quadPoints)) {
                return false;
            }
        }
        return true;
    }

    private boolean writeShapes(final List<Shape> shapes) {
        BrushColor colorOption = SingletonSharedPreference.getExportScribbleColor();
        for (Shape shape : shapes) {
            int color = getColorOfShape(shape, colorOption);
            boolean succ;
            if (shape instanceof NormalPencilShape ||
                    shape instanceof BrushScribbleShape) {
                succ = writePolyLine(shape, color);
            } else if (shape instanceof LineShape) {
                succ = writeLine((LineShape)shape, color);
            } else if (shape instanceof RectangleShape) {
                succ = writeSquare((RectangleShape)shape, color);
            } else if (shape instanceof CircleShape) {
                succ = writeCircle((CircleShape)shape, color);
            } else if (shape instanceof TriangleShape) {
                succ = writeTriangle((TriangleShape)shape, color);
            } else {
                return false;
            }
            if (!succ) {
                return false;
            }
        }
        return true;
    }

    private int getColorOfShape(Shape shape, BrushColor overrideColor) {
        switch (overrideColor) {
            case Original:
                return shape.getColor();
            case Red:
                return Color.RED;
            case Black:
                return Color.BLACK;
            case Green:
                return Color.GREEN;
            case White:
                return Color.WHITE;
            case Blue:
                return Color.BLUE;
            default:
                return shape.getColor();
        }
    }

    private float[] getBoundingRect(final Shape shape) {
        float[] rect = new float[4];
        rect[0] = shape.getBoundingRect().left;
        rect[1] = shape.getBoundingRect().top;
        rect[2] = shape.getBoundingRect().right;
        rect[3] = shape.getBoundingRect().bottom;
        return rect;
    }

    private boolean writePolyLine(final Shape line, final int color) {
        int page = Integer.parseInt(line.getPageUniqueId());
        float[] boundingRect = getBoundingRect(line);
        float[] vertices = new float[line.getPoints().size() * 2];
        for (int i = 0; i < line.getPoints().size(); i++) {
            TouchPoint point = line.getPoints().get(i);
            vertices[i * 2] = point.getX();
            vertices[(i * 2) + 1] = point.getY();
        }
        return PdfWriterUtils.writePolyLine(page, boundingRect, color,
                line.getStrokeWidth(), vertices);
    }

    private boolean writeLine(final LineShape line, final int color) {
        int page = Integer.parseInt(line.getPageUniqueId());
        float[] boundingRect = getBoundingRect(line);
        float startX = line.getPoints().get(0).x;
        float startY = line.getPoints().get(0).y;
        float endX = line.getPoints().get(1).x;
        float endY = line.getPoints().get(1).y;
        return PdfWriterUtils.writeLine(page, boundingRect, color,
                line.getStrokeWidth(), startX, startY, endX, endY);
    }

    private boolean writeSquare(final RectangleShape square, final int color) {
        int page = Integer.parseInt(square.getPageUniqueId());
        float[] boundingRect = getBoundingRect(square);
        return PdfWriterUtils.writeSquare(page, boundingRect, color,
                square.getStrokeWidth());
    }

    private boolean writeCircle(final CircleShape circle, final int color) {
        int page = Integer.parseInt(circle.getPageUniqueId());
        float[] boundingRect = getBoundingRect(circle);
        return PdfWriterUtils.writeCircle(page, boundingRect, color,
                circle.getStrokeWidth());
    }

    private boolean writeTriangle(final TriangleShape triangle, final int color) {
        int page = Integer.parseInt(triangle.getPageUniqueId());
        float[] boundingRect = getBoundingRect(triangle);
        float[] vertices = new float[6];
        vertices[0] = triangle.getBoundingRect().centerX();
        vertices[1] = triangle.getBoundingRect().top;
        vertices[2] = triangle.getBoundingRect().left;
        vertices[3] = triangle.getBoundingRect().bottom;
        vertices[4] = triangle.getBoundingRect().right;
        vertices[5] = triangle.getBoundingRect().bottom;
        return PdfWriterUtils.writePolygon(page, boundingRect, color,
                triangle.getStrokeWidth(), vertices);
    }

}
