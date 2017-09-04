package com.onyx.edu.note.ui;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.edu.note.NoteApplication;
import com.onyx.edu.note.R;
import com.onyx.edu.note.data.ScribbleFunctionBarMenuID;
import com.onyx.edu.note.data.ScribbleFunctionMenuIDType;
import com.onyx.edu.note.data.ScribbleSubMenuID;
import com.onyx.edu.note.databinding.PageMenuItemBinding;
import com.onyx.edu.note.scribble.PageMenuItemViewHolder;
import com.onyx.edu.note.scribble.PageMenuItemViewModel;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by lxm on 2017/9/1.
 */

public class SubMenuViewModel extends BaseMenuViewModel {

    public final ObservableInt subMenuWidth = new ObservableInt();

    public SubMenuViewModel(EventBus eventBus) {
        super(eventBus);
    }

    public void calculateMenuWidth(final View parent) {
        parent.post(new Runnable() {
            @Override
            public void run() {
                int width = parent.getMeasuredWidth();
                int value = width / parent.getContext().getResources().getInteger(R.integer.note_menu_columns);
                subMenuWidth.set(value);
            }
        });
    }

}
