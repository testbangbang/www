package com.onyx.android.sdk.ui.dialog;

import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.data.GAdapter;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.data.util.GAdapterUtil;
import com.onyx.android.sdk.ui.AlignTextView;
import com.onyx.android.sdk.ui.ContentItemView;
import com.onyx.android.sdk.ui.ContentView;
import com.onyx.android.sdk.ui.dialog.data.AbstractReaderMenuCallback;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by solskjaer49 on 16/1/12 16:23.
 */
public class DialogFontStyleSettings extends OnyxAlertDialog {
    static final String TAG = DialogFontStyleSettings.class.getSimpleName();
    private AbstractReaderMenuCallback mMenuCallback = null;
    GAdapter lineSpacingAdapter, pageMarginAdapter, indentAdapter;
    HashMap<String, Integer> dataViewMapping;
    ArrayList<FontSizeButton> fontSizeButtonArrayList;
    boolean saveCurrentConfig = false;

    public DialogFontStyleSettings setMenuCallback(AbstractReaderMenuCallback mMenuCallback) {
        this.mMenuCallback = mMenuCallback;
        return this;
    }

    class FontSizeButton {
        int actualPixelSize;
        AlignTextView fontSizeExample;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setParams(new Params().setTittleString(getString(R.string.font_style_ettings))
                .setDialogWidth(configWidth())
                .setCustomLayoutResID(R.layout.alert_dialog_font_style_setting)
                .setEnablePageIndicator(false)
                .setPositiveAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveCurrentConfig = true;
                        dismiss();
                    }
                })
                .setCanceledOnTouchOutside(false)
                .setNegativeAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                })
                .setCustomViewAction(new CustomViewAction() {
                    @Override
                    public void onCreateCustomView(View customView, TextView pageIndicator) {
                        final ContentView lineSpacingContentView = (ContentView) customView.findViewById(R.id.line_spacing_contentView);
                        final ContentView pageMarginContentView = (ContentView) customView.findViewById(R.id.page_margin_contentView);
                        final ContentView indentContentView = (ContentView) customView.findViewById(R.id.indent_contentView);
                        Button fontFaceButton = (Button) customView.findViewById(R.id.button_font_face);
                        fontFaceButton.setText(mMenuCallback.getFontFace() != null ?
                                mMenuCallback.getFontFace() : getResources().getString(R.string.default_font));
                        fontFaceButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mMenuCallback.selectFontFace();
                                dismiss();
                            }
                        });
                        fontFaceButton.setWidth((configWidth() * 3 / 8));
                        buildDataViewMap();
                        lineSpacingContentView.setShowPageInfoArea(false);
                        pageMarginContentView.setShowPageInfoArea(false);
                        indentContentView.setShowPageInfoArea(false);
                        lineSpacingContentView.setupGridLayout(1, 5);
                        pageMarginContentView.setupGridLayout(1, 5);
                        indentContentView.setupGridLayout(1, 2);
                        lineSpacingContentView.setSubLayoutParameter(R.layout.onyx_reader_menu_imagebutton, dataViewMapping);
                        pageMarginContentView.setSubLayoutParameter(R.layout.onyx_reader_menu_imagebutton, dataViewMapping);
                        indentContentView.setSubLayoutParameter(R.layout.onyx_reader_menu_imagebutton, dataViewMapping);
                        lineSpacingContentView.setAdapter(buildLineSpacingAdapter(), 0);
                        pageMarginContentView.setAdapter(buildPageMarginAdapter(), 0);
                        indentContentView.setAdapter(buildIndentAdapter(), 0);
                        configFontSizePanel((LinearLayout) customView.findViewById(R.id.font_size_layout));
                        lineSpacingContentView.setCallback(new ContentView.ContentViewCallback() {
                            @Override
                            public void onItemClick(ContentItemView view) {
                                postProcessOnClick(lineSpacingContentView, view.getData());
                                if (mMenuCallback != null) {
                                    int data = view.getData().getInt(GAdapterUtil.TAG_UNIQUE_ID);
                                    switch (data) {
                                        case Integer.MIN_VALUE:
                                            mMenuCallback.decreaseLineSpacing();
                                            break;
                                        case Integer.MAX_VALUE:
                                            mMenuCallback.increaseLineSpacing();
                                            break;
                                        default:
                                            mMenuCallback.setLineSpacing((AbstractReaderMenuCallback.LineSpacingScale.values()[data]));
                                    }
                                }
                            }
                        });
                        pageMarginContentView.setCallback(new ContentView.ContentViewCallback() {
                            @Override
                            public void onItemClick(ContentItemView view) {
                                postProcessOnClick(pageMarginContentView, view.getData());
                                int data = view.getData().getInt(GAdapterUtil.TAG_UNIQUE_ID);
                                if (mMenuCallback != null) {
                                    switch (data) {
                                        case Integer.MIN_VALUE:
                                            mMenuCallback.decreasePageMargin();
                                            break;
                                        case Integer.MAX_VALUE:
                                            mMenuCallback.increasePageMargin();
                                            break;
                                        default:
                                            mMenuCallback.setPageMargin(view.getData().getInt(GAdapterUtil.TAG_UNIQUE_ID));
                                    }
                                }
                            }
                        });
                        indentContentView.setCallback(new ContentView.ContentViewCallback() {
                            @Override
                            public void onItemClick(ContentItemView view) {
                                postProcessOnClick(indentContentView, view.getData());
                                if (mMenuCallback != null) {
                                    mMenuCallback.setIndent(view.getData().getInt(GAdapterUtil.TAG_UNIQUE_ID));
                                }
                            }
                        });
                    }
                }));
        super.onCreate(savedInstanceState);
    }

    private HashMap<String, Integer> buildDataViewMap() {
        if (dataViewMapping == null) {
            dataViewMapping = new HashMap<String, Integer>();
            dataViewMapping.put(GAdapterUtil.TAG_IMAGE_RESOURCE, R.id.imageview_icon);
            dataViewMapping.put(GAdapterUtil.TAG_SELECTABLE, R.id.checkbox_current_select);
        }
        return dataViewMapping;
    }

    private void postProcessOnClick(ContentView contentView, GObject targetObject) {
        int dataIndex = contentView.getCurrentAdapter().getGObjectIndex(targetObject);
        if (targetObject.getInt(GAdapterUtil.TAG_UNIQUE_ID) != Integer.MIN_VALUE
                && targetObject.getInt(GAdapterUtil.TAG_UNIQUE_ID) != Integer.MAX_VALUE) {
            targetObject.putBoolean(GAdapterUtil.TAG_SELECTABLE, true);
            contentView.getCurrentAdapter().setObject(dataIndex, targetObject);
        }
        contentView.unCheckOtherViews(dataIndex, true);
        contentView.updateCurrentPage();
    }

    private void configFontSizePanel(LinearLayout panelParentLayout) {
        ArrayList<AlignTextView> buttonList = new ArrayList<AlignTextView>();
        buttonList.add((AlignTextView) panelParentLayout.findViewById(R.id.text_view_font_size_0));
        buttonList.add((AlignTextView) panelParentLayout.findViewById(R.id.text_view_font_size_1));
        buttonList.add((AlignTextView) panelParentLayout.findViewById(R.id.text_view_font_size_2));
        buttonList.add((AlignTextView) panelParentLayout.findViewById(R.id.text_view_font_size_3));
        buttonList.add((AlignTextView) panelParentLayout.findViewById(R.id.text_view_font_size_4));
        if (mMenuCallback.getFontSizeArray() == null || mMenuCallback.getFontSizeArray().size() == 0 ||
                mMenuCallback.getFontSizeArray().size() < buttonList.size())
            return;
        ImageView minusButton = (ImageView) panelParentLayout.findViewById(R.id.imageView_MinusButton);
        ImageView addButton = (ImageView) panelParentLayout.findViewById(R.id.imageView_AddButton);
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearOtherFontSizeIconCheck(-1);
                mMenuCallback.decreaseFontSize();
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearOtherFontSizeIconCheck(-1);
                mMenuCallback.increaseFontSize();
            }
        });
        ArrayList<Integer> fontSizeArray = mMenuCallback.getFontSizeArray();
        fontSizeButtonArrayList = new ArrayList<FontSizeButton>();
        for (int i = 0; i < buttonList.size(); i++) {
            final FontSizeButton button = new FontSizeButton();
            button.actualPixelSize = fontSizeArray.get(i);
            button.fontSizeExample = buttonList.get(i);
            if (mMenuCallback.getFontSize() == button.actualPixelSize) {
                button.fontSizeExample.setChecked(true);
            }
            button.fontSizeExample.setTextSize(TypedValue.COMPLEX_UNIT_PX, button.actualPixelSize);
            button.fontSizeExample.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clearOtherFontSizeIconCheck(button.actualPixelSize);
                    button.fontSizeExample.setChecked(true);
                    mMenuCallback.setFontSize(button.actualPixelSize);
                }
            });
            fontSizeButtonArrayList.add(button);
        }
    }

    private void clearOtherFontSizeIconCheck(int excludeTextSize) {
        for (FontSizeButton button : fontSizeButtonArrayList) {
            if (button.actualPixelSize != excludeTextSize) {
                button.fontSizeExample.setChecked(false);
            }
        }
    }

    private GAdapter buildLineSpacingAdapter() {
        if (lineSpacingAdapter == null) {
            lineSpacingAdapter = new GAdapter();
            lineSpacingAdapter.addObject(createLineSpacingItem(R.drawable.ic_dlg_font_style_small_line_spacing,
                    AbstractReaderMenuCallback.LineSpacingScale.Small.ordinal(), mMenuCallback.getLineSpacing()));
            lineSpacingAdapter.addObject(createLineSpacingItem(R.drawable.ic_dlg_font_style_normal_line_spacing,
                    AbstractReaderMenuCallback.LineSpacingScale.Normal.ordinal(), mMenuCallback.getLineSpacing()));
            lineSpacingAdapter.addObject(createLineSpacingItem(R.drawable.ic_dlg_font_style_large_line_spacing,
                    AbstractReaderMenuCallback.LineSpacingScale.Large.ordinal(), mMenuCallback.getLineSpacing()));
            lineSpacingAdapter.addObject(createLineSpacingItem(R.drawable.ic_dlg_font_style_decrease_line_spacing,
                    Integer.MIN_VALUE, mMenuCallback.getLineSpacing()));
            lineSpacingAdapter.addObject(createLineSpacingItem(R.drawable.ic_dlg_font_style_increase_line_spacing,
                    Integer.MAX_VALUE, mMenuCallback.getLineSpacing()));
        }
        return lineSpacingAdapter;
    }

    private GAdapter buildPageMarginAdapter() {
        if (pageMarginAdapter == null) {
            pageMarginAdapter = new GAdapter();
            pageMarginAdapter.addObject(createItem(R.drawable.ic_dlg_font_style_small_margin, 10, mMenuCallback.getPageMargin()));
            pageMarginAdapter.addObject(createItem(R.drawable.ic_dlg_font_style_normal_margin, 20, mMenuCallback.getPageMargin()));
            pageMarginAdapter.addObject(createItem(R.drawable.ic_dlg_font_style_large_margin, 30, mMenuCallback.getPageMargin()));
            pageMarginAdapter.addObject(createItem(R.drawable.ic_dlg_font_style_decrease_margin, Integer.MIN_VALUE, mMenuCallback.getPageMargin()));
            pageMarginAdapter.addObject(createItem(R.drawable.ic_dlg_font_style_increase_margin, Integer.MAX_VALUE, mMenuCallback.getPageMargin()));
        }
        return pageMarginAdapter;
    }

    private GAdapter buildIndentAdapter() {
        if (indentAdapter == null) {
            indentAdapter = new GAdapter();
            indentAdapter.addObject(createItem(R.drawable.ic_dlg_font_style_no_indent, 0, mMenuCallback.getIndent()));
            indentAdapter.addObject(createItem(R.drawable.ic_dlg_font_style_indent, 2, mMenuCallback.getIndent()));
        }
        return indentAdapter;
    }

    private GObject createItem(int iconResource, int itemValue, int currentValue) {
        GObject object = GAdapterUtil.createTableItem(0, 0, iconResource, 0, null);
        object.putInt(GAdapterUtil.TAG_UNIQUE_ID, itemValue);
        object.putBoolean(GAdapterUtil.TAG_SELECTABLE, false);
        if (itemValue == currentValue) {
            object.putBoolean(GAdapterUtil.TAG_SELECTABLE, true);
        }
        return object;
    }


    private GObject createLineSpacingItem(int iconResource, int itemValue,
                                          AbstractReaderMenuCallback.LineSpacingScale currentScale) {
        GObject object = GAdapterUtil.createTableItem(0, 0, iconResource, 0, null);
        object.putInt(GAdapterUtil.TAG_UNIQUE_ID, itemValue);
        object.putBoolean(GAdapterUtil.TAG_SELECTABLE, false);
        if (itemValue != Integer.MAX_VALUE && itemValue != Integer.MIN_VALUE) {
            if (AbstractReaderMenuCallback.LineSpacingScale.values()[itemValue] == currentScale) {
                object.putBoolean(GAdapterUtil.TAG_SELECTABLE, true);
            }
        }
        return object;
    }

    private int configWidth() {
        if (getResources().getInteger(R.integer.font_style_dialog_use_percentage_width) != 1) {
            return getDefaultWidth(false);
        }
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels * 4 / 5;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (!saveCurrentConfig) {
            mMenuCallback.restoreFontStyleOption();
        } else {
            mMenuCallback.saveCurrentFontStyleOption();
        }
        super.onDismiss(dialog);
    }

    public void show(FragmentManager fm) {
        super.show(fm, TAG);
    }
}
