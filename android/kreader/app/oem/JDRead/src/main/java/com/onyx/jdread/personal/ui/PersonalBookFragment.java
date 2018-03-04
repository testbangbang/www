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
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.Metadata_Table;
import com.onyx.android.sdk.data.model.ModelType;
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
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.personal.action.CompareLocalMetadataAction;
import com.onyx.jdread.personal.action.GetBoughtAction;
import com.onyx.jdread.personal.action.GetUnlimitedAction;
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
    private GPaginator paginator;
    private List<PersonalBookBean> allBook = new ArrayList<>();
    private List<PersonalBookBean> importBooks = new ArrayList<>();
    private List<PersonalBookBean> boughtBooks;
    private List<PersonalBookBean> unlimitedBooks;

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

                if (ResManager.getString(R.string.self_import).equals(binding.getFilterName()) || infoBean.percentage == DownLoadHelper.DOWNLOAD_PERCENT_FINISH) {
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
                    DownLoadHelper.stopDownloadingTask(metadata.getTags());
                    infoBean.downLoadState = DownLoadHelper.getPausedState();
                    updateProgress(infoBean, metadata.getTags());
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

    private void openBook(String localPath, BookDetailResultBean.DetailBean detailBean) {
        DocumentInfo documentInfo = new DocumentInfo();
        DocumentInfo.SecurityInfo securityInfo = new DocumentInfo.SecurityInfo();
        securityInfo.setKey(detailBean.key);
        securityInfo.setRandom(detailBean.random);
        documentInfo.setBookName(detailBean.name);
        securityInfo.setUuId(DrmTools.getHardwareId(Build.SERIAL));
        documentInfo.setSecurityInfo(securityInfo);
        documentInfo.setBookPath(localPath);
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
            infoBean.percentage = DownLoadHelper.DOWNLOAD_PERCENT_FINISH;
            updateProgress(infoBean, String.valueOf(event.tag));
        }
    }

    private synchronized void updateProgress(BookExtraInfoBean info, String tag) {
        if (personalBookAdapter == null) {
            return;
        }
        List<PersonalBookBean> data = personalBookAdapter.getData();
        for (int i = 0; i < data.size(); i++) {
            Metadata metadata = data.get(i).metadata;
            if (tag.equals(metadata.getName())) {
                String infoBean = JSONObjectParseUtils.toJson(info);
                metadata.setDownloadInfo(infoBean);
                metadata.setTags(tag);
                personalBookAdapter.notifyItem(i);
                if (DownLoadHelper.canInsertBookDetail(info.downLoadState)) {
                    BookDetailResultBean.DetailBean detail = covert(metadata);
                    BookshelfInsertAction action = new BookshelfInsertAction(detail, info.localPath);
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
            case R.string.all:
                binding.setFilterName(ResManager.getString(R.string.all));
                queryAllCloud();
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
        QueryArgs queryArgs = QueryBuilder.allBooksQuery(SortBy.None, SortOrder.Asc);
        queryArgs.conditionGroup.and(Metadata_Table.cloudId.isNull());
        final RxMetadataLoadAction action = new RxMetadataLoadAction(queryArgs);
        action.execute(LibraryDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                LibraryViewDataModel libraryViewDataModel = LibraryDataBundle.getInstance().getLibraryViewDataModel();
                ObservableList<DataModel> items = libraryViewDataModel.items;
                List<PersonalBookBean> datas = convertToMetadata(items);
                if (datas != null) {
                    setAdapterData(datas);
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
        if (allBook != null && allBook.size() > 0) {
            setAdapterData(allBook);
            return;
        }
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

    private void compareLocalMetadata(List<PersonalBookBean> list, final boolean isAll) {
        final CompareLocalMetadataAction action = new CompareLocalMetadataAction(list);
        action.execute(PersonalDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                List<PersonalBookBean> metadataList = action.getMetadataList();
                if (metadataList != null) {
                    allBook.addAll(metadataList);
                }
                if (isAll) {
                    List<PersonalBookBean> list = deleteRepeat(allBook);
                    setAdapterData(list);
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

    private void setAdapterData(List<PersonalBookBean> datas) {
        if (personalBookAdapter != null) {
            personalBookAdapter.setData(datas);
            binding.personalBookRecycler.scrollToPosition(0);
            paginator.setCurrentPage(0);
            binding.personalBookRecycler.resize(personalBookAdapter.getRowCount(), personalBookAdapter.getColumnCount(), datas.size());
            String progressText = paginator.getProgressText();
            binding.setPage(progressText);
            binding.setBooks(datas.size());
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
                metadata.setDownloadInfo(JSONObjectParseUtils.toJson(bean));
                bookBean.metadata = metadata;
                bookBean.bitmap = dataModel.coverBitmap.get();
                importBooks.add(bookBean);
            }
        }
        return importBooks;
    }
}
