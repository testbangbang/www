package com.onyx.jdread.library.view;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.DialogMoveToLibraryListLayoutBinding;
import com.onyx.jdread.library.adapter.LibraryListDialogAdapter;

import java.util.List;
import java.util.Observable;

/**
 * Created by hehai on 17-12-19.
 */

public class MoveToLibraryListDialog extends Dialog {
    public MoveToLibraryListDialog(@NonNull Context context) {
        super(context);
    }

    public MoveToLibraryListDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {
        private Context context;
        private MoveToLibraryListDialog.DialogModel model;

        public Builder(Context context, MoveToLibraryListDialog.DialogModel model) {
            this.context = context;
            this.model = model;
        }

        public MoveToLibraryListDialog create() {
            final MoveToLibraryListDialog dialog = new MoveToLibraryListDialog(context, R.style.CustomDialogStyle);
            DialogMoveToLibraryListLayoutBinding bind = DataBindingUtil.bind(View.inflate(context, R.layout.dialog_move_to_library_list_layout, null));
            bind.setDialogModel(model);
            PageRecyclerView recycler = bind.recycler;
            recycler.setLayoutManager(new DisableScrollGridManager(context.getApplicationContext()));
            LibraryListDialogAdapter adapter = new LibraryListDialogAdapter();
            adapter.setRowAndCol(model.row, model.col);
            recycler.setAdapter(adapter);
            adapter.setItemClickListener(new LibraryListDialogAdapter.ItemClickListener() {
                @Override
                public void onItemClicked(DataModel dataModel) {
                    model.listener.onItemClicked(dataModel);
                    dialog.dismiss();
                }
            });

            recycler.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
                @Override
                public void onPageChange(int position, int itemCount, int pageSize) {
                    model.focusPosition.set(position / pageSize);
                }
            });
            bind.closeMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.setContentView(bind.getRoot());
            return dialog;
        }
    }

    public static class DialogModel extends Observable {
        public final ObservableList<DataModel> items = new ObservableArrayList<>();
        public final ObservableInt totalPage = new ObservableInt();
        public final ObservableInt focusPosition = new ObservableInt();
        private int row = 5;
        private int col = 1;
        private ClickListener listener;

        public interface ClickListener {
            void onItemClicked(DataModel dataModel);

            void onBuildLibraryClicked();
        }

        public void onBuildLibraryClicked() {
            listener.onBuildLibraryClicked();
        }

        public DialogModel(int row, int col, List<DataModel> list) {
            this.row = row;
            this.col = col;
            items.addAll(list);
            totalPage.set(getTotalPage(list.size(), row));
        }

        public void setListener(ClickListener listener) {
            this.listener = listener;
        }

        private int getTotalPage(int total, int row) {
            if (total % row == 0) {
                return total / row;
            } else {
                return total / row + 1;
            }
        }
    }

    @Override
    public void show() {
        Window window = getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = getContext().getResources().getInteger(R.integer.move_to_library_list_dialog_width);
        attributes.height = getContext().getResources().getInteger(R.integer.move_to_library_list_dialog_height);
        window.setAttributes(attributes);
        super.show();
    }
}
