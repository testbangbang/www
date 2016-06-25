package com.onyx.android.note.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.onyx.android.note.R;
import com.onyx.android.sdk.data.GAdapter;
import com.onyx.android.sdk.data.GAdapterUtil;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;
import com.onyx.android.sdk.ui.utils.SelectionMode;
import com.onyx.android.sdk.ui.view.ContentItemView;
import com.onyx.android.sdk.ui.view.ContentView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;


public class ManageActivity extends OnyxAppCompatActivity {
    private static final String TAG_CONTENT_ID = "content_id";
    private static final String TAG_CONTENT_TAG = "content_tag";
    private SimpleDateFormat dateFormat;
    private
    @SelectionMode.SelectionModeDef
    int currentSelectMode = SelectionMode.NORMAL_MODE;
    private int currentPage;

    TextView chooseModeButton, addFolderButton, cutButton, deleteButton;
    ArrayList<GObject> mChosenItemsList = new ArrayList<GObject>();
    ContentView contentView;
    GAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        loadData();
    }

    private void initView() {
        initSupportActionBar();
        getSupportActionBar().setTitle(ManageActivity.class.getSimpleName());
        chooseModeButton = (TextView) findViewById(R.id.selectMode);
        chooseModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (currentSelectMode) {
                    case SelectionMode.NORMAL_MODE:
                        switchMode(SelectionMode.MULTISELECT_MODE);
                        break;
                    case SelectionMode.MULTISELECT_MODE:
                        switchMode(SelectionMode.NORMAL_MODE);
                        break;
                    default:
                        break;
                }
            }
        });
        contentView = (ContentView) findViewById(R.id.note_content_view);
        contentView.setBlankAreaAnswerLongClick(false);
        contentView.setupGridLayout(3, 3);
        contentView.setShowPageInfoArea(false);
        contentView.setCallback(new ContentView.ContentViewCallback() {
            @Override
            public void onItemClick(ContentItemView view) {
                switch (currentSelectMode) {
                    case SelectionMode.NORMAL_MODE:
//                        if (GAdapterUtil.isDirectory(view.getData())) {
//                            return;
//                        }
                        if (view.getData().getString(GAdapterUtil.TAG_TITLE_STRING).equalsIgnoreCase("0")) {
                            startActivity(new Intent(ManageActivity.this, ScribbleActivity.class));
                        }
                        break;
                    case SelectionMode.MULTISELECT_MODE:
                        GObject temp = view.getData();
                        int dataIndex = mAdapter.getGObjectIndex(temp);
                        if (view.getData().getBoolean(GAdapterUtil.TAG_SELECTABLE, false)) {
                            temp.putBoolean(GAdapterUtil.TAG_SELECTABLE, false);
                            mChosenItemsList.remove(temp);
                        } else {
                            temp.putBoolean(GAdapterUtil.TAG_SELECTABLE, true);
                            mChosenItemsList.add(temp);
                        }
                        mAdapter.setObject(dataIndex, temp);
                        contentView.updateCurrentPage();
                        contentView.setCustomInfo(Integer.toString(mChosenItemsList.size()), true);
                        break;
                }
            }

        });
    }

    private void loadData() {
        contentView.setSubLayoutParameter(R.layout.scribble_item, getItemViewDataMap(currentSelectMode));
        contentView.setAdapter(getTestAdapter(), 0);
    }

    private HashMap<String, Integer> getItemViewDataMap(@SelectionMode.SelectionModeDef int mode) {
        HashMap<String, Integer> mapping = new HashMap<String, Integer>();
        switch (mode) {
            case SelectionMode.PASTE_MODE:
            case SelectionMode.NORMAL_MODE:
                mapping = new HashMap<>();
                mapping.put(GAdapterUtil.TAG_IMAGE_RESOURCE, R.id.imageview_bg);
                mapping.put(GAdapterUtil.TAG_TITLE_STRING, R.id.textview_title);
                break;
            case SelectionMode.MULTISELECT_MODE:
                mapping = new HashMap<>();
                mapping.put(GAdapterUtil.TAG_IMAGE_RESOURCE, R.id.imageview_bg);
                mapping.put(GAdapterUtil.TAG_TITLE_STRING, R.id.textview_title);
                mapping.put(GAdapterUtil.TAG_SELECTABLE, R.id.checkbox_multi_select);
                break;
        }
        return mapping;
    }

    private GObject createFilterItem(String num, int imageRes) {
        return GAdapterUtil.createTableItem(num, null, imageRes, 0, null);
    }

    private GAdapter getNoteAdapter() {
        if (mAdapter == null) {
            mAdapter = new GAdapter();
        }
        return mAdapter;
    }

    private GAdapter getTestAdapter() {
        if (mAdapter == null) {
            mAdapter = new GAdapter();
            mAdapter.addObject(createFilterItem(Integer.toString(0), R.drawable.ic_student_note_plus_gray_250dp));
            for (int i = 1; i < 4; i++) {
                mAdapter.addObject(createFilterItem(Integer.toString(i), R.drawable.ic_student_note_folder_gray_250dp));
            }
            for (int i = 4; i < 256; i++) {
                if (i % 2 == 0) {
                    mAdapter.addObject(createFilterItem(Integer.toString(i), R.drawable.ic_student_note_doc_gray_250dp));
                } else {
                    mAdapter.addObject(createFilterItem(Integer.toString(i), R.drawable.ic_student_note_pic_gray_250dp));
                }
            }
        }
        return mAdapter;
    }

    private void switchMode(@SelectionMode.SelectionModeDef int selectionMode) {
        currentPage = contentView.getCurrentPage();
        currentSelectMode = selectionMode;
        switch (selectionMode) {
            case SelectionMode.NORMAL_MODE:
                switchToNormalMode();
                break;
            case SelectionMode.MULTISELECT_MODE:
                switchToMultiSelectionMode();
                break;
        }
    }

    private void switchToNormalMode() {
        contentView.setSubLayoutParameter(R.layout.scribble_item,
                getItemViewDataMap(SelectionMode.NORMAL_MODE));
        contentView.setupContent(3, 3, getTestAdapter(), 0, true);
        contentView.unCheckAllViews();
    }

    private void switchToMultiSelectionMode() {
        contentView.setSubLayoutParameter(R.layout.scribble_item,
                getItemViewDataMap(SelectionMode.MULTISELECT_MODE));
        contentView.setupContent(3, 3, getTestAdapter(), 0, true);
    }
}
