package com.onyx.android.sdk.ui;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.*;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.data.util.GAdapterUtil;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: zhuzeng
 * Date: 3/16/14
 * Time: 12:21 PM
 * To change this template use File | Settings | File Templates.
 */
public final class OnyxTableItemView extends LinearLayout {
    private GObject mData;
    private int mLayoutResource;
    private Map<String, Integer> mValueMapping;
    private Map<Integer, Map<Class<?>, Object>> mCallbackMapping;
    private double mWeight = 0.0;

    private OnyxTableItemView(LayoutInflater inflater, GObject data, int layoutResource,
                              Map<String, Integer> valueMapping,
                              Map<Integer, Map<Class<?>, Object>> callbackMapping) {
        super(inflater.getContext());
        setBackgroundResource(getDefaultBackgroundResource());
        init(inflater, data, layoutResource, valueMapping, callbackMapping);
    }

    public static int getDefaultBackgroundResource() {
        return R.drawable.tableview_item_background;
    }

    public static OnyxTableItemView create(LayoutInflater inflater, GObject data) {
        return new OnyxTableItemView(inflater, data,
                data.getInt(GAdapterUtil.TAG_LAYOUT_RESOURCE),
                (Map<String, Integer>)data.getObject(GAdapterUtil.TAG_LAYOUT_MAPPING),
                (Map<Integer, Map<Class<?>, Object>>)data.getObject(GAdapterUtil.TAG_CALLBACK_MAPPING));
    }

    public static OnyxTableItemView create(LayoutInflater inflater, GObject data, int layoutResource, Map<String, Integer> valueMapping) {
        int layout_override = layoutResource;
        Map<String, Integer> mapping_override = valueMapping;
        if (data.getInt(GAdapterUtil.TAG_LAYOUT_RESOURCE, -1) > 0) {
            layout_override = data.getInt(GAdapterUtil.TAG_LAYOUT_RESOURCE);
        }
        if (data.getObject(GAdapterUtil.TAG_LAYOUT_MAPPING) != null) {
            mapping_override = (Map<String, Integer>)data.getObject(GAdapterUtil.TAG_LAYOUT_MAPPING);
        }
        return new OnyxTableItemView(inflater, data, layout_override, mapping_override, null);
    }

    private void init(LayoutInflater inflater, GObject data, int layoutResource,
                      Map<String, Integer> valueMapping, Map<Integer, Map<Class<?>, Object>> callbackMapping) {
        mData = data;
        mLayoutResource = layoutResource;
        mValueMapping = valueMapping;
        mCallbackMapping = callbackMapping;

        mData.setCallback(new GObject.GObjectCallback() {
            @Override
            public void changed(String key, GObject object) {
                OnyxTableItemView.this.update();
            }
        });

        removeAllViews();
        if (layoutResource > 0) {
            View view = inflater.inflate(layoutResource, this, false);
            ((LayoutParams) view.getLayoutParams()).gravity = Gravity.CENTER;
            addView(view);
            mWeight = ((LayoutParams) view.getLayoutParams()).weight;
        }

        if (callbackMapping != null) {
            registerCallbacks(callbackMapping);
        }

        update();
    }

    public int getLayoutResource() {
        return mLayoutResource;
    }

    public GObject getData() {
        return mData;
    }

    public void setData(final GObject object) {
        mData = object;
        int res = object.getInt(GAdapterUtil.TAG_LAYOUT_RESOURCE, -1);
        if (res > 0) {
            mLayoutResource = res;
        }
        mValueMapping = (Map<String, Integer>)object.getObject(GAdapterUtil.TAG_LAYOUT_MAPPING);
        update();
    }

    public double getWeight() {
        return mWeight;
    }

    public int getLayoutWidth() {
        if (getChildCount() <= 0) {
            return ViewGroup.LayoutParams.MATCH_PARENT;
        }
        return this.getChildAt(0).getLayoutParams().width;
    }

    public int getLayoutHeight() {
        if (getChildCount() <= 0) {
            return ViewGroup.LayoutParams.MATCH_PARENT;
        }
        return this.getChildAt(0).getLayoutParams().height;
    }

    private Map<String, Integer> getValueMapping() {
        if (mValueMapping != null) {
            return mValueMapping;
        }
        return GAdapterUtil.getDefaultMenuMaping();
    }

    public void update() {
        if (getChildCount() <= 0) {
            return;
        }

        Map<String, Integer> mapping = getValueMapping();
        if (mapping == null) {
            return;
        }

        View parent = getChildAt(0);
        for (Map.Entry<String, Integer> entry : mapping.entrySet()) {
            if (!getData().hasKey(entry.getKey())) {
                continue;
            }

            int id = entry.getValue();
            View view = parent.findViewById(id);
            if (view == null) {
                continue;
            }
            Object value = getData().getObject(entry.getKey());
            if (value == null || (value instanceof Integer && ((Integer) value).intValue() < 0)) {
                view.setVisibility(View.INVISIBLE);
            }
            if (view instanceof ImageView) {
                this.updateImageView((ImageView)view, value);
            } else if (view instanceof TextView) {
                this.updateTextView((TextView)view, value);
            } else if (view instanceof Button) {
                this.updateButtonText((Button)view, value);
            } else if (view instanceof SeekBar) {
                this.updateSeekBarProgress((SeekBar)view, value);
            }
        }
        updateSelection(parent);
    }

    private void registerCallbacks(Map<Integer, Map<Class<?>, Object>> callbackMapping) {
        if (getChildCount() <= 0) {
            return;
        }

        final View parent = getChildAt(0);
        for (Map.Entry<Integer, Map<Class<?>, Object>> entry: callbackMapping.entrySet()) {
            View view = parent.findViewById(entry.getKey());
            if (view == null) {
                continue;
            }
            if (view instanceof SeekBar) {
                registerSeekBarkCallbacks((SeekBar)view, entry.getValue());
            }
        }
    }

    private void updateTextView(TextView tv, Object value) {
        if (value instanceof String) {
            tv.setText((String) value);
        } else if (value instanceof Integer) {
            tv.setText((Integer) value);
        }
    }

    private void updateImageView(ImageView iv, Object value) {
        if (value == null) {
            return;
        }
        if (value instanceof Integer) {
            int n = ((Integer)value).intValue();
            if (n > 0) {
                iv.setImageResource(n);
            }
        } else if (value instanceof Drawable) {
            iv.setImageDrawable((Drawable) value);
        }else if (value instanceof Bitmap){
            iv.setImageBitmap((Bitmap) value);
        }
    }

    private void updateButtonText(Button button, Object value) {
        if (value instanceof String) {
            button.setText((String) value);
        } else if (value instanceof Integer) {
            button.setText((Integer) value);
        }
    }

    private void updateSeekBarProgress(SeekBar seekBar, Object value) {
        if (value instanceof Integer) {
            int n = ((Integer)value).intValue();
            if (n > 0 & n <= seekBar.getMax()) {
                seekBar.setProgress(n);
            }
        }
    }

    private void registerSeekBarkCallbacks(SeekBar seekBar, Map<Class<?>, Object> callbacks) {
        for (Map.Entry<Class<?>, Object> callback : callbacks.entrySet()) {
            if (callback.getKey().equals(SeekBar.OnSeekBarChangeListener.class)) {
                seekBar.setOnSeekBarChangeListener((SeekBar.OnSeekBarChangeListener)callback.getValue());
            }
        }
    }

    private void updateSelection(View view) {
        View checked_view = null;
        View unchecked_view = null;
        int checked_id = mData.getInt(GAdapterUtil.TAG_SELECTION_CHECKED_VIEW_ID, -1);
        if (checked_id > 0) {
            checked_view = view.findViewById(checked_id);
        }
        int unchecked_id = mData.getInt(GAdapterUtil.TAG_SELECTION_UNCHECKED_VIEW_ID, -1);
        if (unchecked_id > 0) {
            unchecked_view = view.findViewById(unchecked_id);
        }

        boolean in_selection = mData.getBoolean(GAdapterUtil.TAG_IN_SELECTION, false);
        boolean selected = mData.getBoolean(GAdapterUtil.TAG_SELECTED, false);
        if (!in_selection) {
            if (checked_view != null) {
                checked_view.setVisibility(View.INVISIBLE);
            }
            if (unchecked_view != null) {
                unchecked_view.setVisibility(View.INVISIBLE);
            }
        } else {
            // because checked_view and unchecked_view may be same,
            // so always setVisibility(View.VISIBLE) after setVisibility(View.INVISIBLE)
            if (selected) {
                if (unchecked_view != null) {
                    unchecked_view.setVisibility(View.INVISIBLE);
                }
                if (checked_view != null) {
                    checked_view.setVisibility(View.VISIBLE);
                    if (checked_view instanceof CheckBox) {
                        ((CheckBox)checked_view).setChecked(true);
                    }
                }
            } else {
                if (checked_view != null) {
                    checked_view.setVisibility(View.INVISIBLE);
                }
                if (unchecked_view != null) {
                    unchecked_view.setVisibility(View.VISIBLE);
                    if (unchecked_view instanceof CheckBox) {
                        ((CheckBox)unchecked_view).setChecked(false);
                    }
                }
            }
        }
    }

}
