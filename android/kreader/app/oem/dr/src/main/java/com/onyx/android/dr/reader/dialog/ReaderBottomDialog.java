package com.onyx.android.dr.reader.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.view.View;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.data.ReaderMenuBean;
import com.onyx.android.dr.reader.adapter.ReaderTabMenuAdapter;
import com.onyx.android.dr.reader.common.ReaderTabMenuConfig;
import com.onyx.android.dr.reader.event.DisplayStatusBarEvent;
import com.onyx.android.dr.reader.event.ReaderAfterReadingMenuEvent;
import com.onyx.android.dr.reader.event.ReaderAnnotationMenuEvent;
import com.onyx.android.dr.reader.event.ReaderGoodSentenceMenuEvent;
import com.onyx.android.dr.reader.event.ReaderListenMenuEvent;
import com.onyx.android.dr.reader.event.ReaderPostilMenuEvent;
import com.onyx.android.dr.reader.event.ReaderWordQueryMenuEvent;
import com.onyx.android.dr.reader.presenter.ReaderPresenter;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by hehai on 17-7-14.
 */

public class ReaderBottomDialog extends Dialog implements View.OnClickListener {
    PageRecyclerView readerTabMenu;
    private int layoutID = R.layout.reader_menu_bottom_dialog;
    private ReaderPresenter readerPresenter;
    private List<Integer> childIdList = null;
    private ReaderTabMenuAdapter readerTabMenuAdapter;
    private List<ReaderMenuBean> defaultMenuData;
    private View dismissZone;

    public ReaderBottomDialog(ReaderPresenter readerPresenter, @NonNull Context context, int layoutID, List<Integer> childIdList) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.readerPresenter = readerPresenter;
        this.childIdList = childIdList;
        setCanceledOnTouchOutside(false);
        if (layoutID != -1) {
            this.layoutID = layoutID;
        }
        setContentView(this.layoutID);
        initThirdLibrary();
        initData();
        initView();
    }

    private void initData() {
        readerPresenter.getBookOperate().getDocumentInfo();
        defaultMenuData = ReaderTabMenuConfig.getMenuData();
    }

    private void initThirdLibrary() {
        EventBus.getDefault().register(this);
    }

    private void initView() {
        readerTabMenu = (PageRecyclerView) findViewById(R.id.reader_main_tab_menu).findViewById(R.id.tab_menu);
        readerTabMenu.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL);
        readerTabMenu.addItemDecoration(dividerItemDecoration);
        readerTabMenuAdapter = new ReaderTabMenuAdapter();
        readerTabMenu.setAdapter(readerTabMenuAdapter);

        dismissZone = findViewById(R.id.dismiss_zone);
        dismissZone.setOnClickListener(this);

        if (childIdList != null && childIdList.size() > 0) {
            initCustomizeView();
        } else {
            initDefaultView();
        }
    }

    private void initDefaultView() {
        defaultMenuData.get(0).setEnable(false);
        defaultMenuData.get(2).setEnable(false);
        defaultMenuData.get(3).setEnable(false);
        readerTabMenuAdapter.setMenuDataList(defaultMenuData);
        readerTabMenuAdapter.notifyDataSetChanged();
    }

    private void initCustomizeView() {
        for (int i = 0; i < childIdList.size(); i++) {
            View view = findViewById(childIdList.get(i));
            if (view != null) {
                view.setOnClickListener(this);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnReaderPostilMenuEvent(ReaderPostilMenuEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnReaderAnnotationMenuEvent(ReaderAnnotationMenuEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnReaderWordQueryMenuEvent(ReaderWordQueryMenuEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnReaderGoodSentenceMenuEvent(ReaderGoodSentenceMenuEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnReaderListenMenuEvent(ReaderListenMenuEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnReaderAfterReadingMenuEvent(ReaderAfterReadingMenuEvent event) {

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dismiss_zone:
                dismiss();
                break;
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        readerPresenter.getHandlerManger().onStop();
        EventBus.getDefault().unregister(this);
        readerPresenter.getBookOperate().redrawPage();
    }
}
