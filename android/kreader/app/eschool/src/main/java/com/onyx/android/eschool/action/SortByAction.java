package com.onyx.android.eschool.action;

import android.content.Context;
import android.widget.Toast;

import com.onyx.android.eschool.holder.LibraryDataHolder;
import com.onyx.android.eschool.R;
import com.onyx.android.eschool.events.LoadFinishEvent;
import com.onyx.android.eschool.utils.StudentPreferenceManager;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.ui.dialog.DialogSortBy;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by suicheng on 2017/4/15.
 */

public class SortByAction extends BaseAction<LibraryDataHolder> {

    public SortByAction() {
    }

    @Override
    public void execute(final LibraryDataHolder dataHolder, BaseCallback baseCallback) {
        DialogSortBy dialog = getOrderDialog(dataHolder.getContext(), dataHolder.getContext().getString(R.string.sort_fail),
                getSortByList(dataHolder),
                new DialogSortBy.OnSortByListener() {
                    @Override
                    public void onSortBy(int position, String sortBy, SortOrder sortOrder) {
                        saveSortByQueryArgs(dataHolder, position, sortOrder);
                        processSortBy(dataHolder, sortBy, sortOrder);
                    }
                });
        dialog.setCurrentSortBySelectedIndex(dataHolder.getCurrentSortByIndex());
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

    private List<String> getSortByList(LibraryDataHolder dataHolder) {
        return Arrays.asList(dataHolder.getSortByMap().keySet().toArray(new String[0]));
    }

    private void saveSortByQueryArgs(LibraryDataHolder dataHolder, int index, SortOrder sortOrder) {
        dataHolder.setCurrentSortByIndex(index);
        StudentPreferenceManager.setIntValue(dataHolder.getContext(),
                R.string.library_activity_sort_by_key, index);
        StudentPreferenceManager.setIntValue(dataHolder.getContext(),
                R.string.library_activity_asc_order_key, sortOrder.ordinal());
    }

    private void processSortBy(final LibraryDataHolder dataHolder, String sortBy, SortOrder sortOrder) {
        dataHolder.updateSortBy(dataHolder.getSortByMap().get(sortBy), sortOrder);
        QueryArgs args = dataHolder.getQueryArgs(CollectionUtils.getSize(dataHolder.getBookList()), 0);
        new MetadataLoadAction(args).execute(dataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    ToastUtils.showToast(dataHolder.getContext(), R.string.sort_fail);
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
