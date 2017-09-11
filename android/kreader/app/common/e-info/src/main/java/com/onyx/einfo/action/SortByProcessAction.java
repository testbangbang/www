package com.onyx.einfo.action;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Resources;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.ui.dialog.DialogSortBy;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.einfo.R;
import com.onyx.einfo.holder.LibraryDataHolder;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by suicheng on 2017/8/28.
 */
public class SortByProcessAction extends BaseAction<LibraryDataHolder> {

    private FragmentManager fragmentManager;
    private Map<String, SortBy> sortByMap;
    private SortBy defaultSortBy;
    private SortOrder defaultSortOrder;

    private SortBy resultSortBy;
    private SortOrder resultSortOrder;


    public SortByProcessAction(Activity activity, SortBy sortBy, SortOrder sortOrder) {
        this.fragmentManager = activity.getFragmentManager();
        this.defaultSortBy = sortBy;
        this.defaultSortOrder = sortOrder;
    }

    public SortBy getResultSortBy() {
        return resultSortBy;
    }

    public SortOrder getResultSortOrder() {
        return resultSortOrder;
    }

    private Map<String, SortBy> getSortByMap(Context context) {
        if (CollectionUtils.isNullOrEmpty(sortByMap)) {
            sortByMap = new LinkedHashMap<>();
            sortByMap.put(context.getString(R.string.by_name), SortBy.Name);
            sortByMap.put(context.getString(R.string.by_type), SortBy.FileType);
            sortByMap.put(context.getString(R.string.by_size), SortBy.Size);
            sortByMap.put(context.getString(R.string.by_creation_time), SortBy.CreationTime);
        }
        return sortByMap;
    }

    public void setSortByMap(Map<String, SortBy> map) {
        this.sortByMap = map;
    }

    @Override
    public void execute(final LibraryDataHolder dataHolder, final BaseCallback baseCallback) {
        final Map<String, SortBy> sortByMap = getSortByMap(dataHolder.getContext());
        getSortByDialog(dataHolder, sortByMap, new DialogSortBy.OnSortByListener() {
            @Override
            public void onSortBy(int position, String sortBy, SortOrder sortOrder) {
                resultSortBy = sortByMap.get(sortBy);
                resultSortOrder = sortOrder;
                BaseCallback.invoke(baseCallback, null, null);
            }
        });
    }

    private DialogSortBy getSortByDialog(LibraryDataHolder dataHolder, Map<String, SortBy> sortByMap,
                                         DialogSortBy.OnSortByListener listener) {
        Resources resources = dataHolder.getContext().getResources();
        List<String> contentList = Arrays.asList(sortByMap.keySet().toArray(new String[0]));
        DialogSortBy dialog = new DialogSortBy(null, contentList);
        dialog.setOnSortByListener(listener);
        DialogSortBy.AlignLayoutParams alignParams = new DialogSortBy.AlignLayoutParams(
                resources.getDimensionPixelSize(R.dimen.dialog_sort_by_x_pos),
                resources.getDimensionPixelSize(R.dimen.dialog_sort_by_y_pos));
        alignParams.width = 310;
        alignParams.height = 374;
        dialog.setCurrentSortBySelectedIndex(getCurrentSortByIndex(defaultSortBy, sortByMap));
        dialog.setCurrentSortOrderSelected(defaultSortOrder);
        dialog.setAlignParams(alignParams);
        dialog.show(fragmentManager);
        return dialog;
    }

    private int getCurrentSortByIndex(SortBy currentSortBy, Map<String, SortBy> sortByMap) {
        if (currentSortBy == null) {
            return 0;
        }
        int index = Arrays.asList(sortByMap.values().toArray(new SortBy[0])).indexOf(currentSortBy);
        if (index < 0) {
            return 0;
        }
        return index;
    }
}
