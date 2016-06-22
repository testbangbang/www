package com.onyx.kreader.ui.dialog;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.onyx.kreader.R;


/**
 * Created by solskjaer49 on 14-6-30.
 */


public class DialogReflowControlItem extends LinearLayout {
   TextView itemName;
   TextView currentChoice;
   String[] allChoice;
   ImageView backIcon;
   ImageView forwardIcon;
   int currentChoiceIndex;

    public DialogReflowControlItem(Context context) {
        this(context, null);
    }

    public DialogReflowControlItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.dialog_reflow_control_item, this, true);
        currentChoice=(TextView)findViewById(R.id.current_item);
        itemName= (TextView)findViewById(R.id.item_name);
        backIcon = (ImageView) findViewById(R.id.back_icon);
        backIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showLastChoice();
            }
        });
        forwardIcon = (ImageView) findViewById(R.id.forward_icon);
        forwardIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showNextChoice();
            }
        });
    }


    public void setItemName(String sItemName){
        itemName.setText(sItemName);
    }

    public void setAllChoice(String[] sAllChoice){
        allChoice=sAllChoice;
    }

    public void setCurrentChoice(int iCurrentChoice){
        currentChoice.setText(allChoice[iCurrentChoice]);
        currentChoiceIndex=iCurrentChoice;
    }

    private void showLastChoice(){
         if(currentChoiceIndex-1>=0){
             setCurrentChoice(currentChoiceIndex-1);
         }else {
             setCurrentChoice(allChoice.length-1);
         }
    }

    private void showNextChoice(){
        if(currentChoiceIndex+1<allChoice.length){
            setCurrentChoice(currentChoiceIndex+1);
        }else {
            setCurrentChoice(0);
        }
    }

    public int getCurrentChoice(){
        return currentChoiceIndex;
    }
}
