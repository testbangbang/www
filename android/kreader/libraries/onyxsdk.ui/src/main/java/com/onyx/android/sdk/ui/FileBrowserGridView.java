/**
 * 
 */
package com.onyx.android.sdk.ui;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.device.EpdController;
import com.onyx.android.sdk.device.EpdController.UpdateMode;
import com.onyx.android.sdk.ui.data.FileBrowserListAdapter;
import com.onyx.android.sdk.ui.dialog.DialogPageSeekBar;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * @author dxwts
 * 
 */
public class FileBrowserGridView extends LinearLayout
{

    private Context mContext;
    private OnyxGridView mGridView;
    private Button mPreviousButton;
    private Button mNextButton;
    private Button mProgressButton;
    private DialogPageSeekBar mDialogPageSeekBar = null;

    public FileBrowserGridView(Context context)
    {
        this(context, null);
        // TODO Auto-generated constructor stub
    }

    public OnyxGridView getGridView()
    {
        return mGridView;
    }

    public FileBrowserGridView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        mContext = context;

        View view = LayoutInflater.from(mContext).inflate(R.layout.file_browser_gridview, null);

        mGridView = (OnyxGridView) view.findViewById(R.id.gridview_content);

        mGridView.registerOnAdapterChangedListener(new OnyxGridView.OnAdapterChangedListener()
        {

            @Override
            public void onAdapterChanged()
            {
                final FileBrowserListAdapter adapter = (FileBrowserListAdapter) mGridView.getPagedAdapter();
                adapter.registerDataSetObserver(new DataSetObserver()
                {
                    @Override
                    public void onChanged()
                    {
                        FileBrowserGridView.this.updatemTextViewProgress();
                    }

                    @Override
                    public void onInvalidated()
                    {
                        FileBrowserGridView.this.updatemTextViewProgress();
                    }
                });
            }
        });

        mProgressButton = (Button) view.findViewById(R.id.button_progress);
        mPreviousButton = (Button) view.findViewById(R.id.button_previous_page);
        mNextButton = (Button) view.findViewById(R.id.button_next_page);

        mProgressButton.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                mDialogPageSeekBar = new DialogPageSeekBar(mContext,
                        mGridView.getPagedAdapter());
                mDialogPageSeekBar.show();
            }
        });

        mPreviousButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                if (mGridView.getPagedAdapter().getPaginator().canPrevPage()) {
                    mGridView.getPagedAdapter().getPaginator().prevPage();
                }
                mPreviousButton.requestFocus();
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                if (mGridView.getPagedAdapter().getPaginator().canNextPage()) {
                    mGridView.getPagedAdapter().getPaginator().nextPage();
                }
                mNextButton.requestFocus();
            }
        });

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        view.setLayoutParams(params);
        this.addView(view, params);
    }

    private void updatemTextViewProgress()
    {
        final int current_page = mGridView.getPagedAdapter().getPaginator().getPageIndex() + 1;
        final int page_count = mGridView.getPagedAdapter().getPaginator().getPageCount() != 0 ?
                mGridView.getPagedAdapter().getPaginator().getPageCount() : 1;

        mProgressButton.setText(String.valueOf(current_page)
                + mContext.getResources().getString(R.string.slash) + String.valueOf(page_count));
        EpdController.invalidate(mProgressButton, UpdateMode.GU);
    }

}
