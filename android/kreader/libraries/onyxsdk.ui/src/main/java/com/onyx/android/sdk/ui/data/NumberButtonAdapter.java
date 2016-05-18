/**
 * 
 */
package com.onyx.android.sdk.ui.data;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.ui.OnyxGridView;

/**
 * @author dxwts
 *
 */
public class NumberButtonAdapter extends OnyxPagedAdapter
{

    /* (non-Javadoc)
     * @see android.widget.Adapter#getCount()
     */
    
    private String[] mButtonText = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "delete", "0", "OK"};
    
    private Context mContext;
    
    private static final int sHorizontalSpacing = 10;
    private static final int sVerticalSpacing = 10;
    
    public NumberButtonAdapter(OnyxGridView onyxGridView , Context context)
    {
    	super(onyxGridView);
        mContext = context;
        this.getPageLayout().setHorizontalSpacing(sHorizontalSpacing);
        this.getPageLayout().setVerticalSpacing(sVerticalSpacing);
        this.getPageLayout().setItemMinWidth((int)context.getResources().getDimension(R.dimen.dialog_number_item_minWidth));
        this.getPageLayout().setItemMinHeight((int)context.getResources().getDimension(R.dimen.dialog_number_item_minHeight));
    }
    
    @Override
    public int getCount()
    {
        // TODO Auto-generated method stub
        return mButtonText.length;
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position)
    {
        // TODO Auto-generated method stub
        return 0;
    }
    
    public String getItemText(int position)
    {
        return mButtonText[position];
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if(mButtonText[position] == "delete") {
            ImageView img = (ImageView) LayoutInflater.from(mContext).inflate(R.layout.image_item, null);
            img.setMinimumHeight((int)mContext.getResources().getDimension(R.dimen.dialog_number_item_minHeight));
            return img;
        }else
        {
            TextView textView = null;
            textView = (TextView) LayoutInflater.from(mContext).inflate(R.layout.button_item, null);
            textView.setText(mButtonText[position]);
            return textView;
        }
    }

}
