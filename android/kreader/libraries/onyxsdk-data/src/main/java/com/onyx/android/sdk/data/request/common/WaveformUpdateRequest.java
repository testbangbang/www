package com.onyx.android.sdk.data.request.common;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.common.PanelInfo;
import com.onyx.android.sdk.data.model.common.WaveformResult;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.v1.OnyxFileDownloadService;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.File;

import retrofit2.Response;

/**
 * Created by suicheng on 2017/12/19.
 */
public class WaveformUpdateRequest extends BaseCloudRequest {

    private String serverApi;
    private PanelInfo panelInfo;
    private String downloadPath;
    private String md5Path;

    private WaveformResult result;
    private boolean success;

    public WaveformUpdateRequest(@Nullable String serverApi, @NonNull PanelInfo panelInfo, @NonNull String downloadPath,
                                 @NonNull String md5Path) {
        this.serverApi = serverApi;
        this.panelInfo = panelInfo;
        this.downloadPath = downloadPath;
        this.md5Path = md5Path;
    }

    public WaveformResult getResult() {
        return result;
    }

    public boolean isSuccessful() {
        return success;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        if (StringUtils.isNullOrEmpty(serverApi)) {
            serverApi = parent.getCloudConf().getApiBase();
        }
        result = fetchUpdateResult(serverApi, panelInfo);
        if (!WaveformResult.checkValid(result)) {
            return;
        }
        if (result.md5.equalsIgnoreCase(getActualMD5(md5Path))) {
            success = true;
            return;
        }
        String tmpPath = getTempFilePath(downloadPath);
        if (!downloadFile(parent, result.url, tmpPath)) {
            return;
        }
        try {
            success = checkFileMd5(tmpPath, result.md5) &&
                    FileUtils.copyFile(new File(tmpPath), new File(downloadPath)) &&
                    saveToMD5File(result.md5, downloadPath, md5Path);
            if (success) {
                setFileReadable(downloadPath);
                setFileReadable(md5Path);
            }
        } catch (Exception e) {
            setException(e);
        } finally {
            FileUtils.deleteFile(tmpPath);
        }
    }

    private String getTempFilePath(String path) {
        String tempPath = path + ".tmp";
        File file = new File(tempPath);
        if (!file.exists() && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        return tempPath;
    }

    private void setFileReadable(@NonNull String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.setReadable(true, false);
        }
    }

    private String getActualMD5(String md5FilePath) {
        if (!FileUtils.fileExist(md5FilePath)) {
            return null;
        }
        String content = FileUtils.readContentOfFile(md5FilePath);
        if (StringUtils.isNullOrEmpty(content)) {
            return null;
        }
        String[] result = content.split(" ");
        return result[0];
    }

    private boolean saveToMD5File(String md5, String targetFilePath, String md5Path) {
        return FileUtils.saveContentToFile(md5.toLowerCase() + " " + targetFilePath, new File(md5Path));
    }

    private WaveformResult fetchUpdateResult(String serverApi, PanelInfo panelInfo) {
        try {
            Response<WaveformResult> response = executeCall(ServiceFactory.getOTAService(serverApi).fetchWaveformUpdate(panelInfo));
            if (response.isSuccessful()) {
                return response.body();
            }
        } catch (Exception e) {
            setException(e);
        }
        return null;
    }

    private boolean downloadFile(CloudManager parent, String url, String path) throws Exception {
        try {
            OnyxFileDownloadService service = ServiceFactory.getFileDownloadService(parent.getCloudConf().getApiBase());
            return writeFileToDisk(parent, executeCall(service.fileDownload(url)).body(),
                    new File(path), getCallback());
        } catch (Exception e) {
            setException(e);
            FileUtils.deleteFile(path);
            return false;
        }
    }

    private boolean checkFileMd5(String filePath, String compareMd5) throws Exception {
        if (StringUtils.isNullOrEmpty(compareMd5) || !FileUtils.fileExist(filePath)) {
            return false;
        }
        try {
            String md5 = FileUtils.computeFullMD5Checksum(new File(filePath));
            return StringUtils.isNotBlank(md5) && md5.equalsIgnoreCase(compareMd5);
        } catch (Exception e) {
            return false;
        }
    }
}
