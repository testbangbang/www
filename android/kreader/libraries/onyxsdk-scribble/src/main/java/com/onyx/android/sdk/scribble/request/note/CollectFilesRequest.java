package com.onyx.android.sdk.scribble.request.note;

import com.onyx.android.sdk.data.FileInfo;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;
import com.onyx.android.sdk.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by ming on 2017/7/21.
 */

public class CollectFilesRequest extends BaseNoteRequest {

    private String parentPath;
    private Set<String> extensionFilters;
    private boolean recursive;
    private Collection<String> fileList;

    public CollectFilesRequest(String parentPath, Set<String> extensionFilters, boolean recursive, Collection<String> fileList) {
        this.parentPath = parentPath;
        this.extensionFilters = extensionFilters;
        this.recursive = recursive;
        this.fileList = fileList;
    }

    @Override
    public void execute(NoteViewHelper helper) throws Exception {
        FileUtils.collectFiles(parentPath, extensionFilters, recursive, fileList);
    }
}
