package com.onyx.android.plato.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.onyx.android.sdk.utils.DimenUtils;
import com.onyx.android.plato.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jackdeng on 2017/10/30.
 */

public class TableView extends View {

    private float unitColumnWidth;
    private float rowHeight;
    private float dividerWidth;
    private int dividerColor;
    private float textSize;
    private int textColor;
    private int headerColor;
    private float headerTextSize;
    private int headerTextColor;

    private int rowCount;
    private int columnCount;

    private Paint paint;

    private float[] columnLefts;
    private float[] columnWidths;

    private int[] columnWeights;
    private List<String[]> tableContents;
    private ArrayList<Integer> rowTypes;
    private float mMarginLeft;
    private float mMarginRight;
    private static final float DEFAULT_ROW_HEIGHT = 40;
    private static final int DEFAULT_UNIT_COLUMN_WIDTH = 0;
    private static final int DEFAULT_DIVIDER_WIDTH = 1;
    private static final int DEFAULT_TEXT_SIZE = 10;
    private static final int DEFAULT_HEADER_TEXT_SIZE = 10;
    private static final int DEFAULT_DIVIDER_COLOR = Color.parseColor("#E1E1E1");
    private static final int DEFAULT_TEXT_COLOR = Color.parseColor("#999999");
    private static final int DEFAULT_HEADER_COLOR = Color.parseColor("#88000000");
    private static final int DEFAULT_HEADER_TEXT_COLOR = Color.parseColor("#ffffff");

    public TableView(Context context) {
        super(context);
        init(null);
    }

    public TableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        tableContents = new ArrayList<>();
        initDefaultParams(attrs);
        setHeader("Header1", "Header2").addContent("Column1", "Column2");
        initTableSize();
    }

    private void initDefaultParams(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TableView);
            unitColumnWidth = typedArray.getDimensionPixelSize(R.styleable.TableView_unitColumnWidth, DEFAULT_UNIT_COLUMN_WIDTH);
            rowHeight = typedArray.getDimensionPixelSize(R.styleable.TableView_rowHeight, DimenUtils.dip2px(getContext(), DEFAULT_ROW_HEIGHT));
            dividerWidth = typedArray.getDimensionPixelSize(R.styleable.TableView_dividerWidth, DEFAULT_DIVIDER_WIDTH);
            dividerColor = typedArray.getColor(R.styleable.TableView_dividerColor, DEFAULT_DIVIDER_COLOR);
            textSize = typedArray.getDimensionPixelSize(R.styleable.TableView_textSize, DimenUtils.dip2px(getContext(), DEFAULT_TEXT_SIZE));
            textColor = typedArray.getColor(R.styleable.TableView_textColor, DEFAULT_TEXT_COLOR);
            headerColor = typedArray.getColor(R.styleable.TableView_headerColor, DEFAULT_HEADER_COLOR);
            headerTextSize = typedArray.getDimensionPixelSize(R.styleable.TableView_headerTextSize, DimenUtils.dip2px(getContext(), DEFAULT_HEADER_TEXT_SIZE));
            headerTextColor = typedArray.getColor(R.styleable.TableView_headerTextColor, DEFAULT_HEADER_TEXT_COLOR);
            typedArray.recycle();
        } else {
            unitColumnWidth = DEFAULT_UNIT_COLUMN_WIDTH;
            rowHeight = DimenUtils.dip2px(getContext(), DEFAULT_ROW_HEIGHT);
            dividerWidth = DEFAULT_DIVIDER_WIDTH;
            dividerColor = DEFAULT_DIVIDER_COLOR;
            textSize = DimenUtils.dip2px(getContext(), DEFAULT_TEXT_SIZE);
            textColor = DEFAULT_TEXT_COLOR;
            headerColor = DEFAULT_HEADER_COLOR;
            headerTextSize = DimenUtils.dip2px(getContext(), DEFAULT_HEADER_TEXT_SIZE);
            headerTextColor = DEFAULT_HEADER_TEXT_COLOR;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int weightSum = 0;
        if (columnWeights != null) {
            for (int i = 0; i < columnCount; i++) {
                if (columnWeights.length > i) {
                    weightSum += columnWeights[i];
                } else {
                    weightSum += 1;
                }
            }
        } else {
            weightSum = columnCount;
        }

        float width;
        if (unitColumnWidth == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            width = getMeasuredWidth();
            unitColumnWidth = (width - (columnCount + 1) * dividerWidth) / weightSum;
        } else {
            width = dividerWidth * (columnCount + 1) + unitColumnWidth * weightSum;
        }
        float height = (dividerWidth + rowHeight) * rowCount + dividerWidth;

        setMeasuredDimension((int) width, (int) height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        calculateColumns();
        drawHeader(canvas);
        drawFramework(canvas);
        drawContent(canvas);
    }

    private void drawHeader(Canvas canvas) {
        paint.setColor(headerColor);
        canvas.drawRect(dividerWidth, dividerWidth, getWidth() - dividerWidth, rowHeight + dividerWidth, paint);
    }

    private void drawFramework(Canvas canvas) {
        paint.setColor(dividerColor);
        for (int i = 0; i < columnCount + 1; i++) {
            if (i == 0) {
                canvas.drawRect(0, 0, dividerWidth, getHeight(), paint);
                continue;
            }
            if (i == columnCount) {
                canvas.drawRect(getWidth() - dividerWidth, 0, getWidth(), getHeight(), paint);
                continue;
            }
            canvas.drawRect(columnLefts[i], 0, columnLefts[i] + dividerWidth, getHeight(), paint);
        }
        if (mMarginLeft == 0) {
            mMarginLeft = getColumnWidth(0);
        }
        if (mMarginRight == 0) {
            mMarginRight = getColumnWidth(columnCount - 1);
        }

        float left = 0;
        float right = getWidth();

        for (int i = 0; i < rowCount + 1; i++) {
            if (rowTypes != null && rowTypes.size() > 0) {
                if (i > 1 && i <= rowCount - 1) {
                    int rowType = 1;
                    try {
                        rowType = rowTypes.get(i - 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (rowType == 0) {
                        left = mMarginLeft;
                        right = right - mMarginRight;
                    }
                }
            }
            canvas.drawRect(left, i * (rowHeight + dividerWidth), right, i * (rowHeight + dividerWidth) + dividerWidth, paint);
            left = 0;
            right = getWidth();
        }
    }

    private void drawContent(Canvas canvas) {
        for (int i = 0; i < rowCount; i++) {
            final String[] rowContent = tableContents.size() > i ? tableContents.get(i) : new String[0];
            if (i == 0) {
                paint.setColor(headerTextColor);
                paint.setTextSize(headerTextSize);
            }
            for (int j = 0; j < columnCount; j++) {
                if (rowContent.length > j) {
                    int rowWeight = getRowWeight(i, j);
                    if (rowWeight != 0) {
                        if (rowContent[j].length() < 9) {
                            canvas.drawText(rowContent[j],
                                    columnLefts[j] + columnWidths[j] / 2,
                                    getTextBaseLine(i * (rowHeight + dividerWidth), paint, rowWeight),
                                    paint);
                        } else {
                            String content1 = "";
                            String content2 = "";
                            if (i == 0) {
                                String[] splitString = rowContent[j].split(" ", 2);
                                if (splitString != null && splitString.length > 0) {
                                    if (splitString.length == 1) {
                                        content1 = splitString[0];
                                    } else {
                                        content1 = splitString[0];
                                        content2 = splitString[1];
                                    }
                                }
                            } else {
                                content1 = rowContent[j].substring(0, rowContent[j].length() / 2);
                                content2 = rowContent[j].substring(rowContent[j].length() / 2);
                            }

                            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
                            float fontSize = (fontMetrics.bottom - fontMetrics.top) / 2;

                            canvas.drawText(content1,
                                    columnLefts[j] + columnWidths[j] / 2,
                                    getTextBaseLine(i * (rowHeight + dividerWidth), paint, rowWeight) - fontSize,
                                    paint);

                            canvas.drawText(content2,
                                    columnLefts[j] + columnWidths[j] / 2,
                                    getTextBaseLine(i * (rowHeight + dividerWidth), paint, rowWeight) + fontSize,
                                    paint);
                        }
                    }
                }
            }
            if (i == 0) {
                paint.setColor(textColor);
                paint.setTextSize(textSize);
            }
        }
    }

    private void calculateColumns() {
        columnLefts = new float[columnCount];
        columnWidths = new float[columnCount];
        for (int i = 0; i < columnCount; i++) {
            columnLefts[i] = getColumnLeft(i);
            columnWidths[i] = getColumnWidth(i);
        }
    }

    private float getColumnLeft(int columnIndex) {
        if (columnWeights == null) {
            return columnIndex * (unitColumnWidth + dividerWidth);
        }
        int weightSum = 0;
        for (int i = 0; i < columnIndex; i++) {
            if (columnWeights.length > i) {
                weightSum += columnWeights[i];
            } else {
                weightSum += 1;
            }
        }
        return columnIndex * dividerWidth + weightSum * unitColumnWidth;
    }

    private float getColumnWidth(int columnIndex) {
        if (columnWeights == null) {
            return unitColumnWidth;
        }
        int weight = columnWeights.length > columnIndex ? columnWeights[columnIndex] : 1;
        return weight * unitColumnWidth;
    }

    private float getTextBaseLine(float rowStart, Paint paint, int rowWeight) {
        final Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return (rowStart + (rowStart + rowHeight * rowWeight) - fontMetrics.bottom - fontMetrics.top) / 2;
    }

    private int getRowWeight(int rowIndex, int columnIndex) {
        int rowWeight = 1;
        if (columnIndex == 0 || columnIndex == (columnCount - 1)) {
            if (rowIndex > 0 && rowIndex <= rowCount - 1) {
                if (rowTypes != null && rowTypes.size() > 0) {
                    int rowType = 1;
                    try {
                        rowType = rowTypes.get(rowIndex - 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    rowWeight = rowType;
                }
            }
        }
        return rowWeight;
    }

    public TableView clearTableContents() {
        columnWeights = null;
        tableContents.clear();
        return this;
    }

    public TableView setColumnWeights(int... columnWeights) {
        this.columnWeights = columnWeights;
        return this;
    }

    public TableView setHeader(String... headers) {
        tableContents.add(0, headers);
        return this;
    }

    public TableView addContent(String... contents) {
        tableContents.add(contents);
        return this;
    }

    public TableView addContents(List<String[]> contents) {
        tableContents.addAll(contents);
        return this;
    }

    private void initTableSize() {
        rowCount = tableContents.size();
        unitColumnWidth = 0;
        mMarginLeft = 0;
        mMarginRight = 0;
        if (rowCount > 0) {
            columnCount = tableContents.get(0).length;
        }
    }

    public void refreshTable() {
        initTableSize();
        requestLayout();
    }

    public TableView setRowTypes(ArrayList<Integer> rowTypes) {
        this.rowTypes = rowTypes;
        return this;
    }

}
