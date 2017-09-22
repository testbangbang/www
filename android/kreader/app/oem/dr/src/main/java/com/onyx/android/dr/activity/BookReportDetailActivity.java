package com.onyx.android.dr.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.dr.R;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.interfaces.BookReportView;
import com.onyx.android.dr.presenter.BookReportPresenter;
import com.onyx.android.sdk.data.model.v2.CommentsBean;
import com.onyx.android.sdk.data.model.v2.CreateBookReportResult;
import com.onyx.android.sdk.data.model.v2.GetBookReportListBean;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.Serializable;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by li on 2017/9/15.
 */

public class BookReportDetailActivity extends BaseActivity implements BookReportView {
    @Bind(R.id.image_view_back)
    ImageView imageViewBack;
    @Bind(R.id.title_bar_title)
    TextView titleBarTitle;
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
    @Bind(R.id.title_bar_right_image)
    ImageView titleBarRightImage;
    @Bind(R.id.title_bar_right_edit_text)
    EditText titleBarRightEditText;
    @Bind(R.id.title_bar_right_menu)
    TextView titleBarRightMenu;
    @Bind(R.id.book_report_detail_title)
    TextView bookReportDetailTitle;
    @Bind(R.id.book_report_detail_contents)
    EditText bookReportDetailContents;
    private Button dialogCancel;
    private Button dialogSure;
    private AlertDialog dialog;
    private GetBookReportListBean data;
    private String bookName;
    private String bookPage;
    private String bookId;
    private BookReportPresenter bookReportPresenter;
    private boolean isSave = false;

    @Override
    protected Integer getLayoutId() {
        return R.layout.book_report_detail_layout;
    }

    @Override
    protected void initConfig() {
        bookReportPresenter = new BookReportPresenter(this);
    }

    @Override
    protected void initView() {
        titleBarTitle.setText(getResources().getString(R.string.reader_response));
        titleBarRightSelectTime.setVisibility(View.VISIBLE);
        titleBarRightSelectTime.setText(getResources().getString(R.string.Call_the_modified_file));
        titleBarRightIconOne.setVisibility(View.VISIBLE);
        titleBarRightIconOne.setImageResource(R.drawable.ic_reader_note_export);
        titleBarRightIconTwo.setVisibility(View.VISIBLE);
        titleBarRightIconTwo.setImageDrawable(getResources().getDrawable(R.drawable.ic_reader_note_diary_save));
        titleBarRightIconThree.setVisibility(View.VISIBLE);
        titleBarRightIconThree.setImageDrawable(getResources().getDrawable(R.drawable.ic_reader_share));
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        Serializable serializableExtra = intent.getSerializableExtra(Constants.BOOK_REPORT_DATA);
        if(serializableExtra != null) {
            data = (GetBookReportListBean) serializableExtra;
            bookReportDetailContents.setText(data.content);
            bookReportDetailTitle.setText(data.name);
            List<CommentsBean> comments = data.comments;
            if(comments != null && comments.size() > 0) {
                //TODO:webView
            }
        }

        bookName = intent.getStringExtra(Constants.BOOK_NAME);
        bookPage = intent.getStringExtra(Constants.BOOK_PAGE);
        bookId = intent.getStringExtra(Constants.BOOK_ID);
        if(!StringUtils.isNullOrEmpty(bookName)) {
            bookReportDetailTitle.setText(bookName);
        }
        initListener();
    }

    private void initListener() {
        bookReportDetailContents.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                return false;
            }
        });
    }

    @OnClick({R.id.image_view_back, R.id.title_bar_title, R.id.title_bar_right_select_time,
            R.id.title_bar_right_icon_one, R.id.title_bar_right_icon_two, R.id.title_bar_right_icon_three,
            R.id.title_bar_right_icon_four, R.id.title_bar_right_shopping_cart, R.id.title_bar_right_image, R.id.title_bar_right_edit_text, R.id.title_bar_right_menu,
            R.id.book_report_detail_title})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_view_back:
                backActivity();
                break;
            case R.id.title_bar_title:
                backActivity();
                break;
            case R.id.title_bar_right_select_time:
                break;
            case R.id.title_bar_right_icon_one:
                break;
            case R.id.title_bar_right_icon_two:
                save();
                break;
            case R.id.title_bar_right_icon_three:
                break;
            case R.id.title_bar_right_icon_four:
                break;
            case R.id.title_bar_right_shopping_cart:
                break;
            case R.id.title_bar_right_image:
                break;
            case R.id.title_bar_right_edit_text:
                showMarks();
                break;
            case R.id.title_bar_right_menu:
                break;
            case R.id.book_report_detail_title:
                break;
        }
    }

    private void showMarks() {
        if(data != null) {
            //TODO:
        }
    }

    private void save() {
        String text = bookReportDetailContents.getText().toString();
        if(StringUtils.isNullOrEmpty(text)) {
            CommonNotices.showMessage(this, getResources().getString(R.string.no_content));
            return;
        }

        if(!StringUtils.isNullOrEmpty(bookName) && !StringUtils.isNullOrEmpty(bookId) && !StringUtils.isNullOrEmpty(bookPage)) {
            bookReportPresenter.createImpression(bookId, bookName, text, bookPage);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            //TODO: show dialog to comfirm save content
            backActivity();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    private void backActivity() {
        if(isSave) {
            finish();
        }else {
            showTipDiaolog();
        }
    }

    private void showTipDiaolog() {
        dialog = new AlertDialog.Builder(this).create();
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(R.layout.tip_save_reader_report_layout);
        dialogCancel = (Button) window.findViewById(R.id.tip_dialog_cancel);
        dialogSure = (Button) window.findViewById(R.id.tip_dialog_sure);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        initDialogListener();
    }

    private void initDialogListener() {
        dialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialogSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
                dialog.dismiss();
            }
        });
    }

    @Override
    public void setCreateBookReportData(CreateBookReportResult result) {
        isSave = true;
    }

    @Override
    public void setBookReportList(List<GetBookReportListBean> list) {

    }

    @Override
    public void setDeleteResult() {

    }
}
