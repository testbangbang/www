package com.onyx.android.sdk.scribble;

import android.app.Application;
import android.graphics.Bitmap;
import android.test.ApplicationTestCase;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sdk.scribble.data.PageNameList;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;
import com.onyx.android.sdk.utils.TestUtils;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.config.ShapeGeneratedDatabaseHolder;
import com.raizlabs.android.dbflow.sql.language.Delete;

import java.util.*;

/**
 * Created by zhuzeng on 6/21/16.
 */
public class NoteTest extends ApplicationTestCase<Application> {

    private static boolean init = false;

    public NoteTest() {
        super(Application.class);
    }

    private void initDB() {
        if (init) {
            return;
        }
        FlowConfig.Builder builder = new FlowConfig.Builder(getContext());
        builder.addDatabaseHolder(ShapeGeneratedDatabaseHolder.class);
        FlowManager.init(builder.build());
        init = true;
    }

    private NoteModel randomShapeModel() {
        NoteModel shapeModel = new NoteModel();
        shapeModel.setUniqueId(UUID.randomUUID().toString());
        return shapeModel;
    }

    public void testSave() {
        initDB();
        final NoteModel document = randomShapeModel();
        NoteDataProvider.saveNote(getContext(), document);
        NoteModel model = NoteDataProvider.load(getContext(), document.getUniqueId());
        assertNotNull(model);
        assertTrue(model.getUniqueId().equalsIgnoreCase(document.getUniqueId()));
        NoteDataProvider.remove(getContext(), document.getUniqueId());
        model = NoteDataProvider.load(getContext(), document.getUniqueId());
        assertNull(model);
    }

    public void testMove() {
        initDB();
        final NoteModel document = randomShapeModel();
        NoteDataProvider.saveNote(getContext(), document);
        NoteModel model = NoteDataProvider.load(getContext(), document.getUniqueId());
        assertNotNull(model);
        assertTrue(model.getUniqueId().equalsIgnoreCase(document.getUniqueId()));

        final String newParentId = UUID.randomUUID().toString();
        NoteDataProvider.moveNote(getContext(), document.getUniqueId(), newParentId);

        model = NoteDataProvider.load(getContext(), document.getUniqueId());
        assertNotNull(model);
        assertTrue(model.getParentUniqueId().equalsIgnoreCase(newParentId));
    }

    public void testDocumentList() {
        initDB();
        Delete.tables(NoteModel.class);
        int max = TestUtils.randInt(10, 30);
        Map<String, NoteModel> map = new HashMap<String, NoteModel>();
        for(int i = 0; i < max; ++i) {
            final NoteModel document = randomShapeModel();
            NoteDataProvider.saveNote(getContext(), document);
            map.put(document.getUniqueId(), document);
        }
        List<NoteModel> list = NoteDataProvider.loadNoteList(getContext(), null);
        assertTrue(list.size() == map.size());
        for(NoteModel model : list) {
            assertTrue(map.containsKey(model.getUniqueId()));
        }

        int limit = TestUtils.randInt(1, max);
        List<String> newLibrary = new ArrayList<String>();
        for(int i = 0; i < limit; ++i) {
            final String newLibraryId = UUID.randomUUID().toString();
            newLibrary.add(newLibraryId);
            final NoteModel model = list.get(i);
            NoteDataProvider.moveNote(getContext(), model.getUniqueId(), newLibraryId);
        }

        for(String string: newLibrary) {
            final List<NoteModel> subList = NoteDataProvider.loadNoteList(getContext(), string);
            assertTrue(subList.size() == 1);
            final NoteModel model = subList.get(0);
            assertTrue(model.getParentUniqueId().equalsIgnoreCase(string));
        }

        list = NoteDataProvider.loadNoteList(getContext(), null);
        assertTrue(list.size() == map.size() - newLibrary.size());
    }

    public void testThumbnail() {
        initDB();
        Delete.tables(NoteModel.class);
        NoteDataProvider.removeAllThumbnails(getContext());
        int max = TestUtils.randInt(10, 30);
        Map<String, NoteModel> map = new HashMap<String, NoteModel>();
        for(int i = 0; i < max; ++i) {
            final NoteModel document = randomShapeModel();
            NoteDataProvider.saveNote(getContext(), document);
            map.put(document.getUniqueId(), document);
            assertFalse(NoteDataProvider.hasThumbnail(getContext(), document.getUniqueId()));
            final int width = TestUtils.randInt(100, 500);
            final int height = TestUtils.randInt(100, 500);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            NoteDataProvider.saveThumbnail(getContext(), document.getUniqueId(), bitmap);
            assertTrue(NoteDataProvider.hasThumbnail(getContext(), document.getUniqueId()));
            final Bitmap result = NoteDataProvider.loadThumbnail(getContext(), document.getUniqueId());
            assertNotNull(result);
            assertEquals(result.getWidth(), width);
            assertEquals(result.getHeight(), height);
        }
        NoteDataProvider.removeAllThumbnails(getContext());
    }

    public void testCreateAndRemove() {
        initDB();
        Delete.tables(NoteModel.class);
        final NoteModel noteModel = NoteDataProvider.createLibrary(getContext(), ShapeUtils.generateUniqueId(), null, ShapeUtils.generateUniqueId());
        final NoteModel result = NoteDataProvider.load(getContext(), noteModel.getUniqueId());
        assertNotNull(result);
        assertEquals(result.getTitle(), noteModel.getTitle());

        NoteDataProvider.remove(getContext(), noteModel.getUniqueId());
        NoteModel result2 = NoteDataProvider.load(getContext(), noteModel.getUniqueId());
        assertNull(result2);
    }

    public void testCreateAndRemove2() {
        initDB();
        Delete.tables(NoteModel.class);
        final NoteModel noteModel = NoteDataProvider.createNote(getContext(), UUID.randomUUID().toString(),  null, UUID.randomUUID().toString());
        final NoteModel result = NoteDataProvider.load(getContext(), noteModel.getUniqueId());
        assertNotNull(result);
        assertEquals(result.getTitle(), noteModel.getTitle());

        NoteDataProvider.remove(getContext(), noteModel.getUniqueId());
        NoteModel result2 = NoteDataProvider.load(getContext(), noteModel.getUniqueId());
        assertNull(result2);
    }

    public void testPageNameList() {
        initDB();
        Delete.tables(NoteModel.class);
        final NoteModel noteModel = NoteDataProvider.createNote(getContext(), UUID.randomUUID().toString(), null, UUID.randomUUID().toString());
        final NoteModel result = NoteDataProvider.load(getContext(), noteModel.getUniqueId());
        assertNotNull(result);
        assertEquals(result.getTitle(), noteModel.getTitle());
        assertNull(result.getPageNameList());

        PageNameList src = new PageNameList();
        int max = TestUtils.randInt(10, 100);
        for(int i = 0; i < max; ++i) {
            src.add(ShapeUtils.generateUniqueId());
        }
        noteModel.setPageNameList(src);
        NoteDataProvider.saveNote(getContext(), noteModel);
        final NoteModel result2 = NoteDataProvider.load(getContext(), noteModel.getUniqueId());
        assertNotNull(result2);
        assertEquals(result2.getPageNameList().size(), noteModel.getPageNameList().size());
        for(int i = 0; i < result2.getPageNameList().size(); ++i) {
            assertEquals(result2.getPageNameList().get(i), noteModel.getPageNameList().get(i));
        }
    }


}
