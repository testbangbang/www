package com.onyx.jdread.personal.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.onyx.android.sdk.data.GPaginator;
import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.OnyxPageDividerItemDecoration;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.PersonalBookBinding;
import com.onyx.jdread.library.model.PopMenuModel;
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

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by li on 2018/1/4.
 */

public class PersonalBookFragment extends BaseFragment {
    private PersonalBookBinding binding;
    private PersonalBookAdapter personalBookAdapter;
    private PersonalBookModel personalBookModel;
    private GPaginator paginator;

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
        if (!PersonalDataBundle.getInstance().getEventBus().isRegistered(this)) {
            PersonalDataBundle.getInstance().getEventBus().register(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        PersonalDataBundle.getInstance().getEventBus().unregister(this);
    }

    private void initView() {
        binding.personalBookRecycler.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        OnyxPageDividerItemDecoration decoration = new OnyxPageDividerItemDecoration(JDReadApplication.getInstance(), OnyxPageDividerItemDecoration.VERTICAL);
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
                BookDetailResultBean.Detail detail = covert(metadata);
                BookExtraInfoBean infoBean = detail.getBookExtraInfoBean();
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
            BookDetailResultBean.Detail bookDetail = ShopDataBundle.getInstance().getBookDetail();
            bookDetail.getBookExtraInfoBean().downLoadState = task.getStatus();
            bookDetail.getBookExtraInfoBean().localPath = task.getPath();
            bookDetail.setTag(event.tag);
            bookDetail.getBookExtraInfoBean().percentage = (int) (event.progressInfoModel.progress * 100);
            updateProgress(bookDetail);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void onDownloadFinishEvent(DownloadFinishEvent event) {
        BaseDownloadTask task = OnyxDownloadManager.getInstance().getTask(event.tag);
        if (task != null) {
            BookDetailResultBean.Detail bookDetail = ShopDataBundle.getInstance().getBookDetail();
            bookDetail.getBookExtraInfoBean().downLoadState = task.getStatus();
            bookDetail.getBookExtraInfoBean().localPath = task.getPath();
            bookDetail.getBookExtraInfoBean().percentage = DownLoadHelper.DOWNLOAD_PERCENT_FINISH;
            updateProgress(bookDetail);
        }
    }

    private synchronized void updateProgress(BookDetailResultBean.Detail detail) {
        if (personalBookAdapter == null) {
            return;
        }
        List<Metadata> data = personalBookAdapter.getData();
        for (int i = 0; i < data.size(); i++) {
            Metadata metadata = data.get(i);
            if (String.valueOf(detail.getEbookId()).equals(metadata.getCloudId())) {
                String infoBean = JSONObjectParseUtils.toJson(detail.getBookExtraInfoBean());
                metadata.setExtraAttributes(infoBean);
                metadata.setTags((String) detail.getTag());
                personalBookAdapter.notifyDataSetChanged();
                if (DownLoadHelper.canInsertBookDetail(detail.getBookExtraInfoBean().downLoadState)) {
                    BookshelfInsertAction action = new BookshelfInsertAction(detail, detail.getBookExtraInfoBean().localPath);
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
                break;
        }
    }

    private void getUnlimitedBooks() {
        final GetUnlimitedAction unlimitedAction = new GetUnlimitedAction();
        unlimitedAction.execute(PersonalDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                List<Metadata> unlimitedBooks = unlimitedAction.getUnlimitedBooks();
                if (unlimitedBooks != null) {
                    compareLocalMetadata(unlimitedBooks);
                }
            }
        });
    }

    private void getBoughtBooks() {
        final GetBoughtAction boughtAction = new GetBoughtAction();
        boughtAction.execute(PersonalDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                List<Metadata> boughtBooks = boughtAction.getBoughtBooks();
                if (boughtBooks != null) {
                    compareLocalMetadata(boughtBooks);
                }
            }
        });
    }

    private void queryAllCloud() {
        final QueryAllCloudMetadataAction queryAction = new QueryAllCloudMetadataAction();
        queryAction.execute(PersonalDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                List<Metadata> metadatas = queryAction.getMetadatas();
                setAdapterData(metadatas);
            }
        });
    }

    private void compareLocalMetadata(List<Metadata> list) {
        final CompareLocalMetadataAction action = new CompareLocalMetadataAction(list);
        action.execute(PersonalDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                List<Metadata> metadataList = action.getMetadataList();
                setAdapterData(metadataList);
            }
        });
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

    private BookDetailResultBean.Detail covert(Metadata metadata) {
        BookDetailResultBean.Detail detail = new BookDetailResultBean.Detail();
        detail.setName(metadata.getName());
        detail.setAuthor(metadata.getAuthors());
        detail.setEbookId(Integer.parseInt(metadata.getCloudId()));
        detail.setImageUrl(metadata.getCoverUrl());
        detail.setFileSize(metadata.getSize());
        detail.setDownLoadUrl(StringUtils.isNotBlank(metadata.getLocation()) ? metadata.getLocation() : null);
        String extraAttributes = metadata.getExtraAttributes();

        BookExtraInfoBean infoBean = null;
        if (StringUtils.isNullOrEmpty(extraAttributes)) {
            infoBean = new BookExtraInfoBean();
        } else {
            infoBean = JSONObjectParseUtils.toBean(extraAttributes, BookExtraInfoBean.class);
        }
        if (StringUtils.isNotBlank(metadata.getTags())) {
            detail.setTag(metadata.getTags());
        }
        detail.setBookExtraInfoBean(infoBean);
        return detail;
    }
}
