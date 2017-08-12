package com.onyx.android.dr.reader.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.dr.R;
import com.onyx.android.dr.reader.action.ShowReaderBottomMenuDialogAction;
import com.onyx.android.dr.reader.adapter.PostilListAdapter;
import com.onyx.android.dr.reader.event.AnnotationsChangeEvent;
import com.onyx.android.dr.reader.event.PostilManageDialogDismissEvent;
import com.onyx.android.dr.reader.presenter.ReaderPresenter;
import com.onyx.android.sdk.reader.common.PageAnnotation;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by hehai on 17-7-25.
 */

public class PostilManageDialog extends Dialog implements View.OnClickListener {
    private ReaderPresenter readerPresenter;
    private List<PageAnnotation> annotations;
    private LinearLayout menuBack;
    private PageRecyclerView recyclerView;
    private PostilListAdapter postilListAdapter;
    private TextView title;

    protected PostilManageDialog(ReaderPresenter readerPresenter, @NonNull Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.readerPresenter = readerPresenter;
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.postil_manage_dialog);
        initThirdLibrary();
        initPostilList();
        initView();
    }

    private void initThirdLibrary() {
        EventBus.getDefault().register(this);
    }

    private void initView() {
        menuBack = (LinearLayout) findViewById(R.id.menu_back);
        title = (TextView) menuBack.findViewById(R.id.title_bar_title);
        title.setText(getContext().getString(R.string.postil));
        recyclerView = (PageRecyclerView) findViewById(R.id.postil_manage_recycler);
        recyclerView.setLayoutManager(new DisableScrollGridManager(getContext()));
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL);
        recyclerView.addItemDecoration(itemDecoration);
        postilListAdapter = new PostilListAdapter(readerPresenter, annotations);
        recyclerView.setAdapter(postilListAdapter);
        menuBack.setOnClickListener(this);
    }

    private void initPostilList() {
        annotations = ShowReaderBottomMenuDialogAction.getAnnotations(readerPresenter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnAnnotationsChangeEvent(AnnotationsChangeEvent event) {
        postilListAdapter.updateData(event.getAnnotation());
    }

    @Override
    public void dismiss() {
        super.dismiss();
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().post(new PostilManageDialogDismissEvent());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_back:
                dismiss();
                break;
        }
    }
}
