/**
 * 
 */
package com.onyx.android.sdk.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.ui.data.SelectionAdapter;

/**
 * 
 * @author qingyue
 *
 */

@Deprecated
public class DialogFontFaceSettings extends DialogBaseSettings
{
    public interface onSettingsFontFaceListener
    {
        public void settingfontFace(int location);
    }
    private onSettingsFontFaceListener mOnSettingsFontFaceLinstener = new onSettingsFontFaceListener()
    {
        
        @Override
        public void settingfontFace(int location)
        {
            //do nothing
        }
    };
    public void setOnSettingsFontFaceListener(onSettingsFontFaceListener l)
    {
        mOnSettingsFontFaceLinstener = l;
    }

    private Dialog mReaderMenu = null;
    private int mReaderMenuY = -1;
    private SelectionAdapter mAdapter = null;
    private int mCurrentFontFaceIndex = -1;

    public DialogFontFaceSettings(Context context, String[] fontFoces, String currentFontFace, Dialog readerMenu)
    {
        super(context);

        mReaderMenu = readerMenu;
        
        mAdapter = new SelectionAdapter(context, this.getGridView(), fontFoces);
        this.getGridView().setAdapter(mAdapter);

        for (int i = 0; i < fontFoces.length; i++) {
            if (currentFontFace.equals(fontFoces[i])) {
                mAdapter.setSelection(i);
                mCurrentFontFaceIndex = i;
                break;
            }
        }

        this.getGridView().setOnItemClickListener(new OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                
                if (mAdapter.getSelection() != position) {
                    int selectedItem = mAdapter.getPaginator().getPageSize() * mAdapter.getPaginator().getPageIndex() + position;
                    mAdapter.setSelection(selectedItem);
                    mAdapter.notifyDataSetChanged();
                    mOnSettingsFontFaceLinstener.settingfontFace(selectedItem);
                }
            }
        });

        this.getButtonSet().setOnClickListener(new View.OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                DialogFontFaceSettings.this.dismiss();
            }
        });

        this.getButtonCancel().setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                mOnSettingsFontFaceLinstener.settingfontFace(mCurrentFontFaceIndex);
                DialogFontFaceSettings.this.dismiss();
            }
        });

        this.getTextViewTitle().setText(R.string.font_face);

        mAdapter.getPaginator().setPageSize(fontFoces.length);

        int[] location = new int[2];
		if (mReaderMenu != null) {
			mReaderMenu.getWindow().getDecorView().getLocationOnScreen(location);
		}
        mReaderMenuY = location[1];
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        int loc[] = new int[2];
        DialogFontFaceSettings.this.getWindow().getDecorView().getLocationOnScreen(loc);
        int y = loc[1];
        if (event.getY() + y < mReaderMenuY) {
			if (mReaderMenu != null) {
				mReaderMenu.dismiss();
			}
        }

        return super.onTouchEvent(event);
    }
}
