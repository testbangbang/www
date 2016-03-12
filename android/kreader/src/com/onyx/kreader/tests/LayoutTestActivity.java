package com.onyx.kreader.tests;

import android.app.Activity;
import android.graphics.*;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import com.onyx.kreader.R;
import com.onyx.kreader.formats.model.BookModel;
import com.onyx.kreader.formats.model.Paragraph;
import com.onyx.kreader.formats.model.TextPosition;
import com.onyx.kreader.formats.model.entry.ParagraphEntry;
import com.onyx.kreader.formats.model.entry.TextParagraphEntry;
import com.onyx.kreader.formats.txt.TxtReader;
import com.onyx.kreader.text.*;
import com.onyx.kreader.utils.TestUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zengzhu on 3/3/16.
 */
public class LayoutTestActivity extends Activity {

    private Button openButton;
    private Button controllerButton;
    private Button runButton;
    private SurfaceHolder holder;
    private SurfaceView surfaceView;
    private static final String TAG = LayoutTestActivity.class.getSimpleName();

    final String path = "/mnt/sdcard/Books/test.txt";
    private TxtReader reader = new TxtReader();
    private BookModel bookModel = new BookModel(path);
    private int lastParagraph = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_test_activity);

        openButton = (Button)findViewById(R.id.test_layout);
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testLayout();
            }
        });

        controllerButton = (Button)findViewById(R.id.test_layout_controller);
        controllerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testController();
            }
        });

        runButton = (Button)findViewById(R.id.test_layout_run);
        runButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testLayoutRun();
            }
        });

        surfaceView = (SurfaceView)findViewById(R.id.surfaceView);
        holder = surfaceView.getHolder();
        reader.open(bookModel);
    }


    private void testLayout() {
        long s1 = System.currentTimeMillis();

        if (!reader.processNext(bookModel)) {
            return;
        }
        long s2 = System.currentTimeMillis();
        final Style style = randStyle();
        TextLayoutJustify instance = new TextLayoutJustify();
        int margin = 100;

        final Paint.FontMetrics fontMetrics = style.getPaint().getFontMetrics();
        float baseLine = margin - fontMetrics.top;
        RectF rect = new RectF(margin, baseLine, surfaceView.getMeasuredWidth() - margin, surfaceView.getMeasuredHeight() - baseLine);

        List<Element> textElementList = new ArrayList<Element>();
        final List<Paragraph> list = bookModel.getTextModel().getParagraphList();

        for (int p = lastParagraph; p < list.size(); ++p) {
            final Paragraph paragraph = list.get(p);
            for (ParagraphEntry entry : paragraph.getParagraphEntryList()) {
                if (entry instanceof TextParagraphEntry) {
                    TextParagraphEntry textParagraphEntry = (TextParagraphEntry) entry;
                    final String text = textParagraphEntry.getText();
                    for (int i = 0; i < text.length(); ++i) {
                        final String sub = text.substring(i, i + 1);
                        if( (Character.UnicodeBlock.of(text.charAt(i)) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS)) {
                        }
                        TextElement element = TextElement.create(text.substring(i, i + 1), style);
                        textElementList.add(element);
                    }
                }
            }
        }
        lastParagraph = list.size() - 1;
        long s3 = System.currentTimeMillis();
        final List<LayoutLine> lines  = instance.layout(rect, textElementList);
        long s4 = System.currentTimeMillis();
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(40);
        paint.setStyle(Paint.Style.STROKE);

        Canvas canvas = holder.lockCanvas();
        canvas.drawColor(Color.WHITE);
        for(Element element : textElementList) {
            element.draw(canvas);
        }


        canvas.drawLine(margin, baseLine, surfaceView.getMeasuredWidth() - margin, baseLine, style.getPaint());
        RectF bounding = new RectF(margin, margin, surfaceView.getMeasuredWidth() - margin, surfaceView.getMeasuredHeight() - margin);
        canvas.drawRect(bounding, paint);

        holder.unlockCanvasAndPost(canvas);
        long s5 = System.currentTimeMillis();
        Log.i(TAG, "performance: " + (s2 - s1) + " " + (s3 - s2) + " " + (s4 - s3) + " " + (s5 - s4));
    }

    private RectF layoutRect() {
        int margin = 100;
        RectF rect = new RectF(margin, margin, surfaceView.getMeasuredWidth() - margin, surfaceView.getMeasuredHeight() - margin);
        return rect;
    }

    private void testController() {
        if (!reader.processNext(bookModel)) {
            return;
        }

        TextController textController = new TextController();
        TextLayoutContext context = new TextLayoutContext();
        context.initializeWithLimitedRect(layoutRect());
        context.createLayoutLine();
        final Style textStyle = randStyle();

        final List<Paragraph> paragraphList = bookModel.getTextModel().getParagraphList();
        for (int p = lastParagraph; p < paragraphList.size(); ++p) {
            final Paragraph paragraph = paragraphList.get(p);
            for (ParagraphEntry entry : paragraph.getParagraphEntryList()) {
                if (entry instanceof TextParagraphEntry) {
                    TextParagraphEntry textParagraphEntry = (TextParagraphEntry) entry;
                    textController.addEntryToLayout(context, textParagraphEntry, textStyle);
                }
            }
        }
    }

    private void testLayoutRun() {
        if (!reader.processNext(bookModel)) {
            return;
        }


        RectF lineRect = new RectF(layoutRect());
        List<LayoutRunLine> lineList = new ArrayList<LayoutRunLine>();
        LayoutRunLine layoutLine = new LayoutRunLine(lineRect);
        lineList.add(layoutLine);
        final Style textStyle = randStyle();
        final List<LayoutRun> runlist = new ArrayList<LayoutRun>();

        TextPosition position = new TextPosition();
        int count = bookModel.getTextModel().getParagraphCount();
        final List<Paragraph> paragraphList = bookModel.getTextModel().getParagraphList();
        for (int p = lastParagraph; p < paragraphList.size(); ++p) {
            final Paragraph paragraph = paragraphList.get(p);
            for (ParagraphEntry entry : paragraph.getParagraphEntryList()) {
                if (entry instanceof TextParagraphEntry) {
                    TextParagraphEntry textParagraphEntry = (TextParagraphEntry) entry;
                    runlist.addAll(LayoutRunSplitter.split(textParagraphEntry.getText(), position, textStyle));
                }
            }
        }

        boolean stop = false;
        int index = 0;
        while (!stop && index < runlist.size()) {
            final LayoutRun layoutRun = runlist.get(index);
            LayoutRunLine.LayoutResult result = layoutLine.layoutRun(layoutRun);
            switch (result) {
                case LAYOUT_ADDED:
                    ++index;
                    break;
                case LAYOUT_FINISHED:
                    if (!layoutLine.nextLine(layoutRect(), lineRect, Math.max(layoutLine.getContentHeight(), textStyle.measureHeight("A")))) {
                        stop = true;
                        break;
                    }
                    layoutLine = new LayoutRunLine(lineRect);
                    lineList.add(layoutLine);
                    ++index;
                    break;
                case LAYOUT_FAIL:
                    if (!layoutLine.nextLine(layoutRect(), lineRect, Math.max(layoutLine.getContentHeight(), textStyle.measureHeight("A")))) {
                        stop = true;
                        break;
                    }
                    layoutLine = new LayoutRunLine(lineRect);
                    lineList.add(layoutLine);
                    break;
                case LAYOUT_BREAK:
                    final LayoutRun another = breakRunByWidth(layoutRun, layoutLine.getAvailableWidth(), textStyle);
                    runlist.add(index + 1, another);
                    break;
            }
        }
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        Canvas canvas = holder.lockCanvas();
        canvas.drawColor(Color.WHITE);
        for(LayoutRunLine lineManager: lineList) {
            for (LayoutRun layoutRun : lineManager.getRunList()) {
                if (layoutRun.isWord()) {
                    final Paint.FontMetrics fontMetrics = textStyle.getPaint().getFontMetrics();
//                    canvas.drawRect(layoutRun.getPositionRect(), textStyle.getPaint());
                    canvas.drawText(layoutRun.getText(),
                            layoutRun.getStart(),
                            layoutRun.getEnd(),
                            layoutRun.getPositionRect().left, layoutRun.getPositionRect().top - fontMetrics.top - fontMetrics.bottom, textStyle.getPaint());
                }
            }
        }

        holder.unlockCanvasAndPost(canvas);
    }

    private final LayoutRun breakRunByWidth(final LayoutRun layoutRun, final float width, final Style textStyle) {
        final float characterWidth = layoutRun.singleCharacterWidth();
        int count = (int)(width /characterWidth);
        float newWidth = textStyle.getPaint().measureText(layoutRun.getText(), layoutRun.getStart(), layoutRun.getStart() + count);
        return layoutRun.breakRun(count, newWidth);
    }

    private Style randStyle() {
        Paint paint = new Paint();
        paint.setTextSize(35);//TestUtils.randInt(10, 80));
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        int value = TestUtils.randInt(10, 20);
        if (value > 10 && value < 13) {
            paint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.ITALIC));
        } else if (value > 13 && value < 16) {
            paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        }
        paint.setStyle(Paint.Style.STROKE);
        return TextStyle.create(paint);
    }





}