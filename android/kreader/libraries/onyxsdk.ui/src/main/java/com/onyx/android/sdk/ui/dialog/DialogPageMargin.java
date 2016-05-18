package com.onyx.android.sdk.ui.dialog;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.data.GAdapter;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.data.util.GAdapterUtil;
import com.onyx.android.sdk.ui.ContentItemView;
import com.onyx.android.sdk.ui.ContentView;

import java.io.File;
import java.util.HashMap;

/**
 * Created by solskjaer49 on 15/11/20 14:28.
 */
public class DialogPageMargin extends DialogBaseOnyx {
    public static final int[] sPageMarginsArray = new int[]{0, 5, 10, 15, 20, 25, 30, 35, 40};
    private static final String TAG = DialogPageMargin.class.getSimpleName();
    private ImageView buttonNext, buttonPrevious;
    private TextView textViewPage = null;
    private ContentView contentView;
    private int rowCount,currentItem;
    private OnPageMarginsListener listener;

    public interface OnPageMarginsListener {
        void onSelectPageMargins(int pageMargins);

        void onSet(int value);
    }

    public DialogPageMargin(Context context, int currentMargin, OnPageMarginsListener l) {
        super(context, R.style.CustomDialog);
        listener = l;
        initView();
        initData(buildAdapter(currentMargin));
    }

    private void initView() {
        this.setContentView(R.layout.dialog_page_margin_setting);
        RelativeLayout dialogTittleBar = (RelativeLayout) findViewById(R.id.dialog_tittleBar);
        contentView = (ContentView) this.findViewById(R.id.page_margin_contentView);
        buttonNext = (ImageView) dialogTittleBar.findViewById(R.id.button_next);
        buttonPrevious = (ImageView) dialogTittleBar.findViewById(R.id.button_previous);
        textViewPage = (TextView) dialogTittleBar.findViewById(R.id.page_size_indicator);
        final TextView dialogTittle = (TextView) dialogTittleBar.findViewById(R.id.textView_title);
        dialogTittle.setText(R.string.page_margins);
        contentView.setShowPageInfoArea(false);
        Button mButtonConfirm = (Button) findViewById(R.id.btn_set);
        Button mButtonCancel = (Button) findViewById(R.id.btn_cancel);
        contentView.setCallback(new ContentView.ContentViewCallback() {
            @Override
            public void afterPageChanged(ContentView contentView, int newPage, int oldPage) {
                setUpDialogHeightAsRowCount(contentView);
                updateTextViewPage();
            }

            @Override
            public void onItemClick(ContentItemView view) {
                GObject temp = view.getData();
                int dataIndex = contentView.getCurrentAdapter().getGObjectIndex(temp);
                temp.putBoolean(GAdapterUtil.TAG_SELECTABLE, true);
                contentView.getCurrentAdapter().setObject(dataIndex, temp);
                contentView.unCheckOtherViews(dataIndex, true);
                contentView.updateCurrentPage();
                currentItem = view.getData().getInt(GAdapterUtil.TAG_UNIQUE_ID);
                listener.onSelectPageMargins(currentItem);
            }

        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentView.nextPage();
            }
        });
        buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentView.prevPage();
            }
        });
        mButtonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSet(currentItem);
                dismiss();
            }
        });
        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSet(-1);
                dismiss();
            }
        });
    }


    private void setIsShowPagingButton() {
        if (contentView.getTotalPageCount() > 1) {
            buttonPrevious.setVisibility(View.VISIBLE);
            buttonNext.setVisibility(View.VISIBLE);
        } else {
            buttonPrevious.setVisibility(View.GONE);
            buttonNext.setVisibility(View.GONE);
        }
    }

    private void updateTextViewPage() {
        textViewPage.setText((contentView.getCurrentPage() + 1) + File.separator + contentView.getTotalPageCount());
    }

    private void initData(GAdapter adapter) {
        HashMap<String, Integer> mapping = new HashMap<String, Integer>();
        mapping.put(GAdapterUtil.TAG_TITLE_STRING, R.id.textview_title);
        mapping.put(GAdapterUtil.TAG_TYPEFACE, R.id.textview_title);
        mapping.put(GAdapterUtil.TAG_DIVIDER_VIEW, R.id.divider);
        mapping.put(GAdapterUtil.TAG_SELECTABLE, R.id.radio_selected);
        rowCount =  adapter.size() < getContext().getResources().getInteger(R.integer.dialog_page_margin_setting_max_row_per_page)
                ? adapter.size() : getContext().getResources().getInteger(R.integer.dialog_page_margin_setting_max_row_per_page);
        contentView.setupGridLayout(rowCount, 1);
        contentView.setSubLayoutParameter(R.layout.dialog_page_margin_setting_item, mapping);
        contentView.setBlankAreaAnswerLongClick(false);
        contentView.setAdapter(adapter, 0);
        setIsShowPagingButton();
        updateTextViewPage();
    }

    @Override
    public void show() {
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        DisplayMetrics dm = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        lp.width = (dm.widthPixels * 3) / 4;
        lp.height = (dm.heightPixels * 3) / 4;
        this.getWindow().setAttributes(lp);
        super.show();
        //TODO:use hide to avoid contentView size change which will cause dirty region.truly show after all size set.
        super.hide();
    }

    private GAdapter buildAdapter(int currentMargin){
        GAdapter adapter = new GAdapter();
        GObject object;
        for (int item : sPageMarginsArray) {
            object = new GObject();
            object.putString(GAdapterUtil.TAG_TITLE_STRING, Integer.toString(item));
            object.putInt(GAdapterUtil.TAG_UNIQUE_ID, item);
            if (currentMargin == item) {
                object.putBoolean(GAdapterUtil.TAG_SELECTED, true);
            }
            adapter.addObject(object);
        }
        return adapter;
    }

    private void setUpDialogHeightAsRowCount(ContentView contentView) {
        int current_height, target_height;
        current_height = contentView.getHeight();
        target_height = rowCount * (int) getContext().getResources().getDimension(R.dimen.dialog_page_setting_setting_per_item_height);
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        if (current_height != target_height) {
            lp.height = lp.height - current_height + target_height;
            this.getWindow().setAttributes(lp);
            return;
        }
        super.show();
    }
}
