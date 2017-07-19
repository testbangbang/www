package com.onyx.android.dr.reader.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.dr.R;
import com.onyx.android.dr.reader.common.ReaderBookInfoDialogConfig;
import com.onyx.android.dr.reader.event.DeleteAnnotationResultEvent;
import com.onyx.android.dr.reader.event.DeleteBookmarkResultEvent;
import com.onyx.android.dr.reader.event.DocumentInfoRequestResultEvent;
import com.onyx.android.dr.reader.event.GotoPositionActionResultEvent;
import com.onyx.android.dr.reader.event.ReaderBookInfoBookmarkEvent;
import com.onyx.android.dr.reader.event.ReaderBookInfoCatalogEvent;
import com.onyx.android.dr.reader.event.ReaderBookInfoItemEvent;
import com.onyx.android.dr.reader.event.ReaderBookInfoNoteEvent;
import com.onyx.android.dr.reader.event.UpdateAnnotationResultEvent;
import com.onyx.android.dr.reader.presenter.ReaderPresenter;
import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.data.model.Bookmark;
import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContent;
import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContentEntry;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.ui.view.TreeRecyclerView;
import com.onyx.android.sdk.utils.DateTimeUtil;
import com.onyx.android.sdk.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huxiaomao on 17/5/17.
 */

public class ReaderBookInfoDialog extends Dialog implements View.OnClickListener, PageRecyclerView.OnPagingListener {
    private ReaderPresenter readerPresenter;
    private int currentMode = -1;
    private TextView bookInfoCatalog;
    private TextView bookInfoBookmark;
    private TextView bookInfoNote;
    private TreeRecyclerView bookInfoCatalogContent;
    private PageRecyclerView bookInfoBookMarkContent;
    private PageRecyclerView bookInfoNoteContent;
    private View bookInfoContentEmpty;
    private List<Bookmark> bookmarkList;
    private List<Annotation> annotationList;
    private ReaderDocumentTableOfContent readerDocumentTableOfContent;
    private TextView bookInfoPage;
    private Map<Integer, PageRecyclerView> viewList = new HashMap<>();

    public ReaderBookInfoDialog(ReaderPresenter readerPresenter, @NonNull Context context, int mode) {
        super(context, android.R.style.Theme_NoTitleBar_Fullscreen);
        this.readerPresenter = readerPresenter;
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.reader_book_info);

        initThirdLibrary();
        initData();
        initView();
        updateTabState(mode);
    }

    public ReaderPresenter getReaderPresenter() {
        return readerPresenter;
    }

    public List<Bookmark> getBookmarkList() {
        return bookmarkList;
    }

    public List<Annotation> getAnnotationList() {
        return annotationList;
    }

    private void initThirdLibrary() {
        EventBus.getDefault().register(this);
    }

    private void initData() {
        readerPresenter.getBookOperate().getDocumentInfo();
    }

    private void initView() {
        intiTabView();
        initContentView();

        viewList.put(ReaderBookInfoDialogConfig.CATALOG_MODE, bookInfoCatalogContent);
        viewList.put(ReaderBookInfoDialogConfig.BOOKMARK_MODE, bookInfoBookMarkContent);
        viewList.put(ReaderBookInfoDialogConfig.NOTE_MODE, bookInfoNoteContent);
        ReaderBookInfoItemEvent.bindItemEvent();
    }

    private void initContentView() {
        bookInfoContentEmpty = findViewById(R.id.book_info_content_empty);
        bookInfoCatalogContent = (TreeRecyclerView) findViewById(R.id.book_info_catalog_content);
        bookInfoBookMarkContent = (PageRecyclerView) findViewById(R.id.book_info_bookmark_content);
        bookInfoNoteContent = (PageRecyclerView) findViewById(R.id.book_info_note_content);
        bookInfoPage = (TextView) findViewById(R.id.book_info_page);
    }

    private void intiTabView() {
        bookInfoCatalog = (TextView) findViewById(R.id.book_info_catalog);
        bookInfoCatalog.setOnClickListener(this);

        bookInfoBookmark = (TextView) findViewById(R.id.book_info_bookmark);
        bookInfoBookmark.setOnClickListener(this);

        bookInfoNote = (TextView) findViewById(R.id.book_info_note);
        bookInfoNote.setOnClickListener(this);
    }

    private boolean updateTabState(int mode) {
        if (currentMode == mode) {
            return false;
        }
        currentMode = mode;

        bookInfoCatalog.setSelected(currentMode == ReaderBookInfoDialogConfig.CATALOG_MODE ? true : false);
        bookInfoCatalog.setTextColor(currentMode == ReaderBookInfoDialogConfig.CATALOG_MODE ? Color.WHITE : Color.BLACK);
        bookInfoCatalogContent.setVisibility(currentMode == ReaderBookInfoDialogConfig.CATALOG_MODE ? View.VISIBLE : View.GONE);

        bookInfoBookmark.setSelected(currentMode == ReaderBookInfoDialogConfig.BOOKMARK_MODE ? true : false);
        bookInfoBookmark.setTextColor(currentMode == ReaderBookInfoDialogConfig.BOOKMARK_MODE ? Color.WHITE : Color.BLACK);
        bookInfoBookMarkContent.setVisibility(currentMode == ReaderBookInfoDialogConfig.BOOKMARK_MODE ? View.VISIBLE : View.GONE);

        bookInfoNote.setSelected(currentMode == ReaderBookInfoDialogConfig.NOTE_MODE ? true : false);
        bookInfoNote.setTextColor(currentMode == ReaderBookInfoDialogConfig.NOTE_MODE ? Color.WHITE : Color.BLACK);
        bookInfoNoteContent.setVisibility(currentMode == ReaderBookInfoDialogConfig.NOTE_MODE ? View.VISIBLE : View.GONE);
        return true;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        Object event = ReaderBookInfoItemEvent.getItemEvent(id);
        if (event != null) {
            EventBus.getDefault().post(event);
        }
    }

    @Subscribe
    public void onReaderBookInfoCatalogEvent(ReaderBookInfoCatalogEvent event) {
        if (!updateTabState(ReaderBookInfoDialogConfig.CATALOG_MODE)) {
            return;
        }
        onPageChanged();
    }

    @Subscribe
    public void onReaderBookInfoBookmarkEvent(ReaderBookInfoBookmarkEvent event) {
        if (!updateTabState(ReaderBookInfoDialogConfig.BOOKMARK_MODE)) {
            return;
        }
        onPageChanged();
    }

    @Subscribe
    public void onReaderBookInfoNoteEvent(ReaderBookInfoNoteEvent event) {
        if (!updateTabState(ReaderBookInfoDialogConfig.NOTE_MODE)) {
            return;
        }
        onPageChanged();
    }

    @Override
    public void dismiss() {
        EventBus.getDefault().unregister(this);
        super.dismiss();
    }

    @Subscribe
    public void onDocumentInfoRequestResultEvent(DocumentInfoRequestResultEvent event) {
        ReaderDocumentTableOfContent readerDocumentTableOfContent = event.getReaderDocumentTableOfContent();
        initTocView(readerDocumentTableOfContent);
        initBookmarkView(event.getBookmarks());
        initAnnotationsView(event.getAnntation());
    }

    private void initTocView(final ReaderDocumentTableOfContent toc) {
        this.readerDocumentTableOfContent = toc;
        final int row = ReaderBookInfoDialogConfig.getPageSize(readerPresenter.getReaderView().getViewContext(),
                ReaderBookInfoDialogConfig.CATALOG_MODE);
        ArrayList<TreeRecyclerView.TreeNode> rootNodes = ReaderBookInfoDialogConfig.buildTreeNodesFromToc(toc);
        bookInfoCatalogContent.setDefaultPageKeyBinding();
        bookInfoCatalogContent.bindTree(rootNodes, new TreeRecyclerView.Callback() {
            @Override
            public void onTreeNodeClicked(TreeRecyclerView.TreeNode node) {
                ReaderDocumentTableOfContentEntry entry = (ReaderDocumentTableOfContentEntry) node.getTag();
                if (entry == null) {
                    return;
                }
                if (PagePositionUtils.isValidPosition(entry.getPosition())) {
                    readerPresenter.getBookOperate().GotoPositionAction(entry.getPosition(), false);
                }
            }

            @Override
            public void onItemCountChanged(int position, int itemCount) {
                if (bookInfoCatalogContent.getVisibility() == View.VISIBLE) {
                    onPageChanged();
                }
            }
        }, row);

        if (toc != null && hasChildren(toc.getRootEntry())) {
            ReaderDocumentTableOfContentEntry entry = locateEntry(toc.getRootEntry().getChildren(),
                    PagePositionUtils.getPosition(getCurrentPagePosition()));
            TreeRecyclerView.TreeNode treeNode = findTreeNodeByTag(rootNodes, entry);
            if (treeNode != null) {
                bookInfoCatalogContent.setCurrentNode(treeNode);
                bookInfoCatalogContent.expandTo(treeNode);
                bookInfoCatalogContent.jumpToNode(treeNode);
            }
        }
        bookInfoCatalogContent.setOnPagingListener(this);
    }

    private static List<Bookmark> deleteDuplicateBookmark(List<Bookmark> bookmarks) {
        Map<String, Bookmark> map = new HashMap<>();
        for (int i = 0; i < bookmarks.size(); i++) {
            Bookmark bookmark = bookmarks.get(i);
            map.put(bookmark.getPosition(), bookmark);
        }

        return new ArrayList<>(map.values());
    }

    private void initBookmarkView(final List<Bookmark> bookmarks) {
        bookmarkList = deleteDuplicateBookmark(bookmarks);
        final int row = ReaderBookInfoDialogConfig.getPageSize(readerPresenter.getReaderView().getViewContext(),
                ReaderBookInfoDialogConfig.BOOKMARK_MODE);
        bookInfoBookMarkContent.setDefaultPageKeyBinding();
        bookInfoBookMarkContent.setAdapter(new PageRecyclerView.PageAdapter() {

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
                return new SimpleListViewItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.dialog_table_of_content_list_item_view, parent, false));
            }

            @Override
            public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                Bookmark bookmark = bookmarks.get(position);
                String title = "";
                if (readerDocumentTableOfContent != null && hasChildren(readerDocumentTableOfContent.getRootEntry())) {
                    ReaderDocumentTableOfContentEntry entry = locateEntry(readerDocumentTableOfContent.getRootEntry().getChildren(), PagePositionUtils.getPosition(bookmark.getPosition()));
                    title = entry.getTitle();
                }
                ((SimpleListViewItemViewHolder) holder).bindView(title,
                        bookmark.getQuote(),
                        PagePositionUtils.fromPageNumber(bookmark.getPageNumber()),
                        bookmark.getPosition(),
                        bookmark.getCreatedAt().getTime(),
                        position,
                        true);

                if (bookInfoBookMarkContent.getVisibility() == View.VISIBLE) {
                    onPageChanged();
                }
            }
        });
        bookInfoBookMarkContent.setOnPagingListener(this);
    }

    private void initAnnotationsView(final List<Annotation> annotations) {
        annotationList = annotations;
        final int row = ReaderBookInfoDialogConfig.getPageSize(readerPresenter.getReaderView().getViewContext(),
                ReaderBookInfoDialogConfig.NOTE_MODE);
        bookInfoNoteContent.setDefaultPageKeyBinding();
        bookInfoNoteContent.setAdapter(new PageRecyclerView.PageAdapter() {
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
                return new SimpleListViewItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_table_of_content_list_item_view, parent, false));
            }

            @Override
            public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                Annotation annotation = annotationList.get(position);
                ((SimpleListViewItemViewHolder) holder).bindView(annotation.getNote(),
                        annotation.getQuote(),
                        PagePositionUtils.fromPageNumber(annotation.getPageNumber()),
                        annotation.getPosition(),
                        annotation.getCreatedAt().getTime(),
                        position,
                        false);

                if (bookInfoNoteContent.getVisibility() == View.VISIBLE) {
                    onPageChanged();
                }
            }
        });
        bookInfoNoteContent.setOnPagingListener(this);
    }

    public String getCurrentPagePosition() {
        return readerPresenter.getReaderViewInfo().getFirstVisiblePage().getPositionSafely();
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

    @Subscribe
    public void onGotoPositionActionResultEvent(GotoPositionActionResultEvent event) {
        dismiss();
    }

    private void onPageChanged() {
        PageRecyclerView pageRecyclerView = viewList.get(currentMode);
        if (pageRecyclerView != null) {
            int totalPage = Math.max(pageRecyclerView.getPaginator().pages(), 1);
            int currentPage = Math.max(pageRecyclerView.getPaginator().getCurrentPage() + 1, 1);
            String format = String.format("%d/%d", currentPage, totalPage);
            bookInfoPage.setText(format);
        }
    }

    private class SimpleListViewItemViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewDescription;
        private TextView textViewTitle;
        private TextView textViewPage;
        private TextView textViewTime;
        private LinearLayout deleteLayout;
        private LinearLayout editLayout;
        private View splitLine;
        private String pageName;
        private String pagePosition;
        private int position;
        private final View noteLine;
        private final ImageView bookMarkTopic;

        public SimpleListViewItemViewHolder(final View itemView) {
            super(itemView);

            textViewTitle = (TextView) itemView.findViewById(R.id.text_view_title);
            textViewDescription = (TextView) itemView.findViewById(R.id.text_view_description);
            textViewPage = (TextView) itemView.findViewById(R.id.text_view_page);
            textViewTime = (TextView) itemView.findViewById(R.id.text_view_time);
            deleteLayout = (LinearLayout) itemView.findViewById(R.id.delete_layout);
            editLayout = (LinearLayout) itemView.findViewById(R.id.edit_layout);
            splitLine = itemView.findViewById(R.id.split_line);
            noteLine = itemView.findViewById(R.id.book_info_note_line);
            bookMarkTopic = (ImageView) itemView.findViewById(R.id.book_info_mark_topic);

            deleteLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OnyxCustomDialog.getConfirmDialog(readerPresenter.getReaderView().getViewContext(),
                            getContext().getString(R.string.sure_delete), new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (currentMode == ReaderBookInfoDialogConfig.BOOKMARK_MODE) {
                                        readerPresenter.getBookOperate().removeBookmark(new DeleteBookmarkResultEvent().setPosition(position));
                                    } else {
                                        readerPresenter.getBookOperate().deleteAnnotation(annotationList.get(position),
                                                new DeleteAnnotationResultEvent().setPosition(position));
                                    }
                                }
                            }, null).show();
                }
            });

            editLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ReaderBookInfoDialogConfig.showAnnotationEditDialog(readerPresenter,
                            annotationList.get(position), position);
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick(v, pagePosition, position);
                }
            });
        }

        public void bindView(String title, String description, String pageName, String pagePosition, long time, int position, boolean isMark) {
            bookMarkTopic.setVisibility(isMark ? View.VISIBLE : View.GONE);
            textViewTitle.setVisibility(isMark ? View.GONE : View.VISIBLE);
            textViewTitle.setText(title);
            description = StringUtils.deleteNewlineSymbol(description);
            textViewDescription.setText(description);
            Date date = new Date(time);
            textViewTime.setText(DateTimeUtil.formatDate(date, DateTimeUtil.DATE_FORMAT_YYYYMMDD_HHMM));
            String format = String.format(readerPresenter.getReaderView().getViewContext().getString(R.string.page_search), Integer.valueOf(pageName) + 1);
            textViewPage.setText(format);
            this.pageName = pageName;
            this.pagePosition = pagePosition;
            this.position = position;
            noteLine.setVisibility(isMark ? View.GONE : View.VISIBLE);

            editLayout.setVisibility(currentMode == ReaderBookInfoDialogConfig.NOTE_MODE ? View.VISIBLE : View.GONE);
        }
    }

    private void onItemClick(View v, final String pagePosition, final int position) {
        if (v.isPressed()) {
            ReaderBookInfoDialogConfig.onJump(readerPresenter, pagePosition);
            return;
        }
        if (currentMode == ReaderBookInfoDialogConfig.BOOKMARK_MODE) {
            new DialogChoose(getContext(), R.string.jump, R.string.delete, new DialogChoose.Callback() {
                @Override
                public void onClickListener(int index) {
                    switch (index) {
                        case 0:
                            ReaderBookInfoDialogConfig.onJump(readerPresenter, pagePosition);
                            break;
                        case 1:
                            ReaderBookInfoDialogConfig.onDelete(ReaderBookInfoDialog.this, position, currentMode);
                            break;
                    }
                }
            }).show();
        }
    }

    @Subscribe
    public void onDeleteAnnotationResultEvent(DeleteAnnotationResultEvent event) {
        int position = event.getPosition();
        annotationList.remove(position);
        bookInfoNoteContent.notifyDataSetChanged();
        onPageChanged();
    }

    @Subscribe
    public void onDeleteBookmarkResultEvent(DeleteBookmarkResultEvent event) {
        int position = event.getPosition();
        bookmarkList.remove(position);
        bookInfoBookMarkContent.notifyDataSetChanged();
        onPageChanged();
    }

    @Subscribe
    public void onUpdateAnnotationResultEvent(UpdateAnnotationResultEvent event) {
        int position = event.getPosition();
        Annotation annotation = event.getAnnotation();
        annotationList.set(position, annotation);
        bookInfoNoteContent.notifyDataSetChanged();
    }
}
