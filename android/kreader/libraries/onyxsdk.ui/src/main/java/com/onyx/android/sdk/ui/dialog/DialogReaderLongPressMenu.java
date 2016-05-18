/**
 * 
 */
package com.onyx.android.sdk.ui.dialog;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.ui.dialog.data.IReaderMenuHandler;

/**
 * @author peekaboo
 *
 */
public class DialogReaderLongPressMenu extends DialogBaseOnyx
{

    private LinearLayout mBookmarkLinearLayout = null;
    private LinearLayout mBackLinearLayout = null;
    private LinearLayout mTOCLinearLayout = null;
    private LinearLayout mMenuLinearLayout = null;
    private ImageButton mCancelImageButton = null;
    private ImageButton mBookmarkImageButton = null;
    private TextView mBookmarkTextView = null;
    private IReaderMenuHandler mHandler = null;
    private LayoutInflater mInflater = null;
    private View mDialogContentView = null;
    private Activity mActivity = null;
    
    public DialogReaderLongPressMenu(Activity activity, IReaderMenuHandler readerMenuHandler)
    {
        super(activity, R.style.dialog_no_board);
        mActivity = activity;
        mInflater = LayoutInflater.from(activity);
        mDialogContentView = mInflater.inflate(R.layout.dialog_reader_long_press_menu, null); 
        
        int menu_width = (int)activity.getResources().getDimension(R.dimen.menu_width);
        int menu_height = (int)activity.getResources().getDimension(R.dimen.menu_height);
        setContentView(mDialogContentView, new LayoutParams(menu_width, menu_height));
        
        mHandler = readerMenuHandler;
        mBookmarkLinearLayout = (LinearLayout)findViewById(R.id.linearlayout_bookmark);
        mBookmarkImageButton = (ImageButton) findViewById(R.id.image_bookmark);
        mBookmarkTextView = (TextView) findViewById(R.id.textview_bookmark);
        mBackLinearLayout = (LinearLayout)findViewById(R.id.linearlayout_back);
        mTOCLinearLayout = (LinearLayout)findViewById(R.id.linearlayout_toc);
        mMenuLinearLayout = (LinearLayout)findViewById(R.id.linearlayout_menu);
        mCancelImageButton = (ImageButton) findViewById(R.id.imageview_cancel);
        mCancelImageButton.requestFocus();
        
        changeBookmarkStatus(readerMenuHandler);
        
        mCancelImageButton.setOnClickListener(new View.OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                DialogReaderLongPressMenu.this.dismiss();
            }
        });
        
        mBookmarkLinearLayout.setOnClickListener(new View.OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                mHandler.addOrDeleteBookmark();
                DialogReaderLongPressMenu.this.dismiss();
            }
        });
        
        mBackLinearLayout.setOnClickListener(new View.OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                mActivity.finish();
                DialogReaderLongPressMenu.this.dismiss();
            }
        });
        
        mTOCLinearLayout.setOnClickListener(new View.OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                mHandler.showTOC();
                DialogReaderLongPressMenu.this.dismiss();
            }
        });
        
        mMenuLinearLayout.setOnClickListener(new View.OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                DialogReaderMenu dlg = new DialogReaderMenu(mActivity, mHandler);
                dlg.show();
                DialogReaderLongPressMenu.this.dismiss();
            }
        });
    }

    private void changeBookmarkStatus(IReaderMenuHandler readerMenuHandler) {
        
        if (readerMenuHandler.hasBookmark()) {
            mBookmarkTextView.setText(R.string.menu_delete_bookmark);
            mBookmarkImageButton.setImageResource(R.drawable.menu_reader_bookmark);
        } else {
            mBookmarkTextView.setText(R.string.menu_add_bookmark);
            mBookmarkImageButton.setImageResource(R.drawable.menu_reader_bookmark);
        }
    }
}
