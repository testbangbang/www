package com.onyx.kreader.host.request;

import android.graphics.Color;
import android.graphics.RectF;
import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.shape.NormalPencilShape;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.kreader.api.ReaderException;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.ui.data.SingletonSharedPreference;
import com.onyx.kreader.utils.PdfWriterUtils;

import java.io.File;
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
        this.annotations.addAll(annotations);
        this.shapes.addAll(shapes);
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
            List<NormalPencilShape> scribbles = getScribblesFromShapes();
            if (SingletonSharedPreference.isExportWithScribble()) {
                if (!writePolyLines(scribbles)) {
                    return false;
                }
            }
            if (SingletonSharedPreference.isExportWithAnnotation()) {
                if (!writeAnnotations(annotations)) {
                    return false;
                }
            }
            if (!PdfWriterUtils.saveAs(getExportDocPath(reader.getDocumentPath()),
                    !SingletonSharedPreference.isExportAllPages())) {
                return false;
            }

            return true;
        } finally {
            PdfWriterUtils.close();
        }
    }

    private List<NormalPencilShape> getScribblesFromShapes() {
        List<NormalPencilShape> scribbles = new ArrayList<>();
        for (Shape shape : shapes) {
            if (shape instanceof NormalPencilShape) {
                scribbles.add((NormalPencilShape) shape);
            }
        }
        return scribbles;
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

    private boolean writePolyLines(final List<NormalPencilShape> polyLines) {
        BrushColor colorOptions = SingletonSharedPreference.getExportScribbleColor();
        for (NormalPencilShape line : polyLines) {
            int page = Integer.parseInt(line.getPageUniqueId());

            float[] boundingRect = new float[4];
            boundingRect[0] = line.getBoundingRect().left;
            boundingRect[1] = line.getBoundingRect().top;
            boundingRect[2] = line.getBoundingRect().right;
            boundingRect[3] = line.getBoundingRect().bottom;

            float[] vertices = new float[line.getPoints().size() * 2];
            for (int i = 0; i < line.getPoints().size(); i++) {
                TouchPoint point = line.getPoints().get(i);
                vertices[i * 2] = point.getX();
                vertices[(i * 2) + 1] = point.getY();
            }

            int color = getColorOfShape(line, colorOptions);
            if (!PdfWriterUtils.writePolyLine(page, boundingRect, color, line.getStrokeWidth(), vertices)) {
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

    private String getExportDocPath(String sourceDocPath) {
        String parent = FileUtils.getParent(sourceDocPath);
        String baseName = FileUtils.getBaseName(sourceDocPath);
        String ext = FileUtils.getFileExtension(sourceDocPath);
        return new File(parent, baseName + "-Exported." + ext).getAbsolutePath();
    }

}
