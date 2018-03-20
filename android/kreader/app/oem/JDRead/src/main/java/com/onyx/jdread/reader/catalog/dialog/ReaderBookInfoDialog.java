package com.onyx.jdread.reader.catalog.dialog;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;

import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.data.model.Bookmark;
import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContent;
import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContentEntry;
import com.onyx.android.sdk.reader.common.ReaderViewInfo;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.ui.view.TreeRecyclerView;
import com.onyx.android.sdk.utils.DeviceUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ReaderBookInfoBinding;
import com.onyx.jdread.reader.actions.GetDocumentInfoAction;
import com.onyx.jdread.reader.actions.GotoPositionAction;
import com.onyx.jdread.reader.catalog.adapter.BookmarkAdapter;
import com.onyx.jdread.reader.catalog.adapter.NoteAdapter;
import com.onyx.jdread.reader.catalog.event.BookmarkItemClickEvent;
import com.onyx.jdread.reader.catalog.event.ReaderBookInfoDialogHandler;
import com.onyx.jdread.reader.catalog.event.ReaderBookInfoTitleBackEvent;
import com.onyx.jdread.reader.catalog.model.ReaderBookInfoModel;
import com.onyx.jdread.reader.catalog.model.BookTitleModel;
import com.onyx.jdread.reader.common.DocumentInfo;
import com.onyx.jdread.reader.common.ReaderUserDataInfo;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.event.CloseDocumentEvent;
import com.onyx.jdread.reader.event.EditNoteClickEvent;
import com.onyx.jdread.reader.menu.common.ReaderBookInfoDialogConfig;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private EventBus eventBus;

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
        eventBus = readerDataHolder.getEventBus();
    }

    private void registerListener() {
        readerBookInfoDialogHandler.registerListener();
    }

    private void initData() {
        initTitleBar();
        binding.setReaderBookInfoModel(new ReaderBookInfoModel(readerBookInfoDialogHandler.getReaderDataHolder().getEventBus()));
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
        BookTitleModel titleBarModel = new BookTitleModel(EventBus.getDefault());
        titleBarModel.backEvent.set(new ReaderBookInfoTitleBackEvent());
        titleBarModel.title.set(readerBookInfoDialogHandler.getReaderDataHolder().getBookName());
        binding.readerBookInfoTitleBar.setBookTitleModel(titleBarModel);
    }

    @Override
    public Dialog getContent() {
        return this;
    }

    @Override
    public void dismiss() {
        readerBookInfoDialogHandler.unregisterListener();
        if(readerBookInfoDialogHandler.getReaderDataHolder().getDocumentInfo().getOpenType() == DocumentInfo.OPEN_BOOK_CATALOG){
            eventBus.post(new CloseDocumentEvent());
        }
        super.dismiss();
    }

    @Override
    public void updateView() {
        initTabData(readerBookInfoDialogHandler.getReaderDataHolder().getReaderUserDataInfo(),
                readerBookInfoDialogHandler.getReaderDataHolder().getReaderViewInfo());
    }

    @Override
    public void updateAnnotation() {
        binding.getReaderBookInfoModel().setNotes(readerBookInfoDialogHandler.getReaderDataHolder().getReaderUserDataInfo().getAnnotationList());
    }

    private void initTabData(ReaderUserDataInfo readerUserDataInfo, ReaderViewInfo readerViewInfo) {
        initCatalogView(readerUserDataInfo,readerViewInfo);
        initBookmarkView(readerUserDataInfo,readerViewInfo);
        initAnnotationsView(readerUserDataInfo,readerViewInfo);
    }

    private void initCatalogView(final ReaderUserDataInfo readerUserDataInfo,ReaderViewInfo readerViewInfo) {
        readerDocumentTableOfContent = readerUserDataInfo.getTableOfContent();
        final int row = getContext().getResources().getInteger(R.integer.book_info_dialog_catalog_row);
        ReaderBookInfoDialogConfig.Node node = ReaderBookInfoDialogConfig.buildTreeNodesFromToc(readerDocumentTableOfContent);
        binding.getReaderBookInfoModel().setRootNodes(node.nodes);
        binding.bookInfoCatalogContent.setDefaultPageKeyBinding();
        binding.bookInfoCatalogContent.setPageTurningCycled(true);
        binding.bookInfoCatalogContent.bindTree(node.nodes, new TreeRecyclerView.Callback() {
            @Override
            public void onTreeNodeClicked(TreeRecyclerView.TreeNode node) {
                ReaderDocumentTableOfContentEntry entry = (ReaderDocumentTableOfContentEntry) node.getTag();
                if (entry == null) {
                    return;
                }
                if (PagePositionUtils.isValidPosition(entry.getPosition())) {
                    new GotoPositionAction(entry.getPosition()).execute(readerBookInfoDialogHandler.getReaderDataHolder(), new RxCallback() {
                        @Override
                        public void onNext(Object o) {
                            ReaderBookInfoDialog.this.dismiss();
                        }
                    });
                }
            }

            @Override
            public void onItemCountChanged(int position, int itemCount) {
                if (binding.bookInfoCatalogContent.getVisibility() == View.VISIBLE) {
                    onPageChanged();
                }
            }
        }, row,node.hasChildren);

        if (readerDocumentTableOfContent != null && hasChildren(readerDocumentTableOfContent.getRootEntry())) {
            ReaderDocumentTableOfContentEntry entry = locateEntry(node,PagePositionUtils.getPosition(getCurrentPagePosition()));
            TreeRecyclerView.TreeNode treeNode = findTreeNodeByTag(node.nodes, entry);
            if (treeNode != null) {
                binding.bookInfoCatalogContent.setCurrentNode(treeNode);
                binding.bookInfoCatalogContent.expandTo(treeNode);
                binding.bookInfoCatalogContent.jumpToNode(treeNode);
            }
        }
        binding.bookInfoCatalogContent.setOnPagingListener(this);
    }

    private static List<Bookmark> deleteDuplicateBookmark(List<Bookmark> bookmarks) {
        List<Bookmark> result = new ArrayList<>();
        Map<String, Bookmark> map = new HashMap<>();
        for (Bookmark bookmark: bookmarks) {
            if(map.get(bookmark.getPosition()) != null){
                continue;
            }
            map.put(bookmark.getPosition(), bookmark);
            result.add(bookmark);
        }
        Collections.sort(result,new Comparator<Bookmark>() {
            @Override
            public int compare(Bookmark o1, Bookmark o2) {
                int position1 = PagePositionUtils.getPosition(o1.getPosition());
                int position2 = PagePositionUtils.getPosition(o2.getPosition());
                return position1 - position2;
            }
        });
        return result;
    }

    @Override
    public void show() {
        super.show();
        DeviceUtils.adjustFullScreenStatus(this.getWindow(),true);
    }

    private void initBookmarkView(final ReaderUserDataInfo readerUserDataInfo,final ReaderViewInfo readerViewInfo) {
        List<Bookmark> bookmarkList = deleteDuplicateBookmark(readerUserDataInfo.getBookmarks());
        binding.bookInfoBookmarkContent.setDefaultPageKeyBinding();
        BookmarkAdapter adapter = new BookmarkAdapter();
        binding.bookInfoBookmarkContent.setPageTurningCycled(true);
        binding.bookInfoBookmarkContent.setAdapter(adapter);
        binding.bookInfoBookmarkContent.setOnPagingListener(this);
        binding.getReaderBookInfoModel().setBookmarks(readerDocumentTableOfContent, bookmarkList,readerViewInfo.getTotalPage());
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String position = (String) v.getTag();
                eventBus.post(new BookmarkItemClickEvent(position));
            }
        });
    }

    private void initAnnotationsView(ReaderUserDataInfo readerUserDataInfo,ReaderViewInfo readerViewInfo) {
        binding.bookInfoNoteContent.setDefaultPageKeyBinding();
        NoteAdapter adapter = new NoteAdapter();
        binding.bookInfoNoteContent.setPageTurningCycled(true);
        binding.bookInfoNoteContent.setAdapter(adapter);
        int size = binding.getReaderBookInfoModel().setNotes(readerUserDataInfo.getAnnotationList());
        if(size > 0){
            binding.readerBookInfoTitleBar.getBookTitleModel().setIsShowExport(true);
        }
        binding.bookInfoNoteContent.setOnPagingListener(this);
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer position = (Integer) v.getTag();
                showAnnotationContent(position);
            }
        });
    }

    private void showAnnotationContent(int position){
        List<Annotation> annotations = readerBookInfoDialogHandler.getReaderDataHolder().getReaderUserDataInfo().getAnnotationList();
        if(annotations == null || annotations.size() <= 0){
            return;
        }
        if(position < 0 || position >= annotations.size()){
            return;
        }

        Annotation annotation = annotations.get(position);

        EditNoteClickEvent editNoteClickEvent = new EditNoteClickEvent();
        editNoteClickEvent.setAnnotation(annotation);
        eventBus.post(editNoteClickEvent);
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
        if(nodeList.size() > 0){
            return nodeList.get(0);
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

    private ReaderDocumentTableOfContentEntry locateEntry(ReaderBookInfoDialogConfig.Node node, int pagePosition) {
        for (int i = 0; i < node.nodes.size() - 1; i++) {
            TreeRecyclerView.TreeNode startNode = node.nodes.get(i);
            ReaderDocumentTableOfContentEntry startEntry = (ReaderDocumentTableOfContentEntry) startNode.getTag();
            int currentPagePosition = PagePositionUtils.getPosition(startEntry.getPosition());

            TreeRecyclerView.TreeNode endNode = node.nodes.get(i + 1);
            ReaderDocumentTableOfContentEntry endEntry = (ReaderDocumentTableOfContentEntry)endNode.getTag();

            int nextPagePosition = PagePositionUtils.getPosition(endEntry.getPosition());
            if (currentPagePosition <= pagePosition && pagePosition < nextPagePosition) {
                return startEntry;
            }
        }
        return null;
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
            int totalPage = 0;
            if(currentMode == ReaderBookInfoDialogConfig.BOOKMARK_MODE){
                totalPage = binding.getReaderBookInfoModel().getBookmarksTotalPage();
            }
            if(currentMode == ReaderBookInfoDialogConfig.NOTE_MODE){
                totalPage = binding.getReaderBookInfoModel().getNotesTotalPage();
            }
            if(currentMode == ReaderBookInfoDialogConfig.CATALOG_MODE) {
                totalPage = Math.max(pageRecyclerView.getPaginator().pages(), 1);
            }
            int currentPage = Math.max(pageRecyclerView.getPaginator().getCurrentPage() + 1, 1);
            String format = String.format("%d/%d", currentPage, totalPage);
            binding.getReaderBookInfoModel().setPageInfo(format);
        }
    }

    @Override
    public void changeTab() {
        onPageChanged();
    }
}
