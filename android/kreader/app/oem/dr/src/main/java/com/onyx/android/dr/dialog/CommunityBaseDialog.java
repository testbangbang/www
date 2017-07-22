package com.onyx.android.dr.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

public class CommunityBaseDialog extends Dialog {
	private Context context;
	private Object tag;
	
	public CommunityBaseDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		context = context;
	}

	public CommunityBaseDialog(Context context, int theme) {
		super(context, theme);
		context = context;
	}

	public CommunityBaseDialog(Context context) {
		super(context);
		context = context;
	}	

	@Override
	public void dismiss() {
		try {
			if (context instanceof Activity) {
				Activity aty = (Activity) context;
				boolean isFinishing = aty.isFinishing();
				if (!isFinishing) {
					super.dismiss();
				}
			} else {
				if (isShowing()) {
					super.dismiss();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void show(){
		try {			
			Activity activity = this.getOwnerActivity();
			if (context instanceof Activity) {
				activity = (Activity)context;
			}
			if ( activity != null ){
				boolean isFinishing = activity.isFinishing();
				if (!isFinishing && activity.getWindow() != null) {
					super.show();
				}
			}else{
				super.show();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setTag(Object tag) {
		tag = tag;
	}
	
	public Object getTag(){
		return tag;
	}
}
