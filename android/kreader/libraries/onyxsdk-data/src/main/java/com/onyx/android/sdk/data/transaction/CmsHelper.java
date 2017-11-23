package com.onyx.android.sdk.data.transaction;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import com.onyx.android.sdk.data.compatability.OnyxCmsCenter;
import com.onyx.android.sdk.data.compatability.OnyxMetadata;
import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.common.AppPreference;
import com.onyx.android.sdk.data.utils.MetadataUtils;
import com.onyx.android.sdk.reader.IMetadataService;
import com.onyx.android.sdk.rx.RxRequest;
import com.onyx.android.sdk.utils.ViewDocumentUtils;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

/**
 * Created by hehai on 17-11-22.
 */

public class CmsHelper {

    private static final String TAG = CmsHelper.class.getSimpleName();

    public static OnyxMetadata extractMetadata(final RxRequest request, final Metadata metadata, final boolean showDebugLog) {
        extractMetadata(request, Uri.fromFile(new File(metadata.getNativeAbsolutePath())), 20 * 1000, showDebugLog);
        return getMetadata(request.getAppContext(), metadata.getAssociationId());
    }

    public static OnyxMetadata getMetadata(Context context, final String associationId) {
        OnyxMetadata onyxMetadata = OnyxCmsCenter.getMetadataByMD5(context, associationId);
        return onyxMetadata;
    }

    static public boolean extractMetadata(final RxRequest request, final Uri fileUri, int ms, final boolean showDebugLog) {
        final File file = new File(fileUri.getPath());
        ComponentName componentName = ViewDocumentUtils.getKreaderServiceComponentName();
        return extractMetadata(request, file, componentName, showDebugLog);
    }

    static public boolean extractMetadata(final RxRequest request, final File file, ComponentName componentName, final boolean showDebugLog) {
        final ContentBrowserServiceConnection connection = new ContentBrowserServiceConnection();
        boolean ret = false;

        try {
            final Intent service = new Intent();
            service.setComponent(componentName);
            request.getAppContext().bindService(service, connection, Context.BIND_AUTO_CREATE | Context.BIND_NOT_FOREGROUND);
            connection.waitUntilConnected(request);
            IBinder remoteService = connection.getRemoteService();
            IMetadataService extractService = IMetadataService.Stub.asInterface(remoteService);
            ret = extractService.extractMetadataAndThumbnail(file.getAbsolutePath(), -1);
            if (!ret) {
                if (showDebugLog) {
                    Log.e(TAG, "extract file " + file.getAbsolutePath() + " failed");
                }
            }
            request.getAppContext().unbindService(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
}
