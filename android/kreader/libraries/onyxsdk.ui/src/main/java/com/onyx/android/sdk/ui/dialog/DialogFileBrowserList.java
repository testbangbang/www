/**
 *
 */
package com.onyx.android.sdk.ui.dialog;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.device.EpdController;
import com.onyx.android.sdk.device.EpdController.UpdateMode;
import com.onyx.android.sdk.ui.FileBrowserGridView;
import com.onyx.android.sdk.ui.OnyxGridView;
import com.onyx.android.sdk.ui.data.FileBrowserListAdapter;
import com.onyx.android.sdk.ui.data.GridViewPageLayout.GridViewMode;
import com.onyx.android.sdk.ui.data.GridViewPaginator.OnPageIndexChangedListener;
import com.onyx.android.sdk.ui.data.GridViewPaginator.OnStateChangedListener;

/**
 * @author dxwts
 *
 */
public class DialogFileBrowserList extends DialogBaseOnyx
{

    private ImageView mDirBack;
    private FileBrowserListAdapter mAdapter;
    private FileBrowserGridView mFileListView;
    private TextView mPathText;
    private Context mContext;

    private OnyxGridView getGridView()
    {
        return mFileListView.getGridView();
    }

    public DialogFileBrowserList(Context context, String path)
    {
        super(context);
        this.setContentView(R.layout.dialog_file_browser_list);

        mContext = context;

        mPathText = (TextView) findViewById(R.id.textview_path);

        mDirBack = (ImageView) findViewById(R.id.dialog_dir_back);
        mDirBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!mAdapter.getHostPath().equals(DialogFileBrowser.getRootPath())) {
                    DialogFileBrowserList.this.browseFile(mAdapter.getParentPath());
                }
            }
        });

        mFileListView = (FileBrowserGridView) findViewById(R.id.dialog_file_gridview);

        mAdapter = new FileBrowserListAdapter(mContext, this.getGridView());
        mAdapter.getPageLayout().setViewMode(GridViewMode.Detail);

        mFileListView.getGridView().registerOnAdapterChangedListener(new OnyxGridView.OnAdapterChangedListener()
        {
            @Override
            public void onAdapterChanged()
            {

                mAdapter.getPaginator().registerOnPageIndexChangedListener(new OnPageIndexChangedListener()
                {

                    @Override
                    public void onPageIndexChanged(int oldIndex, int newIndex)
                    {
                        EpdController.invalidate(mFileListView.getGridView(), UpdateMode.GU);
                    }
                });
                mAdapter.getPaginator().registerOnStateChangedListener(new OnStateChangedListener()
                {

                    @Override
                    public void onStateChanged()
                    {
                        EpdController.invalidate(mFileListView.getGridView(), UpdateMode.GU);
                    }
                });
            }
        });

        mFileListView.getGridView().setOnItemClickListener(new OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                File file = (File) view.getTag();
                browseFile(file.getPath());
            }
        });

        this.getGridView().setAdapter(mAdapter);
        browseFile(path);
    }

    private boolean browseFile(String path)
    {
        File f = new File(path);
        if (!f.exists()) {
            if (EnvironmentUtil.isFileOnRemovableSDCard(f)
                    && !Environment.getExternalStorageState().equals(
                            Environment.MEDIA_MOUNTED)) {
                Toast.makeText(mContext, R.string.SD_card_has_been_removed,
                        Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(mContext, R.string.file_not_exist
                        , Toast.LENGTH_LONG).show();
            }
            return false;
        }
        if (f.isDirectory()) {
            mPathText.setText((path + "/").substring(DialogFileBrowser.getRootPath().length()));

            File[] files = f.listFiles();
            mAdapter.fillItems(files, path);
            return true;
        }

        if (f.isFile()) {
            DialogFileBrowser.setPath(f.getPath());
            this.dismiss();
        }
        return false;
    }
}
