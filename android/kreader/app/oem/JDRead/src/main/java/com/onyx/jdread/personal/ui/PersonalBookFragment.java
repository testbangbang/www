package com.onyx.jdread.personal.ui;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.onyx.android.sdk.data.GPaginator;
import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.Metadata_Table;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.data.utils.QueryBuilder;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.PersonalBookBinding;
import com.onyx.jdread.library.action.RxMetadataLoadAction;
import com.onyx.jdread.library.model.LibraryDataBundle;
import com.onyx.jdread.library.model.LibraryViewDataModel;
import com.onyx.jdread.library.model.PopMenuModel;
import com.onyx.jdread.library.ui.SearchBookFragment;
import com.onyx.jdread.library.view.DashLineItemDivider;
import com.onyx.jdread.library.view.MenuPopupWindow;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.personal.action.CompareLocalMetadataAction;
import com.onyx.jdread.personal.action.GetBoughtAction;
import com.onyx.jdread.personal.action.GetUnlimitedAction;
import com.onyx.jdread.personal.action.QueryAllCloudMetadataAction;
import com.onyx.jdread.personal.adapter.PersonalBookAdapter;
import com.onyx.jdread.personal.event.FilterAllEvent;
import com.onyx.jdread.personal.event.FilterHaveBoughtEvent;
import com.onyx.jdread.personal.event.FilterReadVipEvent;
import com.onyx.jdread.personal.event.FilterSelfImportEvent;
import com.onyx.jdread.personal.model.PersonalBookModel;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.setting.event.BackToSettingFragmentEvent;
import com.onyx.jdread.shop.action.BookshelfInsertAction;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookDetailResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookExtraInfoBean;
import com.onyx.jdread.shop.event.DownloadFinishEvent;
import com.onyx.jdread.shop.event.DownloadingEvent;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.utils.BookDownloadUtils;
import com.onyx.jdread.shop.utils.DownLoadHelper;
import com.onyx.jdread.util.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by li on 2018/1/4.
 */

public class PersonalBookFragment extends BaseFragment {
    private PersonalBookBinding binding;
    private PersonalBookAdapter personalBookAdapter;
    private PersonalBookModel personalBookModel;
    private GPaginator paginator;
    private List<Metadata> allBook = new ArrayList<>();
    private List<Metadata> importBooks = new ArrayList<>();
    private List<Metadata> boughtBooks;
    private List<Metadata> unlimitedBooks;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = (PersonalBookBinding) DataBindingUtil.inflate(inflater, R.layout.fragment_personal_book, container, false);
        initView();
        initData();
        initListener();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.ensureRegister(PersonalDataBundle.getInstance().getEventBus(), this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Utils.ensureUnregister(PersonalDataBundle.getInstance().getEventBus(), this);
        if (boughtBooks != null) {
            boughtBooks.clear();
            boughtBooks = null;
        }
        if (unlimitedBooks != null) {
            unlimitedBooks.clear();
            unlimitedBooks = null;
        }
        if (allBook != null) {
            allBook.clear();
        }
    }

    private void initView() {
        binding.personalBookRecycler.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        binding.personalBookRecycler.setPageTurningCycled(true);
        DashLineItemDivider decoration = new DashLineItemDivider();
        binding.personalBookRecycler.addItemDecoration(decoration);
        personalBookAdapter = new PersonalBookAdapter();
        binding.personalBookRecycler.setAdapter(personalBookAdapter);
        paginator = binding.personalBookRecycler.getPaginator();
    }

    private void initData() {
        TitleBarModel titleModel = PersonalDataBundle.getInstance().getTitleModel();
        titleModel.title.set(JDReadApplication.getInstance().getResources().getString(R.string.personal_books));
        titleModel.backEvent.set(new BackToSettingFragmentEvent());
        binding.personalBookTitle.setTitleModel(titleModel);
        personalBookModel = PersonalDataBundle.getInstance().getPersonalBookModel();
        setFilter(R.string.the_default);
    }

    private void initListener() {
        binding.personalBookFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuPopupWindow popupWindow = new MenuPopupWindow(getActivity(), PersonalDataBundle.getInstance().getEventBus());
                List<PopMenuModel> menus = personalBookModel.getMenus();
                if (menus != null && menus.size() > 0) {
                    popupWindow.showPopupWindow(binding.personalBookFilter, menus, JDReadApplication
                                    .getInstance().getResources().getInteger(R.integer.personal_book_filter_x),
                            JDReadApplication.getInstance().getResources().getInteger(R.integer.personal_book_filter_y));
                }

            }
        });

        binding.personalBookSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewEventCallBack.gotoView(SearchBookFragment.class.getName());
            }
        });

        personalBookAdapter.setOnItemClickListener(new PageRecyclerView.PageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (JDReadApplication.getInstance().getResources().getString(R.string.all).equals(binding.getFilterName()) ||
                        JDReadApplication.getInstance().getResources().getString(R.string.self_import).equals(binding.getFilterName())) {
                    // TODO: 2018/1/8 open book
                    return;
                }
                if (JDReadApplication.getInstance().getResources().getString(R.string.read_vip).equals(binding.getFilterName())) {
                    // TODO: 2018/1/9 judge vip
                }

                List<Metadata> data = personalBookAdapter.getData();
                Metadata metadata = data.get(position);
                BookDetailResultBean.DetailBean detail = covert(metadata);
                BookExtraInfoBean infoBean = detail.bookExtraInfoBean;
                if (DownLoadHelper.isDownloading(infoBean.downLoadState)) {
                    DownLoadHelper.stopDownloadingTask(metadata.getTags());
                    infoBean.downLoadState = DownLoadHelper.getPausedState();
                    updateProgress(detail);
                    return;
                }

                if (infoBean.percentage == DownLoadHelper.DOWNLOAD_PERCENT_FINISH) {
                    // TODO: 2018/1/8 open book
                    return;
                }
                BookDownloadUtils.download(detail, ShopDataBundle.getInstance());
            }
        });

        binding.personalBookRecycler.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPageChange(int position, int itemCount, int pageSize) {
                binding.setPage(paginator.getProgressText());
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void onDownloadingEvent(DownloadingEvent event) {
        BaseDownloadTask task = OnyxDownloadManager.getInstance().getTask(event.tag);
        if (task != null) {
            BookDetailResultBean.DetailBean bookDetail = ShopDataBundle.getInstance().getBookDetail();
            bookDetail.bookExtraInfoBean.downLoadState = task.getStatus();
            bookDetail.bookExtraInfoBean.localPath = task.getPath();
            bookDetail.tag = event.tag;
            bookDetail.bookExtraInfoBean.percentage = (int) (event.progressInfoModel.progress * 100);
            updateProgress(bookDetail);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void onDownloadFinishEvent(DownloadFinishEvent event) {
        BaseDownloadTask task = OnyxDownloadManager.getInstance().getTask(event.tag);
        if (task != null) {
            BookDetailResultBean.DetailBean bookDetail = ShopDataBundle.getInstance().getBookDetail();
            bookDetail.bookExtraInfoBean.downLoadState = task.getStatus();
            bookDetail.bookExtraInfoBean.localPath = task.getPath();
            bookDetail.bookExtraInfoBean.percentage = DownLoadHelper.DOWNLOAD_PERCENT_FINISH;
            updateProgress(bookDetail);
        }
    }

    private synchronized void updateProgress(BookDetailResultBean.DetailBean detail) {
        if (personalBookAdapter == null) {
            return;
        }
        List<Metadata> data = personalBookAdapter.getData();
        for (int i = 0; i < data.size(); i++) {
            Metadata metadata = data.get(i);
            if (String.valueOf(detail.ebook_id).equals(metadata.getCloudId())) {
                String infoBean = JSONObjectParseUtils.toJson(detail.bookExtraInfoBean);
                metadata.setExtraAttributes(infoBean);
                metadata.setTags((String) detail.tag);
                personalBookAdapter.notifyDataSetChanged();
                if (DownLoadHelper.canInsertBookDetail(detail.bookExtraInfoBean.downLoadState)) {
                    BookshelfInsertAction action = new BookshelfInsertAction(detail, detail.bookExtraInfoBean.localPath);
                    action.execute(ShopDataBundle.getInstance(), null);
                }
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackToSettingFragmentEvent(BackToSettingFragmentEvent event) {
        viewEventCallBack.viewBack();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFilterAllEvent(FilterAllEvent event) {
        setFilter(event.getResId());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFilterHaveBoughtEvent(FilterHaveBoughtEvent event) {
        setFilter(event.getResId());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFilterReadVipEvent(FilterReadVipEvent event) {
        setFilter(event.getResId());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFilterSelfImportEvent(FilterSelfImportEvent event) {
        setFilter(event.getResId());
    }

    private void setFilter(int id) {
        switch (id) {
            case R.string.the_default:
                binding.setFilterName(JDReadApplication.getInstance().getResources().getString(R.string.all));
                queryAllCloud();
                break;
            case R.string.have_bought:
                binding.setFilterName(JDReadApplication.getInstance().getResources().getString(R.string.have_bought));
                getBoughtBooks();
                break;
            case R.string.read_vip:
                binding.setFilterName(JDReadApplication.getInstance().getResources().getString(R.string.read_vip));
                getUnlimitedBooks();
                break;
            case R.string.self_import:
                binding.setFilterName(JDReadApplication.getInstance().getResources().getString(R.string.self_import));
                getSelfImportBooks();
                break;
        }
    }

    private void getSelfImportBooks() {
        QueryArgs queryArgs = QueryBuilder.allBooksQuery(SortBy.None, SortOrder.Asc);
        queryArgs.conditionGroup.and(Metadata_Table.cloudId.isNull());
        final RxMetadataLoadAction action = new RxMetadataLoadAction(queryArgs);
        action.execute(LibraryDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                LibraryViewDataModel libraryViewDataModel = LibraryDataBundle.getInstance().getLibraryViewDataModel();
                ObservableList<DataModel> items = libraryViewDataModel.items;
                List<Metadata> metadatas = convertToMetadata(items);
                if (metadatas != null) {
                    setAdapterData(metadatas);
                }
            }
        });
    }

    private void getUnlimitedBooks() {
        if (unlimitedBooks != null) {
            setAdapterData(unlimitedBooks);
            return;
        }
        final GetUnlimitedAction unlimitedAction = new GetUnlimitedAction();
        unlimitedAction.execute(PersonalDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                unlimitedBooks = unlimitedAction.getUnlimitedBooks();
                if (unlimitedBooks != null) {
                    compareLocalMetadata(unlimitedBooks, true);
                }
            }
        });
    }

    private void getBoughtBooks() {
        if (boughtBooks != null) {
            setAdapterData(boughtBooks);
            return;
        }
        final GetBoughtAction boughtAction = new GetBoughtAction();
        boughtAction.execute(PersonalDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                boughtBooks = boughtAction.getBoughtBooks();
                if (boughtBooks != null && boughtBooks.size() > 0) {
                    compareLocalMetadata(boughtBooks, false);
                }
                getUnlimitedBooks();
            }
        });
    }

    private void queryAllCloud() {
        final QueryAllCloudMetadataAction queryAction = new QueryAllCloudMetadataAction();
        queryAction.execute(PersonalDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                List<Metadata> metadatas = queryAction.getMetadatas();
                if (metadatas != null && metadatas.size() > 0) {
                    allBook.addAll(metadatas);
                }
                getBoughtBooks();
            }
        });
    }

    private void compareLocalMetadata(List<Metadata> list, final boolean isAll) {
        final CompareLocalMetadataAction action = new CompareLocalMetadataAction(list);
        action.execute(PersonalDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                List<Metadata> metadataList = action.getMetadataList();
                if (metadataList != null) {
                    allBook.addAll(metadataList);
                }
                if (isAll) {
                    List<Metadata> list = deleteRepeat(allBook);
                    setAdapterData(list);
                }
            }
        });
    }

    private List<Metadata> deleteRepeat(List<Metadata> list) {
        HashSet<Metadata> set = new HashSet<>(list);
        list.clear();
        list.addAll(set);
        return list;
    }

    private void setAdapterData(List<Metadata> metadatas) {
        if (personalBookAdapter != null) {
            personalBookAdapter.setData(metadatas);
            binding.personalBookRecycler.resize(personalBookAdapter.getRowCount(), personalBookAdapter.getColumnCount(), metadatas.size());
            String progressText = paginator.getProgressText();
            binding.setPage(progressText);
            binding.setBooks(metadatas.size());
        }
    }

    private BookDetailResultBean.DetailBean covert(Metadata metadata) {
        BookDetailResultBean.DetailBean detail = new BookDetailResultBean.DetailBean();
        detail.name = metadata.getName();
        detail.author = metadata.getAuthors();
        detail.ebook_id = Integer.parseInt(metadata.getCloudId());
        detail.image_url = metadata.getCoverUrl();
        detail.file_size = metadata.getSize();
        detail.downLoadUrl = StringUtils.isNotBlank(metadata.getLocation()) ? metadata.getLocation() : null;
        String extraAttributes = metadata.getExtraAttributes();

        BookExtraInfoBean infoBean = null;
        if (StringUtils.isNullOrEmpty(extraAttributes)) {
            infoBean = new BookExtraInfoBean();
        } else {
            infoBean = JSONObjectParseUtils.toBean(extraAttributes, BookExtraInfoBean.class);
        }
        if (StringUtils.isNotBlank(metadata.getTags())) {
            detail.tag = metadata.getTags();
        }
        detail.bookExtraInfoBean = infoBean;
        return detail;
    }

    private List<Metadata> convertToMetadata(ObservableList<DataModel> items) {
        if (importBooks != null && importBooks.size() > 0) {
            importBooks.clear();
        }
        if (items != null && items.size() > 0) {
            for (int i = 0; i < items.size(); i++) {
                DataModel dataModel = items.get(i);
                Metadata metadata = new Metadata();
                metadata.setName(dataModel.title.get());
                metadata.setParentId(dataModel.parentId.get());
                metadata.setCloudId(dataModel.cloudId.get() + "");
                metadata.setTitle(dataModel.title.get());
                metadata.setIdString(dataModel.idString.get());
                metadata.setId(dataModel.id.get());
                metadata.setAuthors(dataModel.author.get());
                metadata.setSize((long) dataModel.size.get());
                metadata.setProgress(dataModel.progress.get());
                metadata.setDescription(dataModel.desc.get());
                metadata.setNativeAbsolutePath(dataModel.absolutePath.get());
                BookExtraInfoBean bean = new BookExtraInfoBean();
                bean.percentage = 100;
                metadata.setExtraAttributes(JSONObjectParseUtils.toJson(bean));
                importBooks.add(metadata);
            }
        }
        return importBooks;
    }
}
