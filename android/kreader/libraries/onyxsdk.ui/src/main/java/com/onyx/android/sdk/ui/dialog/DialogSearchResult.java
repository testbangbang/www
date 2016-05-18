/**
 * 
 */
package com.onyx.android.sdk.ui.dialog;

import java.util.ArrayList;
import java.util.Collection;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.ui.DirectoryGridView;
import com.onyx.android.sdk.ui.OnyxGridView;
import com.onyx.android.sdk.ui.dialog.data.SearchResultAdapter;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author joy
 *
 */
public class DialogSearchResult extends DialogBaseOnyx
{
    public static class SearchResultItem {
        private String mTitle = null;
        private String mPage = null;
        private Object mTag = null;
        
        public SearchResultItem(String title, String page, Object tag)
        {
            mTitle = title;
            mPage = page;
            mTag = tag;
        }
        
        public String getTitle()
        {
            return mTitle;
        }
        
        public String getPage()
        {
            return mPage;
        }
        
        public Object getTag()
        {
            return mTag;
        }
    }

    public static interface SearchResultItemSelectedListener {
        void onSearchResultItem(SearchResultItem item);
    }
    
    private SearchResultItemSelectedListener mSearchResultItemSelectedListener = null;
    public void setSearchResultItemSelectedListener(SearchResultItemSelectedListener l) 
    {
        mSearchResultItemSelectedListener = l;
    }
    private void notifySearchResultItemSelected(SearchResultItem item)
    {
        if (mSearchResultItemSelectedListener != null) {
            mSearchResultItemSelectedListener.onSearchResultItem(item);
        }
    }
    
    public DialogSearchResult(Context context, Collection<SearchResultItem> items)
    {
        super(context, R.style.full_screen_dialog);
        
        this.setContentView(R.layout.dialog_search_result);
        
        OnyxGridView grid_view = ((DirectoryGridView)this.findViewById(R.id.gridview_search_result)).getGridView();
        grid_view.setOnItemClickListener(new OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                DialogSearchResult.this.dismiss();
                SearchResultItem item = (SearchResultItem)view.getTag();
                DialogSearchResult.this.notifySearchResultItemSelected(item);
            }
        });
        
        ArrayList<SearchResultItem> array_items = new ArrayList<SearchResultItem>();
        array_items.addAll(items);
        SearchResultAdapter adapter = new SearchResultAdapter(grid_view, array_items);
        grid_view.setAdapter(adapter);
    }
}
