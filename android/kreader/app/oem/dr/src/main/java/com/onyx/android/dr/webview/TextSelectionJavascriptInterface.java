package com.onyx.android.dr.webview;

import android.content.Context;
import android.os.Handler;
import android.webkit.JavascriptInterface;

/**
 * This javascript interface allows the page to communicate that text has been selected by the user.
 *
 * @author btate
 *
 */
public class TextSelectionJavascriptInterface {
	/** The TAG for logging. */
	private static final String TAG = "TextSelectionJavascriptInterface";
	/** The javascript interface name for adding to web view. */
	private final String interfaceName = "TextSelection";
	/** The webview to work with. */
	private TextSelectionJavascriptInterfaceListener listener;
	/** The context. */
	Context context;
    // Need handler for callbacks to the UI thread
    final Handler handler = new Handler();

	/**
	 * Constructor accepting context.
	 * @param c
	 */
	public TextSelectionJavascriptInterface(Context c){
		this.context = c;
	}
	
	/**
	 * Constructor accepting context and listener.
	 * @param c
	 * @param listener
	 */
	public TextSelectionJavascriptInterface(Context c, TextSelectionJavascriptInterfaceListener listener){
		this.context = c;
		this.listener = listener;
	}
	
	/**
	 * Handles javascript errors.
	 * @param error
	 */
    @JavascriptInterface
	public void jsError(final String error){
		if(this.listener != null){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.tsjiJSError(error);
                }
            });
		}
	}
	
	/**
	 * Gets the interface name
	 * @return
	 */
    @JavascriptInterface
	public String getInterfaceName(){
		return this.interfaceName;
	}
	
	/**
	 * Put the app in "selection mode".
	 */
    @JavascriptInterface
	public void startSelectionMode(){
		if(this.listener != null)
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.tsjiStartSelectionMode();
                }
            });
	}
	
	/**
	 * Take the app out of "selection mode".
	 */
    @JavascriptInterface
	public void endSelectionMode(){
		if(this.listener != null)
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.tsjiEndSelectionMode();
                }
            });
	}
    
	/**
	 * Show the context menu
	 * @param range
	 * @param text
	 * @param menuBounds
	 */
    @JavascriptInterface
	public void selectionChanged(final String range, final String text, final String handleBounds, final String menuBounds){
		if(this.listener != null)  {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.tsjiSelectionChanged(range, text, handleBounds, menuBounds);
                }
            });
        }
	}
    
    @JavascriptInterface
	public void setContentWidth(final float contentWidth){
		if(this.listener != null)
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.tsjiSetContentWidth(contentWidth);
                }
            });
	}
}
