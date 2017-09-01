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

public class BottomMenuViewModel extends BaseMenuViewModel {

    public final ObservableInt currentPage = new ObservableInt(1);
    public final ObservableInt totalPage = new ObservableInt(1);

    public final ObservableList<Integer> menuActions = new ObservableArrayList<>();

    public BottomMenuViewModel(EventBus eventBus) {
        super(eventBus);
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

    @Subscribe
    public void onHandlerActivate(HandlerActivateEvent activateEvent) {
        setMenuActions(activateEvent.getFunctionBarMenuFunctionIDList());
    }

    public void setMenuActions(List<Integer> actions) {
        menuActions.clear();
        menuActions.addAll(actions);
    }

    public static class PageFunctionAdapter extends PageDataBindingAdapter<PageMenuItemViewHolder, Integer, PageMenuItemViewModel> {

        private EventBus eventBus;

        public PageFunctionAdapter(EventBus eventBus) {
            this.eventBus = eventBus;
        }

        @Override
        public int getRowCount() {
            return 1;
        }

        @Override
        public int getColumnCount() {
            return 6;
        }

        @Override
        public PageMenuItemViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
            return new PageMenuItemViewHolder(PageMenuItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }

        @Override
        public void onPageBindViewHolder(PageMenuItemViewHolder holder, int position) {
            super.onPageBindViewHolder(holder, position);

        }

        @Override
        public void setRawData(PageRecyclerView parent, List<Integer> rawData) {
            super.setRawData(parent, rawData);
            for (Integer mainMenuID : rawData) {
                PageMenuItemViewModel viewModel = new PageMenuItemViewModel(eventBus, mainMenuID, ScribbleFunctionMenuIDType.FUNCTION_BAR_MENU);
                addItemVM(viewModel);
            }
            parent.notifyDataSetChanged();
        }
    }
}
