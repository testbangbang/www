package com.onyx.kreader.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.data.model.Bookmark;
import com.onyx.android.sdk.ui.utils.DialogHelp;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.OnyxCustomViewPager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.ui.view.TreeRecyclerView;
import com.onyx.android.sdk.utils.DateTimeUtil;
import com.onyx.android.sdk.utils.DimenUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.R;
import com.onyx.kreader.api.ReaderDocumentTableOfContent;
import com.onyx.kreader.api.ReaderDocumentTableOfContentEntry;
import com.onyx.kreader.host.request.DeleteAnnotationRequest;
import com.onyx.kreader.host.request.DeleteBookmarkRequest;
import com.onyx.kreader.note.actions.GetNotePageListAction;
import com.onyx.kreader.note.actions.GetScribbleBitmapAction;
import com.onyx.kreader.ui.actions.GotoPageAction;
import com.onyx.kreader.ui.actions.ShowAnnotationEditDialogAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.data.SingletonSharedPreference;
import com.onyx.kreader.ui.view.PreviewViewHolder;
import com.onyx.kreader.utils.PagePositionUtils;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by joy on 7/6/16.
 */
public class DialogTableOfContent extends Dialog implements CompoundButton.OnCheckedChangeListener, PageRecyclerView.OnPagingListener {

    private static final String TAG = DialogTableOfContent.class.getSimpleName();

    private ReaderDataHolder readerDataHolder;

    private ImageView preIcon;
    private ImageView nextIcon;
    private TextView pageIndicator;
    private RadioButton btnToc;
    private RadioButton btnBookmark;
    private RadioButton btnAnt;
    private RadioButton btnScribble;
    private RadioGroup btnGroup;
    private OnyxCustomViewPager viewPager;
    private TextView emptyText;
    private TextView totalText;
    private LinearLayout backLayout;

    private ReaderDocumentTableOfContent toc;
    private DirectoryTab currentTab;
    private List<PageRecyclerView> viewList = new ArrayList<>();
    private Map<DirectoryTab,Integer> recordPosition = new Hashtable<>();
    private List<String> scribblePages = new ArrayList<>();
    private List<Bookmark> bookmarkList = new ArrayList<>();
    private List<Annotation> annotationList = new ArrayList<>();
    private Map<String, Bitmap> scribbleBitmapCaches = new HashMap<>();

    public enum DirectoryTab { TOC , Bookmark, Annotation, Scribble}

    private class SimpleListViewItemViewHolder extends RecyclerView.ViewHolder{

        private TextView textViewDescription;
        private TextView textViewTitle;
        private TextView textViewPage;
        private TextView textViewTime;
        private LinearLayout deleteLayout;
        private LinearLayout editLayout;
        private View splitLine;
        private String page;
        private int position;

        public SimpleListViewItemViewHolder(final ReaderDataHolder readerDataHolder, final View itemView) {
            super(itemView);

            textViewTitle = (TextView)itemView.findViewById(R.id.text_view_title);
            textViewDescription = (TextView)itemView.findViewById(R.id.text_view_description);
            textViewPage = (TextView)itemView.findViewById(R.id.text_view_page);
            textViewTime = (TextView)itemView.findViewById(R.id.text_view_time);
            deleteLayout = (LinearLayout) itemView.findViewById(R.id.delete_layout);
            editLayout = (LinearLayout)itemView.findViewById(R.id.edit_layout);
            splitLine = itemView.findViewById(R.id.split_line);

            deleteLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogHelp.getConfirmDialog(getContext(), getContext().getString(R.string.sure_delete), new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (currentTab == DirectoryTab.Bookmark){
                                deleteBookmark(readerDataHolder,position);
                            }else {
                                deleteAnnotation(readerDataHolder,position);
                            }
                        }
                    }).show();
                }
            });

            editLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAnnotationEditDialog(position);
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new GotoPageAction(page).execute(readerDataHolder, new BaseCallback() {
                        @Override
                        public void done(BaseRequest request, Throwable e) {
                            DialogTableOfContent.this.dismiss();
                        }
                    });
                }
            });
        }

        public void bindView(String title,String description,String page,long time,int position,DirectoryTab tab){
            textViewTitle.setText(title);
            description = StringUtils.deleteNewlineSymbol(description);
            textViewDescription.setText(description);
            Date date = new Date(time);
            textViewTime.setText(DateTimeUtil.formatDate(date,DateTimeUtil.DATE_FORMAT_YYYYMMDD_HHMM));
            String format = String.format(readerDataHolder.getContext().getString(R.string.page),Integer.valueOf(page) + 1);
            textViewPage.setText(format);
            this.page = page;
            this.position = position;

            boolean showPageLastLine = (position + 1) % getPageSize(tab) != 0;
            this.splitLine.setVisibility(showPageLastLine ? View.VISIBLE :View.INVISIBLE);
            editLayout.setVisibility(currentTab == DirectoryTab.Annotation ? View.VISIBLE : View.GONE);
        }
    }

    private void showAnnotationEditDialog(final int position){
        ShowAnnotationEditDialogAction action = new ShowAnnotationEditDialogAction(annotationList.get(position));
        action.setOnEditListener(new ShowAnnotationEditDialogAction.OnEditListener() {
            @Override
            public void onUpdateFinished(Annotation annotation) {
                annotationList.set(position,annotation);
                notifyPageDataSetChanged(currentTab);
            }

            @Override
            public void onDeleteFinished() {
                annotationList.remove(position);
                notifyPageDataSetChanged(currentTab);
                updatePageIndicator(position, getPageSize(DirectoryTab.Annotation),getPageItemCount(currentTab));
            }
        });
        action.execute(readerDataHolder, null);
    }

    private void deleteBookmark(ReaderDataHolder readerDataHolder, final int position){
        final DeleteBookmarkRequest DbRequest = new DeleteBookmarkRequest(bookmarkList.get(position));
        readerDataHolder.submitRenderRequest(DbRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                bookmarkList.remove(position);
                notifyPageDataSetChanged(currentTab);
                updatePageIndicator(position, getPageSize(DirectoryTab.Bookmark),getPageItemCount(currentTab));
            }
        });
    }

    private void deleteAnnotation(ReaderDataHolder readerDataHolder, final int position){
        final DeleteAnnotationRequest DaRequest = new DeleteAnnotationRequest(annotationList.get(position));
        readerDataHolder.submitRenderRequest(DaRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                annotationList.remove(position);
                notifyPageDataSetChanged(currentTab);
                updatePageIndicator(position, getPageSize(DirectoryTab.Annotation),getPageItemCount(currentTab));
            }
        });
    }

    public class ViewPagerAdapter extends PagerAdapter{
        @Override
        public int getCount() {
            return 4;
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

    public DialogTableOfContent(final ReaderDataHolder readerDataHolder, DirectoryTab tab,
                                final ReaderDocumentTableOfContent toc,
                                final List<Bookmark> bookmarks,
                                final List<Annotation> annotations) {
        super(readerDataHolder.getContext(), R.style.dialog_no_title);
        this.readerDataHolder = readerDataHolder;
        int position = SingletonSharedPreference.getDialogTableOfContentTab(getContext(), 0);
        if (position < DirectoryTab.values().length ){
            currentTab = DirectoryTab.values()[position];
        }else {
            currentTab = DirectoryTab.TOC;
        }

        this.toc = toc;

        setContentView(R.layout.dialog_table_of_content);
        fitDialogToWindow();
        preIcon = (ImageView) findViewById(R.id.pre_icon);
        nextIcon = (ImageView) findViewById(R.id.next_icon);
        pageIndicator = (TextView) findViewById(R.id.page_size_indicator);
        btnToc = (RadioButton) findViewById(R.id.btn_directory);
        btnBookmark = (RadioButton) findViewById(R.id.btn_bookmark);
        btnAnt = (RadioButton) findViewById(R.id.btn_annotation);
        btnScribble = (RadioButton) findViewById(R.id.btn_scribble);
        viewPager = (OnyxCustomViewPager) findViewById(R.id.viewpager);
        totalText = (TextView) findViewById(R.id.total);
        emptyText = (TextView) findViewById(R.id.empty_text);
        backLayout = (LinearLayout) findViewById(R.id.back_layout);
        btnGroup = (RadioGroup) findViewById(R.id.layout_menu);
        emptyText.setVisibility(View.GONE);
        viewPager.setPagingEnabled(false);

        btnToc.setOnCheckedChangeListener(this);
        btnBookmark.setOnCheckedChangeListener(this);
        btnAnt.setOnCheckedChangeListener(this);
        btnScribble.setOnCheckedChangeListener(this);

        btnToc.setTag(DirectoryTab.TOC);
        btnBookmark.setTag(DirectoryTab.Bookmark);
        btnAnt.setTag(DirectoryTab.Annotation);
        btnScribble.setTag(DirectoryTab.Scribble);

        viewList.add(initTocView(readerDataHolder,toc));
        viewList.add(initBookmarkView(readerDataHolder,bookmarks));
        viewList.add(initAnnotationsView(readerDataHolder,annotations));
        viewList.add(initScribbleView(readerDataHolder));
        viewPager.setAdapter(new ViewPagerAdapter());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                PageRecyclerView.PageAdapter pageAdapter = getPageAdapter(currentTab);
                boolean hasContents = pageAdapter != null && pageAdapter.getItemCount() > 0;
                viewPager.setVisibility(hasContents ? View.VISIBLE : View.INVISIBLE);
                emptyText.setVisibility(hasContents ? View.GONE : View.VISIBLE);
                if (!hasContents){
                    emptyText.setText(getEmptyTips(currentTab));
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        checkRadioButton(currentTab);
        setViewListener();
    }

    private void setViewListener(){
        preIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewList.get(getTabIndex(currentTab)).prevPage();
            }
        });

        nextIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewList.get(getTabIndex(currentTab)).nextPage();
            }
        });

        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogTableOfContent.this.dismiss();
            }
        });

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                readerDataHolder.removeActiveDialog(DialogTableOfContent.this);
            }
        });
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

    private void notifyPageDataSetChanged(DirectoryTab tab){
        PageRecyclerView.PageAdapter pageAdapter = getPageAdapter(tab);
        if (pageAdapter != null){
            pageAdapter.notifyDataSetChanged();
        }
    }

    private int getPageItemCount(DirectoryTab tab){
        PageRecyclerView.PageAdapter pageAdapter = getPageAdapter(tab);
        if (pageAdapter != null){
            return pageAdapter.getItemCount();
        }
        return 0;
    }

    private int getTabIndex(DirectoryTab tab){
        return tab.ordinal();
    }

    private void checkRadioButton(DirectoryTab tab) {
        RadioButton checkButton = (RadioButton)btnGroup.getChildAt(getTabIndex(tab));
        checkButton.setChecked(true);
    }

    private int getPageSize(DirectoryTab tab){
        Resources res = readerDataHolder.getContext().getResources();
        switch (tab){
            case TOC:
                return res.getInteger(R.integer.table_of_content_row);
            case Bookmark:
                return res.getInteger(R.integer.bookmark_row);
            case Annotation:
                return res.getInteger(R.integer.annotation_row);
            case Scribble:
                return 9;
        }
        return 0;
    }

    private String getEmptyTips(DirectoryTab tab){
        switch (tab){
            case TOC:
                return getContext().getString(R.string.no_directories);
            case Bookmark:
                return getContext().getString(R.string.no_bookmarks);
            case Annotation:
                return getContext().getString(R.string.no_annotation);
            case Scribble:
                return getContext().getString(R.string.no_scribble);
            default:
                return getContext().getString(R.string.no_directories);
        }
    }

    private PageRecyclerView.PageAdapter getPageAdapter(DirectoryTab tab){
        int pos = getTabIndex(tab);
        if (viewList.size() > pos){
            return (PageRecyclerView.PageAdapter) viewList.get(pos).getAdapter();
        }
        return null;
    }

    private PageRecyclerView initTocView(final ReaderDataHolder readerDataHolder, final ReaderDocumentTableOfContent toc){
        recordPosition.put(DirectoryTab.TOC,0);
        final int row = getPageSize(DirectoryTab.TOC);
        ArrayList<TreeRecyclerView.TreeNode> rootNodes = buildTreeNodesFromToc(toc);
        TreeRecyclerView treeRecyclerView = new TreeRecyclerView(viewPager.getContext());
        treeRecyclerView.setDefaultPageKeyBinding();
        treeRecyclerView.bindTree(rootNodes, new TreeRecyclerView.Callback() {
            @Override
            public void onTreeNodeClicked(TreeRecyclerView.TreeNode node) {
                ReaderDocumentTableOfContentEntry entry = (ReaderDocumentTableOfContentEntry)node.getTag();
                if (entry == null) {
                    return;
                }
                if (PagePositionUtils.isValidPosition(entry.getPosition())) {
                    new GotoPageAction(entry.getPosition()).execute(readerDataHolder, new BaseCallback() {
                        @Override
                        public void done(BaseRequest request, Throwable e) {
                            DialogTableOfContent.this.dismiss();
                        }
                    });
                }
            }

            @Override
            public void onItemCountChanged(int position,int itemCount) {
                updatePageIndicator(position,row,itemCount);
            }
        },row);

        if (toc != null && hasChildren(toc.getRootEntry())) {
            ReaderDocumentTableOfContentEntry entry = locateEntry(toc.getRootEntry().getChildren(), readerDataHolder.getCurrentPage());
            TreeRecyclerView.TreeNode treeNode = findTreeNodeByTag(rootNodes, entry);
            if (treeNode != null) {
                treeRecyclerView.expandTo(treeNode);
                treeRecyclerView.setCurrentNode(treeNode);
            }
        }

        treeRecyclerView.setOnPagingListener(this);
        updatePageIndicator(0,row,treeRecyclerView.getAdapter().getItemCount());
        return treeRecyclerView;
    }

    private PageRecyclerView initBookmarkView(final ReaderDataHolder readerDataHolder, final List<Bookmark> bookmarks) {
        bookmarkList = bookmarks;
        recordPosition.put(DirectoryTab.Bookmark,0);
        final int row = getPageSize(DirectoryTab.Bookmark);
        PageRecyclerView view = new PageRecyclerView(viewPager.getContext());
        view.setDefaultPageKeyBinding();
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
                return new SimpleListViewItemViewHolder(readerDataHolder, LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_table_of_content_list_item_view, parent, false));
            }

            @Override
            public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                Bookmark bookmark = bookmarks.get(position);
                String title = "";
                if (toc != null && hasChildren(toc.getRootEntry())) {
                    ReaderDocumentTableOfContentEntry entry = locateEntry(toc.getRootEntry().getChildren(), PagePositionUtils.getPosition(bookmark.getPosition()));
                    title = entry.getTitle();
                }
                ((SimpleListViewItemViewHolder)holder).bindView(title,
                        bookmark.getQuote(),
                        bookmark.getPosition(),
                        bookmark.getCreatedAt().getTime(),
                        position,
                        DirectoryTab.Bookmark);
            }
        });
        view.setOnPagingListener(this);
        updatePageIndicator(0,row, view.getAdapter().getItemCount());
        return view;
    }

    private PageRecyclerView initAnnotationsView(final ReaderDataHolder readerDataHolder, final List<Annotation> annotations) {
        annotationList = annotations;
        recordPosition.put(DirectoryTab.Annotation,0);
        final int row = getPageSize(DirectoryTab.Annotation);
        PageRecyclerView view = new PageRecyclerView(viewPager.getContext());
        view.setDefaultPageKeyBinding();
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
                return new SimpleListViewItemViewHolder(readerDataHolder, LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_table_of_content_list_item_view, parent, false));
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
        updatePageIndicator(0,row, view.getAdapter().getItemCount());
        return view;
    }

    private PageRecyclerView initScribbleView(final ReaderDataHolder readerDataHolder){
        recordPosition.put(DirectoryTab.Scribble,0);
        final PageRecyclerView view = new PageRecyclerView(viewPager.getContext());
        int padding = DimenUtils.dip2px(getContext(), 10);
        view.setPadding(padding, padding, padding, padding);
        view.setDefaultPageKeyBinding();
        view.setLayoutManager(new DisableScrollGridManager(getContext()));
        view.setAdapter(new PageRecyclerView.PageAdapter() {
            @Override
            public int getRowCount() {
                return 3;
            }

            @Override
            public int getColumnCount() {
                return 3;
            }

            @Override
            public int getDataCount() {
                return scribblePages.size();
            }

            @Override
            public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                final PreviewViewHolder previewViewHolder = new PreviewViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.preview_list_item_view, parent, false));
                previewViewHolder.getContainer().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new GotoPageAction(previewViewHolder.getPage()).execute(readerDataHolder, new BaseCallback() {
                            @Override
                            public void done(BaseRequest request, Throwable e) {
                                DialogTableOfContent.this.dismiss();
                            }
                        });
                    }
                });
                return previewViewHolder;
            }

            @Override
            public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                final PreviewViewHolder previewViewHolder = (PreviewViewHolder)holder;
                final String page = scribblePages.get(position);
                Bitmap bitmap = scribbleBitmapCaches.get(page);
                if (bitmap == null || bitmap.isRecycled()){
                    final GetScribbleBitmapAction scribbleBitmapAction = new GetScribbleBitmapAction(page, 300, 400);
                    scribbleBitmapAction.execute(readerDataHolder, new BaseCallback() {
                        @Override
                        public void done(BaseRequest request, Throwable e) {
                            Bitmap scribbleBitmap = scribbleBitmapAction.getPdfBitmapImpl().getBitmap();
                            scribbleBitmapCaches.put(page, scribbleBitmap);
                            previewViewHolder.bindPreview(scribbleBitmap,page);
                        }
                    });
                }else {
                    previewViewHolder.bindPreview(bitmap,page);
                }
            }
        });

        final GetNotePageListAction noteDataAction = new GetNotePageListAction();
        noteDataAction.execute(readerDataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                scribblePages = noteDataAction.getScribblePages();
                view.getPageAdapter().notifyDataSetChanged();
            }
        });

        view.setOnPagingListener(this);
        updatePageIndicator(0,getPageSize(DirectoryTab.Scribble), view.getAdapter().getItemCount());
        return view;
    }

    private void updatePageIndicator(int position, int itemCountOfPage, int itemCount){
        int page = itemCount / itemCountOfPage;
        int currentPage = page > 0 ? position / itemCountOfPage + 1 : 1;
        page = Math.max(page, 1);
        String show = String.format("%d/%d",currentPage,page);
        pageIndicator.setText(show);
        recordPosition.put(currentTab,position);
        updateTotalText(currentTab);
    }

    private void updateTotalText(DirectoryTab tab){
        switch (tab){
            case TOC:
                totalText.setVisibility(View.GONE);
                break;
            case Bookmark:
                totalText.setText(String.format(getContext().getString(R.string.total_page),bookmarkList.size()));
                totalText.setVisibility(View.VISIBLE);
                break;
            case Annotation:
                totalText.setText(String.format(getContext().getString(R.string.total_page),annotationList.size()));
                totalText.setVisibility(View.VISIBLE);
                break;
        }
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
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int pressedColor = getContext().getResources().getColor(android.R.color.white);
        int normalColor = getContext().getResources().getColor(android.R.color.black);
        buttonView.setTextColor(isChecked ? pressedColor : normalColor);
        if (isChecked){
            switchViewPage((DirectoryTab)buttonView.getTag());
        }
    }

    private void switchViewPage(DirectoryTab tab){
        int position = getTabIndex(tab);
        SingletonSharedPreference.setDialogTableOfContentTab(getContext(), position);
        currentTab = tab;
        viewPager.setCurrentItem(position,false);
        final int row = getPageSize(tab);
        PageRecyclerView.PageAdapter pageAdapter = getPageAdapter(tab);
        if (pageAdapter != null){
            updatePageIndicator(recordPosition.get(tab),row,pageAdapter.getItemCount());
            if (tab != DirectoryTab.TOC){
                pageAdapter.notifyDataSetChanged();
            }
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
