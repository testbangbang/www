package com.onyx.edu.student.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.onyx.android.sdk.ui.compat.AppCompatRelativeLayout;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.TreeObserverUtils;
import com.onyx.edu.student.R;
import com.onyx.edu.student.utils.QRCodeUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by suicheng on 2017/10/27.
 */
public class CustomRadioGroup extends RelativeLayout {

    private static final float MAX_RADIUS = 300;
    private static final int INDICATOR_IMAGE_MIN_HEIGHT = 66;

    private RadioGroup contentRadioGroup;
    private ImageView checkedIndicatorImage;
    private Bitmap checkedIndicatorBitmap;

    private int contentLayoutWidth;

    private OnCheckedChangeListener listener;

    public interface OnCheckedChangeListener {
        void onCheckedChanged(RadioGroup group, int checkedId, int checkedIndex);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.listener = listener;
    }

    public CustomRadioGroup(Context context) {
        this(context, null);
    }

    public CustomRadioGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomRadioGroup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initLayout(context);
        initAttrs(context, attrs);
    }

    private int getCheckedViewIndex() {
        int checkedId = contentRadioGroup.getCheckedRadioButtonId();
        View radioButton = contentRadioGroup.findViewById(checkedId);
        return contentRadioGroup.indexOfChild(radioButton);
    }

    public void nextTab() {
        int index = getCheckedViewIndex();
        if (index + 1 > contentRadioGroup.getChildCount()) {
            return;
        }
        checkIndex(index + 1);
    }

    public void prevTab() {
        int index = getCheckedViewIndex();
        if (index <= 0) {
            return;
        }
        checkIndex(index - 1);
    }

    private void addOnGlobalLayoutListener() {
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                TreeObserverUtils.removeGlobalOnLayoutListener(getViewTreeObserver(), this);
                contentLayoutWidth = getMeasuredWidth();
                int height = checkedIndicatorImage.getMeasuredHeight();
                height = height <= 0 ? INDICATOR_IMAGE_MIN_HEIGHT : height;
                checkedIndicatorImage.setImageBitmap(getCheckedIndicatorBitmap(getCheckedIndicatorWidth(), height));
            }
        });
    }

    private int getCheckedIndicatorWidth() {
        int count = contentRadioGroup.getChildCount();
        return contentLayoutWidth / count + 12 / count * 3;
    }

    private void initLayout(Context context) {
        LayoutInflater.from(context).inflate(R.layout.custom_radio_group, this, true);
        addOnGlobalLayoutListener();
        checkedIndicatorImage = (ImageView) findViewById(R.id.select_tab);
        contentRadioGroup = (RadioGroup) findViewById(R.id.select_content);
        contentRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int checkedIndex = 0;
                for (int i = 0; i < group.getChildCount(); i++) {
                    if (group.getChildAt(i).getId() == checkedId) {
                        checkedIndex = i;
                        break;
                    }
                }
                processItemChecked(group, checkedId, checkedIndex);
                moveSelectedImage(checkedIndex, group.getChildCount());
            }
        });
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomRadioGroup_style);
        int entryResId = a.getResourceId(R.styleable.CustomRadioGroup_style_entryTexts, -1);
        if (entryResId > 0) {
            updateItemsText(context.getResources().getStringArray(entryResId));
        }
        a.recycle();
    }

    private void checkIndex(int index) {
        if (index >= contentRadioGroup.getChildCount()) {
            return;
        }
        RadioButton button = (RadioButton) contentRadioGroup.getChildAt(index);
        button.setChecked(true);
    }

    private void processItemChecked(RadioGroup group, int checkedId, int checkedIndex) {
        if (listener != null) {
            listener.onCheckedChanged(group, checkedId, checkedIndex);
        }
    }

    private void moveSelectedImage(int checkedIndex, int totalCount) {
        int offset = contentLayoutWidth / totalCount;
        if (checkedIndex == 0) {
            offset = 0;
        } else if (checkedIndex == totalCount - 1) {
            offset = contentLayoutWidth - checkedIndicatorImage.getMeasuredWidth();
        } else {
            offset = offset * checkedIndex - (checkedIndicatorImage.getMeasuredWidth() - contentLayoutWidth / 3);
        }
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) checkedIndicatorImage.getLayoutParams();
        layoutParams.setMargins(offset, layoutParams.topMargin, layoutParams.rightMargin, layoutParams.bottomMargin);
        checkedIndicatorImage.setLayoutParams(layoutParams);
    }

    private Bitmap createRoundRectBitmap(int width, int height) {
        float[] outerR = new float[]{MAX_RADIUS, MAX_RADIUS, MAX_RADIUS, MAX_RADIUS,
                MAX_RADIUS, MAX_RADIUS, MAX_RADIUS, MAX_RADIUS};

        RoundRectShape shape = new RoundRectShape(outerR, null, null);
        ShapeDrawable drawable = new ShapeDrawable(shape);
        drawable.getPaint().setColor(Color.CYAN);
        drawable.getPaint().setAntiAlias(true);
        drawable.getPaint().setStyle(Paint.Style.FILL);
        drawable.setBounds(0, 0, width, height);
        Bitmap src = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(src);
        drawable.draw(canvas);
        return src;
    }

    private Bitmap getCFABitmap(Bitmap src) {
        if (src == null) {
            return null;
        }
        Bitmap scale = Bitmap.createScaledBitmap(src, src.getWidth() / 2, src.getHeight() / 2, false);
        if (scale == null) {
            return null;
        }
        Bitmap dst = QRCodeUtil.getCFABitMap(scale);
        src.recycle();
        scale.recycle();
        return dst;
    }

    private Bitmap getCheckedIndicatorBitmap(int width, int height) {
        if (checkedIndicatorBitmap != null && !checkedIndicatorBitmap.isRecycled()) {
            return checkedIndicatorBitmap;
        }
        return checkedIndicatorBitmap = getCFABitmap(createRoundRectBitmap(width, height));
    }

    public void updateItemsText(List<String> list) {
        if (CollectionUtils.isNullOrEmpty(list)) {
            return;
        }
        adjustGroupChildrenCount(CollectionUtils.getSize(list));
        for (int i = 0; i < contentRadioGroup.getChildCount(); i++) {
            RadioButton button = (RadioButton) contentRadioGroup.getChildAt(i);
            button.setText(list.get(i));
        }
        checkIndex(0);
    }

    private void adjustGroupChildrenCount(int targetSize) {
        int childCount = contentRadioGroup.getChildCount();
        if (childCount == targetSize) {
            return;
        }
        if (targetSize > childCount) {
            for (int i = 0; i < targetSize - childCount; i++) {
                RadioButton button = inflateChildRadioButton(i);
                if (i == 0) {
                    button.setChecked(true);
                }
            }
        } else if (targetSize < childCount) {
            for (int i = 0; i < childCount - targetSize; i++) {
                contentRadioGroup.removeViewAt(0);
            }
        }
        resizeCheckedIndicatorWidth();
    }

    private void resizeCheckedIndicatorWidth() {
        if (checkedIndicatorImage.getMeasuredHeight() <= 0) {
            return;
        }
        checkedIndicatorBitmap = getCFABitmap(createRoundRectBitmap(getCheckedIndicatorWidth(),
                checkedIndicatorImage.getMeasuredHeight()));
    }

    private RadioButton inflateChildRadioButton(int specifyId) {
        LayoutInflater.from(getContext()).inflate(R.layout.custom_radio, contentRadioGroup, true);
        RadioButton radioButton = (RadioButton) contentRadioGroup.findViewById(R.id.radio_button);
        radioButton.setId(R.id.radio_button3 + specifyId);
        RadioGroup.LayoutParams layoutParams = (RadioGroup.LayoutParams) radioButton.getLayoutParams();
        layoutParams.weight = 1;
        layoutParams.width = 0;
        radioButton.setLayoutParams(layoutParams);
        return radioButton;
    }

    public void updateItemsText(String... itemTexts) {
        if (itemTexts == null || itemTexts.length <= 0) {
            return;
        }
        updateItemsText(Arrays.asList(itemTexts));
    }
}
