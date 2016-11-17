package com.onyx.android.sdk;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Library_Table;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.data.provider.DataProviderManager;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

import static com.onyx.android.sdk.MetadataTestUtils.getRandomLibrary;

/**
 * Created by zhuzeng on 17/11/2016.
 */

public class LibraryTest extends ApplicationTestCase<Application> {


    public LibraryTest() {
        super(Application.class);
    }

    public DataProviderBase getProviderBase() {
        MetadataTestUtils.init(getContext());
        return DataProviderManager.getDataProvider();
    }


    public void testAddLibrary() {
        final DataProviderBase providerBase = getProviderBase();
        providerBase.clearLibrary();

        Library library = getRandomLibrary();
        providerBase.addLibrary(library);
        Library testLibrary = providerBase.loadLibrary(library.getIdString());
        assertNotNull(testLibrary);
        assertTrue(testLibrary.getIdString().equals(library.getIdString()));
    }

    public void testUpdateLibrary() {
        final DataProviderBase providerBase = getProviderBase();
        providerBase.clearLibrary();

        Library library = getRandomLibrary();
        providerBase.addLibrary(library);
        library.setDescription(MetadataTestUtils.generateRandomUUID());
        providerBase.updateLibrary(library);
        Library testLibrary = providerBase.loadLibrary(library.getIdString());
        assertNotNull(testLibrary);
        assertTrue(testLibrary.getIdString().equals(library.getIdString()));
        assertTrue(testLibrary.getDescription().equals(library.getDescription()));
    }

    public void testDeleteLibrary() {
        final DataProviderBase providerBase = getProviderBase();
        providerBase.clearLibrary();

        Library library = getRandomLibrary();
        providerBase.addLibrary(library);
        assertTrue(library.getId() > 0);
        providerBase.deleteLibrary(library);
        Library testLibrary = providerBase.loadLibrary(library.getIdString());
        assertNull(testLibrary);
    }

    public void testQueryLibrary() {
        final DataProviderBase providerBase = getProviderBase();
        providerBase.clearLibrary();

        Library library = getRandomLibrary();
        providerBase.addLibrary(library);

        Library testLibrary = providerBase.loadLibrary(library.getIdString());
        assertNotNull(testLibrary);
        assertTrue(testLibrary.getIdString().equals(library.getIdString()));

        //test query
        String like = "%" + testLibrary.getDescription().substring(0, 8) + "%";
        List<Library> libraryList = new Select().from(Library.class)
                .where(Library_Table.description.like(like))
                .queryList();
        assertNotNull(libraryList);
        assertTrue(libraryList.size() > 0);

        providerBase.clearLibrary();
        final int count = 5;
        final Library[] librarySet = new Library[count];
        for (int i = 0; i < count; i++) {
            librarySet[i] = getRandomLibrary();
            providerBase.addLibrary(librarySet[i]);
        }
        libraryList = providerBase.loadAllLibrary(null);
        assertNotNull(libraryList);
        assertTrue(libraryList.size() >= count);

        librarySet[0].setParentUniqueId(MetadataTestUtils.generateRandomUUID());
        providerBase.updateLibrary(librarySet[0]);
        libraryList = providerBase.loadAllLibrary(librarySet[0].getParentUniqueId());
        assertNotNull(libraryList);
        assertTrue(libraryList.size() == 1);
    }

}
