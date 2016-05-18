/**
 * 
 */
package com.onyx.android.sdk.ui.dialog;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.ui.GridViewTOC;
import com.onyx.android.sdk.ui.data.BookmarkItem;
import com.onyx.android.sdk.ui.data.GridViewBookmarkAdapter;

/**
 * @author dxwts
 *
 */
public class DialogBookmarks extends DialogBaseOnyx
{

    public interface onGoToPageListener
    {
        public void onGoToPage(BookmarkItem item);
    }

    private onGoToPageListener mOnGoToPageListener = new onGoToPageListener()
    {

        @Override
        public void onGoToPage(BookmarkItem item)
        {
            //do nothing
        }
    };

    public void setOnGoToPageListener(onGoToPageListener l)
    {
        mOnGoToPageListener = l;
    }

    public interface onDeleteBookmarkListener
    {
        public void DeleteBookmark(BookmarkItem item);
    }

    private onDeleteBookmarkListener mOnDeleteBookmarkListener = new onDeleteBookmarkListener()
    {

        @Override
        public void DeleteBookmark(BookmarkItem item)
        {
            // TODO Auto-generated method stub

        }
    };
    public void setOnDeleteBookmarkListener(onDeleteBookmarkListener l)
    {
        mOnDeleteBookmarkListener = l;
    }

    private ArrayList<BookmarkItem> mBookmarks = null;

    public DialogBookmarks(Context context, ArrayList<BookmarkItem> bookmarks)
    {
        super(context, R.style.full_screen_dialog);
        setContentView(R.layout.dialog_bookmarks);

        mBookmarks = bookmarks;

        Button button_exit = (Button)findViewById(R.id.button_exit);
        button_exit.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                DialogBookmarks.this.dismiss();

            }
        });

        GridViewTOC bookmarksGridView = (GridViewTOC)findViewById(R.id.gridview_bookmarks);
        bookmarksGridView.getGridView().setOnItemClickListener(new OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                DialogBookmarks.this.dismiss();
                BookmarkItem item = (BookmarkItem) view.getTag();
                mOnGoToPageListener.onGoToPage(item);
            }
        });

        bookmarksGridView.getGridView().setOnItemLongClickListener(new OnItemLongClickListener()
        {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                BookmarkItem item = (BookmarkItem) view.getTag();
                mOnDeleteBookmarkListener.DeleteBookmark(item);
                return false;
            }
        });

        if(mBookmarks == null) {
            ArrayList<BookmarkItem> BookmarkItem = new ArrayList<BookmarkItem>();
            for (int i = 0; i < 100; i++) {
                BookmarkItem item = new BookmarkItem("title"+i, "tag"+i);
                BookmarkItem.add(item);
            }
            mBookmarks = BookmarkItem;
        }

        GridViewBookmarkAdapter adapter = new GridViewBookmarkAdapter(context, bookmarksGridView.getGridView(), mBookmarks);
        bookmarksGridView.getGridView().setAdapter(adapter);

    }
}
