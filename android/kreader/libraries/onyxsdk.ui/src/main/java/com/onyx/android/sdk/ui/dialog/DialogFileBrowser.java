/**
 *
 */
package com.onyx.android.sdk.ui.dialog;

import java.io.File;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.device.EnvironmentUtil;
/**
 * @author dxwts
 *
 */
public class DialogFileBrowser extends DialogBaseOnyx
{
    public interface OnOpenFileListener
    {
        void onOpenFile(String path);
    }

    private OnOpenFileListener mOnOpenFileListener = new OnOpenFileListener()
    {

        @Override
        public void onOpenFile(String path)
        {
            // do nothing
        }
    };
    public void setOnOpenFileListener(OnOpenFileListener l)
    {
        mOnOpenFileListener = l;
    }

    private static EditText mPathEditText;
    private Context mContext;

    public static String getRootPath()
    {
        return EnvironmentUtil.getExternalStorageDirectory().getPath();
    }

    public DialogFileBrowser(Context context, String path)
    {
        super(context);
        mContext = context;
        this.setContentView(R.layout.dialog_file_browser);

        mPathEditText = (EditText) findViewById(R.id.edittext_browser);
        if (path != null) {
            setPath(path);
        }

        Button cancelButton = new Button(context);
        cancelButton = (Button) findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                DialogFileBrowser.this.cancel();
            }
        });

        Button browserButton = new Button(context);
        browserButton = (Button) findViewById(R.id.button_browser);
        browserButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                File file = new File(getPath());
                if (file.exists()) {
                    String startPath = null;
                    if (file.isFile()) {
                        startPath = file.getParent();
                    }
                    else {
                        startPath = getPath();
                    }
                    DialogFileBrowserList fileList = new DialogFileBrowserList(mContext, startPath);
                    fileList.show();
                }
                else {
                    Toast.makeText(mContext, R.string.jump_to_root_directory, Toast.LENGTH_LONG).show();

                    DialogFileBrowserList fileList = new DialogFileBrowserList(mContext, getRootPath());
                    fileList.show();
                }
            }
        });

        Button okButton = new Button(context);
        okButton = (Button) findViewById(R.id.button_ok);
        okButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                DialogFileBrowser.this.cancel();
                String path = getPath();
                mOnOpenFileListener.onOpenFile(path);
            }
        });
    }

    public String getPath()
    {
        return getRootPath() + mPathEditText.getText().toString();
    }

    public static void setPath(String path)
    {
        mPathEditText.setText(path.substring(getRootPath().length()));
    }
}
