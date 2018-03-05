package com.onyx.jdread.setting.request;

import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.rxrequest.data.fs.RxBaseFSRequest;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.JDPreferenceManager;
import com.onyx.jdread.setting.model.ScreenSaversBean;
import com.onyx.jdread.setting.model.ScreenSaversModel;

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
        File config = new File(dir, "config.png");
        if (config.exists()) {
            String checkedPath = JDPreferenceManager.getStringValue(R.string.screen_saver_key, null);
            String s = FileUtils.readContentOfFile(config);
            List<ScreenSaversBean> beans = JSONObject.parseArray(s, ScreenSaversBean.class);
            for (ScreenSaversBean bean : beans) {
                ScreenSaversModel.ItemModel itemModel = new ScreenSaversModel.ItemModel();
                String path = dir + File.separator + bean.getPics().get(0);
                if (FileUtils.fileExist(path)) {
                    itemModel.picPath.set(path);
                    itemModel.pics.addAll(bean.getPics());
                    itemModel.name.set(bean.getDisplay());
                    itemModel.isChecked.set(StringUtils.isNotBlank(checkedPath) ? checkedPath.equals(bean.getDisplay()) : bean.isChecked());
                    pics.add(itemModel);
                }
            }
        }
        return this;
    }

    public List<ScreenSaversModel.ItemModel> getPics() {
        return pics;
    }
}
