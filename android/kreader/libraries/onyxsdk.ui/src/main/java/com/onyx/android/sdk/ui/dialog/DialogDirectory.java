/**
 *
 */
package com.onyx.android.sdk.ui.dialog;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.device.DeviceInfo;
import com.onyx.android.sdk.device.IDeviceFactory.TouchType;
import com.onyx.android.sdk.ui.DirectoryGridView;
import com.onyx.android.sdk.ui.OnyxGridView;
import com.onyx.android.sdk.ui.data.DirectoryItem;
import com.onyx.android.sdk.ui.data.GridViewAnnotationAdapter;
import com.onyx.android.sdk.ui.data.GridViewDirectoryAdapter;
import com.onyx.android.sdk.ui.dialog.data.AnnotationItem;

import java.util.ArrayList;

/**
 * @author qingyue
 */
public class DialogDirectory extends DialogBaseOnyx
{
    public static enum DirectoryTab {toc, bookmark, annotation};
    private BookmarksPopupWindow mPopupWindow = null;
    private boolean enableLongPress = true;
    boolean isTOCFirstLoad = true;
    boolean isBookMarkFirstLoad=true;
    boolean isAnnotationFirstLoad = true;
    private int mCurrentPage;

    public static interface IGotoPageHandler {
        public void jumpTOC(DirectoryItem item);

        public void jumpBookmark(DirectoryItem item);

        public void jumpAnnotation(DirectoryItem item);
    }

    public static interface IEditPageHandler {
        public void deleteBookmark(DirectoryItem item);

        public void deleteAnnotation(DirectoryItem item);

        public void editAnnotation(DirectoryItem item);
    }

    private IGotoPageHandler mGotoPageHandler = null;
    private IEditPageHandler mEditPageHandler = null;
    private TextView mTextViewTitle = null;
    private Context mContext = null;
    DirectoryGridView gridViewTOC, gridViewBookmark, gridViewAnnotation;

    public DialogDirectory(Context context, ArrayList<DirectoryItem> tocItems,
                           ArrayList<DirectoryItem> bookmarkItems, ArrayList<AnnotationItem> annotationItems,
                           final IGotoPageHandler gotoPageHandler,final IEditPageHandler editPageHandler, DirectoryTab tab){
        this(context, tocItems, bookmarkItems, annotationItems, gotoPageHandler, editPageHandler, tab, -1);
    }

    public DialogDirectory(Context context, ArrayList<DirectoryItem> tocItems,
            ArrayList<DirectoryItem> bookmarkItems, ArrayList<AnnotationItem> annotationItems,
            final IGotoPageHandler gotoPageHandler,final IEditPageHandler editPageHandler, DirectoryTab tab , int currentPage)
    {
        super(context, R.style.full_screen_dialog);
        setContentView(R.layout.dialog_directory);
        mContext = context;
        mGotoPageHandler = gotoPageHandler;
        mEditPageHandler = editPageHandler;
        mCurrentPage = currentPage;
        TabHost tab_host = (TabHost) findViewById(R.id.tabhost);
        tab_host.setup();

        TextView toc = (TextView) LayoutInflater.from(context).inflate(R.layout.onyx_tabwidget, null);
        toc.setText(R.string.tabwidget_toc);
        TextView bookmark = (TextView) LayoutInflater.from(context).inflate(R.layout.onyx_tabwidget, null);
        bookmark.setText(R.string.tabwidget_bookmark);
        TextView annotation = (TextView) LayoutInflater.from(context).inflate(R.layout.onyx_tabwidget, null);
        annotation.setText(R.string.tabwidget_annotation);

        Resources resources = context.getResources();

        tab_host.addTab(tab_host.newTabSpec(resources.getString(R.string.tabwidget_toc)).setIndicator(toc).setContent(R.id.layout_toc));
        tab_host.addTab(tab_host.newTabSpec(resources.getString(R.string.tabwidget_bookmark)).setIndicator(bookmark).setContent(R.id.layout_bookmark));

        if (DeviceInfo.currentDevice.getTouchType(context) != TouchType.None) {
        	tab_host.addTab(tab_host.newTabSpec(resources.getString(R.string.tabwidget_annotation)).setIndicator(annotation).setContent(R.id.layout_annotation));
		} else {
        	View v = this.findViewById(R.id.layout_annotation);
        	v.setVisibility(View.GONE);
        }

        tab_host.setOnTabChangedListener(new OnTabChangeListener()
        {

            @Override
            public void onTabChanged(String tabId)
            {
                mTextViewTitle.setText(tabId);
            }
        });

        mTextViewTitle = (TextView) findViewById(R.id.textview_title);

        gridViewTOC = (DirectoryGridView) findViewById(R.id.gridview_toc);
        gridViewBookmark = (DirectoryGridView) findViewById(R.id.gridview_bookmark);
        gridViewAnnotation = (DirectoryGridView) findViewById(R.id.gridview_annotation);

        if (tocItems != null) {
            final int tocColumnTargetIndex = findIndexInTocItemListByCurrentPage(tocItems, 0, (tocItems.size() - 1));
            final GridViewDirectoryAdapter tocAdapter = new GridViewDirectoryAdapter(context, gridViewTOC.getGridView(), tocItems, tocColumnTargetIndex);
            gridViewTOC.getGridView().setAdapter(tocAdapter);
            gridViewTOC.getGridView().registerOnSizeChangedListener(new OnyxGridView.OnSizeChangedListener() {
                @Override
                public void onSizeChanged() {
                    if (gridViewTOC.getGridView().getPagedAdapter().getPaginator().getPageSize() != 0) {
                        if (isTOCFirstLoad) {
                            gridViewTOC.getGridView().getPagedAdapter().locatePageByItemIndex(tocColumnTargetIndex);
                            isTOCFirstLoad = false;
                        }
                    }
                }
            });
        }
        if (bookmarkItems != null) {
            final int bookmarkColumnTargetIndex = findIndexInTocItemListByCurrentPage(tocItems, 0, (bookmarkItems.size() - 1));
            GridViewDirectoryAdapter bookmarkAdapter = new GridViewDirectoryAdapter(context, gridViewBookmark.getGridView(), bookmarkItems, bookmarkColumnTargetIndex);
            gridViewBookmark.getGridView().setAdapter(bookmarkAdapter);
            gridViewBookmark.getGridView().registerOnSizeChangedListener(new OnyxGridView.OnSizeChangedListener() {
                @Override
                public void onSizeChanged() {
                    if (gridViewBookmark.getGridView().getPagedAdapter().getPaginator().getPageSize() != 0) {
                        if (isBookMarkFirstLoad) {
                            gridViewBookmark.getGridView().getPagedAdapter().locatePageByItemIndex(bookmarkColumnTargetIndex);
                            isBookMarkFirstLoad = false;
                        }
                    }
                }
            });
        }
        if (annotationItems != null) {
            final int annotationColumnTargetIndex = findIndexInTocItemListByCurrentPage(tocItems, 0, (annotationItems.size() - 1));
            GridViewAnnotationAdapter annotationAdapter = new GridViewAnnotationAdapter(context, gridViewAnnotation.getGridView(), annotationItems ,annotationColumnTargetIndex);
            gridViewAnnotation.getGridView().setAdapter(annotationAdapter);
            gridViewAnnotation.getGridView().registerOnSizeChangedListener(new OnyxGridView.OnSizeChangedListener() {
                @Override
                public void onSizeChanged() {
                    if (gridViewAnnotation.getGridView().getPagedAdapter().getPaginator().getPageSize() != 0) {
                        if (isAnnotationFirstLoad) {
                            gridViewAnnotation.getGridView().getPagedAdapter().locatePageByItemIndex(annotationColumnTargetIndex);
                            isAnnotationFirstLoad = false;
                        }
                    }
                }
            });
        }

        gridViewTOC.getGridView().setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DialogDirectory.this.dismiss();
                DirectoryItem item = (DirectoryItem) view.getTag();
                mGotoPageHandler.jumpTOC(item);
            }
        });

        gridViewBookmark.getGridView().setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DirectoryItem item = (DirectoryItem) view.getTag();
                mGotoPageHandler.jumpBookmark(item);
                DialogDirectory.this.dismiss();
            }
        });

        gridViewBookmark.getGridView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (!enableLongPress) {
                    return false;
                }
                DirectoryItem item = (DirectoryItem) view.getTag();
                View mContentView =LayoutInflater.from(mContext).inflate(R.layout.bookmarkpopupmenu,null);
                if (mPopupWindow == null) {
                    mPopupWindow = new BookmarksPopupWindow(mContext, mContentView, DialogDirectory.this,
                            mGotoPageHandler, mEditPageHandler, BookmarksPopupWindow.TOC_MODE.BOOKMARK_MODE);
                    mPopupWindow.setOutsideTouchable(true);
                    mPopupWindow.setFocusable(true);
                    mPopupWindow.setTouchable(true);
                }
                mPopupWindow.switchMode(BookmarksPopupWindow.TOC_MODE.BOOKMARK_MODE);
                mPopupWindow.setDirectoryItem(item, gridViewBookmark, position);
                ViewGroup.LayoutParams params = view.getLayoutParams();
                int width = params.width - mPopupWindow.getWidth();
                mPopupWindow.setTouchInterceptor(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                            mPopupWindow.dismiss();
                            return true;
                        }
                        return false;
                    }
                });
                mPopupWindow.showAsDropDown(view, width, 0);
                return true;
            }
        });

        gridViewAnnotation.getGridView().setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DirectoryItem item = (DirectoryItem) view.getTag();
                mGotoPageHandler.jumpAnnotation(item);
                DialogDirectory.this.dismiss();
            }
        });

        gridViewAnnotation.getGridView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (!enableLongPress) {
                    return false;
                }
                DirectoryItem item = (DirectoryItem) view.getTag();
                View mContentView =LayoutInflater.from(mContext).inflate(R.layout.bookmarkpopupmenu,null);
                if (mPopupWindow == null) {
                    mPopupWindow = new BookmarksPopupWindow(mContext, mContentView, DialogDirectory.this,
                            mGotoPageHandler, mEditPageHandler, BookmarksPopupWindow.TOC_MODE.ANNOTATION_MODE);
                    mPopupWindow.setOutsideTouchable(true);
                    mPopupWindow.setFocusable(true);
                    mPopupWindow.setTouchable(true);
                }
                mPopupWindow.switchMode(BookmarksPopupWindow.TOC_MODE.ANNOTATION_MODE);
                mPopupWindow.setDirectoryItem(item, gridViewAnnotation, position);
                ViewGroup.LayoutParams params = view.getLayoutParams();
                int width = params.width - mPopupWindow.getWidth();
                mPopupWindow.setTouchInterceptor(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                            mPopupWindow.dismiss();
                            return true;
                        }
                        return false;
                    }
                });
                mPopupWindow.showAsDropDown(view, width, 0);
                return true;
            }
        });

        Button button_exit = (Button) findViewById(R.id.button_exit);
        button_exit.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                DialogDirectory.this.dismiss();
            }
        });

        switch (tab) {
        case toc:
            tab_host.setCurrentTab(0);
            mTextViewTitle.setText(R.string.tabwidget_toc);
            break;
        case bookmark:
            tab_host.setCurrentTab(1);
            mTextViewTitle.setText(R.string.tabwidget_bookmark);
            break;
        case annotation:
            tab_host.setCurrentTab(2);
            mTextViewTitle.setText(R.string.tabwidget_annotation);
            break;
        default:
            tab_host.setCurrentTab(0);
            mTextViewTitle.setText(R.string.tabwidget_toc);
            break;
        }
    }

    private int findIndexInTocItemListByCurrentPage(ArrayList<DirectoryItem> tocList, int startIndex, int endIndex) {
        if (tocList == null || tocList.size() <= 0) {
            return -1;
        }
        int targetIndex = (startIndex + endIndex) / 2;
        int targetPage = Integer.parseInt(tocList.get(targetIndex).getPage());
        if (mCurrentPage <= targetPage &&
                ((startIndex != targetIndex) && (endIndex != targetIndex))) {
            if (mCurrentPage != targetPage) {
                return findIndexInTocItemListByCurrentPage(tocList, startIndex, targetIndex);
            }
            return targetIndex;
        } else if ((startIndex != targetIndex) && (endIndex != targetIndex)) {
            return findIndexInTocItemListByCurrentPage(tocList, targetIndex, endIndex);
        } else {
            return targetIndex;
        }
    }

}
