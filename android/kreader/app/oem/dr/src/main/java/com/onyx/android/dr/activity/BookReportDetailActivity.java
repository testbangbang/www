package com.onyx.android.dr.activity;

import android.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.dr.R;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by li on 2017/9/15.
 */

public class BookReportDetailActivity extends BaseActivity {
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

    @Override
    protected Integer getLayoutId() {
        return R.layout.book_report_detail_layout;
    }

    @Override
    protected void initConfig() {

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
                //TODO:whether save
                finish();
                break;
            case R.id.title_bar_title:
                //TODO:whether save
                finish();
                break;
            case R.id.title_bar_right_select_time:
                break;
            case R.id.title_bar_right_icon_one:
                break;
            case R.id.title_bar_right_icon_two:
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
                break;
            case R.id.title_bar_right_menu:
                break;
            case R.id.book_report_detail_title:
                break;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            //TODO: show dialog to comfirm save content
            showTipDiaolog();
            return true;
        }
        return super.dispatchKeyEvent(event);
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
                //TODO: SAVE CONTENT
                dialog.dismiss();
            }
        });
    }
}
