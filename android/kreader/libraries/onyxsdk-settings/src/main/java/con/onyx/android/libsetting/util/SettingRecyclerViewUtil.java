package con.onyx.android.libsetting.util;

import com.onyx.android.sdk.data.GPaginator;
import com.onyx.android.sdk.ui.view.OnyxPageDividerItemDecoration;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

/**
 * Created by solskjaer49 on 2016/12/5 19:51.
 */

public class SettingRecyclerViewUtil {
    public static void updateItemDecoration(PageRecyclerView pageRecyclerView,
                                            PageRecyclerView.PageAdapter adapter,
                                            OnyxPageDividerItemDecoration itemDecoration) {
        GPaginator pageIndicator = pageRecyclerView.getPaginator();
        if (pageIndicator.isLastPage()) {
            itemDecoration.setBlankCount(pageIndicator.itemsInCurrentPage() == adapter.getRowCount() ? 0 :
                    adapter.getRowCount() - (adapter.getDataCount() % adapter.getRowCount()));
        } else {
            itemDecoration.setBlankCount(0);
        }
    }
}
