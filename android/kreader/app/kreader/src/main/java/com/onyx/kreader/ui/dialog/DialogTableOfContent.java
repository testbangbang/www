package com.onyx.kreader.ui.dialog;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.KeyEvent;
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
import android.widget.Toast;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.data.model.Bookmark;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.ui.dialog.DialogChoose;
import com.onyx.android.sdk.ui.dialog.OnyxBaseDialog;
import com.onyx.android.sdk.ui.dialog.OnyxCustomDialog;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.OnyxCustomViewPager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.ui.view.RadioButtonCenter;
import com.onyx.android.sdk.ui.view.TreeRecyclerView;
import com.onyx.android.sdk.utils.DateTimeUtil;
import com.onyx.android.sdk.utils.DimenUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.R;
import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContent;
import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContentEntry;
import com.onyx.android.sdk.reader.host.request.DeleteAnnotationRequest;
import com.onyx.android.sdk.reader.host.request.DeleteBookmarkRequest;
import com.onyx.kreader.note.actions.ClearPageAction;
import com.onyx.kreader.note.actions.GetScribbleBitmapAction;
import com.onyx.kreader.ui.actions.ExportAnnotationAction;
import com.onyx.kreader.ui.actions.ExportScribbleAction;
import com.onyx.kreader.ui.actions.GetDocumentInfoChain;
import com.onyx.kreader.ui.actions.GotoPositionAction;
import com.onyx.kreader.ui.actions.ShowAnnotationEditDialogAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.data.SingletonSharedPreference;
import com.onyx.kreader.ui.view.PreviewViewHolder;
import com.onyx.kreader.device.DeviceConfig;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by joy on 7/6/16.
 */
public class DialogTableOfContent extends OnyxBaseDialog implements CompoundButton.OnCheckedChangeListener, PageRecyclerView.OnPagingListener {

    private static final String TAG = DialogTableOfContent.class.getSimpleName();

    private ReaderDataHolder readerDataHolder;

    private ImageView preIcon;
    private ImageView nextIcon;
    private TextView pageIndicator;
    private RadioButtonCenter btnToc;
    private RadioButtonCenter btnBookmark;
    private RadioButtonCenter btnAnt;
    private RadioButtonCenter btnScribble;
    private RadioGroup btnGroup;
    private OnyxCustomViewPager viewPager;
    private LinearLayout emptyLayout;
    private TextView totalText;
    private LinearLayout backLayout;
    private LinearLayout pageIndicatorLayout;
    private LinearLayout exportLayout;

    private ReaderDocumentTableOfContent toc;
    private DirectoryTab currentTab;
    private List<PageRecyclerView> viewList = new ArrayList<>();
    private List<Bookmark> bookmarkList = new ArrayList<>();
    private List<Annotation> annotationList = new ArrayList<>();
    private SparseArray<Bitmap> scribblePreviewMap = new SparseArray<>();
    private SparseArray<PageInfo> pageInfoMap = new SparseArray<>();
    private PageRecyclerView scribblePageView;
    private GetScribbleBitmapAction getScribbleBitmapAction;
    private List<String> requestPages;
    private boolean loadedScribble = false;
    private View.OnFocusChangeListener onFocusChangeListener;

    public enum DirectoryTab {TOC, Bookmark, Annotation, Scribble}

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

        public SimpleListViewItemViewHolder(final ReaderDataHolder readerDataHolder, final View itemView) {
            super(itemView);

            textViewTitle = (TextView) itemView.findViewById(R.id.text_view_title);
            textViewDescription = (TextView) itemView.findViewById(R.id.text_view_description);
            textViewPage = (TextView) itemView.findViewById(R.id.text_view_page);
            textViewTime = (TextView) itemView.findViewById(R.id.text_view_time);
            deleteLayout = (LinearLayout) itemView.findViewById(R.id.delete_layout);
            editLayout = (LinearLayout) itemView.findViewById(R.id.edit_layout);
            splitLine = itemView.findViewById(R.id.split_line);

            deleteLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OnyxCustomDialog.getConfirmDialog(getContext(), getContext().getString(R.string.sure_delete), new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (currentTab == DirectoryTab.Bookmark) {
                                deleteBookmark(readerDataHolder, position);
                            } else {
                                deleteAnnotation(readerDataHolder, position);
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
                    onItemClick(v, pagePosition, position);
                }
            });
        }

        public void bindView(String title, String description, String pageName, String pagePosition, long time, int position, DirectoryTab tab) {
            textViewTitle.setText(title);
            description = StringUtils.deleteNewlineSymbol(description);
            textViewDescription.setText(description);
            Date date = new Date(time);
            textViewTime.setText(DateTimeUtil.formatDate(date, DateTimeUtil.DATE_FORMAT_YYYYMMDD_HHMM));
            String format = String.format(readerDataHolder.getContext().getString(R.string.page), Integer.valueOf(pageName) + 1);
            textViewPage.setText(format);
            this.pageName = pageName;
            this.pagePosition = pagePosition;
            this.position = position;

            editLayout.setVisibility(currentTab == DirectoryTab.Annotation ? View.VISIBLE : View.GONE);
        }
    }

    private void onDelete(final int position) {
        OnyxCustomDialog.getConfirmDialog(getContext(), getContext().getString(R.string.sure_delete), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (currentTab == DirectoryTab.Bookmark) {
                    deleteBookmark(readerDataHolder, position);
                } else {
                    deleteAnnotation(readerDataHolder, position);
                }
            }
        }).show();
    }

    private void onItemClick(View v, final String pagePosition, final int position) {
        if (v.isPressed()) {
            onJump(pagePosition);
            return;
        }
        if (currentTab == DirectoryTab.Bookmark) {
            new DialogChoose(getContext(), R.string.jump, R.string.delete, new DialogChoose.Callback() {
                @Override
                public void onClickListener(int index) {
                    switch (index) {
                        case 0:
                            onJump(pagePosition);
                            break;
                        case 1:
                            onDelete(position);
                            break;
                    }
                }
            }).show();
        }
    }

    private void onJump(final String pagePosition) {
        new GotoPositionAction(pagePosition).execute(readerDataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                DialogTableOfContent.this.dismiss();
            }
        });
    }

    private void showAnnotationEditDialog(final int position) {
        ShowAnnotationEditDialogAction action = new ShowAnnotationEditDialogAction(annotationList.get(position));
        action.setOnEditListener(new ShowAnnotationEditDialogAction.OnEditListener() {
            @Override
            public void onUpdateFinished(Annotation annotation) {
                annotationList.set(position, annotation);
                notifyPageDataSetChanged(currentTab);
            }

            @Override
            public void onDeleteFinished() {
                annotationList.remove(position);
                notifyPageDataSetChanged(currentTab);
                onPageChanged();
            }
        });
        action.execute(readerDataHolder, null);
    }

    private void deleteBookmark(ReaderDataHolder readerDataHolder, final int position) {
        final DeleteBookmarkRequest DbRequest = new DeleteBookmarkRequest(bookmarkList.get(position));
        readerDataHolder.submitRenderRequest(DbRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                bookmarkList.remove(position);
                notifyPageDataSetChanged(currentTab);
                onPageChanged();
            }
        });
    }

    private void deleteAnnotation(ReaderDataHolder readerDataHolder, final int position) {
        final DeleteAnnotationRequest DaRequest = new DeleteAnnotationRequest(annotationList.get(position));
        readerDataHolder.submitRenderRequest(DaRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                annotationList.remove(position);
                notifyPageDataSetChanged(currentTab);
                onPageChanged();
            }
        });
    }

    public class ViewPagerAdapter extends PagerAdapter {
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

    public DialogTableOfContent(final ReaderDataHolder readerDataHolder, DirectoryTab tab) {
        super(readerDataHolder.getContext(), R.style.android_dialog_no_title);
        this.readerDataHolder = readerDataHolder;
        int position = SingletonSharedPreference.getDialogTableOfContentTab(getContext(), 0);
        if (position < DirectoryTab.values().length) {
            currentTab = DirectoryTab.values()[position];
        } else {
            currentTab = DirectoryTab.TOC;
        }

        setContentView(R.layout.dialog_table_of_content);
        fitDialogToWindow();
        preIcon = (ImageView) findViewById(R.id.pre_icon);
        nextIcon = (ImageView) findViewById(R.id.next_icon);
        pageIndicator = (TextView) findViewById(R.id.page_size_indicator);
        btnToc = (RadioButtonCenter) findViewById(R.id.btn_directory);
        btnBookmark = (RadioButtonCenter) findViewById(R.id.btn_bookmark);
        btnAnt = (RadioButtonCenter) findViewById(R.id.btn_annotation);
        btnScribble = (RadioButtonCenter) findViewById(R.id.btn_scribble);
        viewPager = (OnyxCustomViewPager) findViewById(R.id.viewpager);
        totalText = (TextView) findViewById(R.id.total);
        emptyLayout = (LinearLayout) findViewById(R.id.empty_layout);
        backLayout = (LinearLayout) findViewById(R.id.back_layout);
        pageIndicatorLayout = (LinearLayout) findViewById(R.id.page_indicator_layout);
        exportLayout = (LinearLayout) findViewById(R.id.export_layout);
        btnGroup = (RadioGroup) findViewById(R.id.layout_menu);
        emptyLayout.setVisibility(View.INVISIBLE);
        viewPager.setPagingEnabled(false);

        btnToc.setOnCheckedChangeListener(this);
        btnBookmark.setOnCheckedChangeListener(this);
        btnAnt.setOnCheckedChangeListener(this);
        btnScribble.setOnCheckedChangeListener(this);

        btnToc.setTag(DirectoryTab.TOC);
        btnBookmark.setTag(DirectoryTab.Bookmark);
        btnAnt.setTag(DirectoryTab.Annotation);
        btnScribble.setTag(DirectoryTab.Scribble);

        this.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                        btnToc.setChecked(true);
                        return true;
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        btnBookmark.setChecked(true);
                        return true;
                }
                return false;
            }
        });

        showExportLayout(currentTab);
        setViewListener();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        if (DeviceConfig.sharedInstance(getContext()).isDisableNoteFunc() ||
                !readerDataHolder.isFixedPageDocument()) {
            btnScribble.setVisibility(View.GONE);
        }
        if (!Device.detectDevice().isTouchable(readerDataHolder.getContext())){
            btnAnt.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        getDocumentInfo();
    }

    private void getDocumentInfo() {
        final GetDocumentInfoChain documentInfoChain = new GetDocumentInfoChain();
        documentInfoChain.execute(readerDataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                EpdController.disableRegal();
                initViewPager(documentInfoChain.getTableOfContent(),
                        documentInfoChain.getBookmarks(),
                        documentInfoChain.getAnnotations(),
                        documentInfoChain.getScribblePages());
            }
        });
    }

    private void initViewPager(final ReaderDocumentTableOfContent tableOfContent,
                               final List<Bookmark> bookmarks,
                               final List<Annotation> annotations,
                               final List<String> scribblePages) {
        viewList.add(initTocView(readerDataHolder, tableOfContent));
        viewList.add(initBookmarkView(readerDataHolder, bookmarks));
        viewList.add(initAnnotationsView(readerDataHolder, annotations));
        viewList.add(initScribbleView(readerDataHolder, scribblePages));
        viewPager.setAdapter(new ViewPagerAdapter());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                PageRecyclerView.PageAdapter pageAdapter = getPageAdapter(currentTab);
                boolean hasContents = pageAdapter != null && pageAdapter.getItemCount() > 0;
                viewPager.setVisibility(hasContents ? View.VISIBLE : View.INVISIBLE);
                emptyLayout.setVisibility(hasContents ? View.GONE : View.VISIBLE);
                onPageChanged();
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        checkRadioButton(currentTab);
    }

    private void setViewListener() {
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

        exportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                export();
            }
        });
    }

    private void export() {
        if (currentTab == DirectoryTab.Annotation) {
            if (annotationList.size() <= 0) {
                Toast.makeText(getContext(), getContext().getString(R.string.no_data), Toast.LENGTH_SHORT).show();
                return;
            }

            new ExportAnnotationAction(annotationList, false, true).execute(readerDataHolder, new BaseCallback() {
                @Override
                public void done(BaseRequest request, Throwable e) {
                    String text = getContext().getString(e == null ? R.string.export_success : R.string.export_fail);
                    Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
                }
            });
        } else if (currentTab == DirectoryTab.Scribble) {
            if (scribblePreviewMap.size() <= 0) {
                Toast.makeText(getContext(), getContext().getString(R.string.no_data), Toast.LENGTH_SHORT).show();
                return;
            }
            List<String> requestPages = new ArrayList<>();
            for (int i = 0; i < scribblePreviewMap.size(); i++) {
                requestPages.add(String.valueOf(scribblePreviewMap.keyAt(i)));
            }
            new ExportScribbleAction(requestPages).execute(readerDataHolder, new BaseCallback() {
                @Override
                public void done(BaseRequest request, Throwable e) {
                    String text = getContext().getString(e == null ? R.string.export_success : R.string.export_fail);
                    Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
                }
            });
        }
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

    private void notifyPageDataSetChanged(DirectoryTab tab) {
        PageRecyclerView pageRecyclerView = getPageView(tab);
        if (pageRecyclerView != null) {
            pageRecyclerView.notifyDataSetChanged();
        }
    }

    private int getTabIndex(DirectoryTab tab) {
        return tab.ordinal();
    }

    private void checkRadioButton(DirectoryTab tab) {
        RadioButton checkButton = (RadioButton) btnGroup.getChildAt(getTabIndex(tab));
        checkButton.setChecked(true);
    }

    private int getPageSize(DirectoryTab tab) {
        Resources res = readerDataHolder.getContext().getResources();
        switch (tab) {
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

    private PageRecyclerView.PageAdapter getPageAdapter(DirectoryTab tab) {
        int pos = getTabIndex(tab);
        if (viewList.size() > pos) {
            return (PageRecyclerView.PageAdapter) viewList.get(pos).getAdapter();
        }
        return null;
    }

    private PageRecyclerView getPageView(DirectoryTab tab) {
        int pos = getTabIndex(tab);
        if (viewList.size() > pos) {
            return viewList.get(pos);
        }
        return null;
    }

    private PageRecyclerView initTocView(final ReaderDataHolder readerDataHolder, final ReaderDocumentTableOfContent toc) {
        this.toc = toc;
        final int row = getPageSize(DirectoryTab.TOC);
        ArrayList<TreeRecyclerView.TreeNode> rootNodes = buildTreeNodesFromToc(toc);
        TreeRecyclerView treeRecyclerView = new TreeRecyclerView(viewPager.getContext());
        treeRecyclerView.setDefaultPageKeyBinding();
        treeRecyclerView.bindTree(rootNodes, new TreeRecyclerView.Callback() {
            @Override
            public void onTreeNodeClicked(TreeRecyclerView.TreeNode node) {
                ReaderDocumentTableOfContentEntry entry = (ReaderDocumentTableOfContentEntry) node.getTag();
                if (entry == null) {
                    return;
                }
                if (PagePositionUtils.isValidPosition(entry.getPosition())) {
                    new GotoPositionAction(entry.getPosition()).execute(readerDataHolder, new BaseCallback() {
                        @Override
                        public void done(BaseRequest request, Throwable e) {
                            DialogTableOfContent.this.dismiss();
                        }
                    });
                }
            }

            @Override
            public void onItemCountChanged(int position, int itemCount) {
                onPageChanged();
            }
        }, row);

        if (toc != null && hasChildren(toc.getRootEntry())) {
            ReaderDocumentTableOfContentEntry entry = locateEntry(toc.getRootEntry().getChildren(), PagePositionUtils.getPosition(readerDataHolder.getCurrentPagePosition()));
            TreeRecyclerView.TreeNode treeNode = findTreeNodeByTag(rootNodes, entry);
            if (treeNode != null) {
                treeRecyclerView.setCurrentNode(treeNode);
                treeRecyclerView.expandTo(treeNode);
                treeRecyclerView.jumpToNode(treeNode);
            }
        }

        treeRecyclerView.setOnPagingListener(this);
        return treeRecyclerView;
    }

    private PageRecyclerView initBookmarkView(final ReaderDataHolder readerDataHolder, final List<Bookmark> bookmarks) {
        bookmarkList = bookmarks;
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
                ((SimpleListViewItemViewHolder) holder).bindView(title,
                        bookmark.getQuote(),
                        PagePositionUtils.fromPageNumber(bookmark.getPageNumber()),
                        bookmark.getPosition(),
                        bookmark.getCreatedAt().getTime(),
                        position,
                        DirectoryTab.Bookmark);
            }
        });
        view.setOnPagingListener(this);
        return view;
    }

    private PageRecyclerView initAnnotationsView(final ReaderDataHolder readerDataHolder, final List<Annotation> annotations) {
        annotationList = annotations;
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
                ((SimpleListViewItemViewHolder) holder).bindView(annotation.getNote(),
                        annotation.getQuote(),
                        PagePositionUtils.fromPageNumber(annotation.getPageNumber()),
                        annotation.getPosition(),
                        annotation.getCreatedAt().getTime(),
                        position,
                        DirectoryTab.Bookmark);
            }
        });
        view.setOnPagingListener(this);
        return view;
    }

    private PageRecyclerView initScribbleView(final ReaderDataHolder readerDataHolder, List<String> scribblePages) {
        for (String page : scribblePages) {
            scribblePreviewMap.put(Integer.valueOf(page), null);
        }
        scribblePageView = new PageRecyclerView(viewPager.getContext());
        final int padding = DimenUtils.dip2px(getContext(), 10);
        scribblePageView.setPadding(padding, padding, padding, padding);
        scribblePageView.setDefaultPageKeyBinding();
        scribblePageView.setLayoutManager(new DisableScrollGridManager(getContext()));
        scribblePageView.setAdapter(new PageRecyclerView.PageAdapter() {
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
                return scribblePreviewMap.size();
            }

            @Override
            public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                final PreviewViewHolder previewViewHolder = new PreviewViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.preview_list_item_view, parent, false));
                previewViewHolder.getContainer().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new GotoPositionAction(PagePositionUtils.fromPageNumber(previewViewHolder.getPage())).execute(readerDataHolder, new BaseCallback() {
                            @Override
                            public void done(BaseRequest request, Throwable e) {
                                DialogTableOfContent.this.dismiss();
                            }
                        });
                    }
                });
                previewViewHolder.getCloseView().setVisibility(View.VISIBLE);
                return previewViewHolder;
            }

            @Override
            public void onPageBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
                final PreviewViewHolder previewViewHolder = (PreviewViewHolder) holder;
                final int page = scribblePreviewMap.keyAt(position);
                Bitmap scribbleBitmap = scribblePreviewMap.get(page);
                previewViewHolder.getCloseView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OnyxCustomDialog.getConfirmDialog(getContext(), getContext().getString(R.string.sure_delete), new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeScribble(page, position);
                            }
                        }).show();
                    }
                });
                previewViewHolder.bindPreview(scribbleBitmap, page);
            }

            @Override
            public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
                super.onViewDetachedFromWindow(holder);
                final PreviewViewHolder previewViewHolder = (PreviewViewHolder) holder;
                Bitmap bitmap = previewViewHolder.getBitmap();
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                    previewViewHolder.getImageView().setImageDrawable(new ColorDrawable(Color.WHITE));
                }
            }
        });

        scribblePageView.setOnPagingListener(this);
        return scribblePageView;
    }

    private void removeScribble(final int page, final int position) {
        PageInfo pageInfo = pageInfoMap.get(page);
        if (pageInfo != null) {
            new ClearPageAction(pageInfo).execute(readerDataHolder, new BaseCallback() {
                @Override
                public void done(BaseRequest request, Throwable e) {
                    getPageAdapter(currentTab).notifyItemRemoved(position);
                }
            });
        }
    }

    private void requestScribblePreview(final PageRecyclerView scribblePageView) {
        clearRequestPages();
        if (scribblePreviewMap.size() <= 0) {
            return;
        }
        int pageBegin = scribblePageView.getPaginator().getCurrentPageBegin();
        int pageEnd = scribblePageView.getPaginator().getCurrentPageEnd();

        if (pageBegin < 0 || pageBegin > pageEnd) {
            return;
        }

        requestPages = new ArrayList<>();
        for (int i = pageBegin; i <= pageEnd; i++) {
            requestPages.add(String.valueOf(scribblePreviewMap.keyAt(i)));
        }

        getScribbleBitmapAction = new GetScribbleBitmapAction(requestPages, 300, 400);
        getScribbleBitmapAction.execute(readerDataHolder, new GetScribbleBitmapAction.Callback() {
            @Override
            public void onNext(String page, Bitmap bitmap, PageInfo pageInfo) {
                int pageNumber = Integer.valueOf(page);
                scribblePreviewMap.put(pageNumber, bitmap);
                pageInfoMap.put(pageNumber, pageInfo);
                loadedScribble = true;
                scribblePageView.getPageAdapter().notifyItemChanged(scribblePreviewMap.indexOfKey(pageNumber));
            }
        });
    }

    private void clearRequestPages() {
        if (requestPages != null) {
            requestPages.clear();
        }
    }

    private void onPageChanged() {
        updateTotalText(currentTab);
        if (viewList.size() == 0) {
            return;
        }
        PageRecyclerView pageView = viewList.get(getTabIndex(currentTab));
        int currentPage = Math.max(pageView.getPaginator().getCurrentPage() + 1, 1);
        int pages = Math.max(pageView.getPaginator().pages(), 1);
        final String show = String.format("%d/%d", currentPage, pages);
        // post to force relayout of page indicator, a work around for 4.0 and 4.2 devices
        pageView.post(new Runnable() {
            @Override
            public void run() {
                pageIndicator.setText(show);
            }
        });

        if (currentTab == DirectoryTab.Scribble && scribblePageView != null) {
            requestScribblePreview(scribblePageView);
        }
        showExportLayout(currentTab);
    }

    private void showExportLayout(DirectoryTab tab) {
        boolean showExport = tab == DirectoryTab.Scribble || tab == DirectoryTab.Annotation;
        exportLayout.setVisibility(showExport ? View.VISIBLE : View.GONE);
    }

    private void updateTotalText(DirectoryTab tab) {
        switch (tab) {
            case TOC:
                totalText.setVisibility(View.GONE);
                break;
            case Bookmark:
                totalText.setText(String.format(getContext().getString(R.string.total_page), bookmarkList.size()));
                totalText.setVisibility(View.VISIBLE);
                break;
            case Annotation:
                totalText.setText(String.format(getContext().getString(R.string.total_page), annotationList.size()));
                totalText.setVisibility(View.VISIBLE);
                break;
            case Scribble:
                totalText.setText(String.format(getContext().getString(R.string.total_page), scribblePreviewMap.size()));
                totalText.setVisibility(View.VISIBLE);
                break;
        }
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
        String pageName = PagePositionUtils.getPageNumberForDisplay(entry.getPageName());
        TreeRecyclerView.TreeNode node = new TreeRecyclerView.TreeNode(parent, entry.getTitle(), pageName, entry);
        if (entry.getChildren() != null) {
            for (ReaderDocumentTableOfContentEntry child : entry.getChildren()) {
                node.addChild(buildTreeNode(node, child));
            }
        }
        return node;
    }

    @Override
    public void dismiss() {
        clearRequestPages();
        if (loadedScribble) {
            new GotoPositionAction(readerDataHolder.getCurrentPagePosition()).execute(readerDataHolder);
        }
        super.dismiss();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            switchViewPage((DirectoryTab) buttonView.getTag());
        }
    }

    private void switchViewPage(DirectoryTab tab) {
        int position = getTabIndex(tab);
        SingletonSharedPreference.setDialogTableOfContentTab(getContext(), position);
        currentTab = tab;
        viewPager.setCurrentItem(position, false);
        if (tab != DirectoryTab.TOC) {
            notifyPageDataSetChanged(tab);
        }
    }

    @Override
    public void onPageChange(int position, int itemCount, int pageSize) {
        onPageChanged();
    }
}
