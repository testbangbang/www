package com.onyx.android.dr.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.event.WebViewJSEvent;
import com.onyx.android.dr.interfaces.BookReportView;
import com.onyx.android.dr.interfaces.OnLongClickTouchListener;
import com.onyx.android.dr.interfaces.WebViewInterface;
import com.onyx.android.dr.presenter.BookReportPresenter;
import com.onyx.android.dr.reader.event.RedrawPageEvent;
import com.onyx.android.dr.util.BookReportComparator;
import com.onyx.android.dr.util.DRPreferenceManager;
import com.onyx.android.dr.util.Utils;
import com.onyx.android.dr.view.BookMarksPopupWindow;
import com.onyx.android.dr.view.NotationDialog;
import com.onyx.android.sdk.data.model.v2.CommentsBean;
import com.onyx.android.sdk.data.model.v2.CreateBookReportResult;
import com.onyx.android.sdk.data.model.v2.GetBookReportListBean;
import com.onyx.android.sdk.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by li on 2017/9/15.
 */

public class BookReportDetailActivity extends BaseActivity implements BookReportView {
    @Bind(R.id.image_view_back)
    ImageView imageViewBack;
    @Bind(R.id.image)
    ImageView image;
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
    @Bind(R.id.book_report_detail_web_view)
    WebView bookReportWebContent;
    private Button dialogCancel;
    private Button dialogSure;
    private AlertDialog dialog;
    private GetBookReportListBean data;
    private String bookName;
    private String bookPage;
    private String bookId;
    private BookReportPresenter bookReportPresenter;
    private boolean isSave = false;
    private BookMarksPopupWindow bookMarksPopupWindow;
    private String userType;
    private float currentX;
    private float currentY;
    private NotationDialog notationDialog;
    private int screenHeight;
    private CreateBookReportResult createBookReportResult;
    private OnLongClickTouchListener listener;

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
        image.setImageResource(R.drawable.ic_reader_menu_idea);
        titleBarRightSelectTime.setVisibility(View.VISIBLE);
        titleBarRightSelectTime.setText(getResources().getString(R.string.Call_the_modified_file));
        titleBarRightIconOne.setVisibility(View.VISIBLE);
        titleBarRightIconOne.setImageResource(R.drawable.ic_reader_note_export);
        titleBarRightIconTwo.setVisibility(View.VISIBLE);
        titleBarRightIconTwo.setImageDrawable(getResources().getDrawable(R.drawable.ic_reader_note_diary_save));
        titleBarRightIconThree.setVisibility(View.VISIBLE);
        titleBarRightIconThree.setImageDrawable(getResources().getDrawable(R.drawable.ic_reader_share));
        bookReportDetailContents.setHighlightColor(Color.TRANSPARENT);
        userType = DRPreferenceManager.getUserType(DRApplication.getInstance(), "");
        WebSettings settings = bookReportWebContent.getSettings();
        settings.setTextSize(WebSettings.TextSize.LARGER);
        settings.setJavaScriptEnabled(true);
        bookReportWebContent.addJavascriptInterface(new WebViewInterface(), WebViewInterface.INTERFACE_NAME);
        bookReportWebContent.setWebViewClient(new ReportWebViewClient());
        bookReportWebContent.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                int lineNumber = consoleMessage.lineNumber();
                String message = consoleMessage.message();
                Log.d("webview++", "onConsoleMessage: " + lineNumber + ":" + message);
                return super.onConsoleMessage(consoleMessage);
            }
        });

        if (bookMarksPopupWindow == null) {
            bookMarksPopupWindow = new BookMarksPopupWindow(this);
        }
    }

    @Override
    protected void initData() {
        screenHeight = Utils.getScreenHeight(this);
        Intent intent = getIntent();
        Serializable serializableExtra = intent.getSerializableExtra(Constants.BOOK_REPORT_DATA);
        if (serializableExtra != null) {
            data = (GetBookReportListBean) serializableExtra;
            bookReportDetailContents.setText(data.content);
            bookReportDetailContents.setSelection(data.content.length());
            bookReportDetailTitle.setText(data.name);
            List<CommentsBean> comments = data.comments;
            if (comments != null && comments.size() > 0) {
                bookReportWebContent.setVisibility(View.VISIBLE);
                bookReportDetailContents.setVisibility(View.GONE);
                String newContent = insertJsTag();
                bookReportWebContent.loadDataWithBaseURL(null, newContent, "text/html", "utf-8", null);
            }
        }

        bookName = intent.getStringExtra(Constants.BOOK_NAME);
        bookPage = intent.getStringExtra(Constants.BOOK_PAGE);
        bookId = intent.getStringExtra(Constants.BOOK_ID);
        if (!StringUtils.isNullOrEmpty(bookName)) {
            bookReportDetailTitle.setText(bookName);
        }
        initListener();
    }

    private void initListener() {
        bookReportDetailContents.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                isSave = false;
            }
        });

        bookReportDetailContents.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bookReportDetailContents.onTouchEvent(event);
                currentX = event.getX();
                currentY = event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP && listener != null) {
                    listener.clicked();
                }
                return true;
            }
        });

        bookReportWebContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                currentX = event.getX();
                currentY = event.getY() > (screenHeight * 0.5) ? -event.getY() : event.getY();
                return false;
            }
        });

        bookReportDetailContents.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (Constants.ACCOUNT_TYPE_TEACHER.equals(userType)) {
                    setListener();
                }
                return false;
            }
        });

        bookReportDetailContents.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
    }

    private void setListener() {
        setOnLongClickTouchListener(new OnLongClickTouchListener() {
            @Override
            public void clicked() {
                showNotationDialog();
                listener = null;
            }
        });
    }

    private void showNotationDialog() {
        if (data == null) {
            CommonNotices.showMessage(this, getResources().getString(R.string.no_content));
            return;
        }

        if (data.comments == null && data.comments.size() <= 0) {
            CommonNotices.showMessage(this, getResources().getString(R.string.no_content));
            return;
        }
        if (notationDialog == null) {
            notationDialog = new NotationDialog();
        }

        Bundle bundle = new Bundle();
        bundle.putString(Constants.MARK_BOOK_ID, data._id);
        bundle.putString(Constants.MARK_TOP, String.valueOf(currentY));
        bundle.putString(Constants.MARK_LEFT, String.valueOf(bookReportDetailContents.getSelectionStart()));
        notationDialog.setArguments(bundle);
        notationDialog.show(this.getFragmentManager(), "tag");
    }

    @OnClick({R.id.image_view_back, R.id.title_bar_title, R.id.title_bar_right_select_time,
            R.id.title_bar_right_icon_one, R.id.title_bar_right_icon_two, R.id.title_bar_right_icon_three})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_view_back:
                backActivity();
                break;
            case R.id.title_bar_title:
                backActivity();
                break;
            case R.id.title_bar_right_select_time:
                showMarks();
                break;
            case R.id.title_bar_right_icon_one:
                export();
                break;
            case R.id.title_bar_right_icon_two:
                save();
                break;
            case R.id.title_bar_right_icon_three:
                share();
                break;
        }
    }

    private void share() {
        if (Constants.ACCOUNT_TYPE_TEACHER.equals(userType)) {
            return;
        }
        if (data != null) {
//            ActivityManager.startShareBookReportActivity(this, data._id);
            return;
        }
        if (!StringUtils.isNullOrEmpty(bookId)) {
//            ActivityManager.startShareBookReportActivity(this, createBookReportResult._id);
            return;
        }
    }

    private void showMarks() {
        Utils.hideSoftWindow(this);
        if (data == null) {
            CommonNotices.showMessage(this, getResources().getString(R.string.no_content));
            return;
        }

        if (data.comments == null && data.comments.size() <= 0) {
            CommonNotices.showMessage(this, getResources().getString(R.string.no_content));
            return;
        }

        bookMarksPopupWindow.show(titleBarRightEditText, data.comments);
    }

    private void save() {
        if (data != null && Constants.ACCOUNT_TYPE_TEACHER.equals(userType)) {
            return;
        }

        if (data != null) {
            bookReportPresenter.deleteImpression(data._id);
            return;
        }
        String text = bookReportDetailContents.getText().toString();
        if (StringUtils.isNullOrEmpty(text)) {
            CommonNotices.showMessage(this, getResources().getString(R.string.no_content));
            return;
        }

        if (!StringUtils.isNullOrEmpty(bookName) && !StringUtils.isNullOrEmpty(bookId) && !StringUtils.isNullOrEmpty(bookPage)) {
            bookReportPresenter.createImpression(bookId, bookName, text, bookPage);
        }
    }

    private void export() {
        if (data == null) {
            CommonNotices.showMessage(DRApplication.getInstance(), getResources().getString(R.string.no_comment_no_export));
            return;
        }

        bookReportPresenter.getImpression(data._id);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            backActivity();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    private void backActivity() {
        if (isSave || (Constants.ACCOUNT_TYPE_TEACHER.equals(userType) && data != null)) {
            finish();
        } else {
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
                isSave = true;
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
    public void setCreateBookReportData(CreateBookReportResult createBookReportResult) {
        this.createBookReportResult = createBookReportResult;
        isSave = true;
        bookReportDetailContents.setEnabled(false);
    }

    @Override
    public void setBookReportList(List<GetBookReportListBean> list) {

    }

    @Override
    public void setDeleteResult() {
        String content = bookReportDetailContents.getText().toString();
        bookReportPresenter.createImpression(data.book, data.name, content, data.pageNumber);
    }

    @Override
    public void getBookReport(CreateBookReportResult result) {
        //TODO: confirm not to export comments ?
        GetBookReportListBean getBookReportListBean = new GetBookReportListBean();
        getBookReportListBean.updatedAt = result.updatedAt;
        getBookReportListBean.name = result.name;
        getBookReportListBean.content = result.content;
        bookReportPresenter.bringOutReport(getBookReportListBean);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post(new RedrawPageEvent());
    }

    public void addCommentResult(CreateBookReportResult result) {

    }

    @Override
    public void setLibraryId(String bookId, String libraryId) {
        //bookReportPresenter.shareImpression(bookId, libraryId);
    }

    private String insertJsTag() {
        StringBuilder sb = new StringBuilder();
        if (data != null && data.comments != null && data.comments.size() > 0) {
            List<CommentsBean> comments = data.comments;
            Collections.sort(comments, new BookReportComparator());
            String content = data.content;
            int currentPosition = 0;
            for (CommentsBean bean : comments) {
                int left = Integer.parseInt(bean.left) - currentPosition;
                sb.append(content.substring(0, left));
                String js = "<img id=\"" + bean._id + "\" src=\"file:///android_asset/ic_postil.png\" onclick=\"addComment('" + bean.content + "')\"/>";
                sb.append(js);
                content = content.substring(left);
                currentPosition = left;
            }
            sb.append(content);
        }
        return sb.toString();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWebViewJSEvent(WebViewJSEvent event) {
        String message = event.getMessage();
        float rateH = currentY / screenHeight;
        bookMarksPopupWindow.showText(titleBarRightEditText, message, (int) (screenHeight * 0.3 * rateH));
    }

    private class ReportWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            String addComment = "function addComment(comments){" +
                    "control.showDialog(comments);}";
            bookReportWebContent.loadUrl("javascript:" + addComment);
        }
    }

    public void setOnLongClickTouchListener(OnLongClickTouchListener listener) {
        this.listener = listener;
    }
}
