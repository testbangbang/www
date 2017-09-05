package com.onyx.android.dr.webview;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;

import android.widget.PopupWindow;
import android.content.Context;

/**
 * Custom popup window.
 *
 * @author Lorensius W. L. T <lorenz@londatiga.net>
 *
 */
public class PopupWindows {
	protected Context context;
	protected PopupWindow window;
	protected View rootView;
	protected Drawable background = null;
	protected WindowManager windowManager;

	/**
	 * Constructor.
	 *
	 * @param context Context
	 */
	public PopupWindows(Context context) {
		this.context = context;
		window = new PopupWindow(context);
		window.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					window.dismiss();
					return false;
				}
				return false;
			}
		});
		windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
	}

	/**
	 * On dismiss
	 */
	protected void onDismiss() {
	}

	/**
	 * On show
	 */
	protected void onShow() {
	}

	/**
	 * On pre show
	 */
	protected void preShow() {
		if (rootView == null)
			throw new IllegalStateException("setContentView was not called with a view to display.");
		onShow();
		if (background == null) {
			window.setBackgroundDrawable(new BitmapDrawable());
		}else {
			window.setBackgroundDrawable(background);
		}
		window.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
		window.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		window.setTouchable(true);
		window.setOutsideTouchable(true);
		window.setContentView(rootView);
	}

	/**
	 * Set background drawable.
	 *
	 * @param background Background drawable
	 */
	public void setBackgroundDrawable(Drawable background) {
		this.background = background;
	}

	/**
	 * Set content view.
	 *
	 * @param root Root view
	 */
	public void setContentView(View root) {
		rootView = root;
		window.setContentView(root);
	}

	/**
	 * Set content view.
	 *
	 * @param layoutResID Resource id
	 */
	public void setContentView(int layoutResID) {
		LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		setContentView(inflator.inflate(layoutResID, null));
	}

	/**
	 * Set listener on window dismissed.
	 *
	 * @param listener
	 */
	public void setOnDismissListener(PopupWindow.OnDismissListener listener) {
		window.setOnDismissListener(listener);
	}

	/**
	 * Dismiss the popup window.
	 */
	public void dismiss() {
		window.dismiss();
	}
}