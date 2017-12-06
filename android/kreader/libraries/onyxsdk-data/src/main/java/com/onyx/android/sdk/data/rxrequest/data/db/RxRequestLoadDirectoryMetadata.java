package com.onyx.android.sdk.data.rxrequest.data.db;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.MimeTypeUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by hehai on 17-12-6.
 */

public class RxRequestLoadDirectoryMetadata extends RxBaseDBRequest {
    private String directoryPath;
    private List<Metadata> metadataList = new ArrayList<>();
    private Set<String> dirPathSet = new HashSet<>();
    private Set<String> ignoreDirectory = new HashSet<>();

    public RxRequestLoadDirectoryMetadata(DataManager dm, String directoryPath) {
        super(dm);
        this.directoryPath = directoryPath;
    }

    public RxRequestLoadDirectoryMetadata(DataManager dm, String directoryPath, Set<String> ignoreDirectory) {
        super(dm);
        this.directoryPath = directoryPath;
        this.ignoreDirectory = ignoreDirectory;
    }

    public List<Metadata> getMetadataList() {
        return metadataList;
    }

    public Set<String> getDirPathSet() {
        return dirPathSet;
    }

    @Override
    public RxRequestLoadDirectoryMetadata call() throws Exception {
        File file = new File(directoryPath);
        File[] files = file.listFiles();
        for (File file1 : files) {
            if (isIgnoreDirectory(file1)) {
                continue;
            }
            if (file1.isDirectory()) {
                boolean b = FileUtils.haveBookInDirectory(file1);
                if (b) {
                    dirPathSet.add(file1.getAbsolutePath());
                }
            } else if (MimeTypeUtils.getDocumentExtension().contains(FileUtils.getFileExtension(file1))) {
                Metadata metadataByPath = getDataProvider().findMetadataByPath(getAppContext(), file1.getAbsolutePath());
                metadataList.add(metadataByPath);
            }
        }
        return this;
    }

    private boolean isIgnoreDirectory(File file1) {
        return ignoreDirectory.contains(file1.getAbsolutePath());
    }
}
