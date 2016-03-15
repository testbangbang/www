package com.onyx.kreader.tests.layout;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import com.onyx.kreader.formats.model.BookModel;
import com.onyx.kreader.formats.model.Paragraph;
import com.onyx.kreader.formats.model.TextModel;
import com.onyx.kreader.formats.model.TextModelPosition;
import com.onyx.kreader.formats.model.entry.ParagraphEntry;
import com.onyx.kreader.formats.model.entry.TextParagraphEntry;
import com.onyx.kreader.tests.ReaderTestActivity;
import com.onyx.kreader.text.*;
import com.onyx.kreader.utils.TestUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zengzhu on 3/6/16.
 */
public class RunSplitterTest  extends ActivityInstrumentationTestCase2<ReaderTestActivity> {

    private String TAG = RunSplitterTest.class.getSimpleName();

    public RunSplitterTest() {
        super(ReaderTestActivity.class);
    }



    static public Style randStyle() {
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

    public void testSplitter1() {
        final String text = "这是 个混合test this a 混合测test!测试分词目的";
        final List<LayoutRun> list = new ArrayList<LayoutRun>();
        LayoutRunGenerator.splitText(list, text, randStyle());
        assertTrue(list.size() == 23);

        StringBuilder sb = new StringBuilder();
        int length = 0;
        for(LayoutRun run : list) {
            length += text.substring(run.getStart(), run.getEnd()).length();
            sb.append(text.substring(run.getStart(), run.getEnd()));
        }
        assertTrue(length == text.length());
        final String result = sb.toString();
        assertTrue(text.equals(result));
    }

    public void testSplitter2() {
        final String text = "这是  个 混合test  this a 混合测test!测试分词目的ру́сский язы́к, russkiy yazyk,";
        final List<LayoutRun> list = new ArrayList<LayoutRun>();
        LayoutRunGenerator.splitText(list, text, randStyle());
        assertTrue(list.size() == 35);

        StringBuilder sb = new StringBuilder();
        int length = 0;
        for(LayoutRun run : list) {
            Log.d(TAG, "run: " + text.substring(run.getStart(), run.getEnd()));
            length += text.substring(run.getStart(), run.getEnd()).length();
            sb.append(text.substring(run.getStart(), run.getEnd()));
        }
        assertTrue(length == text.length());
        final String result = sb.toString();
        assertTrue(text.equals(result));
    }

    public void testSplitter3() {
        final String text = "这是\n 个\t 混合test\t  this a 混合测test!测试分词目的ру́сский \tязы́к, russkiy yazyk,";
        final List<LayoutRun> list = new ArrayList<LayoutRun>();
        LayoutRunGenerator.splitText(list, text, randStyle());
        assertTrue(list.size() == 38);

        StringBuilder sb = new StringBuilder();
        int length = 0;
        for(LayoutRun run : list) {
            Log.d(TAG, "run: " + text.substring(run.getStart(), run.getEnd()));
            length += text.substring(run.getStart(), run.getEnd()).length();
            sb.append(text.substring(run.getStart(), run.getEnd()));
        }
        assertTrue(length == text.length());
        final String result = sb.toString();
        assertTrue(text.equals(result));
    }


    private final BookModel randomModel(final List<String> list) {
        final BookModel bookModel = new BookModel(null);
        final TextModel textModel = bookModel.getTextModel();

        int paragraphCount = TestUtils.randInt(1, 3);
        for(int i = 0; i < paragraphCount; ++i) {
            final Paragraph paragraph = Paragraph.create(Paragraph.ParagraphKind.TEXT_PARAGRAPH);
            int entryCount = TestUtils.randInt(1, 3);
            for(int j = 0; j < entryCount; ++j) {
                final String text = TestUtils.randString();
                list.add(text);
                final ParagraphEntry paragraphEntry = new TextParagraphEntry(text);
                paragraph.addEntry(paragraphEntry);
            }
            textModel.addParagraph(paragraph);
        }
        textModel.setLoadFinished(true);
        return bookModel;
    }

    public void testLayoutRunGenerator() {
        StringBuilder s1 = new StringBuilder();
        StringBuilder s2 = new StringBuilder();

        List<String> list = new ArrayList<String>();
        final BookModel bookModel = randomModel(list);
        for(String s: list) {
            s1.append(s);
        }

        TextModelPosition modelPosition = new TextModelPosition(null, bookModel);
        LayoutRunGenerator generator = new LayoutRunGenerator(modelPosition);

        final Style style = randStyle();
        while (generator.hasNext()) {
            final LayoutRun layoutRun = generator.getRun(style, 2000);
            final String text = layoutRun.getRealText();
            if (text != null) {
                s2.append(text);
            }
            generator.moveToNextRun();
        }

        final String source = s1.toString();
        final String target = s2.toString();
        assertTrue(source.equals(target));
    }


}
