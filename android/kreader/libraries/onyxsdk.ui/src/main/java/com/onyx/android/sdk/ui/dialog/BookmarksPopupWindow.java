package com.onyx.android.sdk.ui.dialog;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.ui.DirectoryGridView;
import com.onyx.android.sdk.ui.data.DirectoryItem;
import com.onyx.android.sdk.ui.data.GridViewAnnotationAdapter;
import com.onyx.android.sdk.ui.data.GridViewDirectoryAdapter;
import com.onyx.android.sdk.ui.dialog.DialogAnnotation.AnnotationAction;
import com.onyx.android.sdk.ui.dialog.DialogAnnotation.onUpdateAnnotationListener;
import com.onyx.android.sdk.ui.dialog.DialogDirectory.IEditPageHandler;
import com.onyx.android.sdk.ui.dialog.DialogDirectory.IGotoPageHandler;
import com.onyx.android.sdk.ui.dialog.data.AnnotationItem;

/**
 * @author cap
 */
public class BookmarksPopupWindow extends PopupWindow implements View.OnKeyListener {

    public enum TOC_MODE {
        BOOKMARK_MODE, ANNOTATION_MODE
    }

    private TOC_MODE mCurrentMode;

    private DialogDirectory mDialogDirectory = null;
    private DirectoryItem mDirectoryItem = null;
    private IGotoPageHandler mIGotoPageHandler = null;
    private IEditPageHandler mIEditPageHandler = null;

    private DirectoryGridView mGridView = null;
    private int mPosition = -1;

    private View mView;
    private ImageView mGoToButton = null;
    private ImageView mEditButton = null;
    private ImageView mDeleButton = null;

    private final Context mContext;

    public BookmarksPopupWindow(Context context, View contentView, DialogDirectory dialogDirectory,
                                IGotoPageHandler iGoto, IEditPageHandler iEdit, TOC_MODE mRequestMode) {
        super(contentView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, false);
        mContext = context;
        mDialogDirectory = dialogDirectory;
        mIGotoPageHandler = iGoto;
        mIEditPageHandler = iEdit;
        mView = contentView;
        this.setBackgroundDrawable(new BitmapDrawable());
        mGoToButton = (ImageView) contentView.findViewById(R.id.goTo_Button);
        mEditButton = (ImageView) contentView.findViewById(R.id.edit_Button);
        mDeleButton = (ImageView) contentView.findViewById(R.id.delete_Button);
        mCurrentMode = mRequestMode;
        if (mRequestMode == TOC_MODE.BOOKMARK_MODE) {
            mEditButton.setVisibility(View.GONE);
        }
        setClickListener();
    }

    public void setDirectoryItem(DirectoryItem item, DirectoryGridView onyxGridView, int position) {
        mDirectoryItem = item;
        mGridView = onyxGridView;
        this.mPosition = position;
        mGoToButton.requestFocus();
    }

    private void setClickListener() {
        mGoToButton.setOnKeyListener(this);
        mEditButton.setOnKeyListener(this);
        mDeleButton.setOnKeyListener(this);
        mGoToButton.setFocusable(true);
        mEditButton.setFocusable(true);
        mDeleButton.setFocusable(true);
        mGoToButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mDialogDirectory.dismiss();
                switch (mCurrentMode) {
                    case BOOKMARK_MODE:
                        mIGotoPageHandler.jumpBookmark(mDirectoryItem);
                        break;
                    case ANNOTATION_MODE:
                        mIGotoPageHandler.jumpAnnotation(mDirectoryItem);
                        break;
                }
            }
        });


        mEditButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                BookmarksPopupWindow.this.dismiss();
                String note = mDirectoryItem.getTitle();
                DialogAnnotation dialogAnnotation = new DialogAnnotation(mContext, AnnotationAction.onlyUpdate, note);
                dialogAnnotation.setOnUpdateAnnotationListener(new onUpdateAnnotationListener() {

                    @Override
                    public void updateAnnotation(String note) {
                        GridViewAnnotationAdapter annotationAdapter = (GridViewAnnotationAdapter) (mGridView.getGridView().getAdapter());
                        AnnotationItem annotation = new AnnotationItem(note, mDirectoryItem.getPage(), mDirectoryItem.getTag(), null);
                        annotationAdapter.update(annotation, mPosition);
                        mIEditPageHandler.editAnnotation(annotation);
                    }
                });
                dialogAnnotation.show();
            }
        });

        mDeleButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogDeleteConfirm mDialog = new DialogDeleteConfirm(mContext);
                mDialog.setCanceledOnTouchOutside(true);
                mDialog.setTittle(R.string.confirm).setMessage(R.string.confirm_delete);
                switch (mCurrentMode) {
                    case BOOKMARK_MODE:
                        mDialog.setButtonOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mIEditPageHandler.deleteBookmark(mDirectoryItem);
                                GridViewDirectoryAdapter bookMarkAdapter = (GridViewDirectoryAdapter) (mGridView.getGridView().getAdapter());
                                bookMarkAdapter.remove(mPosition);
                                mDialog.dismiss();
                            }
                        }, new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();
                            }
                        });
                        break;
                    case ANNOTATION_MODE:
                        mDialog.setButtonOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mIEditPageHandler.deleteAnnotation(mDirectoryItem);
                                GridViewAnnotationAdapter annotationAdapter = (GridViewAnnotationAdapter) (mGridView.getGridView().getAdapter());
                                annotationAdapter.remove(mPosition);
                                mDialog.dismiss();
                            }
                        }, new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();
                            }
                        });
                        break;
                }
                mDialog.show();
                BookmarksPopupWindow.this.dismiss();
            }
        });
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (BookmarksPopupWindow.this.isShowing()) {
                BookmarksPopupWindow.this.dismiss();
                return true;
            }
        }
        return false;
    }

    public TOC_MODE getMode() {
        return mCurrentMode;
    }

    public void switchMode(TOC_MODE mMode) {
        mCurrentMode = mMode;
        switch (mMode) {
            case BOOKMARK_MODE:
                mEditButton.setVisibility(View.GONE);
                break;
            case ANNOTATION_MODE:
                mEditButton.setVisibility(View.VISIBLE);
                break;
        }
    }
}
