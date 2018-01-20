package com.onyx.jdread.reader.catalog.dialog;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;

import com.onyx.android.sdk.data.model.Bookmark;
import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContent;
import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContentEntry;
import com.onyx.android.sdk.reader.common.ReaderViewInfo;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.ui.view.TreeRecyclerView;
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

    public ReaderBookInfoDialog(@NonNull Context context, ReaderDataHolder readerDataHolder, int mode) {
        super(context, android.R.style.Theme_NoTitleBar_Fullscreen);
        initEventHandler(readerDataHolder);
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

    private void initEventHandler(ReaderDataHolder readerDataHolder) {
        readerBookInfoDialogHandler = new ReaderBookInfoDialogHandler(readerDataHolder);
        readerBookInfoDialogHandler.setReaderBookInfoViewBack(this);
    }

    private void registerListener() {
        readerBookInfoDialogHandler.registerListener();
    }

    private void initData() {
        initTitleBar();
        binding.setReaderBookInfoModel(new ReaderBookInfoModel());
        new GetDocumentInfoAction().execute(readerBookInfoDialogHandler.getReaderDataHolder(),null);
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
    public void updateView() {
        initTabData(readerBookInfoDialogHandler.getReaderDataHolder().getReaderUserDataInfo());
    }

    private void initTabData(ReaderUserDataInfo readerUserDataInfo) {
        initCatalogView(readerUserDataInfo);
        initBookmarkView(readerUserDataInfo);
        initAnnotationsView(readerUserDataInfo);
    }

    private void initCatalogView(final ReaderUserDataInfo readerUserDataInfo) {
        readerDocumentTableOfContent = readerUserDataInfo.getTableOfContent();
        final int row = getContext().getResources().getInteger(R.integer.book_info_dialog_catalog_row);
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
        binding.getReaderBookInfoModel().setBookmarks(readerDocumentTableOfContent, bookmarkList);
    }

    private void initAnnotationsView(ReaderUserDataInfo readerUserDataInfo) {
        binding.bookInfoNoteContent.setDefaultPageKeyBinding();
        NoteAdapter adapter = new NoteAdapter();
        binding.bookInfoNoteContent.setAdapter(adapter);
        binding.getReaderBookInfoModel().setNotes(readerUserDataInfo.getAnnotations());
        binding.bookInfoNoteContent.setOnPagingListener(this);
    }

    public String getCurrentPagePosition() {
        return readerBookInfoDialogHandler.getReaderDataHolder().getReaderViewInfo().getFirstVisiblePage().getPositionSafely();
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
}
