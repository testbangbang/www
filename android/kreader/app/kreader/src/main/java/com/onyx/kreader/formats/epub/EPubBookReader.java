package com.onyx.kreader.formats.epub;

import com.onyx.kreader.formats.model.BookModel;
import com.onyx.kreader.formats.model.BookReader;
import com.onyx.kreader.formats.model.BookReaderContext;
import com.onyx.kreader.formats.model.zip.ZipFileEntry;
import com.onyx.kreader.formats.model.zip.ZipFileReader;
import com.onyx.android.sdk.utils.FileUtils;


/**
 * Created by zengzhu on 3/15/16.
 */
public class EPubBookReader implements BookReader {

    private ZipFileReader zipFileReader;
    private String packageResourceHref;
    private String ncxResourceHref;
    private EPubPackage manifest;

    public boolean open(final BookReaderContext bookReaderContext, final BookModel bookModel) {
        createFileReader();
        if (!getZipFileReader().open(bookReaderContext.path, bookReaderContext.password)) {
            return false;
        }
        setup(bookModel);
        return true;
    }

    private final ZipFileReader createFileReader() {
        if (zipFileReader == null) {
            zipFileReader = new ZipFileReader();
        }
        return zipFileReader;
    }

    private final ZipFileReader getZipFileReader() {
        return zipFileReader;
    }

    /**
     * read (OPF) epub spine.http://www.idpf.org/epub/20/spec/OPF_2.0.1_draft.htm#Section2.4.1
     * 1. getMetadataById container.xml or content.opf path at first
     * 2. parse the container.xml or content.opf retrieved at first step.
     * 3. parse ncx.
     */
    private void setup(final BookModel bookModel) {
        parsePackageResource();
        parseOPFResource();
        parseNcxResourceHref();
    }

    private ZipFileEntry getPackageResourceHref() {
        String defaultResult = "OEBPS/content.opf";
        ZipFileEntry containerEntry = getZipFileReader().getEntry("META-INF/container.xml");
        if (containerEntry != null) {
            return containerEntry;
        }
        return getZipFileReader().getEntry(defaultResult);
    }

    /**
     * parse the file of META-INF/container.xml or OEBPS/content.opf
     * to getMetadataById the package resource full path.
     */
    private void parsePackageResource() {
        final ZipFileEntry entry = getPackageResourceHref();
        EPubPackageResolver EPubPackageResolver = new EPubPackageResolver(entry);
        EPubPackageResolver.parse();
        packageResourceHref = EPubPackageResolver.getFullPath();
    }


    /**
     * Use EPubPackageParser to parse data and use getEPubPackage to retrieve information.
     * 1. metadata
     * 2. manifest
     * 3. spine
     * 4. guide
     */
    private void parseOPFResource() {
        final ZipFileEntry entry = getZipFileReader().getEntry(packageResourceHref);
        EPubPackageParser parser = new EPubPackageParser(entry);
        parser.parse();
        final EPubPackage ePubPackage = parser.getEPubPackage();
        updateNcxResourceHref(ePubPackage);
    }

    private void updateNcxResourceHref(final EPubPackage ePubPackage) {
        ncxResourceHref = ePubPackage.getNcxHref();
        ncxResourceHref = FileUtils.canonicalPath(packageResourceHref, ncxResourceHref);
    }

    private void parseNcxResourceHref() {
        final ZipFileEntry entry = getZipFileReader().getEntry(ncxResourceHref);
        EPubNCXParser parser = new EPubNCXParser(entry);
        parser.parse();
    }

    public boolean processNext(final BookModel bookModel) {
        return false;
    }

    public boolean close(final BookModel bookModel) {
        return false;
    }

}
