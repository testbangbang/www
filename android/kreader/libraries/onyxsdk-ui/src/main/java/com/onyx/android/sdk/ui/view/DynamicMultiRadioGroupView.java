package com.onyx.android.sdk.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.onyx.android.sdk.ui.R;

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

    private void clearButtonCheckState(CompoundButton checkedButton){
        for (CompoundButton button : compoundButtonList) {
            if (!button.equals(checkedButton)){
                button.setChecked(false);
            }
        }
    }

    private void initLayout(){
        initMultiLineGroup();
        addNewLineButton();
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

    private void setButtonListener(CompoundButton button, final int position){
        button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && multiAdapter.isRadioButton()){
                    clearButtonCheckState(buttonView);
                }
                if (onCheckedChangeListener != null){
                    onCheckedChangeListener.onCheckedChanged(buttonView, isChecked, position);
                }
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
        this.multiAdapter = multiAdapter;
        initLayout();
    }

    public static abstract class MultiAdapter {

        private int paddingLeft, paddingTop, paddingRight, paddingBottom;
        private int marginLeft, marginTop, marginRight, marginBottom;
        private int textSize = 20;

        public abstract int getRows();
        public abstract int getColumns();
        public abstract int getBackgroundResId();
        public abstract boolean isRadioButton();
        public abstract void bindView(CompoundButton button, int position);
        public abstract List<String> getButtonTexts();

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

        public void setTextSize(int textSize) {
            this.textSize = textSize;
        }

        public int getItemCount(){
            if (getButtonTexts() == null){
                return 0;
            }
            return getButtonTexts().size();
        }

        public final CompoundButton createCompoundButton(ViewGroup parent, int position) {
            CompoundButton button;
            if (isRadioButton()){
                button = new RadioButton(parent.getContext());
            }else {
                button = new CheckBox(parent.getContext());
            }
            initButton(button,position);
            if (position < getItemCount()){
                bindView(button,position);
            }else {
                button.setVisibility(INVISIBLE);
            }
            return button;
        }

        private CompoundButton initButton(CompoundButton button, int position) {
            String text = position < getItemCount() ? getButtonTexts().get(position) : "";
            button.setText(text);
            button.setButtonDrawable(R.color.transparent);
            button.setBackgroundResource(getBackgroundResId());
            button.setGravity(Gravity.CENTER);
            button.setTextSize(textSize);
            button.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

            LayoutParams layoutParams = new LayoutParams(0, LayoutParams.MATCH_PARENT);
            layoutParams.weight = 1;
            layoutParams.setMargins(marginLeft,marginTop,marginRight,marginBottom);
            button.setLayoutParams(layoutParams);
            return button;
        }
    }
}
