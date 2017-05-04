package com.onyx.android.sdk.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created by suicheng on 2017/5/4.
 */

public class DialogInformation extends OnyxBaseDialog implements DialogInterface {
    static final String TAG = DialogInformation.class.getSimpleName();
    private RelativeLayout alertTittleBarLayout;
    private LinearLayout functionPanelLayout;
    private TextView tittleTextView, alertMessageView, pageSizeIndicator;
    private Button positiveButton;
    private Button negativeButton;
    private Button neutralButton;
    private View customContentView, topDividerLine, functionButtonDividerLine, bottomDivider, btnNeutralDivider;
    private Params params = new Params();

    public interface CustomViewAction {
        void onCreateCustomView(View customView, TextView pageIndicator);
    }

    public Params getParams() {
        return params;
    }

    public void setParams(Params params) {
        this.params = params;
    }

    public DialogInformation(Context context) {
        super(context, R.style.CustomDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getWindow() == null) {
            return;
        }

        if (params.dialogHeight != ViewGroup.LayoutParams.WRAP_CONTENT
                || params.dialogWidth != ViewGroup.LayoutParams.WRAP_CONTENT) {
            getWindow().setLayout(params.dialogWidth, params.dialogHeight);
        } else {
            getWindow().setLayout(getDefaultWidth(params.isUsePercentageWidth()), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        setCanceledOnTouchOutside(params.canceledOnTouchOutside);
    }

    protected int getDefaultWidth(boolean usePercentageWidth) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        return usePercentageWidth ? (dm.widthPixels * 7 / 10) :
                dm.widthPixels - getContext().getResources().getDimensionPixelSize(R.dimen.onyx_alert_dialog_width_margin);
    }

    public void initView() {
        int layoutResId = params.customLayoutResID == -1 ?
                params.defaultLayoutResID : params.customLayoutResID;
        View view = LayoutInflater.from(getContext()).inflate(layoutResId, null);
        setContentView(view);
        alertTittleBarLayout = (RelativeLayout) view.findViewById(R.id.dialog_tittleBar);
        tittleTextView = (TextView) alertTittleBarLayout.findViewById(R.id.textView_title);
        pageSizeIndicator = (TextView) alertTittleBarLayout.findViewById(R.id.page_size_indicator);
        alertMessageView = (TextView) view.findViewById(R.id.alert_msg_text);
        functionPanelLayout = (LinearLayout) view.findViewById(R.id.btn_function_panel);
        topDividerLine = view.findViewById(R.id.top_divider_line);
        bottomDivider = view.findViewById(R.id.bottom_divider_line);
        btnNeutralDivider = view.findViewById(R.id.button_panel_neutral_divider);
        positiveButton = (Button) view.findViewById(R.id.btn_ok);
        negativeButton = (Button) view.findViewById(R.id.btn_cancel);
        neutralButton = (Button) view.findViewById(R.id.btn_neutral);
        functionButtonDividerLine = view.findViewById(R.id.button_panel_divider);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        neutralButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        customByParams(view);
    }

    private void customByParams(View parentView) {
        if (params.enableTittle) {
            alertTittleBarLayout.setVisibility(View.VISIBLE);
            tittleTextView.setText(params.tittleString);
        } else {
            topDividerLine.setVisibility(View.GONE);
            alertTittleBarLayout.setVisibility(View.GONE);
        }

        setNegativeButton(params.enableNegativeButton, params.negativeButtonText, params.negativeAction);
        setPositiveButton(params.enablePositiveButton, params.positiveButtonText, params.positiveAction);
        setNeutralButton(params.enableNeutralButton, params.neutralButtonText, params.neutralAction);
        setEnableFunctionPanel(params.enableFunctionPanel);
        if (!(params.enableNegativeButton && params.enablePositiveButton)) {
            functionButtonDividerLine.setVisibility(View.GONE);
        }
        parentView.findViewById(R.id.button_function_panel).setVisibility(params.enablePageIndicator ? View.VISIBLE : View.GONE);
        pageSizeIndicator.setVisibility(params.enablePageIndicator ? View.VISIBLE : View.GONE);

        if (params.alertMsgGravity != Gravity.CENTER) {
            alertMessageView.setGravity(params.alertMsgGravity);
        }

        if (params.customContentLayoutResID != -1) {
            setCustomContentLayout(parentView, params.customContentLayoutResID,
                    params.customLayoutHeight, params.customLayoutWidth);
            params.customViewAction.onCreateCustomView(customContentView, pageSizeIndicator);
        } else {
            setAlertMsg(params.alertMsgString);
        }
        if (params.customLayoutBackgroundResId != -1) {
            ViewGroup viewGroup = (ViewGroup) parentView.findViewById(R.id.layout_dialog);
            viewGroup.setBackgroundResource(params.customLayoutBackgroundResId);
        }
    }

    private void setCustomContentLayout(View parentView, int layoutID, int layoutHeight, int layoutWidth) {
        //using custom Layout must define id at top level custom layout.
        alertMessageView.setVisibility(View.GONE);
        if (customContentView == null) {
            customContentView = LayoutInflater.from(getContext()).inflate(layoutID, null);
            RelativeLayout parentLayout = (RelativeLayout) parentView;
            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(layoutWidth,
                    layoutHeight);
            p.addRule(RelativeLayout.BELOW, R.id.top_divider_line);
            parentLayout.addView(customContentView, p);
            View bottomDivider = parentLayout.findViewById(R.id.bottom_divider_line);
            RelativeLayout.LayoutParams dividerParams = (RelativeLayout.LayoutParams) bottomDivider.getLayoutParams();
            dividerParams.addRule(RelativeLayout.BELOW, customContentView.getId());
            bottomDivider.setLayoutParams(dividerParams);
        }
    }

    public static class Params {
        /**
         * use this class to setup dialog
         * all params have default values,just config the item which u really need is ok.
         *
         * @param enableTittle use this value to configure tittleBar visibility.
         * @param enableFunctionPanel use this value to configure FunctionPanel visibility.
         * @param enablePositiveButton use this value to configure Positive Button visibility.
         * @param enableNegativeButton use this value to configure Negative Button visibility.
         * @param enableNeutralButton use this value to configure NeutralButton Button visibility.
         * @param enablePageIndicator use this value to configure Page Indicator visibility.
         * @param canceledOnTouchOutside use this value to configure cancel this dialog when touch outside.
         * @param customContentLayoutResID use this value to configure Custom Content Layout ID.
         * @param customLayoutResID use this value to configure CustomLayout ID,Some Custom Manufacture will require a total different
         * layout,so use this id to change the whole dialog outside Layout,but remember should provide all id exist in
         * onyx_custom_alert_dialog.(Future will add id check.)
         * @param customLayoutHeight use this value to configure Custom Layout Height.
         * @param customLayoutWidth use this value to configure Custom Layout Width.
         * @param dialogWidth use this value to configure Dialog Width.
         * @param dialogHeight use this value to configure Dialog Height.
         * @param tittleString use this value to configure Dialog tittle String.
         * @param alertMsgString use this value to configure Dialog message String.
         * @param positiveAction use this value to configure Positive Action,if custom here,u may have to dismiss the dialog in ur action.
         * @param negativeAction use this value to configure Negative Action,if custom here,u may have to dismiss the dialog in ur action.
         * @param neutralAction use this value to configure Neutral Action,if custom here,u may have to dismiss the dialog in ur action.
         * @param customViewAction use this value to configure customView Action,when ur view is load,it would give u the view which u inject,
         * and the page indicator,u can setup action by findViewById in your custom view and do the custom action.
         * @param usePercentageWidth use this flag to configure use percentage width or not,
         * if not,dialog itself would use margin Left&Right in x dp,
         * which defines in values/dimens.xml/onyx_alert_dialog_width_margin.
         * @param alertMsgGravity allow outside to control alert msg gravity.
         */
        boolean enableTittle = true;
        boolean enableFunctionPanel = true;
        boolean enablePositiveButton = true;
        boolean enableNegativeButton = true;
        boolean enableNeutralButton = false;
        boolean enablePageIndicator = false;
        boolean canceledOnTouchOutside = true;
        boolean usePercentageWidth = true;
        int customContentLayoutResID = -1;
        int customLayoutResID = -1;
        int customLayoutBackgroundResId = -1;
        String neutralButtonText = "";
        String positiveButtonText = "";
        String negativeButtonText = "";
        final int defaultLayoutResID = R.layout.onyx_custom_alert_dialog;

        int customLayoutHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        int customLayoutWidth = ViewGroup.LayoutParams.MATCH_PARENT;
        int dialogWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
        int dialogHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        String tittleString = "";
        String alertMsgString = "";

        public int getAlertMsgGravity() {
            return alertMsgGravity;
        }

        public DialogInformation.Params setAlertMsgGravity(int alertMsgGravity) {
            this.alertMsgGravity = alertMsgGravity;
            return this;
        }

        int alertMsgGravity = Gravity.CENTER;
        OnyxAlertDialog.CustomViewAction customViewAction = new OnyxAlertDialog.CustomViewAction() {
            @Override
            public void onCreateCustomView(View customView, TextView pageIndicator) {
                Log.i(TAG, "onCreateCustomView");
            }
        };
        View.OnClickListener positiveAction, negativeAction, neutralAction;

        public boolean isEnableTittle() {
            return enableTittle;
        }

        public DialogInformation.Params setEnableTittle(boolean enableTittle) {
            this.enableTittle = enableTittle;
            return this;
        }

        public boolean isEnablePositiveButton() {
            return enablePositiveButton;
        }

        public DialogInformation.Params setEnablePositiveButton(boolean enablePositiveButton) {
            this.enablePositiveButton = enablePositiveButton;
            return this;
        }

        public boolean isEnableNegativeButton() {
            return enableNegativeButton;
        }

        public DialogInformation.Params setEnableNegativeButton(boolean enableNegativeButton) {
            this.enableNegativeButton = enableNegativeButton;
            return this;
        }

        public int getCustomContentLayoutResID() {
            return customContentLayoutResID;
        }

        public DialogInformation.Params setCustomContentLayoutResID(int customContentLayoutResID) {
            this.customContentLayoutResID = customContentLayoutResID;
            return this;
        }

        public int getCustomLayoutBackgroundResId() {
            return customLayoutBackgroundResId;
        }

        public DialogInformation.Params setCustomLayoutBackgroundResId(int customBackgroundLayoutResId) {
            this.customLayoutBackgroundResId = customBackgroundLayoutResId;
            return this;
        }

        public String getTittleString() {
            return tittleString;
        }

        public DialogInformation.Params setTittleString(String tittleString) {
            this.tittleString = tittleString;
            return this;
        }

        public String getAlertMsgString() {
            return alertMsgString;
        }

        public DialogInformation.Params setAlertMsgString(String alertMsgString) {
            this.alertMsgString = alertMsgString;
            return this;
        }

        public View.OnClickListener getNegativeAction() {
            return negativeAction;
        }

        public DialogInformation.Params setNegativeAction(View.OnClickListener negativeAction) {
            this.negativeAction = negativeAction;
            return this;
        }

        public View.OnClickListener getPositiveAction() {
            return positiveAction;
        }

        public DialogInformation.Params setPositiveAction(View.OnClickListener positiveAction) {
            this.positiveAction = positiveAction;
            return this;
        }

        public int getCustomLayoutHeight() {
            return customLayoutHeight;
        }

        public DialogInformation.Params setCustomLayoutHeight(int customLayoutHeight) {
            this.customLayoutHeight = customLayoutHeight;
            return this;
        }

        public int getCustomLayoutWidth() {
            return customLayoutWidth;
        }

        public DialogInformation.Params setCustomLayoutWidth(int customLayoutWidth) {
            this.customLayoutWidth = customLayoutWidth;
            return this;
        }

        public DialogInformation.Params setCustomViewAction(OnyxAlertDialog.CustomViewAction customViewAction) {
            this.customViewAction = customViewAction;
            return this;
        }

        public boolean isEnablePageIndicator() {
            return enablePageIndicator;
        }

        public DialogInformation.Params setEnablePageIndicator(boolean enablePageIndicator) {
            this.enablePageIndicator = enablePageIndicator;
            return this;
        }

        public DialogInformation.Params setDialogHeight(int dialogHeight) {
            this.dialogHeight = dialogHeight;
            return this;
        }

        public DialogInformation.Params setDialogWidth(int dialogWidth) {
            this.dialogWidth = dialogWidth;
            return this;
        }

        public boolean isEnableFunctionPanel() {
            return enableFunctionPanel;
        }

        public DialogInformation.Params setEnableFunctionPanel(boolean enableFunctionPanel) {
            this.enableFunctionPanel = enableFunctionPanel;
            return this;
        }

        public boolean isCanceledOnTouchOutside() {
            return canceledOnTouchOutside;
        }

        public DialogInformation.Params setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
            this.canceledOnTouchOutside = canceledOnTouchOutside;
            return this;
        }

        public boolean isUsePercentageWidth() {
            return usePercentageWidth;
        }

        public DialogInformation.Params setUsePercentageWidth(boolean usePercentageWidth) {
            this.usePercentageWidth = usePercentageWidth;
            return this;
        }

        public boolean isEnableNeutralButton() {
            return enableNeutralButton;
        }

        public DialogInformation.Params setEnableNeutralButton(boolean enableNeutralButton) {
            this.enableNeutralButton = enableNeutralButton;
            return this;
        }

        public String getNeutralButtonText() {
            return neutralButtonText;
        }

        public DialogInformation.Params setNeutralButtonText(String neutralButtonText) {
            this.neutralButtonText = neutralButtonText;
            return this;
        }

        public View.OnClickListener getNeutralAction() {
            return neutralAction;
        }

        public DialogInformation.Params setNeutralAction(View.OnClickListener neutralAction) {
            this.neutralAction = neutralAction;
            return this;
        }

        public int getCustomLayoutResID() {
            return customLayoutResID;
        }

        public DialogInformation.Params setCustomLayoutResID(int customLayoutResID) {
            this.customLayoutResID = customLayoutResID;
            return this;
        }

        public String getPositiveButtonText() {
            return positiveButtonText;
        }

        public DialogInformation.Params setPositiveButtonText(String positiveButtonText) {
            this.positiveButtonText = positiveButtonText;
            return this;
        }

        public String getNegativeButtonText() {
            return negativeButtonText;
        }

        public DialogInformation.Params setNegativeButtonText(String negativeButtonText) {
            this.negativeButtonText = negativeButtonText;
            return this;
        }
    }

    public void setAlertMsg(String alertMsg) {
        alertMessageView.setVisibility(View.VISIBLE);
        alertMessageView.setText(alertMsg);
    }

    public void setEnableFunctionPanel(boolean enable) {
        functionPanelLayout.setVisibility(enable ? View.VISIBLE : View.GONE);
        bottomDivider.setVisibility(enable ? View.VISIBLE : View.GONE);
    }

    public void setNegativeButton(boolean enable, String text, View.OnClickListener listener) {
        setButtonView(negativeButton, enable, text, listener);
        if (!(getParams().isEnablePositiveButton() && enable)) {
            functionButtonDividerLine.setVisibility(View.GONE);
        }
    }

    public void setPositiveButton(boolean enable, String text, View.OnClickListener listener) {
        setButtonView(positiveButton, enable, text, listener);
        if (!(getParams().isEnableNegativeButton() && enable)) {
            functionButtonDividerLine.setVisibility(View.GONE);
        }
    }

    public void setNeutralButton(boolean enable, String text, View.OnClickListener listener) {
        setButtonView(neutralButton, enable, text, listener);
        btnNeutralDivider.setVisibility(enable ? View.VISIBLE : View.GONE);
    }

    private void setButtonView(Button button, boolean enable, String text, View.OnClickListener listener) {
        if (!enable) {
            button.setVisibility(View.GONE);
            return;
        }
        button.setVisibility(View.VISIBLE);
        if (StringUtils.isNotBlank(text)) {
            button.setText(text);
        }
        if (listener != null) {
            button.setOnClickListener(listener);
        }
    }
}
