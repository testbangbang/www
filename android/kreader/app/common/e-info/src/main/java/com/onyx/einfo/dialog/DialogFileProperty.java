package com.onyx.einfo.dialog;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.TextView;

import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;
import com.onyx.android.sdk.utils.DateTimeUtil;
import com.onyx.android.sdk.utils.MimeTypeUtils;
import com.onyx.einfo.R;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.Date;
import java.util.Locale;

/**
 * Created by solskjaer49 on 15/12/2 17:23.
 */
public class DialogFileProperty extends OnyxAlertDialog {
    static final public String ARGS_FILE_PATH = "args_file_path";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        final String filePath = getArguments().getString(ARGS_FILE_PATH);
        setParams(new OnyxAlertDialog.Params().setTittleString(getString(R.string.menu_file_property))
                .setCustomContentLayoutResID(R.layout.alert_dialog_content_file_property)
                .setCustomViewAction(new OnyxAlertDialog.CustomViewAction() {
                    @Override
                    public void onCreateCustomView(View customView, TextView pageIndicator) {
                        TextView fileName, fileSize, fileType, fileLastModifiedTime;
                        fileName = (TextView) customView.findViewById(R.id.textView_file_name);
                        fileSize = (TextView) customView.findViewById(R.id.textView_file_size);
                        fileType = (TextView) customView.findViewById(R.id.textView_file_type);
                        fileLastModifiedTime = (TextView) customView.findViewById(R.id.textView_file_last_edit_date);
                        File file = new File(filePath);
                        fileName.setText(file.getName());
                        fileSize.setText(org.apache.commons.io.FileUtils.byteCountToDisplaySize(
                                org.apache.commons.io.FileUtils.sizeOf(file)));
                        fileType.setText(setUpFileTypeDescription(customView.getContext(), file));
                        fileLastModifiedTime.setText(DateTimeUtil.formatDate(new Date(file.lastModified())));
                    }
                })
                .setEnableNegativeButton(false));
        super.onCreate(savedInstanceState);
    }

    private String setUpFileTypeDescription(Context context, File file) {
        if (file.isDirectory()) {
            return getString(R.string.folder);
        }
        String fileExt = FilenameUtils.getExtension(file.getName());
        if (MimeTypeUtils.getDocumentExtension().contains(fileExt)) {
            return fileExt.toUpperCase(Locale.getDefault()) + " " + getString(R.string.file);
        }
        String MimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExt);
        if (MimeType != null) {
            if (MimeType.contains(MimeTypeUtils.IMAGE_PREFIX)) {
                return getString(R.string.image) + " " + getString(R.string.file);
            }
            if (MimeType.contains(MimeTypeUtils.AUDIO_PREFIX)) {
                return getString(R.string.audio) + " " + getString(R.string.file);
            }
            if (MimeType.contains(MimeTypeUtils.TEXT_PREFIX)) {
                return getString(R.string.text) + " " + getString(R.string.file);
            }
        }
        return getString(R.string.unknown_type_file);
    }

    public void show(FragmentManager fm) {
        super.show(fm, DialogFileProperty.class.getSimpleName());
    }
}
