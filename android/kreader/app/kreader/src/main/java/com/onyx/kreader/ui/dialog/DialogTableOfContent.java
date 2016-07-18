package com.onyx.kreader.ui.dialog;

import android.app.Dialog;
import android.content.res.Resources;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.ui.view.TreeRecyclerView;
import com.onyx.android.sdk.utils.DateTimeUtil;
import com.onyx.android.sdk.utils.DimenUtils;
import com.onyx.kreader.R;
import com.onyx.kreader.api.ReaderDocumentTableOfContent;
import com.onyx.kreader.api.ReaderDocumentTableOfContentEntry;
import com.onyx.kreader.dataprovider.Annotation;
import com.onyx.kreader.dataprovider.Bookmark;
import com.onyx.kreader.host.request.DeleteAnnotationRequest;
import com.onyx.kreader.host.request.DeleteBookmarkRequest;
import com.onyx.kreader.ui.ReaderActivity;
import com.onyx.kreader.ui.actions.GotoPageAction;
import com.onyx.kreader.ui.actions.ShowAnnotationEditDialogAction;
import com.onyx.kreader.utils.PagePositionUtils;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by joy on 7/6/16.
 */
public class DialogTableOfContent extends Dialog implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, PageRecyclerView.OnPagingListener {

    private static final String TAG = DialogTableOfContent.class.getSimpleName();

    private ReaderActivity mActivity;

    private ImageView mPreIcon;
    private ImageView mNextIcon;
    private ImageView mBackIcon;
    private TextView mPageIndicator;
    private TextView mBackText;
    private RadioButton mBtnToc;
    private RadioButton mBtnBookmark;
    private RadioButton mBtnAnt;
    private ViewPager mViewPager;

    private int mCurrentPosition = 0;
    private List<PageRecyclerView> mViewList = new ArrayList<>();
    private Map<Integer,Integer> mRecordPosition = new Hashtable<>();
    List<Bookmark> mBookmarkList;
    List<Annotation> mAnnotationList;

    public enum DirectoryTab { TOC , Bookmark, Annotation }

    private class SimpleListViewItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ReaderActivity mActivity;

        private TextView textViewDescription;
        private TextView textViewTitle;
        private TextView textViewPage;
        private TextView textViewTime;
        private TextView textViewDelete;
        private ImageView imageViewDelete;
        private TextView textViewEdit;
        private ImageView imageViewEdit;
        private View splitLine;
        private String page;
        private int position;

        public SimpleListViewItemViewHolder(final ReaderActivity readerActivity, final View itemView) {
            super(itemView);

            mActivity = readerActivity;
            textViewTitle = (TextView)itemView.findViewById(R.id.text_view_title);
            textViewDescription = (TextView)itemView.findViewById(R.id.text_view_description);
            textViewPage = (TextView)itemView.findViewById(R.id.text_view_page);
            textViewTime = (TextView)itemView.findViewById(R.id.text_view_time);
            textViewDelete = (TextView)itemView.findViewById(R.id.text_view_delete);
            imageViewDelete = (ImageView) itemView.findViewById(R.id.image_view_delete);
            textViewEdit = (TextView)itemView.findViewById(R.id.text_view_edit);
            imageViewEdit = (ImageView) itemView.findViewById(R.id.image_view_edit);
            splitLine = itemView.findViewById(R.id.split_line);

            imageViewDelete.setOnClickListener(this);
            textViewDelete.setOnClickListener(this);
            textViewEdit.setOnClickListener(this);
            imageViewEdit.setOnClickListener(this);
            textViewTitle.setOnClickListener(this);
        }

        public void bindView(String title,String description,String page,long time,int position,DirectoryTab tab){
            textViewTitle.setText(title);
            textViewDescription.setText(description);
            Date date = new Date(time);
            textViewTime.setText(DateTimeUtil.formatDate(date,DateTimeUtil.DATE_FORMAT_YYYYMMDD_HHMM));
            String format = String.format(mActivity.getString(R.string.page),page);
            textViewPage.setText(format);
            this.page = page;
            this.position = position;

            boolean showPageLastLine = (position + 1) % getPageSize(tab) != 0;
            this.splitLine.setVisibility(showPageLastLine ? View.VISIBLE :View.INVISIBLE);
            textViewEdit.setVisibility(mCurrentPosition == getTabIndex(DirectoryTab.Annotation) ? View.VISIBLE : View.GONE);
            imageViewEdit.setVisibility(mCurrentPosition == getTabIndex(DirectoryTab.Annotation) ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View v) {
            if (v.equals(textViewTitle)){
                DialogTableOfContent.this.hide();
                new GotoPageAction(page).execute(mActivity);
            }else if (v.equals(textViewDelete) || v.equals(imageViewDelete)){
                if (mCurrentPosition == getTabIndex(DirectoryTab.Bookmark)){
                    deleteBookmark(mActivity,position);
                }else {
                    deleteAnnotation(mActivity,position);
                }
            }else if (v.equals(imageViewEdit) || v.equals(textViewEdit)){
                showAnnotationEditDialog(position);
            }
        }
    }

    private void showAnnotationEditDialog(final int position){
        ShowAnnotationEditDialogAction action = new ShowAnnotationEditDialogAction(mAnnotationList.get(position));
        action.setOnEditListener(new ShowAnnotationEditDialogAction.OnEditListener() {
            @Override
            public void onUpdateFinished(Annotation annotation) {
                mAnnotationList.set(position,annotation);
                mViewList.get(mCurrentPosition).getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onDeleteFinished() {
                mAnnotationList.remove(position);
                mViewList.get(mCurrentPosition).getAdapter().notifyDataSetChanged();
                updatePageIndicator(position,getPageSize(DirectoryTab.Annotation),mViewList.get(mCurrentPosition).getAdapter().getItemCount());
            }
        });
        action.execute(mActivity);
    }

    private void deleteBookmark(ReaderActivity readerActivity, final int position){
        final DeleteBookmarkRequest DbRequest = new DeleteBookmarkRequest(mBookmarkList.get(position));
        readerActivity.getReader().submitRequest(readerActivity,DbRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                mBookmarkList.remove(position);
                mViewList.get(mCurrentPosition).getAdapter().notifyDataSetChanged();
                updatePageIndicator(position,getPageSize(DirectoryTab.Bookmark),mViewList.get(mCurrentPosition).getAdapter().getItemCount());
            }
        });
    }

    private void deleteAnnotation(ReaderActivity readerActivity, final int position){
        final DeleteAnnotationRequest DaRequest = new DeleteAnnotationRequest(mAnnotationList.get(position));
        readerActivity.getReader().submitRequest(readerActivity,DaRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                mAnnotationList.remove(position);
                mViewList.get(mCurrentPosition).getAdapter().notifyDataSetChanged();
                updatePageIndicator(position,getPageSize(DirectoryTab.Annotation),mViewList.get(mCurrentPosition).getAdapter().getItemCount());
            }
        });
    }

    public class ViewPagerAdapter extends PagerAdapter{
        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PageRecyclerView view = mViewList.get(position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViewList.get(position));
        }
    }

    public DialogTableOfContent(final ReaderActivity activity, DirectoryTab tab,
                                final ReaderDocumentTableOfContent toc,
                                final List<Bookmark> bookmarks,
                                final List<Annotation> annotations) {
        super(activity);
        mActivity = activity;

        setContentView(R.layout.dialog_table_of_content);
        fitDialogToWindow();
        mPreIcon = (ImageView) findViewById(R.id.pre_icon);
        mNextIcon = (ImageView) findViewById(R.id.next_icon);
        mBackIcon = (ImageView) findViewById(R.id.back_icon);
        mPageIndicator = (TextView) findViewById(R.id.page_size_indicator);
        mBackText = (TextView) findViewById(R.id.back_text);
        mBtnToc = (RadioButton) findViewById(R.id.btn_directory);
        mBtnBookmark = (RadioButton) findViewById(R.id.btn_bookmark);
        mBtnAnt = (RadioButton) findViewById(R.id.btn_annotation);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mPreIcon.setOnClickListener(this);
        mNextIcon.setOnClickListener(this);
        mBackIcon.setOnClickListener(this);
        mBackText.setOnClickListener(this);
        mBtnToc.setOnCheckedChangeListener(this);
        mBtnBookmark.setOnCheckedChangeListener(this);
        mBtnAnt.setOnCheckedChangeListener(this);

        mViewList.add(initTocView(mActivity,toc));
        mViewList.add(initBookmarkView(mActivity,bookmarks));
        mViewList.add(initAnnotationsView(mActivity,annotations));
        mViewPager.setAdapter(new ViewPagerAdapter());
        checkRadioButton(tab);
    }

    private void fitDialogToWindow() {
        Window mWindow = getWindow();
        WindowManager.LayoutParams mParams = mWindow.getAttributes();
        mParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mParams.gravity = Gravity.BOTTOM;
        mWindow.setAttributes(mParams);
        //force use all space in the screen.
        mWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    private int getTabIndex(DirectoryTab tab){
        switch (tab) {
            case TOC:
                return 0;
            case Bookmark:
                return 1;
            case Annotation:
                return 2;
        }
        return 0;
    }

    private void checkRadioButton(DirectoryTab tab) {
        switch (tab) {
            case TOC:
                mBtnToc.setChecked(true);
                break;
            case Bookmark:
                mBtnBookmark.setChecked(true);
                break;
            case Annotation:
                mBtnAnt.setChecked(true);
                break;
        }
    }

    private int getPageSize(DirectoryTab tab){
        Resources res = mActivity.getResources();
        switch (tab){
            case TOC:
                return res.getInteger(R.integer.table_of_content_row);
            case Bookmark:
                return res.getInteger(R.integer.bookmark_row);
            case Annotation:
                return res.getInteger(R.integer.annotation_row);
        }
        return 0;
    }

    private PageRecyclerView initTocView(final ReaderActivity activity,final ReaderDocumentTableOfContent toc){
        mRecordPosition.put(getTabIndex(DirectoryTab.TOC),0);
        Resources res = getContext().getResources();
        final int row = getPageSize(DirectoryTab.TOC);
        ArrayList<TreeRecyclerView.TreeNode> rootNodes = buildTreeNodesFromToc(toc);
        TreeRecyclerView treeRecyclerView = new TreeRecyclerView(mViewPager.getContext());
        int paddingTop = DimenUtils.dip2px(activity,res.getDimension(R.dimen.toc_tree_padding_top));
        treeRecyclerView.setPadding(0,paddingTop,0,paddingTop);
        treeRecyclerView.bindTree(rootNodes, new TreeRecyclerView.Callback() {
            @Override
            public void onTreeNodeClicked(TreeRecyclerView.TreeNode node) {
                ReaderDocumentTableOfContentEntry entry = (ReaderDocumentTableOfContentEntry)node.getTag();
                if (entry == null) {
                    return;
                }
                if (PagePositionUtils.isValidPosition(entry.getPosition())) {
                    DialogTableOfContent.this.hide();
                    new GotoPageAction(entry.getPosition()).execute(activity);
                }
            }

            @Override
            public void onItemCountChanged(int position,int itemCount) {
                updatePageIndicator(position,row,itemCount);
            }
        },row);

        if (toc != null && hasChildren(toc.getRootEntry())) {
            ReaderDocumentTableOfContentEntry entry = locateEntry(toc.getRootEntry().getChildren(), activity.getCurrentPage());
            TreeRecyclerView.TreeNode treeNode = findTreeNodeByTag(rootNodes, entry);
            if (treeNode != null) {
                treeRecyclerView.expandTo(treeNode);
            }
        }

        treeRecyclerView.setOnPagingListener(this);
        updatePageIndicator(0,row,treeRecyclerView.getAdapter().getItemCount());
        return treeRecyclerView;
    }

    private PageRecyclerView initBookmarkView(final ReaderActivity activity, final List<Bookmark> bookmarks) {
        mBookmarkList = bookmarks;
        mRecordPosition.put(getTabIndex(DirectoryTab.Bookmark),0);
        final int row = getPageSize(DirectoryTab.Bookmark);
        PageRecyclerView view = new PageRecyclerView(mViewPager.getContext());
        Resources res = getContext().getResources();
        int paddingTop = DimenUtils.dip2px(activity,res.getDimension(R.dimen.toc_tree_padding_top));
        view.setPadding(0,0,0,15);
        view.setAdapter(new PageRecyclerView.PageAdapter() {

            @Override
            public int getRowCount() {
                return row;
            }

            @Override
            public int getColumnCount() {
                return 1;
            }

            @Override
            public int getDataCount() {
                return mBookmarkList.size();
            }

            @Override
            public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                return new SimpleListViewItemViewHolder(activity, LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_table_of_content_list_item_view, parent, false));
            }

            @Override
            public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                Bookmark bookmark = bookmarks.get(position);
                ((SimpleListViewItemViewHolder)holder).bindView("",
                        bookmark.getQuote(),
                        bookmark.getPosition(),
                        bookmark.getCreatedAt().getTime(),
                        position,
                        DirectoryTab.Bookmark);
            }
        });
        view.setOnPagingListener(this);
        updatePageIndicator(0,row,view.getAdapter().getItemCount());
        return view;
    }

    private PageRecyclerView initAnnotationsView(final ReaderActivity activity, final List<Annotation> annotations) {
        mAnnotationList = annotations;
        mRecordPosition.put(getTabIndex(DirectoryTab.Annotation),0);
        final int row = getPageSize(DirectoryTab.Annotation);
        PageRecyclerView view = new PageRecyclerView(mViewPager.getContext());
        view.setAdapter(new PageRecyclerView.PageAdapter() {
            @Override
            public int getRowCount() {
                return row;
            }

            @Override
            public int getColumnCount() {
                return 1;
            }

            @Override
            public int getDataCount() {
                return mAnnotationList.size();
            }

            @Override
            public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                return new SimpleListViewItemViewHolder(mActivity, LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_table_of_content_list_item_view, parent, false));
            }

            @Override
            public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                Annotation annotation = mAnnotationList.get(position);
                ((SimpleListViewItemViewHolder)holder).bindView(annotation.getNote(),
                        annotation.getQuote(),
                        annotation.getPosition(),
                        annotation.getCreatedAt().getTime(),
                        position,
                        DirectoryTab.Bookmark);
            }
        });
        view.setOnPagingListener(this);
        updatePageIndicator(0,row,view.getAdapter().getItemCount());
        return view;
    }

    private void updatePageIndicator(int position, int itemCountOfPage, int itemCount){
        int page = itemCount / itemCountOfPage;
        int currentPage = page > 0 ? position / itemCountOfPage + 1 : 0;
        String show = String.format("%d/%d",currentPage,page);
        mPageIndicator.setText(show);
        mRecordPosition.put(mCurrentPosition,position);
    }

    private ReaderDocumentTableOfContentEntry locateEntry(List<ReaderDocumentTableOfContentEntry> entries, int page) {
        for (int i = 0; i < entries.size() - 1; i++) {
            ReaderDocumentTableOfContentEntry current = entries.get(i);
            int currentPage = PagePositionUtils.getPageNumber(current.getPosition());
            int nextPage = PagePositionUtils.getPageNumber(entries.get(i + 1).getPosition());
            if (currentPage <= page && page < nextPage) {
                return locateEntryWithChildren(current, page);
            }
        }

        ReaderDocumentTableOfContentEntry current = entries.get(entries.size() - 1);
        return locateEntryWithChildren(current, page);
    }

    private ReaderDocumentTableOfContentEntry locateEntryWithChildren(ReaderDocumentTableOfContentEntry entry, int page) {
        int currentPage = PagePositionUtils.getPageNumber(entry.getPosition());
        if (!hasChildren(entry)) {
            return entry;
        }
        int firstChildPage = PagePositionUtils.getPageNumber(entry.getChildren().get(0).getPosition());
        if (currentPage <= page && page < firstChildPage) {
            return entry;
        }
        return locateEntry(entry.getChildren(), page);
    }

    private TreeRecyclerView.TreeNode findTreeNodeByTag(List<TreeRecyclerView.TreeNode> nodeList, ReaderDocumentTableOfContentEntry entry) {
        for (TreeRecyclerView.TreeNode node : nodeList) {
            if (node.getTag() == entry) {
                return node;
            }
            if (node.hasChildren()) {
                TreeRecyclerView.TreeNode find = findTreeNodeByTag(node.getChildren(), entry);
                if (find != null) {
                    return find;
                }
            }
        }
        return null;
    }

    private boolean hasChildren(ReaderDocumentTableOfContentEntry entry) {
        return entry.getChildren() != null && entry.getChildren().size() > 0;
    }

    private ArrayList<TreeRecyclerView.TreeNode> buildTreeNodesFromToc(ReaderDocumentTableOfContent toc) {
        ArrayList<TreeRecyclerView.TreeNode> nodes = new ArrayList<>();
        if (toc != null && toc.getRootEntry().getChildren() != null) {
            for (ReaderDocumentTableOfContentEntry entry : toc.getRootEntry().getChildren()) {
                nodes.add(buildTreeNode(null, entry));
            }
        }
        return nodes;
    }

    private TreeRecyclerView.TreeNode buildTreeNode(TreeRecyclerView.TreeNode parent, ReaderDocumentTableOfContentEntry entry) {
        int page = PagePositionUtils.getPageNumber(entry.getPosition());
        String pos = page < 0 ? "" : String.valueOf(page + 1);
        TreeRecyclerView.TreeNode node = new TreeRecyclerView.TreeNode(parent, entry.getTitle(), pos, entry);
        if (entry.getChildren() != null) {
            for (ReaderDocumentTableOfContentEntry child : entry.getChildren()) {
                node.addChild(buildTreeNode(node, child));
            }
        }
        return node;
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mPreIcon)){
            mViewList.get(mCurrentPosition).prevPage();
        }else if (v.equals(mNextIcon)){
            mViewList.get(mCurrentPosition).nextPage();
        }else if (v.equals(mBackIcon) || v.equals(mBackText)){
            DialogTableOfContent.this.hide();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int pressedColor = getContext().getResources().getColor(android.R.color.white);
        int normalColor = getContext().getResources().getColor(android.R.color.black);
        buttonView.setTextColor(isChecked ? pressedColor : normalColor);
        if (isChecked){
            if (buttonView.equals(mBtnToc)){
                switchViewPage(DirectoryTab.TOC);
            }else if (buttonView.equals(mBtnBookmark)){
                switchViewPage(DirectoryTab.Bookmark);
                mViewList.get(getTabIndex(DirectoryTab.Bookmark)).getAdapter().notifyDataSetChanged();
            }else if (buttonView.equals(mBtnAnt)){
                switchViewPage(DirectoryTab.Annotation);
                mViewList.get(getTabIndex(DirectoryTab.Annotation)).getAdapter().notifyDataSetChanged();
            }
        }
    }

    private void switchViewPage(DirectoryTab tab){
        int position = getTabIndex(tab);
        mCurrentPosition = position;
        mViewPager.setCurrentItem(position,false);
        final int row = getPageSize(tab);
        updatePageIndicator(mRecordPosition.get(position),row,mViewList.get(position).getAdapter().getItemCount());
    }

    @Override
    public void onPrevPage(int prevPosition, int itemCount,int pageSize) {
        updatePageIndicator(prevPosition,pageSize, itemCount);
    }

    @Override
    public void onNextPage(int nextPosition, int itemCount,int pageSize) {
        updatePageIndicator(nextPosition,pageSize, itemCount);
    }
}
