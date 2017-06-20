package com.onyx.android.sdk.ui.view;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 16/6/30.
 */
public class DynamicMultiRadioGroupView extends LinearLayout {

    private Context mContext;
    private List<CompoundButton> compoundButtonList;
    private MultiAdapter multiAdapter;
    private OnCheckedChangeListener onCheckedChangeListener;

    public interface OnCheckedChangeListener{
        void onCheckedChanged(CompoundButton buttonView, boolean isChecked, int position);
    }

    public DynamicMultiRadioGroupView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public DynamicMultiRadioGroupView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    public DynamicMultiRadioGroupView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    private void initView(){
        compoundButtonList = new ArrayList<>();
    }

    public void clearAllButtonCheckState(){
        for (CompoundButton button : compoundButtonList) {
            button.setChecked(false);
        }
    }

    private void initLayout(){
        compoundButtonList.clear();
        initMultiLineGroup();
        addNewLineButton();
        clearAllButtonCheckState();
    }

    private void initMultiLineGroup(){
        setOrientation(VERTICAL);
    }

    private void addNewLineButton(){
        int rows = multiAdapter.getRows();
        int columns = multiAdapter.getColumns();
        int index = 0;
        for (int r = 0; r < rows; r++) {
            LinearLayout linearLayout = generateNewLinearLayout();
            for (int c = 0; c < columns ; c++) {
                CompoundButton button = multiAdapter.createCompoundButton(this,index);
                linearLayout.addView(button);
                if (index < multiAdapter.getItemCount()){
                    compoundButtonList.add(button);
                    setButtonListener(button,index);
                }
                index++;
            }
            this.addView(linearLayout);
        }
    }

    private void setButtonListener(final CompoundButton button, final int position){
        button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && !multiAdapter.isMultiCheck()){
                    for (CompoundButton button : compoundButtonList) {
                        if (!button.equals(buttonView)){
                            button.setChecked(false);
                        }
                    }
                }
                if (onCheckedChangeListener != null){
                    onCheckedChangeListener.onCheckedChanged(buttonView, isChecked, position);
                }
                getMultiAdapter().bindView(button, position);
            }
        });
    }

    private LinearLayout generateNewLinearLayout(){
        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
        layoutParams.weight = 1;
        linearLayout.setLayoutParams(layoutParams);
        return linearLayout;
    }

    public void setMultiAdapter(MultiAdapter multiAdapter){
        removeAllViews();
        this.multiAdapter = multiAdapter;
        initLayout();
    }

    public MultiAdapter getMultiAdapter() {
        return multiAdapter;
    }

    public List<CompoundButton> getCompoundButtonList() {
        return compoundButtonList;
    }

    public enum CompoundFillStyle {WrapContent, Average, WeightWrapContent}

    public static abstract class MultiAdapter {

        private int paddingLeft = -1, paddingTop = -1, paddingRight = -1, paddingBottom = -1;
        private int marginLeft, marginTop, marginRight, marginBottom;
        private float textSize = -1;
        private int buttonDrawableResId = -1;
        private DynamicMultiRadioGroupView parent;
        private boolean multiCheck = false;
        private int backgroundResId = -1;
        private List<String> buttonTexts;
        private CompoundFillStyle fillStyle = CompoundFillStyle.Average;

        public abstract int getRows();
        public abstract int getColumns();
        public abstract int getItemCount();
        public abstract void bindView(CompoundButton button, int position);

        public void setPadding(int left, int top, int right, int bottom){
            paddingLeft = left;
            paddingTop = top;
            paddingRight = right;
            paddingBottom = bottom;
        }

        public void setMargin(int left, int top, int right, int bottom){
            marginLeft = left;
            marginTop = top;
            marginRight = right;
            marginBottom = bottom;
        }

        public void setTextSize(float textSize) {
            this.textSize = textSize;
        }

        public void setButtonTexts(List<String> buttonTexts) {
            this.buttonTexts = buttonTexts;
        }

        public void setButtonDrawableResId(int buttonDrawableResId) {
            this.buttonDrawableResId = buttonDrawableResId;
        }

        public void setMultiCheck(boolean multiCheck) {
            this.multiCheck = multiCheck;
        }

        public boolean isMultiCheck() {
            return multiCheck;
        }

        public void setBackgroundResId(int backgroundResId) {
            this.backgroundResId = backgroundResId;
        }

        public int getBackgroundResId() {
            return backgroundResId;
        }

        public void setFillStyle(CompoundFillStyle fillStyle) {
            this.fillStyle = fillStyle;
        }

        public final CompoundButton createCompoundButton(DynamicMultiRadioGroupView parent, int position) {
            this.parent = parent;
            CompoundButton button;
            if (isMultiCheck()){
                button = new OnyxCheckBox(parent.getContext());
            }else {
                button = new OnyxRadioButton(parent.getContext());
            }
            setDefaultStyle(button,position);
            if (position < getItemCount()){
                bindView(button,position);
            }else {
                button.setVisibility(INVISIBLE);
            }
            return button;
        }

        private CompoundButton setDefaultStyle(CompoundButton button, int position) {
            if (buttonTexts != null && buttonTexts.size() > position){
                String text = buttonTexts.get(position);
                button.setText(text);
            }else {
                button.setText("");
            }

            if (backgroundResId > 0){
                button.setBackgroundResource(getBackgroundResId());
            }
            if (backgroundResId > 0){
                button.setButtonDrawable(backgroundResId);
            }

            button.setGravity(Gravity.CENTER);
            if (textSize > 0){
                button.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }
            //4.0 the default padding has value So to judge
            button.setPadding(paddingLeft >= 0 ? paddingLeft : button.getPaddingLeft(),
                    paddingTop >= 0 ? paddingTop : button.getPaddingTop(),
                    paddingRight >= 0 ? paddingRight : button.getPaddingRight(),
                    paddingBottom >= 0 ? paddingBottom : button.getPaddingBottom());

            LayoutParams layoutParams;
            if (fillStyle == CompoundFillStyle.Average){
                layoutParams = new LayoutParams(0, LayoutParams.MATCH_PARENT);
                layoutParams.weight = 1;
            }else if (fillStyle == CompoundFillStyle.WrapContent){
                layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            }else{
                layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
                layoutParams.weight = 1;
            }

            layoutParams.setMargins(marginLeft,marginTop,marginRight,marginBottom);
            button.setLayoutParams(layoutParams);
            return button;
        }

        private DynamicMultiRadioGroupView getParent(){
            return parent;
        }

        public void setItemRangeChecked(boolean checked, int positionStart, int itemCount){
            for (int i = positionStart; i < itemCount + positionStart; i++) {
                setItemChecked(checked, i);
            }
        }

        public void setItemChecked(boolean checked, int position){
            DynamicMultiRadioGroupView parentView = getParent();
            if (parentView == null) {
                return;
            }
            List<CompoundButton> compoundButtons = parentView.getCompoundButtonList();
            if (compoundButtons == null || position >= compoundButtons.size() || position < 0){
                return;
            }
            compoundButtons.get(position).setChecked(checked);
        }

        public void resetItemChecked(int positionChecked, int itemCount) {
            setItemRangeChecked(true, 0, positionChecked + 1);
            setItemRangeChecked(false, positionChecked + 1, itemCount-positionChecked-1);
        }
    }
}