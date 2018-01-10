package com.onyx.jdread.reader.catalog.dialog;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.data.model.Bookmark;
import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContent;
import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContentEntry;
import com.onyx.android.sdk.reader.common.ReaderViewInfo;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;
import com.onyx.android.sdk.ui.dialog.DialogChoose;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.ui.view.TreeRecyclerView;
import com.onyx.android.sdk.utils.DateTimeUtil;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ReaderBookInfoBinding;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.reader.actions.GetDocumentInfoAction;
import com.onyx.jdread.reader.catalog.adapter.BookmarkAdapter;
import com.onyx.jdread.reader.catalog.adapter.NoteAdapter;
import com.onyx.jdread.reader.catalog.event.ReaderBookInfoDialogHandler;
import com.onyx.jdread.reader.catalog.event.ReaderBookInfoTitleBackEvent;
import com.onyx.jdread.reader.catalog.model.ReaderBookInfoModel;
import com.onyx.jdread.reader.common.ReaderUserDataInfo;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.menu.common.ReaderBookInfoDialogConfig;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huxiaomao on 17/5/17.
 */

public class ReaderBookInfoDialog extends Dialog implements PageRecyclerView.OnPagingListener, ReaderBookInfoViewBack {
    private ReaderBookInfoBinding binding;
    private ReaderDocumentTableOfContent readerDocumentTableOfContent;
    private Map<Integer, PageRecyclerView> viewList = new HashMap<>();
    private ReaderBookInfoDialogHandler readerBookInfoDialogHandler;
    private int mode;

    public ReaderBookInfoDialog(@NonNull Context context, ReaderDataHolder readerDataHolder, ReaderViewInfo readerViewInfo, ReaderUserDataInfo readerUserDataInfo, int mode) {
        super(context, android.R.style.Theme_NoTitleBar_Fullscreen);
        initEventHandler(readerDataHolder, readerViewInfo, readerUserDataInfo);
        this.mode = mode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(false);

        initView();
        registerListener();
        initData();
    }

    private void initEventHandler(ReaderDataHolder readerDataHolder, ReaderViewInfo readerViewInfo, ReaderUserDataInfo readerUserDataInfo) {
        readerBookInfoDialogHandler = new ReaderBookInfoDialogHandler(readerDataHolder);
        readerBookInfoDialogHandler.setReaderViewInfo(readerViewInfo);
        readerBookInfoDialogHandler.setReaderUserDataInfo(readerUserDataInfo);
        readerBookInfoDialogHandler.setReaderBookInfoViewBack(this);
    }

    private void registerListener() {
        readerBookInfoDialogHandler.registerListener();
    }

    private void initData() {
        initTitleBar();
        binding.setReaderBookInfoModel(new ReaderBookInfoModel());
        new GetDocumentInfoAction(readerBookInfoDialogHandler.getReaderUserDataInfo()).execute(readerBookInfoDialogHandler.getReaderDataHolder());
    }

    private void initView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.reader_book_info, null, false);
        setContentView(binding.getRoot());

        viewList.put(ReaderBookInfoDialogConfig.CATALOG_MODE, binding.bookInfoCatalogContent);
        viewList.put(ReaderBookInfoDialogConfig.BOOKMARK_MODE, binding.bookInfoBookmarkContent);
        viewList.put(ReaderBookInfoDialogConfig.NOTE_MODE, binding.bookInfoNoteContent);
    }

    private void initTitleBar() {
        TitleBarModel titleBarModel = new TitleBarModel(EventBus.getDefault());
        titleBarModel.backEvent.set(new ReaderBookInfoTitleBackEvent());
        titleBarModel.title.set(readerBookInfoDialogHandler.getReaderDataHolder().getReader().getDocumentInfo().getBookName());
        binding.readerBookInfoTitleBar.setTitleModel(titleBarModel);
    }

    @Override
    public Dialog getContent() {
        return this;
    }

    @Override
    public void dismiss() {
        readerBookInfoDialogHandler.unregisterListener();
        super.dismiss();
    }

    @Override
    public void updateView(ReaderUserDataInfo readerUserDataInfo) {
        initTabData(readerUserDataInfo);
    }

    private void initTabData(ReaderUserDataInfo readerUserDataInfo) {
        initCatalogView(readerUserDataInfo);
        initBookmarkView(readerUserDataInfo);
        initAnnotationsView(readerUserDataInfo);
    }

    private void initCatalogView(final ReaderUserDataInfo readerUserDataInfo) {
        readerDocumentTableOfContent = readerUserDataInfo.getTableOfContent();
        final int row = ReaderBookInfoDialogConfig.getPageSize(getContext(),
                ReaderBookInfoDialogConfig.CATALOG_MODE);
        ArrayList<TreeRecyclerView.TreeNode> rootNodes = ReaderBookInfoDialogConfig.buildTreeNodesFromToc(readerDocumentTableOfContent);
        binding.bookInfoCatalogContent.setDefaultPageKeyBinding();
        binding.bookInfoCatalogContent.bindTree(rootNodes, new TreeRecyclerView.Callback() {
            @Override
            public void onTreeNodeClicked(TreeRecyclerView.TreeNode node) {
                ReaderDocumentTableOfContentEntry entry = (ReaderDocumentTableOfContentEntry) node.getTag();
                if (entry == null) {
                    return;
                }
                if (PagePositionUtils.isValidPosition(entry.getPosition())) {
                    //readerPresenter.getBookOperate().GotoPositionAction(entry.getPosition(), false);
                }
            }

            @Override
            public void onItemCountChanged(int position, int itemCount) {
                if (binding.bookInfoCatalogContent.getVisibility() == View.VISIBLE) {
                    onPageChanged();
                }
            }
        }, row);

        if (readerDocumentTableOfContent != null && hasChildren(readerDocumentTableOfContent.getRootEntry())) {
            ReaderDocumentTableOfContentEntry entry = locateEntry(readerDocumentTableOfContent.getRootEntry().getChildren(),
                    PagePositionUtils.getPosition(getCurrentPagePosition()));
            TreeRecyclerView.TreeNode treeNode = findTreeNodeByTag(rootNodes, entry);
            if (treeNode != null) {
                binding.bookInfoCatalogContent.setCurrentNode(treeNode);
                binding.bookInfoCatalogContent.expandTo(treeNode);
                binding.bookInfoCatalogContent.jumpToNode(treeNode);
            }
        }
        binding.bookInfoCatalogContent.setOnPagingListener(this);
    }

    private static List<Bookmark> deleteDuplicateBookmark(List<Bookmark> bookmarks) {
        Map<String, Bookmark> map = new HashMap<>();
        for (int i = 0; i < bookmarks.size(); i++) {
            Bookmark bookmark = bookmarks.get(i);
            map.put(bookmark.getPosition(), bookmark);
        }

        return new ArrayList<>(map.values());
    }

    private void initBookmarkView(final ReaderUserDataInfo readerUserDataInfo) {
        List<Bookmark> bookmarkList = deleteDuplicateBookmark(readerUserDataInfo.getBookmarks());
        binding.bookInfoBookmarkContent.setDefaultPageKeyBinding();
        BookmarkAdapter adapter = new BookmarkAdapter();
        binding.bookInfoBookmarkContent.setAdapter(adapter);
        binding.getReaderBookInfoModel().setBookmarks(readerDocumentTableOfContent,bookmarkList);
        //binding.bookInfoBookmarkContent.setOnPagingListener(this);
    }

    private void initAnnotationsView(ReaderUserDataInfo readerUserDataInfo) {
        final int row = ReaderBookInfoDialogConfig.getPageSize(getContext(),
                ReaderBookInfoDialogConfig.NOTE_MODE);
        binding.bookInfoNoteContent.setDefaultPageKeyBinding();
        NoteAdapter adapter = new NoteAdapter();
        binding.bookInfoNoteContent.setAdapter(adapter);
        binding.getReaderBookInfoModel().setNotes(readerUserDataInfo.getAnnotations());
//        binding.bookInfoNoteContent.setAdapter(new PageRecyclerView.PageAdapter() {
//            @Override
//            public int getRowCount() {
//                return row;
//            }
//
//            @Override
//            public int getColumnCount() {
//                return 1;
//            }
//
//            @Override
//            public int getDataCount() {
//                return annotationList.size();
//            }
//
//            @Override
//            public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
//                SimpleListViewItemViewHolder simpleListViewItemViewHolder = new SimpleListViewItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_table_of_content_list_item_view, parent, false));
//                simpleListViewItemViewHolder.setDialogViewId(R.string.delete_book_note);
//                return simpleListViewItemViewHolder;
//            }
//
//            @Override
//            public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//                Annotation annotation = annotationList.get(position);
//                ((SimpleListViewItemViewHolder) holder).bindView(annotation.getNote(),
//                        annotation.getQuote(),
//                        PagePositionUtils.fromPageNumber(annotation.getPageNumber()),
//                        annotation.getPosition(),
//                        annotation.getCreatedAt().getTime(),
//                        position,
//                        false);
//
//                if (binding.bookInfoNoteContent.getVisibility() == View.VISIBLE) {
//                    onPageChanged();
//                }
//            }
//        });
        binding.bookInfoNoteContent.setOnPagingListener(this);
    }

    public String getCurrentPagePosition() {
        return readerBookInfoDialogHandler.getReaderViewInfo().getFirstVisiblePage().getPositionSafely();
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

    private ReaderDocumentTableOfContentEntry locateEntry(List<ReaderDocumentTableOfContentEntry> entries, int pagePosition) {
        for (int i = 0; i < entries.size() - 1; i++) {
            ReaderDocumentTableOfContentEntry current = entries.get(i);
            int currentPagePosition = PagePositionUtils.getPosition(current.getPosition());
            int nextPagePosition = PagePositionUtils.getPosition(entries.get(i + 1).getPosition());
            if (currentPagePosition <= pagePosition && pagePosition < nextPagePosition) {
                return locateEntryWithChildren(current, pagePosition);
            }
        }

        int startEntryPosition = getDocumentTableOfContentEntryPosition(entries, 0);
        ReaderDocumentTableOfContentEntry current = entries.get(pagePosition < startEntryPosition ? 0 : entries.size() - 1);
        return locateEntryWithChildren(current, pagePosition);
    }

    private int getDocumentTableOfContentEntryPosition(final List<ReaderDocumentTableOfContentEntry> entries, final int index) {
        ReaderDocumentTableOfContentEntry entry = entries.get(index);
        return PagePositionUtils.getPosition(entry.getPosition());
    }

    private ReaderDocumentTableOfContentEntry locateEntryWithChildren(ReaderDocumentTableOfContentEntry entry, int pagePosition) {
        int currentPagePosition = PagePositionUtils.getPosition(entry.getPosition());
        if (!hasChildren(entry)) {
            return entry;
        }
        int firstChildPagePosition = PagePositionUtils.getPosition(entry.getChildren().get(0).getPosition());
        if (currentPagePosition <= pagePosition && pagePosition < firstChildPagePosition) {
            return entry;
        }
        return locateEntry(entry.getChildren(), pagePosition);
    }

    private boolean hasChildren(ReaderDocumentTableOfContentEntry entry) {
        return entry.getChildren() != null && entry.getChildren().size() > 0;
    }

    @Override
    public void onPageChange(int position, int itemCount, int pageSize) {
        onPageChanged();
    }

    private void onPageChanged() {
        int currentMode = binding.getReaderBookInfoModel().getCurrentTab().get();
        PageRecyclerView pageRecyclerView = viewList.get(currentMode);
        if (pageRecyclerView != null) {
            int totalPage = Math.max(pageRecyclerView.getPaginator().pages(), 1);
            int currentPage = Math.max(pageRecyclerView.getPaginator().getCurrentPage() + 1, 1);
            String format = String.format("%d/%d", currentPage, totalPage);
            binding.getReaderBookInfoModel().setPageInfo(format);
        }
    }

//    @Subscribe
//    public void onDeleteAnnotationResultEvent(DeleteAnnotationResultEvent event) {
//        int position = event.getPosition();
//        annotationList.remove(position);
//        bookInfoNoteContent.notifyDataSetChanged();
//        onPageChanged();
//    }

//    @Subscribe
//    public void onDeleteBookmarkResultEvent(DeleteBookmarkResultEvent event) {
//        int position = event.getPosition();
//        bookmarkList.remove(position);
//        bookInfoBookMarkContent.notifyDataSetChanged();
//        onPageChanged();
//    }

//    @Subscribe
//    public void onUpdateAnnotationResultEvent(UpdateAnnotationResultEvent event) {
//        int position = event.getPosition();
//        Annotation annotation = event.getAnnotation();
//        annotationList.set(position, annotation);
//        bookInfoNoteContent.notifyDataSetChanged();
//    }
}
