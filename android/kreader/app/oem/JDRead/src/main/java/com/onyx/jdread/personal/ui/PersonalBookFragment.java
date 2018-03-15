package com.onyx.jdread.personal.ui;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableList;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jingdong.app.reader.data.DrmTools;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.onyx.android.sdk.data.GPaginator;
import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.ModelType;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.PersonalBookBinding;
import com.onyx.jdread.library.model.PopMenuModel;
import com.onyx.jdread.library.ui.SearchBookFragment;
import com.onyx.jdread.library.view.DashLineItemDivider;
import com.onyx.jdread.library.view.MenuPopupWindow;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.personal.action.CompareLocalMetadataAction;
import com.onyx.jdread.personal.action.GetBoughtAction;
import com.onyx.jdread.personal.action.GetUnlimitedAction;
import com.onyx.jdread.personal.action.ImportAction;
import com.onyx.jdread.personal.action.QueryAllCloudMetadataAction;
import com.onyx.jdread.personal.adapter.PersonalBookAdapter;
import com.onyx.jdread.personal.cloud.entity.jdbean.PersonalBookBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.UserInfo;
import com.onyx.jdread.personal.event.FilterAllEvent;
import com.onyx.jdread.personal.event.FilterHaveBoughtEvent;
import com.onyx.jdread.personal.event.FilterReadVipEvent;
import com.onyx.jdread.personal.event.FilterSelfImportEvent;
import com.onyx.jdread.personal.model.PersonalBookModel;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.reader.common.DocumentInfo;
import com.onyx.jdread.reader.common.OpenBookHelper;
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
import java.util.List;

/**
 * Created by li on 2018/1/4.
 */

public class PersonalBookFragment extends BaseFragment {
    private PersonalBookBinding binding;
    private PersonalBookAdapter personalBookAdapter;
    private PersonalBookModel personalBookModel;
    private List<PersonalBookBean> allBook = new ArrayList<>();
    private List<PersonalBookBean> importBooks = new ArrayList<>();
    private List<PersonalBookBean> boughtBooks = new ArrayList<>();
    private List<PersonalBookBean> unlimitedBooks = new ArrayList<>();
    private int currentBoughtPage = 1;
    private int currentUnLimitedPage = 1;
    private int boughtTotalPage;
    private GPaginator paginator;
    private long boughtTotals;
    private long unlimitedTotals;
    private int unlimitedTotalPage;
    private long localTotal;
    private int currentId;
    private long total;
    private int offset;
    private int currentPage;

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
    }

    @Override
    public void onDestroy() {
        if (boughtBooks != null) {
            boughtBooks.clear();
        }
        if (unlimitedBooks != null) {
            unlimitedBooks.clear();
        }
        if (importBooks != null) {
            importBooks.clear();
        }
        if (allBook != null) {
            allBook.clear();
        }
        currentBoughtPage = 1;
        currentUnLimitedPage = 1;
        offset = 0;
        super.onDestroy();
    }

    private void initView() {
        binding.personalBookRecycler.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
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
        personalBookModel.setQueryPagination(paginator);
        setFilter(R.string.all);
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
                UserInfo userInfo = PersonalDataBundle.getInstance().getUserInfo();
                List<PersonalBookBean> data = personalBookAdapter.getData();
                Metadata metadata = data.get(position).metadata;

                BookDetailResultBean.DetailBean detail = covert(metadata);
                BookExtraInfoBean infoBean = detail.bookExtraInfoBean;

                if (ResManager.getString(R.string.self_import).equals(binding.getFilterName()) ||
                        DownLoadHelper.isDownloaded(infoBean.downLoadState) || StringUtils.isNullOrEmpty(metadata.getCloudId())) {
                    openBook(metadata.getNativeAbsolutePath(), detail);
                    return;
                }
                if (metadata.getOrdinal() != 0 && userInfo != null && userInfo.vip_remain_days <= 0) {
                    ToastUtil.showToast(ResManager.getString(R.string.membership_expired));
                    return;
                }
                if (DownLoadHelper.isDownloading(infoBean.downLoadState) ||
                        DownLoadHelper.isStarted(infoBean.downLoadState) ||
                        DownLoadHelper.isConnected(infoBean.downLoadState)) {
                    DownLoadHelper.stopDownloadingTask(metadata.getCloudId() + Constants.WHOLE_BOOK_DOWNLOAD_TAG);
                    infoBean.downLoadState = DownLoadHelper.getPausedState();
                    updateProgress(infoBean, metadata.getCloudId());
                    return;
                }

                BaseDownloadTask task = OnyxDownloadManager.getInstance().getTask(metadata.getCloudId() + Constants.WHOLE_BOOK_DOWNLOAD_TAG);
                if (task != null) {
                    OnyxDownloadManager.getInstance().removeTask(metadata.getCloudId() + Constants.WHOLE_BOOK_DOWNLOAD_TAG);
                }
                BookDownloadUtils.download(detail, ShopDataBundle.getInstance(), null);
            }
        });

        binding.personalBookRecycler.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPageChange(int position, int itemCount, int pageSize) {
                currentPage = position / pageSize + 1;
                receiveData(itemCount / pageSize);
            }
        });

        binding.personalBookRecycler.setOnArrayEndPageListener(new PageRecyclerView.OnArrayEndPageListener() {
            @Override
            public void onArrayEndPage() {
                if (currentPage == pages((int) total)) {
                    binding.personalBookRecycler.gotoPage(0);
                }
            }
        });
    }

    private void receiveData(int pages) {
        setPage(currentPage);
        if (currentPage == pages) {
            getBoughtBooks();
        }
    }

    private void openBook(String localPath, BookDetailResultBean.DetailBean detailBean) {
        DocumentInfo documentInfo = new DocumentInfo();
        DocumentInfo.SecurityInfo securityInfo = new DocumentInfo.SecurityInfo();
        securityInfo.setKey(detailBean.key);
        securityInfo.setRandom(detailBean.random);
        documentInfo.setBookName(detailBean.name);
        securityInfo.setUuId(DrmTools.getHardwareId(Build.SERIAL));
        documentInfo.setSecurityInfo(securityInfo);
        documentInfo.setBookPath(localPath);
        documentInfo.setWholeBookDownLoad(detailBean.bookExtraInfoBean.isWholeBookDownLoad);
        documentInfo.setCloudId(detailBean.ebook_id);
        OpenBookHelper.openBook(super.getContext(), documentInfo);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void onDownloadingEvent(DownloadingEvent event) {
        BaseDownloadTask task = OnyxDownloadManager.getInstance().getTask(event.tag);
        if (task != null) {
            BookExtraInfoBean infoBean = new BookExtraInfoBean();
            infoBean.downLoadState = task.getStatus();
            infoBean.localPath = task.getPath();
            infoBean.percentage = (int) (event.progressInfoModel.progress * 100);
            updateProgress(infoBean, String.valueOf(event.tag));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void onDownloadFinishEvent(DownloadFinishEvent event) {
        BaseDownloadTask task = OnyxDownloadManager.getInstance().getTask(event.tag);
        if (task != null) {
            BookExtraInfoBean infoBean = new BookExtraInfoBean();
            infoBean.downLoadState = task.getStatus();
            infoBean.localPath = task.getPath();
            if (DownLoadHelper.isDownloaded(task.getStatus())) {
                infoBean.percentage = DownLoadHelper.DOWNLOAD_PERCENT_FINISH;
            }
            updateProgress(infoBean, String.valueOf(event.tag));
        }
    }

    private synchronized void updateProgress(BookExtraInfoBean info, String tag) {
        if (personalBookAdapter == null) {
            return;
        }
        if (tag.contains(Constants.WHOLE_BOOK_DOWNLOAD_TAG)) {
            tag = tag.replace(Constants.WHOLE_BOOK_DOWNLOAD_TAG, "");
        }
        List<PersonalBookBean> data = personalBookAdapter.getData();
        for (int i = 0; i < data.size(); i++) {
            Metadata metadata = data.get(i).metadata;
            if (tag.equals(metadata.getCloudId())) {
                BookDetailResultBean.DetailBean bookDetail = ShopDataBundle.getInstance().getBookDetail();
                BookExtraInfoBean bean = null;
                String downloadInfo = metadata.getDownloadInfo();
                info.downLoadTaskTag = tag + Constants.WHOLE_BOOK_DOWNLOAD_TAG;
                info.downloadUrl = bookDetail.downLoadUrl;
                info.isWholeBookDownLoad = true;
                if (StringUtils.isNotBlank(downloadInfo)) {
                    bean = JSONObjectParseUtils.toBean(downloadInfo, BookExtraInfoBean.class);
                    bean.downLoadState = info.downLoadState;
                    bean.localPath = info.localPath;
                    bean.percentage = info.percentage == 0 ? bean.percentage : info.percentage;
                    bean.downLoadTaskTag = info.downLoadTaskTag;
                    bean.isWholeBookDownLoad = info.isWholeBookDownLoad;
                    if (bookDetail.ebook_id == PagePositionUtils.getPosition(metadata.getCloudId())) {
                        bean.key = bookDetail.key;
                        bean.random = bookDetail.random;
                    }
                }

                String infoBean = JSONObjectParseUtils.toJson(bean == null ? info : bean);
                metadata.setDownloadInfo(infoBean);
                metadata.setTags(tag);
                metadata.setNativeAbsolutePath(info.localPath);
                personalBookAdapter.notifyItem(i);
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
        this.currentId = id;
        binding.personalBookRecycler.scrollToPosition(0);
        paginator.setCurrentPage(0);
        switch (id) {
            case R.string.all:
                binding.setFilterName(ResManager.getString(R.string.all));
                getBoughtBooks();
                break;
            case R.string.have_bought:
                binding.setFilterName(ResManager.getString(R.string.have_bought));
                getBoughtBooks();
                break;
            case R.string.read_vip:
                binding.setFilterName(ResManager.getString(R.string.read_vip));
                getUnlimitedBooks();
                break;
            case R.string.self_import:
                binding.setFilterName(ResManager.getString(R.string.self_import));
                getSelfImportBooks();
                break;
        }
    }

    private void getSelfImportBooks() {
        if (importBooks != null && importBooks.size() > 0 && importBooks.size() >= localTotal) {
            compareLocalMetadata(null, false);
            return;
        }
        QueryArgs queryArgs = personalBookModel.getQueryArgs(offset);
        final ImportAction action = new ImportAction(queryArgs);
        action.execute(PersonalDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                List<PersonalBookBean> localBooks = action.getBooks();
                if (localBooks != null && localBooks.size() > 0) {
                    importBooks.addAll(localBooks);
                    allBook.addAll(localBooks);
                    PersonalBookBean bookBean = localBooks.get(0);
                    localTotal = bookBean.total;
                    if (localBooks.size() < localTotal) {
                        offset = personalBookModel.getOffset();
                    }
                }
                compareLocalMetadata(null, false);
            }
        });
    }

    private void getUnlimitedBooks() {
        if (unlimitedBooks != null && unlimitedBooks.size() > 0 && unlimitedBooks.size() >= unlimitedTotals) {
            getSelfImportBooks();
            return;
        }
        final GetUnlimitedAction unlimitedAction = new GetUnlimitedAction(currentUnLimitedPage);
        unlimitedAction.execute(PersonalDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                List<PersonalBookBean> books = unlimitedAction.getUnlimitedBooks();
                if (books != null && books.size() > 0) {
                    unlimitedTotals = books.get(0).total;
                    unlimitedTotalPage = books.get(0).total_page;
                    if (currentUnLimitedPage < unlimitedTotalPage) {
                        currentUnLimitedPage++;
                    }
                    compareLocalMetadata(books, false);
                }
                getSelfImportBooks();
            }
        });
    }

    private void getBoughtBooks() {
        if (boughtBooks != null && boughtBooks.size() > 0 && boughtBooks.size() == boughtTotals) {
            getUnlimitedBooks();
            return;
        }
        final GetBoughtAction boughtAction = new GetBoughtAction(currentBoughtPage);
        boughtAction.execute(PersonalDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                List<PersonalBookBean> books = boughtAction.getBoughtBooks();
                if (books != null && books.size() > 0) {
                    boughtTotalPage = books.get(0).total_page;
                    boughtTotals = books.get(0).total;
                    if (currentBoughtPage < boughtTotalPage) {
                        currentBoughtPage++;
                    }
                    compareLocalMetadata(books, true);
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
                List<PersonalBookBean> books = queryAction.getBooks();
                if (books != null && books.size() > 0) {
                    allBook.addAll(books);
                }
                getBoughtBooks();
            }
        });
    }

    private void compareLocalMetadata(List<PersonalBookBean> list, final boolean isBought) {
        if (list == null) {
            setAdapterData();
            return;
        }
        final CompareLocalMetadataAction action = new CompareLocalMetadataAction(list);
        action.execute(PersonalDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                List<PersonalBookBean> metadataList = action.getMetadataList();
                if (metadataList != null) {
                    allBook.addAll(metadataList);
                    if (isBought) {
                        boughtBooks.addAll(metadataList);
                    } else {
                        unlimitedBooks.addAll(metadataList);
                    }
                }
                if (currentId != R.string.all) {
                    setAdapterData();
                }
            }
        });
    }

    private List<PersonalBookBean> deleteRepeat(List<PersonalBookBean> list) {
        List<PersonalBookBean> data = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            PersonalBookBean bookBean = list.get(i);
            if (ids.contains(bookBean.metadata.getCloudId())) {
                continue;
            }
            ids.add(bookBean.metadata.getCloudId());
            data.add(bookBean);
        }
        list.clear();
        list.addAll(data);
        return list;
    }

    private void setAdapterData() {
        if (personalBookAdapter != null) {
            switch (currentId) {
                case R.string.all:
                    total = boughtTotals + unlimitedTotals + localTotal;
                    setData(allBook);
                    break;
                case R.string.have_bought:
                    total = boughtTotals;
                    setData(boughtBooks);
                    break;
                case R.string.read_vip:
                    total = unlimitedTotals;
                    setData(unlimitedBooks);
                    break;
                case R.string.self_import:
                    total = localTotal;
                    setData(importBooks);
                    break;
            }
        }
    }

    private void setData(List<PersonalBookBean> data) {
        personalBookAdapter.setData(data);
        binding.personalBookRecycler.resize(personalBookAdapter.getRowCount(),
                personalBookAdapter.getColumnCount(), data.size());
        int visibleCurrentPage = paginator.getVisibleCurrentPage();
        setPage(visibleCurrentPage);
    }

    public int pages(int total) {
        int itemsPerPage = ResManager.getInteger(R.integer.personal_book_row) * ResManager.getInteger(R.integer.personal_book_col);
        int pages = total / itemsPerPage;
        if (pages * itemsPerPage < total) {
            return pages + 1;
        }
        return pages;
    }

    private BookDetailResultBean.DetailBean covert(Metadata metadata) {
        BookDetailResultBean.DetailBean detail = new BookDetailResultBean.DetailBean();
        detail.name = metadata.getName();
        detail.author = metadata.getAuthors();
        detail.ebook_id = PagePositionUtils.getPosition(metadata.getCloudId());
        detail.image_url = metadata.getCoverUrl();
        detail.file_size = metadata.getSize();
        detail.downLoadUrl = StringUtils.isNotBlank(metadata.getLocation()) ? metadata.getLocation() : null;
        detail.format = metadata.getType();
        String downloadInfo = metadata.getDownloadInfo();

        BookExtraInfoBean infoBean = null;
        if (StringUtils.isNullOrEmpty(downloadInfo)) {
            infoBean = new BookExtraInfoBean();
        } else {
            infoBean = JSONObjectParseUtils.toBean(downloadInfo, BookExtraInfoBean.class);
            detail.key = infoBean.key;
            detail.random = infoBean.random;
        }
        if (StringUtils.isNotBlank(metadata.getTags())) {
            detail.tag = metadata.getTags();
        }
        detail.bookExtraInfoBean = infoBean;
        return detail;
    }

    private List<PersonalBookBean> convertToMetadata(ObservableList<DataModel> items) {
        if (importBooks != null && importBooks.size() > 0) {
            importBooks.clear();
        }
        if (items != null && items.size() > 0) {
            for (int i = 0; i < items.size(); i++) {
                DataModel dataModel = items.get(i);
                if (ModelType.TYPE_LIBRARY.ordinal() == dataModel.type.get().ordinal()) {
                    continue;
                }
                PersonalBookBean bookBean = new PersonalBookBean();
                Metadata metadata = new Metadata();
                metadata.setName(dataModel.title.get());
                metadata.setParentId(dataModel.parentId.get());
                metadata.setCloudId(dataModel.cloudId.get());
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
                metadata.setDownloadInfo(JSONObjectParseUtils.toJson(bean));
                bookBean.metadata = metadata;
                bookBean.bitmap = dataModel.coverBitmap.get();
                importBooks.add(bookBean);
            }
        }
        return importBooks;
    }

    public void setPage(int current) {
        binding.setPage(current + "/" + pages((int) total));
        binding.setBooks((int) total);
    }
}
