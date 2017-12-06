package com.onyx.kreader.ui.dialog;

import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.ui.dialog.OnyxBaseDialog;
import com.onyx.android.sdk.ui.view.SeekBarWithEditTextView;
import com.onyx.kreader.R;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.reader.host.options.BaseOptions;
import com.onyx.android.sdk.reader.host.request.GammaCorrectionRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ming on 2016/11/28.
 */

public class DialogContrast extends OnyxBaseDialog implements CompoundButton.OnCheckedChangeListener {

    private static final Class TAG = DialogContrast.class;

    @Bind(R.id.btn_zero)
    CheckBox btnZero;
    @Bind(R.id.btn_one)
    CheckBox btnOne;
    @Bind(R.id.btn_two)
    CheckBox btnTwo;
    @Bind(R.id.btn_three)
    CheckBox btnThree;
    @Bind(R.id.btn_four)
    CheckBox btnFour;
    @Bind(R.id.btn_cancel)
    Button btnCancel;
    @Bind(R.id.btn_ok)
    Button btnOk;
    @Bind(R.id.text_bold_layout)
    RadioGroup textBoldLayout;
    @Bind(R.id.seekbar_view)
    SeekBarWithEditTextView seekbarView;
    @Bind(R.id.btn_five)
    CheckBox btnFive;

    private int originalBoldSize;
    private int originalGamma;
    private int boldSize = BaseOptions.minEmboldenLevel();
    private int gamma;
    private CheckBox[] checkBoxes;
    private ReaderDataHolder readerDataHolder;

    public DialogContrast(ReaderDataHolder readerDataHolder) {
        super(readerDataHolder.getContext(), R.style.dialog_no_title);
        this.readerDataHolder = readerDataHolder;
        setContentView(R.layout.dialog_contrast);
        ButterKnife.bind(this);
        checkBoxes = new CheckBox[]{btnZero, btnOne, btnTwo, btnThree, btnFour, btnFive};
        initView();
        initData();
        initSeekbar();
    }

    private void initView() {
        btnZero.setOnCheckedChangeListener(this);
        btnOne.setOnCheckedChangeListener(this);
        btnTwo.setOnCheckedChangeListener(this);
        btnThree.setOnCheckedChangeListener(this);
        btnFour.setOnCheckedChangeListener(this);
        btnFive.setOnCheckedChangeListener(this);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetAndDismiss();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            resetAndDismiss();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void resetAndDismiss() {
        readerDataHolder.removeActiveDialog(this);
        GammaCorrectionRequest request = new GammaCorrectionRequest(originalGamma, originalGamma, originalGamma, originalBoldSize);
        readerDataHolder.submitRenderRequest(request, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                dismiss();
            }
        });
    }

    private void initData() {
        originalBoldSize = readerDataHolder.getReader().getDocumentOptions().getEmboldenLevel();
        originalGamma = (int) readerDataHolder.getReader().getDocumentOptions().getGammaLevel();
        boldSize = originalBoldSize;
        gamma = originalGamma;
        clearCheckbox();
        for (int i = 0; i <= boldSize; i++) {
            checkBoxes[i].setChecked(true);
        }
        seekbarView.updateValue(R.string.contrast_level, gamma, BaseOptions.minGammaLevel(), BaseOptions.maxGammaLevel());
    }

    private void submitGammaLevel() {
        Debug.d(TAG, "gamma %d emboldenLevel %d", gamma, boldSize);
        GammaCorrectionRequest request = new GammaCorrectionRequest(gamma, gamma, gamma, boldSize);
        readerDataHolder.submitRenderRequest(request);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        updateCheckBox(buttonView, isChecked);
    }

    private void updateCheckBox(CompoundButton buttonView, boolean isChecked) {
        if (!buttonView.isPressed() && !buttonView.isFocused()) {
            return;
        }
        clearCheckbox();

        for (int i = 0; i < checkBoxes.length; i++) {
            CheckBox checkBox = checkBoxes[i];
            checkBox.setChecked(true);
            if (checkBox.equals(buttonView)) {
                boldSize = i;
                break;
            }
        }

        if (buttonView.equals(btnZero)) {
            buttonView.setChecked(true);
            boldSize = 0;
        }
        submitGammaLevel();
    }

    private void clearCheckbox() {
        for (CheckBox checkBox : checkBoxes) {
            checkBox.setChecked(false);
        }
    }

    private void initSeekbar() {
        seekbarView.setIgnoreSeekBarTrackingValue(true);
        seekbarView.setCallback(new SeekBarWithEditTextView.Callback() {
            @Override
            public void valueChange(int newValue) {
                if (gamma != newValue) {
                    gamma = newValue;
                    submitGammaLevel();
                }
            }
        });
    }
}
