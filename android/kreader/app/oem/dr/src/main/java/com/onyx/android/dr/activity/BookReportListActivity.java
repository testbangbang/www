package com.onyx.android.dr.activity;

import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.BookReportListAdapter;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.event.ExportHtmlFailedEvent;
import com.onyx.android.dr.event.ExportHtmlSuccessEvent;
import com.onyx.android.dr.interfaces.BookReportView;
import com.onyx.android.dr.presenter.BookReportPresenter;
import com.onyx.android.dr.reader.event.RedrawPageEvent;
import com.onyx.android.dr.reader.view.DisableScrollGridManager;
import com.onyx.android.dr.view.DividerItemDecoration;
import com.onyx.android.dr.view.PageRecyclerView;
import com.onyx.android.sdk.data.GPaginator;
import com.onyx.android.sdk.data.model.v2.CreateBookReportResult;
import com.onyx.android.sdk.data.model.v2.GetBookReportListBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by li on 2017/9/15.
 */

public class BookReportListActivity extends BaseActivity implements BookReportView {
    @Bind(R.id.image_view_back)
    ImageView imageViewBack;
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.title_bar_title)
    TextView titleBarTitle;
    @Bind(R.id.menu_back)
    LinearLayout menuBack;
    @Bind(R.id.title_bar_right_select_time)
    TextView titleBarRightSelectTime;
    @Bind(R.id.title_bar_right_icon_one)
    ImageView iconOne;
    @Bind(R.id.title_bar_right_icon_two)
    ImageView iconTwo;
    @Bind(R.id.title_bar_right_icon_three)
    ImageView iconThree;
    @Bind(R.id.title_bar_right_icon_four)
    ImageView iconFour;
    @Bind(R.id.title_bar_right_shopping_cart)
    TextView titleBarRightShoppingCart;
    @Bind(R.id.title_bar_right_container)
    LinearLayout titleBarRightContainer;
    @Bind(R.id.title_bar_right_image)
    ImageView titleBarRightImage;
    @Bind(R.id.title_bar_right_edit_text)
    EditText titleBarRightEditText;
    @Bind(R.id.title_bar_right_menu)
    TextView titleBarRightMenu;
    @Bind(R.id.book_report_list_recycle)
    PageRecyclerView bookReportListRecycle;
    @Bind(R.id.book_report_list_total_size)
    TextView bookReportListTotalSize;
    @Bind(R.id.book_report_list_page)
    TextView bookReportListPage;
    @Bind(R.id.book_report_list_page_left)
    ImageButton pageLeft;
    @Bind(R.id.book_report_list_page_right)
    ImageButton PageRight;
    @Bind(R.id.book_report_list_all_check)
    CheckBox allCheck;
    private BookReportListAdapter bookReportListAdapter;
    private GPaginator paginator;
    private List<GetBookReportListBean> list;
    private int currentPage = 1;
    private int pages;
    private BookReportPresenter bookReportPresenter;
    private String bookName;
    private String bookPage;
    private String bookId;
    private List<Boolean> listCheck;

    @Override
    protected Integer getLayoutId() {
        return R.layout.book_report_list_layout;
    }

    @Override
    protected void initConfig() {
    }

    @Override
    protected void initView() {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setDrawLine(true);
        bookReportListRecycle.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        bookReportListRecycle.addItemDecoration(dividerItemDecoration);
        bookReportListAdapter = new BookReportListAdapter();
        bookReportListRecycle.setAdapter(bookReportListAdapter);
        paginator = bookReportListRecycle.getPaginator();
    }

    @Override
    protected void initData() {
        bookReportPresenter = new BookReportPresenter(this);
        list = new ArrayList<>();
        listCheck = new ArrayList<>();
        initIntentAndTitleData();
        initListener();
    }

    private void initListener() {
        bookReportListRecycle.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPrevPage(int prevPosition, int itemCount, int pageSize) {
                setCurrentPage(prevPosition, pageSize);
            }

            @Override
            public void onNextPage(int nextPosition, int itemCount, int pageSize) {
                setCurrentPage(nextPosition, pageSize);
            }
        });
        bookReportListAdapter.setOnItemListener(new BookReportListAdapter.OnItemClickListener() {
            @Override
            public void setOnItemClick(int position, boolean isCheck) {
                listCheck.set(position, isCheck);
            }

            @Override
            public void setOnItemCheckedChanged(int position, boolean isCheck) {
                listCheck.set(position, isCheck);
            }
        });
        allCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                if (isCheck) {
                    for (int i = 0, j = list.size(); i < j; i++) {
                        listCheck.set(i, true);
                    }
                } else {
                    for (int i = 0, j = list.size(); i < j; i++) {
                        listCheck.set(i, false);
                    }
                }
                bookReportListAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setCurrentPage(int prevPosition, int pageSize) {
        currentPage = prevPosition / pageSize + 1;
        setPage(currentPage);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bookReportPresenter.getImpressionsList();
    }

    private void initIntentAndTitleData() {
        int type = getIntent().getIntExtra(Constants.JUMP_SOURCE, -1);
        if (type == Constants.MY_NOTE_SOURCE) {
            iconOne.setVisibility(View.GONE);
        } else if (type == Constants.BOOK_SOURCE) {
            iconOne.setVisibility(View.VISIBLE);
            iconOne.setImageResource(R.drawable.ic_reader_note_diary_set);
            bookName = getIntent().getStringExtra(Constants.BOOK_NAME);
            bookPage = getIntent().getStringExtra(Constants.BOOK_PAGE);
            bookId = getIntent().getStringExtra(Constants.BOOK_ID);
        }
        titleBarTitle.setText(getResources().getString(R.string.reader_response));
        image.setImageResource(R.drawable.ic_reader_menu_idea);
        iconFour.setVisibility(View.VISIBLE);
        iconThree.setVisibility(View.VISIBLE);
        iconTwo.setVisibility(View.VISIBLE);
        iconFour.setImageResource(R.drawable.ic_reader_note_delet);
        iconThree.setImageResource(R.drawable.ic_reader_share);
        iconTwo.setImageResource(R.drawable.ic_reader_note_export);
    }


    @Override
    public void setBookReportList(List<GetBookReportListBean> list, List<Boolean> listCheck) {
        if (list != null && listCheck != null) {
            this.list = list;
            this.listCheck = listCheck;
        }
        setData(list);
    }

    private void setData(List<GetBookReportListBean> list) {
        if (bookReportListAdapter != null) {
            bookReportListAdapter.setData(list, listCheck);
            String format = DRApplication.getInstance().getResources().getString(R.string.fragment_speech_recording_all_number);
            bookReportListTotalSize.setText(String.format(format, list.size()));
            initPage();
        }
    }

    @OnClick({R.id.menu_back,
            R.id.book_report_list_page_left,
            R.id.title_bar_right_icon_one,
            R.id.title_bar_right_icon_two,
            R.id.title_bar_right_icon_three,
            R.id.title_bar_right_icon_four,
            R.id.book_report_list_page_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.menu_back:
                finish();
                break;
            case R.id.book_report_list_page_left:
                bookReportListRecycle.prevPage();
                break;
            case R.id.book_report_list_page_right:
                bookReportListRecycle.nextPage();
                break;
            case R.id.title_bar_right_icon_one:
                startToActivity();
                break;
            case R.id.title_bar_right_icon_two:
                exportData();
                break;
            case R.id.title_bar_right_icon_three:
                bookReportPresenter.shareReadingRate(listCheck, list);
                break;
            case R.id.title_bar_right_icon_four:
                deleteCheckedData();
                break;
        }
    }

    private void startToActivity() {
        Intent intent = new Intent();
        intent.putExtra(Constants.BOOK_NAME, bookName);
        intent.putExtra(Constants.BOOK_PAGE, bookPage);
        intent.putExtra(Constants.BOOK_ID, bookId);
        ActivityManager.startReadingReportActivity(this, intent);
    }

    private void deleteCheckedData() {
        if (list.size() > 0) {
            bookReportPresenter.remoteAdapterData(listCheck, bookReportListAdapter, list);
        } else {
            CommonNotices.showMessage(this, getString(R.string.no_relevant_data));
        }
    }

    private void exportData() {
        if (list.size() > 0) {
            ArrayList<String> htmlTitleData = bookReportPresenter.getHtmlTitleData();
            bookReportPresenter.exportDataToHtml(listCheck, htmlTitleData, list);
        } else {
            CommonNotices.showMessage(this, getString(R.string.no_relevant_data));
        }
    }

    @Override
    public void setDeleteResult() {
        setData(list);
    }

    @Override
    public void getBookReport(CreateBookReportResult result) {
    }

    @Override
    public void addCommentResult(CreateBookReportResult result) {
    }

    @Override
    public void setLibraryId(String bookId, String libraryId) {
    }

    @Override
    public void saveBookReportData(CreateBookReportResult createBookReportResult) {
    }

    private void initPage() {
        paginator.resize(bookReportListAdapter.getRowCount(), bookReportListAdapter.getColumnCount(), list.size());
        pages = paginator.pages();
        setPage(currentPage);
    }

    private void setPage(int current) {
        bookReportListPage.setText(current + "/" + (current > pages ? 1 : pages));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExportHtmlSuccessEvent(ExportHtmlSuccessEvent event) {
        CommonNotices.showMessage(this, getString(R.string.export_success));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExportHtmlFailedEvent(ExportHtmlFailedEvent event) {
        CommonNotices.showMessage(this, getString(R.string.export_failed));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post(new RedrawPageEvent());
    }
}
