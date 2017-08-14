package com.onyx.android.dr.reader.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.reader.adapter.ReaderTabMenuAdapter;
import com.onyx.android.dr.reader.common.ReaderTabMenuConfig;
import com.onyx.android.dr.reader.common.ToastManage;
import com.onyx.android.dr.reader.event.AfterReadingMenuEvent;
import com.onyx.android.dr.reader.event.ReadingSummaryMenuEvent;
import com.onyx.android.dr.reader.presenter.ReaderPresenter;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by hehai on 17-8-10.
 */

public class AfterReadingDialog extends Dialog implements View.OnClickListener {
    private ReaderPresenter readerPresenter;
    private LinearLayout menuBack;
    private TextView title;
    private View afterReadingMenu;
    private PageRecyclerView afterReadingTabMenu;
    private ReaderTabMenuAdapter afterReadingTabMenuAdapter;

    protected AfterReadingDialog(ReaderPresenter readerPresenter, @NonNull Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.readerPresenter = readerPresenter;
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.after_reading_dialog);
        initView();
    }

    private void initView() {
        menuBack = (LinearLayout) findViewById(R.id.menu_back);
        title = (TextView) menuBack.findViewById(R.id.title_bar_title);
        title.setText(getContext().getString(R.string.reflection_after_reading));
        afterReadingMenu = findViewById(R.id.after_reading_menu);
        afterReadingTabMenu = (PageRecyclerView) afterReadingMenu.findViewById(R.id.tab_menu);
        afterReadingTabMenu.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL);
        afterReadingTabMenu.addItemDecoration(dividerItemDecoration);
        afterReadingTabMenuAdapter = new ReaderTabMenuAdapter();
        afterReadingTabMenuAdapter.setMenuDataList(ReaderTabMenuConfig.getAfterReaderMenus());
        afterReadingTabMenu.setAdapter(afterReadingTabMenuAdapter);

        View dismissZone = findViewById(R.id.dismiss_zone);
        menuBack.setOnClickListener(this);
        dismissZone.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_back:
            case R.id.dismiss_zone:
                dismiss();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReadingSummaryMenuEvent(ReadingSummaryMenuEvent event) {
        ToastManage.showMessage(getContext(), getContext().getString(R.string.read_summary));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAfterReadingMenuEvent(AfterReadingMenuEvent event) {
        ToastManage.showMessage(getContext(), getContext().getString(R.string.after_reading));
        String documentMd5 = readerPresenter.getReader().getDocumentMd5();
        ActivityManager.startAfterReadingActivity(getContext(), documentMd5);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
