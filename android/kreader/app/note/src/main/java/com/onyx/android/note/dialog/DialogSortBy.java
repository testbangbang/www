package com.onyx.android.note.dialog;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.onyx.android.note.R;
import com.onyx.android.sdk.data.GAdapter;
import com.onyx.android.sdk.data.GAdapterUtil;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.scribble.data.AscDescOrder;
import com.onyx.android.sdk.scribble.data.SortBy;
import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;
import com.onyx.android.sdk.ui.view.ContentItemView;
import com.onyx.android.sdk.ui.view.ContentView;

import java.util.HashMap;

public class DialogSortBy extends OnyxAlertDialog {
    private static final String TAG = DialogSortBy.class.getSimpleName();
    static final public String ARGS_ASC = "args_asc";
    static final public String ARGS_SORT_BY = "args_sort_by";
    private
    @AscDescOrder.AscDescOrderDef
    int ascOrder;
    private
    @SortBy.SortByDef
    int currentSortBy;
    private ContentView mContentView;
    private GAdapter mAdapter;
    private Callback mCallBack;

    public DialogSortBy setCallBack(Callback mCallBack) {
        this.mCallBack = mCallBack;
        return this;
    }

    public interface Callback {
        void onSortBy(@SortBy.SortByDef int sortBy, @AscDescOrder.AscDescOrderDef int ascOrder);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        currentSortBy = SortBy.translate(getArguments().getInt(ARGS_SORT_BY));
        ascOrder = AscDescOrder.translate(getArguments().getInt(ARGS_ASC));
        buildOptionsAdapter();
        setParams(new Params().setTittleString(getString(R.string.sort))
                .setCustomContentLayoutResID(R.layout.alert_dialog_content_sory_by)
                .setEnablePageIndicator(false)
                .setPositiveButtonText(getString(R.string.asc))
                .setNegativeButtonText(getString(R.string.desc))
                .setCustomLayoutHeight((int) (4.3 * (getResources().getDimensionPixelSize(R.dimen.button_minHeight))))
                .setCustomViewAction(new CustomViewAction() {
                    @Override
                    public void onCreateCustomView(View customView, TextView pageIndicator) {
                        mContentView = (ContentView) customView.findViewById(R.id.sort_by_content_view);
                        HashMap<String, Integer> mapping = new HashMap<String, Integer>();
                        mapping.put(GAdapterUtil.TAG_TITLE_RESOURCE, R.id.radio_item);
                        mapping.put(GAdapterUtil.TAG_SELECTABLE, R.id.radio_item);
                        mContentView.setupGridLayout(4, 1);
                        mContentView.setAlwaysShowPageIndicator(false);
                        mContentView.setSubLayoutParameter(R.layout.sort_by_item, mapping);
                        mContentView.setAdapter(mAdapter, 0);
                        mContentView.setCallback(new ContentView.ContentViewCallback() {
                            @Override
                            public void onItemClick(ContentItemView view) {
                                GObject temp = view.getData();
                                int dataIndex = mContentView.getCurrentAdapter().getGObjectIndex(temp);
                                temp.putBoolean(GAdapterUtil.TAG_SELECTABLE, true);
                                mContentView.getCurrentAdapter().setObject(dataIndex, temp);
                                mContentView.unCheckOtherViews(dataIndex, false);
                                mContentView.updateCurrentPage();
                                currentSortBy = SortBy.translate(GAdapterUtil.getUniqueIdAsIntegerType(temp));
                            }
                        });
                    }
                }).setPositiveAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCallBack != null) {
                            mCallBack.onSortBy(currentSortBy, AscDescOrder.ASC);
                        }
                        dismiss();
                    }
                }).setNegativeAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCallBack != null) {
                            mCallBack.onSortBy(currentSortBy, AscDescOrder.DESC);
                        }
                        dismiss();
                    }
                }));
        super.onCreate(savedInstanceState);
    }

    private GAdapter buildOptionsAdapter() {
        if (mAdapter == null) {
            mAdapter = new GAdapter();
            mAdapter.addObject(createSortOrderItem(R.string.updated_at, SortBy.UPDATED_AT));
            mAdapter.addObject(createSortOrderItem(R.string.title, SortBy.TITLE));
            mAdapter.addObject(createSortOrderItem(R.string.created_at, SortBy.CREATED_AT));
            mAdapter.addObject(createSortOrderItem(R.string.type, SortBy.TYPE));
        }
        return mAdapter;
    }

    private GObject createSortOrderItem(int stringResource, @SortBy.SortByDef int sortBy) {
        GObject object = GAdapterUtil.createTableItem(stringResource, 0, 0, 0, null);
        object.putInt(GAdapterUtil.TAG_UNIQUE_ID, sortBy);
        object.putBoolean(GAdapterUtil.TAG_SELECTABLE, false);
        if (sortBy == currentSortBy) {
            object.putBoolean(GAdapterUtil.TAG_SELECTABLE, true);
        }
        return object;
    }

    public void show(FragmentManager fm) {
        show(fm, TAG);
    }

}
