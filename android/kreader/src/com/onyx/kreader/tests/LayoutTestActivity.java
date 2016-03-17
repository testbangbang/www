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
import com.onyx.kreader.formats.model.TextModelPosition;
import com.onyx.kreader.formats.txt.TxtBookReader;
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

    private BookModel bookModel = new BookModel();
    private TxtBookReader bookReader = new TxtBookReader();
    private TextModelPosition position = new TextModelPosition(bookReader, bookModel);
    private List<LayoutRun> runlist = new ArrayList<LayoutRun>();
    private Style textStyle = randStyle();
    private LayoutRunGenerator generator = new LayoutRunGenerator(position);


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
        bookReader.open(path, bookModel);
    }


    private void testLayout() {

    }

    private RectF leftLayoutRect() {
        int margin = 50;
        RectF rect = new RectF(margin, margin, surfaceView.getMeasuredWidth() - margin, surfaceView.getMeasuredHeight() / 2 - margin);
        return rect;
    }

    private RectF rightLayoutRect() {
        int margin = 50;
        RectF rect = new RectF( margin, surfaceView.getMeasuredHeight() / 2 + margin, surfaceView.getMeasuredWidth() - margin, surfaceView.getMeasuredHeight()   - margin);
        return rect;
    }


    private void testController() {
    }

    private void testLayoutRun() {
        long start = System.currentTimeMillis();
        LayoutBlock left = new LayoutBlock();
        left.layoutWithCallback(leftLayoutRect(), new LayoutBlock.Callback() {
            @Override
            public boolean hasNextRun() {
                return generator.hasNext();
            }

            @Override
            public LayoutRun getRun() {
                return generator.getRun(textStyle, 400);
            }

            @Override
            public void moveToPrevRun() {
                generator.moveToPrevRun();
            }

            @Override
            public void moveToNextRun() {
                generator.moveToNextRun();
            }

            @Override
            public boolean breakRun(float width) {
                return false;
            }

            @Override
            public Style styleForRun(LayoutRun run) {
                return textStyle;
            }
        });

        LayoutBlock right = new LayoutBlock();
        right.layoutWithCallback(rightLayoutRect(), new LayoutBlock.Callback() {
            @Override
            public boolean hasNextRun() {
                return generator.hasNext();
            }

            @Override
            public void moveToPrevRun() {
                generator.moveToPrevRun();
            }

            @Override
            public LayoutRun getRun() {
                return generator.getRun(textStyle, 400);
            }

            @Override
            public void moveToNextRun() {
                generator.moveToNextRun();
            }

            @Override
            public boolean breakRun(float width) {
                return false;
            }

            @Override
            public Style styleForRun(LayoutRun run) {
                return textStyle;
            }
        });

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        Canvas canvas = holder.lockCanvas();
        canvas.drawColor(Color.WHITE);
        for(LayoutRunLine lineManager: left.getLineList()) {
            canvas.drawRect(lineManager.getLineRect(), textStyle.getPaint());
            for (LayoutRun layoutRun : lineManager.getRunList()) {
                if (layoutRun.isWord() || layoutRun.isPunctuation()) {
                    final Paint.FontMetrics fontMetrics = textStyle.getPaint().getFontMetrics();

                    canvas.drawText(layoutRun.getText(),
                            layoutRun.getStart(),
                            layoutRun.getEnd(),
                            layoutRun.getPositionRect().left, layoutRun.getPositionRect().top - fontMetrics.top - fontMetrics.bottom, textStyle.getPaint());
                }
            }
        }

        for(LayoutRunLine lineManager: right.getLineList()) {
            canvas.drawRect(lineManager.getLineRect(), textStyle.getPaint());
            for (LayoutRun layoutRun : lineManager.getRunList()) {
                if (layoutRun.isWord() || layoutRun.isPunctuation()) {
                    final Paint.FontMetrics fontMetrics = textStyle.getPaint().getFontMetrics();
                    canvas.drawText(layoutRun.getText(),
                            layoutRun.getStart(),
                            layoutRun.getEnd(),
                            layoutRun.getPositionRect().left, layoutRun.getPositionRect().top - fontMetrics.top - fontMetrics.bottom, textStyle.getPaint());
                }
            }
        }

        canvas.drawRect(leftLayoutRect(), paint);
        canvas.drawRect(rightLayoutRect(), paint);
        holder.unlockCanvasAndPost(canvas);

        long end = System.currentTimeMillis();
        Log.d(TAG, "layout and rendering takes: " + (end - start));
    }

    private Style randStyle() {
        Paint paint = new Paint();
        paint.setTextSize(40);//TestUtils.randInt(10, 80));
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