/**
 * 
 */
package com.onyx.android.sdk.ui.dialog;

import android.content.Context;
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
import com.onyx.android.sdk.ui.dialog.DialogGotoPage.AcceptNumberListener;

/**
 * @author peekaboo
 *
 */
public class DialogPageSeekBarNew extends DialogBaseOnyx
{
    @SuppressWarnings("unused")
    private static final String TAG = "DialogPagesSeekBar";
    
    public static interface OnPageChangedListener
    {
    	void onPageChanged(int newPage, int oldPage);
    }
    
    private OnPageChangedListener mOnPageChangedListener = null;
    private void notifyPageChanged(int newPage, int oldPage)
    {
    	if (mOnPageChangedListener != null) {
    		mOnPageChangedListener.onPageChanged(newPage, oldPage);
    	}
    }
    public void setOnPageChangedListener(OnPageChangedListener l)
    {
    	mOnPageChangedListener = l;
    }
    
    private SeekBar mSeekBarPage = null;
    private TextView mTextViewCurrentPage = null;
    private TextView mTextViewAllPage = null;
    private Context mContext = null;
    private int mTotal = 0;
    private int mCurrentOldPage = 0;
    private int mCurrentPage = 0;

    public DialogPageSeekBarNew(Context context, int total, int current)
    {
        super(context, R.style.dialog_seekbar);
        
        mContext = context;
        this.mTotal = total;
        this.mCurrentPage = current;
        this.mCurrentOldPage = current;

        View view = View.inflate(context, R.layout.dialog_page_seekbar, (ViewGroup)findViewById(R.id.layout_pages_seekbar));

        mSeekBarPage = (SeekBar)view.findViewById(R.id.seekbar_page);
        mTextViewAllPage = (TextView)view.findViewById(R.id.textview_allpage);
        mTextViewCurrentPage = (TextView)view.findViewById(R.id.textview_currentpage);
        
        this.initProgressBar();
        
        mSeekBarPage.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
        {
            private int mPage = mCurrentPage;
            private static final int mMinUnits = 100;
            private static final int mOffset = mMinUnits / 10;
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
            	// Seekbar progress starts from 0
            	mCurrentPage = mPage + 1;
            	DialogPageSeekBarNew.this.initProgressBar();
            	notifyPageChanged(mCurrentPage, mCurrentOldPage);
            	mCurrentOldPage = mCurrentPage;
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
                            int current_progress = mCurrentPage * mMinUnits;

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
                DialogPageSeekBarNew.this.onGotoPage();
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
                        DialogPageSeekBarNew.this.onGotoPage();
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

    }

    private void initProgressBar()
    {
        final int current_page = mCurrentPage;
        final int page_count = (mTotal != 0) ? mTotal : 1;

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
        	mCurrentPage = page;
            DialogPageSeekBarNew.this.initProgressBar();
            mTextViewCurrentPage.setText(String.valueOf(page));
            mSeekBarPage.setProgress(page * 100);
            notifyPageChanged(mCurrentPage, mCurrentOldPage);
            return true;
        }
        else {
            Toast.makeText(mContext, R.string.Exceed_the_total_number_of_pages, Toast.LENGTH_LONG).show();
            return false;
        }
    }
    
    private void onGotoPage()
    {
        DialogGotoPage dialogGotoPage = new DialogGotoPage(DialogPageSeekBarNew.this.getContext());
        dialogGotoPage.setAcceptNumberListener(new AcceptNumberListener()
        {

            @Override
            public void onAcceptNumber(int num)
            {
                if (DialogPageSeekBarNew.this.judgeSkipPage(num)) {
                    DialogPageSeekBarNew.this.cancel();
                }
            }
        });
        dialogGotoPage.show();
    }
}