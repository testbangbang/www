package com.onyx.einfo.action;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;

import com.onyx.einfo.holder.LibraryDataHolder;
import com.onyx.einfo.R;
import com.onyx.einfo.manager.ConfigPreferenceManager;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.BookFilter;
import com.onyx.android.sdk.data.LibraryDataModel;
import com.onyx.android.sdk.data.LibraryViewInfo;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.ui.dialog.DialogSortBy;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by suicheng on 2017/4/15.
 */

public class ConfigFilterAction extends BaseAction<LibraryDataHolder> {
    private Map<String, BookFilter> filterMap = new LinkedHashMap<>();
    private FragmentManager fragmentManager;

    private LibraryDataModel libraryDataModel;
    private BaseCallback baseCallback;

    public ConfigFilterAction(Activity activity) {
        this.fragmentManager = activity.getFragmentManager();
    }

    @Override
    public void execute(final LibraryDataHolder dataHolder, BaseCallback baseCallback) {
        this.baseCallback = baseCallback;
        DialogSortBy dialog = getOrderDialog(dataHolder.getContext(), dataHolder.getContext().getString(R.string.filter),
                getFilterByList(dataHolder),
                new DialogSortBy.OnSortByListener() {
                    @Override
                    public void onSortBy(int position, String sortBy, SortOrder sortOrder) {
                        saveFilterByQueryArgs(dataHolder, sortBy, sortOrder);
                        processFilterBy(dataHolder, sortBy, sortOrder);
                    }
                });
        LibraryViewInfo libraryViewInfo = dataHolder.getLibraryViewInfo();
        dialog.setCurrentSortBySelectedIndex(getCurrentFilterByIndex(dataHolder.getContext(),
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

    private List<String> getFilterByList(LibraryDataHolder dataHolder) {
        return Arrays.asList(getFilterMap(dataHolder.getContext()).keySet().toArray(new String[0]));
    }

    private void saveFilterByQueryArgs(LibraryDataHolder dataHolder, String filterBy, SortOrder sortOrder) {
        ConfigPreferenceManager.setStringValue(dataHolder.getContext(),
                R.string.library_activity_book_filter_key, getFilterMap(dataHolder.getContext()).get(filterBy).toString());
        ConfigPreferenceManager.setIntValue(dataHolder.getContext(),
                R.string.library_activity_asc_order_key, sortOrder.ordinal());
    }

    private void processFilterBy(final LibraryDataHolder dataHolder, String filterBy, SortOrder sortOrder) {
        LibraryViewInfo libraryViewInfo = dataHolder.getLibraryViewInfo();
        libraryViewInfo.updateFilterBy(getFilterMap(dataHolder.getContext()).get(filterBy), sortOrder);
        final MetadataLoadAction loadAction = new MetadataLoadAction(libraryViewInfo.libraryQuery());
        loadAction.execute(dataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e == null) {
                    libraryDataModel = loadAction.getLibraryDataModel();
                }
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
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

    public LibraryDataModel getLibraryDataModel() {
        return libraryDataModel;
    }
}
