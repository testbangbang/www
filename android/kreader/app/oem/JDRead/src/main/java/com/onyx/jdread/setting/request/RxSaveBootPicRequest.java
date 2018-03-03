package com.onyx.jdread.setting.request;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.rxrequest.data.fs.RxBaseFSRequest;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.setting.utils.Constants;
import com.onyx.jdread.setting.utils.ScreenSaversUtil;

import java.util.List;

/**
 * Created by hehai on 17-3-28.
 */

public class RxSaveBootPicRequest extends RxBaseFSRequest {
    private List<String> pics;

    public RxSaveBootPicRequest(DataManager dataManager, List<String> pics) {
        super(dataManager);
        this.pics = pics;
    }

    @Override
    public RxSaveBootPicRequest call() throws Exception {
        if (CollectionUtils.isNullOrEmpty(pics)) {
            return this;
        }
        for (int i = 0; i < pics.size(); i++) {
            String format = String.format(ResManager.getString(R.string.standby_name_format), i + 1);
            ScreenSaversUtil.saveScreen(Constants.SYSTEM_MEADIA + pics.get(i), Constants.STANDBY_PIC_DIRECTORY + format);
        }
        return this;
    }
}
