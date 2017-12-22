package com.onyx.jdread.library.view;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.data.model.ModelType;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.DialogSingleManageLayoutBinding;
import com.onyx.jdread.library.event.DeleteBookEvent;
import com.onyx.jdread.library.event.LibraryDeleteEvent;
import com.onyx.jdread.library.event.LibraryDeleteIncludeBookEvent;
import com.onyx.jdread.library.event.LibraryRenameEvent;
import com.onyx.jdread.library.event.MoveToLibraryEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.Observable;


/**
 * Created by 12 on 2016/12/14.
 */

public class SingleItemManageDialog extends Dialog {
    private SingleItemManageDialog(Context context) {
        super(context);
    }

    private SingleItemManageDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {
        private Context context;
        private DialogModel model;

        public Builder(Context context, DialogModel model) {
            this.context = context;
            this.model = model;
        }

        public SingleItemManageDialog create() {
            final SingleItemManageDialog dialog = new SingleItemManageDialog(context, R.style.CustomDialogStyle);
            DialogSingleManageLayoutBinding bind = DataBindingUtil.bind(View.inflate(context, R.layout.dialog_single_manage_layout, null));
            bind.setDialogModel(model);
            bind.closeMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            bind.bookDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            bind.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    model.getEventBus().post(new DeleteBookEvent());
                }
            });
            bind.moveTo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    model.getEventBus().post(new MoveToLibraryEvent());
                }
            });
            bind.renameLibrary.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    model.getEventBus().post(new LibraryRenameEvent(model.dataModel.get()));
                }
            });
            bind.deleteLibrary.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    model.getEventBus().post(new LibraryDeleteEvent(model.dataModel.get()));
                }
            });
            bind.deleteLibraryIncludeBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    model.getEventBus().post(new LibraryDeleteIncludeBookEvent(model.dataModel.get()));
                }
            });
            dialog.setContentView(bind.getRoot());
            return dialog;
        }
    }

    public static class DialogModel extends Observable {
        public final ObservableField<DataModel> dataModel = new ObservableField<>();
        private EventBus eventBus;

        public DialogModel(EventBus eventBus) {
            this.eventBus = eventBus;
        }

        public EventBus getEventBus() {
            return eventBus;
        }

        public boolean showBookMenu() {
            return dataModel.get().type.get() == ModelType.TYPE_METADATA;
        }

        public boolean showLibraryMenu() {
            return dataModel.get().type.get() == ModelType.TYPE_LIBRARY;
        }
    }

    @Override
    public void show() {
        Window window = getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = getContext().getResources().getInteger(R.integer.library_delete_dialog_width);
        attributes.height = getContext().getResources().getInteger(R.integer.single_manage_dialog_height);
        window.setAttributes(attributes);
        super.show();
    }
}
