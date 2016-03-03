package com.onyx.kreader.tests;

import android.app.Activity;
import android.graphics.*;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import com.onyx.kreader.R;
import com.onyx.kreader.formats.model.BookModel;
import com.onyx.kreader.formats.model.Paragraph;
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
    private SurfaceHolder holder;
    private SurfaceView surfaceView;

    final String path = "/mnt/sdcard/Books/test.txt";
    TxtReader reader = new TxtReader();
    BookModel bookModel = new BookModel(path);


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

        surfaceView = (SurfaceView)findViewById(R.id.surfaceView);
        holder = surfaceView.getHolder();
        reader.open(bookModel);
    }


    private void testLayout() {

        if (!reader.processNext(bookModel)) {
            return;
        }

        TextLayoutJustify instance = new TextLayoutJustify();
        RectF rect = new RectF(0, 0, surfaceView.getMeasuredWidth(), surfaceView.getMeasuredHeight());

        List<Element> textElementList = new ArrayList<Element>();
        final List<Paragraph> list = bookModel.getTextModel().getParagraphList();
        final Style style = randStyle();
        for (Paragraph paragraph : list) {
            for (ParagraphEntry entry : paragraph.getParagraphEntryList()) {
                if (entry instanceof TextParagraphEntry) {
                    TextParagraphEntry textParagraphEntry = (TextParagraphEntry) entry;
                    final String text = textParagraphEntry.getText();
                    for (int i = 0; i < text.length(); ++i) {
                        TextElement element = TextElement.create(text.substring(i, i + 1), style);
                        textElementList.add(element);
                    }
                }
            }

        }
        final List<LayoutLine> lines  = instance.layout(rect, textElementList);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(40);
        paint.setStyle(Paint.Style.STROKE);

        Canvas canvas = holder.lockCanvas();
        canvas.drawColor(Color.WHITE);
        for(Element element : textElementList) {
            element.draw(canvas);
        }
        canvas.drawRect(rect, paint);
        holder.unlockCanvasAndPost(canvas);

    }

    private Style randStyle() {
        Paint paint = new Paint();
        paint.setTextSize(35);
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