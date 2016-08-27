package com.onyx.android.sdk;

import android.app.Application;
import android.test.ApplicationTestCase;
import com.onyx.android.sdk.dataprovider.*;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.utils.TestUtils;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.io.File;
import java.util.*;

/**
 * Created by zhuzeng on 8/26/16.
 */
public class MetadataTest extends ApplicationTestCase<Application> {

    private static boolean dbInit = false;

    public MetadataTest() {
        super(Application.class);
    }

    public void init() {
        if (dbInit) {
            return;
        }
        dbInit = true;
        AsyncDataProvider.init(getContext(), null);
    }

    public static Set<String> defaultContentTypes() {
        Set<String> defaultTypes = null;
        if (defaultTypes == null) {
            defaultTypes = new HashSet<String>();
            defaultTypes.add("epub");
            defaultTypes.add("pdf");
            defaultTypes.add("mobi");
            defaultTypes.add("prc");
            defaultTypes.add("rtf");
            defaultTypes.add("doc");
            defaultTypes.add("fb2");
            defaultTypes.add("txt");
            defaultTypes.add("docx");
            defaultTypes.add("chm");
            defaultTypes.add("djvu");
            defaultTypes.add("azw3");
            defaultTypes.add("zip");
        }
        return defaultTypes;
    }

    public static File generateRandomFolder(final String parent) {
        File dir = new File(parent);
        dir.mkdirs();

        File childFolder = new File(parent, Metadata.generateUniqueId());
        childFolder.mkdirs();
        return childFolder;
    }

    public static File generateRandomFile(final String parent, boolean hasExtension) {
        File dir = new File(parent);
        dir.mkdirs();

        final String ext;
        if (hasExtension) {
            ext =  "." + randomType();
        } else {
            ext = "";
        }
        File file = new File(parent, UUID.randomUUID().toString() + ext);
        StringBuilder builder = new StringBuilder();
        int limit = TestUtils.randInt(100, 1024);
        for(int i = 0; i < limit; ++i) {
            builder.append(UUID.randomUUID().toString());
        }
        FileUtils.saveContentToFile(builder.toString(), file);
        return file;
    }

    public static String randomType() {
        List<String> list = new ArrayList<String>();
        list.addAll(defaultContentTypes());
        int index = TestUtils.randInt(0, list.size() - 1);
        return list.get(index);
    }

    public static List<String> randomAuthors() {
        int value = TestUtils.randInt(1, 5);
        List<String> authors = new ArrayList<>();
        for(int i = 0; i < value; ++i) {
            authors.add(UUID.randomUUID().toString());
        }
        return authors;
    }

    public static Metadata randomReadingMetadata(final String parent, boolean hasExtension) {
        Metadata metadata = null;
        try {
            File file = generateRandomFile(parent, hasExtension);
            metadata = new Metadata();
            String md5 = FileUtils.computeMD5(file);
            metadata.setUniqueId(md5);
            metadata.setName(file.getName());
            metadata.setLocation(file.getAbsolutePath());
            metadata.setNativeAbsolutePath(file.getAbsolutePath());
            metadata.setSize(file.length());
            metadata.setType(FileUtils.getFileExtension(file.getName()));
            int value = TestUtils.randInt(0, 10);
            if (value < 5) {
                metadata.setProgress(new ReadingProgress(TestUtils.randInt(50, 100), 100).toString());
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            return metadata;
        }
    }

    public static Metadata randomMetadata(final String parent, boolean ext) {
        Metadata metadata = null;
        try {
            File file = generateRandomFile(parent, ext);
            metadata = new Metadata();
            String md5 = FileUtils.computeMD5(file);
            metadata.setUniqueId(md5);
            metadata.setName(file.getName());
            metadata.setLocation(file.getAbsolutePath());
            metadata.setNativeAbsolutePath(file.getAbsolutePath());
            metadata.setSize(file.length());
            metadata.setType(FileUtils.getFileExtension(file.getName()));
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            return metadata;
        }
    }

    public static String testFolder() {
        return "/mnt/media_rw/test";
    }

    public void test() {
        init();
        List<String> authors = randomAuthors();
        Metadata metadata = randomMetadata(testFolder(), true);
        metadata.setAuthors(StringUtils.join(authors, Metadata.DELIMITER));
        metadata.save();
        StringBuilder target = new StringBuilder();
        target.append(authors.get(0));
        List<Metadata> list = new Select().from(Metadata.class).where(Metadata_Table.authors.like("%" + target.toString() + "%")).or(Metadata_Table.authors.like("%" + UUID.randomUUID().toString() + "%")).queryList();
        assertNotNull(list);
        assertTrue(list.size() > 0);
    }
}

