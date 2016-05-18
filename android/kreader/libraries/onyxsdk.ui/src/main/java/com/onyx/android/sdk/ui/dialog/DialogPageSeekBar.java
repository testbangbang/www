/**
 * 
 */
package com.onyx.android.sdk.ui.dialog;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.ui.data.OnyxPagedAdapter;
import com.onyx.android.sdk.ui.dialog.DialogGotoPage.AcceptNumberListener;

/**
 * @author qingyue
 *
 */
public class DialogPageSeekBar extends DialogBaseOnyx
{
    @SuppressWarnings("unused")
    private static final String TAG = "DialogPageSeekBar";
    
    private SeekBar mSeekBarPage = null;
    private TextView mTextViewCurrentPage = null;
    private TextView mTextViewAllPage = null;
    private OnyxPagedAdapter mAdapter = null;
    private Context mContext = null;

    public DialogPageSeekBar(Context context, OnyxPagedAdapter adapter)
    {
        super(context, R.style.dialog_seekbar);
        
        mAdapter = adapter;
        mContext = context;

        View view = View.inflate(context, R.layout.dialog_page_seekbar, (ViewGroup)findViewById(R.id.layout_pages_seekbar));

        mSeekBarPage = (SeekBar)view.findViewById(R.id.seekbar_page);
        mTextViewAllPage = (TextView)view.findViewById(R.id.textview_allpage);
        mTextViewCurrentPage = (TextView)view.findViewById(R.id.textview_currentpage);
        
        this.initProgressBar();
        
        mSeekBarPage.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
        {
            private int mPage = mAdapter.getPaginator().getPageIndex() + 1;
            private static final int mMinUnits = 100;
            private static final int mOffset = mMinUnits / 10;
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                mAdapter.getPaginator().setPageIndex(mPage);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
                
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                    boolean fromUser)
            {
                if (((mPage + 1) * mMinUnits) + mOffset < progress || ((mPage + 1) * mMinUnits) - mOffset > progress) {
                    if (fromUser) {
                        int page_index = 0;

                        if (progress < seekBar.getMax()) {
                            int current_progress = (mAdapter.getPaginator().getPageIndex() + 1) * mMinUnits;

                            if ((progress < current_progress) && (progress >= (current_progress - mMinUnits))) {
                                if (progress >= mMinUnits) {
                                    progress -= mMinUnits;
                                }
                            }

                            page_index = progress / mMinUnits;
                        }
                        else {
                            page_index = progress / mMinUnits - 1;
                        }
                        assert (page_index >= 0);
                        mPage = page_index;

                        progress = (mPage + 1) * mMinUnits;
                    }
                    seekBar.setProgress(progress);
                }
            }
        });
        
        mTextViewCurrentPage.setOnClickListener(new View.OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                DialogPageSeekBar.this.onGotoPage();
            }
        });
        mTextViewCurrentPage.setOnKeyListener(new View.OnKeyListener()
        {
            
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_ENTER:
                        DialogPageSeekBar.this.onGotoPage();
                        return true;
                    default:
                        break;
                    }
                }
                return false;
            }
        });

        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();

        params.width = window.getWindowManager().getDefaultDisplay().getWidth();
        params.y = window.getWindowManager().getDefaultDisplay().getHeight();

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                |WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        window.setAttributes(params);

        this.setContentView(view);

        mAdapter.registerDataSetObserver(new DataSetObserver()
        {
            @Override
            public void onChanged()
            {
                DialogPageSeekBar.this.initProgressBar();
            }
        });
        
    }

    private void initProgressBar()
    {
        final int current_page = mAdapter.getPaginator().getPageIndex() + 1;
        final int page_count = (mAdapter.getPaginator().getPageCount() != 0) ? 
                mAdapter.getPaginator().getPageCount() : 1;

        mTextViewAllPage.setText(String.valueOf(page_count));
        mTextViewCurrentPage.setText(String.valueOf(current_page));

        mSeekBarPage.setMax(page_count * 100);
        // due to seekbar's bug, we have to reset seekbar's value to force updating UI
        mSeekBarPage.setProgress(0);
        mSeekBarPage.setProgress(current_page * 100);
    }

    private boolean judgeSkipPage(int page)
    {
        if (page <= Integer.parseInt(mTextViewAllPage.getText().toString()) && page > 0) {
            mAdapter.getPaginator().setPageIndex(page - 1);
            mTextViewCurrentPage.setText(String.valueOf(page));
            mSeekBarPage.setProgress(page * 100);
            return true;
        }
        else {
            Toast.makeText(mContext, R.string.Exceed_the_total_number_of_pages, Toast.LENGTH_LONG).show();
            return false;
        }
    }
    
    private void onGotoPage()
    {
        DialogGotoPage dialogGotoPage = new DialogGotoPage(DialogPageSeekBar.this.getContext());
        dialogGotoPage.setAcceptNumberListener(new AcceptNumberListener()
        {

            @Override
            public void onAcceptNumber(int num)
            {
                if (DialogPageSeekBar.this.judgeSkipPage(num)) {
                    DialogPageSeekBar.this.cancel();
                }
            }
        });
        dialogGotoPage.show();
    }
}