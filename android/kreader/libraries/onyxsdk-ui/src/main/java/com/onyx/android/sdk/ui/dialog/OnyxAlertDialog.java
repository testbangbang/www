package com.onyx.android.sdk.ui.dialog;

import android.app.DialogFragment;
import android.app.FragmentManager;
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

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.utils.StringUtils;

/**
 * New Dialog For Onyx Apps
 * Use Params To Config Dialog Properties.
 * Params settings support builder mode,could set all properties in one line.
 * Created by solskjaer49 on 15/12/1 11:54 12:03.
 */
public class OnyxAlertDialog extends DialogFragment {
    static final String TAG = OnyxAlertDialog.class.getSimpleName();
    private RelativeLayout alertTittleBarLayout;
    private LinearLayout functionPanelLayout;
    private TextView tittleTextView, alertMessageView, pageSizeIndicator;
    private Button positiveButton;

    protected Button getPositiveButton() {
        return positiveButton;
    }

    private Button negativeButton;
    private Button neutralButton;
    private View customContentView, topDividerLine, functionButtonDividerLine, bottomDivider, btnNeutralDivider;
    private Params params = new Params();
    private DialogEventsListener eventsListener;

    public interface CustomViewAction {
        void onCreateCustomView(View customView, TextView pageIndicator);
    }

    public interface DialogEventsListener {
        void onCancel(OnyxAlertDialog dialog, DialogInterface dialogInterface);

        void onDismiss(OnyxAlertDialog dialog, DialogInterface dialogInterface);
    }

    public void setDialogEventsListener(DialogEventsListener listener) {
        this.eventsListener = listener;
    }

    public Params getParams() {
        return params;
    }

    public void setParams(Params params) {
        this.params = params;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null) {
            return;
        }

        if (params.dialogHeight != ViewGroup.LayoutParams.WRAP_CONTENT
                || params.dialogWidth != ViewGroup.LayoutParams.WRAP_CONTENT) {
            getDialog().getWindow().setLayout(params.dialogWidth, params.dialogHeight);
        } else {
            getDialog().getWindow().setLayout(getDefaultWidth(params.isUsePercentageWidth()), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        getDialog().setCanceledOnTouchOutside(params.canceledOnTouchOutside);
    }

    protected int getDefaultWidth(boolean usePercentageWidth) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        return usePercentageWidth ? (dm.widthPixels * 7 / 10) :
                dm.widthPixels - getResources().getDimensionPixelSize(R.dimen.onyx_alert_dialog_width_margin);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(params.customLayoutResID == -1 ?
                params.defaultLayoutResID : params.customLayoutResID, container, false);
        ViewGroup viewGroup = (ViewGroup) view.findViewById(R.id.layout_dialog);
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
        return view;
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
        if (params.keyAction != null) {
            getDialog().setOnKeyListener(params.keyAction);
        }
    }

    private void setCustomContentLayout(View parentView, int layoutID, int layoutHeight, int layoutWidth) {
        //using custom Layout must define id at top level custom layout.
        alertMessageView.setVisibility(View.GONE);
        if (customContentView == null) {
            customContentView = getActivity().getLayoutInflater().inflate(layoutID, null);
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

    @Override
    public void show(FragmentManager manager, String tag) {
        EpdController.disableRegal();
        super.show(manager, tag);
    }

    @Override
    public void dismiss() {
        EpdController.enableRegal();
        super.dismiss();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (eventsListener != null) {
            eventsListener.onCancel(OnyxAlertDialog.this, dialog);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (eventsListener != null) {
            eventsListener.onDismiss(OnyxAlertDialog.this, dialog);
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

        public Params setAlertMsgGravity(int alertMsgGravity) {
            this.alertMsgGravity = alertMsgGravity;
            return this;
        }

        int alertMsgGravity = Gravity.CENTER;
        CustomViewAction customViewAction = new CustomViewAction() {
            @Override
            public void onCreateCustomView(View customView, TextView pageIndicator) {
                Log.i(TAG, "onCreateCustomView");
            }
        };
        View.OnClickListener positiveAction, negativeAction, neutralAction;
        DialogInterface.OnKeyListener keyAction;

        public boolean isEnableTittle() {
            return enableTittle;
        }

        public Params setEnableTittle(boolean enableTittle) {
            this.enableTittle = enableTittle;
            return this;
        }

        public boolean isEnablePositiveButton() {
            return enablePositiveButton;
        }

        public Params setEnablePositiveButton(boolean enablePositiveButton) {
            this.enablePositiveButton = enablePositiveButton;
            return this;
        }

        public boolean isEnableNegativeButton() {
            return enableNegativeButton;
        }

        public Params setEnableNegativeButton(boolean enableNegativeButton) {
            this.enableNegativeButton = enableNegativeButton;
            return this;
        }

        public int getCustomContentLayoutResID() {
            return customContentLayoutResID;
        }

        public Params setCustomContentLayoutResID(int customContentLayoutResID) {
            this.customContentLayoutResID = customContentLayoutResID;
            return this;
        }

        public int getCustomLayoutBackgroundResId() {
            return customLayoutBackgroundResId;
        }

        public Params setCustomLayoutBackgroundResId(int customBackgroundLayoutResId) {
            this.customLayoutBackgroundResId = customBackgroundLayoutResId;
            return this;
        }

        public String getTittleString() {
            return tittleString;
        }

        public Params setTittleString(String tittleString) {
            this.tittleString = tittleString;
            return this;
        }

        public String getAlertMsgString() {
            return alertMsgString;
        }

        public Params setAlertMsgString(String alertMsgString) {
            this.alertMsgString = alertMsgString;
            return this;
        }

        public View.OnClickListener getNegativeAction() {
            return negativeAction;
        }

        public Params setNegativeAction(View.OnClickListener negativeAction) {
            this.negativeAction = negativeAction;
            return this;
        }

        public View.OnClickListener getPositiveAction() {
            return positiveAction;
        }

        public Params setPositiveAction(View.OnClickListener positiveAction) {
            this.positiveAction = positiveAction;
            return this;
        }

        public int getCustomLayoutHeight() {
            return customLayoutHeight;
        }

        public Params setCustomLayoutHeight(int customLayoutHeight) {
            this.customLayoutHeight = customLayoutHeight;
            return this;
        }

        public int getCustomLayoutWidth() {
            return customLayoutWidth;
        }

        public Params setCustomLayoutWidth(int customLayoutWidth) {
            this.customLayoutWidth = customLayoutWidth;
            return this;
        }

        public Params setCustomViewAction(CustomViewAction customViewAction) {
            this.customViewAction = customViewAction;
            return this;
        }

        public boolean isEnablePageIndicator() {
            return enablePageIndicator;
        }

        public Params setEnablePageIndicator(boolean enablePageIndicator) {
            this.enablePageIndicator = enablePageIndicator;
            return this;
        }

        public Params setDialogHeight(int dialogHeight) {
            this.dialogHeight = dialogHeight;
            return this;
        }

        public Params setDialogWidth(int dialogWidth) {
            this.dialogWidth = dialogWidth;
            return this;
        }

        public boolean isEnableFunctionPanel() {
            return enableFunctionPanel;
        }

        public Params setEnableFunctionPanel(boolean enableFunctionPanel) {
            this.enableFunctionPanel = enableFunctionPanel;
            return this;
        }

        public boolean isCanceledOnTouchOutside() {
            return canceledOnTouchOutside;
        }

        public Params setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
            this.canceledOnTouchOutside = canceledOnTouchOutside;
            return this;
        }

        public boolean isUsePercentageWidth() {
            return usePercentageWidth;
        }

        public Params setUsePercentageWidth(boolean usePercentageWidth) {
            this.usePercentageWidth = usePercentageWidth;
            return this;
        }

        public boolean isEnableNeutralButton() {
            return enableNeutralButton;
        }

        public Params setEnableNeutralButton(boolean enableNeutralButton) {
            this.enableNeutralButton = enableNeutralButton;
            return this;
        }

        public String getNeutralButtonText() {
            return neutralButtonText;
        }

        public Params setNeutralButtonText(String neutralButtonText) {
            this.neutralButtonText = neutralButtonText;
            return this;
        }

        public View.OnClickListener getNeutralAction() {
            return neutralAction;
        }

        public Params setNeutralAction(View.OnClickListener neutralAction) {
            this.neutralAction = neutralAction;
            return this;
        }

        public int getCustomLayoutResID() {
            return customLayoutResID;
        }

        public Params setCustomLayoutResID(int customLayoutResID) {
            this.customLayoutResID = customLayoutResID;
            return this;
        }

        public String getPositiveButtonText() {
            return positiveButtonText;
        }

        public Params setPositiveButtonText(String positiveButtonText) {
            this.positiveButtonText = positiveButtonText;
            return this;
        }

        public String getNegativeButtonText() {
            return negativeButtonText;
        }

        public Params setNegativeButtonText(String negativeButtonText) {
            this.negativeButtonText = negativeButtonText;
            return this;
        }

        public DialogInterface.OnKeyListener getKeyAction(){
            return keyAction;
        }

        public Params setKeyAction(DialogInterface.OnKeyListener keyAction) {
            this.keyAction = keyAction;
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