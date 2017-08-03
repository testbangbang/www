package com.onyx.android.dr.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.onyx.android.dr.R;
import com.onyx.android.dr.view.WheelView;

import java.util.List;

/**
 * Created by zhouzhiming on 2017/7/13.
 */
public class HourAndMinuteDialog extends AlertDialog {
	private Context context;
	private final int offSetValue = 2;
	private WheelView hourWheelView;
	private WheelView minuteWheelView;

	public HourAndMinuteDialog(Context context) {
		super(context, R.style.base_dialog);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}

	private void initView() {
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_hour_and_minute, null);
		hourWheelView = (WheelView) view.findViewById(R.id.dialog_hour_wheel_view);
		minuteWheelView = (WheelView) view.findViewById(R.id.dialog_minute_wheel_view);
		setContentView(view);
		setCanceledOnTouchOutside(true);
		hourWheelView.setOffset(offSetValue);
		minuteWheelView.setOffset(offSetValue);
		initEvent();
	}

	private void initEvent() {
		hourWheelView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
			@Override
			public void onSelected(int selectedIndex, String item) {
			}
		});

		minuteWheelView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
			@Override
			public void onSelected(int selectedIndex, String item) {
			}
		});
	}

	public void setItemData(List<String> hourList, List<String> minuteList) {
		hourWheelView.setItems(hourList);
		minuteWheelView.setItems(minuteList);
	}

	public void setSelection(int hour, int minute) {
		hourWheelView.setSeletion(hour);
		minuteWheelView.setSeletion(minute);
	}
}
