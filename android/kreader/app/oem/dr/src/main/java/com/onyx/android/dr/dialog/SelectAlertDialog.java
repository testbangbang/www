package com.onyx.android.dr.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import com.onyx.android.dr.R;

/**
 * Created by zhouzhiming on 2017/7/13.
 */
public class SelectAlertDialog extends CommunityBaseDialog {
	private Context mContext;
	private LinearLayout contentView;

	public SelectAlertDialog(Context context) {
		super(context, R.style.base_dialog);
		mContext = context;
		initUI();
	}

	private void initUI() {

		View dlgView = LayoutInflater.from(mContext).inflate(R.layout.dialog_choose_business_select_list, null);
		contentView = (LinearLayout) dlgView.findViewById(R.id.dilog_content_set_view);
		setContentView(dlgView);
		setCanceledOnTouchOutside(true);
	}

	public void setView(View view) {
		contentView.removeAllViews();
		contentView.addView(view);
		contentView.setVisibility(View.VISIBLE);
	}
}
