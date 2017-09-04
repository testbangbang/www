package com.onyx.edu.note.ui;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.onyx.android.sdk.scribble.request.ShapeDataInfo;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.edu.note.data.ScribbleFunctionMenuIDType;
import com.onyx.edu.note.databinding.PageMenuItemBinding;
import com.onyx.edu.note.scribble.PageMenuItemViewHolder;
import com.onyx.edu.note.scribble.PageMenuItemViewModel;
import com.onyx.edu.note.scribble.event.HandlerActivateEvent;
import com.onyx.edu.note.scribble.event.RequestInfoUpdateEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * Created by lxm on 2017/8/31.
 */

public class MainMenuViewModel extends BaseMenuViewModel {

    public final ObservableInt currentPage = new ObservableInt(1);
    public final ObservableInt totalPage = new ObservableInt(1);

    public final ObservableList<Integer> menuActions = new ObservableArrayList<>();

    public MainMenuViewModel(EventBus eventBus) {
        super(eventBus);
        eventBus.register(this);
    }

    public void updatePage(int currentPage, int totalPage) {
        this.currentPage.set(currentPage);
        this.totalPage.set(totalPage);
    }

    @Subscribe
    public void onRequestFinished(RequestInfoUpdateEvent event) {
        if (!event.getRequest().isAbort() && event.getThrowable() == null) {
            ShapeDataInfo shapeDataInfo = event.getShapeDataInfo();
            updatePage(shapeDataInfo.getHumanReadableCurPageIndex(), shapeDataInfo.getPageCount());
        }
    }

}
