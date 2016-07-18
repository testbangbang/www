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

    private ReaderActivity readerActivity;

    private ImageView preIcon;
    private ImageView nextIcon;
    private ImageView backIcon;
    private TextView pageIndicator;
    private TextView backText;
    private RadioButton btnToc;
    private RadioButton btnBookmark;
    private RadioButton btnAnt;
    private ViewPager viewPager;

    private DirectoryTab currentTab;
    private List<PageRecyclerView> viewList = new ArrayList<>();
    private Map<DirectoryTab,Integer> recordPosition = new Hashtable<>();
    List<Bookmark> bookmarkList;
    List<Annotation> annotationList;

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
            textViewEdit.setVisibility(currentTab == DirectoryTab.Annotation ? View.VISIBLE : View.GONE);
            imageViewEdit.setVisibility(currentTab == DirectoryTab.Annotation ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View v) {
            if (v.equals(textViewTitle)){
                DialogTableOfContent.this.hide();
                new GotoPageAction(page).execute(mActivity);
            }else if (v.equals(textViewDelete) || v.equals(imageViewDelete)){
                if (currentTab == DirectoryTab.Bookmark){
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
        ShowAnnotationEditDialogAction action = new ShowAnnotationEditDialogAction(annotationList.get(position));
        action.setOnEditListener(new ShowAnnotationEditDialogAction.OnEditListener() {
            @Override
            public void onUpdateFinished(Annotation annotation) {
                annotationList.set(position,annotation);
                getPageAdapter(currentTab).notifyDataSetChanged();
            }

            @Override
            public void onDeleteFinished() {
                annotationList.remove(position);
                getPageAdapter(currentTab).notifyDataSetChanged();
                updatePageIndicator(position,getPageSize(DirectoryTab.Annotation),getPageAdapter(currentTab).getItemCount());
            }
        });
        action.execute(readerActivity);
    }

    private void deleteBookmark(ReaderActivity readerActivity, final int position){
        final DeleteBookmarkRequest DbRequest = new DeleteBookmarkRequest(bookmarkList.get(position));
        readerActivity.getReader().submitRequest(readerActivity,DbRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                bookmarkList.remove(position);
                getPageAdapter(currentTab).notifyDataSetChanged();
                updatePageIndicator(position,getPageSize(DirectoryTab.Bookmark),getPageAdapter(currentTab).getItemCount());
            }
        });
    }

    private void deleteAnnotation(ReaderActivity readerActivity, final int position){
        final DeleteAnnotationRequest DaRequest = new DeleteAnnotationRequest(annotationList.get(position));
        readerActivity.getReader().submitRequest(readerActivity,DaRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                annotationList.remove(position);
                getPageAdapter(currentTab).notifyDataSetChanged();
                updatePageIndicator(position,getPageSize(DirectoryTab.Annotation),getPageAdapter(currentTab).getItemCount());
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
            PageRecyclerView view = viewList.get(position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(viewList.get(position));
        }
    }

    public DialogTableOfContent(final ReaderActivity activity, DirectoryTab tab,
                                final ReaderDocumentTableOfContent toc,
                                final List<Bookmark> bookmarks,
                                final List<Annotation> annotations) {
        super(activity);
        readerActivity = activity;
        currentTab = tab;

        setContentView(R.layout.dialog_table_of_content);
        fitDialogToWindow();
        preIcon = (ImageView) findViewById(R.id.pre_icon);
        nextIcon = (ImageView) findViewById(R.id.next_icon);
        backIcon = (ImageView) findViewById(R.id.back_icon);
        pageIndicator = (TextView) findViewById(R.id.page_size_indicator);
        backText = (TextView) findViewById(R.id.back_text);
        btnToc = (RadioButton) findViewById(R.id.btn_directory);
        btnBookmark = (RadioButton) findViewById(R.id.btn_bookmark);
        btnAnt = (RadioButton) findViewById(R.id.btn_annotation);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        preIcon.setOnClickListener(this);
        nextIcon.setOnClickListener(this);
        backIcon.setOnClickListener(this);
        backText.setOnClickListener(this);
        btnToc.setOnCheckedChangeListener(this);
        btnBookmark.setOnCheckedChangeListener(this);
        btnAnt.setOnCheckedChangeListener(this);

        viewList.add(initTocView(readerActivity,toc));
        viewList.add(initBookmarkView(readerActivity,bookmarks));
        viewList.add(initAnnotationsView(readerActivity,annotations));
        viewPager.setAdapter(new ViewPagerAdapter());
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
                btnToc.setChecked(true);
                break;
            case Bookmark:
                btnBookmark.setChecked(true);
                break;
            case Annotation:
                btnAnt.setChecked(true);
                break;
        }
    }

    private int getPageSize(DirectoryTab tab){
        Resources res = readerActivity.getResources();
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

    private PageRecyclerView.PageAdapter getPageAdapter(DirectoryTab tab){
        int pos = getTabIndex(tab);
        if (viewList.size() > pos){
            return (PageRecyclerView.PageAdapter) viewList.get(pos).getAdapter();
        }
        return null;
    }

    private PageRecyclerView initTocView(final ReaderActivity activity,final ReaderDocumentTableOfContent toc){
        recordPosition.put(DirectoryTab.TOC,0);
        Resources res = getContext().getResources();
        final int row = getPageSize(DirectoryTab.TOC);
        ArrayList<TreeRecyclerView.TreeNode> rootNodes = buildTreeNodesFromToc(toc);
        TreeRecyclerView treeRecyclerView = new TreeRecyclerView(viewPager.getContext());
        int paddingTop = DimenUtils.dip2px(activity,res.getDimension(R.dimen.toc_tree_padding_top));
        treeRecyclerView.setPadding(0,paddingTop,0,0);
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
        bookmarkList = bookmarks;
        recordPosition.put(DirectoryTab.Bookmark,0);
        final int row = getPageSize(DirectoryTab.Bookmark);
        PageRecyclerView view = new PageRecyclerView(viewPager.getContext());
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
                return bookmarkList.size();
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
        annotationList = annotations;
        recordPosition.put(DirectoryTab.Annotation,0);
        final int row = getPageSize(DirectoryTab.Annotation);
        PageRecyclerView view = new PageRecyclerView(viewPager.getContext());
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
                return annotationList.size();
            }

            @Override
            public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                return new SimpleListViewItemViewHolder(readerActivity, LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_table_of_content_list_item_view, parent, false));
            }

            @Override
            public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                Annotation annotation = annotationList.get(position);
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
        pageIndicator.setText(show);
        recordPosition.put(currentTab,position);
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
        if (v.equals(preIcon)){
            viewList.get(getTabIndex(currentTab)).prevPage();
        }else if (v.equals(nextIcon)){
            viewList.get(getTabIndex(currentTab)).nextPage();
        }else if (v.equals(backIcon) || v.equals(backText)){
            DialogTableOfContent.this.hide();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int pressedColor = getContext().getResources().getColor(android.R.color.white);
        int normalColor = getContext().getResources().getColor(android.R.color.black);
        buttonView.setTextColor(isChecked ? pressedColor : normalColor);
        if (isChecked){
            if (buttonView.equals(btnToc)){
                switchViewPage(DirectoryTab.TOC);
            }else if (buttonView.equals(btnBookmark)){
                switchViewPage(DirectoryTab.Bookmark);
            }else if (buttonView.equals(btnAnt)){
                switchViewPage(DirectoryTab.Annotation);
            }
        }
    }

    private void switchViewPage(DirectoryTab tab){
        int position = getTabIndex(tab);
        currentTab = tab;
        viewPager.setCurrentItem(position,false);
        final int row = getPageSize(tab);
        PageRecyclerView.PageAdapter pageAdapter = getPageAdapter(tab);
        if (pageAdapter != null){
            updatePageIndicator(recordPosition.get(tab),row,pageAdapter.getItemCount());
        }
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
