package com.onyx.kreader.ui.dialog;

import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.onyx.kreader.R;
import com.onyx.kreader.common.Debug;
import com.onyx.kreader.host.options.BaseOptions;
import com.onyx.kreader.host.request.GammaCorrectionRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.view.SeekBarWithEditTextView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ming on 2016/11/28.
 */

public class DialogContrast extends Dialog implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "DialogContrast";

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
    @Bind(R.id.text_zero)
    TextView textZero;
    @Bind(R.id.text_one)
    TextView textOne;
    @Bind(R.id.text_two)
    TextView textTwo;
    @Bind(R.id.text_three)
    TextView textThree;
    @Bind(R.id.text_four)
    TextView textFour;
    @Bind(R.id.text_five)
    TextView textFive;
    @Bind(R.id.btn_cancel)
    Button btnCancel;
    @Bind(R.id.btn_ok)
    Button btnOk;
    @Bind(R.id.text_bold_layout)
    RadioGroup textBoldLayout;
    @Bind(R.id.seekbar_view)
    SeekBarWithEditTextView seekbarView;

    private TextView[] numberTexts;
    private CheckBox[] checkBoxes;
    private int boldSize = BaseOptions.minEmboldenLevel();
    private ReaderDataHolder readerDataHolder;
    private int gamma;

    public DialogContrast(ReaderDataHolder readerDataHolder) {
        super(readerDataHolder.getContext(), R.style.dialog_no_title);
        this.readerDataHolder = readerDataHolder;
        setContentView(R.layout.dialog_contrast);
        ButterKnife.bind(this);
        numberTexts = new TextView[]{textZero, textOne, textTwo, textThree, textFour, textFive};
        checkBoxes = new CheckBox[]{btnZero, btnOne, btnTwo, btnThree, btnFour};
        initView();
        initData();
    }

    private void initView() {
        btnZero.setChecked(true);
        btnZero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearCheckbox();
                btnZero.setChecked(true);
                updateCheckBox(btnZero, true, true);
            }
        });
        btnOne.setOnCheckedChangeListener(this);
        btnTwo.setOnCheckedChangeListener(this);
        btnThree.setOnCheckedChangeListener(this);
        btnFour.setOnCheckedChangeListener(this);

        textBoldLayout.post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < numberTexts.length; i++) {
                    TextView view = numberTexts[i];
                    if (i < checkBoxes.length) {
                        view.setX(checkBoxes[i].getX());
                    } else {
                        CheckBox leftBox = checkBoxes[i - 1];
                        view.setX(leftBox.getX() + leftBox.getMeasuredWidth());
                    }
                }
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        initSeekbar();
    }

    private void initData() {
        boldSize = readerDataHolder.getReader().getDocumentOptions().getEmboldenLevel();
        gamma = (int) readerDataHolder.getReader().getDocumentOptions().getGammaLevel();
        int index = Math.max(0, boldSize - 1);
        clearCheckbox();
        if (index < checkBoxes.length) {
            checkBoxes[index].setChecked(true);
            updateCheckBox(checkBoxes[index], true, true);
        }
        seekbarView.updateValue(R.string.contrast_level, gamma, BaseOptions.minGammaLevel(), BaseOptions.maxGammaLevel());
    }

    private void submitGammaLevel() {
        boldSize = Math.max(1, boldSize);
        Debug.d(TAG, "gamma %d emboldenLevel %d", gamma, boldSize);
        GammaCorrectionRequest request = new GammaCorrectionRequest(gamma, boldSize);
        readerDataHolder.submitRenderRequest(request);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        updateCheckBox(buttonView, isChecked, buttonView.isPressed());
    }

    private void updateCheckBox(CompoundButton buttonView, boolean isChecked, boolean isPressed) {
        if (!isPressed) {
            return;
        }
        clearCheckbox();
        if (isChecked) {
            for (int i = 0; i < checkBoxes.length; i++) {
                CheckBox checkBox = checkBoxes[i];
                checkBox.setChecked(true);
                if (checkBox.equals(buttonView)) {
                    boldSize = i + 1;
                    break;
                }
            }
        } else {
            for (int i = 0; i < checkBoxes.length; i++) {
                CheckBox checkBox = checkBoxes[i];
                if (checkBox.equals(buttonView)) {
                    boldSize = i;
                    break;
                }
                checkBox.setChecked(true);
            }
        }
        submitGammaLevel();
    }

    private void clearCheckbox() {
        for (CheckBox checkBox : checkBoxes) {
            checkBox.setChecked(false);
        }
    }

    private void initSeekbar() {
        seekbarView.setCallback(new SeekBarWithEditTextView.Callback() {
            @Override
            public void valueChange(int newValue) {
                gamma = newValue;
                submitGammaLevel();
            }
        });
    }
}
