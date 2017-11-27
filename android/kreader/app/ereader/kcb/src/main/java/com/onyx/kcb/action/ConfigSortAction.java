package com.onyx.kcb.action;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;

import com.onyx.android.sdk.data.LibraryDataModel;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.dialog.DialogSortBy;
import com.onyx.android.sdk.utils.CollectionUtils;
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

public class ConfigSortAction extends BaseAction<DataBundle> {
    private Map<String, SortBy> sortByMap = new LinkedHashMap();
    private FragmentManager fragmentManager;

    private LibraryDataModel libraryDataModel;
    private RxCallback baseCallback;

    public ConfigSortAction(Activity activity) {
        this.fragmentManager = activity.getFragmentManager();
    }

    @Override
    public void execute(final DataBundle dataBundle, RxCallback baseCallback) {
        this.baseCallback = baseCallback;
        DialogSortBy dialog = getOrderDialog(dataBundle.getAppContext(), dataBundle.getAppContext().getString(R.string.sort),
                getSortByList(dataBundle),
                new DialogSortBy.OnSortByListener() {
                    @Override
                    public void onSortBy(int position, String sortBy, SortOrder sortOrder) {
                        saveSortByQueryArgs(dataBundle, sortBy, sortOrder);
                        processSortBy(dataBundle, sortBy, sortOrder);
                    }
                });
        dialog.setCurrentSortBySelectedIndex(getCurrentSortByIndex(dataBundle.getAppContext(),
                dataBundle.getLibraryViewDataModel().getCurrentQueryArgs()));
        dialog.setCurrentSortOrderSelected(dataBundle.getLibraryViewDataModel().getCurrentSortOrder());
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

    private List<String> getSortByList(DataBundle dataBundle) {
        return Arrays.asList(getSortByMap(dataBundle.getAppContext()).keySet().toArray(new String[0]));
    }

    private void saveSortByQueryArgs(DataBundle dataHolder, String sortBy, SortOrder sortOrder) {
        PreferenceManager.setStringValue(dataHolder.getAppContext(),
                R.string.library_activity_sort_by_key, getSortByMap(dataHolder.getAppContext()).get(sortBy).toString());
        PreferenceManager.setIntValue(dataHolder.getAppContext(),
                R.string.library_activity_asc_order_key, sortOrder.ordinal());
    }

    private void processSortBy(final DataBundle dataHolder, String sortBy, SortOrder sortOrder) {
        LibraryViewDataModel libraryViewInfo = dataHolder.getLibraryViewDataModel();
        libraryViewInfo.updateSortBy(getSortByMap(dataHolder.getAppContext()).get(sortBy), sortOrder);
        baseCallback.onComplete();
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
