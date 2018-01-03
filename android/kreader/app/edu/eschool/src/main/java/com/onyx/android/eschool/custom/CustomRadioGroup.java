package com.onyx.android.eschool.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.support.v4.view.ViewCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.onyx.android.eschool.R;
import com.onyx.android.eschool.utils.QRCodeUtil;
import com.onyx.android.sdk.ui.compat.AppCompatUtils;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.TreeObserverUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by suicheng on 2017/10/27.
 */
public class CustomRadioGroup extends RelativeLayout {

    private RadioGroup contentRadioGroup;
    private ImageView checkedIndicatorImage;
    private Bitmap checkedIndicatorBitmap;

    private int contentLayoutWidth;

    private OnCheckedChangeListener listener;

    private int backgroundMaxRadius = 300;
    private int indicatorImageMinHeight = 44;
    private boolean useColorCFA = true;

    private boolean showIndicator = true;
    private boolean divideIndicatorAverage = true;

    private int indicatorColor = Color.rgb(0, 180, 255);
    //Color.rgb(17, 228, 17);

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
                height = height <= 0 ? indicatorImageMinHeight : height;
                checkedIndicatorImage.setImageBitmap(getCheckedIndicatorBitmap(getCheckedIndicatorWidth(), height));
                moveSelectedImage(getCheckedViewIndex(), contentRadioGroup.getChildCount());
            }
        });
    }

    private int getCheckedIndicatorWidth() {
        if (isDivideIndicatorAverage()) {
            int count = contentRadioGroup.getChildCount();
            return contentLayoutWidth / count + 12 / count * 3;
        }
        RadioButton view = (RadioButton) contentRadioGroup.getChildAt(0);
        int minimumWidth = ViewCompat.getMinimumWidth(view);
        int measuredWidth = getViewMeasuredWidth(view);
        if (minimumWidth > measuredWidth) {
            return minimumWidth;
        }
        return measuredWidth;
    }

    private int getViewMeasuredWidth(RadioButton view) {
        TextPaint paint = view.getPaint();
        float measuredWidth = paint.measureText(String.valueOf(view.getText()));
        return (int) Math.ceil(measuredWidth + view.getPaddingLeft() + view.getPaddingRight());
    }

    private void initLayout(Context context) {
        LayoutInflater.from(context).inflate(R.layout.custom_radio_group, this, true);
        addOnGlobalLayoutListener();
        contentLayoutWidth = getMeasuredWidth();
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
            updateItemsText(0, context.getResources().getStringArray(entryResId));
        }
        a.recycle();
    }

    public void checkIndex(int index) {
        if (index >= contentRadioGroup.getChildCount()) {
            return;
        }
        contentRadioGroup.check(contentRadioGroup.getChildAt(index).getId());
    }

    private void processItemChecked(RadioGroup group, int checkedId, int checkedIndex) {
        if (listener != null) {
            listener.onCheckedChanged(group, checkedId, checkedIndex);
        }
    }

    private void moveSelectedImage(int checkedIndex, int totalCount) {
        if (!isShowIndicator()) {
            return;
        }
        if (checkedIndex < 0 || checkedIndex >= totalCount) {
            return;
        }
        int offset = getIndicatorOffset(checkedIndex, totalCount);
        LayoutParams layoutParams = (LayoutParams) checkedIndicatorImage.getLayoutParams();
        layoutParams.setMargins(offset, layoutParams.topMargin, layoutParams.rightMargin, layoutParams.bottomMargin);
        checkedIndicatorImage.setLayoutParams(layoutParams);
    }

    private int getIndicatorOffset(int checkedIndex, int totalCount) {
        if(isDivideIndicatorAverage()) {
            return getIndicatorAverageOffset(checkedIndex, totalCount);
        }
        return getIndicatorRadioButtonOffset(checkedIndex);
    }

    private int getIndicatorRadioButtonOffset(int checkedIndex) {
        View view = contentRadioGroup.getChildAt(checkedIndex);
        return view.getLeft();
    }

    private int getIndicatorAverageOffset(int checkedIndex, int totalCount) {
        int offset = contentLayoutWidth / totalCount;
        int indicatorWidth = getCheckedIndicatorWidth();
        if (checkedIndex == 0) {
            offset = 0;
        } else if (checkedIndex == totalCount - 1) {
            offset = contentLayoutWidth - indicatorWidth;
        } else {
            int diff = (indicatorWidth - offset) / 2;
            offset = offset * checkedIndex - diff;
        }
        return offset;
    }

    private Bitmap createRoundRectBitmap(int width, int height) {
        float[] outerR = new float[]{backgroundMaxRadius, backgroundMaxRadius, backgroundMaxRadius, backgroundMaxRadius,
                backgroundMaxRadius, backgroundMaxRadius, backgroundMaxRadius, backgroundMaxRadius};

        RoundRectShape shape = new RoundRectShape(outerR, null, null);
        ShapeDrawable drawable = new ShapeDrawable(shape);
        drawable.getPaint().setColor(indicatorColor);
        drawable.getPaint().setAntiAlias(true);
        drawable.getPaint().setStyle(Paint.Style.FILL);
        drawable.setBounds(0, 0, width, height);
        Bitmap src = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(src);
        drawable.draw(canvas);
        return src;
    }

    private Bitmap getCFABitmap(Bitmap src) {
        if (!useColorCFA) {
            return src;
        }
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

    private void adjustGroupChildrenCount(int targetSize) {
        int childCount = contentRadioGroup.getChildCount();
        if (childCount == targetSize) {
            return;
        }
        if (targetSize > childCount) {
            for (int i = 0; i < targetSize - childCount; i++) {
                RadioButton button = inflateChildRadioButton(i + 1);
                if (i == 0) {
                    button.setChecked(true);
                }
            }
        } else if (targetSize < childCount) {
            for (int i = 0; i < childCount - targetSize; i++) {
                contentRadioGroup.removeViewAt(0);
            }
        }
    }

    private void resizeCheckedIndicatorWidth() {
        if (checkedIndicatorImage.getMeasuredHeight() <= 0) {
            return;
        }
        checkedIndicatorBitmap = getCFABitmap(createRoundRectBitmap(getCheckedIndicatorWidth(),
                checkedIndicatorImage.getMeasuredHeight()));
        checkedIndicatorImage.setImageBitmap(checkedIndicatorBitmap);
    }

    private RadioButton inflateChildRadioButton(int specifyId) {
        LayoutInflater.from(getContext()).inflate(R.layout.custom_radio, contentRadioGroup, true);
        RadioButton radioButton = (RadioButton) contentRadioGroup.findViewById(R.id.radio_button);
        radioButton.setId(R.id.radio_button3 + specifyId);
        return radioButton;
    }

    public void updateItemsText(int checkIndex, String... itemTexts) {
        if (itemTexts == null || itemTexts.length <= 0) {
            return;
        }
        updateItemsText(checkIndex, Arrays.asList(itemTexts));
    }

    public void updateItemsText(int checkIndex, List<String> list) {
        if (CollectionUtils.isNullOrEmpty(list)) {
            return;
        }
        adjustGroupChildrenCount(CollectionUtils.getSize(list));
        for (int i = 0; i < contentRadioGroup.getChildCount(); i++) {
            RadioButton button = (RadioButton) contentRadioGroup.getChildAt(i);
            button.setText(list.get(i));
        }
        resizeCheckedIndicatorWidth();
        checkIndex(checkIndex);
    }

    public int getBackgroundMaxRadius() {
        return backgroundMaxRadius;
    }

    public void setBackgroundMaxRadius(int backgroundMaxRadius) {
        this.backgroundMaxRadius = backgroundMaxRadius;
    }

    public int getIndicatorImageMinHeight() {
        return indicatorImageMinHeight;
    }

    public void setIndicatorImageMinHeight(int indicatorImageMinHeight) {
        this.indicatorImageMinHeight = indicatorImageMinHeight;
    }

    public void setUseColorCFA(boolean useColorCFA) {
        this.useColorCFA = useColorCFA;
    }

    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
    }

    public boolean isShowIndicator() {
        return showIndicator;
    }

    public void setShowIndicator(boolean showIndicator) {
        this.showIndicator = showIndicator;
        checkedIndicatorImage.setVisibility(showIndicator ? VISIBLE : INVISIBLE);
    }

    public boolean isDivideIndicatorAverage() {
        return divideIndicatorAverage;
    }

    public void setDivideIndicatorAverage(boolean dividerIndicatorAverage) {
        this.divideIndicatorAverage = dividerIndicatorAverage;
    }
}
