/**
 * 
 */
package com.onyx.android.sdk.ui;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.device.EpdController;
import com.onyx.android.sdk.device.EpdController.UpdateMode;
import com.onyx.android.sdk.ui.dialog.DialogPageSeekBar;

/**
 * @author qingyue
 * 
 */
public class DirectoryGridView extends LinearLayout
{

    private Context mContext;
    private OnyxGridView mGridView;
    private Button mPreviousButton;
    private Button mNextButton;
    private Button mProgressButton;
    private DialogPageSeekBar mDialogPageSeekBar = null;

    public DirectoryGridView(Context context)
    {
        this(context, null);
    }

    public OnyxGridView getGridView()
    {
        return mGridView;
    }

    public DirectoryGridView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        mContext = context;

        View view = LayoutInflater.from(mContext).inflate(R.layout.gridview_directory, null);

        mGridView = (OnyxGridView) view.findViewById(R.id.gridview_content);

        mGridView.registerOnAdapterChangedListener(new OnyxGridView.OnAdapterChangedListener()
        {

            @Override
            public void onAdapterChanged()
            {
                mGridView.getPagedAdapter().registerDataSetObserver(new DataSetObserver()
                {
                    @Override
                    public void onChanged()
                    {
                        DirectoryGridView.this.updatemTextViewProgress();
                    }

                    @Override
                    public void onInvalidated()
                    {
                        DirectoryGridView.this.updatemTextViewProgress();
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
                if (mGridView.getPagedAdapter() != null && mGridView.getPagedAdapter().getPaginator().canPrevPage()) {
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
                if (mGridView.getPagedAdapter() != null && mGridView.getPagedAdapter().getPaginator().canNextPage()) {
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
