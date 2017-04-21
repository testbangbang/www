package com.onyx.android.eschool.action;

import android.content.Context;

import com.onyx.android.eschool.holder.LibraryDataHolder;
import com.onyx.android.eschool.R;
import com.onyx.android.eschool.events.LoadFinishEvent;
import com.onyx.android.eschool.utils.StudentPreferenceManager;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.ui.dialog.DialogSortBy;
import com.onyx.android.sdk.ui.utils.ToastUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by suicheng on 2017/4/15.
 */

public class FilterByAction extends BaseAction<LibraryDataHolder> {

    public FilterByAction() {
    }

    @Override
    public void execute(final LibraryDataHolder dataHolder, BaseCallback baseCallback) {
        DialogSortBy dialog = getOrderDialog(dataHolder.getContext(), dataHolder.getContext().getString(R.string.filter),
                getFilterByList(dataHolder),
                new DialogSortBy.OnSortByListener() {
                    @Override
                    public void onSortBy(int position, String sortBy, SortOrder sortOrder) {
                        saveFilterByQueryArgs(dataHolder, position, sortOrder);
                        processFilterBy(dataHolder, sortBy, sortOrder);
                    }
                });
        dialog.setCurrentSortBySelectedIndex(dataHolder.getCurrentFilterByIndex());
        dialog.setCurrentSortOrderSelected(dataHolder.getCurrentSortOrder());
        dialog.show(dataHolder.getFragmentManager());
    }

    private DialogSortBy getOrderDialog(Context context, String title, List<String> contentList,
                                        DialogSortBy.OnSortByListener listener) {
        DialogSortBy dialog = new DialogSortBy(title, contentList);
        dialog.setOnSortByListener(listener);
        DialogSortBy.AlignLayoutParams alignParams = new DialogSortBy.AlignLayoutParams(
                context.getResources().getDimensionPixelSize(R.dimen.dialog_sort_by_x_pos),
                context.getResources().getDimensionPixelSize(R.dimen.dialog_sort_by_y_pos));
        alignParams.width = 330;
        alignParams.height = 470;
        dialog.setAlignParams(alignParams);
        return dialog;
    }

    private List<String> getFilterByList(LibraryDataHolder dataHolder) {
        return Arrays.asList(dataHolder.getFilterMap().keySet().toArray(new String[0]));
    }

    private void saveFilterByQueryArgs(LibraryDataHolder dataHolder, int index, SortOrder sortOrder) {
        dataHolder.setCurrentFilterByIndex(index);
        StudentPreferenceManager.setIntValue(dataHolder.getContext(),
                R.string.library_activity_book_filter_key, index);
        StudentPreferenceManager.setIntValue(dataHolder.getContext(),
                R.string.library_activity_asc_order_key, sortOrder.ordinal());
    }

    private void processFilterBy(final LibraryDataHolder dataHolder, String filterBy, SortOrder sortOrder) {
        dataHolder.updateFilterByBy(dataHolder.getFilterMap().get(filterBy), sortOrder);
        new MetadataLoadAction(dataHolder.getQueryArgs()).execute(dataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    ToastUtils.showToast(dataHolder.getContext(), R.string.filter_fail);
                    return;
                }
                postLoadFinishEvent(dataHolder);
            }
        });
    }

    private void postLoadFinishEvent(LibraryDataHolder dataHolder) {
        dataHolder.getEventBus().post(new LoadFinishEvent());
    }
}
