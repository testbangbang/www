package com.onyx.kreader.ui.dialog;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.onyx.android.sdk.data.GAdapter;
import com.onyx.android.sdk.data.GAdapterUtil;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;
import com.onyx.android.sdk.ui.view.ContentItemView;
import com.onyx.android.sdk.ui.view.ContentView;
import com.onyx.kreader.R;
import com.onyx.android.sdk.reader.dataprovider.LegacySdkDataUtils;

import java.util.HashMap;

/**
 * Created by solskjaer49 on 16/1/9 15:19.
 */
public class DialogScreenRefresh extends OnyxAlertDialog {

    public interface onScreenRefreshChangedListener {
        void onRefreshIntervalChanged(int oldValue, int newValue);
    }

    public static final int DEFAULT_INTERVAL_COUNT = Integer.MAX_VALUE;

    private GAdapter mAdapter = null;
    int interval = DEFAULT_INTERVAL_COUNT;

    public void setListener(onScreenRefreshChangedListener listener) {
        this.listener = listener;
    }

    onScreenRefreshChangedListener listener = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO not sure how to use OnyxSysCenter
        interval = LegacySdkDataUtils.getScreenUpdateGCInterval(getActivity(), DEFAULT_INTERVAL_COUNT);
        buildScreenRefreshAdapter();
        setParams(new Params().setTittleString(getString(R.string.screen_refresh))
                .setCustomContentLayoutResID(R.layout.dialog_screen_refresh)
                .setEnableFunctionPanel(false)
                .setEnablePageIndicator(false)
                .setCustomLayoutHeight((int) (mAdapter.size() * getResources().getDimension(R.dimen.button_minHeight)))
                .setCustomViewAction(new CustomViewAction() {
                    @Override
                    public void onCreateCustomView(View customView, TextView pageIndicator) {
                        final ContentView refreshContentView = (ContentView) customView.findViewById(R.id.screen_refresh_contentView);
                        refreshContentView.setShowPageInfoArea(false);
                        HashMap<String, Integer> mapping = new HashMap<>();
                        mapping.put(GAdapterUtil.TAG_TITLE_RESOURCE, R.id.textview_title);
                        mapping.put(GAdapterUtil.TAG_SELECTABLE, R.id.radio_selected);
                        mapping.put(GAdapterUtil.TAG_DIVIDER_VIEW, R.id.divider);
                        refreshContentView.setupGridLayout(mAdapter.size(),1);
                        refreshContentView.setSubLayoutParameter(R.layout.dialog_screen_refresh_item, mapping);
                        refreshContentView.setAdapter(mAdapter, 0);
                        refreshContentView.setCallback(new ContentView.ContentViewCallback() {
                            @Override
                            public void onItemClick(ContentItemView view) {
                                GObject temp = view.getData();
                                int dataIndex = refreshContentView.getCurrentAdapter().getGObjectIndex(temp);
                                temp.putBoolean(GAdapterUtil.TAG_SELECTABLE, true);
                                refreshContentView.getCurrentAdapter().setObject(dataIndex, temp);
                                refreshContentView.unCheckOtherViews(dataIndex, true);
                                refreshContentView.updateCurrentPage();
                                if (listener != null) {
                                    listener.onRefreshIntervalChanged(interval, view.getData().getInt(GAdapterUtil.TAG_UNIQUE_ID));
                                }
                                DialogScreenRefresh.this.dismiss();
                            }
                        });
                    }
                })
                .setEnableNegativeButton(false));
        super.onCreate(savedInstanceState);
    }

    public void show(FragmentManager fm) {
        super.show(fm, DialogScreenRefresh.class.getSimpleName());
    }

    private GAdapter buildScreenRefreshAdapter() {
        if (mAdapter == null) {
            mAdapter = new GAdapter();
            mAdapter.addObject(createScreenRefreshItem(R.string.always, 1));
            mAdapter.addObject(createScreenRefreshItem(R.string.every_3_pages, 3));
            mAdapter.addObject(createScreenRefreshItem(R.string.every_5_pages, 5));
            mAdapter.addObject(createScreenRefreshItem(R.string.every_7_pages, 7));
            mAdapter.addObject(createScreenRefreshItem(R.string.every_9_pages, 9));
            mAdapter.addObject(createScreenRefreshItem(R.string.never, Integer.MAX_VALUE));
        }
        return mAdapter;
    }

    private GObject createScreenRefreshItem(int stringResource, int refreshPageInterval) {
        GObject object = GAdapterUtil.createTableItem(stringResource, 0, 0, 0, null);
        object.putInt(GAdapterUtil.TAG_UNIQUE_ID, refreshPageInterval);
        object.putBoolean(GAdapterUtil.TAG_SELECTABLE, false);
        if (refreshPageInterval == interval) {
            object.putBoolean(GAdapterUtil.TAG_SELECTABLE, true);
        }
        return object;
    }
}
