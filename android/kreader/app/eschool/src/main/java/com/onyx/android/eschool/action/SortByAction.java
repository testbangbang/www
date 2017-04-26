package com.onyx.android.eschool.action;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;

import com.onyx.android.eschool.holder.LibraryDataHolder;
import com.onyx.android.eschool.R;
import com.onyx.android.eschool.events.LoadFinishEvent;
import com.onyx.android.eschool.utils.StudentPreferenceManager;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.ui.dialog.DialogSortBy;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by suicheng on 2017/4/15.
 */

public class SortByAction extends BaseAction<LibraryDataHolder> {
    Map<String, SortBy> sortByMap = new LinkedHashMap();
    private FragmentManager fragmentManager;

    public SortByAction(Activity activity) {
        this.fragmentManager = activity.getFragmentManager();
    }

    @Override
    public void execute(final LibraryDataHolder dataHolder, BaseCallback baseCallback) {
        DialogSortBy dialog = getOrderDialog(dataHolder.getContext(), dataHolder.getContext().getString(R.string.sort_fail),
                getSortByList(dataHolder),
                new DialogSortBy.OnSortByListener() {
                    @Override
                    public void onSortBy(int position, String sortBy, SortOrder sortOrder) {
                        saveSortByQueryArgs(dataHolder, sortBy, sortOrder);
                        processSortBy(dataHolder, sortBy, sortOrder);
                    }
                });
        dialog.setCurrentSortBySelectedIndex(getCurrentSortByIndex(dataHolder.getContext(), dataHolder.getCurrentQueryArgs()));
        dialog.setCurrentSortOrderSelected(dataHolder.getCurrentSortOrder());
        dialog.show(fragmentManager);
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
        return Arrays.asList(getSortByMap(dataHolder.getContext()).keySet().toArray(new String[0]));
    }

    private void saveSortByQueryArgs(LibraryDataHolder dataHolder, String sortBy, SortOrder sortOrder) {
        StudentPreferenceManager.setStringValue(dataHolder.getContext(),
                R.string.library_activity_sort_by_key, getSortByMap(dataHolder.getContext()).get(sortBy).toString());
        StudentPreferenceManager.setIntValue(dataHolder.getContext(),
                R.string.library_activity_asc_order_key, sortOrder.ordinal());
    }

    private void processSortBy(final LibraryDataHolder dataHolder, String sortBy, SortOrder sortOrder) {
        dataHolder.updateSortBy(getSortByMap(dataHolder.getContext()).get(sortBy), sortOrder);
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

    public int getCurrentSortByIndex(Context context, QueryArgs queryArgs) {
        SortBy sortBy = queryArgs.sortBy;
        if (sortBy == null) {
            return 0;
        }
        int index = Arrays.asList(getSortByMap(context).values().toArray(new SortBy[0])).indexOf(sortBy);
        if (index < 0) {
            return 0;
        }
        return index;
    }

    private Map<String, SortBy> getSortByMap(Context context) {
        if (CollectionUtils.isNullOrEmpty(sortByMap)) {
            sortByMap.put(context.getString(R.string.by_name), SortBy.Name);
            sortByMap.put(context.getString(R.string.by_tittle), SortBy.BookTitle);
            sortByMap.put(context.getString(R.string.by_type), SortBy.FileType);
            sortByMap.put(context.getString(R.string.by_size), SortBy.Size);
            sortByMap.put(context.getString(R.string.by_creation_time), SortBy.CreationTime);
            sortByMap.put(context.getString(R.string.by_author), SortBy.Author);
        }
        return sortByMap;
    }
}
