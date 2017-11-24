package com.onyx.android.sdk.data.rxrequest.data.db;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.compatability.OnyxMetadata;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.transaction.CmsHelper;
import com.onyx.android.sdk.utils.Benchmark;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by hehai on 17-11-23.
 */

public class RxExtractMetadataRequest extends RxBaseDBRequest {
    private QueryArgs queryArg;
    private boolean forceScanMetadata;

    public RxExtractMetadataRequest(DataManager dm, QueryArgs queryArg) {
        super(dm);
        this.queryArg = queryArg;
    }

    public void setForceScanMetadata(boolean forceScanMetadata) {
        this.forceScanMetadata = forceScanMetadata;
    }

    @Override
    public Scheduler subscribeScheduler() {
        return Schedulers.newThread();
    }

    @Override
    public RxExtractMetadataRequest call() throws Exception {
        List<Metadata> metadataList = getDataProvider().findMetadataByQueryArgs(getAppContext(), queryArg);
        extractMetadata(metadataList);
        return this;
    }

    private void extractMetadata(List<Metadata> metadataList) {
        if (CollectionUtils.isNullOrEmpty(metadataList)) {
            return;
        }
        for (Metadata metadata : metadataList) {
            OnyxMetadata onyxMetadata = CmsHelper.getMetadata(getAppContext(), metadata.getAssociationId());
            if (onyxMetadata == null || forceScanMetadata) {
                Benchmark benchmark = new Benchmark();
                CmsHelper.extractMetadata(this, metadata, false);
                benchmark.report("extractMetadata");
            }
        }
    }
}
