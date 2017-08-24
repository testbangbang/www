package com.onyx.edu.manager.request;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.v2.DeviceBind;
import com.onyx.android.sdk.data.model.v2.DeviceBind_Table;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by suicheng on 2017/8/23.
 */
public class DeviceBindExportToCsvRequest extends BaseDataRequest {

    private static final String COMMA = ",";

    private String exportFilePath;
    private boolean result;

    public boolean isSuccessful() {
        return result;
    }

    public DeviceBindExportToCsvRequest(String exportFilePath) {
        this.exportFilePath = exportFilePath;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        List<DeviceBind> deviceBindList = new Select().from(DeviceBind.class).orderBy(DeviceBind_Table.createdAt, false)
                .queryList();
        if (CollectionUtils.isNullOrEmpty(deviceBindList)) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        writeCsv(sb, "model", "mac", "installationId", "tag");
        Set<String> macSet = new HashSet<>();
        for (DeviceBind deviceBind : deviceBindList) {
            if (macSet.contains(deviceBind.mac)) {
                continue;
            }
            macSet.add(deviceBind.mac);
            writeCsv(sb, deviceBind.model, deviceBind.mac, deviceBind.installationId, deviceBind.tag);
        }
        flushExportResult(sb);
    }

    private void writeCsv(StringBuilder sb, String... var) {
        if (var == null || var.length <= 0) {
            return;
        }
        for (int i = 0; i < var.length; i++) {
            sb.append(var[i]);
            String divider = COMMA;
            if (i == var.length - 1) {
                divider = "\n";
            }
            sb.append(divider);
        }
    }

    private void flushExportResult(StringBuilder sb) throws Exception {
        FileUtils.ensureFileExists(exportFilePath);
        result = FileUtils.saveContentToFile(sb.toString().getBytes(), new File(exportFilePath));
    }
}
