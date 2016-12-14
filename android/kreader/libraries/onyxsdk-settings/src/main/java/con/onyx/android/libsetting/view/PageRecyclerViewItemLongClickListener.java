package con.onyx.android.libsetting.view;

import android.databinding.BaseObservable;

/**
 * Created by solskjaer49 on 2016/12/5 20:01.
 */

public interface PageRecyclerViewItemLongClickListener<T extends BaseObservable> {
    void itemLongClick(T observable);
}
