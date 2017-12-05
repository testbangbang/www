package com.onyx.kcb.action;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;

import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.dialog.DialogSortBy;
import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.kcb.R;
import com.onyx.kcb.holder.DataBundle;
import com.onyx.kcb.model.LibraryViewDataModel;
import com.onyx.kcb.utils.Constant;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hehai on 17-12-5.
 */

public class DisplayModeAction extends BaseAction<DataBundle> {
    private Map<String, Integer> filterMap = new LinkedHashMap<>();
    private FragmentManager fragmentManager;

    public DisplayModeAction(Activity activity) {
        this.fragmentManager = activity.getFragmentManager();
    }

    @Override
    public void execute(final DataBundle dataBundle, final RxCallback baseCallback) {
        DialogSortBy dialog = getOrderDialog(dataBundle.getAppContext(), dataBundle.getAppContext().getString(R.string.filter),
                getDisplayModeList(dataBundle),
                new DialogSortBy.OnSortByListener() {
                    @Override
                    public void onSortBy(int position, String sortBy, SortOrder sortOrder) {
                        saveDisplayMode(dataBundle, position);
                        if (baseCallback != null) {
                            baseCallback.onNext(position);
                        }
                    }
                });

        dialog.setCurrentSortBySelectedIndex(getCurrentDisplayModeByIndex(dataBundle.getAppContext()));
        dialog.setShowSortOrderLayout(false);
        dialog.show(fragmentManager);
    }

    private void saveDisplayMode(DataBundle dataBundle, int position) {
        PreferenceManager.setIntValue(dataBundle.getAppContext(), R.string.library_display_mode_key,
                filterMap.get(getDisplayModeList(dataBundle).get(position)));
    }

    private int getCurrentDisplayModeByIndex(Context appContext) {
        return PreferenceManager.getIntValue(appContext, R.string.library_display_mode_key, Constant.LibraryDisplayMode.NORMAL_MODE);
    }

    private List<String> getDisplayModeList(DataBundle dataBundle) {
        return Arrays.asList(getDisplayModeMap(dataBundle.getAppContext()).keySet().toArray(new String[0]));
    }

    private Map<String, Integer> getDisplayModeMap(Context appContext) {
        if (filterMap.isEmpty()) {
            filterMap.put(appContext.getString(R.string.normal_mode), Constant.LibraryDisplayMode.NORMAL_MODE);
            filterMap.put(appContext.getString(R.string.child_library_mode), Constant.LibraryDisplayMode.CHILD_LIBRARY_MODE);
            filterMap.put(appContext.getString(R.string.directory_mode), Constant.LibraryDisplayMode.DIR_MODE);
        }
        return filterMap;
    }

    private DialogSortBy getOrderDialog(Context context, String title, List<String> contentList,
                                        DialogSortBy.OnSortByListener listener) {
        DialogSortBy dialog = new DialogSortBy(title, contentList);
        dialog.setOnSortByListener(listener);
        DialogSortBy.AlignLayoutParams alignParams = new DialogSortBy.AlignLayoutParams(
                context.getResources().getDimensionPixelSize(R.dimen.dialog_sort_by_x_pos),
                context.getResources().getDimensionPixelSize(R.dimen.dialog_sort_by_y_pos));
        alignParams.width = 220;
        alignParams.height = 300;
        dialog.setAlignParams(alignParams);
        return dialog;
    }
}
