package com.onyx.kreader.ui.dialog;

import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.onyx.android.sdk.data.GAdapter;
import com.onyx.android.sdk.data.GAdapterUtil;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.ui.view.ContentItemView;
import com.onyx.android.sdk.ui.view.ContentView;
import com.onyx.kreader.R;
import com.onyx.kreader.ui.actions.ChangeNavigationSettingsAction;
import com.onyx.kreader.ui.data.ReaderCropArgs;
import com.onyx.kreader.ui.data.ReaderDataHolder;

import java.util.HashMap;

/**
 * Created by joy on 3/5/15.
 */
public class DialogNavigationSettings extends DialogBase {

    private final static String TAG = DialogNavigationSettings.class.getSimpleName();

    public enum SubScreenMode {
        // ROWS X COLUMNS
        SUB_SCREEN_1_1,
        SUB_SCREEN_1_2,
        SUB_SCREEN_1_3,
        SUB_SCREEN_2_1,
        SUB_SCREEN_2_2,
        SUB_SCREEN_2_3,
        SUB_SCREEN_3_1,
        SUB_SCREEN_3_2,
        SUB_SCREEN_3_3,
    }

    public static SubScreenMode[][] SubScreenModeMatrix = new SubScreenMode[][] {
        { SubScreenMode.SUB_SCREEN_1_1, SubScreenMode.SUB_SCREEN_1_2, SubScreenMode.SUB_SCREEN_1_3},
            { SubScreenMode.SUB_SCREEN_2_1, SubScreenMode.SUB_SCREEN_2_2, SubScreenMode.SUB_SCREEN_3_3},
            { SubScreenMode.SUB_SCREEN_3_1, SubScreenMode.SUB_SCREEN_3_2, SubScreenMode.SUB_SCREEN_3_3},
    };

    private static abstract class ObjectSelectedCallback {
        public abstract void onObjectSelected(GObject object);
    }

    private ReaderDataHolder readerDataHolder;
    private ContentView mCropContentView;
    private ContentView mSubScreenContentView;
    private ContentView mNavigationContentView;
    private GAdapter mCropAdapter;
    private GAdapter mSubScreenAdapter;
    private GAdapter mNavigationAdapter;
    private boolean autoCropForEachBlock = false;
    private CheckBox autoCropForEachBlockCheckBox;
    private ReaderCropArgs.CropPageMode currentChoosingCropMode = ReaderCropArgs.CropPageMode.None;
    private ReaderCropArgs.CropPageMode currentUsingCropMode = ReaderCropArgs.CropPageMode.None;
    private SubScreenMode currentChoosingSubScreenMode = SubScreenMode.SUB_SCREEN_1_1;
    private SubScreenMode currentUsingSubScreenMode = SubScreenMode.SUB_SCREEN_1_1;
    private ReaderCropArgs navigationArgs = new ReaderCropArgs();
    private ReaderCropArgs.NavigationMode currentChoosingNavigationMode =
            ReaderCropArgs.NavigationMode.ROWS_LEFT_TO_RIGHT_MODE;
    private ReaderCropArgs.NavigationMode currentUsingNavigationMode =
            ReaderCropArgs.NavigationMode.ROWS_LEFT_TO_RIGHT_MODE;

    public DialogNavigationSettings(ReaderDataHolder readerDataHolder) {
        super(readerDataHolder.getContext());
        this.readerDataHolder = readerDataHolder;
        initLayout();
    }

    private void initLayout() {
        setContentView(R.layout.dialog_navigation_settings);
        currentChoosingCropMode = ReaderCropArgs.CropPageMode.AUTO_CROP_PAGE;
        currentChoosingSubScreenMode = getSubScreenModeFromReader(2, 2);
        currentChoosingNavigationMode = ReaderCropArgs.NavigationMode.ROWS_LEFT_TO_RIGHT_MODE;
        currentUsingCropMode = ReaderCropArgs.CropPageMode.AUTO_CROP_PAGE;
        currentUsingSubScreenMode = getSubScreenModeFromReader(2, 2);
        currentUsingNavigationMode = ReaderCropArgs.NavigationMode.ROWS_LEFT_TO_RIGHT_MODE;

        mCropContentView = (ContentView) findViewById(R.id.crop_contentView);
        autoCropForEachBlockCheckBox = (CheckBox) findViewById(R.id.auto_crop_for_each_block);
        setupContentView(mCropContentView, R.layout.dialog_navigation_settings_mode_icon_view, new ObjectSelectedCallback() {
            @Override
            public void onObjectSelected(GObject object) {
                currentChoosingCropMode = getCropPageMode(object);
            }
        });

        mSubScreenContentView = (ContentView) findViewById(R.id.subscreen_contentView);
        int subScreenRows = getContext().getResources().getInteger(R.integer.dialog_navigation_setting_sub_screen_rows);
        int subScreenColumns = getContext().getResources().getInteger(R.integer.dialog_navigation_setting_sub_screen_columns);
        if (getContext().getResources().getConfiguration().orientation == 2) {
            int landscapeRows = getContext().getResources().getInteger(R.integer.dialog_navigation_setting_landscape_sub_screen_rows);
            int landscapeColumns = getContext().getResources().getInteger(R.integer.dialog_navigation_setting_landscape_sub_screen_columns);
            if (landscapeRows > 0 && landscapeColumns > 0) {
                double heightRatio = landscapeRows / (double)subScreenRows;
                subScreenRows = landscapeRows;
                subScreenColumns = landscapeColumns;
                // adjust content view's height account to the real row number
                mSubScreenContentView.getLayoutParams().height *= heightRatio;
            }
        }
        setupContentView(mSubScreenContentView, subScreenRows, subScreenColumns, R.layout.dialog_navigation_settings_mode_icon_no_text_view, new ObjectSelectedCallback() {
            @Override
            public void onObjectSelected(GObject object) {
                currentChoosingSubScreenMode = getSubScreenMode(object);
                buildNavigationModeAdapter();
                updateNavigationMode();
                mNavigationContentView.setAdapter(mNavigationAdapter, 0);
            }
        });

        mNavigationContentView = (ContentView) findViewById(R.id.navigation_contentView);
        setupContentView(mNavigationContentView, R.layout.dialog_navigation_settings_mode_icon_no_text_view, new ObjectSelectedCallback() {
            @Override
            public void onObjectSelected(GObject object) {
                currentChoosingNavigationMode = getNavigationMode(object);
            }
        });

        findViewById(R.id.buttonConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationArgs.setNavigationMode(currentChoosingNavigationMode);
                navigationArgs.setCropPageMode(currentChoosingCropMode);
                navigationArgs.setRows(getRowNumber());
                navigationArgs.setColumns(getColumnNumber());
                new ChangeNavigationSettingsAction(navigationArgs, autoCropForEachBlock).execute(readerDataHolder, null);
                DialogNavigationSettings.this.dismiss();
            }
        });
        findViewById(R.id.buttonCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogNavigationSettings.this.dismiss();
            }
        });
        autoCropForEachBlockCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                autoCropForEachBlock = isChecked;
            }
        });
        autoCropForEachBlockCheckBox.setChecked(readerDataHolder.getReaderViewInfo().autoCropForEachBlock);
        buildCropModeAdapter();
        buildSubScreenAdapter();
        buildNavigationModeAdapter();
        mCropContentView.setAdapter(mCropAdapter, 0);
        mSubScreenContentView.setAdapter(mSubScreenAdapter, 0);
        mNavigationContentView.setAdapter(mNavigationAdapter, 0);
    }

    private void setupContentView(final ContentView view, int rows, int columns, int layoutId, final ObjectSelectedCallback callback) {
        view.setSubLayoutParameter(layoutId, getDefaultMap());
        view.setShowPageInfoArea(false);
        view.setupGridLayout(rows, columns);
        view.setCallback(new ContentView.ContentViewCallback() {
            @Override
            public void onItemClick(ContentItemView v) {
                GObject object = v.getData();
                int dataIndex = view.getCurrentAdapter().getGObjectIndex(object);
                setSelected(object, true);
                view.getCurrentAdapter().setObject(dataIndex, object);
                view.unCheckOtherViews(dataIndex);
                view.updateCurrentPage();
                if (callback != null) {
                    callback.onObjectSelected(object);
                }
            }
        });
    }

    private void setupContentView(final ContentView view, int layoutId, final ObjectSelectedCallback callback) {
        setupContentView(view, 1, 6, layoutId, callback);
    }

    private HashMap<String, Integer> getDefaultMap() {
        HashMap<String, Integer> mapping = new HashMap<>();
        mapping.put(GAdapterUtil.TAG_IMAGE_RESOURCE, R.id.mode_icon);
        mapping.put(GAdapterUtil.TAG_SELECTABLE, R.id.checkbox_current_select);
        mapping.put(GAdapterUtil.TAG_TITLE_STRING, R.id.text_view_title);
        return mapping;
    }

    private GObject createNavigationModeItem(int iconResource, ReaderCropArgs.NavigationMode mode) {
        GObject object = GAdapterUtil.createTableItem("", "", iconResource, 0, null);
        setNavigationMode(object, mode);
        if (mode == currentChoosingNavigationMode) {
            setSelected(object, true);
        } else {
            setSelected(object, false);
        }
        return object;
    }

    private GObject createCropPageModeItem(int iconResource, String title,ReaderCropArgs.CropPageMode mode) {
        GObject object = GAdapterUtil.createTableItem(title, "", iconResource, 0, null);
        setCropPageMode(object, mode);
        if (mode == currentChoosingCropMode) {
            setSelected(object, true);
        } else {
            setSelected(object, false);
        }
        return object;
    }

    private GObject createSubScreenModeItem(int iconResource, SubScreenMode mode) {
        GObject object = GAdapterUtil.createTableItem("", "", iconResource, 0, null);
        setSubScreenMode(object, mode);
        if (mode == currentChoosingSubScreenMode) {
            setSelected(object, true);
        } else {
            setSelected(object, false);
        }
        return object;
    }


    private GAdapter buildCropModeAdapter() {
        mCropAdapter = new GAdapter();
        mCropAdapter.addObject(createCropPageModeItem(R.drawable.ic_dialog_reader_browse_cut_four,getContext().getString(R.string.auto), ReaderCropArgs.CropPageMode.AUTO_CROP_PAGE));
        mCropAdapter.addObject(createCropPageModeItem(R.drawable.ic_dialog_reader_browse_cut_single,getContext().getString(R.string.single_page), ReaderCropArgs.CropPageMode.MANUAL_CROP_PAGE));
        mCropAdapter.addObject(createCropPageModeItem(R.drawable.ic_dialog_reader_browse_cut_double,getContext().getString(R.string.double_page), ReaderCropArgs.CropPageMode.MANUAL_CROP_PAGE_BY_ODD_AND_EVEN));
        mCropAdapter.addObject(createCropPageModeItem(R.drawable.ic_dialog_reader_browse_cut_nothing,getContext().getString(R.string.nothing), ReaderCropArgs.CropPageMode.None));
        return mCropAdapter;
    }

    private GAdapter buildSubScreenAdapter() {
        mSubScreenAdapter = new GAdapter();
        mSubScreenAdapter.addObject(createSubScreenModeItem(R.drawable.ic_dialog_navigation_settings_subscreen_1_1, SubScreenMode.SUB_SCREEN_1_1));
        mSubScreenAdapter.addObject(createSubScreenModeItem(R.drawable.ic_dialog_navigation_settings_subscreen_1_2, SubScreenMode.SUB_SCREEN_1_2));
        mSubScreenAdapter.addObject(createSubScreenModeItem(R.drawable.ic_dialog_navigation_settings_subscreen_2_1, SubScreenMode.SUB_SCREEN_2_1));
        mSubScreenAdapter.addObject(createSubScreenModeItem(R.drawable.ic_dialog_navigation_settings_subscreen_2_2, SubScreenMode.SUB_SCREEN_2_2));
        mSubScreenAdapter.addObject(createSubScreenModeItem(R.drawable.ic_dialog_navigation_settings_subscreen_3_2, SubScreenMode.SUB_SCREEN_3_2));
        mSubScreenAdapter.addObject(createSubScreenModeItem(R.drawable.ic_dialog_navigation_settings_subscreen_2_3, SubScreenMode.SUB_SCREEN_2_3));
        mSubScreenAdapter.addObject(createSubScreenModeItem(R.drawable.ic_dialog_navigation_settings_subscreen_3_3, SubScreenMode.SUB_SCREEN_3_3));
        return mSubScreenAdapter;
    }

    private GAdapter buildNavigationModeAdapter() {
        mNavigationAdapter = new GAdapter();
        switch (currentChoosingSubScreenMode) {
            case SUB_SCREEN_1_1:
                mNavigationAdapter.addObject(createNavigationModeItem(R.drawable.ic_dialog_navigation_settings_navigation_normal,
                        ReaderCropArgs.NavigationMode.SINGLE_PAGE_MODE));
                break;
            case SUB_SCREEN_1_2:
                mNavigationAdapter.addObject(createNavigationModeItem(R.drawable.ic_dialog_navigation_settings_rows_left_to_right,
                        ReaderCropArgs.NavigationMode.ROWS_LEFT_TO_RIGHT_MODE));
                mNavigationAdapter.addObject(createNavigationModeItem(R.drawable.ic_dialog_navigation_settings_rows_right_to_left,
                        ReaderCropArgs.NavigationMode.ROWS_RIGHT_TO_LEFT_MODE));
                break;
            case SUB_SCREEN_2_1:
            case SUB_SCREEN_3_1:
                mNavigationAdapter.addObject(createNavigationModeItem(R.drawable.ic_dialog_navigation_settings_columns_left_to_right,
                        ReaderCropArgs.NavigationMode.COLUMNS_LEFT_TO_RIGHT_MODE));
                break;
            default:
                mNavigationAdapter.addObject(createNavigationModeItem(R.drawable.ic_dialog_navigation_settings_rows_left_to_right,
                        ReaderCropArgs.NavigationMode.ROWS_LEFT_TO_RIGHT_MODE));
                mNavigationAdapter.addObject(createNavigationModeItem(R.drawable.ic_dialog_navigation_settings_rows_right_to_left,
                        ReaderCropArgs.NavigationMode.ROWS_RIGHT_TO_LEFT_MODE));
                mNavigationAdapter.addObject(createNavigationModeItem(R.drawable.ic_dialog_navigation_settings_columns_left_to_right,
                        ReaderCropArgs.NavigationMode.COLUMNS_LEFT_TO_RIGHT_MODE));
                mNavigationAdapter.addObject(createNavigationModeItem(R.drawable.ic_dialog_navigation_settings_columns_right_to_left,
                        ReaderCropArgs.NavigationMode.COLUMNS_RIGHT_TO_LEFT_MODE));
                break;
        }
        return mNavigationAdapter;
    }

    private void updateNavigationMode() {
        if (mNavigationAdapter.size() <= 0) {
            throw new IllegalStateException();
        }
        GObject object = getSelectedObject(mNavigationAdapter);
        if (object != null) {
            setSelected(object, false);
        }
        setSelected(mNavigationAdapter.get(0), true);
        currentChoosingNavigationMode = getNavigationMode(mNavigationAdapter.get(0));
    }

    private GObject getSelectedObject(GAdapter adapter) {
        for (GObject o : adapter.getList()) {
            if (isSelected(o)) {
                return o;
            }
        }
        return null;
    }

    private void setSelected(GObject object, boolean value) {
        object.putBoolean(GAdapterUtil.TAG_SELECTABLE, value);
    }

    private boolean isSelected(GObject object) {
        return object.getBoolean(GAdapterUtil.TAG_SELECTABLE);
    }

    private void setCropPageMode(GObject object, ReaderCropArgs.CropPageMode mode) {
        object.putObject(GAdapterUtil.TAG_UNIQUE_ID, mode);
    }

    public ReaderCropArgs.CropPageMode getCropPageMode(GObject object) {
        return (ReaderCropArgs.CropPageMode)object.getObject(GAdapterUtil.TAG_UNIQUE_ID);
    }

    private void setSubScreenMode(GObject object, SubScreenMode mode) {
        object.putObject(GAdapterUtil.TAG_UNIQUE_ID, mode);
    }

    private SubScreenMode getSubScreenMode(GObject object) {
        return (SubScreenMode)object.getObject(GAdapterUtil.TAG_UNIQUE_ID);
    }

    private void setNavigationMode(GObject object, ReaderCropArgs.NavigationMode mode) {
        object.putObject(GAdapterUtil.TAG_UNIQUE_ID, mode);
    }

    private ReaderCropArgs.NavigationMode getNavigationMode(GObject object) {
        return (ReaderCropArgs.NavigationMode)object.getObject(GAdapterUtil.TAG_UNIQUE_ID);
    }

    private SubScreenMode getSubScreenModeFromReader(int rows, int columns) {
        return SubScreenModeMatrix[rows - 1][columns - 1];
    }

    private int getRowNumber() {
        switch (currentChoosingSubScreenMode) {
            case SUB_SCREEN_1_1:
            case SUB_SCREEN_1_2:
                return 1;
            case SUB_SCREEN_2_1:
            case SUB_SCREEN_2_2:
            case SUB_SCREEN_2_3:
                return 2;
            case SUB_SCREEN_3_1:
            case SUB_SCREEN_3_2:
            case SUB_SCREEN_3_3:
                return 3;
            default:
                assert(false);
                return 1;
        }
    }

    private int getColumnNumber() {
        switch (currentChoosingSubScreenMode) {
            case SUB_SCREEN_1_1:
            case SUB_SCREEN_2_1:
            case SUB_SCREEN_3_1:
                return 1;
            case SUB_SCREEN_1_2:
            case SUB_SCREEN_2_2:
            case SUB_SCREEN_3_2:
                return 2;
            case SUB_SCREEN_2_3:
            case SUB_SCREEN_3_3:
                return 3;
            default:
                assert(false);
                return 1;
        }
    }

    @Override
    public void show() {
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        DisplayMetrics dm = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        lp.width = (dm.widthPixels * 4) / 5;
        this.getWindow().setAttributes(lp);
        super.show();
    }
}
