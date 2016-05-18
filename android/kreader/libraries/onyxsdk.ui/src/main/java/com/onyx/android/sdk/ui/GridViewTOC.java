/**
 * 
 */
package com.onyx.android.sdk.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.device.EpdController;
import com.onyx.android.sdk.device.EpdController.UpdateMode;
import com.onyx.android.sdk.ui.data.OnyxPagedAdapter;
import com.onyx.android.sdk.ui.dialog.DialogPageSeekBar;

/**
 * custom control of a OnyxGridView with page navigation buttons
 * 
 * @author joy
 *
 */
public class GridViewTOC extends LinearLayout
{
    private Context mContext = null;
    
    private OnyxGridView mGridView = null;
    private Button mButtonProgress = null;
    private Button mButtonPreviousPage = null;
    private Button mButtonNextPage = null;
    
    private DialogPageSeekBar mDialogPageSeekBar = null;

    public GridViewTOC(Context context)
    {
        this(context, null);
    }

    public GridViewTOC(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        mContext = context;

        View view = LayoutInflater.from(context).inflate(R.layout.gridview_toc, null); 

        mGridView = (OnyxGridView)view.findViewById(R.id.gridview_content);
        mButtonProgress = (Button)view.findViewById(R.id.button_progress);
        mButtonPreviousPage = (Button)view.findViewById(R.id.button_previous_page);
        mButtonNextPage = (Button)view.findViewById(R.id.button_next_page);

        mGridView.registerOnAdapterChangedListener(new OnyxGridView.OnAdapterChangedListener()
        {
            
            @Override
            public void onAdapterChanged()
            {
                final OnyxPagedAdapter adapter = mGridView.getPagedAdapter();

                adapter.registerDataSetObserver(new DataSetObserver()
                {
                    @Override
                    public void onChanged()
                    {
                        GridViewTOC.this.updatemTextViewProgress();
                    }

                    @Override
                    public void onInvalidated()
                    {
                        GridViewTOC.this.updatemTextViewProgress();
                    }
                });
            }
        });

        mButtonProgress.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                mDialogPageSeekBar = new DialogPageSeekBar(GridViewTOC.this.mContext,
                        mGridView.getPagedAdapter());
                mDialogPageSeekBar.show();
            }
        });

        mButtonPreviousPage.setOnClickListener(new View.OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                if (mGridView.getPagedAdapter().getPaginator().canPrevPage()) {
                    mGridView.getPagedAdapter().getPaginator().prevPage();
                }
                mButtonPreviousPage.requestFocus();
            }
        });

        mButtonNextPage.setOnClickListener(new View.OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                if (mGridView.getPagedAdapter().getPaginator().canNextPage()) {
                    mGridView.getPagedAdapter().getPaginator().nextPage();
                }
                mButtonNextPage.requestFocus();
            }
        });

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        view.setLayoutParams(params);
        this.addView(view, params);
    }

    public OnyxGridView getGridView()
    {
        return mGridView;
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);

        if (mDialogPageSeekBar != null && mDialogPageSeekBar.isShowing()) {
            Window windowDialogPageSeekBar = mDialogPageSeekBar.getWindow();
            WindowManager.LayoutParams params = windowDialogPageSeekBar.getAttributes();

            params.width = windowDialogPageSeekBar.getWindowManager().getDefaultDisplay().getWidth();
            params.y = windowDialogPageSeekBar.getWindowManager().getDefaultDisplay().getHeight();

            windowDialogPageSeekBar.setAttributes(params);
        }
    }

    private void updatemTextViewProgress()
    {
        final int current_page = mGridView.getPagedAdapter().getPaginator().getPageIndex() + 1;
        final int page_count = (mGridView.getPagedAdapter().getPaginator().getPageCount() != 0) ? 
                mGridView.getPagedAdapter().getPaginator().getPageCount() : 1;

        mButtonProgress.setText(String.valueOf(current_page) + mContext.getResources().getString(R.string.slash) + String.valueOf(page_count));

        EpdController.invalidate(GridViewTOC.this, UpdateMode.GU);
    }
}
