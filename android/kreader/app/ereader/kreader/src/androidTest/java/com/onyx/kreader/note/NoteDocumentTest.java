package com.onyx.kreader.note;

import android.graphics.Matrix;
import android.test.ActivityInstrumentationTestCase2;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.shape.NormalPencilShape;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;
import com.onyx.android.sdk.utils.Benchmark;
import com.onyx.android.sdk.utils.TestUtils;
import com.onyx.kreader.note.data.ReaderNoteDocument;
import com.onyx.kreader.note.data.ReaderNotePage;
import com.onyx.kreader.note.model.ReaderNoteDataProvider;
import com.onyx.kreader.tests.ReaderTestActivity;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by zhuzeng on 9/17/16.
 */
public class NoteDocumentTest extends ActivityInstrumentationTestCase2<ReaderTestActivity> {

    public NoteDocumentTest() {
        super(ReaderTestActivity.class);
    }

    private TouchPoint randomTouchPoint() {
        TouchPoint touchPoint = new TouchPoint(
                TestUtils.randInt(0, 1000),
                TestUtils.randInt(0, 1000),
                TestUtils.randInt(0, 1000),
                TestUtils.randInt(0, 1000),
                TestUtils.randInt(0, 1000));
        return touchPoint;
    }


    private Shape randomShape(final String documentId, final String pageUniqueId, int min, int max) {
        Shape shape = new NormalPencilShape();
        shape.setDocumentUniqueId(documentId);
        shape.setPageUniqueId(pageUniqueId);
        shape.setShapeUniqueId(UUID.randomUUID().toString());
        int points = TestUtils.randInt(min, max);
        final TouchPointList list = new TouchPointList(points);
        for(int i = 0; i < points; ++i) {
            list.add(randomTouchPoint());
        }
        shape.addPoints(list);
        return shape;
    }


    public void testOpen() {
        ReaderNoteDataProvider.clear(getActivity());

        final String docId = ShapeUtils.generateUniqueId();
        final String pageName = "0";
        final ReaderNoteDocument src = new ReaderNoteDocument();
        src.open(getActivity(), docId, null);
        final ReaderNotePage page = src.createPage(pageName, 0);
        assertTrue(page.getShapeList().size() == 0);
        final Shape shape = randomShape(docId, page.getPageUniqueId(), 10, 100);
        page.addShape(shape, false);
        src.save(getActivity(), "test");
        src.close(getActivity());


        final ReaderNoteDocument dst = new ReaderNoteDocument();
        dst.open(getActivity(), docId, null);
        final ReaderNotePage resultPage = dst.loadPage(getActivity(), pageName, 0);
        assertNotNull(resultPage);
        assertNotNull(resultPage.getShapeList());
        assertTrue(resultPage.getShapeList().size() == 1);
        assertEquals(resultPage.getShapeList().get(0).getShapeUniqueId(), shape.getShapeUniqueId());
    }


    public void testMultiSubPages() {
        ReaderNoteDataProvider.clear(getActivity());

        final String docId = ShapeUtils.generateUniqueId();
        final ReaderNoteDocument src = new ReaderNoteDocument();
        src.open(getActivity(), docId, null);

        HashMap<String, LinkedHashMap<String, ReaderNotePage>> origin = new HashMap<>();

        int pageCount = TestUtils.randInt(5, 10);
        for(int i = 0; i < pageCount; ++i) {
            String pageName = String.valueOf(i);
            LinkedHashMap<String, ReaderNotePage> subPageMap = new LinkedHashMap<>();
            int subPageCount = TestUtils.randInt(2, 5);
            for(int k = 0; k < subPageCount; ++k) {
                final ReaderNotePage page = src.createPage(pageName, k);
                assertTrue(page.getShapeList().size() == 0);
                int shapeCount = TestUtils.randInt(10, 20);
                for (int j = 0; j < shapeCount; ++j) {
                    final Shape shape = randomShape(docId, page.getPageUniqueId(), 10, 50);
                    page.addShape(shape, false);
                }
                subPageMap.put(page.getSubPageUniqueId(), page);
            }
            origin.put(pageName, subPageMap);
        }
        src.save(getActivity(), "test");
        src.close(getActivity());


        // re-open for verify
        final ReaderNoteDocument dst = new ReaderNoteDocument();
        dst.open(getActivity(), docId, null);

        for(int i = 0; i < pageCount; ++i) {
            String pageName = String.valueOf(i);
            int subPageCount = dst.getSubPageCount(pageName);
            for(int j = 0; j < subPageCount; ++j) {
                final ReaderNotePage resultPage = dst.loadPage(getActivity(), pageName, j);
                final LinkedHashMap<String, ReaderNotePage> map = origin.get(pageName);
                final ReaderNotePage originPage = map.get(resultPage.getSubPageUniqueId());

                // compare with origin page
                assertNotNull(resultPage);
                assertNotNull(resultPage.getShapeList());
                assertEquals(originPage.getPageUniqueId(), resultPage.getPageUniqueId());
                assertEquals(originPage.getSubPageUniqueId(), resultPage.getSubPageUniqueId());
                assertEquals(originPage.getDocumentUniqueId(), resultPage.getDocumentUniqueId());
                assertTrue(resultPage.getShapeList().size() == originPage.getShapeList().size());
                for(int k = 0; k < originPage.getShapeList().size(); ++k) {
                    assertEquals(resultPage.getShapeList().get(k).getShapeUniqueId(), originPage.getShapeList().get(k).getShapeUniqueId());
                }
            }
        }
    }


    public void testMultiSubPagesWithRemove() {
        ReaderNoteDataProvider.clear(getActivity());

        final String docId = ShapeUtils.generateUniqueId();
        final ReaderNoteDocument src = new ReaderNoteDocument();
        src.open(getActivity(), docId, null);

        HashMap<String, LinkedHashMap<String, ReaderNotePage>> origin = new HashMap<>();

        int pageCount = TestUtils.randInt(5, 10);
        for(int i = 0; i < pageCount; ++i) {
            String pageName = String.valueOf(i);
            LinkedHashMap<String, ReaderNotePage> subPageMap = new LinkedHashMap<>();
            int subPageCount = TestUtils.randInt(2, 5);
            for(int k = 0; k < subPageCount; ++k) {
                final ReaderNotePage page = src.createPage(pageName, k);
                assertTrue(page.getShapeList().size() == 0);
                int shapeCount = TestUtils.randInt(10, 20);
                for (int j = 0; j < shapeCount; ++j) {
                    final Shape shape = randomShape(docId, page.getPageUniqueId(), 10, 50);
                    page.addShape(shape, false);
                }
                subPageMap.put(page.getSubPageUniqueId(), page);
            }
            origin.put(pageName, subPageMap);
        }
        src.save(getActivity(), "test");
        src.close(getActivity());


        // re-open for verify
        final ReaderNoteDocument temp = new ReaderNoteDocument();
        temp.open(getActivity(), docId, null);

        for(int i = 0; i < pageCount; ++i) {
            String pageName = String.valueOf(i);
            int subPageCount = temp.getSubPageCount(pageName);
            int subPageIndex = TestUtils.randInt(0, subPageCount);
            temp.clearPage(getActivity(), pageName, subPageIndex);
        }
        temp.save(getActivity(), "some title");


        // open again
        final ReaderNoteDocument verify = new ReaderNoteDocument();
        verify.open(getActivity(), docId, null);
        for(int i = 0; i < pageCount; ++i) {
            String pageName = String.valueOf(i);
            int subPageCount = verify.getSubPageCount(pageName);
            LinkedHashMap<String, ReaderNotePage> originSubPageList = origin.get(pageName);
            assertTrue(subPageCount + 1 == originSubPageList.size());

            for(int j = 0; j < subPageCount; ++j) {
                final ReaderNotePage resultPage = verify.loadPage(getActivity(), pageName, j);
                final LinkedHashMap<String, ReaderNotePage> map = origin.get(pageName);
                final ReaderNotePage originPage = map.get(resultPage.getSubPageUniqueId());

                // compare with origin page
                assertNotNull(resultPage);
                assertNotNull(resultPage.getShapeList());
                assertEquals(originPage.getPageUniqueId(), resultPage.getPageUniqueId());
                assertEquals(originPage.getSubPageUniqueId(), resultPage.getSubPageUniqueId());
                assertEquals(originPage.getDocumentUniqueId(), resultPage.getDocumentUniqueId());
                assertTrue(resultPage.getShapeList().size() == originPage.getShapeList().size());
                for(int k = 0; k < originPage.getShapeList().size(); ++k) {
                    assertEquals(resultPage.getShapeList().get(k).getShapeUniqueId(), originPage.getShapeList().get(k).getShapeUniqueId());
                }
            }
        }
    }

    public void testMatrixEqual() {
        Matrix src = new Matrix();
        int scaleX = TestUtils.randInt(10, 300);
        int scaleY = TestUtils.randInt(10, 300);
        int offsetX = TestUtils.randInt(10, 3000);
        int offsetY = TestUtils.randInt(10, 3000);
        src.postScale(scaleX, scaleY);
        src.postTranslate(offsetX, offsetY);

        Matrix dst = new Matrix();
        dst.postScale(scaleX, scaleY);
        dst.postTranslate(offsetX, offsetY);
        assertTrue(src.equals(dst));

        dst = new Matrix();
        dst.postScale(scaleX + 1, scaleY);
        dst.postTranslate(offsetX, offsetY);
        assertFalse(src.equals(dst));

        dst = new Matrix();
        dst.postScale(scaleX, scaleY);
        dst.postTranslate(offsetX + 1, offsetY);
        assertFalse(src.equals(dst));

        dst = new Matrix(src);
        assertFalse(dst == src);
        assertTrue(src.equals(dst));

        dst.set(src);
        assertTrue(src.equals(dst));
    }


    public void test0Performance() {
        ReaderNoteDataProvider.clear(getActivity());

        final String docId = ShapeUtils.generateUniqueId();
        final ReaderNoteDocument src = new ReaderNoteDocument();
        src.open(getActivity(), docId, null);

        Benchmark benchmark = new Benchmark();
        int pages = TestUtils.randInt(1000, 2000);
        for(int i = 0; i < pages; ++i) {
            String pageName = String.valueOf(i);
            final ReaderNotePage page = src.createPage(pageName, 0);
            assertTrue(page.getShapeList().size() == 0);
            int shapes = TestUtils.randInt(1000, 2000);
            for(int s = 0; s < shapes; ++s) {
                final Shape shape = randomShape(docId, page.getPageUniqueId(), 10, 100);
                page.addShape(shape, false);
            }
            benchmark.restart();
            JSON.toJSON(page);
            src.save(getActivity(), "test");
            benchmark.report("save: " + page.getShapeList().size() + " takes: ");
        }
        src.close(getActivity());
    }
}
