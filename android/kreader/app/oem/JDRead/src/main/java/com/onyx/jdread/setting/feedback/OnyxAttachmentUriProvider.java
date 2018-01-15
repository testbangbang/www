package com.onyx.jdread.setting.feedback;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.onyx.android.sdk.utils.LogUtils;
import com.onyx.jdread.JDReadApplication;

import org.acra.ACRA;
import org.acra.annotation.AcraCore;
import org.acra.attachment.AttachmentUriProvider;
import org.acra.config.CoreConfiguration;
import org.acra.file.Directory;

import java.util.ArrayList;
import java.util.List;

import static org.acra.ACRA.LOG_TAG;

/**
 * <pre>
 *     author : liao lin tao
 *     time   : 2018/1/9 11:44
 *     desc   :
 * </pre>
 */

public class OnyxAttachmentUriProvider implements AttachmentUriProvider {

    @NonNull
    @Override
    public List<Uri> getAttachments(@NonNull Context context, @NonNull CoreConfiguration configuration) {
        List<Uri> result = new ArrayList<>();

        List<String> attachFileModels = initFile();
        for (String uriString : attachFileModels) {
            try {
                result.add(Uri.parse(uriString));
            } catch (Exception e) {
                ACRA.log.e(LOG_TAG, "Failed to parse Uri " + uriString, e);
            }
        }
        return result;
    }

    /**
     * Configure the log file you want to send.
     */
    @NonNull
    private List<String> initFile() {
        List<String> result = new ArrayList<>();
        // TODO: 2018/1/9 add your log file
        return result;
    }

    /**
     * Refer to {@link AcraCore#attachmentUris()}, {@link AcraCore#attachmentUriProvider()}
     *
     * @param directory refer to {@link Directory}
     * @param filePath  relative path. eg: log/util-01-06.txt
     * @return eg: <code>content://com.onyx.edu.teacher.acra/external_cache/log/util-01-06.txt</code>
     */
    @NonNull
    private String getUriStr(@NonNull Directory directory, @NonNull String filePath) {
        return "content://"
                + JDReadApplication.getInstance().getPackageName() + ".acra/"
                + directory.name().toLowerCase() + "/"
                + filePath;
    }

    @NonNull
    private String getUriStr(@NonNull String filePath) {
        return "content://" + filePath;
    }
}
