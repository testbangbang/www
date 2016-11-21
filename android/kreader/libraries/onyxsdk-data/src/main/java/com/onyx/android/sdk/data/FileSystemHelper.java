package com.onyx.android.sdk.data;

import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * Created by zhuzeng on 21/11/2016.
 */

public class FileSystemHelper {

    public static void collectFiles(final String path,
                                    final List<File> fileList,
                                    final HashMap<String, Long> hashMap,
                                    final BaseDataRequest request,
                                    int sizeLimit,
                                    final Set<String> ignoreList,
                                    final Set<String> extensionFilters) {
        File parent = new File(path);
        File[] files = parent.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (request.isAbort()) {
                return;
            }
            if (fileList != null && fileList.size() >= sizeLimit) {
                return;
            }
            if (file.isHidden()) {
                continue;
            }
            if (file.isFile() && file.length() < sizeLimit && !CollectionUtils.contains(ignoreList, file.getName())) {
                final String ext = FileUtils.getFileExtension(file);
                if (!StringUtils.isNullOrEmpty(ext) && CollectionUtils.contains(extensionFilters, ext)) {
                    if (fileList != null) {
                        fileList.add(file);
                    }
                    if (hashMap != null) {
                        hashMap.put(file.getAbsolutePath(), FileUtils.getLastChangeTime(file));
                    }
                }
            } else if (file.isDirectory()) {
                collectFiles(file.getAbsolutePath(), fileList, hashMap, request, sizeLimit, ignoreList, extensionFilters);
            }
        }
    }

    public static void collectFolders(final String path, final List<File> folderList, final BaseDataRequest request) {
        File parent = new File(path);
        File[] files = parent.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (request.isAbort()) {
                return;
            }
            if (file.isHidden()) {
                continue;
            }
            if (file.isFile()) {
                continue;
            } else if (file.isDirectory() && !file.getName().equals(".") && !file.getName().equals("..")) {
                folderList.add(file);
                collectFolders(file.getAbsolutePath(), folderList, request);
            }
        }
    }

    /**
     * get file list of tree of path by depth first
     *
     * @param path
     * @param flattenedFileList
     * @param request
     */
    public static void collectFileTree(final String path, final List<File> flattenedFileList, final BaseDataRequest request) {
        File rootFile = new File(path);
        if (!rootFile.exists()) {
            return;
        }

        // desc sort by file name
        Comparator<File> comparator = new Comparator<File>() {
            @Override
            public int compare(File lhs, File rhs) {
                return -lhs.getName().compareToIgnoreCase(rhs.getName());
            }
        };

        Stack<File> stack = new Stack<File>();
        stack.push(rootFile);
        while (!stack.isEmpty()) {
            if (request.isAbort()) {
                return;
            }

            File parent = stack.pop();
            flattenedFileList.add(parent);
            if (parent.isDirectory()) {
                File[] files = parent.listFiles();
                if (files == null) {
                    continue;
                }
                Arrays.sort(files, comparator);
                for (File f : files) {
                    if (!f.getName().equals(".") && !f.getName().equals("..")) {
                        stack.push(f);
                    }
                }
            }
        }
    }




}
