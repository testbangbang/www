package com.onyx.android.dr.view;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.interfaces.BookReportView;
import com.onyx.android.dr.presenter.BookReportPresenter;
import com.onyx.android.sdk.data.model.CreateInformalEssayBean;
import com.onyx.android.sdk.data.model.CreateInformalSecondBean;
import com.onyx.android.sdk.data.model.v2.CreateBookReportResult;
import com.onyx.android.sdk.data.model.v2.GetBookReportListBean;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by li on 2017/9/23.
 */

public class NotationDialog extends DialogFragment implements BookReportView {
    @Bind(R.id.notation_dialog_cancel)
    TextView notationDialogCancel;
    @Bind(R.id.notation_dialog_confirm)
    TextView notationDialogConfirm;
    @Bind(R.id.notation_dialog_mark)
    EditText notationDialogMark;
    private String bookId;
    private String top;
    private String left;
    private BookReportPresenter bookReportPresenter;
    private int type;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.notation_dialog_layout, container);
        ButterKnife.bind(this, view);
        initData();
        return view;
    }

    private void initData() {
        if (bookReportPresenter == null) {
            bookReportPresenter = new BookReportPresenter(this);
        }
        Bundle args = getArguments();
        if (args != null) {
            bookId = args.getString(Constants.MARK_BOOK_ID);
            top = args.getString(Constants.MARK_TOP);
            left = args.getString(Constants.MARK_LEFT);
            type = args.getInt(Constants.JUMP_SOURCE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.notation_dialog_cancel, R.id.notation_dialog_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.notation_dialog_cancel:
                dismiss();
                break;
            case R.id.notation_dialog_confirm:
                addComment();
                dismiss();
                break;
        }
    }

    private void addComment() {
        String content = notationDialogMark.getText().toString();
        if (StringUtils.isNullOrEmpty(content)) {
            CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance().getResources().getString(R.string.no_content));
            return;
        }
        if (type == Constants.READER_RESPONSE_SOURCE_TAG) {
            bookReportPresenter.addComment(bookId, top, left, content);
        } else if (type == Constants.INFORMAL_ESSAY_SOURCE_TAG) {
            bookReportPresenter.addInformalComment(bookId, top, left, content);
        }
    }

    @Override
    public void setBookReportList(List<GetBookReportListBean> list, List<Boolean> listCheck) {
    }

    @Override
    public void setDeleteResult() {
    }

    @Override
    public void getBookReport(CreateBookReportResult result) {
    }

    @Override
    public void addCommentResult(CreateBookReportResult result) {
        if (result != null) {
            CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance().getResources().getString(R.string.notation_add_success));
        } else {
            CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance().getResources().getString(R.string.notation_add_fail));
        }
    }

    @Override
    public void addInformalCommentResult(CreateInformalSecondBean result) {
        if (result != null) {
            CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance().getResources().getString(R.string.notation_add_success));
        } else {
            CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance().getResources().getString(R.string.notation_add_fail));
        }
    }

    @Override
    public void setLibraryId(String bookId, String libraryId) {
    }

    @Override
    public void saveBookReportData(CreateBookReportResult createBookReportResult) {
    }

    @Override
    public void setInformalEssayData(List<CreateInformalEssayBean> dataList, List<Boolean> listCheck) {
    }

    @Override
    public void createInformalEssay(boolean tag) {
    }
}
