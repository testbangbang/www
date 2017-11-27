package com.onyx.kcb.action;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;

import com.onyx.android.sdk.data.BookFilter;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.dialog.DialogSortBy;
import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.kcb.R;
import com.onyx.kcb.holder.DataBundle;
import com.onyx.kcb.model.LibraryViewDataModel;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by suicheng on 2017/4/15.
 */

public class ConfigFilterAction extends BaseAction<DataBundle> {
    private Map<String, BookFilter> filterMap = new LinkedHashMap<>();
    private FragmentManager fragmentManager;

    private RxCallback baseCallback;

    public ConfigFilterAction(Activity activity) {
        this.fragmentManager = activity.getFragmentManager();
    }

    @Override
    public void execute(final DataBundle dataBundle, RxCallback baseCallback) {
        this.baseCallback = baseCallback;
        DialogSortBy dialog = getOrderDialog(dataBundle.getAppContext(), dataBundle.getAppContext().getString(R.string.filter),
                getFilterByList(dataBundle),
                new DialogSortBy.OnSortByListener() {
                    @Override
                    public void onSortBy(int position, String sortBy, SortOrder sortOrder) {
                        saveFilterByQueryArgs(dataBundle, sortBy, sortOrder);
                        processFilterBy(dataBundle, sortBy, sortOrder);
                    }
                });


        LibraryViewDataModel libraryViewInfo = dataBundle.getLibraryViewDataModel();
        dialog.setCurrentSortBySelectedIndex(getCurrentFilterByIndex(dataBundle.getAppContext(),
                libraryViewInfo.getCurrentQueryArgs()));
        dialog.setCurrentSortOrderSelected(libraryViewInfo.getCurrentSortOrder());
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

    private List<String> getFilterByList(DataBundle dataBundle) {
        return Arrays.asList(getFilterMap(dataBundle.getAppContext()).keySet().toArray(new String[0]));
    }

    private void saveFilterByQueryArgs(DataBundle dataBundle, String filterBy, SortOrder sortOrder) {
        PreferenceManager.setStringValue(dataBundle.getAppContext(),
                R.string.library_activity_book_filter_key, getFilterMap(dataBundle.getAppContext()).get(filterBy).toString());
        PreferenceManager.setIntValue(dataBundle.getAppContext(),
                R.string.library_activity_asc_order_key, sortOrder.ordinal());
    }

    private void processFilterBy(final DataBundle dataBundle, String filterBy, SortOrder sortOrder) {
        dataBundle.getLibraryViewDataModel().updateFilterBy(getFilterMap(dataBundle.getAppContext()).get(filterBy), sortOrder);
        baseCallback.onComplete();
    }

    public Map<String, BookFilter> getFilterMap(Context context) {
        if (filterMap.isEmpty()) {
            filterMap.put(context.getString(R.string.filter_all), BookFilter.ALL);
            filterMap.put(context.getString(R.string.filter_new_books), BookFilter.NEW);
            filterMap.put(context.getString(R.string.filter_reading), BookFilter.READING);
            filterMap.put(context.getString(R.string.filter_read), BookFilter.FINISHED);
            filterMap.put(context.getString(R.string.filter_tag), BookFilter.TAG);
        }
        return filterMap;
    }

    public int getCurrentFilterByIndex(Context context, QueryArgs queryArgs) {
        BookFilter filter = queryArgs.filter;
        if (filter == null) {
            return 0;
        }
        int index = Arrays.asList(getFilterMap(context).values().toArray(new BookFilter[0])).indexOf(filter);
        if (index < 0) {
            return 0;
        }
        return index;
    }
}
