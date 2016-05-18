/**
 * 
 */
package com.onyx.android.sdk.ui.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.reader.PagingMode;
import com.onyx.android.sdk.ui.data.SelectionAdapter;
import com.onyx.android.sdk.ui.dialog.data.IReaderMenuHandler;

import android.content.Context;
import android.util.Pair;
import android.view.View;

/**
 * @author joy
 *
 */
public class DialogReaderReadingMode extends DialogBaseSettings
{
    private final static HashMap<PagingMode, Integer> MODE_STRING_MAP = new HashMap<PagingMode, Integer>(); 
    
    static {
        MODE_STRING_MAP.put(PagingMode.Hard_Pages, R.string.reading_mode_single_page);
        MODE_STRING_MAP.put(PagingMode.Scroll_Pages, R.string.reading_mode_scroll);
        MODE_STRING_MAP.put(PagingMode.Hard_Pages_2Up, R.string.reading_mode_two_columns);
    }
    
    private IReaderMenuHandler mMenuHandler = null;

    private final ArrayList<Pair<String, Object>> mItems = new ArrayList<Pair<String, Object>>();
    private SelectionAdapter mAdapter = null;

    public DialogReaderReadingMode(Context context, List<PagingMode> pagingModes,
            PagingMode currentMode, IReaderMenuHandler menuHandler)
    {
        super(context);
        
        mMenuHandler = menuHandler;
        
        for (PagingMode m : pagingModes) {
            if (MODE_STRING_MAP.containsKey(m)) {
                String text = context.getString(MODE_STRING_MAP.get(m));
                mItems.add(new Pair<String, Object>(text, m));
            }
        }
        
        int initial_selection = 0;
        for (int i = 0; i < mItems.size(); i++) {
            if (mItems.get(i).second == currentMode) {
                initial_selection = i;
                break;
            }
        }
        mAdapter = new SelectionAdapter(context, this.getGridView(), mItems,
                initial_selection);
        this.getGridView().setAdapter(mAdapter);
        
        this.getButtonSet().setOnClickListener(new View.OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                int sel = mAdapter.getSelection();
                if (sel >= 0) {
                    mMenuHandler.setReadingMode((PagingMode)mItems.get(sel).second);
                }
                
                DialogReaderReadingMode.this.dismiss();
            }
        });
        
        this.getButtonCancel().setOnClickListener(new View.OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                DialogReaderReadingMode.this.cancel();
            }
        });
        
        this.getTextViewTitle().setText(R.string.menu_item_reading_mode);

        mAdapter.getPaginator().setPageSize(mItems.size());
        
    }

}
