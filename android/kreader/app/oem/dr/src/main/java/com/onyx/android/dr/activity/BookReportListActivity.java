package com.onyx.android.dr.activity;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.BookReportListAdapter;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.event.BringOutBookReportEvent;
import com.onyx.android.dr.event.DeleteBookReportEvent;
import com.onyx.android.dr.event.ShareBookReportEvent;
import com.onyx.android.dr.interfaces.BookReportView;
import com.onyx.android.dr.presenter.BookReportPresenter;
import com.onyx.android.dr.reader.event.RedrawPageEvent;
import com.onyx.android.dr.reader.view.DisableScrollGridManager;
import com.onyx.android.dr.util.DRPreferenceManager;
import com.onyx.android.dr.view.DividerItemDecoration;
import com.onyx.android.dr.view.PageRecyclerView;
import com.onyx.android.sdk.data.GPaginator;
import com.onyx.android.sdk.data.model.v2.CreateBookReportResult;
import com.onyx.android.sdk.data.model.v2.GetBookReportListBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    ImageView titleBarRightIconOne;
    @Bind(R.id.title_bar_right_icon_two)
    ImageView titleBarRightIconTwo;
    @Bind(R.id.title_bar_right_icon_three)
    ImageView titleBarRightIconThree;
    @Bind(R.id.title_bar_right_icon_four)
    ImageView titleBarRightIconFour;
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
    private BookReportListAdapter bookReportListAdapter;
    private GPaginator paginator;
    private List<GetBookReportListBean> list;
    private int currentPage = 1;
    private int pages;
    private BookReportPresenter bookReportPresenter;
    private String[] childrenId;
    private String bookName;
    private String bookPage;
    private String bookId;

    @Override
    protected Integer getLayoutId() {
        return R.layout.book_report_list_layout;
    }

    @Override
    protected void initConfig() {

    }

    @Override
    protected void initView() {
        titleBarTitle.setText(getResources().getString(R.string.reader_response));
        image.setImageResource(R.drawable.ic_reader_menu_idea);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setDrawLine(true);
        bookReportListRecycle.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        bookReportListRecycle.addItemDecoration(dividerItemDecoration);
        bookReportListAdapter = new BookReportListAdapter();
        bookReportListRecycle.setAdapter(bookReportListAdapter);
        paginator = bookReportListRecycle.getPaginator();
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
    }

    private void setCurrentPage(int prevPosition, int pageSize) {
        currentPage = prevPosition / pageSize + 1;
        setPage(currentPage);
    }

    @Override
    protected void initData() {
        bookReportPresenter = new BookReportPresenter(this);
        initIntentAndTitleData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bookReportPresenter.getImpressionsList();
    }

    private void initIntentAndTitleData() {
        int type = getIntent().getIntExtra(Constants.JUMP_SOURCE, -1);
        if (type == Constants.MY_NOTE_SOURCE) {
            titleBarRightIconFour.setVisibility(View.GONE);
        } else if (type == Constants.BOOK_SOURCE) {
            titleBarRightIconFour.setVisibility(View.VISIBLE);
            titleBarRightIconFour.setImageResource(R.drawable.ic_reader_note_diary_set);
            bookName = getIntent().getStringExtra(Constants.BOOK_NAME);
            bookPage = getIntent().getStringExtra(Constants.BOOK_PAGE);
            bookId = getIntent().getStringExtra(Constants.BOOK_ID);
        }
    }

    @OnClick({R.id.image_view_back,
            R.id.title_bar_title,
            R.id.book_report_list_page_left,
            R.id.title_bar_right_icon_four,
            R.id.book_report_list_page_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_view_back:
                finish();
                break;
            case R.id.title_bar_title:
                finish();
                break;
            case R.id.book_report_list_page_left:
                bookReportListRecycle.prevPage();
                break;
            case R.id.book_report_list_page_right:
                bookReportListRecycle.nextPage();
                break;
            case R.id.title_bar_right_icon_four:
                startToActivity();
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

    @Override
    public void setBookReportList(List<GetBookReportListBean> list) {
        this.list = list;
        setData(list);
    }

    private void setData(List<GetBookReportListBean> list) {
        if (bookReportListAdapter != null) {
            bookReportListAdapter.setData(list);
            String format = DRApplication.getInstance().getResources().getString(R.string.fragment_speech_recording_all_number);
            bookReportListTotalSize.setText(String.format(format, list.size()));
            initPage();
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
    public void onDeleteBookReportEvent(DeleteBookReportEvent event) {
        GetBookReportListBean bookReportBean = event.getBookReportBean();
        bookReportPresenter.deleteImpression(bookReportBean._id);
        list.remove(bookReportBean);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnBringOutBookReportEvent(BringOutBookReportEvent event) {
        GetBookReportListBean bookReportBean = event.getBookReportBean();
        bookReportPresenter.bringOutReport(bookReportBean);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShareBookReportEvent(ShareBookReportEvent event) {
        GetBookReportListBean bookReportBean = event.getBookReportBean();
        DRPreferenceManager.saveShareType(DRApplication.getInstance(), Constants.READER_RESPONSE);
        ActivityManager.startShareBookReportActivity(this, bookReportBean._id, childrenId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post(new RedrawPageEvent());
    }
}
