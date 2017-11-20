package com.onyx.android.dr.reader.data;

/**
 * Created by hehai on 17-8-19.
 */

public class ReadSummaryNewWordReviewBean {
    public String word;
    public String property;
    public String commonlyUsed;
    public String interpretation;
    private boolean checked;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
