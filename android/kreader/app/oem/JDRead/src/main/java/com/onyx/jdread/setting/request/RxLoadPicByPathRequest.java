package com.onyx.jdread.setting.request;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.rxrequest.data.fs.RxBaseFSRequest;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.setting.model.ScreenSaversModel;
import com.onyx.jdread.setting.utils.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hehai on 18-1-1.
 */

public class RxLoadPicByPathRequest extends RxBaseFSRequest {
    private String dir;
    private List<ScreenSaversModel.ItemModel> pics = new ArrayList<>();

    public RxLoadPicByPathRequest(DataManager dm, String dir) {
        super(dm);
        this.dir = dir;
    }

    @Override
    public RxLoadPicByPathRequest call() throws Exception {
        pics.clear();
        File file = new File(dir);
        if (StringUtils.isNullOrEmpty(dir) || !file.exists() || file.isFile()) {
            return this;
        }

        File current = new File(Constants.STANDBY_PIC_DIRECTORY + Constants.STANDBY_PIC_NAME);
        String md5 = "";
        if (current.exists()) {
            md5 = FileUtils.computeMD5(current);
        }
        File[] files = file.listFiles();
        for (File f : files) {
            if (FileUtils.getFileExtension(f).equalsIgnoreCase("png")) {
                ScreenSaversModel.ItemModel itemModel = new ScreenSaversModel.ItemModel();
                itemModel.picPath.set(f.getAbsolutePath());
                itemModel.isChecked.set(FileUtils.computeMD5(f).equals(md5));
                pics.add(itemModel);
            }
        }
        return this;
    }

    public List<ScreenSaversModel.ItemModel> getPics() {
        return pics;
    }
}
