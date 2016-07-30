package com.onyx.kreader.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.onyx.kreader.R;
import com.onyx.kreader.reflow.ImageReflowSettings;
import com.onyx.kreader.utils.DeviceUtils;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: zhuzeng
 * Date: 5/13/14
 * Time: 9:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class DialogReflowSettings extends DialogBase {

    static public abstract class ReflowCallback {
        abstract public void onFinished(boolean confirm, final ImageReflowSettings settings);
    }

    private ImageReflowSettings settings;
    private ReflowCallback callback;
    private int currentTab = 0;
    private DialogReflowControlItem[] dialogReflowControlItems=new DialogReflowControlItem[5];
    private String[] leftTabItemName=new String[5];
    private String[] rightTabItemName=new String[5];
    private ArrayList<String[]> leftContentArray = new ArrayList<String[]>();
    private ArrayList<String[]> rightContentArray = new ArrayList<String[]>();
    private int[] leftTabCurrentChoice=new int[5];
    private int[] rightTabCurrentChoice=new int[5];

    public DialogReflowSettings(Context context, ImageReflowSettings s, final ReflowCallback c) {
        super(context);
        setContentView(R.layout.dialog_reflow_settings);
        setCanceledOnTouchOutside(true);
        initReflowControlItem();
        initStringSource(context);
        settings = s;
        callback = c;
        updateDensity(context, settings);
        initTabChoiceBySettng(settings);
        resetReflowControlItemCotent(leftTabItemName,leftContentArray,leftTabCurrentChoice);
        setupListeners();
    }

    private void initReflowControlItem() {
        dialogReflowControlItems[0] = (DialogReflowControlItem) findViewById(R.id.item0);
        dialogReflowControlItems[1] = (DialogReflowControlItem) findViewById(R.id.item1);
        dialogReflowControlItems[2] = (DialogReflowControlItem) findViewById(R.id.item2);
        dialogReflowControlItems[3] = (DialogReflowControlItem) findViewById(R.id.item3);
        dialogReflowControlItems[4] = (DialogReflowControlItem) findViewById(R.id.item4);
    }

    private void initStringSource(Context context) {
        leftTabItemName[0] = context.getString(R.string.dialog_reflow_settings_defect_size);
        leftTabItemName[1] = context.getString(R.string.dialog_reflow_settings_page_margins);
        leftTabItemName[2] = context.getString(R.string.dialog_reflow_settings_line_spacing);
        leftTabItemName[3] = context.getString(R.string.dialog_reflow_settings_word_spacing);
        leftTabItemName[4] = context.getString(R.string.dialog_reflow_settings_render_size);
        rightTabItemName[0] = context.getString(R.string.dialog_reflow_settings_auto_straighten);
        rightTabItemName[1] = context.getString(R.string.dialog_reflow_settings_justification);
        rightTabItemName[2] = context.getString(R.string.dialog_reflow_settings_columns);
        rightTabItemName[3] = context.getString(R.string.dialog_reflow_settings_contrast);
        rightTabItemName[4] = context.getString(R.string.dialog_reflow_settings_rotation);
        leftContentArray.add(new String[]{context.getString(R.string.dialog_reflow_settings_small),
                context.getString(R.string.dialog_reflow_settings_medium),
                context.getString(R.string.dialog_reflow_settings_large)});
        leftContentArray.add(new String[]{context.getString(R.string.dialog_reflow_settings_page_margins_small),
                context.getString(R.string.dialog_reflow_settings_page_margins_medium),
                context.getString(R.string.dialog_reflow_settings_page_margins_large)});
        leftContentArray.add(new String[]{context.getString(R.string.dialog_reflow_settings_small),
                context.getString(R.string.dialog_reflow_settings_medium),
                context.getString(R.string.dialog_reflow_settings_large)});
        leftContentArray.add(new String[]{context.getString(R.string.dialog_reflow_settings_small),
                context.getString(R.string.dialog_reflow_settings_medium),
                context.getString(R.string.dialog_reflow_settings_large)});
        leftContentArray.add(new String[]{context.getString(R.string.dialog_reflow_settings_render_small),
                context.getString(R.string.dialog_reflow_settings_render_medium),
                context.getString(R.string.dialog_reflow_settings_render_large),
                context.getString(R.string.dialog_reflow_settings_render_largest)});
        rightContentArray.add(new String[]{"0","5","10"});
        rightContentArray.add(new String[]{context.getString(R.string.dialog_reflow_settings_justification_auto),
                context.getString(R.string.dialog_reflow_settings_justification_left),
                context.getString(R.string.dialog_reflow_settings_justification_center),
                context.getString(R.string.dialog_reflow_settings_justification_right),
                context.getString(R.string.dialog_reflow_settings_justification_full)});
        rightContentArray.add(new String[]{"1","2","3","4"});
        rightContentArray.add(new String[]{context.getString(R.string.dialog_reflow_settings_contrast_lightest),
                context.getString(R.string.dialog_reflow_settings_contrast_lighter),
                context.getString(R.string.dialog_reflow_settings_contrast_default),
                context.getString(R.string.dialog_reflow_settings_contrast_darker),
                context.getString(R.string.dialog_reflow_settings_contrast_darkest)});
        rightContentArray.add(new String[]{"0","90","180","270"});
    }

    private void initTabChoiceBySettng(ImageReflowSettings settings) {
        updateDefectSize(settings);
        updatePageMargins(settings);
        updateLineSpacing(settings);
        updateWordSpacing(settings);
        updateRenderSize(settings);
        updateAutoStraighten(settings);
        updateJustification(settings);
        updateColumns(settings);
        updateConstrast(settings);
        updateRotation(settings);
    }

    private void syncTabChoice(ImageReflowSettings settings) {
        if (settings == null) {
            return;
        }
        switch (currentTab) {
            case 0:
                syncLeftTab(settings);
                break;
            case 1:
                syncRightTab(settings);
                break;
        }
    }

    private void syncLeftTab(ImageReflowSettings s) {
        syncDefectSize(s);
        syncPageMargins(s);
        syncLineSpacing(s);
        syncWordSpacing(s);
        syncRenderSize(s);
    }

    private void syncRightTab(ImageReflowSettings s) {
        syncAutoStraighten(s);
        syncJustification(s);
        syncColumns(s);
        syncConstrast(s);
        syncRotation(s);
    }

    private void resetReflowControlItemCotent(String[] tabItemName, ArrayList<String[]> contentArray, int[] currentChoice) {
        for (int i = 0; i <= 4; i++) {
            dialogReflowControlItems[i].setItemName(tabItemName[i]);
            dialogReflowControlItems[i].setAllChoice(contentArray.get(i));
            dialogReflowControlItems[i].setCurrentChoice(currentChoice[i]);
        }
    }

    private void updateDensity(Context context, ImageReflowSettings settings) {
        if (settings == null) {
            return;
        }
        settings.dev_dpi = (int) DeviceUtils.getDensity(context);
    }

    private void updateDefectSize(ImageReflowSettings s) {
        if (s.defect_size <= 1.0) {
            leftTabCurrentChoice[0]=0;
        } else if (s.defect_size <= 8.0) {
            leftTabCurrentChoice[0]=1;
        } else if (s.defect_size >= 15) {
            leftTabCurrentChoice[0]=2;
        }
    }

    private void updatePageMargins(ImageReflowSettings s) {
        if (Double.compare(s.margin, 0.05) == 0) {
            leftTabCurrentChoice[1]=0;
        } else if (Double.compare(s.margin ,0.1) == 0) {
            leftTabCurrentChoice[1]=1;
        } else {
            leftTabCurrentChoice[1]=2;
        }
    }

    private void updateLineSpacing(ImageReflowSettings s) {
        if (s.line_spacing <= 1.0) {
            leftTabCurrentChoice[2]=0;
        } else if (s.line_spacing <= 1.2) {
            leftTabCurrentChoice[2]=1;
        } else {
            leftTabCurrentChoice[2]=2;
        }
    }

    private void updateWordSpacing(ImageReflowSettings s) {
        if (s.word_spacing <= 0.05) {
            leftTabCurrentChoice[3]=0;
        } else if (s.word_spacing <= 0.15) {
            leftTabCurrentChoice[3]=1;
        } else {
            leftTabCurrentChoice[3]=2;
        }
    }

    private void updateRenderSize(ImageReflowSettings s) {
        if (s.quality <= 0.3) {
            leftTabCurrentChoice[4]=3;
        } else if (s.quality <= 0.45) {
            leftTabCurrentChoice[4]=2;
        } else if (s.quality <= 0.6) {
            leftTabCurrentChoice[4]=1;
        } else {
            leftTabCurrentChoice[4]=0;
        }
    }

    private void updateAutoStraighten(ImageReflowSettings s) {
        if (s.straighten <= 0) {
            rightTabCurrentChoice[0]=0;
        } else if (s.straighten <= 5) {
            rightTabCurrentChoice[0]=1;
        } else {
            rightTabCurrentChoice[0]=2;
        }
    }

    private void updateJustification(ImageReflowSettings s) {
        switch (s.justification) {
            case -1:
                rightTabCurrentChoice[1]=0;
                break;
            case 0:
                rightTabCurrentChoice[1]=1;
                break;
            case 1:
                rightTabCurrentChoice[1]=2;
                break;
            case 2:
                rightTabCurrentChoice[1]=3;
                break;
            case 3:
                rightTabCurrentChoice[1]=4;
                break;
            default:
                break;
        }
    }

    private void updateColumns(ImageReflowSettings s) {
        switch (s.columns) {
            case 1:
                rightTabCurrentChoice[2]=0;
                break;
            case 2:
                rightTabCurrentChoice[2]=1;
                break;
            case 3:
                rightTabCurrentChoice[2]=2;
                break;
            case 4:
                rightTabCurrentChoice[2]=3;
                break;
        }
    }

    private void updateConstrast(ImageReflowSettings s) {
        if (s.contrast >= 2.0) {
            rightTabCurrentChoice[3]=0;
        } else if (s.contrast >= 1.5) {
            rightTabCurrentChoice[3]=1;
        } else if (s.contrast >= 1.0) {
            rightTabCurrentChoice[3]=2;
        } else if (s.contrast >= 0.5) {
            rightTabCurrentChoice[3]=3;
        } else if (s.contrast >= 0.2) {
            rightTabCurrentChoice[3]=4;
        }
    }

    private void updateRotation(ImageReflowSettings s) {
        switch (s.rotate) {
            case 0:
                rightTabCurrentChoice[4]=0;
                break;
            case 90:
                rightTabCurrentChoice[4]=1;
                break;
            case 180:
                rightTabCurrentChoice[4]=2;
                break;
            case 270:
                rightTabCurrentChoice[4]=3;
                break;
        }
    }
    private void syncDefectSize(ImageReflowSettings s) {
        switch (dialogReflowControlItems[0].getCurrentChoice()){
            case 0:
                s.defect_size = 1.0;
                break;
            case 1:
                s.defect_size = 8.0;
                break;
            case 2:
                s.defect_size = 15;
                break;
        }
    }

    private void syncPageMargins(ImageReflowSettings s) {
        switch (dialogReflowControlItems[1].getCurrentChoice()){
            case 0:
                s.margin = 0.05;
                break;
            case 1:
                s.margin = 0.1;
                break;
            case 2:
                s.margin = 0.15;
                break;
        }
    }

    private void syncLineSpacing(ImageReflowSettings s) {
        switch (dialogReflowControlItems[2].getCurrentChoice()){
            case 0:
                s.line_spacing = 1.0;
                break;
            case 1:
                s.line_spacing = 1.2;
                break;
            case 2:
                s.line_spacing = 1.4;
                break;
        }
    }

    private void syncWordSpacing(ImageReflowSettings s) {
        switch (dialogReflowControlItems[3].getCurrentChoice()){
            case 0:
                s.word_spacing = 0.05;
                break;
            case 1:
                s.word_spacing = 0.15;
                break;
            case 2:
                s.word_spacing = 0.375;
                break;
        }
    }

    private void syncRenderSize(ImageReflowSettings s) {
        switch (dialogReflowControlItems[4].getCurrentChoice()){
            case 0:
                s.quality = 0.75;
                break;
            case 1:
                s.quality = 0.6;
                break;
            case 2:
                s.quality = 0.45;
                break;
            case 3:
                s.quality = 0.3;
                break;
        }
    }

    private void syncAutoStraighten(ImageReflowSettings s) {
        switch (dialogReflowControlItems[0].getCurrentChoice()){
            case 0:
                s.straighten = 0;
                break;
            case 1:
                s.straighten = 5;
                break;
            case 2:
                s.straighten = 10;
                break;
        }
    }

    private void syncJustification(ImageReflowSettings s) {
        switch (dialogReflowControlItems[1].getCurrentChoice()){
            case 0:
                s.justification = -1;
                break;
            case 1:
                s.justification = 0;
                break;
            case 2:
                s.justification = 1;
                break;
            case 3:
                s.justification = 2;
                break;
            case 4:
                s.justification = 3;
                break;
        }
    }

    private void syncColumns(ImageReflowSettings s) {
        switch (dialogReflowControlItems[2].getCurrentChoice()){
            case 0:
                s.columns = 1;
                break;
            case 1:
                s.columns = 2;
                break;
            case 2:
                s.columns = 3;
                break;
            case 3:
                s.columns = 4;
                break;
        }
    }

    private void syncConstrast(ImageReflowSettings s) {
        switch (dialogReflowControlItems[3].getCurrentChoice()){
            case 0:
                s.contrast = 2.0;
                break;
            case 1:
                s.contrast = 1.5;
                break;
            case 2:
                s.contrast = 1.0;
                break;
            case 3:
                s.contrast = 0.5;
                break;
            case 4:
                s.contrast = 0.2;
                break;
        }
    }

    private void syncRotation(ImageReflowSettings s) {
        switch (dialogReflowControlItems[4].getCurrentChoice()){
            case 0:
                s.rotate = 0;
                break;
            case 1:
                s.rotate = 90;
                break;
            case 2:
                s.rotate = 180;
                break;
            case 3:
                s.rotate = 270;
                break;
        }
    }


    private void setupListeners() {
        TextView leftTab = (TextView) findViewById(R.id.left_tab);
        TextView rightTab = (TextView) findViewById(R.id.right_tab);
        leftTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncTabChoice(settings);
                initTabChoiceBySettng(settings);
                resetReflowControlItemCotent(leftTabItemName, leftContentArray, leftTabCurrentChoice);
                currentTab = 0;
            }
        });
        rightTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncTabChoice(settings);
                initTabChoiceBySettng(settings);
                resetReflowControlItemCotent(rightTabItemName, rightContentArray, rightTabCurrentChoice);
                currentTab = 1;
            }
        });
        Button cancel = (Button) findViewById(R.id.button_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callback != null) {
                    callback.onFinished(false, null);
                }
                dismiss();
            }
        });
        Button confirm = (Button) findViewById(R.id.button_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callback != null) {
                    syncTabChoice(settings);
                    callback.onFinished(true, settings);
                }
                dismiss();
            }
        });
    }
}
