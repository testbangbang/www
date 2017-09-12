package com.onyx.android.sdk.data.request.data.fs;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.FileErrorPolicy;
import com.onyx.android.sdk.data.FileReplacePolicy;
import com.onyx.android.sdk.data.common.ContentException;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by suicheng on 2017/9/12.
 */
public class FileCopyRequest extends BaseFSRequest {

    private File sourceDir, targetDir;
    private List<File> sourceFlattenedFileList;
    private List<File> replaceTargetFileList = new ArrayList<>();
    private List<File> skipSourceFileList = new ArrayList<>();
    private List<File> sourceFiles = new ArrayList<>();
    private boolean isCut;

    private File replaceFile, errorFile;
    private AtomicBoolean abortHolder = new AtomicBoolean();

    public FileReplacePolicy replacePolicy = FileReplacePolicy.Ask;
    public FileErrorPolicy errorPolicy = FileErrorPolicy.Retry;

    public FileCopyRequest(final List<File> sourceFiles, final File targetDir, boolean isCut) {
        this.sourceFiles = sourceFiles;
        this.targetDir = targetDir;
        this.isCut = isCut;
        this.sourceDir = sourceFiles.get(0).getParentFile();
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        checkSourceTargetInSameDir();
        processFileCopy();
    }

    private boolean checkSourceTargetInSameDir() throws ContentException {
        if (sourceDir.equals(targetDir)) {
            throw new ContentException.FileCopyException(sourceDir, targetDir);
        }
        return false;
    }

    private void prevSetSourceFlattenedList() {
        getSourceFlattenedFileList();
    }

    private void prevProcessReplaceFile() {
        if (replaceFile != null) {
            if (isFileReplaceSkippedByUser()) {
                skipFileReplace(replaceFile);
                replaceFile = null;
            }
        }
    }

    private void prevProcessErrorFile() {
        if (errorFile != null) {
            if (isFileErrorSkippedByUser()) {
                skipFileError(errorFile);
                errorFile = null;
            }
        }
    }

    private List<File> getSourceFlattenedFileList() {
        if (sourceFlattenedFileList == null) {
            sourceFlattenedFileList = getFlattenedFileList(sourceFiles);
        }
        return sourceFlattenedFileList;
    }

    private File getSourceFlattenedFileFirstOne() {
        return getSourceFlattenedFileList().get(0);
    }

    private File removeSourceFlattenedFileFirstOne() {
        return getSourceFlattenedFileList().remove(0);
    }

    private List<File> getSkipSourceFileList() {
        return skipSourceFileList;
    }

    private List<File> getReplaceTargetFileList() {
        return replaceTargetFileList;
    }

    private ArrayList<File> getFlattenedFileList(List<File> pathFileList) {
        ArrayList<File> flattenedFiles = new ArrayList<>();
        if (CollectionUtils.isNullOrEmpty(pathFileList)) {
            return flattenedFiles;
        }
        for (File file : pathFileList) {
            FileUtils.collectFileTree(file, flattenedFiles, abortHolder);
        }
        Collections.reverse(flattenedFiles);
        return flattenedFiles;
    }

    private void skipFileReplace(final File file) {
        skipFile(file);
        if (replacePolicy == FileReplacePolicy.Skip) {
            replacePolicy = FileReplacePolicy.Ask;
        }
    }

    private void skipFile(final File file) {
        removeSourceFlattenedFileFirstOne();
        getSkipSourceFileList().add(file);
    }

    private boolean isFileReplaceSkippedByUser() {
        return replacePolicy.isSkipPolicy();
    }

    private boolean isFileErrorSkippedByUser() {
        return errorPolicy.isSkipPolicy();
    }

    private void skipFileError(final File file) {
        skipFile(file);
        if (errorPolicy == FileErrorPolicy.Skip) {
            errorPolicy = FileErrorPolicy.Retry;
        }
    }

    private boolean isFileSkipped(File file) {
        for (File skipFile : getSkipSourceFileList()) {
            if (skipFile.getAbsolutePath().startsWith(file.getAbsolutePath())) {
                return true;
            }
        }
        return false;
    }

    private boolean isFileReplaceConfirmedByUser() {
        return replacePolicy.isReplacePolicy();
    }

    private boolean isInReplaceTargetList(File toReplace) {
        for (File target : getReplaceTargetFileList()) {
            if (target.equals(toReplace)) {
                return true;
            }
        }
        return false;
    }

    private boolean beforeFileReplace(File source, File target) throws ContentException {
        if (isFileReplaceSkippedByUser()) {
            skipFileReplace(source);
            return false;
        } else if (isFileReplaceConfirmedByUser() || isInReplaceTargetList(target)) {
            if (replacePolicy == FileReplacePolicy.Replace && !isInReplaceTargetList(target)) {
                replaceTargetFileList.add(target);
                replacePolicy = FileReplacePolicy.Ask;
            }

            if (source.isDirectory() && target.isDirectory()) {
                return true;
            }
            if (FileUtils.deleteFile(target, true)) {
                return true;
            }
            if (isFileErrorSkippedByUser()) {
                skipFileError(source);
                return false;
            } else {
                errorFile = source;
                throw new ContentException.FileDeleteException(target);
            }
        } else {
            replaceFile = source;
            throw new ContentException.FileAskForReplaceException(source, target);
        }
    }

    private boolean beforeCopy(final File source) throws ContentException {
        String subPath = source.getAbsolutePath().substring(sourceDir.getAbsolutePath().length());
        if (subPath.startsWith("/")) {
            subPath = subPath.substring(1);
        }

        File currentSourceDir = sourceDir;
        File currentTargetDir = targetDir;
        String[] segments = subPath.split("/");
        for (int i = 0; i < segments.length - 1; i++) {
            currentSourceDir = new File(currentSourceDir, segments[i]);
            currentTargetDir = new File(currentTargetDir, segments[i]);
            if (!currentTargetDir.exists()) {
                if (currentTargetDir.mkdirs()) {
                    getReplaceTargetFileList().add(currentTargetDir);
                } else {
                    if (isFileErrorSkippedByUser()) {
                        skipFileError(currentSourceDir);
                    } else {
                        errorFile = currentSourceDir;
                        throw new ContentException.FileCreateException(currentTargetDir);
                    }
                }
            } else {
                if (!beforeFileReplace(currentSourceDir, currentTargetDir)) {
                    return false;
                }
            }
        }
        File target = new File(targetDir, subPath);
        return !target.exists() || beforeFileReplace(source, target);
    }

    private File targetFile(final File baseDir, final File sourceFile, final File targetDir) {
        String filePath = sourceFile.getAbsolutePath();
        if (filePath.startsWith(baseDir.getAbsolutePath())) {
            String relative = filePath.substring(baseDir.getAbsolutePath().length() + 1);
            return new File(targetDir, relative);
        }
        return null;
    }

    private boolean copyFile(File source) throws ContentException {
        File target = targetFile(sourceDir, source, targetDir);
        if (source.isDirectory()) {
            if (!target.exists() && !target.mkdirs()) {
                if (isFileErrorSkippedByUser()) {
                    skipFileError(source);
                    return false;
                } else {
                    throw new ContentException.FileCreateException(target);
                }
            }
        } else if (source.isFile()) {
            boolean success;
            if (isCut) {
                if (FileUtils.onSameSDCard(source, target)) {
                    success = source.renameTo(target);
                } else {
                    success = FileUtils.copyFile(source, target);
                    if (success) {
                        FileUtils.deleteFile(source);
                    }
                }
            } else {
                success = FileUtils.copyFile(source, target);
            }
            if (!success) {
                if (isFileErrorSkippedByUser()) {
                    skipFileError(source);
                    return false;
                } else {
                    throw new ContentException.FileCopyException(source, target);
                }
            }
        }
        return true;
    }

    private void processFileCopy() throws ContentException {
        prevSetSourceFlattenedList();
        prevProcessReplaceFile();
        prevProcessErrorFile();

        while (true) {
            if (isAbort() || CollectionUtils.isNullOrEmpty(getSourceFlattenedFileList())) {
                return;
            }

            final File source = getSourceFlattenedFileFirstOne();
            if (isFileSkipped(source)) {
                removeSourceFlattenedFileFirstOne();
                continue;
            }

            if (!beforeCopy(source)) {
                if (isFileSkipped(source)) {
                    removeSourceFlattenedFileFirstOne();
                    continue;
                } else {
                    return;
                }
            }

            if (!copyFile(source)) {
                if (isFileSkipped(source)) {
                    continue;
                } else {
                    return;
                }
            }
            if (isCut) {
                FileUtils.deleteFile(source);
            }
            removeSourceFlattenedFileFirstOne();
        }
    }

    @Override
    public void setAbort() {
        super.setAbort();
        abortHolder.set(isAbort());
    }
}
